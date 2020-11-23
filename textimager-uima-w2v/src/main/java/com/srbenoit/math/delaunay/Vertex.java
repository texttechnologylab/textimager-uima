package com.srbenoit.math.delaunay;

/**
 * A vertex in a Delaunay triangulation.
 */
public class Vertex {

    /** distance within which we consider a vertex to fall on an edge */
    public static final double EPSILON = 1e-6;

    /** the X coordinate */
    public final double xPos;

    /** the Y coordinate */
    public final double yPos;

    /** flag indicating the vertex is bogus */
    public final boolean isBogus;

    /**
     * Constructs a new <code>Vertex</code>.
     *
     * @param  xCoord  the X coordinate
     * @param  yCoord  the Y coordinate
     */
    public Vertex(final double xCoord, final double yCoord) {

        this(xCoord, yCoord, false);
    }

    /**
     * Constructs a new <code>Vertex</code>.
     *
     * @param  xCoord  the X coordinate
     * @param  yCoord  the Y coordinate
     * @param  bogus   <code>true</code> if the vertex is bogus
     */
    public Vertex(final double xCoord, final double yCoord, final boolean bogus) {

        this.xPos = xCoord;
        this.yPos = yCoord;
        this.isBogus = bogus;
    }

    /**
     * Returns the square of the distance of this vertex from the origin.
     *
     * @return  the distance from the origin squared
     */
    public double lengthSquared() {

        return (this.xPos * this.xPos) + (this.yPos * this.yPos);
    }

    /**
     * Tests whether a point lies to the right of an edge.
     *
     * @param   edge  the edge
     * @return  <code>true</code> if the point lies to the right of the edge; <code>false</code>
     *          otherwise
     */
    public boolean isRightOf(final Edge edge) {

        return isCcw(this, edge.dest(), edge.org());
    }

    /**
     * Tests whether a point lies to the left of an edge.
     *
     * @param   edge  the edge
     * @return  <code>true</code> if the point lies to the left of the edge; <code>false</code>
     *          otherwise
     */
    public boolean isLeftOf(final Edge edge) {

        return isCcw(this, edge.org(), edge.dest());
    }

    /**
     * Tests whether this point lies on an edge (within some small epsilon).
     *
     * @param   edge  the edge
     * @return  <code>true</code> if the point lies on the edge; <code>false</code> otherwise
     */
    public boolean isOnEdge(final Edge edge) {

        Vertex org;
        Vertex dest;
        double tx;
        double ty;
        double len;
        double lineA;
        double lineB;
        double lineC;
        boolean onEdge;

        org = edge.org();
        dest = edge.dest();

        tx = dest.xPos - org.xPos;
        ty = dest.yPos - org.yPos;
        len = Math.sqrt((tx * tx) + (ty * ty));

        lineA = ty / len;
        lineB = -tx / len;
        lineC = -((lineA * org.xPos) + (lineB * org.yPos));

        onEdge = Math.abs((lineA * this.xPos) + (lineB * this.yPos) + lineC) < EPSILON;

        return onEdge;
    }

    /**
     * Tests whether this point lies within the circle circumscribing a triangle.
     *
     * @param   pt1  the first point defining the triangle
     * @param   pt2  the second point defining the triangle
     * @param   pt3  the third point defining the triangle
     * @return  <code>true</code> if this point is inside the circumscribing circle; <code>
     *          false</code> if not
     */
    public boolean isInCircle(final Vertex pt1, final Vertex pt2, final Vertex pt3) {

        return ((pt1.lengthSquared() * triArea(pt2, pt3, this))
                - (pt2.lengthSquared() * triArea(pt1, pt3, this))
                + (pt3.lengthSquared() * triArea(pt1, pt2, this))
                - (this.lengthSquared() * triArea(pt1, pt2, pt3))) > 0;
    }

    /**
     * Generates the string representation of the vertex.
     *
     * @return  the string representation
     */
    @Override public String toString() {

        return "(" + (float) this.xPos + "," + (float) this.yPos + ")";
    }

    /**
     * Computes twice the area of the oriented triangle <code>(pt1, pt2, pt3)</code> (the area is
     * positive if the triangle is oriented counterclockwise).
     *
     * @param   pt1  the first point
     * @param   pt2  the second point
     * @param   pt3  the third point
     * @return  twice the oriented area
     */
    public static double triArea(final Vertex pt1, final Vertex pt2, final Vertex pt3) {

        return ((pt2.xPos - pt1.xPos) * (pt3.yPos - pt1.yPos))
            - ((pt2.yPos - pt1.yPos) * (pt3.xPos - pt1.xPos));
    }

    /**
     * Tests whether the points of a triangle are in counterclockwise order.
     *
     * @param   pt1  the first point
     * @param   pt2  the second point
     * @param   pt3  the third point
     * @return  <code>true</code> if triangle (pt1, pt2, pt3) is in counterclockwise order
     */
    public static boolean isCcw(final Vertex pt1, final Vertex pt2, final Vertex pt3) {

        return triArea(pt1, pt2, pt3) > 0;
    }

    /**
     * Creates a vertex that lies at the center of the circle that circumscribes a triangle.
     *
     * @param   pt1  the first vertex of the triangle
     * @param   pt2  the second vertex of the triangle
     * @param   pt3  the third vertex of the triangle
     * @return  the vertex at the circumcenter
     */
    public static Vertex circumcenter(final Vertex pt1, final Vertex pt2, final Vertex pt3) {

        double len1Sq;
        double len2Sq;
        double len3Sq;
        double denom;
        double xNumer;
        double yNumer;
        Vertex center;

        len1Sq = pt1.lengthSquared();
        len2Sq = pt2.lengthSquared();
        len3Sq = pt3.lengthSquared();

        denom = 2
            * ((pt1.xPos * (pt2.yPos - pt3.yPos)) + (pt2.xPos * (pt3.yPos - pt1.yPos))
                + (pt3.xPos * (pt1.yPos - pt2.yPos)));

        xNumer = (len1Sq * (pt2.yPos - pt3.yPos)) + (len2Sq * (pt3.yPos - pt1.yPos))
            + (len3Sq * (pt1.yPos - pt2.yPos));
        yNumer = (len1Sq * (pt3.xPos - pt2.xPos)) + (len2Sq * (pt1.xPos - pt3.xPos))
            + (len3Sq * (pt2.xPos - pt1.xPos));

        center = new Vertex(xNumer / denom, yNumer / denom);

        return center;
    }
}