package com.srbenoit.math.delaunay;

/**
 * A hash table to store half-edges.
 */
public class PQ {

    /** the minimum Y coordinate of any site */
    private final double minY;

    /** the height of the bounding box */
    private final double height;

    /** the number of half-edges in the hash table */
    private int count; // NOPMD

    /** the minimum hash bucket */
    private int minBucket;

    /**  */
    private final int hashsize;

    /**  */
    private final Halfedge[] hash;

    /**
     * Constructs a new <code>PQ</code>.
     *
     * @param  sqrtNsites  the square root of (the number of sites + 4)
     * @param  minSiteY    the minimum Y value of any site
     * @param  siteYspan   the total Y height spanned by all sites
     */
    public PQ(final int sqrtNsites, final double minSiteY, final double siteYspan) {

        this.minY = minSiteY;
        this.height = siteYspan;

        this.count = 0;
        this.minBucket = 0;
        this.hashsize = 4 * sqrtNsites;
        this.hash = new Halfedge[this.hashsize];

        for (int i = 0; i < this.hashsize; i += 1) {
            this.hash[i] = new Halfedge(); // NOPMD SRB
        }
    }

    /**
     * Determines the bucket where a half-edge should be stored. The bucket is based on the y*
     * value, and runs from 0 (y* is at the minimum y) to the hash-size (y* is at the maximum y).
     *
     * <p>The minimum hash bucket is updated if the result is smaller than the current minimum.
     *
     * @param   he  the half-edge
     * @return  the bucket
     */
    public int bucket(final Halfedge he) {

        int bucket;

        bucket = (int) ((he.ystar - this.minY) / this.height * this.hashsize);

        if (bucket < 0) {
            bucket = 0;
        }

        if (bucket >= this.hashsize) {
            bucket = this.hashsize - 1;
        }

        if (bucket < this.minBucket) {
            this.minBucket = bucket;
        }

        return bucket;
    }

    /**
     * Adds the HalfEdge to the ordered linked list of vertices in its appropriate bucket.
     *
     * @param  he      the half edge to add
     * @param  v       the vertex the half edge is associated with
     * @param  offset  the offset from y to y* (distance to nearest site)
     */
    public void insert(final Halfedge he, final Site v, final double offset) {

        Halfedge last;
        Halfedge next;

        he.vertex = v;
        he.ystar = v.yPos + offset;

        // Get the first entry in the bucket this half edge should be in
        // (based on its y-start value)
        last = this.hash[bucket(he)];

        // Insert the half edge in the bucket's linked list such that the
        // items in the linked list are sorted by increasing y-star
        next = last.pqNext;

        while ((next != null)
                && ((he.ystar > next.ystar)
                    || ((he.ystar == next.ystar) && (v.xPos > next.vertex.xPos)))) {
            last = next;
            next = last.pqNext;
        }

        he.pqNext = last.pqNext;
        last.pqNext = he;

        this.count += 1;
    }

    /**
     * Removes the HalfEdge from the list of vertices.
     *
     * @param  he  the half edge to remove
     */
    public void delete(final Halfedge he) {

        Halfedge last;

        if (he.vertex != null) {
            last = this.hash[bucket(he)];

            while (last.pqNext != he) {
                last = last.pqNext;
            }

            last.pqNext = he.pqNext;
            this.count -= 1;
            he.vertex = null;
        }
    }

    /**
     * Tests whether the hashtable is empty.
     *
     * @return  <code>true</code> if the hash table is empty
     */
    public boolean empty() {

        return (this.count == 0);
    }

    /**
     * Gets the vertex in the hashtable with the minimum y-star value.
     *
     * @return  the vertex
     */
    public Vertex min() {

        while (this.hash[this.minBucket].pqNext == null) {
            this.minBucket += 1;
        }

        return new Vertex(this.hash[this.minBucket].pqNext.vertex.xPos,
                this.hash[this.minBucket].pqNext.ystar);
    }

    /**
     * Gets the half edge with the minimum y-star value and deletes that half edge from the
     * hashtable.
     *
     * @return  the half-edge
     */
    public Halfedge extractMin() {

        Halfedge curr;

        curr = this.hash[this.minBucket].pqNext;
        this.hash[this.minBucket].pqNext = curr.pqNext;
        this.count -= 1;

        return curr;
    }
}