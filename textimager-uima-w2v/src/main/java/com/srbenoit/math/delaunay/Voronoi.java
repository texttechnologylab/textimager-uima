package com.srbenoit.math.delaunay;

import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A class to compute Voronoi diagrams.
 */
public class Voronoi {

    /** array index of left edge */
    private final static int LEFTEDGE = 0;

    /** array index of right edge */
    private final static int RIGHTEDGE = 1;

    /** a distance within which we consider points equal */
    private final double epsilon;

    /** the minimum distance between sites */
    private final double minSiteDist;

    /** the minimum X coordinate of any site */
    private double minX;

    /** the maximum X coordinate of any site */
    private double maxX;

    /** the minimum Y coordinate of any site */
    private double minY;

    /** the maximum Y coordinate of any site */
    private double maxY;

    /** the width of the bounding box */
    private double width;

    /** the height of the bounding box */
    private double height;

    /** a hash table to store half-edges */
    private PQ pq;

    /** a hash table to store ??? */
    private EL el;

    /** the number of sites */
    private int nsites;

    /** the square root of (the number of sites + 4) */
    private int sqrtNsites;

    /** the sorted list of sites */
    private Site[] sites;

    /** the index of the site being processed */
    private int siteidx;

    /** the number of vertices */
    private int nvertices; // NOPMD

    /** the number of edges */
    private int nedges; // NOPMD

    /** ??? */
    private Site bottomsite;

    /** the set of all edges in the graph */
    private final List<GraphEdge> allEdges;

    /**
     * Constructs a new <code>Voronoi</code>.
     *
     * @param  minSiteSpacing  the minimum distance between sites - any sites closer than this
     *                         distance will be considered a single site
     */
    public Voronoi(final double minSiteSpacing) {

        this.siteidx = 0;
        this.sites = null;

        this.minSiteDist = minSiteSpacing;
        this.epsilon = minSiteSpacing / 100.0;
        this.nvertices = 0;
        this.nedges = 0;
        this.nsites = 0;
        this.sqrtNsites = 0;

        this.allEdges = new LinkedList<GraphEdge>();
    }

    /**
     * Generates the Voronoi diagram using Fortune's algorithm.
     *
     * @param   points  an array of coordinates for each site
     * @return  the list of graph edges in the Voronoi diagram
     */
    public List<GraphEdge> generateVoronoi(final Vertex[] points) {

        GraphEdge edge;

        // Remove any prior run's output
        this.allEdges.clear();

        // Compute bounding box (only used to clip edges in the future)
        computeBounds(points);

        // Create a list of sites from the input list of points that is sorted
        // by increasing Y coordinate (and increasing X for values of equal Y)
        makeSortedSiteList(points);

        // Build the Voronoi diagram
        voronoiBuild();

        // Remove any edges whose end and start points are the same
        for (int i = this.allEdges.size() - 1; i >= 0; i--) {
            edge = this.allEdges.get(i);

            if (isSame(edge.xPos1, edge.yPos1, edge.xPos2, edge.yPos2)) {
                this.allEdges.remove(i);
            }
        }

        return this.allEdges;
    }

    /**
     * Computes the bounding box of a set of points.
     *
     * @param  points  the points
     */
    private void computeBounds(final Vertex[] points) {

        this.minX = Double.MAX_VALUE;
        this.minY = Double.MAX_VALUE;
        this.maxX = -Double.MAX_VALUE;
        this.maxY = -Double.MAX_VALUE;

        for (Vertex vert : points) {

            if (vert.xPos < this.minX) {
                this.minX = vert.xPos;
            }

            if (vert.xPos > this.maxX) {
                this.maxX = vert.xPos;
            }

            if (vert.yPos < this.minY) {
                this.minY = vert.yPos;
            }

            if (vert.yPos > this.maxY) {
                this.maxY = vert.yPos;
            }
        }

        this.height = this.maxY - this.minY;
        this.width = this.maxX - this.minX;
    }

    /**
     * Creates a list of <code>Site</code> objects whose coordinates are a given set of points, and
     * that is sorted in order of increasing Y (then increasing X if Y coordinates are the same)
     *
     * @param  points  the list of points
     */
    private void makeSortedSiteList(final Vertex[] points) {

        this.nsites = points.length;
        this.sqrtNsites = (int) Math.sqrt(this.nsites + 4);

        this.sites = new Site[this.nsites];
        this.siteidx = 0;

        for (int i = 0; i < this.nsites; i++) {
            this.sites[i] = new Site(points[i].xPos, points[i].yPos, i); // NOPMD SRB
        }

        Arrays.sort(this.sites);
    }

    /**
     * Gets the next site to be processed.
     *
     * @return  the next site; <code>null</code> if there are no more sites
     */
    private Site nextOne() {

        Site site;

        if (this.siteidx < this.nsites) {
            site = this.sites[this.siteidx];
            this.siteidx += 1;
        } else {
            site = null;
        }

        return site;
    }

    /**
     * Constructs the bisector between two sites, as a line with equation ax + by = cParam.
     *
     * <p>The midpoint of the line is (s1 + s2)/2.
     *
     * <p>For a line changing more in Y than in X, we seek a formula for the bisector of the form
     * ax + y = cParam, and for a line changing more in X than in Y, we seek a formula for the
     * bisector of the form x + by = cParam.
     *
     * <p>For |dy|>|dx|, the slope of the bisector is -dx/dy, and so the equation of the line is
     *
     * <pre>
     * y - (yPos1+yPos2)/2 = (-dx/dy)[x - (xPos1+xPos2)/2]
     * (dx/dy)x + y = [(dx/dy)(xPos1+xPos2) + (yPos1+yPos2)]/2
     * (dx/dy)x + y = [((xPos2-xPos1)(xPos1+xPos2)/(yPos2-yPos1)) + (yPos1+yPos2)]/2
     * (dx/dy)x + y = [xPos2^2 - xPos1^2 + yPos2^2 - yPos1^2] / 2dy
     * </pre>
     *
     * <p>For |dx|>|dy|, the y-x slope of the bisector is -dy/dx, and so the equation of the line
     * is
     *
     * <pre>
     * x - (xPos1+xPos2)/2 = (-dy/dx)[y - (yPos1+yPos2)/2]
     * x + (dy/dx)y = [(dy/dx)(yPos1+yPos2) + (xPos1+xPos2)]/2
     * x + (dy/dx)y = [((yPos2-yPos1)/(xPos2-xPos1))(yPos1+yPos2) + (xPos1+xPos2)]/2
     * x + (dy/dx)y = [xPos2^2 - xPos1^2 + yPos2^2 - yPos1^2] / 2dx
     * </pre>
     *
     * @param   site1  the first site
     * @param   site2  the second site
     * @return  the bisector
     */
    private VorEdge bisect(final Site site1, final Site site2) {

        double distX;
        double distY;
        double adx;
        double ady;
        double temp;
        VorEdge newedge;

        newedge = new VorEdge();

        // store the sites that this edge is bisecting
        newedge.reg[0] = site1;
        newedge.reg[1] = site2;

        // to begin with, there are no endpoints on the bisector - it goes to
        // infinity
        newedge.endPoints[0] = null;
        newedge.endPoints[1] = null;

        // vector from site 1 to site 2
        distX = site2.xPos - site1.xPos;
        distY = site2.yPos - site1.yPos;

        // make sure that the difference in positive
        adx = (distX > 0) ? distX : -distX;
        ady = (distY > 0) ? distY : -distY;

        temp = ((site2.xPos * site2.xPos) - (site1.xPos * site1.xPos) + (site2.yPos * site2.yPos)
                - (site1.yPos * site1.yPos)) * 0.5;

        if (adx > ady) {
            newedge.aParam = 1.0f;
            newedge.bParam = distY / distX;
            newedge.cParam = temp / distX;
        } else {
            newedge.bParam = 1.0f;
            newedge.aParam = distX / distY;
            newedge.cParam = temp / distY;
        }

        newedge.edgeNumber = this.nedges;
        this.nedges += 1;

        return newedge;
    }

    /**
     * ???
     *
     * @param   he  ???
     * @return  ???
     */
    private Site leftreg(final Halfedge he) {

        if (he.elEdge == null) {
            return this.bottomsite;
        }

        return (he.elPm == LEFTEDGE) ? he.elEdge.reg[LEFTEDGE] : he.elEdge.reg[RIGHTEDGE];
    }

    /**
     * ???
     *
     * @param  leftSite   ???
     * @param  rightSite  ???
     * @param  xPos1      ???
     * @param  yPos1      ???
     * @param  xPos2      ???
     * @param  yPos2      ???
     */
    private void pushGraphEdge(final Site leftSite, final Site rightSite, final double xPos1,
        final double yPos1, final double xPos2, final double yPos2) {

        GraphEdge newEdge;

        newEdge = new GraphEdge();
        this.allEdges.add(newEdge);

        newEdge.xPos1 = xPos1;
        newEdge.yPos1 = yPos1;
        newEdge.xPos2 = xPos2;
        newEdge.yPos2 = yPos2;

        newEdge.site1 = leftSite.sitenbr;
        newEdge.site2 = rightSite.sitenbr;
    }

    /**
     * Clips a line to the bounding box.
     *
     * @param  edge  the edge to clip
     */
    private void clipLine(final VorEdge edge) {

        double pxmin, pxmax, pymin, pymax;
        Site site1;
        Site site2;
        double xPos1 = 0;
        double xPos2 = 0;
        double yPos1 = 0;
        double yPos2 = 0;

        xPos1 = edge.reg[0].xPos;
        xPos2 = edge.reg[1].xPos;
        yPos1 = edge.reg[0].yPos;
        yPos2 = edge.reg[1].yPos;

        // if the distance between the two points this line was created from is
        // less than the square root of 2, then ignore it
        if (Math.sqrt(((xPos2 - xPos1) * (xPos2 - xPos1)) + ((yPos2 - yPos1) * (yPos2 - yPos1)))
                < this.minSiteDist) {
            return;
        }

        pxmin = this.minX;
        pxmax = this.maxX;
        pymin = this.minY;
        pymax = this.maxY;

        if ((edge.aParam == 1.0) && (edge.bParam >= 0.0)) {
            site1 = edge.endPoints[1];
            site2 = edge.endPoints[0];
        } else {
            site1 = edge.endPoints[0];
            site2 = edge.endPoints[1];
        }

        if (edge.aParam == 1.0) {
            yPos1 = pymin;

            if ((site1 != null) && (site1.yPos > pymin)) {
                yPos1 = site1.yPos;
            }

            if (yPos1 > pymax) {
                yPos1 = pymax;
            }

            xPos1 = edge.cParam - (edge.bParam * yPos1);
            yPos2 = pymax;

            if ((site2 != null) && (site2.yPos < pymax)) {
                yPos2 = site2.yPos;
            }

            if (yPos2 < pymin) {
                yPos2 = pymin;
            }

            xPos2 = (edge.cParam) - ((edge.bParam) * yPos2);

            if (((xPos1 > pxmax) & (xPos2 > pxmax)) | ((xPos1 < pxmin) & (xPos2 < pxmin))) {
                return;
            }

            if (xPos1 > pxmax) {
                xPos1 = pxmax;
                yPos1 = (edge.cParam - xPos1) / edge.bParam;
            }

            if (xPos1 < pxmin) {
                xPos1 = pxmin;
                yPos1 = (edge.cParam - xPos1) / edge.bParam;
            }

            if (xPos2 > pxmax) {
                xPos2 = pxmax;
                yPos2 = (edge.cParam - xPos2) / edge.bParam;
            }

            if (xPos2 < pxmin) {
                xPos2 = pxmin;
                yPos2 = (edge.cParam - xPos2) / edge.bParam;
            }
        } else {
            xPos1 = pxmin;

            if ((site1 != null) && (site1.xPos > pxmin)) {
                xPos1 = site1.xPos;
            }

            if (xPos1 > pxmax) {
                xPos1 = pxmax;
            }

            yPos1 = edge.cParam - (edge.aParam * xPos1);
            xPos2 = pxmax;

            if ((site2 != null) && (site2.xPos < pxmax)) {
                xPos2 = site2.xPos;
            }

            if (xPos2 < pxmin) {
                xPos2 = pxmin;
            }

            yPos2 = edge.cParam - (edge.aParam * xPos2);

            if (((yPos1 > pymax) & (yPos2 > pymax)) | ((yPos1 < pymin) & (yPos2 < pymin))) {
                return;
            }

            if (yPos1 > pymax) {
                yPos1 = pymax;
                xPos1 = (edge.cParam - yPos1) / edge.aParam;
            }

            if (yPos1 < pymin) {
                yPos1 = pymin;
                xPos1 = (edge.cParam - yPos1) / edge.aParam;
            }

            if (yPos2 > pymax) {
                yPos2 = pymax;
                xPos2 = (edge.cParam - yPos2) / edge.aParam;
            }

            if (yPos2 < pymin) {
                yPos2 = pymin;
                xPos2 = (edge.cParam - yPos2) / edge.aParam;
            }
        }

        pushGraphEdge(edge.reg[0], edge.reg[1], xPos1, yPos1, xPos2, yPos2);
    }

    /**
     * ???
     *
     * @param  edge  ???
     * @param  lr    ???
     * @param  site  ???
     */
    private void endpoint(final VorEdge edge, final int lr, final Site site) {

        edge.endPoints[lr] = site;

        if (edge.endPoints[RIGHTEDGE - lr] == null) {
            return;
        }

        clipLine(edge);
    }

    /**
     * ???
     *
     * @param   he  ???
     * @return  ???
     */
    private Site rightreg(final Halfedge he) {

        if (he.elEdge == null) {

            // if this half edge has no edge, return the bottom site (whatever
            // that is)
            return this.bottomsite;
        }

        // if the elPm field is zero, return the site 0 that this edge bisects,
        // otherwise return site number 1
        return (he.elPm == LEFTEDGE) ? he.elEdge.reg[RIGHTEDGE] : he.elEdge.reg[LEFTEDGE];
    }

    /**
     * Computes the distance between two sites.
     *
     * @param   site1  the first site
     * @param   site2  the second site
     * @return  the distance between the sites
     */
    private double dist(final Site site1, final Site site2) {

        double distX;
        double distY;

        distX = site1.xPos - site2.xPos;
        distY = site1.yPos - site2.yPos;

        return Math.sqrt((distX * distX) + (distY * distY));
    }

    /**
     * Creates a new site where the HalfEdges el1 and el2 intersect.
     *
     * @param   el1  ???
     * @param   el2  ???
     * @return  ???
     */
    private Site intersect(final Halfedge el1, final Halfedge el2) {

        VorEdge edge1;
        VorEdge edge2;
        VorEdge edge3;
        Halfedge edge;
        double det;
        double xint;
        double yint;
        boolean right_of_site;

        edge1 = el1.elEdge;
        edge2 = el2.elEdge;

        if ((edge1 == null) || (edge2 == null)) {
            return null;
        }

        // if the two edges bisect the same parent, return null
        if (edge1.reg[1] == edge2.reg[1]) {
            return null;
        }

        det = (edge1.aParam * edge2.bParam) - (edge1.bParam * edge2.aParam);

        if ((-1.0e-10 < det) && (det < 1.0e-10)) {
            return null;
        }

        xint = ((edge1.cParam * edge2.bParam) - (edge2.cParam * edge1.bParam)) / det;
        yint = ((edge2.cParam * edge1.aParam) - (edge1.cParam * edge2.aParam)) / det;

        if ((edge1.reg[1].yPos < edge2.reg[1].yPos)
                || ((edge1.reg[1].yPos == edge2.reg[1].yPos)
                    && (edge1.reg[1].xPos < edge2.reg[1].xPos))) {
            edge = el1;
            edge3 = edge1;
        } else {
            edge = el2;
            edge3 = edge2;
        }

        right_of_site = xint >= edge3.reg[1].xPos;

        if ((right_of_site && (edge.elPm == LEFTEDGE))
                || (!right_of_site && (edge.elPm == RIGHTEDGE))) {
            return null;
        }

        // create a new site at the point of intersection - this is a new
        // vector event waiting to happen
        return new Site(xint, yint, -1);
    }

    /**
     * ???
     */
    private void voronoiBuild() {

        Site newsite;
        Site bot;
        Site top;
        Site temp;
        Site p;
        Site v;
        Vertex newintstar = null;
        int pm;
        Halfedge lbnd;
        Halfedge rbnd;
        Halfedge llbnd;
        Halfedge rrbnd;
        Halfedge bisector;
        VorEdge edge;

        this.pq = new PQ(this.sqrtNsites, this.minY, this.height);
        this.el = new EL(this.sqrtNsites, this.minX, this.width);

        this.bottomsite = nextOne();
        newsite = nextOne();

        for (;;) {

            if (!this.pq.empty()) {
                newintstar = this.pq.min();
            }

            // if the lowest site has a smaller y value than the lowest vector
            // intersection, process the site otherwise process the vector
            // intersection
            if ((newsite != null)
                    && (this.pq.empty() || (newsite.yPos < newintstar.yPos)
                        || ((newsite.yPos == newintstar.yPos)
                            && (newsite.xPos < newintstar.xPos)))) {

                /* new site is smallest - this is a site event */
                // get the first HalfEdge to the LEFT of the new site
                lbnd = this.el.leftbnd((newsite));

                // get the first HalfEdge to the RIGHT of the new site
                rbnd = lbnd.elRight;

                // if this half edge has no edge,bot = bottom site (whatever
                // that is)
                bot = rightreg(lbnd);

                // create a new edge that bisects
                edge = bisect(bot, newsite);

                // create a new HalfEdge, setting its elPm field to 0
                bisector = new Halfedge(edge, LEFTEDGE); // NOPMD SRB

                // insert this new bisector edge between the left and right
                // vectors in a linked list
                this.el.insert(lbnd, bisector);

                // if the new bisector intersects with the left edge,
                // remove the left edge's vertex, and put in the new one
                p = intersect(lbnd, bisector);

                if (p != null) {
                    this.pq.delete(lbnd);
                    this.pq.insert(lbnd, p, dist(p, newsite));
                }

                lbnd = bisector;

                // create a new HalfEdge, setting its elPm field to 1
                bisector = new Halfedge(edge, RIGHTEDGE); // NOPMD SRB

                // insert the new HE to the right of the original bisector
                // earlier in the IF statement
                this.el.insert(lbnd, bisector);

                // if this new bisector intersects with the new HalfEdge
                p = intersect(bisector, rbnd);

                if (p != null) {

                    // push the HE into the ordered linked list of vertices
                    this.pq.insert(bisector, p, dist(p, newsite));
                }

                newsite = nextOne();
            } else if (this.pq.empty()) {
                break;
            } else {
                /* intersection is smallest - this is a vector event */

                // pop the HalfEdge with the lowest vector off the ordered list
                // of vectors
                lbnd = this.pq.extractMin();

                // get the HalfEdge to the left of the above HE
                llbnd = lbnd.elLeft;

                // get the HalfEdge to the right of the above HE
                rbnd = lbnd.elRight;

                // get the HalfEdge to the right of the HE to the right of the
                // lowest HE
                rrbnd = rbnd.elRight;

                // get the Site to the left of the left HE which it bisects
                bot = leftreg(lbnd);

                // get the Site to the right of the right HE which it bisects
                top = rightreg(rbnd);

                v = lbnd.vertex;

                // get the vertex that caused this event set the vertex number
                // - couldn't do this searlier since we didn't know when it
                // would be processed
                v.sitenbr = this.nvertices;
                this.nvertices += 1;

                endpoint(lbnd.elEdge, lbnd.elPm, v);

                // set the endpoint of the left HalfEdge to be this vector
                endpoint(rbnd.elEdge, rbnd.elPm, v);

                // set the endpoint of the right HalfEdge to be this vector
                this.el.delete(lbnd); // mark the lowest HE for

                // deletion - can't delete yet because there might be pointers
                // to it in Hash Map
                this.pq.delete(rbnd);

                // remove all vertex events to do with the right HE
                this.el.delete(rbnd); // mark the right HE for

                // deletion - can't delete yet because there might be pointers
                // to it in Hash Map
                pm = LEFTEDGE; // set the pm variable to zero

                if (bot.yPos > top.yPos) {

                    // if the site to the left of the event is higher than the
                    // Site to the right of it, then swap them and set the 'pm'
                    // variable to 1
                    temp = bot;
                    bot = top;
                    top = temp;
                    pm = RIGHTEDGE;
                }

                edge = bisect(bot, top);

                // create an Edge (or line) that is between the two Sites. This
                // creates the formula of the line, and assigns a line number
                // to it
                bisector = new Halfedge(edge, pm); // NOPMD SRB

                // create a HE from the Edge 'e', and make it point to that
                // edge with its elEdge field
                this.el.insert(llbnd, bisector);

                // insert the new bisector to the sright of the left HE
                endpoint(edge, RIGHTEDGE - pm, v);
                // set one endpoint to the new edge to be the vector point 'v'. If the site to the
                // left of this bisector is higher than the right Site, then this endpoint is put
                // in position 0; otherwise in pos 1

                // if left HE and the new bisector intersect, then delete sthe
                // left HE, and reinsert it
                p = intersect(llbnd, bisector);

                if (p != null) {
                    this.pq.delete(llbnd);
                    this.pq.insert(llbnd, p, dist(p, bot));
                }

                // if right HE and the new bisector intersect, then reinsert it
                p = intersect(bisector, rrbnd);

                if (p != null) {
                    this.pq.insert(bisector, p, dist(p, bot));
                }
            }
        }

        for (lbnd = this.el.leftEnd.elRight; lbnd != this.el.rightEnd; lbnd = lbnd.elRight) {
            edge = lbnd.elEdge;
            clipLine(edge);
        }
    }

    /**
     * Given a list of vertices and a list of voronoi edges, computes a set of polygons, each
     * representing a Voronoi region, then finds the vertex in that region and constructs a map
     * from vertex to region.
     *
     * @param   vertices  the list of vertices
     * @param   edges     the list of Voronoi edges
     * @return  the constructed map
     */
    public Map<Vertex, Path2D> makeVoronoiPolygons(final Vertex[] vertices,
        final List<GraphEdge> edges) {

        Map<Vertex, Path2D> map;

        map = new HashMap<Vertex, Path2D>(edges.size());

        // Scan through all edges
        for (int onEdge = 0; onEdge < edges.size(); onEdge++) {
            findPolygon(onEdge, vertices, edges, map, true);
            findPolygon(onEdge, vertices, edges, map, false);
        }

        return map;
    }

    /**
     * Follows an edge around the polygon to its left, connecting subsequent edges to form a
     * polygon,then adds that polygon to the map based on the vertex it contains.
     *
     * @param  onEdge    the edge
     * @param  vertices  the list of vertices
     * @param  edges     the list of edges
     * @param  map       the map from vertex to containing polygon
     * @param  isLeft    <code>true <code>to look for edges that follow the polygon to the left of
     *                   the starting edge; <code>false</code> to follow the polygon to the right
     */
    private void findPolygon(final int onEdge, final Vertex[] vertices,
        final List<GraphEdge> edges, final Map<Vertex, Path2D> map, final boolean isLeft) {

        GraphEdge root;
        GraphEdge terminal;
        Vertex start;
        Vertex end;
        Vertex prior;
        Vertex current;
        List<Vertex> list;
        double angle;
        double maxAngle;
        double dx1;
        double dx2;
        double dy1;
        double dy2;
        GraphEdge best = null;
        double bestX = 0;
        double bestY = 0;
        boolean finished;
        boolean found;
        int len;
        Line2D line;
        Path2D poly;

        root = edges.get(onEdge);
        list = new LinkedList<Vertex>();
        finished = false;

        // Create the initial two points in the polygon
        start = new Vertex(root.xPos1, root.yPos1);
        list.add(start);
        end = new Vertex(root.xPos2, root.yPos2);
        list.add(end);

        prior = start;
        current = end;
        terminal = root;

outer:
        for (;;) {

            // Look for edges that start where the current edge ends, then
            // identify the one whose endpoint lies to the left (right) of the
            // current edge and which makes the largest angle with the current
            // edge
            maxAngle = 0;
            dx1 = current.xPos - prior.xPos;
            dy1 = current.yPos - prior.yPos;
            found = false;

            for (GraphEdge test : edges) {

                if ((test == root) || (test == terminal)) {

                    // edge would trivially link back to its start point
                    continue;
                }

                if ((Math.abs(test.xPos1 - current.xPos) < this.epsilon)
                        && (Math.abs(test.yPos1 - current.yPos) < this.epsilon)) {

                    // Found a link
                    if ((Math.abs(test.xPos2 - start.xPos) < this.epsilon)
                            && (Math.abs(test.yPos2 - start.yPos) < this.epsilon)) {

                        // Edge closed on the starting vertex - finished
                        finished = true;

                        break outer;
                    }

                    if (isLeft
                            == isCcw(prior.xPos, prior.yPos, current.xPos, current.yPos,
                                test.xPos2, test.yPos2)) {

                        dx2 = test.xPos2 - test.xPos1;
                        dy2 = test.yPos2 - test.yPos1;

                        angle = Math.abs(Math.acos(
                                    ((dx1 * dx2) + (dy1 * dy2))
                                    / (Math.sqrt((dx1 * dx1) + (dy1 * dy1)))
                                    / (Math.sqrt((dx2 * dx2) + (dy2 * dy2)))));

                        if (angle > maxAngle) {
                            found = true;
                            maxAngle = angle;
                            best = test;
                            bestX = test.xPos2;
                            bestY = test.yPos2;
                        }
                    }
                } else if ((Math.abs(test.xPos2 - current.xPos) < this.epsilon)
                        && (Math.abs(test.yPos2 - current.yPos) < this.epsilon)) {

                    // Found a link
                    if ((Math.abs(test.xPos1 - start.xPos) < this.epsilon)
                            && (Math.abs(test.yPos1 - start.yPos) < this.epsilon)) {

                        // Edge closed on the starting vertex - finished
                        finished = true;

                        break outer;
                    }

                    if (isLeft
                            == isCcw(prior.xPos, prior.yPos, current.xPos, current.yPos,
                                test.xPos1, test.yPos1)) {

                        dx2 = test.xPos1 - test.xPos2;
                        dy2 = test.yPos1 - test.yPos2;
                        angle = Math.abs(Math.acos(
                                    ((dx1 * dx2) + (dy1 * dy2))
                                    / (Math.sqrt((dx1 * dx1) + (dy1 * dy1)))
                                    / (Math.sqrt((dx2 * dx2) + (dy2 * dy2)))));

                        if (angle > maxAngle) {
                            found = true;
                            maxAngle = angle;
                            best = test;
                            bestX = test.xPos1;
                            bestY = test.yPos1;
                        }
                    }
                }
            }

            if (found) {
                prior = current;
                current = new Vertex(bestX, bestY); // NOPMD SRB
                list.add(current);
                terminal = best;

            } else {
                break;
            }
        }

        if (!finished) {

            prior = end;
            current = start;

outer:
            for (;;) {

                // Look for edges that end where the current edge starts, then
                // identify the one whose endpoint lies to the left of the
                // current edge and which makes the largest angle with the
                // current edge
                maxAngle = 0;
                dx1 = current.xPos - prior.xPos;
                dy1 = current.yPos - prior.yPos;
                found = false;

                for (GraphEdge test : edges) {

                    if ((test == root) || (test == terminal)) {

                        // edge would trivially link back to its start point
                        continue;
                    }

                    if ((Math.abs(test.xPos1 - current.xPos) < this.epsilon)
                            && (Math.abs(test.yPos1 - current.yPos) < this.epsilon)) {

                        // Found a link
                        if ((Math.abs(test.xPos2 - end.xPos) < this.epsilon)
                                && (Math.abs(test.yPos2 - end.yPos) < this.epsilon)) {

                            // Edge closed on the starting vertex - finished
                            finished = true;

                            break outer;
                        }

                        if (isLeft
                                == isCcw(current.xPos, current.yPos, prior.xPos, prior.yPos,
                                    test.xPos2, test.yPos2)) {

                            dx2 = test.xPos2 - test.xPos1;
                            dy2 = test.yPos2 - test.yPos1;
                            angle = Math.abs(Math.acos(
                                        ((dx1 * dx2) + (dy1 * dy2))
                                        / (Math.sqrt((dx1 * dx1) + (dy1 * dy1)))
                                        / (Math.sqrt((dx2 * dx2) + (dy2 * dy2)))));

                            if (angle > maxAngle) {
                                found = true;
                                maxAngle = angle;
                                best = test;
                                bestX = test.xPos2;
                                bestY = test.yPos2;
                            }
                        }
                    } else if ((Math.abs(test.xPos2 - current.xPos) < this.epsilon)
                            && (Math.abs(test.yPos2 - current.yPos) < this.epsilon)) {

                        // Found a link
                        if ((Math.abs(test.xPos1 - end.xPos) < this.epsilon)
                                && (Math.abs(test.yPos1 - end.yPos) < this.epsilon)) {

                            // Edge closed on the starting vertex - finished
                            finished = true;

                            break outer;
                        }

                        if (isLeft
                                == isCcw(current.xPos, current.yPos, prior.xPos, prior.yPos,
                                    test.xPos1, test.yPos1)) {

                            dx2 = test.xPos1 - test.xPos2;
                            dy2 = test.yPos1 - test.yPos2;
                            angle = Math.abs(Math.acos(
                                        ((dx1 * dx2) + (dy1 * dy2))
                                        / (Math.sqrt((dx1 * dx1) + (dy1 * dy1)))
                                        / (Math.sqrt((dx2 * dx2) + (dy2 * dy2)))));

                            if (angle > maxAngle) {
                                found = true;
                                maxAngle = angle;
                                best = test;
                                bestX = test.xPos1;
                                bestY = test.yPos1;
                            }
                        }
                    }
                }

                if (found) {
                    prior = current;
                    current = new Vertex(bestX, bestY); // NOPMD SRB
                    list.add(0, current);
                    terminal = best;
                } else {
                    finished = true;

                    break;
                }
            }
        }

        if (finished) {
            Vertex vert1 = null;
            Vertex vert2 = null;

            len = list.size();

            if (len > 2) {

                poly = new Path2D.Double();

                for (int i = 1; i < len; i++) {
                    vert1 = list.get(i - 1);
                    vert2 = list.get(i);
                    line = new Line2D.Double(vert1.xPos, vert1.yPos, vert2.xPos, vert2.yPos); // NOPMD SRB
                    poly.append(line, true);
                }

                for (Vertex v : vertices) {

                    if (poly.contains(v.xPos, v.yPos)) {
                        map.put(v, poly);
                    }
                }
            }
        }
    }

    /**
     * Computes twice the area of the oriented triangle <code>(xPos1, yPos1), (xPos2, yPos2), (x3,
     * y3)</code> (the area is positive if the triangle is oriented counterclockwise).
     *
     * @param   xPos1  the first point X coordinate
     * @param   yPos1  the first point Y coordinate
     * @param   xPos2  the second point X coordinate
     * @param   yPos2  the second point Y coordinate
     * @param   xPos3  the third point X coordinate
     * @param   yPos3  the third point Y coordinate
     * @return  twice the oriented area
     */
    public static final double triArea(final double xPos1, final double yPos1, final double xPos2,
        final double yPos2, final double xPos3, final double yPos3) {

        return ((xPos2 - xPos1) * (yPos3 - yPos1)) - ((yPos2 - yPos1) * (xPos3 - xPos1));
    }

    /**
     * Tests whether the points of a triangle are in counterclockwise order.
     *
     * @param   xPos1  the first point X coordinate
     * @param   yPos1  the first point Y coordinate
     * @param   xPos2  the second point X coordinate
     * @param   yPos2  the second point Y coordinate
     * @param   xPos3  the third point X coordinate
     * @param   yPos3  the third point Y coordinate
     * @return  <code>true</code> if triangle <code>(xPos1, yPos1), (xPos2, yPos2), (x3, y3)</code>
     *          is in counterclockwise order
     */
    public static final boolean isCcw(final double xPos1, final double yPos1, final double xPos2,
        final double yPos2, final double xPos3, final double yPos3) {

        return triArea(xPos1, yPos1, xPos2, yPos2, xPos3, yPos3) > 0;
    }

    /**
     * Tests whether two points are at the same location; two points are considered to be at the
     * same location when their X and Y coordinates both differ by less than <code>epsilon</code>.
     *
     * @param   xPos1  the first point X coordinate
     * @param   yPos1  the first point Y coordinate
     * @param   xPos2  the second point X coordinate
     * @param   yPos2  the second point Y coordinate
     * @return  <code>true</code> if the points are at the same location; <code>false</code> if not
     */
    private boolean isSame(final double xPos1, final double yPos1, final double xPos2,
        final double yPos2) {

        return (Math.abs(xPos2 - xPos1) < this.epsilon)
            && (Math.abs(yPos2 - yPos1) < this.epsilon);
    }
}
