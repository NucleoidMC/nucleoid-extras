package xyz.nucleoid.extras.lobby.tree;

import org.joml.Matrix4f;

import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.elements.InteractionElement;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import eu.pb4.polymer.virtualentity.api.elements.VirtualElement.InteractionHandler;
import net.minecraft.block.Block;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import xyz.nucleoid.extras.lobby.block.TreeDecorationBlockEntity;

public final class OrnamentModel implements InteractionHandler {
    public static final float WIDTH = 8 / 16f;
    public static final float HEIGHT = 11 / 16f;

    private final TreeDecorationBlockEntity blockEntity;
    private final Ornament ornament;

    private final ItemDisplayElement hook;
    private final ItemDisplayElement item;

    private final InteractionElement interaction;

    private int wobbleTicks = 0;
    private float wobbleStrength = 0;

    public OrnamentModel(TreeDecorationBlockEntity blockEntity, Ornament ornament) {
        this.blockEntity = blockEntity;
        this.ornament = ornament;

        this.hook = new ItemDisplayElement(Items.TRIPWIRE_HOOK);

        this.hook.setOffset(ornament.offset());
        this.hook.setInterpolationDuration(1);
        this.hook.setInvisible(true);

        this.item = new ItemDisplayElement(ornament.item());

        this.item.setOffset(ornament.offset());
        this.item.setInterpolationDuration(1);
        this.item.setInvisible(true);

        this.interaction = new InteractionElement(this);

        this.interaction.setOffset(ornament.offset().subtract(0, 0.6, 0));
        this.interaction.setSize(WIDTH, HEIGHT);

        this.updateTransformations(true);
    }

    private void wobble(ServerPlayerEntity player, boolean careful) {
        this.wobbleTicks = 10;
        this.wobbleStrength = Math.min(this.wobbleStrength + 10, careful ? 20 : 60);

        var pos = this.blockEntity.getPos().toCenterPos().add(this.ornament.offset());
        float pitch = 1.3f + player.getRandom().nextFloat() * 0.2f;

        player.getWorld().playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_CHAIN_HIT, SoundCategory.BLOCKS, 0.5f, pitch);
    }

    @Override
    public void interact(ServerPlayerEntity player, Hand hand) {
        this.wobble(player, true);

        MinecraftServer server = this.blockEntity.getWorld().getServer();
        Text ownerName = this.ornament.getOwnerName(server);

        Block block = Block.getBlockFromItem(this.ornament.item());
        Text itemName = block.getName();

        player.sendMessage(Text.translatable("text.nucleoid_extras.ornament.details", itemName, ownerName), true);
    }

    @Override
    public void attack(ServerPlayerEntity player) {
        this.wobble(player, false);

        if (this.ornament.canBeRemovedBy(player) && this.wobbleStrength > 40) {
            this.blockEntity.removeOrnament(ornament);
        }
    }

    private long getTime() {
        World world = this.blockEntity.getWorld();

        return world == null ? 0 : world.getTime();
    }

    private void updateTransformations(boolean initial) {
        long time = this.getTime();
        float rotation;

        if (this.wobbleTicks > 0) {
            rotation = MathHelper.RADIANS_PER_DEGREE * MathHelper.sin(this.wobbleTicks) * this.wobbleTicks * this.wobbleStrength / 10.0f;
        } else {
            rotation = (float) Math.sin(time / 12f) * 0.04f;
        }

        float hookRotationY = -this.ornament.hookYaw() * MathHelper.RADIANS_PER_DEGREE + MathHelper.PI;
        float rotationY = -this.ornament.yaw() * MathHelper.RADIANS_PER_DEGREE + MathHelper.PI;

        var hookTransformation = new Matrix4f()
                .rotateZ(rotation)
                .rotateY(hookRotationY)
                .scale(0.5f)
                .translate(0, -4 / 32f, 0);

        this.hook.setTransformation(hookTransformation);

        var itemTransformation = new Matrix4f()
                .rotateZ(rotation)
                .rotateY(rotationY)
                .translate(0, -3 / 32f, 0);

        this.item.setTransformation(itemTransformation);

        if (!initial) {
            this.hook.startInterpolation();
            this.item.startInterpolation();
        }
    }

    public void tick() {
        if (this.wobbleTicks > 0) this.wobbleTicks -= 1;
        if (this.wobbleStrength > 0) this.wobbleStrength -= 1;

        this.updateTransformations(false);
    }

    public void addToHolder(ElementHolder holder) {
        holder.addElement(this.hook);
        holder.addElement(this.item);
        holder.addElement(this.interaction);
    }

    public void removeFromHolder(ElementHolder holder) {
        holder.removeElement(this.hook);
        holder.removeElement(this.item);
        holder.removeElement(this.interaction);
    }
}
