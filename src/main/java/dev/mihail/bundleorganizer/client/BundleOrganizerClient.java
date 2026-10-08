package dev.mihail.bundleorganizer.client;

import dev.mihail.bundleorganizer.BundleOrganizer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/**
 * Клиентская точка входа. Этот класс не загружается на выделенном сервере,
 * поэтому здесь безопасно обращаться к клиентским классам.
 */
@Mod(value = BundleOrganizer.MOD_ID, dist = Dist.CLIENT)
public class BundleOrganizerClient {
    public BundleOrganizerClient(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        // Экран настроек в списке модов строится автоматически по ModConfigSpec.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
