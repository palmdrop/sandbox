package color.palette.file;

public class PaletteFileFormatException extends Exception {
    public PaletteFileFormatException(String path, String message) {
        super("Wrong format in file " + path + ": " + message);
    }
}
