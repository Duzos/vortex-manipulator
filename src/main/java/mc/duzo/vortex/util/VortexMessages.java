package mc.duzo.vortex.util;

import mc.duzo.vortex.VortexMod;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class VortexMessages {
    public static final Identifier REQUEST_TELEPORT = new Identifier(VortexMod.MOD_ID, "request_teleport");

    public static void initialise() {
        ServerPlayNetworking.registerGlobalReceiver(REQUEST_TELEPORT, ((server, player, handler, buf, responseSender) -> recieveTeleportRequest(player)));
    }

    // I know its spelt wrong, but its the better way.
    private static void recieveTeleportRequest(ServerPlayerEntity player) {
        Optional<ItemStack> found = VortexUtil.findManipulator(player);

        if (found.isEmpty()) return;

        Waypoint selected = VortexUtil.getSelectedWaypoint(found.get());
        if (selected == null) return;

        VortexUtil.teleportToWaypoint(player, selected);
    }
}
