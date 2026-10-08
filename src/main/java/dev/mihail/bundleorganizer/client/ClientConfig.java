package dev.mihail.bundleorganizer.client;

import dev.mihail.bundleorganizer.core.SortMode;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Клиентские настройки: внешний вид панели и индикаторов, режим сортировки по умолчанию.
 */
public final class ClientConfig {
    /** С какой стороны от инвентаря рисовать боковую панель. */
    public enum PanelSide {
        /** Справа, а если не помещается, то слева. */
        AUTO,
        LEFT,
        RIGHT
    }

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue SHOW_PANEL = BUILDER
            .comment("Show the side panel with details when the cursor is over a bundle.")
            .define("showPanel", true);

    public static final ModConfigSpec.EnumValue<PanelSide> PANEL_SIDE = BUILDER
            .comment("Which side of the inventory the panel is drawn on.")
            .defineEnum("panelSide", PanelSide.AUTO);

    public static final ModConfigSpec.IntValue PANEL_MAX_ROWS = BUILDER
            .comment("Maximum number of item rows listed in the panel.")
            .defineInRange("panelMaxRows", 8, 1, 16);

    public static final ModConfigSpec.BooleanValue SLOT_INDICATOR = BUILDER
            .comment("Draw the number of free capacity units on the bundle icon.")
            .define("slotIndicator", true);

    public static final ModConfigSpec.BooleanValue TOOLTIP_LINE = BUILDER
            .comment("Add a 'Free: X of 64' line to the bundle tooltip.")
            .define("tooltipLine", true);

    public static final ModConfigSpec.EnumValue<SortMode> SORT_MODE = BUILDER
            .comment("Sorting mode used by the sort key. Shift + sort key switches it in game.")
            .defineEnum("sortMode", SortMode.CATEGORY);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private ClientConfig() {
    }
}
