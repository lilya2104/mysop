package edu.rutmiit.demo.grpcenrichment.listener;

import edu.rutmiit.demo.events.EventMetadata;
import edu.rutmiit.demo.events.GreenhouseEvent;
import edu.rutmiit.demo.grpc.AnalyzeGreenhouseRequest;
import edu.rutmiit.demo.grpc.GreenhouseAnalysisResponse;
import edu.rutmiit.demo.grpc.GreenhouseAnalyticsGrpc;
import edu.rutmiit.demo.grpcenrichment.publisher.EnrichmentEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Слушатель событий greenhouse.created из RabbitMQ.
 *
 * Десериализация — ручная (как в audit-service), потому что EventEnvelope<T>
 * является generic-типом, и Jackson не может определить конкретный подтип T.
 */
@Component
public class GreenhouseCreatedListener {

    private static final Logger log = LoggerFactory.getLogger(GreenhouseCreatedListener.class);

    private final GreenhouseAnalyticsGrpc.GreenhouseAnalyticsBlockingStub analyticsStub;
    private final EnrichmentEventPublisher enrichmentPublisher;
    private final JsonMapper jsonMapper;

    public GreenhouseCreatedListener(GreenhouseAnalyticsGrpc.GreenhouseAnalyticsBlockingStub analyticsStub,
                               EnrichmentEventPublisher enrichmentPublisher,
                               JsonMapper jsonMapper) {
        this.analyticsStub = analyticsStub;
        this.enrichmentPublisher = enrichmentPublisher;
        this.jsonMapper = jsonMapper;
    }

    /**
     * Обрабатывает событие greenhouse.created:
     * 1. Десериализует событие из JSON
     * 2. Формирует gRPC-запрос
     * 3. Вызывает gRPC-сервер (синхронно)
     * 4. Публикует результат как событие greenhouse.enriched
     */
    @RabbitListener(queues = "q.enrichment.greenhouse-created", messageConverter = "")
    public void handleGreenhouseCreated(Message message) {
        try {
            // 1. Парсим JSON-конверт
            byte[] body = message.getBody();
            JsonNode root = jsonMapper.readTree(body);

            JsonNode metaNode = root.get("metadata");
            EventMetadata metadata = jsonMapper.treeToValue(metaNode, EventMetadata.class);

            JsonNode payloadNode = root.get("payload");
            GreenhouseEvent.Created greenhouseCreated = jsonMapper.treeToValue(payloadNode, GreenhouseEvent.Created.class);

            log.info("Получено событие greenhouse.created: greenhouseId={}, растение {} [eventId={}]",
                    greenhouseCreated.greenhouseId(), greenhouseCreated.namePlant(), metadata.eventId());

            // 2. Формируем gRPC-запрос
            AnalyzeGreenhouseRequest grpcRequest = AnalyzeGreenhouseRequest.newBuilder()
                    .setGreenhouseId(greenhouseCreated.greenhouseId())
                    .setPlantName(greenhouseCreated.namePlant())
                    .setVariety(greenhouseCreated.varietyPlant() != null ? greenhouseCreated.varietyPlant() : "")
                    .setStatus(greenhouseCreated.status() != null ? String.valueOf(greenhouseCreated.status()) : "")
                    .setQuantity(greenhouseCreated.quantityPlant() != null ? greenhouseCreated.quantityPlant() : 0)
                    .setDaysSinceLastWatering(calculateDaysSinceLastWatering(greenhouseCreated.lastWatered()))
                    .setTotalWaterings(greenhouseCreated.totalWaterings() != null ? greenhouseCreated.totalWaterings() : 0)
                    .build();

            // 3. Вызываем gRPC-сервер (синхронно)
            log.info("Вызов gRPC: GreenhouseAnalytics.AnalyzeGreenhouse(greenhouseId={})", greenhouseCreated.greenhouseId());
            GreenhouseAnalysisResponse grpcResponse = analyticsStub.analyzeGreenhouse(grpcRequest);

            log.info("gRPC ответ получен: greenhouseId={}, здоровье={}, срочность={}",
                    grpcResponse.getGreenhouseId(),
                    grpcResponse.getHealthStatus(),
                    grpcResponse.getWateringUrgency());

            // 4. Публикуем событие
            enrichmentPublisher.publishEnriched(greenhouseCreated.greenhouseId(), grpcResponse);

            log.info("Теплица обогащена: greenhouseId={}, статус здоровья={} и greenhouse.analyzed отправлено",
                    greenhouseCreated.greenhouseId(), grpcResponse.getHealthStatus());

        } catch (io.grpc.StatusRuntimeException e) {
            log.error("gRPC ошибка при обогащении теплицы: {} ({})",
                    e.getStatus().getDescription(), e.getStatus().getCode());
            throw new RuntimeException("gRPC-вызов завершился ошибкой", e);

        } catch (Exception e) {
            log.error("Ошибка обработки события greenhouse.created: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось обработать событие greenhouse.created", e);
        }
    }

    /**
     * Вычисляет количество дней, прошедших с последнего полива
     */
    private int calculateDaysSinceLastWatering(LocalDate lastWatered) {
        long days = ChronoUnit.DAYS.between(lastWatered, LocalDate.now());
        return (int) Math.max(0, days);
    }
}
