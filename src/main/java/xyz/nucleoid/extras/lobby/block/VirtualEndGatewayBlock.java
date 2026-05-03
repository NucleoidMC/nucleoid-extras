package xyz.nucleoid.extras.lobby.block;

import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import xyz.nucleoid.packettweaker.PacketContext;

public class VirtualEndGatewayBlock extends Block implements PolymerBlock {
    public VirtualEndGatewayBlock(Properties settings) {
        super(settings);
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        return Blocks.END_GATEWAY.defaultBlockState();
    }

    @Override
    public void onPolymerBlockSend(BlockState blockState, BlockPos.MutableBlockPos pos, PacketContext.NotNullWithPlayer contexts) {
        var main = new CompoundTag();
        main.putString("id", "minecraft:end_gateway");
        main.putInt("x", pos.getX());
        main.putInt("y", pos.getY());
        main.putInt("z", pos.getZ());
        main.putLong("Age", Long.MIN_VALUE);

        contexts.getPlayer().connection.send(PolymerBlockUtils.createBlockEntityPacket(pos, BlockEntityType.END_GATEWAY, main));
    }
}
