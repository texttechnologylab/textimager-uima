package com.srbenoit.math.delaunay;

/**
 */
public class EL {

    /** array index of left edge */
    private final static int LEFTEDGE = 0;

    /** array index of right edge */
    private final static int RIGHTEDGE = 1;

    /** the minimum Y coordinate of any site */
    private final double minX;

    /** the height of the bounding box */
    private final double width;

    /** the length of the hash table */
    private final int hashsize;

    /** the hash table */
    private Halfedge[] hash;

    /** the leftmost element in the list */
    public Halfedge leftEnd;

    /** the rightmost element in the list */
    public Halfedge rightEnd;

    /**
     * Constructs a new <code>EL</code>.
     *
     * @param  sqrtNsites  the square root of (the number of sites + 4)
     * @param  minSiteX    the minimum X value of any site
     * @param  siteXspan   the total X width spanned by all sites
     */
    public EL(final int sqrtNsites, final double minSiteX, final double siteXspan) {

        this.minX = minSiteX;
        this.width = siteXspan;

        this.hashsize = 2 * sqrtNsites;
        this.hash = new Halfedge[this.hashsize];

        for (int i = 0; i < this.hashsize; i += 1) {
            this.hash[i] = null;
        }

        this.leftEnd = new Halfedge(null, 0);
        this.rightEnd = new Halfedge(null, 0);

        this.leftEnd.elLeft = null;
        this.leftEnd.elRight = this.rightEnd;

        this.rightEnd.elLeft = this.leftEnd;
        this.rightEnd.elRight = null;

        this.hash[0] = this.leftEnd;
        this.hash[this.hashsize - 1] = this.rightEnd;
    }

    /**
     * Inserts an edge in the linked list to the right of a specified edge.
     *
     * @param  lb     the edge that should be to the left of the inserted edge
     * @param  newHe  the edge to insert
     */
    public void insert(final Halfedge lb, final Halfedge newHe) {

        newHe.elLeft = lb;
        newHe.elRight = lb.elRight;
        lb.elRight.elLeft = newHe;
        lb.elRight = newHe;
    }

    /**
     * Deletes an edge from the linked list.
     *
     * @param  edge  the edge to delete
     */
    public void delete(final Halfedge edge) {

        edge.elLeft.elRight = edge.elRight;
        edge.elRight.elLeft = edge.elLeft;
        edge.deleted = true;
    }

    /**
     * Gets entry from hash table, pruning any deleted nodes.
     *
     * @param   index  the index in the hash table
     * @return  the found entry
     */
    public Halfedge gethash(final int index) {

        Halfedge edge;

        if ((index < 0) || (index >= this.hashsize)) {
            edge = null;
        } else {
            edge = this.hash[index];

            if ((edge != null) && edge.deleted) {
                this.hash[index] = null;
                edge = null;
            }
        }

        return edge;
    }

    /**
     * ???
     *
     * @param   vert  the vertex
     * @return  the located half-edge
     */
    public Halfedge leftbnd(final Vertex vert) {

        int bucket;
        Halfedge he;

        // identify the bucket near the point based on it's X coordinate
        bucket = (int) ((vert.xPos - this.minX) / this.width * this.hashsize);

        // check the bucket range
        if (bucket < 0) {
            bucket = 0;
        } else if (bucket >= this.hashsize) {
            bucket = this.hashsize - 1;
        }

        he = gethash(bucket);

        if (he == null) {

            // if the HE isn't found, search backwards and forwards in the hash
            // map for the first non-null entry
            for (int i = 1; i < this.hashsize; i += 1) {

                if ((he = gethash(bucket - i)) != null) {
                    break;
                }

                if ((he = gethash(bucket + i)) != null) {
                    break;
                }
            }
        }

        if (he != null) {

            // search linear list of half edges for the correct one
            if ((he == this.leftEnd) || ((he != this.rightEnd) && rightOf(he, vert))) {

                // keep going right on the list until either the end is
                // reached, or you find the 1st edge which the point isn't to
                // the right of
                do {
                    he = he.elRight;
                } while ((he != this.rightEnd) && rightOf(he, vert));

                he = he.elLeft;
            } else {

                // if the point is to the left of the HalfEdge, then search
                // left for the HE just to the left of the point
                do {
                    he = he.elLeft;
                } while ((he != this.leftEnd) && !rightOf(he, vert));
            }
        }

        // update hash table and reference counts
        if ((bucket > 0) && (bucket < (this.hashsize - 1))) {
            this.hash[bucket] = he;
        }

        return he;
    }

    /**
     * Tests if p is to right of half edge e.
     *
     * @param   el     the half sedge
     * @param   point  the point
     * @return  <code>true</code> if <code>point</code> is to the right of <code>e</code>
     */
    private boolean rightOf(final Halfedge el, final Vertex point) {

        VorEdge e;
        Site topsite;
        boolean rightOfSite;
        boolean above;
        boolean fast;
        double dxp;
        double dyp;
        double dxs;
        double t1;
        double t2;
        double t3;
        double yl;
        boolean result;

        e = el.elEdge;
        topsite = e.reg[1];
        rightOfSite = (point.xPos > topsite.xPos);

        if (rightOfSite && (el.elPm == LEFTEDGE)) {
            result = true;
        } else if (!rightOfSite && (el.elPm == RIGHTEDGE)) {
            result = false;
        } else {

            if (e.aParam == 1.0) {
                dyp = point.yPos - topsite.yPos;
                dxp = point.xPos - topsite.xPos;
                fast = false;

                if ((!rightOfSite & (e.bParam < 0.0)) | (rightOfSite & (e.bParam >= 0.0))) {
                    above = dyp >= (e.bParam * dxp);
                    fast = above;
                } else {
                    above = (point.xPos + (point.yPos * e.bParam)) > e.cParam;

                    if (e.bParam < 0.0) {
                        above = !above;
                    }

                    if (!above) {
                        fast = true;
                    }
                }

                if (!fast) {
                    dxs = topsite.xPos - (e.reg[0]).xPos;
                    above = (e.bParam * ((dxp * dxp) - (dyp * dyp)))
                        < (dxs * dyp * (1.0 + (2.0 * dxp / dxs) + (e.bParam * e.bParam)));

                    if (e.bParam < 0.0) {
                        above = !above;
                    }
                }
            } else {
                yl = e.cParam - (e.aParam * point.xPos);
                t1 = point.yPos - yl;
                t2 = point.xPos - topsite.xPos;
                t3 = yl - topsite.yPos;
                above = (t1 * t1) > ((t2 * t2) + (t3 * t3));
            }

            result = (el.elPm == LEFTEDGE) ? above : (!above);
        }

        return result;
    }
}