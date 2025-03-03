package book;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;
import tradables.*;
import exceptions.DataValidationException;
import exceptions.InvalidSymbolException;
import users.UserManager;

public class ProductManager
{
    private static ProductManager myProductManager = null;

    // Product symbol is key, ProductBook object is value
    private final HashMap<String, ProductBook> productBooks;

    private ProductManager()
    {
        productBooks = new HashMap<>();
    }

    public static ProductManager getInstance()
    {
        if (myProductManager == null)
        {
            myProductManager = new ProductManager();
        }
        return myProductManager;
    }

    public void addProduct(String symbol) throws DataValidationException
    {
        if (symbol == null)
        {
            throw new DataValidationException("Product symbol cannot be null");
        }

        try
        {
            ProductBook productBook = new ProductBook(symbol);
            productBooks.put(symbol, productBook);
        }
        catch (InvalidSymbolException e)
        {
            throw new DataValidationException("Invalid product symbol: " + e.getMessage());
        }
    }

    public ProductBook getProductBook(String symbol) throws DataValidationException
    {
        if (!productBooks.containsKey(symbol))
        {
            throw new DataValidationException("Product does not exist: " + symbol);
        }

        return productBooks.get(symbol);
    }

    public String getRandomProduct() throws DataValidationException
    {
        if (productBooks.isEmpty())
        {
            throw new DataValidationException("No products exist");
        }

        ArrayList<String> symbols = new ArrayList<>(productBooks.keySet());
        int randomIndex = new Random().nextInt(symbols.size());
        return symbols.get(randomIndex);
    }

    public TradableDTO addTradable(Tradable tradable) throws DataValidationException
    {
        if (tradable == null)
        {
            throw new DataValidationException("Tradable cannot be null");
        }

        String symbol = tradable.getProduct();
        ProductBook book = getProductBook(symbol);

        try
        {
            TradableDTO dto = book.add(tradable);
            UserManager.getInstance().updateTradable(tradable.getUser(), dto);
            return dto;
        }
        catch (Exception e)
        {
            throw new DataValidationException("Failed to add tradable: " + e.getMessage());
        }
    }

    public TradableDTO[] addQuote(Quote quote) throws DataValidationException
    {
        if (quote == null)
        {
            throw new DataValidationException("Quote cannot be null");
        }

        String symbol = quote.getProduct();
        ProductBook book = getProductBook(symbol);

        try
        {
            // First remove any existing quotes for this user
            book.removeQuotesForUser(quote.getUser());

            // Then add the new quote
            TradableDTO[] dtos = book.add(quote);

            // Update the user with the new quote sides
            for (TradableDTO dto : dtos)
            {
                UserManager.getInstance().updateTradable(quote.getUser(), dto);
            }

            return dtos;
        }
        catch (Exception e)
        {
            throw new DataValidationException("Failed to add quote: " + e.getMessage());
        }
    }

    public TradableDTO cancel(TradableDTO tradableDTO) throws DataValidationException
    {
        if (tradableDTO == null)
        {
            throw new DataValidationException("TradableDTO cannot be null");
        }

        try
        {
            ProductBook book = getProductBook(tradableDTO.product());
            TradableDTO result = book.cancel(tradableDTO.side(), tradableDTO.tradableId());

            if (result != null)
            {
                UserManager.getInstance().updateTradable(tradableDTO.user(), result);
                return result;
            }
            else
            {
                System.out.println("Failed to cancel tradable: " + tradableDTO.tradableId());
                return null;
            }
        }
        catch (Exception e)
        {
            throw new DataValidationException("Failed to cancel tradable: " + e.getMessage());
        }
    }

    public TradableDTO[] cancelQuote(String symbol, String user) throws DataValidationException
    {
        if (symbol == null)
        {
            throw new DataValidationException("Product symbol cannot be null");
        }

        if (user == null)
        {
            throw new DataValidationException("User ID cannot be null");
        }

        ProductBook book = getProductBook(symbol);
        ArrayList<TradableDTO> cancelledQuotes = book.removeQuotesForUser(user);

        // Update the user with the cancelled quotes
        for (TradableDTO dto : cancelledQuotes)
        {
            UserManager.getInstance().updateTradable(user, dto);
        }

        return cancelledQuotes.toArray(new TradableDTO[0]);
    }

    @Override
    public String toString()
    {
        String result = "";

        for (ProductBook book : productBooks.values())
        {
            result += book.toString() + "\n";
        }

        return result;
    }
}