package exceptions;

public class InvalidOrderException extends Exception
{
    public InvalidOrderException(String message)
    {
        super(message);
    }
}
