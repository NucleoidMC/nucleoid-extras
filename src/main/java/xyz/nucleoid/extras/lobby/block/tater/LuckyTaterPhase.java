package xyz.nucleoid.extras.lobby.block.tater;

import net.minecraft.util.StringRepresentable;

public enum LuckyTaterPhase implements StringRepresentable {
    READY("ready", 0),
    BUILDING_COURAGE("building_courage", 1),
    COOLDOWN("cooldown", 15),
    ;

    private final String name;
    private final int comparatorOutput;

    private LuckyTaterPhase(String name, int comparatorOutput) {
        this.name = name;
        this.comparatorOutput = comparatorOutput;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public int getComparatorOutput() {
        return this.comparatorOutput;
    }
}
