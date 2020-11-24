package com.srbenoit.math.delaunay;

/**
 */
public class Halfedge {

    /**  */
    public Halfedge elLeft;

    /**  */
    public Halfedge elRight;

    /**  */
    public VorEdge elEdge;

    /**  */
    public boolean deleted;

    /**  */
    public int elPm;

    /**  */
    public Site vertex;

    /**  */
    public double ystar;

    /** the next half-edge in the hash table */
    public Halfedge pqNext;

    /**
     * Constructs a new <code>Halfedge</code>.
     */
    public Halfedge() {

        this.pqNext = null;
    }

    /**
     * Constructs a new <code>Halfedge</code>.
     *
     * @param  edge  ???
     * @param  pm    ???
     */
    public Halfedge(final VorEdge edge, final int pm) {

        this();

        this.elEdge = edge;
        this.elPm = pm;
        this.vertex = null;
    }

}