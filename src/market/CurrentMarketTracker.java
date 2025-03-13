package market;

import prices.Price;
import prices.InvalidPriceException;


public class CurrentMarketTracker
{
    private static CurrentMarketTracker instance = null;


    private CurrentMarketTracker() {}


    public static CurrentMarketTracker getInstance()
    {
        if (instance == null)
        {
            instance = new CurrentMarketTracker();
        }
        return instance;
    }


    public void updateMarket(String symbol, Price buyPrice, int buyVolume, Price sellPrice, int sellVolume)
    {
        // Calculate the market width
        Price marketWidth = null;
        if (buyPrice != null && sellPrice != null)
        {
            try
            {
                marketWidth = sellPrice.subtract(buyPrice);
            }
            catch (InvalidPriceException e)
            {
                System.out.println("Error calculating market width: " + e.getMessage());
                marketWidth = null;
            }
        }

        // Create CurrentMarketSide objects for buy and sell sides
        CurrentMarketSide buySide = (buyPrice != null) ? new CurrentMarketSide(buyPrice, buyVolume) : null;
        CurrentMarketSide sellSide = (sellPrice != null) ? new CurrentMarketSide(sellPrice, sellVolume) : null;

        // Print the current market
        System.out.println("*********** Current Market ***********");
        System.out.print("* " + symbol + " ");

        if (buySide != null)
        {
            System.out.print(buySide);
        }
        else
        {
            System.out.print("$0.00x0");
        }

        System.out.print(" - ");

        if (sellSide != null)
        {
            System.out.print(sellSide);
        }
        else
        {
            System.out.print("$0.00x0");
        }

        System.out.print(" [");
        if (marketWidth != null)
        {
            System.out.print(marketWidth);
        }
        else
        {
            System.out.print("$0.00");
        }
        System.out.println("]");
        System.out.println("**************************************");

        // Call the CurrentMarketPublisher to notify observers
        CurrentMarketPublisher.getInstance().acceptCurrentMarket(symbol, buySide, sellSide);
    }
}