package com.srbenoit.math.delaunay;

/**
 * A QuadEdge, consisting of four edges linked appropriately. The edges form a cyclical,
 * doubly-linked list, and each edge has a link to the next edge in a counterclockwise edge list
 * around its origin vertex.
 */
public class QuadEdge {

    /** the "canonical" edge in this QuadEdge structure */
    public Edge edge;

    /**
     * Constructs a new <code>QuadEdge</code>, implementing the "MakeEdge" topological operator from
     * Guibas & Stolfi (1985).
     */
    public QuadEdge() {

        Edge edge2;
        Edge edge3;
        Edge edge4;

        this.edge = new Edge();
        edge2 = new Edge();
        edge3 = new Edge();
        edge4 = new Edge();

        this.edge.rotEdge = edge2;
        edge2.rotEdge = edge3;
        edge3.rotEdge = edge4;
        edge4.rotEdge = this.edge;

        this.edge.invrotEdge = edge4;
        edge4.invrotEdge = edge3;
        edge3.invrotEdge = edge2;
        edge2.invrotEdge = this.edge;

        this.edge.next = this.edge;
        edge2.next = edge4;
        edge3.next = edge3;
        edge4.next = edge2;
    }
}