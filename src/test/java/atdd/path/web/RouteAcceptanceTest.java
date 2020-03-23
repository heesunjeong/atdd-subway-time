package atdd.path.web;

import atdd.AbstractAcceptanceTest;
import atdd.TestConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RouteAcceptanceTest extends AbstractAcceptanceTest {

    private static final String ROUTES_URI = "/routes";

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

    @DisplayName("지하철역 사이의 최단거리 경로 조회")
    @Test
    void retrievePathByShortDistance() {
        webTestClient.get().uri(ROUTES_URI + "/distance?startId=" + gangnamStation + "&endId=" + samsungStation)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists("ETag")
                .expectBody()
                .jsonPath("$.startStationId").isEqualTo(gangnamStation)
                .jsonPath("$.endStationId").isEqualTo(samsungStation)
                .jsonPath("$.stations.size()").isEqualTo(4)
                .jsonPath("$.estimatedTime").isEqualTo(7);
    }

    @DisplayName("지하철역 사이의 최단시간 경로 조회")
    @Test
    void retrievePathByShortTime() {
        webTestClient.get().uri(ROUTES_URI + "/time?startId=" + gangnamStation + "&endId=" + samsungStation)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists("ETag")
                .expectBody()
                .jsonPath("$.startStationId").isEqualTo(gangnamStation)
                .jsonPath("$.endStationId").isEqualTo(samsungStation)
                .jsonPath("$.stations.size()").isEqualTo(4)
                .jsonPath("$.estimatedTime").isEqualTo(7);
    }

    @DisplayName("지하철역 사이의 실시간 최단시간 경로 조회")
    @Test
    void retrievePathByRealTime() {
        Long expTerminalStation = stationHttpTest.createStation(TestConstant.STATION_NAME_11);
        Long univOfEducationStation = stationHttpTest.createStation(TestConstant.STATION_NAME_12);

        Long thirdLine = lineHttpTest.createLine(TestConstant.LINE_NAME_3);

        lineHttpTest.createEdgeRequest(thirdLine, univOfEducationStation, expTerminalStation, 2);
        lineHttpTest.createEdgeRequest(secondLine, expTerminalStation, gangnamStation, 2);


        webTestClient.get().uri(ROUTES_URI + "/real-time?startId=" + expTerminalStation + "&endId=" + samsungStation)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists("Etag")
                .expectBody()
                .jsonPath("$.startStationId").isEqualTo(expTerminalStation)
                .jsonPath("$.endStationId").isEqualTo(samsungStation)
                .jsonPath("$.stations.size()").isEqualTo(6)
                .jsonPath("$.lines.size()").isEqualTo(2)
                .jsonPath("$.distance").isEqualTo(6)
                .jsonPath("$.departAt").isEqualTo(6)
                .jsonPath("$.arriveBy").isEqualTo(6);
    }
}
