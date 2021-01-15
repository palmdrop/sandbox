package color.space;

import java.util.List;

public class InvalidColorComponentException extends Exception {
    public InvalidColorComponentException() {
        super();
    }
    public InvalidColorComponentException(String message) {
        super(message);
    }
    public InvalidColorComponentException(int numberOfComponents, double... values) {
        this("Number of components = " + numberOfComponents + ". Supplied values = " + List.of(values));
    }
}
