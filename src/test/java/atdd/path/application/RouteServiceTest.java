package atdd.path.application;

import atdd.path.application.dto.RealTimeRouteResponseView;
import atdd.path.application.dto.RouteResponseDto;
import atdd.path.application.dto.StationResponseDto;
import atdd.path.repository.LineRepository;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static atdd.TestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RouteServiceTest {

    @Autowired
    private RouteService routeService;
    @Mock
    private LineRepository lineRepository;

    @BeforeEach
    void setUp() {
        this.routeService = new RouteService(lineRepository);

        given(lineRepository.findAll()).willReturn(Lists.list(TEST_LINE, TEST_LINE_2, TEST_LINE_3, TEST_LINE_4));
    }

    @DisplayName("지하철역 사이의 최단 시간 경로 조회")
    @Test
    void findShortestTimePath() {
        RouteResponseDto response = routeService.findShortestTimePath(STATION_ID_12, STATION_ID_6);

        assertThat(response.getStations()).size().isEqualTo(3);
        assertThat(response.getStations()).extracting(StationResponseDto::getId).containsExactly(12L, 1L, 6L);
        assertThat(response.getEstimatedTime()).isEqualTo(3);
    }

    @DisplayName("지하철역 사이의 최단 거리 경로 조회")
    @Test
    void findShortestDistancePath() {
        RouteResponseDto response = routeService.findShortestDistancePath(STATION_ID_12, STATION_ID_6);

        assertThat(response.getStations()).size().isEqualTo(3);
        assertThat(response.getStations()).extracting(StationResponseDto::getId).containsExactly(12L, 13L, 6L);
        assertThat(response.getEstimatedTime()).isEqualTo(3);
    }

    @DisplayName("지하철역 사이의 실시간 최단시간 경로 조회")
    @Test
    void findRealTimePath() {
        // given
        LocalDateTime departureTime = LocalDateTime.now();

        // when
        RealTimeRouteResponseView response = routeService.findRealTimePath(STATION_ID, STATION_ID_3, departureTime);

        // then
        assertThat(response.getStations().size()).isEqualTo(6);
        assertThat(response.getLines().size()).isEqualTo(2);
    }
}
