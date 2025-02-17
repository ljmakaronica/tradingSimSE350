package tradables;

import prices.Price;

public record TradableDTO(
        String user,
        String product,
        Price price,
        int originalVolume,
        int remainingVolume,
        int cancelledVolume,
        int filledVolume,
        BookSide side,
        String tradableId
)
{}
