package edu.rutmiit.demo.demorest.event;

import edu.rutmiit.demo.events.EventEnvelope;
import edu.rutmiit.demo.events.GreenhouseEvent;
import edu.rutmiit.demo.events.RoutingKeys;
import edu.rutmiit.demo.plantsapicontract.diff.WateredStatus;
import edu.rutmiit.demo.plantsapicontract.dto.GreenhouseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Публикация доменных событий теплиц в RabbitMQ.
 *
 * Паттерн: GreenhouseService вызывает publish-метод ПОСЛЕ успешного завершения
 * бизнес-операции. Если RabbitMQ недоступен — событие логируется как ошибка,
 * но основная операция (создание/удаление теплицы) НЕ откатывается.
 *
 * Это паттерн «fire-and-forget» — допустимая потеря события лучше,
 * чем отказ бизнес-операции из-за недоступности брокера.
 *
 * В промышленных системах для гарантированной доставки используют:
 * - Transactional Outbox (запись события в БД в одной транзакции с данными),
 * - Change Data Capture (Debezium/Kafka Connect).
 */
@Component
public class GreenhouseEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(GreenhouseEventPublisher.class);
    private static final String SOURCE = "demo-rest";

    private final RabbitTemplate rabbitTemplate;

    public GreenhouseEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Публикует событие «теплица создана».
     */
    public void publishCreated(GreenhouseResponse greenhouse, Integer totalWaterings) {
        var event = new GreenhouseEvent.Created(
                greenhouse.getId(),
                greenhouse.getNamePlant(),
                greenhouse.getVarietyPlant(),
                greenhouse.getQuantityPlant(),
                greenhouse.getStatus(),
                greenhouse.getLastWateredAt(),
                totalWaterings
        );
        send(RoutingKeys.GREENHOUSE_CREATED, event);
    }

    /**
     * Публикует событие «теплица обновлена».
     */
    public void publishUpdated(GreenhouseResponse greenhouse) {
        var event = new GreenhouseEvent.Updated(
                greenhouse.getId(),
                greenhouse.getNamePlant(),
                greenhouse.getVarietyPlant(),
                greenhouse.getQuantityPlant(),
                greenhouse.getStatus(),
                greenhouse.getLastWateredAt()
        );
        send(RoutingKeys.GREENHOUSE_UPDATED, event);
    }

    /**
     * Публикует событие «теплица полита».
     */
    public void publishWatered(GreenhouseResponse greenhouse, Integer totalWaterings) {
        var event = new GreenhouseEvent.Watered(
                greenhouse.getId(),
                greenhouse.getNamePlant(),
                greenhouse.getStatus(),
                LocalDate.now(),
                totalWaterings
        );
        send(RoutingKeys.GREENHOUSE_WATERED, event);
    }

    /**
     * Публикует событие «изменён статус полива».
     */
    public void publishStatusChanged(GreenhouseResponse greenhouse, WateredStatus oldStatus) {
        var event = new GreenhouseEvent.StatusChanged(
                greenhouse.getId(),
                greenhouse.getNamePlant(),
                oldStatus,
                greenhouse.getStatus(),
                LocalDate.now()
        );
        send(RoutingKeys.GREENHOUSE_STATUSCHANGED, event);
    }

    /**
     * Публикует событие «теплица удалена».
     */
    public void publishDeleted(Long bookId, String title) {
        var event = new GreenhouseEvent.Deleted(bookId, title);
        send(RoutingKeys.GREENHOUSE_DELETED, event);
    }

    /**
     * Отправляет событие в RabbitMQ, обёрнутое в EventEnvelope.
     *
     * convertAndSend:
     * - 1й аргумент: имя exchange
     * - 2й аргумент: routing key (определяет, в какие очереди попадёт сообщение)
     * - 3й аргумент: объект, который Jackson сериализует в JSON
     *
     * try-catch гарантирует, что ошибка публикации не сломает основной бизнес-поток.
     */
    private void send(String routingKey, GreenhouseEvent event) {
        try {
            EventEnvelope<GreenhouseEvent> envelope = EventEnvelope.wrap(event, SOURCE, routingKey);
            rabbitTemplate.convertAndSend(RoutingKeys.EXCHANGE, routingKey, envelope);
            log.info("Событие отправлено: {} [eventId={}]", routingKey, envelope.metadata().eventId());
        } catch (Exception e) {
            log.error("Не удалось отправить событие {}: {}", routingKey, e.getMessage());
        }
    }
}
