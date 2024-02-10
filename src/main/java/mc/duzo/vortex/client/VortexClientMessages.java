package mc.duzo.vortex.client;

import mc.duzo.vortex.client.screen.CreateWaypointScreen;
import mc.duzo.vortex.client.screen.ReplaceWaypointScreen;
import mc.duzo.vortex.util.VortexMessages;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;

public class VortexClientMessages {
    public static void initialise() {
        ClientPlayNetworking.registerGlobalReceiver(VortexMessages.OPEN_SCREEN, ((client, handler, buf, responseSender) -> recieveOpenScreen(buf)));
    }

    public static void sendTeleportRequest() {
        ClientPlayNetworking.send(VortexMessages.REQUEST_TELEPORT, PacketByteBufs.create());
    }

    private static void recieveOpenScreen(PacketByteBuf buf) {
        recieveOpenScreen(buf.readInt());
    }
    private static void recieveOpenScreen(int id) {
        Screen screen = findScreen(id);
        if (screen == null) return;
        MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(screen));
    }

    private static Screen findScreen(int id) {
        return switch (id) {
            default -> null;
            case 0 -> new CreateWaypointScreen();
            case 1 -> new ReplaceWaypointScreen();
        };
    }

    public static void sendWaypointName(String name) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(name);
        ClientPlayNetworking.send(VortexMessages.WAYPOINT_NAME, buf);
    }

    public static void sendReplaceWaypoint(String name) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(name);
        ClientPlayNetworking.send(VortexMessages.REPLACE_WAYPOINT, buf);
    }
}
