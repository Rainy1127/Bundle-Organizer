package dev.mihail.bundleorganizer.core;

import java.util.Locale;
import java.util.Optional;

/**
 * Категория предмета для фильтрации и сортировки содержимого мешка.
 * <p>
 * Порядок констант определяет порядок при сортировке «по категории».
 * Чистая Java: без обращений к Minecraft, поэтому покрывается юнит-тестами.
 */
public enum ItemCategory {
    BLOCKS("blocks", 0xFF6BA3E8),
    RESOURCES("resources", 0xFFE8B84A),
    TOOLS("tools", 0xFF5CCB7B),
    WEAPONS("weapons", 0xFFE8604C),
    ARMOR("armor", 0xFF4FC3C9),
    FOOD("food", 0xFFE88F3C),
    POTIONS("potions", 0xFFB06BE8),
    MISC("misc", 0xFFA0A0A0);

    private static final ItemCategory[] VALUES = values();

    private final String id;
    private final int argb;

    ItemCategory(String id, int argb) {
        this.id = id;
        this.argb = argb;
    }

    /** Стабильный строковый идентификатор (для конфигов и языковых ключей). */
    public String id() {
        return id;
    }

    /** Цвет категории в формате ARGB. */
    public int argb() {
        return argb;
    }

    /** Ключ перевода названия категории. */
    public String langKey() {
        return "category.bundleorganizer." + id;
    }

    public static Optional<ItemCategory> byId(String id) {
        if (id == null) {
            return Optional.empty();
        }
        String normalized = id.trim().toLowerCase(Locale.ROOT);
        for (ItemCategory category : VALUES) {
            if (category.id.equals(normalized)) {
                return Optional.of(category);
            }
        }
        return Optional.empty();
    }

    /**
     * Следующая категория для переключения фильтра.
     * Цикл: «все» ({@code null}) → BLOCKS → ... → MISC → «все».
     *
     * @param current текущий фильтр, {@code null} означает «все категории»
     * @return следующий фильтр, {@code null} означает «все категории»
     */
    public static ItemCategory nextFilter(ItemCategory current) {
        if (current == null) {
            return VALUES[0];
        }
        int next = current.ordinal() + 1;
        return next >= VALUES.length ? null : VALUES[next];
    }

    /**
     * Кодирует фильтр для сети: 0 = «все», иначе ordinal + 1.
     */
    public static int encodeFilter(ItemCategory filter) {
        return filter == null ? 0 : filter.ordinal() + 1;
    }

    /**
     * Обратное преобразование для {@link #encodeFilter(ItemCategory)}.
     * Неизвестные значения (в том числе из чужих версий мода) превращаются в «все».
     */
    public static ItemCategory decodeFilter(int encoded) {
        if (encoded <= 0 || encoded > VALUES.length) {
            return null;
        }
        return VALUES[encoded - 1];
    }
}
