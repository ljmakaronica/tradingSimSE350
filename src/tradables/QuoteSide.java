package tradables;
import exceptions.*;
import prices.InvalidPriceException;
import prices.Price;
public class QuoteSide implements Tradable
{
    private final String user;
    private final String product;
    private final Price price;
    private final int originalVolume;
    private int remainingVolume;
    private int cancelledVolume;
    private int filledVolume;
    private final BookSide side;
    private final String id;

    public QuoteSide(String user, String product, Price price, int volume, BookSide side)
            throws InvalidUserException, InvalidSymbolException, InvalidVolumeException, InvalidPriceException, InvalidQuoteException
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

        // Validate price
        if (price == null)
        {
            throw new InvalidPriceException("Price cannot be null");
        }

        // Validate volume (greater than 0, less than 10,000)
        if (volume <= 0 || volume >= 10000)
        {
            throw new InvalidVolumeException("Volume must be between 1 and 9999");
        }

        // Validate side
        if (side == null)
        {
            throw new InvalidQuoteException("Side cannot be null");
        }

        this.user = user;
        this.product = product;
        this.price = price;
        this.originalVolume = volume;
        this.remainingVolume = volume;
        this.cancelledVolume = 0;
        this.filledVolume = 0;
        this.side = side;

        // Generate ID: user + product + price + nanotime
        this.id = user + product + price.toString() + System.nanoTime();
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
    public String toString() {
        return String.format("%s %s side quote for %s: %s, Orig Vol: %d, Rem Vol: %d, Fill Vol: %d, CXL Vol: %d, ID: %s",
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
