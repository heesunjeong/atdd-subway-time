package atdd.path.domain;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import java.util.List;

import static atdd.TestConstant.*;

public class GraphTest {
    /*public static final List<Line> LINES = Lists.list(TEST_LINE, TEST_LINE_2, TEST_LINE_3, TEST_LINE_4);

    @Test
    public void createSubwayGraph() {
        Graph graph = new Graph(LINES);

        assertThat(graph.getLines().size()).isEqualTo(4);
    }

    @Test
    public void getPath() {
        Graph graph = new Graph(LINES);

        List<Station> result = graph.getShortestDistancePath(STATION_ID, STATION_ID_3);

        assertThat(result.get(0)).isEqualTo(TEST_STATION);
        assertThat(result.get(2)).isEqualTo(TEST_STATION_3);
    }*/

    @Test
    void getPathByRealTime() {

        Graph graph = new Graph(Lists.list(FULL_LINE1, FULL_LINE2, FULL_LINE3, FULL_LINE4));
        List<Station> result = graph.getShortestTimePathByRealTime(STATION_ID_3, STATION_ID);


    }
}
