package com.srbenoit.math.delaunay;

/**
 * A site or vertex in the Voronoi diagram.
 */
public class Site extends Vertex implements Comparable<Site> {

    /** the index of the site */
    public int sitenbr;

    /**
     * Constructs a new <code>Site</code>.
     *
     * @param  xCoord  the X coordinate
     * @param  yCoord  the Y coordinate
     * @param  num     the site number
     */
    public Site(final double xCoord, final double yCoord, final int num) {

        super(xCoord, yCoord);

        this.sitenbr = num;
    }

    /**
     * Compares this object with the specified object for order. Returns a negative integer, zero,
     * or a positive integer as this object is less than, equal to, or greater than the specified
     * object.
     *
     * @param   obj  the object to be compared
     * @return  a negative integer, zero, or a positive integer as this object is less than, equal
     *          to, or greater than the specified object.
     */
    public int compareTo(final Site obj) {

        int result;

        if (this.yPos < obj.yPos) {
            result = -1;
        } else if (this.yPos > obj.yPos) {
            result = 1;
        } else if (this.xPos < obj.xPos) {
            result = -1;
        } else if (this.xPos > obj.xPos) {
            result = 1;
        } else {
            result = 0;
        }

        return result;
    }

    /**
     * Tests this object for equality with another object. To be equal, the other object must be a
     * site, and must have the same X and Y coordinates. The site number is not used in comparison.
     *
     * @param  obj  the object to compare
     */
    @Override public boolean equals(final Object obj) {

        Site site;
        boolean equal;

        if (obj instanceof Site) {
            site = (Site) obj;
            equal = (site.xPos == this.xPos) && (site.yPos == this.yPos);
        } else {
            equal = false;
        }

        return equal;
    }

    /**
     * Generates the hash code of the site.
     *
     * @return  the hash code
     */
    @Override public int hashCode() {

        return (int) Double.doubleToLongBits(this.xPos + this.yPos);
    }
}