package util.space.region;

public class Grid {
    private double rows;
    private double columns;
    private double width;
    private double height;

    private double desiredCellSize;

    public static final int RESIZE_GROW = 0;
    public static final int RESIZE_SCALE = 1;

    private int resizeMode = RESIZE_SCALE;

    public Grid(double width, double height, double desiredCellSize) {
        init(width, height, desiredCellSize);
    }

    private void init(double width, double height, double desiredCellSize) {
        //TODO: fix resizing trouble
        this.desiredCellSize = desiredCellSize;
        int detail = (int)(width / desiredCellSize);

        this.width = width;
        this.height = height;

        double big = Math.max(width, height);
        double sma = Math.min(width, height);
        if(width > height) rows = detail;
        else columns = detail;

        double cs = sma / detail;
        double fit = big / cs;
        if(width > height) columns = (int)fit;
        else rows = (int)fit;
    }

    public double calculateCellWidth(double width) {
        return this.width / columns;
    }

    public double calculateCellHeight(double height) {
        return this.height / rows;
    }

    public int getResizeMode() { return resizeMode; }
    public void setResizeMode(int resizeMode) { this.resizeMode = resizeMode; }

    public double getRows() {
        return rows;
    }
    public double getColumns() {
        return columns;
    }
    public double getCellWidth() {
        return width / columns;
    }
    public double getCellHeight() {
        return height / rows;
    }

    public void resize(double width, double height) {
        if(resizeMode == RESIZE_GROW) init(width, height, desiredCellSize);
        else {
            this.width = width;
            this.height = height;

            desiredCellSize = width / columns;
        }
    }
}