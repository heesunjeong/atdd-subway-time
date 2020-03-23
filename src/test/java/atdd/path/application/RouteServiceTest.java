package atdd.path.application;

import atdd.TestConstant;
import atdd.path.application.dto.RealTimeRouteResponseView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {RouteService.class})
public class RouteServiceTest {

    @Autowired
    private RouteService routeService;

    @DisplayName("지하철역 사이의 실시간 최단시간 경로 조회")
    @Test
    void findRealTimePath() {

        // when
        RealTimeRouteResponseView response = routeService.findRealTimePath(TestConstant.STATION_ID_11, TestConstant.STATION_ID);

        // then
        assertThat(response.getStations().size()).isEqualTo(6);
        assertThat(response.getLines().size()).isEqualTo(2);
    }
}
