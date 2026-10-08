package dev.mihail.bundleorganizer.client;

import dev.mihail.bundleorganizer.core.ItemCategory;

/**
 * Состояние клиента на время игровой сессии: выбранный фильтр категорий.
 * {@code null} означает «все категории».
 */
public final class ClientState {
    private static ItemCategory filter;

    private ClientState() {
    }

    /** Текущий фильтр или {@code null}, если показываются все категории. */
    public static ItemCategory filter() {
        return filter;
    }

    public static void setFilter(ItemCategory newFilter) {
        filter = newFilter;
    }

    /** Переключает фильтр на следующую категорию и возвращает новое значение. */
    public static ItemCategory cycleFilter() {
        filter = ItemCategory.nextFilter(filter);
        return filter;
    }

    /** Подходит ли категория под текущий фильтр. */
    public static boolean matches(ItemCategory category) {
        return filter == null || filter == category;
    }
}
