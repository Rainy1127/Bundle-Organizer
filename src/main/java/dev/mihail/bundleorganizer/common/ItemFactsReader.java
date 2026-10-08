package dev.mihail.bundleorganizer.common;

import dev.mihail.bundleorganizer.core.CategoryClassifier;
import dev.mihail.bundleorganizer.core.ItemCategory;
import dev.mihail.bundleorganizer.core.ItemFacts;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.Tags;

/**
 * Единственное место, где предмет превращается в набор признаков {@link ItemFacts}.
 * Опирается на теги и компоненты, поэтому корректно работает и с предметами из других модов,
 * которые следуют конвенции тегов {@code c:}.
 * <p>
 * Класс общий для клиента и сервера: не содержит клиентских вызовов.
 */
public final class ItemFactsReader {
    private ItemFactsReader() {
    }

    public static ItemFacts read(ItemStack stack) {
        if (stack.isEmpty()) {
            return ItemFacts.NONE;
        }
        return new ItemFacts(
                isPotion(stack),
                isFood(stack),
                isArmor(stack),
                isTool(stack),
                isWeapon(stack),
                isResource(stack),
                stack.getItem() instanceof BlockItem);
    }

    public static ItemCategory categoryOf(ItemStack stack) {
        return CategoryClassifier.classify(read(stack));
    }

    private static boolean isPotion(ItemStack stack) {
        // Компонент зелья есть и у зачарованных стрел, но у настоящих зелий стак равен 1.
        return stack.is(Tags.Items.POTIONS)
                || (stack.has(DataComponents.POTION_CONTENTS) && stack.getMaxStackSize() == 1);
    }

    private static boolean isFood(ItemStack stack) {
        return stack.has(DataComponents.FOOD) || stack.is(Tags.Items.FOODS);
    }

    private static boolean isArmor(ItemStack stack) {
        return stack.is(Tags.Items.ARMORS)
                || stack.is(ItemTags.HEAD_ARMOR)
                || stack.is(ItemTags.CHEST_ARMOR)
                || stack.is(ItemTags.LEG_ARMOR)
                || stack.is(ItemTags.FOOT_ARMOR);
    }

    private static boolean isTool(ItemStack stack) {
        return stack.is(ItemTags.PICKAXES)
                || stack.is(ItemTags.AXES)
                || stack.is(ItemTags.SHOVELS)
                || stack.is(ItemTags.HOES)
                || stack.is(Tags.Items.MINING_TOOL_TOOLS)
                || stack.is(Tags.Items.TOOLS_SHEAR)
                || stack.is(Tags.Items.TOOLS_FISHING_ROD)
                || stack.is(Tags.Items.TOOLS_BRUSH)
                || stack.is(Tags.Items.TOOLS_IGNITER)
                || stack.is(Tags.Items.TOOLS_WRENCH);
    }

    private static boolean isWeapon(ItemStack stack) {
        return stack.is(ItemTags.SWORDS)
                || stack.is(ItemTags.SPEARS)
                || stack.is(Tags.Items.MELEE_WEAPON_TOOLS)
                || stack.is(Tags.Items.RANGED_WEAPON_TOOLS)
                || stack.is(Tags.Items.TOOLS_BOW)
                || stack.is(Tags.Items.TOOLS_CROSSBOW)
                || stack.is(Tags.Items.TOOLS_TRIDENT)
                || stack.is(Tags.Items.TOOLS_MACE)
                || stack.is(Tags.Items.TOOLS_SHIELD);
    }

    private static boolean isResource(ItemStack stack) {
        return stack.is(Tags.Items.ORES)
                || stack.is(Tags.Items.INGOTS)
                || stack.is(Tags.Items.GEMS)
                || stack.is(Tags.Items.RAW_MATERIALS)
                || stack.is(Tags.Items.DUSTS)
                || stack.is(Tags.Items.NUGGETS)
                || stack.is(Tags.Items.RODS)
                || stack.is(Tags.Items.STRINGS)
                || stack.is(Tags.Items.FEATHERS)
                || stack.is(Tags.Items.LEATHERS)
                || stack.is(Tags.Items.BONES)
                || stack.is(Tags.Items.SLIME_BALLS)
                || stack.is(Tags.Items.GUNPOWDERS)
                || stack.is(Tags.Items.ENDER_PEARLS)
                || stack.is(Tags.Items.NETHER_STARS)
                || stack.is(Tags.Items.DYES);
    }
}
