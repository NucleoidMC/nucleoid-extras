package xyz.nucleoid.extras.placeholder;

import net.minecraft.text.TextContent;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.manager.GameSpaceManager;

import java.util.UUID;

public record GameTextContent(GameSpace gameSpace) implements TextContent {
}
