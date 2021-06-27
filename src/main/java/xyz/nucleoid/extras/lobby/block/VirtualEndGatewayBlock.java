package xyz.nucleoid.extras.lobby.block;

import eu.pb4.polymer.block.VirtualBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class VirtualEndGatewayBlock extends Block implements VirtualBlock {
    public VirtualEndGatewayBlock(Settings settings) {
        super(settings);
    }

    @Override
    public Block getVirtualBlock() {
        return Blocks.END_GATEWAY;
    }

    @Override
    public void sendPacketsAfterCreation(ServerPlayerEntity player, BlockPos pos, BlockState blockState) {
        CompoundTag main = new CompoundTag();
        main.putString("id", "minecraft:end_gateway");
        main.putInt("x", pos.getX());
        main.putInt("y", pos.getY());
        main.putInt("z", pos.getZ());
        main.putLong("Age", Long.MIN_VALUE);

        player.networkHandler.sendPacket(new BlockEntityUpdateS2CPacket(pos, 8, main));
    }
}
