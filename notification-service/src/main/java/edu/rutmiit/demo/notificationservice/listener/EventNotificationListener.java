package edu.rutmiit.demo.notificationservice.listener;

import edu.rutmiit.demo.events.*;
import edu.rutmiit.demo.notificationservice.websocket.NotificationWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Слушатель всех доменных событий из RabbitMQ.
 *
 * Получает события из очереди q.notifications.all (binding "#"),
 * формирует человекочитаемое JSON-уведомление и рассылает
 * всем подключённым WebSocket-клиентам через NotificationWebSocketHandler.
 *
 * Дедупликация — по eventId (на случай повторной доставки RabbitMQ).
 */
@Component
public class EventNotificationListener {

    private static final Logger log = LoggerFactory.getLogger(EventNotificationListener.class);

    private final NotificationWebSocketHandler webSocketHandler;
    private final JsonMapper jsonMapper;

    /** Набор обработанных eventId для дедупликации. */
    private final Set<String> processedEventIds = ConcurrentHashMap.newKeySet();

    public EventNotificationListener(NotificationWebSocketHandler webSocketHandler,
                                     JsonMapper jsonMapper) {
        this.webSocketHandler = webSocketHandler;
        this.jsonMapper = jsonMapper;
    }

    @RabbitListener(queues = "q.notifications.all", messageConverter = "")
    public void handleEvent(Message message) {
        try {
            byte[] body = message.getBody();
            JsonNode root = jsonMapper.readTree(body);

            // 1. Парсим метаданные
            JsonNode metaNode = root.get("metadata");
            EventMetadata metadata = jsonMapper.treeToValue(metaNode, EventMetadata.class);

            // 2. Дедупликация по eventId
            if (!processedEventIds.add(metadata.eventId())) {
                log.warn("Дубликат уведомления пропущен: eventId={}", metadata.eventId());
                return;
            }

            // 2. Формируем уведомление
            JsonNode payloadNode = root.get("payload");
            String title = buildTitle(metadata.eventType());
            String description = buildDescription(metadata.eventType(), payloadNode);
            String icon = resolveIcon(metadata.eventType());
            String level = resolveLevel(metadata.eventType());

            // 4. Сериализуем в JSON для WebSocket-клиента
            String notificationJson = jsonMapper.writeValueAsString(
                    new NotificationPayload(
                            "NOTIFICATION",
                            metadata.eventId(),
                            metadata.eventType(),
                            title,
                            description,
                            icon,
                            level,
                            metadata.source(),
                            metadata.timestamp().toString(),
                            Instant.now().toString()
                    )
            );

            // 5. Broadcast в WebSocket
            webSocketHandler.broadcast(notificationJson);

            log.info("[NOTIFY] {} | {} (клиентов: {})",
                    metadata.eventType(), description, webSocketHandler.getActiveConnectionCount());

        } catch (Exception e) {
            log.error("Ошибка обработки события для уведомлений: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось обработать событие", e);
        }
    }

    // Формирование заголовка уведомления
    private String buildTitle(String eventType) {
        return switch (eventType) {
            case "greenhouse.created"             -> "Новая теплица";
            case "greenhouse.updated"             -> "Теплица обновлена";
            case "greenhouse.deleted"             -> "Теплица удалена";
            case "greenhouse.watered"             -> "Полив теплицы";
            case "greenhouse.changeWateredStatus" -> "Изменение статуса состояния почвы";
            case "greenhouse.analyzed"            -> "Анализ теплицы";
            default                               -> "Событие: " + eventType;
        };
    }
    // Формирование описания
    private String buildDescription(String eventType, JsonNode payload) {
        try {
            return switch (eventType) {
                case "greenhouse.created" -> {
                    GreenhouseEvent.Created e = jsonMapper.treeToValue(payload, GreenhouseEvent.Created.class);
                    yield "Создана теплица с растением «%s» (сорт: %s), количество: %d, статус: %s".formatted(
                            e.namePlant(), e.varietyPlant(), e.quantityPlant(), e.status());
                }
                case "greenhouse.updated" -> {
                    GreenhouseEvent.Updated e = jsonMapper.treeToValue(payload, GreenhouseEvent.Updated.class);
                    yield "Обновлена теплица id=%d, растение: «%s»".formatted(e.greenhouseId(), e.namePlant());
                }
                case "greenhouse.deleted" -> {
                    GreenhouseEvent.Deleted e = jsonMapper.treeToValue(payload, GreenhouseEvent.Deleted.class);
                    yield "Удалена теплица id=%d с растением «%s»".formatted(e.greenhouseId(), e.namePlant());
                }
                case "greenhouse.watered" -> {
                    GreenhouseEvent.Watered e = jsonMapper.treeToValue(payload, GreenhouseEvent.Watered.class);
                    yield "Полита теплица id=%d с растением «%s», новый статус: %s, всего поливов: %d".formatted(
                            e.greenhouseId(), e.namePlant(), e.newStatus(), e.totalWaterings());
                }
                case "greenhouse.changeWateredStatus" -> {
                    GreenhouseEvent.StatusChanged e = jsonMapper.treeToValue(payload, GreenhouseEvent.StatusChanged.class);
                    yield "Статус полива теплицы id=%d с растением «%s» изменён с %s на %s".formatted(
                            e.greenhouseId(), e.namePlant(), e.oldStatus(), e.newStatus());
                }
                case "greenhouse.analyzed" -> {
                    GreenhouseEvent.Analyzed e = jsonMapper.treeToValue(payload, GreenhouseEvent.Analyzed.class);
                    yield "Анализ теплицы id=%d: здоровье=%s (%.1f), срочность полива=%s, интервал=%d дн, стадия=%s".formatted(
                            e.greenhouseId(), e.healthStatus(), e.healthScore(),
                            e.wateringUrgency(), e.recommendedWateringDays(), e.growthStage());
                }
                default -> "Неизвестное событие: " + eventType;
            };
        } catch (Exception e) {
            return "Событие " + eventType + " (ошибка парсинга)";
        }
    }

    // Иконка по типу события
    private String resolveIcon(String eventType) {
        return switch (eventType) {
            case "greenhouse.created"   -> "greenhouse-plus";
            case "greenhouse.updated"   -> "greenhouse-edit";
            case "greenhouse.deleted"   -> "greenhouse-remove";
            case "greenhouse.watered"   -> "water-drop";
            case "greenhouse.analyzed"  -> "analytics";
            default                     -> "bell";
        };
    }

    // Уровень уведомления
    private String resolveLevel(String eventType) {
        return switch (eventType) {
            case "greenhouse.deleted" -> "warning";
            case "greenhouse.analyzed" -> "info";
            default                   -> "success";
        };
    }

    // Payload уведомления для WebSocket.
    record NotificationPayload(
            String type,
            String eventId,
            String eventType,
            String title,
            String description,
            String icon,
            String level,
            String source,
            String eventTimestamp,
            String receivedAt
    ) {}
}
