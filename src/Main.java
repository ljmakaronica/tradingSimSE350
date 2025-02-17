import prices.Price;
import prices.PriceFactory;
import prices.InvalidPriceException;
import tradables.*;
import book.ProductBook;
import exceptions.*;
public class Main {

    private static ProductBook productBook;

    public static void main(String[] args) {

        try {
            System.out.println("1) Try to make a ProductBook with a null product symbol");
            makeProductBook(null);

            System.out.println("2) Try to make a ProductBook with an empty product symbol");
            makeProductBook("");

            System.out.println("3) Make a ProductBook with a legitimate product symbol");
            makeProductBook("TGT");
            printTopOfBook();
            printBook();

            System.out.println("4) Try to add a null order to the TGT product book");
            addNullOrder();

            System.out.println("5) Try to add a null quote to the TGT product book");
            addNullQuote();

            System.out.println("6) Add 3 BUY orders to the TGT product book");
            TradableDTO dto1 = addOrder("AXE", "$133.75", 50, BookSide.BUY);
            TradableDTO dto2 = addOrder("BAT", "$133.85", 150, BookSide.BUY);
            TradableDTO dto3 = addOrder("CAM", "$133.95", 80, BookSide.BUY);
            assert dto1 != null;
            assert dto2 != null;
            assert dto3 != null;
            printTopOfBook();
            printBook();

            System.out.println("7) Cancel the 3 BUY orders in the TGT product book");
            cancelOrder(dto1.tradableId(), BookSide.BUY);
            cancelOrder(dto2.tradableId(), BookSide.BUY);
            cancelOrder(dto3.tradableId(), BookSide.BUY);
            printTopOfBook();
            printBook();

            System.out.println("8) Add 3 SELL orders to the TGT product book");
            dto1 = addOrder("AXE", "$134.00", 50, BookSide.SELL);
            dto2 = addOrder("BAT", "$134.05", 150, BookSide.SELL);
            dto3 = addOrder("CAM", "$134.15", 80, BookSide.SELL);
            assert dto1 != null;
            assert dto2 != null;
            assert dto3 != null;
            printTopOfBook();
            printBook();

            System.out.println("9) Cancel the 3 SELL orders in the TGT product book");
            cancelOrder(dto1.tradableId(), BookSide.SELL);
            cancelOrder(dto2.tradableId(), BookSide.SELL);
            cancelOrder(dto3.tradableId(), BookSide.SELL);
            printTopOfBook();
            printBook();

            System.out.println("10) Add 3 quotes to the TGT product book");
            addQuote("AXE", "$134.00", 50, "$134.30", 50);
            addQuote("BAT", "$133.90", 50, "$134.20", 50);
            addQuote("CAM", "$133.80", 50, "$134.10", 50);
            printTopOfBook();
            printBook();

            System.out.println("11) Change the a quotes in the TGT product book");
            addQuote("AXE", "$134.01", 50, "$134.05", 50);
            printBook();

            System.out.println("12) Cancel the 3 quotes in the TGT product book");
            removeQuote("AXE");
            removeQuote("BAT");
            removeQuote("CAM");
            printTopOfBook();
            printBook();

            System.out.println("13) Try to cancel bad orders in the TGT product book");
            cancelOrder(null, BookSide.BUY);
            cancelOrder(null, BookSide.SELL);

            System.out.println("14) Try to cancel a bad quote in the TGT product book");
            removeQuote(null);

            System.out.println("15) Trade test: Single BUY and single SELL order - fully trade");
            addOrder("DEM", "$134.00", 50, BookSide.BUY);
            addOrder("EEG", "$134.00", 50, BookSide.SELL);
            printBook();

            System.out.println("16) Trade test: Single BUY and single SELL order - full/partial trade & cancel");
            addOrder("FEN", "$134.00", 50, BookSide.BUY);
            dto1 = addOrder("GAM", "$134.00", 100, BookSide.SELL);
            assert dto1 != null;
            printTopOfBook();
            printBook();
            cancelOrder(dto1.tradableId(), BookSide.SELL);
            System.out.println();

            System.out.println("17) Trade test: Multiple BUY and single SELL order - fully trade");
            addOrder("HOB", "$134.00", 60, BookSide.BUY);
            addOrder("IGY", "$134.00", 70, BookSide.BUY);
            addOrder("JAM", "$134.00", 80, BookSide.BUY);
            addOrder("KIT", "$134.00", 210, BookSide.SELL);
            printTopOfBook();
            printBook();

            System.out.println("18) Trade test: Multiple BUY and single SELL order - full/partial trade & cancel");
            dto1 = addOrder("LEM", "$134.00", 60, BookSide.BUY);
            dto2 = addOrder("MOD", "$134.00", 70, BookSide.BUY);
            dto3 = addOrder("NET", "$134.00", 80, BookSide.BUY);
            addOrder("OBS", "$134.00", 100, BookSide.SELL);
            assert dto1 != null;
            assert dto2 != null;
            assert dto3 != null;
            cancelOrder(dto1.tradableId(), BookSide.BUY);
            cancelOrder(dto2.tradableId(), BookSide.BUY);
            cancelOrder(dto3.tradableId(), BookSide.BUY);
            printTopOfBook();
            printBook();

            System.out.println("19) Trade test: Single BUY and multiple SELL orders - fully trade");
            addOrder("POP", "$134.00", 50, BookSide.SELL);
            addOrder("QAB", "$134.00", 60, BookSide.SELL);
            addOrder("REX", "$134.00", 70, BookSide.SELL);
            addOrder("SAM", "$134.00", 180, BookSide.BUY);
            printTopOfBook();
            printBook();

            System.out.println("20) Trade test: Single BUY and multiple SELL orders - full/partial trade & cancel");
            addOrder("TIM", "$134.00", 60, BookSide.SELL);
            addOrder("UBL", "$134.00", 70, BookSide.SELL);
            addOrder("VOM", "$134.00", 80, BookSide.SELL);
            dto1 = addOrder("WUN", "$134.00", 250, BookSide.BUY);
            assert dto1 != null;
            cancelOrder(dto1.tradableId(), BookSide.BUY);
            printTopOfBook();
            printBook();

            System.out.println("21) Trade test: Multiple orders and quotes on BUY side traded with large SELL order");
            addQuote("XEN", "$133.00", 50, "$133.20", 75);
            addQuote("YAM", "$133.00", 50, "$133.20", 100);
            addOrder("ZEN", "$133.00", 15, BookSide.BUY);
            addOrder("AAM", "$133.00", 180, BookSide.BUY);
            addOrder("BEN", "$133.00", 65, BookSide.BUY);
            printTopOfBook();
            printBook();
            addOrder("CEN", "$133.00", 360, BookSide.SELL);
            removeQuote("XEN");
            removeQuote("YAM");
            printTopOfBook();
            printBook();

            System.out.println("22) Trade test: Multiple orders and quotes on BUY side traded with 1 SELL order");
            addQuote("XEN", "$133.00", 40, "$133.20", 75);
            addQuote("YAM", "$133.00", 60, "$133.20", 100);
            dto1 = addOrder("ZEN", "$133.00", 15, BookSide.BUY);
            dto2 = addOrder("AAM", "$133.00", 180, BookSide.BUY);
            dto3 = addOrder("BEN", "$133.00", 65, BookSide.BUY);
            assert dto1 != null;
            assert dto2 != null;
            assert dto3 != null;
            printTopOfBook();
            printBook();
            addOrder("CEN", "$133.00", 200, BookSide.SELL);
            printTopOfBook();
            printBook();

            System.out.println("23) Trade test: Cancel all booked orders and quotes");
            removeQuote("XEN");
            removeQuote("YAM");
            cancelOrder(dto1.tradableId(), BookSide.BUY);
            cancelOrder(dto2.tradableId(), BookSide.BUY);
            cancelOrder(dto3.tradableId(), BookSide.BUY);
            printTopOfBook();
            printBook();

        } catch (Exception e) {
            System.out.println("Unexpected exception: " + e.getClass().getSimpleName() +
                    ":" + e.getMessage());
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////
    private static void makeProductBook(String symbol) {
        try {
            productBook = new ProductBook(symbol);
        } catch (Exception e) {
            System.out.println("Failed to make product book: " + symbol + "\n");
        }
    }

    private static void addNullOrder() {
        // Try to add a null order to the TGT product book
        try {
            productBook.add((Tradable) null);
            System.out.println("Did not throw exception when adding null order\n");
        } catch (Exception e) {
            System.out.println("Correctly rejected null order\n");
        }
    }

    private static void addNullQuote() {
        // Try to add a null quote to the TGT product book
        try {
            productBook.add((Quote) null);
            System.out.println("Did not throw exception when adding null quote\n");
        } catch (Exception e) {
            System.out.println("Correctly rejected null quote\n");
        }
    }

    private static TradableDTO addOrder(String user, String price, int volume, BookSide side) {
        try {
            return productBook.add(new Order(user, "TGT", PriceFactory.makePrice(price), volume, side));
        } catch (Exception e) {
            System.out.println("Failed to add BUY orders");
        }
        return null;
    }

    private static void cancelOrder(String tradableId, BookSide side) {
        try {
            productBook.cancel(side, tradableId);
        } catch (Exception e) {
            System.out.println("Failed to cancel " + side + " order\n");
        }
    }

    private static void addQuote(String user, String buyPrice, int buyVolume, String sellPrice, int sellVolume) {
        try {
            productBook.add(new Quote("TGT", PriceFactory.makePrice(buyPrice), buyVolume,
                    PriceFactory.makePrice(sellPrice), sellVolume, user));
        } catch (Exception e) {
            System.out.println("Failed to add quotes");
        }
    }

    private static void removeQuote(String user) {
        try {
            productBook.removeQuotesForUser(user);
        } catch (Exception e) {
            System.out.println("Failed to cancel " + user + " quote\n");
        }
    }

    private static void printTopOfBook() {
        // Print the top of the book
        System.out.println();
        System.out.println("Top of BUY book: " + productBook.getTopOfBookString(BookSide.BUY));
        System.out.println("Top of SELL book: " + productBook.getTopOfBookString(BookSide.SELL));
        System.out.println();

    }

    private static void printBook() {
        // Print the TGT product book
        System.out.println(productBook);
        System.out.println();
    }

}
