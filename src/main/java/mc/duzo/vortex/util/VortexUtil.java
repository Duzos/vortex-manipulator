package mc.duzo.vortex.util;

import mc.duzo.vortex.item.VortexManipulatorItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Utilities regarding the {@link VortexManipulatorItem}
 */
public class VortexUtil {
    public static final int MAX_WAYPOINTS = 3;
    public static final String WAYPOINTS_KEY = "Waypoints";
    public static final String SELECTED_KEY = "Selected Waypoint";

    public static List<Waypoint> findWaypoints(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();

        if (!nbt.contains(WAYPOINTS_KEY)) return List.of();

        NbtList pointNbt = nbt.getList(WAYPOINTS_KEY, NbtElement.COMPOUND_TYPE);
        List<Waypoint> waypoints = new ArrayList<>();

        for (NbtElement i : pointNbt) {
            if (!(i instanceof NbtCompound point)) continue;

            waypoints.add(Waypoint.fromNbt(point));
        }

        return waypoints;
    }

    public static void addWaypoint(ItemStack stack, Waypoint waypoint) {
        NbtCompound nbt = stack.getOrCreateNbt();

        if(!nbt.contains(WAYPOINTS_KEY))
            nbt.put(WAYPOINTS_KEY, new NbtList());

        if (hasWaypoint(stack, waypoint)) return;

        if (findWaypoints(stack).size() + 1 > MAX_WAYPOINTS) {
            removeWaypoint(stack, findWaypoints(stack).get(0));
        }

        nbt.getList(WAYPOINTS_KEY, NbtElement.COMPOUND_TYPE).add(waypoint.toNbt());
    }

    public static void removeWaypoint(ItemStack stack, Waypoint waypoint) {
        NbtCompound nbt = stack.getOrCreateNbt();

        if(!nbt.contains(WAYPOINTS_KEY))
            return;

        NbtList pointNbt = nbt.getList(WAYPOINTS_KEY, NbtElement.COMPOUND_TYPE);
        pointNbt.remove(waypoint.toNbt());
    }

    public static boolean hasWaypoint(ItemStack stack, Waypoint waypoint) {
        List<Waypoint> waypoints = findWaypoints(stack);
        return waypoints.contains(waypoint);
    }

    public static int findPosition(ItemStack stack, Waypoint waypoint) {
        List<Waypoint> waypoints = findWaypoints(stack);
        return waypoints.indexOf(waypoint);
    }

    public static @Nullable Waypoint getSelectedWaypoint(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();

        if(!nbt.contains(SELECTED_KEY)) {
            List<Waypoint> waypoints = findWaypoints(stack);

            if(waypoints.isEmpty()) return null;

            setSelectedWaypoint(stack, waypoints.get(0));
        }

        return findWaypoints(stack).get(nbt.getInt(SELECTED_KEY));
    }
    public static boolean isSelectedWaypoint(ItemStack stack, Waypoint waypoint) {
        return Objects.equals(getSelectedWaypoint(stack), waypoint);
    }

    public static void setSelectedWaypoint(ItemStack stack, Waypoint waypoint) {
        NbtCompound nbt = stack.getOrCreateNbt();

        nbt.putInt(SELECTED_KEY, findPosition(stack, waypoint));
    }
    public static Waypoint getNextWaypoint(ItemStack stack, Waypoint current) {
        List<Waypoint> waypoints = findWaypoints(stack);
        int index = waypoints.indexOf(current);
        return index == waypoints.size() - 1 ? waypoints.get(0) : waypoints.get(index + 1);
    }

    /**
     * Finds the first vortex manipulator in a player inventory, or the one in their hand
     */
    public static Optional<ItemStack> findManipulator(PlayerEntity player) {
        if (player.getMainHandStack().getItem() instanceof VortexManipulatorItem) {
            return Optional.of(player.getMainHandStack());
        }
        if (player.getOffHandStack().getItem() instanceof VortexManipulatorItem) {
            return Optional.of(player.getOffHandStack());
        }

        ItemStack found = null;
        for (ItemStack stack : player.getInventory().main) {
            if (stack.getItem() instanceof VortexManipulatorItem) {
                found = stack;
                break;
            }
        }

        return Optional.ofNullable(found);
    }
    public static void teleportToWaypoint(ServerPlayerEntity player, Waypoint waypoint) {
        WorldUtil.teleportToWorld(player, (ServerWorld) waypoint.getWorld(), waypoint.toCenterPos(), waypoint.getDirection().asRotation(), player.getPitch());
    }
}
