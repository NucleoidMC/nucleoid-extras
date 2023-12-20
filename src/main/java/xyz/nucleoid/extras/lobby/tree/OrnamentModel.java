package xyz.nucleoid.extras.lobby.tree;

import org.joml.Matrix4f;

import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;

public final class OrnamentModel {
    private final Ornament ornament;

    private final ItemDisplayElement hook;
    private final ItemDisplayElement item;

    public OrnamentModel(Ornament ornament, long time) {
        this.ornament = ornament;

        this.hook = new ItemDisplayElement(Items.TRIPWIRE_HOOK);

        this.hook.setOffset(ornament.offset());
        this.hook.setInterpolationDuration(1);

        this.item = new ItemDisplayElement(ornament.item());

        this.item.setOffset(ornament.offset());
        this.item.setInterpolationDuration(1);

        this.updateTransformations(time, true);
    }

    public void updateTransformations(long time, boolean initial) {
        float rotationX = (float) Math.sin(time / 12f) * 0.04f;

        float hookRotationY = -this.ornament.hookYaw() * MathHelper.RADIANS_PER_DEGREE + MathHelper.PI;
        float rotationY = -this.ornament.yaw() * MathHelper.RADIANS_PER_DEGREE + MathHelper.PI;

        Matrix4f hookTransformation = new Matrix4f()
                .rotateZ(rotationX)
                .rotateY(hookRotationY)
                .scale(0.5f)
                .translate(0, -4 / 32f, 0);

        this.hook.setTransformation(hookTransformation);

        Matrix4f itemTransformation = new Matrix4f()
                .rotateZ(rotationX)
                .rotateY(rotationY)
                .translate(0, -3 / 32f, 0);

        this.item.setTransformation(itemTransformation);

        if (!initial) {
            this.hook.startInterpolation();
            this.item.startInterpolation();
        }
    }

    public void addToHolder(ElementHolder holder) {
        holder.addElement(this.hook);
        holder.addElement(this.item);
    }

    public void removeFromHolder(ElementHolder holder) {
        holder.removeElement(this.hook);
        holder.removeElement(this.item);
    }
}
