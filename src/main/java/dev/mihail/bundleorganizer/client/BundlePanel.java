package dev.mihail.bundleorganizer.client;

import java.util.List;

import dev.mihail.bundleorganizer.core.BundleFill;
import dev.mihail.bundleorganizer.core.BundleSummary;
import dev.mihail.bundleorganizer.core.ItemCategory;
import dev.mihail.bundleorganizer.core.SortMode;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * Боковая панель с подробностями о мешке под курсором: цветная полоса заполнения,
 * свободное место, сводка по категориям и список предметов в порядке извлечения.
 * <p>
 * Рисуется только вызовами {@code fill} и {@code text}, без сторонних ресурсов.
 */
public final class BundlePanel {
    private static final int WIDTH = 156;
    private static final int PAD = 5;
    private static final int LINE = 10;
    private static final int GAP = 6;
    private static final int BAR_HEIGHT = 9;

    private static final int COLOR_BACKGROUND = 0xF0100010;
    private static final int COLOR_BORDER = 0xC05028B4;
    private static final int COLOR_TEXT = 0xFFFFFFFF;
    private static final int COLOR_DIM = 0xFF6E6E6E;
    private static final int COLOR_SUBTLE = 0xFFA8A8A8;
    private static final int COLOR_ACCENT = 0xFFFFD34D;
    private static final int COLOR_BAR_BACK = 0xFF373737;
    private static final int COLOR_BAR_FRAME = 0xFF000000;
    private static final int COLOR_DIVIDER = 0x44FFFFFF;
    private static final int COLOR_HIGHLIGHT = 0x33FFFFFF;

    private BundlePanel() {
    }

    /**
     * Рисует панель рядом с открытым экраном.
     *
     * @param canSort можно ли сейчас отсортировать мешок (есть мод на сервере, не творческий инвентарь)
     */
    public static void render(GuiGraphicsExtractor graphics, Font font, AbstractContainerScreen<?> screen, ItemStack bundle, boolean canSort) {
        BundleView view = BundleView.of(bundle);
        if (view == null) {
            return;
        }

        // Первый проход только считает высоту, второй рисует.
        int height = layout(new Painter(graphics, font, false), view, bundle, canSort, 0, 0);

        int left = screen.getLeftPos();
        int right = left + screen.getImageWidth();
        int px = switch (ClientConfig.PANEL_SIDE.get()) {
            case LEFT -> left - WIDTH - GAP;
            case RIGHT -> right + GAP;
            case AUTO -> right + GAP + WIDTH > screen.width - 2 ? left - WIDTH - GAP : right + GAP;
        };
        px = Math.max(2, Math.min(px, screen.width - WIDTH - 2));
        int py = Math.max(2, Math.min(screen.getTopPos(), screen.height - height - 2));

        graphics.fill(px - 1, py - 1, px + WIDTH + 1, py + height + 1, COLOR_BORDER);
        graphics.fill(px, py, px + WIDTH, py + height, COLOR_BACKGROUND);
        layout(new Painter(graphics, font, true), view, bundle, canSort, px, py);
    }

    /** Раскладывает содержимое панели и возвращает её высоту. При {@code painter.draw == false} ничего не рисует. */
    private static int layout(Painter p, BundleView view, ItemStack bundle, boolean canSort, int originX, int originY) {
        int x = originX + PAD;
        int contentWidth = WIDTH - 2 * PAD;
        int y = originY + PAD;

        // Заголовок: название предмета (учитывает переименование на наковальне).
        p.text(bundle.getHoverName(), x, y, COLOR_TEXT);
        y += LINE + 1;

        // Полоса заполнения, разбитая по цветам категорий.
        y = drawFillBar(p, view, x, y, contentWidth);

        // Строка «занято / свободно».
        boolean empty = view.rows().isEmpty();
        int free = view.fill().freeUnits();
        if (empty) {
            p.text(Component.translatable("gui.bundleorganizer.empty"), x, y, COLOR_SUBTLE);
        } else {
            p.text(Component.translatable("gui.bundleorganizer.used", view.fill().usedUnits(), BundleFill.UNITS), x, y, COLOR_SUBTLE);
        }
        Component freeText = Component.translatable("gui.bundleorganizer.free", free);
        p.text(freeText, x + contentWidth - p.width(freeText), y, view.fill().level().argb());
        y += LINE;

        y = divider(p, x, y, contentWidth);

        // Подсказки по клавишам.
        if (canSort) {
            SortMode mode = ClientConfig.SORT_MODE.get();
            p.text(Component.translatable("gui.bundleorganizer.sort", Component.translatable(mode.langKey()), ModKeys.SORT.getTranslatedKeyMessage()), x, y, COLOR_SUBTLE);
        } else {
            p.text(Component.translatable("gui.bundleorganizer.sort_unavailable"), x, y, COLOR_DIM);
        }
        y += LINE;

        ItemCategory filter = ClientState.filter();
        Component filterName = filter == null ? Component.translatable("gui.bundleorganizer.filter_all") : Component.translatable(filter.langKey());
        p.text(Component.translatable("gui.bundleorganizer.filter", filterName, ModKeys.FILTER.getTranslatedKeyMessage()), x, y, COLOR_SUBTLE);
        y += LINE;

        if (!empty) {
            y = divider(p, x, y, contentWidth);
            y = drawLegend(p, view, x, y, contentWidth);
            y = divider(p, x, y, contentWidth);
            y = drawRows(p, view, x, y, contentWidth);
        }

        return y + PAD - originY;
    }

    private static int drawFillBar(Painter p, BundleView view, int x, int y, int width) {
        p.fill(x, y, x + width, y + BAR_HEIGHT, COLOR_BAR_FRAME);
        p.fill(x + 1, y + 1, x + width - 1, y + BAR_HEIGHT - 1, COLOR_BAR_BACK);

        int inner = width - 2;
        double ratio = view.fill().ratio();
        int filled = (int) Math.round(ratio * inner);
        if (ratio > 0 && filled == 0) {
            filled = 1;
        }

        if (filled > 0) {
            List<BundleSummary.Part> parts = view.parts();
            int[] widths = BundleSummary.segmentWidths(parts, filled);
            int cursor = x + 1;
            for (int i = 0; i < parts.size(); i++) {
                ItemCategory category = parts.get(i).category();
                int segment = widths[i];
                if (segment > 0) {
                    p.fill(cursor, y + 1, cursor + segment, y + BAR_HEIGHT - 1, dimUnlessMatching(category.argb(), category));
                    cursor += segment;
                }
            }
            if (cursor == x + 1) {
                // Сводка пуста (например, не удалось посчитать вес): рисуем одним цветом уровня заполнения.
                p.fill(x + 1, y + 1, x + 1 + filled, y + BAR_HEIGHT - 1, view.fill().level().argb());
            }
        }
        return y + BAR_HEIGHT + 3;
    }

    private static int drawLegend(Painter p, BundleView view, int x, int y, int width) {
        ItemCategory filter = ClientState.filter();
        for (BundleSummary.Part part : view.parts()) {
            ItemCategory category = part.category();
            boolean active = filter != null && filter == category;
            int textColor = ClientState.matches(category) ? COLOR_TEXT : COLOR_DIM;

            if (active) {
                p.fill(x - 2, y - 1, x + width + 2, y + LINE - 1, COLOR_HIGHLIGHT);
            }
            p.fill(x, y + 2, x + 5, y + 7, dimUnlessMatching(category.argb(), category));
            p.text(Component.translatable(category.langKey()), x + 9, y, textColor);
            Component count = Component.literal(Integer.toString(part.items()));
            p.text(count, x + width - p.width(count), y, textColor);
            y += LINE;
        }
        return y;
    }

    private static int drawRows(Painter p, BundleView view, int x, int y, int width) {
        List<BundleView.Row> rows = view.rows();
        int max = ClientConfig.PANEL_MAX_ROWS.getAsInt();
        int shown = Math.min(rows.size(), max);

        for (int i = 0; i < shown; i++) {
            BundleView.Row row = rows.get(i);
            boolean matches = ClientState.matches(row.category());
            int color = matches ? COLOR_TEXT : COLOR_DIM;

            p.fill(x, y + 1, x + 2, y + 8, dimUnlessMatching(row.category().argb(), row.category()));

            int reserved = i == 0 && rows.size() > 1 ? p.width(">") + 4 : 0;
            String label = row.count() + "x " + row.stack().getHoverName().getString();
            p.text(p.trim(label, width - 6 - reserved), x + 5, y, color);
            if (reserved > 0) {
                // Первая запись достаётся из мешка первой.
                p.text(">", x + width - p.width(">"), y, COLOR_ACCENT);
            }
            y += LINE;
        }

        int hidden = rows.size() - shown;
        if (hidden > 0) {
            p.text(Component.translatable("gui.bundleorganizer.more", hidden), x + 5, y, COLOR_DIM);
            y += LINE;
        }
        if (rows.size() > 1) {
            p.text(Component.translatable("gui.bundleorganizer.first_out"), x + 5, y, COLOR_DIM);
            y += LINE;
        }
        return y;
    }

    private static int divider(Painter p, int x, int y, int width) {
        p.fill(x, y + 1, x + width, y + 2, COLOR_DIVIDER);
        return y + 4;
    }

    /** Приглушает цвет категории, если она не проходит под активный фильтр. */
    private static int dimUnlessMatching(int argb, ItemCategory category) {
        if (ClientState.matches(category)) {
            return argb;
        }
        int r = ((argb >> 16) & 0xFF) / 3;
        int g = ((argb >> 8) & 0xFF) / 3;
        int b = (argb & 0xFF) / 3;
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    /** Обёртка над {@link GuiGraphicsExtractor}, которая в «сухом» режиме только измеряет. */
    private static final class Painter {
        private final GuiGraphicsExtractor graphics;
        private final Font font;
        private final boolean draw;

        Painter(GuiGraphicsExtractor graphics, Font font, boolean draw) {
            this.graphics = graphics;
            this.font = font;
            this.draw = draw;
        }

        void fill(int x1, int y1, int x2, int y2, int argb) {
            if (draw) {
                graphics.fill(x1, y1, x2, y2, argb);
            }
        }

        void text(Component text, int x, int y, int argb) {
            if (draw) {
                graphics.text(font, text, x, y, argb);
            }
        }

        void text(String text, int x, int y, int argb) {
            if (draw) {
                graphics.text(font, text, x, y, argb);
            }
        }

        int width(Component text) {
            return font.width(text);
        }

        int width(String text) {
            return font.width(text);
        }

        /** Обрезает строку с многоточием, чтобы она влезла в заданную ширину. */
        String trim(String text, int maxWidth) {
            if (font.width(text) <= maxWidth) {
                return text;
            }
            String ellipsis = "...";
            int end = text.length();
            while (end > 0 && font.width(text.substring(0, end) + ellipsis) > maxWidth) {
                end--;
            }
            return text.substring(0, end) + ellipsis;
        }
    }
}
