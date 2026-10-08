package dev.mihail.bundleorganizer.network;

import dev.mihail.bundleorganizer.common.BundleData;
import dev.mihail.bundleorganizer.common.ServerConfig;
import dev.mihail.bundleorganizer.core.ItemCategory;
import dev.mihail.bundleorganizer.core.SortMode;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Серверная обработка запроса на сортировку. Вся логика выполняется только на сервере,
 * клиенту доверять нельзя, поэтому проверяется каждое поле пакета.
 */
public final class ServerSortHandler {
    private ServerSortHandler() {
    }

    public static void handle(SortBundlePayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }

        if (!ServerConfig.ALLOW_SORTING.getAsBoolean()) {
            player.sendOverlayMessage(Component.translatable("message.bundleorganizer.sorting_disabled"));
            return;
        }

        AbstractContainerMenu menu = player.containerMenu;
        // Меню могло закрыться или смениться, пока пакет шёл до сервера.
        if (menu.containerId != payload.containerId()) {
            return;
        }

        int index = payload.slotIndex();
        if (index < 0 || index >= menu.slots.size()) {
            return;
        }

        Slot slot = menu.getSlot(index);
        // Нельзя трогать слоты, из которых игрок не может забрать предмет (например, слот результата).
        if (!slot.mayPickup(player)) {
            return;
        }

        ItemStack stack = slot.getItem();
        // Стак из нескольких мешков ванильная логика мешка тоже не обрабатывает.
        if (stack.getCount() != 1 || !BundleData.isBundle(stack)) {
            return;
        }

        SortMode mode = SortMode.byOrdinal(payload.sortMode());
        ItemCategory priority = ItemCategory.decodeFilter(payload.filter());

        if (BundleData.sortInPlace(stack, mode, priority)) {
            slot.setChanged();
            menu.broadcastChanges();
        }
    }
}
