package backend.academy.analyzer;

import java.util.List;
import java.util.Map;
import backend.academy.analyzer.enums.ResponseCode;
import backend.academy.analyzer.metrics.Metrics;
import backend.academy.analyzer.metrics.MetricsFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class MetricsTest {

    @Mock
    private MetricsFacade metricsFacade;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetFromDate() {
        when(metricsFacade.getFromDate()).thenReturn("2024-11-01");

        String result = Metrics.getFromDate(metricsFacade);
        assertEquals("2024-11-01", result, "Ожидалось '2024-11-01' для toDate, но получено: " + result);
    }

    @Test
    void testGetToDate() {
        when(metricsFacade.getToDate()).thenReturn("2024-11-01");

        String result = Metrics.getToDate(metricsFacade);
        assertEquals("2024-11-01", result, "Ожидалось '2024-11-01' для fromDate, но получено: " + result);
    }

    @Test
    void testGetFrequentlyRequestedResources() {
        when(metricsFacade.getResourceCountMap()).thenReturn(Map.of(
            "/index.html", 5,
            "/about.html", 10
        ));

        String result = Metrics.getFrequentlyRequestedResources(metricsFacade);
        assertEquals("/about.html", result,
            "Ожидался ресурс '/about.html' как самый часто запрашиваемый, но получено: " + result);
    }

    @Test
    void testGetFrequentlyRequestedCode() {
        when(metricsFacade.getResponseCodeCountMap()).thenReturn(Map.of(
            ResponseCode.OK, 15,
            ResponseCode.NOT_FOUND, 5
        ));

        int result = Metrics.getFrequentlyRequestedCode(metricsFacade);
        assertEquals(ResponseCode.OK.code(), result,
            "Ожидался статус код '200' (OK) как наиболее частый, но получено: " + result);
    }

    @Test
    void testGetAverageSizeServerResponse() {
        when(metricsFacade.getBytesList()).thenReturn(List.of(500, 1000, 1500));

        double result = Metrics.getAverageSizeServerResponse(metricsFacade);
        assertEquals(1000.0, result, "Ожидался средний размер ответа 1000.0 байт, но получено: " + result);
    }

    @Test
    void testGetPercentile() {
        when(metricsFacade.getBytesList()).thenReturn(List.of(100, 200, 300, 400, 500));

        double result = Metrics.getPercentile(metricsFacade);
        assertTrue(result > 400.0, "Ожидался процентиль больше 400.0, но получено: " + result);
    }
}
