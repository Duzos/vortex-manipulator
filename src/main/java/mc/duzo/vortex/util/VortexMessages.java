package mc.duzo.vortex.util;

import mc.duzo.vortex.VortexMod;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class VortexMessages {
    public static final Identifier REQUEST_TELEPORT = createIdentifier("request_teleport");
    public static final Identifier OPEN_SCREEN = createIdentifier("open_screen");
    public static final Identifier WAYPOINT_NAME = createIdentifier("waypoint_name");
    public static final Identifier REPLACE_WAYPOINT = createIdentifier("replace_waypoint");

    public static void initialise() {
        ServerPlayNetworking.registerGlobalReceiver(REQUEST_TELEPORT, ((server, player, handler, buf, responseSender) -> recieveTeleportRequest(player)));
        ServerPlayNetworking.registerGlobalReceiver(WAYPOINT_NAME, ((server, player, handler, buf, responseSender) -> recieveWaypointName(player, buf)));
        ServerPlayNetworking.registerGlobalReceiver(REPLACE_WAYPOINT, ((server, player, handler, buf, responseSender) -> recieveReplaceWaypoint(player, buf)));
    }

    private static Identifier createIdentifier(String id) {
        return new Identifier(VortexMod.MOD_ID, id);
    }

    // I know its spelt wrong, but its the better way.
    private static void recieveTeleportRequest(ServerPlayerEntity player) {
        Optional<ItemStack> found = VortexUtil.findManipulator(player);

        if (found.isEmpty()) return;

        if (player.getItemCooldownManager().isCoolingDown(found.get().getItem())) return;

        Waypoint selected = VortexUtil.getSelectedWaypoint(found.get());
        if (selected == null) return;

        // todo organise

        VortexUtil.createTeleportEffect(player.getServerWorld(), player.getPos());
        player.getServerWorld().playSound(null, BlockPos.ofFloored(player.getPos()), SoundEvents.BLOCK_PORTAL_TRIGGER, SoundCategory.PLAYERS, 1.0f, 1.0f);

        VortexUtil.teleportToWaypoint(player, selected);

        Vec3d dest = selected.toCenterPos().subtract(0, 0.5, 0);
        VortexUtil.createTeleportEffect((ServerWorld) selected.getWorld(), dest);
        selected.getWorld().playSound(null, BlockPos.ofFloored(dest), SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.PLAYERS, 0.5f, 1.0f);

        player.getItemCooldownManager().set(found.get().getItem(), 16 * 20);
        found.get().damage(1, player, p -> p.sendToolBreakStatus(player.getActiveHand()));
    }

    public static void sendOpenScreen(ServerPlayerEntity player, int id) {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeInt(id);

        ServerPlayNetworking.send(player, OPEN_SCREEN, buf);
    }

    private static void recieveWaypointName(ServerPlayerEntity player, PacketByteBuf buf) {
        recieveWaypointName(player, buf.readString());
    }
    private static void recieveWaypointName(ServerPlayerEntity player, String name) {
        Optional<ItemStack> found = VortexUtil.findManipulator(player);

        if (found.isEmpty()) return;

        VortexUtil.addWaypoint(found.get(), Waypoint.fromEntity(player).setName(name));

        player.getServerWorld().playSound(null, player.getBlockPos(), SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value(), SoundCategory.AMBIENT, 1, 1);
        player.getItemCooldownManager().set(found.get().getItem(), 2 * 20);
    }

    private static void recieveReplaceWaypoint(ServerPlayerEntity player, PacketByteBuf buf) {
        recieveReplaceWaypoint(player, buf.readString());
    }
    private static void recieveReplaceWaypoint(ServerPlayerEntity player, String name) {
        Optional<ItemStack> found = VortexUtil.findManipulator(player);

        if (found.isEmpty()) return;

        Waypoint removed = VortexUtil.findByName(found.get(), name);

        if (removed == null) return;

        VortexUtil.removeWaypoint(found.get(), removed);
    }
}
