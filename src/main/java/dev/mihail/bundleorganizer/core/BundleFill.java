package dev.mihail.bundleorganizer.core;

/**
 * Расчёт заполненности мешка.
 * <p>
 * Ванильный мешок хранит заполненность как дробь {@code числитель/знаменатель}, где 1 означает «полон».
 * Для игрока удобнее считать в «юнитах»: один юнит соответствует одному предмету из стака в 64.
 * Полный мешок содержит {@value #UNITS} юнитов.
 */
public final class BundleFill {
    /** Вместимость мешка в юнитах. */
    public static final int UNITS = 64;

    private BundleFill() {
    }

    /**
     * Свободное место в юнитах.
     * <p>
     * Если мешок ещё не полон, но свободного места меньше одного юнита, возвращается 1:
     * так индикатор никогда не показывает «0 свободно» у мешка, в который можно что-то положить.
     *
     * @param numerator   числитель дроби заполненности (неотрицательный)
     * @param denominator знаменатель дроби заполненности (положительный)
     */
    public static int freeUnits(int numerator, int denominator) {
        if (denominator <= 0 || numerator <= 0) {
            return UNITS;
        }
        if (numerator >= denominator) {
            return 0;
        }
        long free = (long) UNITS * (denominator - numerator) / denominator;
        return (int) Math.max(1, Math.min(UNITS, free));
    }

    /** Занятое место в юнитах (дополняет {@link #freeUnits(int, int)} до {@value #UNITS}). */
    public static int usedUnits(int numerator, int denominator) {
        return UNITS - freeUnits(numerator, denominator);
    }

    /** Заполненность от 0.0 до 1.0. */
    public static double ratio(int numerator, int denominator) {
        if (denominator <= 0 || numerator <= 0) {
            return 0.0;
        }
        return Math.min(1.0, (double) numerator / denominator);
    }

    /** Уровень заполненности для выбора цвета индикатора. */
    public static FillLevel level(int numerator, int denominator) {
        if (denominator <= 0 || numerator <= 0) {
            return FillLevel.EMPTY;
        }
        if (numerator >= denominator) {
            return FillLevel.FULL;
        }
        double ratio = (double) numerator / denominator;
        if (ratio < 0.5) {
            return FillLevel.LOW;
        }
        if (ratio < 0.85) {
            return FillLevel.MEDIUM;
        }
        return FillLevel.HIGH;
    }
}
