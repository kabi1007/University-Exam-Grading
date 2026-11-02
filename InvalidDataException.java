/** Custom (user defined) exception. Thrown whenever a business rule is broken. */
public class InvalidDataException extends Exception {
    public InvalidDataException(String message) {
        super(message);
    }
}
