package book;
import prices.Price;
import tradables.*;
import java.util.*;
import exceptions.*;
import prices.InvalidPriceException;
public class ProductBookSide
{
    private final BookSide side;
    private final TreeMap<Price, ArrayList<Tradable>> bookEntries;

    public ProductBookSide(BookSide side)
    {
        if (side == null)
        {
            throw new IllegalArgumentException("Side cannot be null");
        }
        this.side = side;
        if (side == BookSide.BUY)
        {
            this.bookEntries = new TreeMap<>(Comparator.reverseOrder()); //We need to get the highest buy price (highest at top)
        }
        else
        {
            this.bookEntries = new TreeMap<>(); // Default ascending //We need to get the lowest sell price (lowest at top)
        }
    }

    public TradableDTO add(Tradable t)
    {
        if (t == null)
        {
            throw new IllegalArgumentException("Tradable cannot be null");
        }
        if (t.getSide() != side)
        {
            throw new IllegalArgumentException("Tradable side does not match book side");
        }

        Price price = t.getPrice();
        ArrayList<Tradable> tradables = bookEntries.get(price);
        if (tradables == null)
        {
            tradables = new ArrayList<>();
            bookEntries.put(price, tradables);
        }
        tradables.add(t);

        return t.makeTradableDTO();
    }

    public TradableDTO cancel(String id)
    {
        if (id == null || id.isEmpty())
        {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }

        // Loop through each price level
        for (Price price : bookEntries.keySet())
        {
            ArrayList<Tradable> tradables = bookEntries.get(price);

            // Find the tradable with matching ID
            for (int i = 0; i < tradables.size(); i++)
            {
                Tradable t = tradables.get(i);
                if (t.getId().equals(id))
                {
                    System.out.println("**CANCEL: " + t);
                    // Found it - remove from list
                    tradables.remove(i);

                    // If that was the last tradable at this price, remove the price
                    if (tradables.isEmpty())
                    {
                        bookEntries.remove(price);
                    }

                    // Update the volumes
                    t.setCancelledVolume(t.getRemainingVolume());
                    t.setRemainingVolume(0);

                    return t.makeTradableDTO();
                }
            }
        }
        return null;
    }

    public ArrayList<TradableDTO> removeQuotesForUser(String user)
    {
        if (user == null || user.isEmpty())
        {
            throw new IllegalArgumentException("User cannot be null or empty");
        }

        ArrayList<TradableDTO> cancelledQuotes = new ArrayList<>();
        ArrayList<Price> emptyPrices = new ArrayList<>();

        for (Price price : bookEntries.keySet())
        {
            ArrayList<Tradable> tradables = bookEntries.get(price);
            ArrayList<Tradable> keepTradables = new ArrayList<>();

            for (Tradable t : tradables) {
                if (t instanceof QuoteSide && t.getUser().equals(user))
                {
                    System.out.println("**CANCEL: " + t);
                    // Found a quote from this user - cancel it
                    t.setCancelledVolume(t.getRemainingVolume());
                    t.setRemainingVolume(0);
                    cancelledQuotes.add(t.makeTradableDTO());
                }
                else
                {
                    keepTradables.add(t);
                }
            }

            if (keepTradables.isEmpty())
            {
                emptyPrices.add(price);  // Price level is now empty
            }
            else
            {
                bookEntries.put(price, keepTradables);
            }
        }

        for (Price p : emptyPrices)
        {
            bookEntries.remove(p);
        }
        return cancelledQuotes;
    }

    public Price topOfBookPrice()
    {
        return bookEntries.isEmpty() ? null : bookEntries.firstKey();
    }

    public int topOfBookVolume()
    {
        if (bookEntries.isEmpty())
        {
            return 0;
        }
        ArrayList<Tradable> tradables = bookEntries.firstEntry().getValue();
        int totalVolume = 0;
        for (Tradable t : tradables)
        {
            totalVolume += t.getRemainingVolume();
        }
        return totalVolume;
    }


    public boolean tradeOut(Price price, int volume)
    {
        if (price == null || volume <= 0)
        {
            return false;
        }

        Price topPrice = topOfBookPrice();
        if (topPrice == null)
        {
            return false;
        }

        try
        {
            boolean canTrade = (side == BookSide.BUY)
                    ? topPrice.greaterOrEqual(price)
                    : topPrice.lessOrEqual(price);

            if (!canTrade)
            {
                return false;
            }

            int remainingVolume = volume;
            ArrayList<Price> emptyPrices = new ArrayList<>();

            for (Price bookPrice : bookEntries.keySet())
            {
                if (remainingVolume <= 0) break;

                canTrade = (side == BookSide.BUY)
                        ? bookPrice.greaterOrEqual(price)
                        : bookPrice.lessOrEqual(price);

                if (!canTrade) break;

                ArrayList<Tradable> tradables = bookEntries.get(bookPrice);
                ArrayList<Tradable> remainingTradables = new ArrayList<>();

                // First pass - calculate total volume at this price
                int totalVolumeAtPrice = 0;
                for (Tradable t : tradables) {
                    totalVolumeAtPrice += t.getRemainingVolume();
                }

                // Second pass - perform trades
                for (Tradable t : tradables)
                {
                    if (remainingVolume <= 0)
                    {
                        remainingTradables.add(t);
                        continue;
                    }

                    // Calculate this trade's portion of the total volume
                    double ratio = (double) t.getRemainingVolume() / totalVolumeAtPrice;
                    int tradeVolume = (int) Math.ceil(volume * ratio);
                    tradeVolume = Math.min(tradeVolume, remainingVolume);
                    tradeVolume = Math.min(tradeVolume, t.getRemainingVolume());

                    boolean isFullFill = tradeVolume == t.getRemainingVolume();

                    // Update volumes BEFORE printing
                    t.setFilledVolume(t.getFilledVolume() + tradeVolume);
                    t.setRemainingVolume(t.getRemainingVolume() - tradeVolume);
                    remainingVolume -= tradeVolume;

                    // Print AFTER updating
                    System.out.printf("\t%s FILL: (%s %d) %s\n",
                            isFullFill ? "FULL" : "PARTIAL",
                            side,
                            tradeVolume,
                            t.toString());

                    if (t.getRemainingVolume() > 0)
                    {
                        remainingTradables.add(t);
                    }
                }

                if (remainingTradables.isEmpty())
                {
                    emptyPrices.add(bookPrice);
                }
                else
                {
                    bookEntries.put(bookPrice, remainingTradables);
                }
            }

            for (Price p : emptyPrices)
            {
                bookEntries.remove(p);
            }
            return remainingVolume == 0;
        }
        catch (InvalidPriceException e)
        {
            return false;
        }
    }

    @Override
    public String toString()
    {
        if (bookEntries.isEmpty())
        {
            return "\t<Empty>\n";
        }

        String result = "";
        for (Map.Entry<Price, ArrayList<Tradable>> entry : bookEntries.entrySet())
        {
            result += "\t" + entry.getKey() + ":\n";
            for (Tradable t : entry.getValue())
            {
                result += "\t\t" + t.toString() + "\n";
            }
        }
        return result;
    }


}
