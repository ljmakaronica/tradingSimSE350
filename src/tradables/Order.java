package tradables;
import prices.InvalidPriceException;
import prices.Price;
import exceptions.*;

public class Order implements Tradable
{
    private String user;
    private String product;
    private Price price;
    private int originalVolume;
    private int remainingVolume;
    private int cancelledVolume;
    private int filledVolume;
    private BookSide side;
    private String id;

    public Order(String user, String product, Price price, int volume, BookSide side)
            throws InvalidUserException, InvalidSymbolException, InvalidVolumeException, InvalidOrderException, InvalidPriceException
    {
        // ASSIGNMENT 2 CORRECTION: NO LONGER USING INLINE VALIDATION ITS SETTER METHODS NOW
        setUser(user);
        setProduct(product);
        setPrice(price);
        setOriginalVolume(volume);
        setSide(side);

        this.remainingVolume = volume;
        this.cancelledVolume = 0;
        this.filledVolume = 0;

        // Generate ID: user + product + price + nanotime
        this.id = user + product + price.toString() + System.nanoTime();
    }

    private void setUser(String user) throws InvalidUserException
    {
        // Validate user (3 letters, no spaces, no numbers, no special characters)
        if (user == null || user.length() != 3)
        {
            throw new InvalidUserException("User must be exactly 3 characters");
        }
        for (char c : user.toCharArray())
        {
            if (!Character.isLetter(c))
            {
                throw new InvalidUserException("User must contain only letters");
            }
        }
        this.user = user;
    }

    private void setProduct(String product) throws InvalidSymbolException
    {
        if (product == null || product.length() < 1 || product.length() > 5)
        {
            throw new InvalidSymbolException("Product must be between 1 and 5 characters");
        }
        for (char c : product.toCharArray())
        {
            if (!Character.isLetter(c) && c != '.')
            {
                throw new InvalidSymbolException("Product must contain only letters or dots");
            }
        }
        this.product = product;
    }

    private void setPrice(Price price) throws InvalidPriceException
    {
        // Validate price
        if (price == null)
        {
            throw new InvalidPriceException("Price cannot be null");
        }
        this.price = price;
    }

    private void setOriginalVolume(int volume) throws InvalidVolumeException
    {
        // Validate volume (greater than 0, less than 10,000)
        if (volume <= 0 || volume >= 10000)
        {
            throw new InvalidVolumeException("Volume must be between 1 and 9999");
        }
        this.originalVolume = volume;
    }

    private void setSide(BookSide side) throws InvalidOrderException
    {
        // Validate side
        if (side == null)
        {
            throw new InvalidOrderException("Side cannot be null");
        }
        this.side = side;
    }

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public int getRemainingVolume()
    {
        return remainingVolume;
    }

    @Override
    public void setCancelledVolume(int newVol)
    {
        this.cancelledVolume = newVol;
    }

    @Override
    public int getCancelledVolume()
    {
        return cancelledVolume;
    }

    @Override
    public void setRemainingVolume(int newVol)
    {
        this.remainingVolume = newVol;
    }

    @Override
    public TradableDTO makeTradableDTO()
    {
        return new TradableDTO(
                user,
                product,
                price,
                originalVolume,
                remainingVolume,
                cancelledVolume,
                filledVolume,
                side,
                id
        );
    }

    @Override
    public Price getPrice()
    {
        return price;
    }

    @Override
    public void setFilledVolume(int newVol)
    {
        this.filledVolume = newVol;
    }

    @Override
    public int getFilledVolume()
    {
        return filledVolume;
    }

    @Override
    public BookSide getSide()
    {
        return side;
    }

    @Override
    public String getUser()
    {
        return user;
    }

    @Override
    public String getProduct()
    {
        return product;
    }

    @Override
    public int getOriginalVolume()
    {
        return originalVolume;
    }

    @Override
    public String toString()
    {
        return String.format("%s %s order: %s at %s, Orig Vol: %d, Rem Vol: %d, Fill Vol: %d, CXL Vol: %d, ID: %s",
                user,
                side,
                product,
                price,
                originalVolume,
                remainingVolume,
                filledVolume,
                cancelledVolume,
                id
        );
    }
}