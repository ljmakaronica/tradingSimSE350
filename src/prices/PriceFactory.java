package prices;
import java.util.HashMap;
public abstract class PriceFactory
{
    //This is where we hold the Price objects
    private static final HashMap<Integer, Price> priceCache = new HashMap<>();

    public static Price makePrice (int value)
    {
        // Is this price already in the cache?
        Integer key = value;
        Price price = priceCache.get(key);

        // If its not in the cache then we make the price object and then put it in the cache
        if (price == null)
        {
            price = new Price(value);
            priceCache.put(key, price);
        }

        return price;

    }
    public static Price makePrice (String stringValueIn) throws InvalidPriceException
    {
        if (stringValueIn == null || stringValueIn.isEmpty())
        {
            throw new InvalidPriceException("Price format is invalid");
        }
        String inputPrice = stringValueIn;
        for (int i = 0; i < inputPrice.length(); i++)
        {
            char c = inputPrice.charAt(i);
            if (!Character.isDigit(c) && c != '$' && c != '.' && c != '-' && c != ',')
            {
                throw new InvalidPriceException("Price format is invalid");
            }
        }
        if (inputPrice.contains("$") && inputPrice.indexOf("$") != 0) {
            throw new InvalidPriceException("Price format is invalid");
        }

        int numberOfDecimals = 0;
        for (int i = 0; i < inputPrice.length(); i++)
        {
            char c = inputPrice.charAt(i);
            if (c == '.')
            {
                numberOfDecimals++;
            }
        }
        if(numberOfDecimals > 1)
        {
            throw new InvalidPriceException("Price format is invalid.");

        }
        int numbersAfterDecimal = 0;
        boolean decimalPresent = false;
        for (int i = 0; i < inputPrice.length(); i++)
        {
            char c = inputPrice.charAt(i);

            if (c == '.')
            {
                decimalPresent = true;
            }
            else if (decimalPresent && Character.isDigit(c))
            {
                numbersAfterDecimal++;
            }

        }
        if (numbersAfterDecimal == 1 || numbersAfterDecimal > 2)
        {
            throw new InvalidPriceException("Price format is invalid.");
        }
        String allCleanedUp = inputPrice.replaceAll("\\$","").replaceAll(",","");

        if(decimalPresent && numbersAfterDecimal > 0)
        {
            allCleanedUp = allCleanedUp.replaceAll("\\.","");
        }
        else
        {
            allCleanedUp = allCleanedUp.replaceAll("\\.","") + "00";
        }

        int newValue = Integer.parseInt(allCleanedUp);
        return makePrice(newValue);
    }

}
