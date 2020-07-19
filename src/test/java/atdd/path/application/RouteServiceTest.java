package atdd.path.application;

import atdd.TestConstant;
import atdd.path.application.dto.RealTimeRouteResponseView;
import atdd.path.domain.Line;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static atdd.TestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {RouteService.class})
public class RouteServiceTest {

    @Autowired
    private RouteService routeService;

    @DisplayName("지하철역 사이의 실시간 최단시간 경로 조회")
    @Test
    void findRealTimePath() {
        List<Line> lines = Lists.list(TEST_LINE, TEST_LINE_2, TEST_LINE_3, TEST_LINE_4);
        LocalDateTime departureTime = LocalDateTime.now();

        //given(lineRepository.findAll()).willReturn()

        // when
        RealTimeRouteResponseView response = routeService.findRealTimePath(TestConstant.STATION_ID_11, TestConstant.STATION_ID, departureTime);

        // then
        assertThat(response.getStations().size()).isEqualTo(6);
        assertThat(response.getLines().size()).isEqualTo(2);
    }
}
