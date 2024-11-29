package xyz.nucleoid.extras.test;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Dynamic;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.Schemas;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringNbtReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DataFixTests {
    private static final int DATA_VERSION_1_20_4 = 3700;
    private static final int DATA_VERSION_1_21_3 = 4082;


    @BeforeAll
    public static void beforeAll() {
        SharedConstants.createGameVersion();
        Bootstrap.initialize();
    }

    @Test
    public void testGamePortalOpenerWithoutIdComponentization() {
        assertItemStackComponentization("""
            {
                id: "nucleoid_extras:game_portal_opener",
                Count: 1
            }
        """, """
            {
                id: "nucleoid_extras:game_portal_opener",
                count: 1
            }
        """);
    }

    @Test
    public void testGamePortalOpenerComponentization() {
        assertItemStackComponentization("""
            {
                id: "nucleoid_extras:game_portal_opener",
                Count: 1,
                tag: {
                    GamePortal: "nucleoid:top_level/navigator"
                }
            }
        """, """
            {
                id: "nucleoid_extras:game_portal_opener",
                count: 1,
                components: {
                    "nucleoid_extras:game_portal": {
                        game_portal_id: "nucleoid:top_level/navigator"
                    }
                }
            }
        """);
    }

    @Test
    public void testTaterBoxWithoutSelectionComponentization() {
        assertItemStackComponentization("""
            {
                id: "nucleoid_extras:tater_box",
                Count: 1
            }
        """, """
            {
                id: "nucleoid_extras:tater_box",
                count: 1
            }
        """);
    }

    @Test
    public void testTaterBoxComponentization() {
        assertItemStackComponentization("""
            {
                id: "nucleoid_extras:tater_box",
                Count: 1,
                tag: {
                    SelectedTater: "nucleoid_extras:tiny_potato"
                }
            }
        """, """
            {
                id: "nucleoid_extras:tater_box",
                count: 1,
                components: {
                    "nucleoid_extras:tater_selection": {
                        tater: "nucleoid_extras:tiny_potato"
                    }
                }
            }
        """);
    }

    @Test
    public void testTaterBoxWithLegacyTatersComponentization() {
        assertItemStackComponentization("""
            {
                id: "nucleoid_extras:tater_box",
                Count: 1,
                tag: {
                    SelectedTater: "nucleoid_extras:botanical_tater",
                    Taters: [
                        "nucleoid_extras:tiny_potato",
                        "nucleoid_extras:botanical_tater"
                    ]
                }
            }
        """, """
            {
                id: "nucleoid_extras:tater_box",
                count: 1,
                components: {
                    "nucleoid_extras:tater_selection": {
                        tater: "nucleoid_extras:botanical_tater"
                    },
                    "minecraft:custom_data": {
                        Taters: [
                            "nucleoid_extras:tiny_potato",
                            "nucleoid_extras:botanical_tater"
                        ]
                    }
                }
            }
        """);
    }

    @Test
    public void testCreativeTaterBoxComponentization() {
        assertItemStackComponentization("""
            {
                id: "nucleoid_extras:creative_tater_box",
                Count: 1,
                tag: {
                    SelectedTater: "nucleoid_extras:bedrock_tater"
                }
            }
        """, """
            {
                id: "nucleoid_extras:creative_tater_box",
                count: 1,
                components: {
                    "nucleoid_extras:tater_selection": {
                        tater: "nucleoid_extras:bedrock_tater"
                    }
                }
            }
        """);
    }

    @Test
    public void testTaterGuidebookComponentization() {
        assertItemStackComponentization("""
            {
                id: "nucleoid_extras:tater_guidebook",
                Count: 1,
                tag: {
                    tater_positions: {
                        "nucleoid_extras:irritater": [
                            {X: 0, Y: 1, Z: 2}
                        ]
                    }
                }
            }
        """, """
            {
                id: "nucleoid_extras:tater_guidebook",
                count: 1,
                components: {
                    "nucleoid_extras:tater_positions": {
                        positions: {
                            "nucleoid_extras:irritater": [
                                {X: 0, Y: 1, Z: 2}
                            ]
                        }
                    }
                }
            }
        """);
    }

    @Test
    public void testDefaultLaunchFeatherComponentization() {
        assertItemStackComponentization("""
            {
                id: "nucleoid_extras:launch_feather",
                Count: 1
            }
        """, """
            {
                id: "nucleoid_extras:launch_feather",
                count: 1
            }
        """);
    }

    @Test
    public void testLaunchFeatherWithPitchComponentization() {
        assertItemStackComponentization("""
            {
                id: "nucleoid_extras:launch_feather",
                Count: 1,
                tag: {
                    Pitch: 20.1
                }
            }
        """, """
            {
                id: "nucleoid_extras:launch_feather",
                count: 1,
                components: {
                    "nucleoid_extras:launcher": {
                        pitch: 20.1
                    }
                }
            }
        """);
    }

    @Test
    public void testLaunchFeatherWithPowerComponentization() {
        assertItemStackComponentization("""
            {
                id: "nucleoid_extras:launch_feather",
                Count: 1,
                tag: {
                    Power: 5.2
                }
            }
        """, """
            {
                id: "nucleoid_extras:launch_feather",
                count: 1,
                components: {
                    "nucleoid_extras:launcher": {
                        power: 5.2
                    }
                }
            }
        """);
    }

    @Test
    public void testLaunchFeatherWithPitchAndPowerComponentization() {
        assertItemStackComponentization("""
            {
                id: "nucleoid_extras:launch_feather",
                Count: 1,
                tag: {
                    Pitch: 18.3,
                    Power: 4.6
                }
            }
        """, """
            {
                id: "nucleoid_extras:launch_feather",
                count: 1,
                components: {
                    "nucleoid_extras:launcher": {
                        pitch: 18.3,
                        power: 4.6
                    }
                }
            }
        """);
    }

    private static void assertItemStackComponentization(String oldNbtString, String newNbtString) {
        var oldNbt = parseNbtString("oldNbt", oldNbtString);
        var newNbt = parseNbtString("newNbt", newNbtString);

        var input = new Dynamic<>(NbtOps.INSTANCE, oldNbt);
        var output = Schemas.getFixer().update(TypeReferences.ITEM_STACK, input, DATA_VERSION_1_20_4, DATA_VERSION_1_21_3);

        assertEquals(newNbt, output.getValue());
    }

    private static NbtCompound parseNbtString(String name, String string) {
        try {
            return StringNbtReader.parse(string);
        } catch (CommandSyntaxException e) {
            throw new RuntimeException("Failed to parse " + name, e);
        }
    }
}
