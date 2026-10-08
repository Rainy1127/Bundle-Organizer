package dev.mihail.bundleorganizer.network;

import dev.mihail.bundleorganizer.BundleOrganizer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Запрос клиента серверу: отсортировать мешок в указанном слоте открытого меню.
 * <p>
 * Клиент отправляет только «что и как» (номер меню, слот, режим, приоритетная категория).
 * Содержимое мешка сервер берёт у себя и ничему из пакета не доверяет без проверки.
 *
 * @param containerId номер открытого меню (должен совпасть с {@code containerMenu} игрока на сервере)
 * @param slotIndex   индекс слота в меню
 * @param sortMode    порядковый номер {@code SortMode}
 * @param filter      закодированная категория-приоритет: 0 = нет, иначе ordinal + 1
 */
public record SortBundlePayload(int containerId, int slotIndex, int sortMode, int filter) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SortBundlePayload> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(BundleOrganizer.MOD_ID, "sort_bundle"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SortBundlePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SortBundlePayload::containerId,
            ByteBufCodecs.VAR_INT, SortBundlePayload::slotIndex,
            ByteBufCodecs.VAR_INT, SortBundlePayload::sortMode,
            ByteBufCodecs.VAR_INT, SortBundlePayload::filter,
            SortBundlePayload::new);

    @Override
    public CustomPacketPayload.Type<SortBundlePayload> type() {
        return TYPE;
    }
}
