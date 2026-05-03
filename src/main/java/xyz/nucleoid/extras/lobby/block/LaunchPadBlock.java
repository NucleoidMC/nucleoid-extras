package xyz.nucleoid.extras.lobby.block;

import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.extras.component.LauncherComponent;
import xyz.nucleoid.plasmid.api.util.PlayerUtil;

public class LaunchPadBlock extends Block implements EntityBlock, PolymerBlock {
    private final BlockState virtualBlockState;

    public LaunchPadBlock(Properties settings, BlockState virtualBlockState) {
        super(settings);
        this.virtualBlockState = virtualBlockState;
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        return this.virtualBlockState;
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity, InsideBlockEffectApplier handler, boolean b) {
        var blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof LaunchPadBlockEntity launchPad) {
            tryLaunch(entity, entity, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, new LauncherComponent(launchPad.getPitch(), launchPad.getPower(), launchPad.getSound()));
        }

        super.entityInside(state, world, pos, entity, handler, b);
    }

    public static boolean tryLaunch(Entity entity, Entity source, SoundEvent defaultSound, SoundSource category, LauncherComponent launcher) {
        if (launcher != null && entity.onGround() && !(entity instanceof ArmorStand)) {
            entity.setDeltaMovement(getVector(launcher.pitch(), source.getViewYRot(0)).scale(launcher.power()));
            SoundEvent sound = launcher.sound().map(Holder::value).orElse(defaultSound);

            if (entity instanceof ServerPlayer player) {
                player.connection.send(new ClientboundSetEntityMotionPacket(entity));
                playLaunchSound(player, sound, category);
            }
            if (source != entity && source instanceof ServerPlayer player) {
                playLaunchSound(player, sound, category);
            }

            return true;
        }

        return false;
    }

    public static void playLaunchSound(ServerPlayer player, SoundEvent sound, SoundSource category) {
        PlayerUtil.playSoundToPlayer(player, sound, category, 0.5f, 1);
    }

    private static Vec3 getVector(float pitch, float yaw) {
        double pitchRad = Math.toRadians(pitch);
        double yawRad = Math.toRadians(yaw);

        double horizontal = -Math.cos(pitchRad);
        return new Vec3(
                Math.sin(yawRad) * horizontal,
                Math.sin(pitchRad),
                -Math.cos(yawRad) * horizontal
        );
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LaunchPadBlockEntity(pos, state);
    }
}
