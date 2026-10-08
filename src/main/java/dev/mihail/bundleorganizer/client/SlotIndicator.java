package dev.mihail.bundleorganizer.client;

import dev.mihail.bundleorganizer.common.BundleData;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.neoforged.neoforge.client.IItemDecorator;

/**
 * Рисует на иконке мешка число свободных юнитов вместимости (64 = пустой, 0 = полный).
 * Цвет числа показывает, насколько мешок заполнен. Работает везде, где рисуются предметы:
 * в инвентаре, контейнерах и на панели быстрого доступа.
 */
public final class SlotIndicator implements IItemDecorator {
    public static final SlotIndicator INSTANCE = new SlotIndicator();

    private SlotIndicator() {
    }

    @Override
    public boolean render(GuiGraphicsExtractor graphics, Font font, ItemStack stack, int xOffset, int yOffset) {
        if (!ClientConfig.SLOT_INDICATOR.getAsBoolean()) {
            return false;
        }
        BundleContents contents = stack.get(DataComponents.BUNDLE_CONTENTS);
        // У пустого мешка число не рисуем: оно всегда было бы равно 64 и только загромождало бы слот.
        if (contents == null || contents.items().isEmpty()) {
            return false;
        }

        BundleData.Fill fill = BundleData.fillOf(contents);
        String text = Integer.toString(fill.freeUnits());
        int x = xOffset + 17 - font.width(text);
        int y = yOffset + 9;

        // Тёмная тень, чтобы число читалось на любом фоне иконки.
        graphics.text(font, text, x + 1, y + 1, 0xFF000000);
        graphics.text(font, text, x, y, fill.level().argb());
        return false;
    }
}
