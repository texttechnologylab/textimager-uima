package com.srbenoit.math.delaunay;
//package com.srbenoit.math.delaunay;
//
//import java.awt.geom.Path2D;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//import java.util.logging.Level;
//import com.srbenoit.log.LoggedObject;
//
///**
// * A subdivision of the plane by edges to form a Delaunay triangulation.
// */
//public class Delaunay extends LoggedObject {
//
//    /** the number of points to generate in the random set */
//    private static final int NUM_POINTS = 250;
//
//    /** a visualizer that will display results as triangulation proceeds */
//    private final DelaunayVisualizerInt visualizer;
//
//    /** the original list of points */
//    private final Vertex[] origPoints;
//
//    /** a starting edge for the subdivision */
//    private Edge startingEdge;
//
//    /** the list of all edges in the triangulation */
//    public final List<Edge> delEdges;
//
//    /** the list of all edges in the Voronoi diagram */
//    public final List<GraphEdge> vorEdges;
//
//    /** a map from vertex to the Voronoi region containing it */
//    public Map<Vertex, Path2D> vorMap;
//
//    /**
//     * Constructs a new subdivision consisting of a single triangle that contains all points in a
//     * points list.
//     *
//     * @param  points  the points list
//     * @param  vis     a visualizer that will display results as triangulation proceeds (may be
//     *                 <code>null</code>)
//     */
//    public Delaunay(final Vertex[] points, final DelaunayVisualizerInt vis) {
//
//        double max;
//        double xPos;
//        double yPos;
//
//        max = 0;
//
//        for (int i = 0; i < points.length; i++) {
//            xPos = Math.abs(points[i].xPos);
//            yPos = Math.abs(points[i].yPos);
//
//            if (xPos > max) {
//                max = xPos;
//            }
//
//            if (yPos > max) {
//                max = yPos;
//            }
//        }
//
//        this.origPoints = points.clone();
//        this.visualizer = vis;
//        this.delEdges = new ArrayList<Edge>(points.length + 10);
//        this.vorEdges = new ArrayList<GraphEdge>(points.length + 10);
//
//        // We mark these three points as with asPoint = false so we can
//        // recognize which points belong to this surrounding triangle later
//        // Note: we use 3.1 rather than 3.0 since 3.0 admits the possibility
//        // that a point falls on the edge of the surrounding triangle, and
//        // we can have all points inside at no cost.
//        set(new Vertex(3000 * max, 0, true), new Vertex(0, 3000 * max, true),
//            new Vertex(-3000 * max, -3000 * max, true));
//    }
//
//    /**
//     * Initializes the subdivision to contain a single triangle formed by <code>point1</code>,
//     * <code>point2</code>, and <code>point3</code>.
//     *
//     * @param  point1  the first point
//     * @param  point2  the second point
//     * @param  point3  the third point
//     */
//    private void set(final Vertex point1, final Vertex point2, final Vertex point3) {
//
//        Edge ea;
//        Edge eb;
//        Edge ec;
//
//        ea = new QuadEdge().edge;
//        ea.setEndPoints(point1, point2);
//
//        eb = new QuadEdge().edge;
//        Edge.splice(ea.sym(), eb);
//        eb.setEndPoints(point2, point3);
//
//        ec = new QuadEdge().edge;
//        Edge.splice(eb.sym(), ec);
//        ec.setEndPoints(point3, point1);
//
//        Edge.splice(ec.sym(), ea);
//        this.startingEdge = ea;
//
//        this.delEdges.add(ea);
//        this.delEdges.add(eb);
//        this.delEdges.add(ec);
//    }
//
//    /**
//     * Computes the delaunay triangulation of the points used when constructing the subdivision.
//     */
//    public void compute() {
//
//        Edge edge;
//        Voronoi vor;
//
//        notify("Beginning triangulation");
//
//        for (Vertex vert : this.origPoints) {
//            this.insertSite(vert);
//        }
//
//        notify("Pruning bogus edges");
//
//        // Remove all edges that touch a bogus vertex
//        for (int i = this.delEdges.size() - 1; i >= 0; i--) {
//            edge = this.delEdges.get(i);
//
//            if (edge.org().isBogus || edge.dest().isBogus) {
//                edge.delete();
//                this.delEdges.remove(i);
//            }
//        }
//
//        notify("Computing Voronoi diagram");
//
//        vor = new Voronoi(1e-6);
//        this.vorEdges.addAll(vor.generateVoronoi(this.origPoints));
//        this.vorMap = vor.makeVoronoiPolygons(this.origPoints, this.vorEdges);
//
//        notify("Finished");
//    }
//
//    /**
//     * Inserts a new vertex into a subdivision representing a Delaunay triangulation, and fixes the
//     * affected edges so the result is still a Delaunay triangulation.
//     *
//     * @param  vert  the vertex to insert
//     */
//    public void insertSite(final Vertex vert) {
//
//        Edge edge;
//        Edge base;
//        Edge test;
//        Vertex first;
//
//        edge = locate(vert);
//
//        if ((vert != edge.org()) && (vert != edge.dest())) {
//
//            if (vert.isOnEdge(edge)) {
//                test = edge.oPrev();
//                edge.delete();
//                this.delEdges.remove(edge);
//                edge = test;
//            }
//
//            // Connect the new point to the vertices of the containing triangle
//            // (or quadrilateral, if the new point fell on an existing edge)
//            base = new QuadEdge().edge;
//            this.delEdges.add(base);
//            first = edge.org();
//            base.setEndPoints(first, vert);
//            Edge.splice(base, edge);
//
//            base = edge.connect(base.sym());
//            this.delEdges.add(base);
//            edge = base.oPrev();
//
//            while (edge.dest() != first) {
//                base = edge.connect(base.sym());
//                this.delEdges.add(base);
//                edge = base.oPrev();
//            }
//
//            // Examine suspect edges to ensure that the Delaunay condition is
//            // satisfied
//            for (;;) {
//                test = edge.oPrev();
//
//                if (test.dest().isRightOf(edge)
//                        && vert.isInCircle(edge.org(), test.dest(), edge.dest())) {
//                    edge.swap();
//
//                    // edge = test; // Documented bug
//                    edge = edge.oPrev();
//                } else if (edge.org() == first) {
//                    break;
//                }
//
//                edge = edge.oNext().lPrev();
//            }
//        }
//    }
//
//    /**
//     * Returns an edge <code>e</code> such that either <code>point</code> is on <code>e</code> or
//     * <code>e</code> is an edge of a triangle containing <code>point</code>. The search starts
//     * from <code>startingEdge</code> and proceeds in the general direction of <code>point</code>.
//     *
//     * @param   point  the point
//     * @return  the edge
//     */
//    public Edge locate(final Vertex point) {
//
//        Edge edge;
//
//        edge = this.startingEdge;
//
//        for (;;) {
//
//            if ((point == edge.org()) || (point == edge.dest())) {
//                break;
//            }
//
//            if (point.isRightOf(edge)) {
//                edge = edge.sym();
//            } else if (point.isLeftOf(edge.oNext())) {
//                edge = edge.oNext();
//            } else if (point.isLeftOf(edge.dPrev())) {
//                edge = edge.dPrev();
//            } else {
//                break;
//            }
//        }
//
//        return edge;
//    }
//
//    /**
//     * If there is a visualizer registered, sends a notification to that visualizer to update its
//     * view, and optionally pause before proceeding.
//     *
//     * @param  phase  a string description of the phase just completed
//     */
//    private void notify(final String phase) {
//
//        if (this.visualizer != null) {
//            this.visualizer.update(this.origPoints, this, phase);
//        }
//    }
//
//    /**
//     * Main method that generates a random set of points, builds a visualizer and a Delaunay
//     * triangulator, then steps through the process asking the user for a button press after each
//     * step to move on.
//     *
//     * @param  args  command-line arguments
//     */
//    public static void main(final String... args) {
//
//        Random rnd;
//        Vertex[] points;
//        DelaunayViewer viewer;
//        Delaunay triangulator;
//        double xPos;
//        double yPos;
//
//        rnd = new Random(System.currentTimeMillis());
//        points = new Vertex[NUM_POINTS];
//
//        for (int i = 0; i < NUM_POINTS; i++) {
//            xPos = (rnd.nextDouble() - 0.5);
//            yPos = (rnd.nextDouble() - 0.5);
//            points[i] = new Vertex(xPos, yPos); // NOPMD SRB
//        }
//
//        try {
//            viewer = new DelaunayViewer();
//
//            triangulator = new Delaunay(points, viewer);
//            triangulator.compute();
//        } catch (Exception e) {
//            LOG.log(Level.WARNING, "Exception while creating Delaunay triangulation", e);
//        }
//    }
//}