package xyz.nucleoid.extras.lobby.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class DiceTaterBlock extends TinyPotatoBlock {
    private static final int ROLLING_FACE = 0;
    private static final int MAX_FACE = 6;
    private static final int ROLLING_TICKS = 8;
    private static final IntProperty FACE = IntProperty.of("face", ROLLING_FACE, MAX_FACE);
    private static final String[] TEXTURES = {
        "ewogICJ0aW1lc3RhbXAiIDogMTYzOTMzNzEwMTMwNSwKICAicHJvZmlsZUlkIiA6ICJjNjc3MGJjZWMzZjE0ODA3ODc4MTU0NWRhMGFmMDI1NCIsCiAgInByb2ZpbGVOYW1lIiA6ICJDVUNGTDE2IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2I0ZDQxMjY1NzRjM2RjYjk4NDc1NDdmMjlmMDRlNWRmNmNmMGZjNmQ4NjJiNGFiZTc1OTI2ZjM1OWQ1ZDZhOTEiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
        "ewogICJ0aW1lc3RhbXAiIDogMTYzOTMzNjk2MzE5OCwKICAicHJvZmlsZUlkIiA6ICJjMGYzYjI3YTUwMDE0YzVhYjIxZDc5ZGRlMTAxZGZlMiIsCiAgInByb2ZpbGVOYW1lIiA6ICJDVUNGTDEzIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzU5YjU2OGUzZDRlYjUzMDllMzY2MGY0YWNmYzA0ZWNhYTg0ODI1ZDJmZTRhMTMxMjU5MWQxMjhiMjY4NTllYWYiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
        "ewogICJ0aW1lc3RhbXAiIDogMTYzOTMzNjkxNTMzMSwKICAicHJvZmlsZUlkIiA6ICI5MGQ1NDY0OGEzNWE0YmExYTI2Yjg1YTg4NTU4OGJlOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJFdW4wbWlhIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzc1M2UxOTYxZWJjMWRlNjJkZGIxMzE2ZGJhODQzNDhhN2FhOGMwN2MzMjJiOGY0ZThmNmY1OGY1NWNmMjgwNjAiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
        "ewogICJ0aW1lc3RhbXAiIDogMTYzOTMzNjg0MjM1OCwKICAicHJvZmlsZUlkIiA6ICIyMWFlMDM2OWJhMDM0NGFkOGY1ZjhlM2JlYTMwOTQ3MSIsCiAgInByb2ZpbGVOYW1lIiA6ICJWaXJhbF9BbmdlbCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81N2ViOGYwZTM2ZDk4ODE4Y2FlZTEwNjc2MjVkOGVhYTVkNzY2ZjUxZDBmN2ZhNzU1OTVhNDhmNDMwMzQ1OTg2IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
        "ewogICJ0aW1lc3RhbXAiIDogMTYzOTMzNzAwNDIyMiwKICAicHJvZmlsZUlkIiA6ICJjNTZlMjI0MmNiZWY0MWE2ODdlMzI2MGRjMGNmOTM2MSIsCiAgInByb2ZpbGVOYW1lIiA6ICJMSlI3MzEwMCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS82MTM3MzFkMjI0YmUxZmQxODRmZjc1OWE1ZTE3YTg5NmMzYzI2ZDM1MDU3YzZkOTk4OWYwN2FlN2ZmZTQ3MTIwIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
        "ewogICJ0aW1lc3RhbXAiIDogMTYzOTMzNzA1MTk4MywKICAicHJvZmlsZUlkIiA6ICIxYTc1ZTNiYmI1NTk0MTc2OTVjMmY4NTY1YzNlMDAzZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJUZXJvZmFyIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ExNGIwODA3OGJmOTdlODJiYjcwNTZiN2E0ZjgyMDYyNmE0MmZlNzIzOTVjYmJmMzNhMGVlYTcyYWYwZTdhMTIiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
        "ewogICJ0aW1lc3RhbXAiIDogMTYzOTMzNzAyMjk5MSwKICAicHJvZmlsZUlkIiA6ICJiNjc3NTgwYzExYmU0ZjNiODI1OGM0YjBkNzNhNzg0ZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJPZmZpY2lhbGx5SksiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWM0MGJmNzBmMTY0OGI3ZWU0MzhhNmEyMjkwNDIyOGFiNWZiYmQ0OTI2YWYzMGFlOGFkZTRkZjAxYjhkNzQxMyIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9",
    };

    public DiceTaterBlock(Settings settings) {
        super(settings, ParticleTypes.POOF, null);

        this.setDefaultState(this.stateManager.getDefaultState().with(FACE, 1));
    }

    private boolean isRolling(BlockState state) {
        return state.get(FACE) == ROLLING_FACE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (hand == Hand.OFF_HAND || this.isRolling(state)) {
            return ActionResult.FAIL;
        }

        if (world instanceof ServerWorld) {
            world.setBlockState(pos, state.with(FACE, ROLLING_FACE));
            world.getBlockTickScheduler().schedule(pos, this, ROLLING_TICKS);

            float pitch = 1.6f + world.getRandom().nextFloat() * 0.4f;
            world.playSound(null, pos, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundCategory.BLOCKS, 1, pitch);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (this.isRolling(state)) {
            int face = world.getRandom().nextInt(MAX_FACE) + 1;
            world.setBlockState(pos, state.with(FACE, face));
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        int face = state.get(FACE);
        return MathHelper.floor(face / 6f * 15f);
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACE);
    }

    @Override
    public String getVirtualHeadSkin(BlockState state) {
        int face = state.get(FACE);
        return TEXTURES[face];
    }
}
