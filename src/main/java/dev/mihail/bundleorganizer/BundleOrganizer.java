package dev.mihail.bundleorganizer;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import dev.mihail.bundleorganizer.common.ServerConfig;
import dev.mihail.bundleorganizer.network.NetworkSetup;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

/**
 * Общая (серверная и клиентская) точка входа мода.
 * <p>
 * Здесь нет ни одного обращения к клиентским классам: клиентская часть живёт в
 * {@code dev.mihail.bundleorganizer.client} и загружается отдельным {@code @Mod(dist = Dist.CLIENT)}.
 */
@Mod(BundleOrganizer.MOD_ID)
public class BundleOrganizer {
    public static final String MOD_ID = "bundleorganizer";
    public static final Logger LOGGER = LogUtils.getLogger();

    public BundleOrganizer(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(NetworkSetup::register);
        modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
    }
}
