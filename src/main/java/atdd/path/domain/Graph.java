package atdd.path.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.graph.WeightedMultigraph;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Graph {
    private List<Line> lines;

    public Graph(List<Line> lines) {
        this.lines = lines;
    }

    public List<Line> getLines() {
        return lines;
    }

    public List<Station> getShortestDistancePath(Long startId, Long endId) {
        return getPathStations(makeGraph(lines), startId, endId);
    }

    private WeightedMultigraph<Long, DefaultWeightedEdge> makeGraph(List<Line> lines) {
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        lines.stream()
                .flatMap(it -> it.getStations().stream())
                .forEach(it -> graph.addVertex(it.getId()));

        lines.stream()
                .flatMap(it -> it.getAllEdges().stream())
                .forEach(it -> graph.setEdgeWeight(graph.addEdge(it.getSourceStation().getId(), it.getTargetStation().getId()), it.getDistance()));
        return graph;
    }

    private List<Station> getPathStations(WeightedMultigraph<Long, DefaultWeightedEdge> graph, Long startId, Long endId) {
        GraphPath<Long, DefaultWeightedEdge> path = new DijkstraShortestPath(graph).getPath(startId, endId);

        return path.getVertexList().stream()
                .map(it -> findStation(it))
                .collect(Collectors.toList());
    }

    private Station findStation(Long stationId) {
        return lines.stream()
                .flatMap(it -> it.getStations().stream())
                .filter(it -> it.getId() == stationId)
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    public List<Station> getShortestTimePath(Long startId, Long endId) {
        return getPathStations(makeGraphForTime(lines), startId, endId);
    }

    private WeightedMultigraph<Long, DefaultWeightedEdge> makeGraphForTime(List<Line> lines) {
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        lines.stream()
                .flatMap(it -> it.getStations().stream())
                .forEach(it -> graph.addVertex(it.getId()));

        lines.stream()
                .flatMap(it -> it.getAllEdges().stream())
                .forEach(it -> graph.setEdgeWeight(graph.addEdge(it.getSourceStation().getId(), it.getTargetStation().getId()), it.getElapsedTime()));
        return graph;
    }

    public int getEstimatedTime(Long startId, Long endId) {
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = makeGraphForTime(lines);
        GraphPath<Long, DefaultWeightedEdge> path = new DijkstraShortestPath(graph).getPath(startId, endId);

        return path.getEdgeList().stream()
                .map(it -> findEdge(graph.getEdgeSource(it), graph.getEdgeTarget(it)))
                .mapToInt(Edge::getElapsedTime)
                .sum();
    }


    private Edge findEdge(Long edgeSource, Long edgeTarget) {
        return lines.stream()
                .flatMap(it -> it.getAllEdges().stream())
                .filter(it -> it.getSourceStation().getId().equals(edgeSource)
                        && it.getTargetStation().getId().equals(edgeTarget))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    private Line findLine(Long lineId) {
        Map<Long, Line> collect = lines.stream()
                .collect(Collectors.toMap(Line::getId, Function.identity()));
        return collect.get(lineId);
    }

    public List<Station> getShortestTimePathByRealTime(Long startId, Long endId) {
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = makeGraphForTime(lines);
        KShortestPaths paths = new KShortestPaths(graph, 20);
        List<GraphWalk<Long, DefaultWeightedEdge>> allPaths = paths.getPaths(startId, endId);

        Map<GraphWalk, LocalDateTime> routeWeights = new HashMap();
        Set<Edge> edges = new HashSet<>();
        LocalDateTime currentTime = LocalDateTime.now();

        for (GraphWalk<Long, DefaultWeightedEdge> allPath : allPaths) {
            LocalDateTime passedTime = currentTime;
            List<DefaultWeightedEdge> edgeList = allPath.getEdgeList();

            for (DefaultWeightedEdge defaultWeightedEdge : edgeList) {
                Edge edge = findEdge(graph.getEdgeSource(defaultWeightedEdge), graph.getEdgeTarget(defaultWeightedEdge));
                edges.add(edge);
                passedTime = getDepartTime(edge, passedTime);
            }

            routeWeights.put(allPath, passedTime);
        }

        GraphWalk key = routeWeights.entrySet().stream().min(Comparator.comparing(Map.Entry::getValue)).get().getKey();
        return edges.stream().flatMap(it -> Stream.of(it.getSourceStation(), it.getTargetStation())).collect(Collectors.toList());
    }

    private LocalDateTime getDepartTime(Edge edge, LocalDateTime passedTime) {
        LocalTime currentTime = passedTime.toLocalTime();
        Station targetStation = edge.getTargetStation();
        Line line = edge.getLine();

        return targetStation.getEarliestTime(line, currentTime).isBefore(currentTime)
                ? passedTime.toLocalDate().plusDays(1).atTime(targetStation.getEarliestTime(line, currentTime))
                : passedTime.toLocalDate().atTime(targetStation.getEarliestTime(line, currentTime));
    }
}
