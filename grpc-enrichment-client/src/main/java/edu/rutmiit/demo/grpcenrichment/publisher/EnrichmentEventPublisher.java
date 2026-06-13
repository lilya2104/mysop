package edu.rutmiit.demo.grpcenrichment.publisher;

import edu.rutmiit.demo.events.EventEnvelope;
import edu.rutmiit.demo.events.GreenhouseEvent;
import edu.rutmiit.demo.events.RoutingKeys;
import edu.rutmiit.demo.grpc.GreenhouseAnalysisResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Публикация событий обогащения (greenhouse.enriched) в RabbitMQ.
 *
 * Аналогичен GreenhouseEventPublisher в demo-rest, но публикует другой тип события.
 * Паттерн fire-and-forget: если RabbitMQ недоступен, ошибка логируется,
 * но gRPC-вызов уже выполнен — результат не теряется полностью.
 */
@Component
public class EnrichmentEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(EnrichmentEventPublisher.class);
    private static final String SOURCE = "grpc-enrichment-client";

    private final RabbitTemplate rabbitTemplate;

    public EnrichmentEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Публикует событие greenhouse.analyzed с результатами gRPC-аналитики.
     */
    public void publishEnriched(Long greenhouseId, GreenhouseAnalysisResponse response) {
        try {
            // Создаем событие аналитики
            GreenhouseEvent.Analyzed analyzedEvent = new GreenhouseEvent.Analyzed(
                    greenhouseId,
                    response.getHealthStatus(),
                    response.getHealthScore(),
                    response.getWateringUrgency(),
                    response.getRecommendedWateringDays(),
                    response.getGrowthStage()
            );

            // Создаем конверт с метаданными
            EventEnvelope<GreenhouseEvent> envelope = EventEnvelope.wrap(
                    analyzedEvent,
                    SOURCE,
                    RoutingKeys.GREENHOUSE_ANALYZED
            );

            // Публикуем в RabbitMQ
            rabbitTemplate.convertAndSend(
                    RoutingKeys.EXCHANGE,
                    RoutingKeys.GREENHOUSE_ANALYZED,
                    envelope
            );

            log.info("Событие отправлено: {} [greenhouseId={}, healthStatus={}, eventId={}]",
                    RoutingKeys.GREENHOUSE_ANALYZED,
                    greenhouseId,
                    response.getHealthStatus(),
                    envelope.metadata().eventId());

        } catch (Exception e) {
            log.error("Не удалось отправить событие {}: {}", RoutingKeys.GREENHOUSE_ANALYZED, e.getMessage(), e);
        }
    }
}
