package edu.rutmiit.demo.events;

/**
 * Константы для маршрутизации событий в RabbitMQ.
 *
 * Routing key в topic exchange работает как почтовый индекс:
 * - "greenhouse.created" — конкретное событие
 * - "greenhouse.*"       — все события теплиц
 * - "#"                  — все события вообще
 *
 * Вынесены в контракт, чтобы publisher и consumer использовали одни и те же строки.
 * Рассогласование routing key - частая ошибка, которую трудно отследить.
 */
public final class RoutingKeys {

    private RoutingKeys() {
        // утилитарный класс — экземпляры не создаём
    }

    // Имя общего topic exchange для доменных событий
    public static final String EXCHANGE = "greenhouse.events";

    // Routing keys для событий теплиц
    public static final String GREENHOUSE_CREATED = "greenhouse.created";
    public static final String GREENHOUSE_UPDATED = "greenhouse.updated";
    public static final String GREENHOUSE_WATERED = "greenhouse.watered";
    public static final String GREENHOUSE_STATUSCHANGED = "greenhouse.changeWateredStatus";
    public static final String GREENHOUSE_DELETED = "greenhouse.deleted";

    // Routing key для обогащенных событий (результат gRPC-аналитики)
    public static final String GREENHOUSE_ANALYZED = "greenhouse.analyzed";

    // Паттерны для подписки (wildcard)
    public static final String ALL_GREENHOUSE_EVENTS = "greenhouse.*";
    public static final String ALL_EVENTS = "#";
}
