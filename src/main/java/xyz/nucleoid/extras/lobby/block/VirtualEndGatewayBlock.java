package xyz.nucleoid.extras.lobby.block;

import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.packettweaker.PacketContext;

public class VirtualEndGatewayBlock extends Block implements PolymerBlock {
    public VirtualEndGatewayBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        return Blocks.END_GATEWAY.getDefaultState();
    }

    @Override
    public void onPolymerBlockSend(BlockState blockState, BlockPos.Mutable pos, PacketContext.NotNullWithPlayer contexts) {
        var main = new NbtCompound();
        main.putString("id", "minecraft:end_gateway");
        main.putInt("x", pos.getX());
        main.putInt("y", pos.getY());
        main.putInt("z", pos.getZ());
        main.putLong("Age", Long.MIN_VALUE);

        contexts.getPlayer().networkHandler.sendPacket(PolymerBlockUtils.createBlockEntityPacket(pos, BlockEntityType.END_GATEWAY, main));
    }
}
