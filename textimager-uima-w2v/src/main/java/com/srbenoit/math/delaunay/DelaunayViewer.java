package com.srbenoit.math.delaunay;
//package com.srbenoit.math.delaunay;
//
//import java.awt.BorderLayout;
//import java.lang.reflect.InvocationTargetException;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import javax.swing.SwingUtilities;
//import javax.swing.WindowConstants;
//import com.srbenoit.ui.UIUtilities;
//
///**
// * A viewer that will display the that can show a visualization of the triangulation.
// */
//public class DelaunayViewer implements DelaunayVisualizerInt, Runnable {
//
//    /** flag that controls whether we pause after each step */
//    private static final boolean PAUSE = false;
//
//    /** the panel that will display the triangulation */
//    private DelaunayPanel panel;
//
//    /** a label that will show the description of the phase just completed */
//    private JLabel label;
//
//    /**
//     * Constructs a new <code>DelaunayViewer</code>.
//     *
//     * @throws  InvocationTargetException  if there is an error constructing the interface
//     * @throws  InterruptedException       if there is an interruption while constructing the
//     *                                     interface
//     */
//    public DelaunayViewer() throws InterruptedException, InvocationTargetException {
//
//        SwingUtilities.invokeAndWait(this);
//    }
//
//    /**
//     * Notifies that another step in the process has completed, and visualization can be updated.
//     * Processing will not continue until this method returns, allowing a visualizer to show a
//     * slow, step-by-step process.
//     *
//     * @param  points  the list of points being triangulated
//     * @param  sub     the subdivision to draw
//     * @param  phase   a string description of the phase just completed
//     */
//    public void update(final Vertex[] points, final Delaunay sub, final String phase) {
//
//        this.label.setText(phase);
//        this.panel.update(points, sub);
//
//        if (PAUSE) {
//
//            while (this.panel.isVisible()) {
//
//                synchronized (this) {
//
//                    try {
//                        wait(1000);
//                    } catch (InterruptedException e) {
//                        // No action
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     * Constructs the user interface in the AWT event dispatcher thread.
//     */
//    public void run() {
//
//        JFrame frame;
//        JPanel content;
//
//        frame = new JFrame();
//        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//
//        content = new JPanel(new BorderLayout());
//        frame.setContentPane(content);
//
//        this.panel = new DelaunayPanel();
//        content.add(this.panel, BorderLayout.CENTER);
//
//        this.label = new JLabel("Initializing...");
//        content.add(this.label, BorderLayout.NORTH);
//
//        frame.pack();
//        UIUtilities.positionFrame(frame, 0.5, 0.1);
//        frame.setVisible(true);
//    }
//}