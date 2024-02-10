package mc.duzo.vortex;

import mc.duzo.vortex.util.VortexMessages;
import mc.duzo.vortex.util.WorldUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VortexMod implements ModInitializer {
	public static final String MOD_ID = "vortex";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		Register.initialize();
		VortexMessages.initialise();
		WorldUtil.initialise();
	}
}