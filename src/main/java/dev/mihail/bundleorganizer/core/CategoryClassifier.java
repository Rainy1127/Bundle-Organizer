package dev.mihail.bundleorganizer.core;

/**
 * Определяет категорию предмета по набору признаков.
 * <p>
 * Порядок проверок важен: предмет может иметь несколько признаков сразу
 * (например, руда одновременно «ресурс» и «блок», топор — и «инструмент», и «оружие»).
 * Побеждает первая подходящая категория.
 */
public final class CategoryClassifier {
    private CategoryClassifier() {
    }

    public static ItemCategory classify(ItemFacts facts) {
        if (facts.potion()) {
            return ItemCategory.POTIONS;
        }
        if (facts.food()) {
            return ItemCategory.FOOD;
        }
        if (facts.armor()) {
            return ItemCategory.ARMOR;
        }
        if (facts.tool()) {
            return ItemCategory.TOOLS;
        }
        if (facts.weapon()) {
            return ItemCategory.WEAPONS;
        }
        if (facts.resource()) {
            return ItemCategory.RESOURCES;
        }
        if (facts.block()) {
            return ItemCategory.BLOCKS;
        }
        return ItemCategory.MISC;
    }
}
