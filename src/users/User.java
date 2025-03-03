package users;

import exceptions.InvalidUserException;
import tradables.TradableDTO;
import java.util.HashMap;

public class User
{
    private String userId;

    //tradable id is key, tradable DTO is value
    private HashMap<String, TradableDTO> tradables;

    public User(String userId) throws InvalidUserException
    {
        setUserId(userId);
        this.tradables = new HashMap<>();
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
