package users;
import market.CurrentMarketObserver;
import market.CurrentMarketSide;
import exceptions.InvalidUserException;
import tradables.TradableDTO;
import java.util.HashMap;

public class User implements CurrentMarketObserver
{
    private String userId;

    // tradable id is key, tradable DTO is value
    private HashMap<String, TradableDTO> tradables;

    // symbol is key, CurrentMarketSide array is value
    private HashMap<String, CurrentMarketSide[]> currentMarkets;

    public User(String userId) throws InvalidUserException
    {
        setUserId(userId);
        this.tradables = new HashMap<>();
        this.currentMarkets = new HashMap<>();
    }

    //this wasnt in the assignment writeup but I saw on discord that we should add it
    public String getUserId()
    {
        return userId;
    }


    private void setUserId(String userId) throws InvalidUserException
    {
        // Validate userId (3 letters, no spaces, no numbers, no special characters)
        if (userId == null || userId.length() != 3)
        {
            throw new InvalidUserException("User ID must be exactly 3 characters");
        }

        for (char c : userId.toCharArray())
        {
            if (!Character.isLetter(c))
            {
                throw new InvalidUserException("User ID must contain only letters");
            }
        }

        this.userId = userId;
    }

    public void updateTradable(TradableDTO tradableDTO)
    {
        if (tradableDTO != null)
        {
            tradables.put(tradableDTO.tradableId(), tradableDTO);
        }
    }


    @Override
    public void updateCurrentMarket(String symbol, CurrentMarketSide buySide, CurrentMarketSide sellSide)
    {
        // Create a 2-element array of CurrentMarketSide objects
        CurrentMarketSide[] sides = new CurrentMarketSide[2];
        sides[0] = buySide;  // Buy side
        sides[1] = sellSide; // Sell side

        // Store in the currentMarkets HashMap
        currentMarkets.put(symbol, sides);
    }


    public String getCurrentMarkets()
    {
        StringBuilder result = new StringBuilder();

        for (String symbol : currentMarkets.keySet())
        {
            CurrentMarketSide[] sides = currentMarkets.get(symbol);
            result.append(symbol).append(" ");

            if (sides[0] != null)
            {
                result.append(sides[0].toString());
            }
            else
            {
                result.append("$0.00x0");
            }

            result.append(" - ");

            if (sides[1] != null)
            {
                result.append(sides[1].toString());
            }
            else
            {
                result.append("$0.00x0");
            }

            result.append("\n");
        }

        return result.toString();
    }

    @Override
    public String toString()
    {
        String result = "User Id: " + userId + "\n";

        for (TradableDTO dto : tradables.values())
        {
            result += "Product: " + dto.product() +
                    ", Price: " + dto.price() +
                    ", OriginalVolume: " + dto.originalVolume() +
                    ", RemainingVolume: " + dto.remainingVolume() +
                    ", CancelledVolume: " + dto.cancelledVolume() +
                    ", FilledVolume: " + dto.filledVolume() +
                    ", User: " + dto.user() +
                    ", Side: " + dto.side() +
                    ", Id: " + dto.tradableId() +
                    "\n";
        }

        return result;
    }
}