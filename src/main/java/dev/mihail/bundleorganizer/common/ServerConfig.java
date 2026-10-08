package dev.mihail.bundleorganizer.common;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Серверные настройки. Файл создаётся в папке мира ({@code serverconfig}) и применяется только на сервере.
 */
public final class ServerConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ALLOW_SORTING = BUILDER
            .comment("Whether players may sort the contents of bundles.",
                    "Set to false to disable sorting on this server (the indicator and filter are client-side and keep working).")
            .define("allowSorting", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private ServerConfig() {
    }
}
