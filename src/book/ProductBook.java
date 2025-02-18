package book;
import prices.Price;
import tradables.*;
import java.util.ArrayList;
import prices.InvalidPriceException;
import exceptions.*;
public class ProductBook
{
    private final String product;
    private final ProductBookSide buySide;
    private final ProductBookSide sellSide;

    public ProductBook(String product) throws InvalidSymbolException
    {
        if (product == null)
        {
            throw new InvalidSymbolException("Product cannot be null");
        }

        if (product.length() < 1 || product.length() > 5)
        {
            throw new InvalidSymbolException("Product must be 1-5 characters");
        }

        for (char c : product.toCharArray())
        {
            if (!Character.isLetter(c) && c != '.')
            {
                throw new InvalidSymbolException("Product can only contain letters and periods");
            }
        }

        this.product = product;
        this.buySide = new ProductBookSide(BookSide.BUY);
        this.sellSide = new ProductBookSide(BookSide.SELL);
    }

    public TradableDTO add(Tradable t) throws InvalidOrderException, InvalidQuoteException
    {
        if (t == null)
        {
            throw new InvalidOrderException("Tradable cannot be null");
        }

        System.out.println("**ADD: " + t);

        ProductBookSide side;
        if (t.getSide() == BookSide.BUY)
        {
            side = buySide;
        }
        else
        {
            side = sellSide;
        }
        TradableDTO dto = side.add(t);
        tryTrade();
        return dto;
    }

    public TradableDTO[] add(Quote q) throws InvalidQuoteException, InvalidOrderException
    {
        if (q == null)
        {
            throw new InvalidQuoteException("Quote cannot be null");
        }

        // Remove any existing quotes for this user
        removeQuotesForUser(q.getUser());

        ArrayList<TradableDTO> dtos = new ArrayList<>();

        // Add buy side if it exists
        QuoteSide buySide = q.getQuoteSide(BookSide.BUY);
        if (buySide != null)
        {
            dtos.add(add(buySide));
        }

        // Add sell side if it exists
        QuoteSide sellSide = q.getQuoteSide(BookSide.SELL);
        if (sellSide != null)
        {
            dtos.add(add(sellSide));
        }

        TradableDTO[] result = new TradableDTO[dtos.size()];
        for (int i = 0; i < dtos.size(); i++)
        {
            result[i] = dtos.get(i);
        }
        return result;
    }

    public TradableDTO cancel(BookSide side, String id)
    {
        ProductBookSide bookSide;
        if (side == BookSide.BUY)
        {
            bookSide = buySide;
        }
        else
        {
            bookSide = sellSide;
        }
        return bookSide.cancel(id);
    }

    public ArrayList<TradableDTO> removeQuotesForUser(String user)
    {
        ArrayList<TradableDTO> cancelledQuotes = new ArrayList<>();
        cancelledQuotes.addAll(buySide.removeQuotesForUser(user));
        cancelledQuotes.addAll(sellSide.removeQuotesForUser(user));
        return cancelledQuotes;
    }

    public void tryTrade()
    {
        Price buyPrice = buySide.topOfBookPrice();
        Price sellPrice = sellSide.topOfBookPrice();

        if (buyPrice == null || sellPrice == null)
        {
            return;
        }

        try
        {
            while (buyPrice != null && sellPrice != null && buyPrice.greaterOrEqual(sellPrice))
            {
                int buyVolume = buySide.topOfBookVolume();
                int sellVolume = sellSide.topOfBookVolume();
                int volumeToTrade = Math.min(buyVolume, sellVolume);

                // Execute the trades
                boolean buyTraded = buySide.tradeOut(sellPrice, volumeToTrade);
                boolean sellTraded = sellSide.tradeOut(buyPrice, volumeToTrade);

                if (!buyTraded || !sellTraded)
                {
                    break;
                }

                // Update prices for next iteration
                buyPrice = buySide.topOfBookPrice();
                sellPrice = sellSide.topOfBookPrice();
            }
        }
        catch (InvalidPriceException e)
        {
            System.out.println("Error during trade: " + e.getMessage());
        }
    }

    public String getTopOfBookString(BookSide side)
    {
        String prefix = "Top of " + side + " book: ";
        ProductBookSide bookSide = (side == BookSide.BUY) ? buySide : sellSide;
        Price topPrice = bookSide.topOfBookPrice();
        int topVolume = bookSide.topOfBookVolume();

        if (topPrice == null) {
            return prefix + "$0.00 x 0";
        }

        return prefix + String.format("%s x %d", topPrice, topVolume);
    }




    @Override
    public String toString()
    {
        String result = "";
        result += "--------------------------------------------\n";
        result += "Product Book: " + product + "\n";
        result += "Side: BUY\n";
        String buyStr = buySide.toString();
        result += buyStr.isEmpty() ? "\t<Empty>\n" : buyStr;
        result += "Side: SELL\n";
        String sellStr = sellSide.toString();
        result += sellStr.isEmpty() ? "\t<Empty>\n" : sellStr;
        result += "--------------------------------------------";
        return result;
    }


}
