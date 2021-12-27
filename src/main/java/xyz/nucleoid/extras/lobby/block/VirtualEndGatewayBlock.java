package xyz.nucleoid.extras.lobby.block;

import eu.pb4.polymer.api.block.PolymerBlockUtils;
import eu.pb4.polymer.block.VirtualBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
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
        var main = new NbtCompound();
        main.putString("id", "minecraft:end_gateway");
        main.putInt("x", pos.getX());
        main.putInt("y", pos.getY());
        main.putInt("z", pos.getZ());
        main.putLong("Age", Long.MIN_VALUE);

        player.networkHandler.sendPacket(PolymerBlockUtils.createBlockEntityPacket(pos, BlockEntityType.END_GATEWAY, main));
    }
}
