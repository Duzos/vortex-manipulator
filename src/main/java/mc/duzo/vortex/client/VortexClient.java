package mc.duzo.vortex.client;

import mc.duzo.vortex.VortexMod;
import mc.duzo.vortex.util.VortexMessages;
import mc.duzo.vortex.util.VortexUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import org.lwjgl.glfw.GLFW;

public class VortexClient implements ClientModInitializer {
    private static KeyBinding teleportKey;
    private static boolean wasTeleportHeld;

    @Override
    public void onInitializeClient() {
        setupKeybinds();

        VortexClientMessages.initialise();
    }

    private void setupKeybinds() {
        teleportKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key." + VortexMod.MOD_ID + ".teleport",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                "category." + VortexMod.MOD_ID
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity player = client.player;
            if (player == null) return;

            tickTeleportKey(player);
        });
    }

    private void tickTeleportKey(ClientPlayerEntity player) {
        if (!teleportKey.wasPressed() && wasTeleportHeld) {
            wasTeleportHeld = false;
            return;
        }

        if (wasTeleportHeld || !teleportKey.isPressed()) return;

        wasTeleportHeld = true; // Does not appear to work

        if (VortexUtil.findManipulator(player).isEmpty()) return;

        VortexClientMessages.sendTeleportRequest();
    }
}
