package mc.duzo.vortex.item;

import mc.duzo.vortex.util.VortexMessages;
import mc.duzo.vortex.util.VortexUtil;
import mc.duzo.vortex.util.Waypoint;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VortexManipulatorItem extends Item {
    public VortexManipulatorItem(Settings settings) {
        super(settings.maxCount(1));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient()) {
            if (VortexUtil.validateWaypoints((ServerPlayerEntity) user, stack)) return TypedActionResult.pass(stack);

            if (user.isSneaking()) {
                VortexMessages.sendOpenScreen((ServerPlayerEntity) user, 0);
                return TypedActionResult.success(stack);
            }

            Waypoint current = VortexUtil.getSelectedWaypoint(stack);
            if (current == null) return TypedActionResult.fail(stack);

            Waypoint next = VortexUtil.getNextWaypoint(stack, current);
            VortexUtil.setSelectedWaypoint(stack, next);

            user.sendMessage(Text.literal(next.name()).formatted(Formatting.AQUA), true);

            world.playSound(null, user.getBlockPos(), SoundEvents.BLOCK_NOTE_BLOCK_BELL.value(), SoundCategory.AMBIENT, 1, 2);

            user.getItemCooldownManager().set(stack.getItem(), 1 * 20);

            return TypedActionResult.success(stack);
        }

        return super.use(world, user, hand);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        List<Waypoint> waypoints = VortexUtil.findWaypoints(stack);

        Waypoint selected = VortexUtil.getSelectedWaypoint(stack);
        int start = (selected == null) ? 0 : waypoints.indexOf(selected);
        int size = waypoints.size();

        if (start < 3) {
            waypoints = waypoints.subList(0, Math.min(3, size));
        } else if (start >= size - 3) {
            waypoints = waypoints.subList(Math.max(0, size - 3), size);
        } else {
            waypoints = waypoints.subList(start - 1, Math.min(start + 2, size));
        }

        for (Waypoint point : waypoints) {
            Formatting format = (VortexUtil.isSelectedWaypoint(stack, point)) ? Formatting.AQUA : Formatting.BLUE;
            tooltip.add(Text.literal("> " + ((point.hasName()) ? point.name() : point.toString())).formatted(format));
        }

        super.appendTooltip(stack, world, tooltip, context);
    }
}
