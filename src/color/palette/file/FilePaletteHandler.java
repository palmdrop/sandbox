package color.palette.file;

import color.colors.Color;
import color.colors.Colors;
import color.palette.Palette;
import color.palette.palettes.balanced.BalancedPalette;
import color.palette.palettes.balanced.BasicPalette;
import color.palette.palettes.unbalanced.MainColorPalette;
import util.file.FileUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FilePaletteHandler {
    /*
    Palette file structure:
        header
        #col1
        #col2
        ....

    ex
        mainColorPalette 3 3
        #col1 (main)
        #col2 (sec1)
        #col3 (sec2)
        ...
        #col7 (acc3)
     */

    // PALETTE TYPES
    private final static String GENERIC = "generic"; //Covers all balanced palettes
    private final static String MAIN_COLOR = "mainColor";

    private FilePaletteHandler() {
    }

    //TODO: save palettes in different ways! depending on type of palette!!!!
    private static boolean savePalette(Palette palette, String header, String path) throws IOException {
        StringBuilder data = new StringBuilder(header);
        data.append("\n");

        for(int i = 0; i < palette.getNumberOfColors(); i++) {
            data.append(palette.getColor(i).toHex());
            if(i != palette.getNumberOfColors() - 1) data.append("\n");
        }
        return FileUtils.createFile(path, data.toString());
    }

    public static boolean saveGenericPalette(Palette palette, String path) throws IOException {
        return savePalette(palette, GENERIC, path);
    }

    public static boolean saveMainColorPalette(MainColorPalette palette, String path) throws IOException {
        String header = MAIN_COLOR + " " + palette.getSecondaryColors().getNumberOfColors();
        return savePalette(palette, header, path);
    }

    public static BalancedPalette loadBalancedPalette(String path) throws IOException, PaletteFileFormatException {
        return (BalancedPalette) loadPalette(path); //Will cause class cast exception if invalid!
    }

    public static MainColorPalette loadMainColorPalette(String path) throws IOException, PaletteFileFormatException {
        return (MainColorPalette) loadPalette(path);
    }

    public static Palette loadPalette(String path) throws IOException, PaletteFileFormatException {
        String[] lines = FileUtils.readFileAsString(path).split("\n");

        String[] args = lines[0].split(" ");

        List<Color> colors = new ArrayList<>(lines.length - 1);
        for(int i = 1; i < lines.length; i++) {
            colors.add(Colors.parseHex(lines[i]));
        }

        switch (args[0]) {
            case GENERIC: {
                return new BasicPalette(colors);
            }
            case MAIN_COLOR: {
                int numberOfSecondaryColors = Integer.parseInt(args[1]);

                Color mainColor = colors.get(0);
                List<Color> secondaryColors = colors.subList(1, 1 + numberOfSecondaryColors);
                List<Color> accentColors = colors.subList(1 + numberOfSecondaryColors, colors.size());

                return new MainColorPalette(mainColor, secondaryColors, accentColors);
            }
            default: throw new PaletteFileFormatException(path, "invalid palette type: " + args[0]); //TODO: wrong exception, should be file format exception?
        }
    }
}
