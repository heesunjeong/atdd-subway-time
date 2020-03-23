package atdd.path.web;

import atdd.AbstractAcceptanceTest;
import atdd.TestConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RouteAcceptanceTest extends AbstractAcceptanceTest {

    private StationHttpTest stationHttpTest;
    private LineHttpTest lineHttpTest;

    private Long gangnamStation;
    private Long yeoksamStation;
    private Long seonneungStation;
    private Long samsungStation;

    private Long secondLine;

    @BeforeEach
    void setUp() {
        this.stationHttpTest = new StationHttpTest(webTestClient);
        this.lineHttpTest = new LineHttpTest(webTestClient);

        // given
        this.gangnamStation = stationHttpTest.createStation(TestConstant.STATION_NAME);
        this.yeoksamStation = stationHttpTest.createStation(TestConstant.STATION_NAME_2);
        this.seonneungStation = stationHttpTest.createStation(TestConstant.STATION_NAME_3);
        this.samsungStation = stationHttpTest.createStation(TestConstant.STATION_NAME_4);

        this.secondLine = lineHttpTest.createLine(TestConstant.LINE_NAME);

        lineHttpTest.createEdgeRequest(secondLine, gangnamStation, yeoksamStation, 2);
        lineHttpTest.createEdgeRequest(secondLine, yeoksamStation, seonneungStation, 3);
        lineHttpTest.createEdgeRequest(secondLine, seonneungStation, samsungStation, 2);
    }

    @Test
    void 지하철역_사이의_최단_거리_경로_조회() {
        webTestClient.get().uri("routes/distance?startId=" + gangnamStation + "&endId=" + samsungStation)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists("ETag")
                .expectBody()
                .jsonPath("$.startStationId").isEqualTo(gangnamStation)
                .jsonPath("$.endStationId").isEqualTo(samsungStation)
                .jsonPath("$.stations.size()").isEqualTo(4)
                .jsonPath("$.estimatedTime").isEqualTo(7);
    }

    @Test
    void 지하철역_사이의_최단_시간_경로_조회() {
        webTestClient.get().uri("routes/time?startId=" + gangnamStation + "&endId=" + samsungStation)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists("ETag")
                .expectBody()
                .jsonPath("$.startStationId").isEqualTo(gangnamStation)
                .jsonPath("$.endStationId").isEqualTo(samsungStation)
                .jsonPath("$.stations.size()").isEqualTo(4)
                .jsonPath("$.estimatedTime").isEqualTo(7);
    }
}
