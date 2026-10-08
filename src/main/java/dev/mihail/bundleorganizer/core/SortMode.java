package dev.mihail.bundleorganizer.core;

/**
 * Режим сортировки содержимого мешка.
 */
public enum SortMode {
    /** Сначала категории (в порядке {@link ItemCategory}), внутри категории по ID предмета. */
    CATEGORY("category"),
    /** По ID предмета (алфавит по пути, затем по пространству имён). */
    NAME("name"),
    /** По количеству предметов, от большего к меньшему. */
    COUNT("count");

    private static final SortMode[] VALUES = values();

    private final String id;

    SortMode(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    /** Ключ перевода названия режима. */
    public String langKey() {
        return "sortmode.bundleorganizer." + id;
    }

    /** Следующий режим по кругу. */
    public SortMode next() {
        return VALUES[(ordinal() + 1) % VALUES.length];
    }

    /**
     * Безопасно получает режим по номеру. Неизвестный номер (например, от другой версии мода)
     * превращается в {@link #CATEGORY}, чтобы сервер не падал на мусорных данных.
     */
    public static SortMode byOrdinal(int ordinal) {
        if (ordinal < 0 || ordinal >= VALUES.length) {
            return CATEGORY;
        }
        return VALUES[ordinal];
    }
}
