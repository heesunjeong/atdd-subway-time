package atdd.path.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
public class RealTimeRouteResponseView {
    private Long startStationId;
    private Long endStationId;
    private List<ItemView> stations;
    private List<ItemView> lines;
    private int distance;
    private LocalDateTime departAt;
    private LocalDateTime arriveBy;
}
