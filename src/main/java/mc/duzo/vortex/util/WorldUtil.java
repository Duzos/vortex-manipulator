package mc.duzo.vortex.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Utilities related to the world and the server
 * Some methods based off MDTeam's AIT TardisUtil
 *
 * @author duzo
 * @author MDTeam
 */
public class WorldUtil {
    private static MinecraftServer SERVER;

    public static void initialise() {
        ServerWorldEvents.UNLOAD.register((server, world) -> {
            if (world.getRegistryKey() == World.OVERWORLD) {
                SERVER = null;
            }
        });

        ServerWorldEvents.LOAD.register((server, world) -> {
            if (world.getRegistryKey() == World.OVERWORLD) {
                SERVER = server;
            }
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            SERVER = server;
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            SERVER = null;
        });
    }

    public static MinecraftServer getServer() {
        return SERVER;
    }

    public static ServerWorld findWorld(RegistryKey<World> key) {
        return WorldUtil.getServer().getWorld(key);
    }

    public static ServerWorld findWorld(Identifier identifier) {
        return WorldUtil.findWorld(RegistryKey.of(RegistryKeys.WORLD, identifier));
    }

    public static ServerWorld findWorld(String identifier) {
        return WorldUtil.findWorld(new Identifier(identifier));
    }
    public static void teleportToWorld(ServerPlayerEntity player, ServerWorld target, Vec3d pos, float yaw, float pitch) {
        player.teleport(target, pos.x, pos.y, pos.z, yaw, pitch);
        player.addExperience(0);

        player.getStatusEffects().forEach(effect -> {
            player.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(player.getId(), effect));
        });
        player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player));
    }
}
