package market;

import java.util.ArrayList;
import java.util.HashMap;


public class CurrentMarketPublisher
{
    private static CurrentMarketPublisher instance = null;

    // Holds data on what stocks the subscribed CurrentMarketObservers want to receive
    private final HashMap<String, ArrayList<CurrentMarketObserver>> filters;


    private CurrentMarketPublisher()
    {
        filters = new HashMap<>();
    }


    public static CurrentMarketPublisher getInstance()
    {
        if (instance == null) {
            instance = new CurrentMarketPublisher();
        }
        return instance;
    }


    public void subscribeCurrentMarket(String symbol, CurrentMarketObserver cmo)
    {
        ArrayList<CurrentMarketObserver> observers = filters.get(symbol);

        if (observers == null)
        {
            observers = new ArrayList<>();
            filters.put(symbol, observers);
        }

        observers.add(cmo);
    }


    public void unSubscribeCurrentMarket(String symbol, CurrentMarketObserver cmo)
    {
        ArrayList<CurrentMarketObserver> observers = filters.get(symbol);

        if (observers == null)
        {
            return;
        }

        observers.remove(cmo);
    }


    public void acceptCurrentMarket(String symbol, CurrentMarketSide buySide, CurrentMarketSide sellSide)
    {
        ArrayList<CurrentMarketObserver> observers = filters.get(symbol);

        if (observers == null)
        {
            return;
        }

        for (CurrentMarketObserver observer : observers)
        {
            observer.updateCurrentMarket(symbol, buySide, sellSide);
        }
    }
}