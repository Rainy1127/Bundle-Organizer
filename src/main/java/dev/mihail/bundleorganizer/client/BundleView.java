package dev.mihail.bundleorganizer.client;

import java.util.ArrayList;
import java.util.List;

import dev.mihail.bundleorganizer.common.BundleData;
import dev.mihail.bundleorganizer.common.ItemFactsReader;
import dev.mihail.bundleorganizer.core.BundleSummary;
import dev.mihail.bundleorganizer.core.ItemCategory;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.BundleContents;

/**
 * Снимок содержимого мешка для отрисовки панели: заполненность, строки с предметами и сводка по категориям.
 * <p>
 * Снимок строится заново только когда у предмета появился новый объект {@link BundleContents},
 * поэтому наведённый курсор не тратит время каждый кадр.
 */
public record BundleView(BundleData.Fill fill, List<Row> rows, List<BundleSummary.Part> parts) {

    /**
     * Одна запись мешка.
     *
     * @param stack    предмет для отображения названия
     * @param category категория предмета
     * @param count    количество предметов в записи
     */
    public record Row(ItemStack stack, ItemCategory category, int count) {
    }

    // Единственный элемент кэша: курсор одновременно наведён только на один мешок.
    private static BundleContents cachedContents;
    private static BundleView cachedView;

    /** Строит снимок мешка или возвращает {@code null}, если предмет не мешок. */
    public static BundleView of(ItemStack bundle) {
        BundleContents contents = bundle.get(DataComponents.BUNDLE_CONTENTS);
        if (contents == null) {
            return null;
        }
        if (contents == cachedContents && cachedView != null) {
            return cachedView;
        }

        List<Row> rows = new ArrayList<>();
        List<BundleSummary.WeightedEntry> weighted = new ArrayList<>();
        for (ItemStackTemplate template : contents.items()) {
            ItemStack stack = template.create();
            ItemCategory category = ItemFactsReader.categoryOf(stack);
            int count = template.count();
            rows.add(new Row(stack, category, count));
            // Вес записи, как в ванили: количество / максимальный стак.
            weighted.add(new BundleSummary.WeightedEntry(category, count / (double) Math.max(1, stack.getMaxStackSize()), count));
        }

        BundleView view = new BundleView(BundleData.fillOf(contents), List.copyOf(rows), BundleSummary.aggregate(weighted));
        cachedContents = contents;
        cachedView = view;
        return view;
    }
}
