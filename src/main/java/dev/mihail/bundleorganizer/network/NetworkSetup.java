package dev.mihail.bundleorganizer.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Регистрация сетевых пакетов.
 * <p>
 * Пакеты помечены как {@code optional()}: игрок с модом может зайти на сервер без мода (и наоборот),
 * просто сортировка будет недоступна. Индикатор и фильтр работают целиком на клиенте.
 */
public final class NetworkSetup {
    /** Версия протокола мода. Меняется при несовместимых изменениях пакетов. */
    public static final String PROTOCOL_VERSION = "1";

    private NetworkSetup() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION).optional();
        registrar.playToServer(SortBundlePayload.TYPE, SortBundlePayload.STREAM_CODEC, ServerSortHandler::handle);
    }
}
