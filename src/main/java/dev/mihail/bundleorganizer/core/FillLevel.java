package dev.mihail.bundleorganizer.core;

/**
 * Уровень заполненности мешка и соответствующий цвет индикатора.
 */
public enum FillLevel {
    EMPTY(0xFFAAAAAA),
    LOW(0xFF55FF55),
    MEDIUM(0xFFFFD34D),
    HIGH(0xFFFF9A3C),
    FULL(0xFFFF5555);

    private final int argb;

    FillLevel(int argb) {
        this.argb = argb;
    }

    /** Цвет индикатора в формате ARGB. */
    public int argb() {
        return argb;
    }
}
