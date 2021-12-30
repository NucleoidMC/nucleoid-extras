package xyz.nucleoid.extras.lobby.block;

import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import xyz.nucleoid.extras.lobby.NEBlocks;
import xyz.nucleoid.extras.mixin.BlockWithEntityAccessor;

import java.util.Locale;

public class TateroidBlock extends TinyPotatoBlock implements BlockEntityProvider {
    private static final BooleanProperty POWERED = Properties.POWERED;
    private static final EnumProperty<Color> COLOR = EnumProperty.of("color", Color.class);
    private static final int FULL_DURATION = 15 * SharedConstants.TICKS_PER_SECOND;

    private final SoundEvent defaultSound;

    public TateroidBlock(Settings settings, SoundEvent defaultSound) {
        super(settings, ParticleTypes.NOTE, null);
        this.defaultSound = defaultSound;

        this.setDefaultState(this.stateManager.getDefaultState().with(POWERED, false).with(COLOR, Color.DEFAULT));
    }

    private void activate(World world, BlockPos pos, int duration) {
        var optional = world.getBlockEntity(pos, NEBlocks.TATEROID_ENTITY);
        if (optional.isPresent()) {
            var blockEntity = optional.get();
            blockEntity.setDuration(duration);
        }
    }

    public SoundEvent getDefaultSound() {
        return this.defaultSound;
    }

    private int getDurationFromPower(int power) {
        if (power == 1) {
            return -1;
        }

        return (int) (FULL_DURATION * (power / (float) Properties.LEVEL_15_MAX));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ActionResult result = super.onUse(state, world, pos, player, hand, hit);
        if (result.isAccepted() && !world.isClient()) {
            this.activate(world, pos, FULL_DURATION);
        }

        return result;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (!world.isClient()) {
            int power = world.getReceivedRedstonePower(pos);
            boolean powered = power > 0;

            if (powered != state.get(POWERED)) {
                if (powered) {
                    this.activate(world, pos, this.getDurationFromPower(power));
                }

                world.setBlockState(pos, state.with(POWERED, powered), Block.NOTIFY_ALL);
            }
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        var optional = world.getBlockEntity(pos, NEBlocks.TATEROID_ENTITY);
        if (optional.isPresent()) {
            int duration = optional.get().getDuration();
            float power = (duration / (float) FULL_DURATION) * Properties.LEVEL_15_MAX;

            return (int) MathHelper.clamp(power, 0, Properties.LEVEL_15_MAX);
        }

        return 0;
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(POWERED);
        builder.add(COLOR);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TateroidBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient() ? null : BlockWithEntityAccessor.checkType(type, NEBlocks.TATEROID_ENTITY, TateroidBlockEntity::serverTick);
    }

    @Override
    public String getPolymerSkinValue(BlockState state) {
        return state.get(COLOR).texture;
    }

    public enum Color implements StringIdentifiable {
        DEFAULT("ewogICJ0aW1lc3RhbXAiIDogMTYzNDQ5MjYzNjAzNiwKICAicHJvZmlsZUlkIiA6ICI2MDBjMDE4YmM4ZmM0NGQ1YWJkYjUyODc5ZGUyY2Q0MSIsCiAgInByb2ZpbGVOYW1lIiA6ICJzb2NrZnJpZW5kcyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84ZDUzMWQ0MGQwOWVmZDNhOWE1ODViNTVlNjZhOWE2ZjA0YzczYWY4NGQ5NGQ3YzU2NTU0OWJmMjdiOGIyNmJkIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0="),
        RED("ewogICJ0aW1lc3RhbXAiIDogMTYzNDU2OTEyODY5NywKICAicHJvZmlsZUlkIiA6ICI2MDBjMDE4YmM4ZmM0NGQ1YWJkYjUyODc5ZGUyY2Q0MSIsCiAgInByb2ZpbGVOYW1lIiA6ICJzb2NrZnJpZW5kcyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yYmU1MWIyMjczNjBhYjY1Nzc2NzI1YTkxY2RlZDg0YjU2ZjY5MjBlZWMwZDZmYjVhNTdkNWYxYWRhMTQ3YWE2IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0="),
        ORANGE("ewogICJ0aW1lc3RhbXAiIDogMTYzNDU2ODAyNDE5NiwKICAicHJvZmlsZUlkIiA6ICI2MDBjMDE4YmM4ZmM0NGQ1YWJkYjUyODc5ZGUyY2Q0MSIsCiAgInByb2ZpbGVOYW1lIiA6ICJzb2NrZnJpZW5kcyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jNTM2MmUzMDg4MjJjZjFjNDM2YTRiYTZkMGMzOTc2MTM5Yzk4NjIxYzdhYTJhOTZiZTk5YzczZTk3NzA4ZWZjIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0="),
        YELLOW("ewogICJ0aW1lc3RhbXAiIDogMTYzNDU2OTUwNTAwNywKICAicHJvZmlsZUlkIiA6ICI2MDBjMDE4YmM4ZmM0NGQ1YWJkYjUyODc5ZGUyY2Q0MSIsCiAgInByb2ZpbGVOYW1lIiA6ICJzb2NrZnJpZW5kcyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9mZWY3NGE2YzdjYjQ1ZDNjNGJhZTEzNGU2ZWM0MWZkNzUxN2Y3ZWFiZTJjNzRkYzc2YTUxYjM5YzYzYzM4YmMyIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0="),
        GREEN("ewogICJ0aW1lc3RhbXAiIDogMTYzNDU2ODA2NjM4OCwKICAicHJvZmlsZUlkIiA6ICI2MDBjMDE4YmM4ZmM0NGQ1YWJkYjUyODc5ZGUyY2Q0MSIsCiAgInByb2ZpbGVOYW1lIiA6ICJzb2NrZnJpZW5kcyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81N2JiNjkyNDk5NTYwZjAzOTMzMTRhOWYxZWMxMTQyNWIzNjBlNDNjMWRkYjU2MGRlMjYxY2QwNGI4Y2M4ZTY5IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0="),
        BLUE("ewogICJ0aW1lc3RhbXAiIDogMTYzNDU2Nzk1MDEyMCwKICAicHJvZmlsZUlkIiA6ICI2MDBjMDE4YmM4ZmM0NGQ1YWJkYjUyODc5ZGUyY2Q0MSIsCiAgInByb2ZpbGVOYW1lIiA6ICJzb2NrZnJpZW5kcyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84OWFkNWFlY2ZiOWFiNmYzNjI2MWUwYzQ2MmFjZWNmMjA3OGU3ZTU3NWQ5MzczYmFjYzA1MDMyMjRjNDQyNTBlIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0="),
        PURPLE("ewogICJ0aW1lc3RhbXAiIDogMTYzNDU3NjYzMTAwMiwKICAicHJvZmlsZUlkIiA6ICI2MDBjMDE4YmM4ZmM0NGQ1YWJkYjUyODc5ZGUyY2Q0MSIsCiAgInByb2ZpbGVOYW1lIiA6ICJzb2NrZnJpZW5kcyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9kMTZhMzc1MTJjYjdjYTM3MmFmNWYzN2Y5YmQ5NWQ0NjAzYzRmYTQ0YmU0MTQzZmIyNmFhYTMyNGU2ODFjOWIwIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=");

        private final String texture;

        Color(String texture) {
            this.texture = texture;
        }

        @Override
        public String asString() {
            return this.toString().toLowerCase(Locale.ROOT);
        }
    }
}
