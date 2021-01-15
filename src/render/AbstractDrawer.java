package render;

import util.geometry.Rectangle;

public abstract class AbstractDrawer implements Drawer {
    protected Rectangle bounds;

    public AbstractDrawer(Rectangle bounds) {
        this.bounds = bounds;
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }
}
