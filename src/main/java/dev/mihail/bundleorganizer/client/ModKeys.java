package dev.mihail.bundleorganizer.client;

import org.lwjgl.glfw.GLFW;

import dev.mihail.bundleorganizer.BundleOrganizer;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

/**
 * Клавиши мода. Работают в открытом инвентаре или контейнере, пока курсор наведён на мешок.
 */
public final class ModKeys {
    public static final KeyMapping.Category CATEGORY =
            new KeyMapping.Category(Identifier.fromNamespaceAndPath(BundleOrganizer.MOD_ID, "main"));

    /** Отсортировать мешок под курсором. С Shift переключает режим сортировки. */
    public static final KeyMapping SORT = new KeyMapping("key.bundleorganizer.sort", GLFW.GLFW_KEY_G, CATEGORY);

    /** Переключить фильтр категорий. С Shift сбрасывает фильтр. */
    public static final KeyMapping FILTER = new KeyMapping("key.bundleorganizer.filter", GLFW.GLFW_KEY_H, CATEGORY);

    private ModKeys() {
    }
}
