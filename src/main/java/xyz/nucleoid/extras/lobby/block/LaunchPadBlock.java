package xyz.nucleoid.extras.lobby.block;

import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import xyz.nucleoid.extras.component.LauncherComponent;
import xyz.nucleoid.packettweaker.PacketContext;
import org.jetbrains.annotations.Nullable;

public class LaunchPadBlock extends Block implements BlockEntityProvider, PolymerBlock {
    private final BlockState virtualBlockState;

    public LaunchPadBlock(Settings settings, BlockState virtualBlockState) {
        super(settings);
        this.virtualBlockState = virtualBlockState;
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        return this.virtualBlockState;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        var blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof LaunchPadBlockEntity launchPad) {
            tryLaunch(entity, entity, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, new LauncherComponent(launchPad.getPitch(), launchPad.getPower(), launchPad.getSound()));
        }

        super.onEntityCollision(state, world, pos, entity);
    }

    public static boolean tryLaunch(Entity entity, Entity source, SoundEvent defaultSound, SoundCategory category, LauncherComponent launcher) {
        if (launcher != null && entity.isOnGround() && !(entity instanceof ArmorStandEntity)) {
            entity.setVelocity(getVector(launcher.pitch(), source.getYaw(0)).multiply(launcher.power()));
            SoundEvent sound = launcher.sound().map(RegistryEntry::value).orElse(defaultSound);

            if (entity instanceof ServerPlayerEntity player) {
                player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(entity));
                playLaunchSound(player, sound, category);
            }
            if (source != entity && source instanceof ServerPlayerEntity player) {
                playLaunchSound(player, sound, category);
            }

            return true;
        }

        return false;
    }

    public static void playLaunchSound(ServerPlayerEntity player, SoundEvent sound, SoundCategory category) {
        player.playSoundToPlayer(sound, category, 0.5f, 1);
    }

    private static Vec3d getVector(float pitch, float yaw) {
        double pitchRad = Math.toRadians(pitch);
        double yawRad = Math.toRadians(yaw);

        double horizontal = -Math.cos(pitchRad);
        return new Vec3d(
                Math.sin(yawRad) * horizontal,
                Math.sin(pitchRad),
                -Math.cos(yawRad) * horizontal
        );
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LaunchPadBlockEntity(pos, state);
    }
}
