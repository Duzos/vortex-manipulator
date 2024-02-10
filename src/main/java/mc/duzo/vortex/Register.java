package mc.duzo.vortex;

import mc.duzo.vortex.item.VortexManipulatorItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * This is where all things are registered
 * Based off how Bug registered things
 *
 * @author bug
 * @author duzo
 */
public class Register {
    // Items
    public static final VortexManipulatorItem MANIPULATOR = register(Registries.ITEM, "manipulator", new VortexManipulatorItem(new FabricItemSettings()));

    // Initialising & Registering

    public static void initialize() {}

    public static <V, T extends V> T register(Registry<V> registry, String name, T entry) {
        return Registry.register(registry, new Identifier(VortexMod.MOD_ID, name), entry);
    }

    public static <T extends Block> T registerBlockAndItem(String name, T entry) {
        T output = Register.register(Registries.BLOCK, name, entry);
        Registry.register(Registries.ITEM, new Identifier(VortexMod.MOD_ID, name), new BlockItem(output, new FabricItemSettings()));
        return output;
    }
}
