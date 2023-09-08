package xyz.nucleoid.extras.lobby.item.tater;

public class CreativeTaterBoxItem extends TaterBoxItem {
    private static final int COLOR = 0xFF00FF;

    public CreativeTaterBoxItem(Settings settings) {
        super(settings);
    }

    @Override
    protected int getEmptyColor() {
        return COLOR;
    }

    @Override
    protected boolean isCreative() {
        return true;
    }
}
