package xyz.nucleoid.extras.lobby.block.tater;

import net.minecraft.util.StringIdentifiable;

public enum LuckyTaterPhase implements StringIdentifiable {
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
    public String asString() {
        return this.name;
    }

    public int getComparatorOutput() {
        return this.comparatorOutput;
    }
}
