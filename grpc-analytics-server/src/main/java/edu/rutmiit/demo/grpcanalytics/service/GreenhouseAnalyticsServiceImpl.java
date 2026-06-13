package edu.rutmiit.demo.grpcanalytics.service;

import edu.rutmiit.demo.grpc.AnalyzeGreenhouseRequest;
import edu.rutmiit.demo.grpc.GreenhouseAnalysisResponse;
import edu.rutmiit.demo.grpc.GreenhouseAnalyticsGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Реализация gRPC-сервиса BookAnalytics.
 *
 * Наследует сгенерированный базовый класс BookAnalyticsImplBase —
 * аналог того, как REST-контроллер реализует интерфейс контракта:
 *
 *   REST:    AuthorController implements AuthorApi
 *   GraphQL: BookDataFetcher с @DgsQuery
 *   gRPC:    GreenhouseAnalyticsServiceImpl extends BookAnalyticsGrpc.BookAnalyticsImplBase
 *
 * Ключевые отличия от REST/GraphQL:
 * - Бинарный протокол (protobuf) вместо JSON — компактнее и быстрее
 * - Строго типизированный контракт (.proto) — несовместимость обнаруживается при компиляции
 * - HTTP/2 с мультиплексированием — несколько запросов в одном TCP-соединении
 * - Поддержка streaming (server, client, bidirectional) — здесь используем unary (простой запрос-ответ)
 */
public class GreenhouseAnalyticsServiceImpl extends GreenhouseAnalyticsGrpc.GreenhouseAnalyticsImplBase {

    private static final Logger log = LoggerFactory.getLogger(GreenhouseAnalyticsServiceImpl.class);

    /**
     * Обрабатывает запрос на анализ книги.
     *
     * Паттерн gRPC: метод получает request и StreamObserver для ответа.
     * StreamObserver — это callback-интерфейс:
     *   - onNext(response) — отправить ответ (для unary RPC вызывается один раз)
     *   - onCompleted()    — завершить RPC
     *   - onError(t)       — сообщить об ошибке
     *
     * Для unary RPC (один запрос → один ответ) всегда:
     *   responseObserver.onNext(response);
     *   responseObserver.onCompleted();
     */
    @Override
    public void analyzeGreenhouse(AnalyzeGreenhouseRequest request,
                            StreamObserver<GreenhouseAnalysisResponse> responseObserver) {

        log.info("gRPC запрос: анализ теплицы id={}, растение={}, статус={}, дней без полива={}",
                request.getGreenhouseId(), request.getPlantName(),
                request.getStatus(), request.getDaysSinceLastWatering());

        // ─── Вычисление метрик ───────────────────────────────────────
        String healthStatus = calculateHealthStatus(request.getStatus(), request.getDaysSinceLastWatering());
        double healthScore = calculateHealthScore(request.getStatus(), request.getDaysSinceLastWatering());
        String wateringUrgency = calculateWateringUrgency(request.getStatus(), request.getDaysSinceLastWatering());
        int recommendedDays = getRecommendedWateringInterval(request.getPlantName());
        String growthStage = determineGrowthStage(request.getDaysSinceLastWatering(), request.getTotalWaterings());

        // ─── Формируем ответ ─────────────────────────────────────────
        GreenhouseAnalysisResponse response = GreenhouseAnalysisResponse.newBuilder()
                .setGreenhouseId(request.getGreenhouseId())
                .setHealthStatus(healthStatus)
                .setHealthScore(healthScore)
                .setWateringUrgency(wateringUrgency)
                .setRecommendedWateringDays(recommendedDays)
                .setGrowthStage(growthStage)
                .build();

        log.info("gRPC ответ: теплица id={}, здоровье={}, срочность={}, рекомендуемый интервал={}дн",
                response.getGreenhouseId(), healthStatus, wateringUrgency, recommendedDays);

        // Отправляем ответ клиенту и завершаем RPC
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // ─── Демонстрационная бизнес-логика ──────────────────────────────

    /**
     * Статус здоровья растения на основе статуса почвы и дней без полива
     */
    private String calculateHealthStatus(String status, int daysSinceLastWatering) {
        if ("WET".equalsIgnoreCase(status)) { return "HEALTHY"; }
        else if ("DRY".equalsIgnoreCase(status)) {
            if (daysSinceLastWatering <= 3) return "HEALTHY";
            if (daysSinceLastWatering <= 7) return "ATTENTION_NEEDED";
            return "CRITICAL";
        }
        return "ATTENTION_NEEDED";
    }

    /**
     * Оценка здоровья 0.0 - 10.0
     */
    private double calculateHealthScore(String status, int daysSinceLastWatering) {
        if ("WET".equalsIgnoreCase(status)) { return 9.0; }
        else if ("DRY".equalsIgnoreCase(status)) {
            if (daysSinceLastWatering <= 3) return 7.0;
            if (daysSinceLastWatering <= 5) return 5.0;
            if (daysSinceLastWatering <= 7) return 3.0;
            return 1.0;
        }
        return 4.0;
    }

    /**
     * Срочность полива
     */
    private String calculateWateringUrgency(String status, int daysSinceLastWatering) {
        if ("WET".equalsIgnoreCase(status)) { return "NOT_NEEDED"; }
        else if ("DRY".equalsIgnoreCase(status)) {
            if (daysSinceLastWatering <= 2) return "SOON";
            if (daysSinceLastWatering <= 5) return "URGENT";
            return "OVERDRY";
        }
        return "SOON";
    }

    /**
     * Рекомендуемый интервал полива зависит от типа растения
     */
    private int getRecommendedWateringInterval(String plantName) {
        if (plantName == null) return 0;

        String lowerName = plantName.toLowerCase();
        if (lowerName.contains("томат") || lowerName.contains("помидор") || lowerName.contains("перец")) return 3;
        if (lowerName.contains("огурец")) return 1;
        if (lowerName.contains("клубника") || lowerName.contains("земляника") || lowerName.contains("капуста")) return 2;
        return 4; // по умолчанию раз в 5 дней
    }

    /**
     * Определение стадии роста
     */
    private String determineGrowthStage(int daysSinceLastWatering, int totalWaterings) {
        if (totalWaterings < 3) return "SEEDLING";
        if (totalWaterings < 10) return "GROWING";
        if (totalWaterings < 20) return "FLOWERING";
        return "HARVEST";
    }
}
