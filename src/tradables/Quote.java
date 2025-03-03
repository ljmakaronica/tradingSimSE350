package tradables;

import exceptions.*;
import prices.Price;
import prices.InvalidPriceException;

public class Quote
{
    private String user;
    private String product;
    private QuoteSide buyQuoteSide;
    private QuoteSide sellQuoteSide;

    public Quote(String symbol, Price buyPrice, int buyVolume, Price sellPrice, int sellVolume, String userName)
            throws InvalidUserException, InvalidSymbolException, InvalidVolumeException,
            InvalidPriceException, InvalidQuoteException
    {
        // ASSIGNMENT 2 CORRECTION: NO LONGER USING INLINE VALIDATION ITS SETTER METHODS NOW
        setUser(userName);
        setProduct(symbol);

        if (buyPrice != null && buyVolume > 0)
        {
            this.buyQuoteSide = new QuoteSide(userName, symbol, buyPrice, buyVolume, BookSide.BUY);
        }

        if (sellPrice != null && sellVolume > 0)
        {
            this.sellQuoteSide = new QuoteSide(userName, symbol, sellPrice, sellVolume, BookSide.SELL);
        }
    }

    private void setUser(String userName) throws InvalidUserException
    {
        // Validate user (3 letters, no spaces, no numbers, no special characters)
        if (userName == null || userName.length() != 3)
        {
            throw new InvalidUserException("User must be exactly 3 characters");
        }
        for (char c : userName.toCharArray())
        {
            if (!Character.isLetter(c))
            {
                throw new InvalidUserException("User must contain only letters");
            }
        }
        this.user = userName;
    }

    private void setProduct(String symbol) throws InvalidSymbolException
    {
        // Validate product/symbol
        if (symbol == null || symbol.isEmpty() || symbol.length() > 5)
        {
            throw new InvalidSymbolException("Product must be between 1 and 5 characters");
        }
        for (char c : symbol.toCharArray())
        {
            if (!Character.isLetter(c) && c != '.')
            {
                throw new InvalidSymbolException("Product must contain only letters or dots");
            }
        }
        this.product = symbol;
    }

    public QuoteSide getQuoteSide(BookSide side)
    {
        return (side == BookSide.BUY) ? buyQuoteSide : sellQuoteSide;
    }

    public String getProduct()
    {
        return product;
    }

    public String getUser()
    {
        return user;
    }

    @Override
    public String toString()
    {
        String result = "";
        if (buyQuoteSide != null)
        {
            result += buyQuoteSide.toString();
        }
        if (sellQuoteSide != null)
        {
            if (!result.isEmpty())
            {
                result += "\n";
            }
            result += sellQuoteSide.toString();
        }
        return result;
    }
}