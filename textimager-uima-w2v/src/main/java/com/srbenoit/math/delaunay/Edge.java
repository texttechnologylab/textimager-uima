package com.srbenoit.math.delaunay;

/**
 * A single edge in a QuadEdge data structure.
 */
public class Edge {

    /** the next edge of 4 (counterclockwise) in the edge QuadEdge */
    public Edge rotEdge;

    /** the next edge of 4 (clockwise) in the edge QuadEdge */
    public Edge invrotEdge;

    /** the next edge counterclockwise from the origin vertex */
    public Edge next;

    /** data associated with the edge (origin vertex point) */
    public Vertex data;

    /** data associated with the edge (origin vertex point) */
    public boolean voronoi = false;

    /**
     * Gets the edge from the right face to the left face relative to this edge (think of this as
     * the edge that is rotated 90 degrees from this edge).
     *
     * @return  the edge
     */
    public Edge rot() {

        return this.rotEdge;
    }

    /**
     * Gets the edge from the left face to the right face relative to this edge (think of this as
     * the edge that is rotated -90 degrees from this edge).
     *
     * @return  the edge
     */
    public Edge invRot() {

        return this.invrotEdge;
    }

    /**
     * Gets the edge that connects the same two vertices as this edge, but with opposite
     * orientation.
     *
     * @return  the edge
     */
    public Edge sym() {

        return this.rotEdge.rotEdge;
    }

    /**
     * Gets the next edge we find that leaves this edge's origin vertex as we go around that vertex
     * counterclockwise from this edge.
     *
     * @return  the edge
     */
    public Edge oNext() {

        return this.next;
    }

    /**
     * Gets the next edge we find that leaves this edge's origin vertex as we go around that vertex
     * clockwise from this edge.
     *
     * @return  the edge
     */
    public Edge oPrev() {

        return this.rotEdge.next.rotEdge;
    }

    /**
     * Gets the next edge we find that enters this edge's destination vertex as we go around that
     * vertex counterclockwise from this edge.
     *
     * @return  the edge
     */
    public Edge dNext() {

        return this.rotEdge.rotEdge.next.rotEdge.rotEdge;
    }

    /**
     * Gets the next edge we find that enters this edge's destination vertex as we go around that
     * vertex clockwise from this edge.
     *
     * @return  the edge
     */
    public Edge dPrev() {

        return this.invrotEdge.next.invrotEdge;
    }

    /**
     * Gets the edge that follows this edge in a counterclockwise traversal of the face to the left
     * of this edge.
     *
     * @return  the edge
     */
    public Edge lNext() {

        return this.invrotEdge.next.rotEdge;
    }

    /**
     * Gets the edge that precedes this edge in a counterclockwise traversal of the face to the
     * left of this edge.
     *
     * @return  the edge
     */
    public Edge lPrev() {

        return this.next.rotEdge.rotEdge;
    }

    /**
     * Gets the edge that follows this edge in a counterclockwise traversal of the face to the
     * right of this edge (note that such a traversal goes opposite the direction of this edge).
     *
     * @return  the edge
     */
    public Edge rNext() {

        return this.rotEdge.next.invrotEdge;
    }

    /**
     * Gets the edge that precedes this edge in a counterclockwise traversal of the face to the
     * right of this edge (note that such a traversal goes opposite the direction of this edge).
     *
     * @return  the edge
     */
    public Edge rPrev() {

        return this.rotEdge.rotEdge.next;
    }

    /**
     * Gets the data associated with this edge's origin vertex.
     *
     * @return  the origin vertex data
     */
    public Vertex org() {

        return this.data;
    }

    /**
     * Gets the data associated with this edge's destination vertex.
     *
     * @return  the destination vertex data
     */
    public Vertex dest() {

        return this.rotEdge.rotEdge.data;
    }

    /**
     * Gets the data associated with the face to the left of this edge.
     *
     * @return  the left face data
     */
    public Vertex left() {

        return this.rotEdge.data;
    }

    /**
     * Gets the data associated with the face to the right of this edge.
     *
     * @return  the right face data
     */
    public Vertex right() {

        return this.invrotEdge.data;
    }

    /**
     * Sets the end points of this edge. The points of the dual space (faces) can be set using
     * <code>rotEdge().setEndPoints</code>.
     *
     * @param  org   the origin vertex
     * @param  dest  the destination vertex
     */
    public void setEndPoints(final Vertex org, final Vertex dest) {

        this.data = org;
        sym().data = dest;
    }

    /**
     * Generates the string representation of the edge.
     *
     * @return  the string representation
     */
    @Override public String toString() {

        return "{" + org() + ":" + dest() + "}";
    }

    /**
     * Implementation of the Splice topological primitive from Guibas & Stolfi (1985). If the two
     * edges have the same origin vertex (they are parts of different loops that leave that
     * vertex), the vertex is split. If the two edges have different origin vertices, those
     * vertices are merged.
     *
     * @param  edge1  the first edge
     * @param  edge2  the second edge
     */
    public static void splice(final Edge edge1, final Edge edge2) {

        Edge alpha;
        Edge beta;
        Edge tt1;
        Edge tt2;
        Edge tt3;
        Edge tt4;

        alpha = edge1.next.rotEdge;
        beta = edge2.next.rotEdge;

        tt1 = edge2.next;
        tt2 = edge1.next;
        tt3 = beta.next;
        tt4 = alpha.next;

        edge1.next = tt1;
        edge2.next = tt2;
        alpha.next = tt3;
        beta.next = tt4;
    }

    /**
     * Deletes the edge, adjusting the references of surrounding edges as needed.
     */
    public void delete() {

        splice(this, oPrev());
        splice(sym(), sym().oPrev());
    }

    /**
     * Add a new edge connecting the destination of this edge to the origin of <code>edge2</code>
     * in such a way that all three have the same left face after the connection is complete. The
     * data pointers of the new edge are set to the appropriate vertices from the existing edges.
     *
     * @param   edge2  the second edge
     * @return  the new edge
     */
    public Edge connect(final Edge edge2) {

        Edge edge;

        edge = new QuadEdge().edge;
        Edge.splice(edge, lNext());
        Edge.splice(edge.sym(), edge2);
        edge.setEndPoints(dest(), edge2.org());

        return edge;
    }

    /**
     * Turns the edge counterclockwise inside its enclosing quadrilateral, updating data pointers
     * appropriately.
     */
    public void swap() {

        Edge aEdge;
        Edge bEdge;

        aEdge = oPrev();
        bEdge = sym().oPrev();

        Edge.splice(this, aEdge);
        Edge.splice(sym(), bEdge);
        Edge.splice(this, aEdge.lNext());
        Edge.splice(sym(), bEdge.lNext());

        setEndPoints(aEdge.dest(), bEdge.dest());
    }
}