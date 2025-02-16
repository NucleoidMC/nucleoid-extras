package xyz.nucleoid.extras.lobby.block;

import com.google.common.collect.ImmutableList;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.elements.EntityElement;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import xyz.nucleoid.extras.lobby.contributor.ContributorData;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;

public class ContributorStatueModel extends ElementHolder {
    private static final List<EntityType<?>> SPOOKY_ENTITIES = ImmutableList.of(
        EntityType.ZOMBIE,
        EntityType.DROWNED,
        EntityType.HUSK,
        EntityType.SKELETON,
        EntityType.STRAY,
        EntityType.BOGGED
    );

    private EntityElement<?> entityElement;

    private EntityType<?> getEntityType(String contributorId) {
        LocalDate date = LocalDate.now();

        int month = date.get(ChronoField.MONTH_OF_YEAR);
        int day = date.get(ChronoField.DAY_OF_MONTH);

        if (month == 10 && day == 31) {
            int index = Math.floorMod(contributorId.hashCode(), SPOOKY_ENTITIES.size());
            return SPOOKY_ENTITIES.get(index);
        }

        return EntityType.ARMOR_STAND;
    }

    public void update(String contributorId, ServerWorld world, BlockState state) {
        this.removeElement(this.entityElement);

        var contributor = ContributorData.getContributor(contributorId);
        if (contributor == null) return;

        var entityType = this.getEntityType(contributorId);

        this.entityElement = new EntityElement<>(entityType, world);
        this.entityElement.setOffset(new Vec3d(0, 1, 0));

        var entity = this.entityElement.entity();
        contributor.fillEntity(world.getServer(), entity);

        entity.setYaw(entity.getYaw() + state.get(ContributorStatueBlock.FACING).getPositiveHorizontalDegrees());
        entity.setHeadYaw(entity.getYaw());
        entity.setBodyYaw(entity.getYaw());

        this.addElement(this.entityElement);
    }
}
