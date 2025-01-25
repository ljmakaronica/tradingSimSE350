package prices;

public class Price
{
    private final int cents;

    public Price(int cents)
    {
        this.cents = cents;
    }

    public Price add(Price p2)
    {
        return PriceFactory.makePrice(cents + p2.cents);
    }

}
