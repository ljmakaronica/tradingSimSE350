package users;

import java.util.TreeMap;
import exceptions.DataValidationException;
import tradables.TradableDTO;

public class UserManager
{
    private static UserManager instance = null;

    // userId is key and User object is value
    private final TreeMap<String, User> users;

    private UserManager()
    {
        users = new TreeMap<>();
    }

    public static UserManager getInstance()
    {
        if (instance == null)
        {
            instance = new UserManager();
        }
        return instance;
    }

    public void init(String[] usersIn) throws DataValidationException
    {
        if (usersIn == null)
        {
            throw new DataValidationException("User array cannot be null");
        }

        for (String userId : usersIn)
        {
            try
            {
                User user = new User(userId);
                users.put(userId, user);
            }
            catch (Exception e)
            {
                throw new DataValidationException("Failed to create user: " + e.getMessage());
            }
        }
    }

    public void updateTradable(String userId, TradableDTO tradableDTO) throws DataValidationException
    {
        if (userId == null)
        {
            throw new DataValidationException("User ID cannot be null");
        }

        if (tradableDTO == null)
        {
            throw new DataValidationException("TradableDTO cannot be null");
        }

        User user = users.get(userId);
        if (user == null)
        {
            throw new DataValidationException("User does not exist: " + userId);
        }

        user.updateTradable(tradableDTO);
    }

    @Override
    public String toString()
    {
        String result = "";

        for (User user : users.values())
        {
            result += user.toString();
        }

        return result;
    }
}