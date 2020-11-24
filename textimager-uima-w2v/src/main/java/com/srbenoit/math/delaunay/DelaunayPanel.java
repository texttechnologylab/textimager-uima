package com.srbenoit.math.delaunay;
//package com.srbenoit.math.delaunay;
//
//import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.RenderingHints;
//import java.awt.geom.Line2D;
//import java.awt.geom.Path2D;
//import java.awt.geom.PathIterator;
//import java.awt.image.BufferedImage;
//import java.util.Map;
//import java.util.Random;
//import javax.swing.JPanel;
//import com.srbenoit.color.Gradient;
//
///**
// * A panel that can visualize a Delaunay triangulation at any point in the process.
// */
//public class DelaunayPanel extends JPanel {
//
//    /** version number for serialization */
//    private static final long serialVersionUID = -4125801564718139263L;
//
//    /** the width of the offscreen image */
//    private static final int IMGWIDTH = 920;
//
//    /** the height of the offscreen image */
//    private static final int IMGHEIGHT = 920;
//
//    /** the portion of window to leave as margin on each side */
//    private static final double MARGIN = 0.05;
//
//    /** green color for leaf triangles */
//    private static final Color LINES = new Color(0, 255, 0);
//
//    /** the left edge of a bounding box that contains the points */
//    private double minX;
//
//    /** the right edge of a bounding box that contains the points */
//    private double maxX;
//
//    /** the bottom edge of a bounding box that contains the points */
//    private double minY;
//
//    /** the top edge of a bounding box that contains the points */
//    private double maxY;
//
//    /** X axis scale factor to take points to pixels */
//    private double xScale;
//
//    /** X axis offset to take points to pixels */
//    private double xOffset;
//
//    /** Y axis scale factor to take points to pixels */
//    private double yScale;
//
//    /** Y axis offset to take points to pixels */
//    private double yOffset;
//
//    /** the offscreen image */
//    private final BufferedImage offscreen;
//
//    /** a gradient over hue with 100 steps */
//    private final Gradient gradient;
//
//    /**
//     * Constructs a new <code>DelaunayPanel</code>.
//     */
//    public DelaunayPanel() {
//
//        super();
//
//        setBackground(Color.BLACK);
//        setPreferredSize(new Dimension(IMGWIDTH, IMGHEIGHT));
//
//        this.offscreen = new BufferedImage(IMGWIDTH, IMGHEIGHT, BufferedImage.TYPE_INT_RGB);
//
//        this.gradient = new Gradient(100, 1);
//    }
//
//    /**
//     * Notifies that another step in the process has completed, and visualization can be updated.
//     * Processing will not continue until this method returns, allowing a visualizer to show a
//     * slow, step-by-step process.
//     *
//     * @param  vertices  the list of points being triangulated
//     * @param  sub       the subdivision to draw
//     */
//    public void update(final Vertex[] vertices, final Delaunay sub) {
//
//        Random rnd;
//        Graphics2D grx;
//        int pixX1;
//        int pixY1;
//        int pixX2;
//        int pixY2;
//        Map<Vertex, Path2D> map;
//        double[] seg;
//        double lastX = 0;
//        double lastY = 0;
//        Path2D scaled;
//        Line2D newLine;
//        Color color;
//        PathIterator iter;
//
//        seg = new double[] { 0, 0, 0, 0, 0, 0 };
//        rnd = new Random();
//
//        // Compute the bounding box based on the points
//        getBoundingBox(vertices);
//
//        // Compute the transform to convert points to pixels
//        computeTransform();
//
//        // Do all drawing in a synchronized block
//        synchronized (this.offscreen) {
//            grx = (Graphics2D) this.offscreen.getGraphics();
//            grx.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                RenderingHints.VALUE_ANTIALIAS_ON);
//
//            // Clear the window
//            grx.setColor(Color.BLACK);
//            grx.fillRect(0, 0, IMGWIDTH, IMGHEIGHT);
//
//            // Draw Voronoi regions
//            map = sub.vorMap;
//
//            if (map != null) {
//
//                for (Path2D path : map.values()) {
//                    color = this.gradient.getColor(rnd.nextInt(100));
//                    color = new Color(color.getRed() / 2, color.getGreen() / 2, // NOPMD SRB
//                            color.getBlue() / 2);
//                    grx.setColor(color);
//
//                    scaled = new Path2D.Double(); // NOPMD SRB
//
//                    iter = path.getPathIterator(null);
//
//                    while (!iter.isDone()) {
//
//                        switch (iter.currentSegment(seg)) {
//
//                        case PathIterator.SEG_MOVETO:
//                            lastX = seg[0];
//                            lastY = seg[1];
//                            break;
//
//                        case PathIterator.SEG_LINETO:
//                            newLine = new Line2D.Double(scaleX(lastX), scaleY(lastY), // NOPMD SRB
//                                    scaleX(seg[0]), scaleY(seg[1]));
//                            scaled.append(newLine, true);
//                            lastX = seg[0];
//                            lastY = seg[1];
//                            break;
//
//                        default:
//                            break;
//                        }
//
//                        iter.next();
//                    }
//
//                    grx.fill(scaled);
//                }
//            }
//
//            // Draw all the edges in green
//            grx.setColor(LINES);
//
//            for (Edge edge : sub.delEdges) {
//                pixX1 = scaleX(edge.org().xPos);
//                pixY1 = scaleY(edge.org().yPos);
//                pixX2 = scaleX(edge.dest().xPos);
//                pixY2 = scaleY(edge.dest().yPos);
//                grx.drawLine(pixX1, pixY1, pixX2, pixY2);
//            }
//
//            // Draw all the voronoi boundaries in dark gray
//            grx.setColor(Color.RED);
//
//            for (GraphEdge edge : sub.vorEdges) {
//
//                pixX1 = scaleX(edge.xPos1);
//                pixY1 = scaleY(edge.yPos1);
//                pixX2 = scaleX(edge.xPos2);
//                pixY2 = scaleY(edge.yPos2);
//                grx.drawLine(pixX1, pixY1, pixX2, pixY2);
//            }
//
//            // Plot the points
//            grx.setColor(Color.YELLOW);
//
//            for (Edge edge : sub.delEdges) {
//
//                if (edge.data != null) {
//                    pixX1 = scaleX(edge.data.xPos);
//                    pixY1 = scaleY(edge.data.yPos);
//                    grx.fillOval(pixX1 - 1, pixY1 - 1, 3, 3);
//                }
//
//                if (edge.sym().data != null) {
//                    pixX1 = scaleX(edge.sym().data.xPos);
//                    pixY1 = scaleY(edge.sym().data.yPos);
//                    grx.fillOval(pixX1 - 1, pixY1 - 1, 3, 3);
//                }
//            }
//        }
//
//        repaint();
//    }
//
//    /**
//     * Paints the panel.
//     *
//     * @param  grx  the <code>Graphics</code> to which to draw
//     */
//    @Override public void paintComponent(final Graphics grx) {
//
//        super.paintComponent(grx);
//
//        synchronized (this.offscreen) {
//            grx.drawImage(this.offscreen, 0, 0, getWidth(), getHeight(), null);
//        }
//    }
//
//    /**
//     * Computes the bounding box of a set of points. The box will be just large enough to contain
//     * the points, which could possibly mean having zero width or height if points are colinear.
//     * The bounding rectangle is stored in the member variable <code>bounds</code>.
//     *
//     * @param  points  the set of points
//     */
//    private void getBoundingBox(final Vertex[] points) {
//
//        Vertex vert;
//
//        vert = points[0];
//
//        this.minX = vert.xPos;
//        this.maxX = this.minX;
//        this.minY = vert.yPos;
//        this.maxY = this.minY;
//
//        for (int i = 1; i < points.length; i++) {
//            vert = points[i];
//
//            if (vert.xPos < this.minX) {
//                this.minX = vert.xPos;
//            }
//
//            if (vert.xPos > this.maxX) {
//                this.maxX = vert.xPos;
//            }
//
//            if (vert.yPos < this.minY) {
//                this.minY = vert.yPos;
//            }
//
//            if (vert.yPos > this.maxY) {
//                this.maxY = vert.yPos;
//            }
//        }
//
//        // Ensure that no edge is zero, to make matrix computation easier
//        if (Math.abs(this.minX) < 0.0000001) {
//            this.minX = -0.0000001;
//        }
//
//        if (Math.abs(this.maxX) < 0.0000001) {
//            this.maxX = 0.0000001;
//        }
//
//        if (Math.abs(this.minY) < 0.0000001) {
//            this.minY = -0.0000001;
//        }
//
//        if (Math.abs(this.maxY) < 0.0000001) {
//            this.maxY = 0.0000001;
//        }
//    }
//
//    /**
//     * Computes the transformation matrix to map a rectangle to an area in the panel covering 80%
//     * of the panel in each dimension (that is, the panel will have a 10% margin on each edge).
//     *
//     * <pre>
//     * [ . . ] [x] = [X]
//     * [ . . ] [y]   [Y]
//     * </pre>
//     *
//     * where (x,y) is the real-valued point space, (X, Y) is the pixel, and (minX,minY) maps to
//     * (w/10,9h/10) and (maxX,maxY) maps to (9w/10,h/10).
//     */
//    private void computeTransform() {
//
//        // Compute scale factors for X and Y axis (flipping Y axis)
//        if (this.maxX == this.minX) {
//            this.xScale = 1;
//        } else {
//            this.xScale = (1 - (2 * MARGIN)) * IMGWIDTH / (this.maxX - this.minX);
//        }
//
//        if (this.maxY == this.minY) {
//            this.yScale = -1;
//        } else {
//            this.yScale = -(1 - (2 * MARGIN)) * IMGHEIGHT / (this.maxY - this.minY);
//        }
//
//        if (-this.yScale > this.xScale) {
//            this.yScale = -this.xScale;
//        } else if (this.xScale > -this.yScale) {
//            this.xScale = -this.yScale;
//        }
//
//        // Now set offset so (minX, minY) maps to (0.1W, 0.9H)
//        this.xOffset = (MARGIN * IMGWIDTH) - (this.xScale * this.minX);
//        this.yOffset = ((1 - MARGIN) * IMGHEIGHT) - (this.yScale * this.minY);
//    }
//
//    /**
//     * Generates an X pixel from a real X coordinate in point space.
//     *
//     * @param   coord  the X coordinate
//     * @return  the X pixel value
//     */
//    private int scaleX(final double coord) {
//
//        return (int) ((this.xScale * coord) + this.xOffset + 0.5);
//    }
//
//    /**
//     * Generates a Y pixel from a real Y coordinate in point space.
//     *
//     * @param   coord  the Y coordinate
//     * @return  the Y pixel value
//     */
//    private int scaleY(final double coord) {
//
//        return (int) ((this.yScale * coord) + this.yOffset + 0.5);
//    }
//}