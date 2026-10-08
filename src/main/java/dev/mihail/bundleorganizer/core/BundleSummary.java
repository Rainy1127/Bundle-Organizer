package dev.mihail.bundleorganizer.core;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Сводка содержимого мешка по категориям: сколько места и предметов занимает каждая категория.
 * Используется для цветной полосы заполнения и легенды в боковой панели.
 */
public final class BundleSummary {
    private BundleSummary() {
    }

    /**
     * Одна запись мешка с приблизительным весом (доля от полной вместимости).
     *
     * @param category категория
     * @param weight   вес записи, неотрицательный
     * @param count    количество предметов
     */
    public record WeightedEntry(ItemCategory category, double weight, int count) {
    }

    /**
     * Итог по одной категории.
     *
     * @param category категория
     * @param weight   суммарный вес
     * @param items    суммарное количество предметов
     */
    public record Part(ItemCategory category, double weight, int items) {
    }

    /**
     * Складывает записи по категориям. В результате категории идут в порядке {@link ItemCategory},
     * пустые категории пропускаются.
     */
    public static List<Part> aggregate(Iterable<WeightedEntry> entries) {
        Map<ItemCategory, double[]> sums = new EnumMap<>(ItemCategory.class);
        for (WeightedEntry entry : entries) {
            double[] sum = sums.computeIfAbsent(entry.category(), c -> new double[2]);
            sum[0] += Math.max(0.0, entry.weight());
            sum[1] += Math.max(0, entry.count());
        }

        List<Part> parts = new ArrayList<>(sums.size());
        for (Map.Entry<ItemCategory, double[]> e : sums.entrySet()) {
            double[] sum = e.getValue();
            if (sum[1] > 0 || sum[0] > 0) {
                parts.add(new Part(e.getKey(), sum[0], (int) sum[1]));
            }
        }
        return parts;
    }

    /**
     * Делит заполненную часть полосы между категориями пропорционально их весу
     * (метод наибольших остатков), чтобы сумма сегментов в точности равнялась {@code filledWidth}.
     * Каждая непустая категория получает минимум 1 пиксель, если для этого хватает ширины.
     *
     * @param parts       итоги по категориям
     * @param filledWidth ширина заполненной части полосы в пикселях
     * @return ширины сегментов в том же порядке, что и {@code parts}
     */
    public static int[] segmentWidths(List<Part> parts, int filledWidth) {
        int n = parts.size();
        int[] widths = new int[n];
        if (n == 0 || filledWidth <= 0) {
            return widths;
        }

        double totalWeight = 0.0;
        for (Part part : parts) {
            totalWeight += part.weight();
        }
        if (totalWeight <= 0.0) {
            return widths;
        }

        double[] remainders = new double[n];
        int assigned = 0;
        for (int i = 0; i < n; i++) {
            double exact = parts.get(i).weight() / totalWeight * filledWidth;
            widths[i] = (int) Math.floor(exact);
            remainders[i] = exact - widths[i];
            assigned += widths[i];
        }

        // Раздаём оставшиеся пиксели тем, у кого больше дробная часть.
        // Остаток всегда меньше числа непустых категорий, поэтому одного прохода достаточно.
        int left = filledWidth - assigned;
        if (left > 0) {
            Integer[] order = new Integer[n];
            for (int i = 0; i < n; i++) {
                order[i] = i;
            }
            java.util.Arrays.sort(order, (a, b) -> Double.compare(remainders[b], remainders[a]));
            for (int k = 0; k < n && left > 0; k++) {
                int i = order[k];
                if (parts.get(i).weight() > 0) {
                    widths[i]++;
                    left--;
                }
            }
        }

        // Минимум 1 пиксель каждой непустой категории, недостающее берём у самого широкого сегмента.
        if (filledWidth >= countNonEmpty(parts)) {
            for (int i = 0; i < n; i++) {
                if (parts.get(i).weight() > 0 && widths[i] == 0) {
                    int donor = widest(widths);
                    if (widths[donor] > 1) {
                        widths[donor]--;
                        widths[i] = 1;
                    }
                }
            }
        }
        return widths;
    }

    private static int countNonEmpty(List<Part> parts) {
        int count = 0;
        for (Part part : parts) {
            if (part.weight() > 0) {
                count++;
            }
        }
        return count;
    }

    private static int widest(int[] widths) {
        int best = 0;
        for (int i = 1; i < widths.length; i++) {
            if (widths[i] > widths[best]) {
                best = i;
            }
        }
        return best;
    }
}
