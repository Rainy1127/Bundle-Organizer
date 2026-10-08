package dev.mihail.bundleorganizer.core;

/**
 * Данные одной записи мешка, нужные для сортировки.
 *
 * @param category   категория предмета
 * @param registryId идентификатор предмета в формате {@code namespace:path}
 * @param count      количество предметов в записи
 */
public record SortInfo(ItemCategory category, String registryId, int count) {

    /** Часть идентификатора после двоеточия (или весь идентификатор, если двоеточия нет). */
    public String path() {
        int colon = registryId.indexOf(':');
        return colon < 0 ? registryId : registryId.substring(colon + 1);
    }

    /** Часть идентификатора до двоеточия (пустая строка, если двоеточия нет). */
    public String namespace() {
        int colon = registryId.indexOf(':');
        return colon < 0 ? "" : registryId.substring(0, colon);
    }
}
