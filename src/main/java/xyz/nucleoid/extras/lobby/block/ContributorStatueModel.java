package xyz.nucleoid.extras.lobby.block;

import com.google.common.collect.ImmutableList;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.elements.EntityElement;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
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

    public void update(String contributorId, ServerLevel world, BlockState state) {
        this.removeElement(this.entityElement);

        var contributor = ContributorData.getContributor(contributorId);
        if (contributor == null) return;

        var entityType = this.getEntityType(contributorId);

        this.entityElement = new EntityElement<>(entityType, world);
        this.entityElement.setOffset(new Vec3(0, 1, 0));

        var entity = this.entityElement.entity();
        contributor.fillEntity(world.getServer(), entity);

        entity.setYRot(entity.getYRot() + state.getValue(ContributorStatueBlock.FACING).toYRot());
        entity.setYHeadRot(entity.getYRot());
        entity.setYBodyRot(entity.getYRot());

        this.addElement(this.entityElement);
    }
}
