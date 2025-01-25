package prices;
import java.util.Objects;

public class Price implements Comparable<Price>
{
    private final int cents;

    public Price(int cents)
    {
        this.cents = cents;
    }

    public boolean isNegative()
    {
        return cents < 0;
    }

    public Price add(Price p) throws InvalidPriceException
    {
        if (p == null) {
            throw new InvalidPriceException("Price cannot be null");
        }
        return PriceFactory.makePrice(cents + p.cents);
    }

    public Price subtract(Price p) throws InvalidPriceException
    {
        if (p == null) {
            throw new InvalidPriceException("Price cannot be null");
        }
        return PriceFactory.makePrice(cents - p.cents);
    }

    public Price multiply(int n)
    {
        return PriceFactory.makePrice(cents * n);
    }

    public boolean greaterOrEqual(Price p) throws InvalidPriceException
    {
        if (p == null) {
            throw new InvalidPriceException("Price cannot be null");
        }
        return cents >= p.cents;
    }
    public boolean lessOrEqual(Price p) throws InvalidPriceException
    {
        if (p == null) {
            throw new InvalidPriceException("Price cannot be null");
        }
        return cents <= p.cents;
    }
    public boolean greaterThan(Price p) throws InvalidPriceException
    {
        if (p == null) {
            throw new InvalidPriceException("Price cannot be null");
        }
        return cents > p.cents;
    }
    public boolean lessThan(Price p) throws InvalidPriceException
    {
        if (p == null) {
            throw new InvalidPriceException("Price cannot be null");
        }
        return cents < p.cents;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == null || getClass() != o.getClass()) return false;
        Price price = (Price) o;
        return cents == price.cents;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(cents);
    }

    @Override
    public int compareTo(Price p)
    {
        if (p == null)
        {
            return -1;
        }
        return Math.abs(cents - p.cents);
    }

    @Override
    public String toString()
    {
        return String.format("$%,.2f", cents/100.0);
    }

}
