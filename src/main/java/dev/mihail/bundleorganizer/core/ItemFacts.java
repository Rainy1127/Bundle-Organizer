package dev.mihail.bundleorganizer.core;

/**
 * Набор простых признаков предмета. Заполняется на стороне Minecraft (теги, компоненты),
 * а классификация по ним ({@link CategoryClassifier}) остаётся чистой Java.
 *
 * @param potion   зелья и похожие напитки
 * @param food     еда
 * @param armor    броня
 * @param tool     инструменты (кирки, топоры, лопаты, мотыги, ножницы и т.п.)
 * @param weapon   оружие (мечи, копья, луки, арбалеты, трезубец, булава, щит)
 * @param resource ресурсы (руды, слитки, самоцветы, сырьё, порошки, стержни и т.п.)
 * @param block    предмет-блок
 */
public record ItemFacts(boolean potion, boolean food, boolean armor, boolean tool,
                        boolean weapon, boolean resource, boolean block) {

    /** Предмет без каких-либо признаков, попадает в {@link ItemCategory#MISC}. */
    public static final ItemFacts NONE = new ItemFacts(false, false, false, false, false, false, false);
}
