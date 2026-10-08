package dev.mihail.bundleorganizer.common;

import java.util.List;

import org.apache.commons.lang3.math.Fraction;

import dev.mihail.bundleorganizer.core.BundleFill;
import dev.mihail.bundleorganizer.core.BundleSorter;
import dev.mihail.bundleorganizer.core.FillLevel;
import dev.mihail.bundleorganizer.core.ItemCategory;
import dev.mihail.bundleorganizer.core.SortInfo;
import dev.mihail.bundleorganizer.core.SortMode;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.BundleContents;

/**
 * Мост между ванильным {@link BundleContents} и чистой логикой из пакета {@code core}.
 * Общий для клиента и сервера.
 */
public final class BundleData {
    private BundleData() {
    }

    /** Является ли предмет мешком (имеет компонент содержимого мешка). */
    public static boolean isBundle(ItemStack stack) {
        return !stack.isEmpty() && stack.get(DataComponents.BUNDLE_CONTENTS) != null;
    }

    /**
     * Заполненность мешка как дробь, готовая для {@link BundleFill}.
     *
     * @param numerator   числитель
     * @param denominator знаменатель
     */
    public record Fill(int numerator, int denominator) {
        public int freeUnits() {
            return BundleFill.freeUnits(numerator, denominator);
        }

        public int usedUnits() {
            return BundleFill.usedUnits(numerator, denominator);
        }

        public double ratio() {
            return BundleFill.ratio(numerator, denominator);
        }

        public FillLevel level() {
            return BundleFill.level(numerator, denominator);
        }
    }

    public static Fill fillOf(BundleContents contents) {
        Fraction weight = contents.weight().result().orElse(Fraction.ZERO);
        return new Fill(weight.getNumerator(), weight.getDenominator());
    }

    /** Данные записи мешка для сортировки. */
    public static SortInfo sortInfo(ItemStackTemplate template) {
        ItemStack stack = template.create();
        String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        return new SortInfo(ItemFactsReader.categoryOf(stack), id, template.count());
    }

    /**
     * Сортирует содержимое мешка на месте.
     * <p>
     * Порядок записей в мешке важен: из мешка первым достаётся предмет, выбранный в данный момент
     * (по умолчанию первый), поэтому сортировка меняет и то, что игрок достанет первым.
     * После сортировки выбор предмета сбрасывается, потому что индексы поменялись.
     *
     * @param bundle   стак с компонентом содержимого мешка
     * @param mode     режим сортировки
     * @param priority категория, которая должна идти первой, или {@code null}
     * @return {@code true}, если порядок действительно изменился
     */
    public static boolean sortInPlace(ItemStack bundle, SortMode mode, ItemCategory priority) {
        BundleContents contents = bundle.get(DataComponents.BUNDLE_CONTENTS);
        if (contents == null) {
            return false;
        }
        List<ItemStackTemplate> items = contents.items();
        if (items.size() < 2) {
            return false;
        }
        List<ItemStackTemplate> sorted = BundleSorter.sort(items, BundleData::sortInfo, mode, priority);
        if (sorted.equals(items)) {
            return false;
        }
        bundle.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(sorted));
        return true;
    }
}
