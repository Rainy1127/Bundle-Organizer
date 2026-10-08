package dev.mihail.bundleorganizer.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/**
 * Сортировка записей мешка. Не зависит от Minecraft: работает с любыми объектами,
 * для которых можно получить {@link SortInfo}.
 * <p>
 * Результат всегда детерминирован: при равенстве всех ключей сохраняется исходный порядок.
 */
public final class BundleSorter {
    private BundleSorter() {
    }

    /**
     * Возвращает новый отсортированный список, исходный не изменяется.
     *
     * @param items    записи мешка
     * @param info     извлечение данных для сортировки из записи
     * @param mode     режим сортировки
     * @param priority категория, которая должна идти первой в любом режиме; {@code null} если не нужна
     */
    public static <T> List<T> sort(List<T> items, Function<T, SortInfo> info, SortMode mode, ItemCategory priority) {
        List<Entry<T>> entries = new ArrayList<>(items.size());
        for (int i = 0; i < items.size(); i++) {
            T item = items.get(i);
            entries.add(new Entry<>(item, info.apply(item), i));
        }

        entries.sort(comparator(mode, priority));

        List<T> result = new ArrayList<>(entries.size());
        for (Entry<T> entry : entries) {
            result.add(entry.item());
        }
        return result;
    }

    private static <T> Comparator<Entry<T>> comparator(SortMode mode, ItemCategory priority) {
        Comparator<Entry<T>> byPriority = Comparator.comparingInt(e -> priority != null && e.info().category() == priority ? 0 : 1);
        Comparator<Entry<T>> byCategory = Comparator.comparingInt(e -> e.info().category().ordinal());
        Comparator<Entry<T>> byId = Comparator
                .<Entry<T>, String>comparing(e -> e.info().path())
                .thenComparing(e -> e.info().namespace());
        Comparator<Entry<T>> byCountDesc = Comparator.comparingInt((Entry<T> e) -> e.info().count()).reversed();
        Comparator<Entry<T>> byOriginalIndex = Comparator.comparingInt(Entry::index);

        Comparator<Entry<T>> main = switch (mode) {
            case CATEGORY -> byCategory.thenComparing(byId).thenComparing(byCountDesc);
            case NAME -> byId.thenComparing(byCountDesc);
            case COUNT -> byCountDesc.thenComparing(byCategory).thenComparing(byId);
        };
        return byPriority.thenComparing(main).thenComparing(byOriginalIndex);
    }

    private record Entry<T>(T item, SortInfo info, int index) {
    }
}
