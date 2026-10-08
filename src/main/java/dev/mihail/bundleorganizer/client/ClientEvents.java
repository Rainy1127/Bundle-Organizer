package dev.mihail.bundleorganizer.client;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

import dev.mihail.bundleorganizer.BundleOrganizer;
import dev.mihail.bundleorganizer.common.BundleData;
import dev.mihail.bundleorganizer.core.BundleFill;
import dev.mihail.bundleorganizer.core.ItemCategory;
import dev.mihail.bundleorganizer.core.SortMode;
import dev.mihail.bundleorganizer.network.SortBundlePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

/**
 * Все клиентские обработчики событий мода. Подписки на события игровой шины и шины мода
 * распознаются автоматически по типу события.
 */
@EventBusSubscriber(modid = BundleOrganizer.MOD_ID, value = Dist.CLIENT)
public final class ClientEvents {
    private ClientEvents() {
    }

    // ---------------------------------------------------------------- регистрация

    @SubscribeEvent
    static void registerKeys(RegisterKeyMappingsEvent event) {
        event.registerCategory(ModKeys.CATEGORY);
        event.register(ModKeys.SORT);
        event.register(ModKeys.FILTER);
    }

    @SubscribeEvent
    static void registerDecorations(RegisterItemDecorationsEvent event) {
        // Все мешки: ванильные (в том числе цветные) и мешки из модов, наследующие BundleItem.
        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof BundleItem) {
                event.register(item, SlotIndicator.INSTANCE);
            }
        }
    }

    // ---------------------------------------------------------------- подсказка предмета

    @SubscribeEvent
    static void onTooltip(ItemTooltipEvent event) {
        if (!ClientConfig.TOOLTIP_LINE.getAsBoolean()) {
            return;
        }
        BundleContents contents = event.getItemStack().get(DataComponents.BUNDLE_CONTENTS);
        if (contents == null) {
            return;
        }

        BundleData.Fill fill = BundleData.fillOf(contents);
        int rgb = fill.level().argb() & 0xFFFFFF;
        event.getToolTip().add(Component.translatable("tooltip.bundleorganizer.free", fill.freeUnits(), BundleFill.UNITS)
                .withStyle(style -> style.withColor(rgb)));
    }

    // ---------------------------------------------------------------- панель

    @SubscribeEvent
    static void onRenderForeground(ScreenEvent.Render.Foreground event) {
        if (!ClientConfig.SHOW_PANEL.getAsBoolean()) {
            return;
        }
        if (!(event.getScreen() instanceof AbstractContainerScreen<?> screen)) {
            return;
        }
        Slot hovered = screen.getHoveredSlot();
        if (hovered == null) {
            return;
        }
        ItemStack stack = hovered.getItem();
        if (!BundleData.isBundle(stack)) {
            return;
        }

        BundlePanel.render(event.getGuiGraphics(), Minecraft.getInstance().font, screen, stack, canSort(screen));
    }

    // ---------------------------------------------------------------- клавиши

    @SubscribeEvent
    static void onKeyPressed(ScreenEvent.KeyPressed.Pre event) {
        Screen current = event.getScreen();
        if (!(current instanceof AbstractContainerScreen<?> screen)) {
            return;
        }
        // Пока игрок печатает в текстовом поле (например, в поиске), клавиши мода не перехватываем.
        if (screen.getFocused() instanceof EditBox) {
            return;
        }
        Slot hovered = screen.getHoveredSlot();
        if (hovered == null || !BundleData.isBundle(hovered.getItem())) {
            return;
        }

        var key = InputConstants.getKey(event.getKeyEvent());
        boolean shift = (event.getModifiers() & GLFW.GLFW_MOD_SHIFT) != 0;

        if (ModKeys.SORT.isActiveAndMatches(key)) {
            onSortKey(screen, hovered, shift);
            event.setCanceled(true);
        } else if (ModKeys.FILTER.isActiveAndMatches(key)) {
            onFilterKey(shift);
            event.setCanceled(true);
        }
    }

    private static void onSortKey(AbstractContainerScreen<?> screen, Slot slot, boolean shift) {
        if (shift) {
            SortMode next = ClientConfig.SORT_MODE.get().next();
            ClientConfig.SORT_MODE.set(next);
            ClientConfig.SORT_MODE.save();
            overlay(Component.translatable("message.bundleorganizer.sort_mode", Component.translatable(next.langKey())));
            return;
        }

        if (screen instanceof CreativeModeInventoryScreen) {
            // В творческом инвентаре предметы клиентские: сервер их не видит, сортировать нечего.
            overlay(Component.translatable("message.bundleorganizer.creative"));
            return;
        }

        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection == null || !connection.hasChannel(SortBundlePayload.TYPE)) {
            overlay(Component.translatable("message.bundleorganizer.no_server"));
            return;
        }

        SortMode mode = ClientConfig.SORT_MODE.get();
        ClientPacketDistributor.sendToServer(new SortBundlePayload(
                screen.getMenu().containerId,
                slot.index,
                mode.ordinal(),
                ItemCategory.encodeFilter(ClientState.filter())));
        overlay(Component.translatable("message.bundleorganizer.sorted", Component.translatable(mode.langKey())));
    }

    private static void onFilterKey(boolean shift) {
        ItemCategory filter;
        if (shift) {
            ClientState.setFilter(null);
            filter = null;
        } else {
            filter = ClientState.cycleFilter();
        }
        Component name = filter == null ? Component.translatable("gui.bundleorganizer.filter_all") : Component.translatable(filter.langKey());
        overlay(Component.translatable("message.bundleorganizer.filter", name));
    }

    // ---------------------------------------------------------------- вспомогательное

    /** Можно ли отправить запрос на сортировку из этого экрана. */
    private static boolean canSort(AbstractContainerScreen<?> screen) {
        if (screen instanceof CreativeModeInventoryScreen) {
            return false;
        }
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        return connection != null && connection.hasChannel(SortBundlePayload.TYPE);
    }

    private static void overlay(Component message) {
        Minecraft.getInstance().gui.setOverlayMessage(message, false);
    }
}
