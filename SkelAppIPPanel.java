/*----------------------------------------------------------------------------*\
 *
 * @(#)SkelAppIPPanel.java  2.0
 *
 * Version 2.0
 *
 * The <code>SkelAppIPPanel</code> class represents a panel for drawing
 * graphics objects in a window.
 *
 * @version SkelApp2DGCanvas 1.0, 24/02/99
 * @author  Claude C. Chibelushi
 *          Modifications:
 *              . method setPanelCentre() replaced by updateCentre() [31/01/02, by CCC]
 *              . method getCentre() updates and returns panel-centre position attributes  [31/01/02, by CCC]
 *
 * @version SkelAppIPCanvas 1.0, 27/09/02
 *          --> mods from SkelApp2DGCanvas v1.0:    change of file name stub from SkelApp2DGCanvas to SkelAppIPPanel
 * @version SkelAppIPPanel 2.0, 11/01/09
 * @author  Cathy French
 *          --> mods from SkelAppIPCanvas v1.0:     change of file name to SkelAppIPPanel, update to use Swing Components
 *
 * @since     JDK1.4
 *----------------------------------------------------------------------------*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.*;
import java.lang.Math;
import java.util.Stack;

/**
 * A <code>SkelAppIPPanel</code> is a blank rectangular area of the screen: an
 * application can draw on it or trap (from it) input events from the user.
 *
 * @version 2.0, 11/01/99
 * @author Claude C. Chibelushi
 * @since JDK1.4
 */
public class SkelAppIPPanel extends JPanel {

    JPanel canvas = new JPanel();
    private int xCentre = 0;    // coordinates of centre of panel
    private int yCentre = 0;
    private boolean cropMouseEvent = false;
    private boolean isColorImg = false;
    private RawImage image = null;  // Rawimage
    private ColorImage colorImg = null;//Color Image

    ;
   
  
            
    /**
     * Constructs and initializes a panel object
     *
     * @since JDK1.1
     */
    public SkelAppIPPanel() {
        super();    // call parent's constructor
        addListeners(); // add event listeners
        setBackground(Color.white);
        JScrollPane pane = new JScrollPane();
        canvas.add(pane);
    }

    /**
     * Adds listeners to panel
     *
     * @since JDK1.1
     */
    public void addListeners() {
        // add mouse listener
        addMouseListener(
                // use argument which is an object of an anonymous subclass of MouseAdapter
                new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                serviceMouseEvent(MouseEvent.MOUSE_PRESSED, event.getX(), event.getY());

            }

            @Override
            public void mouseClicked(MouseEvent event) {
                serviceMouseEvent(MouseEvent.MOUSE_CLICKED, event.getX(), event.getY());
                if (cropMouseEvent == true && isColorImg == false) {
                    image.crop(event.getX(), event.getY()); //passes the pixel coordinates
                    repaint();
                    cropMouseEvent = false;
                } else if (cropMouseEvent == true && isColorImg == true) {
                    colorImg.cropColor(event.getX(), event.getY()); //passes the  pixel coordinates 
                    repaint();
                    cropMouseEvent = false;
                }

            }

            @Override
            public void mouseReleased(MouseEvent event) {
                serviceMouseEvent(MouseEvent.MOUSE_RELEASED, event.getX(), event.getY());
            }
        }
        );

        // add mouse motion listener
        addMouseMotionListener(
                // use argument which is an object of an anonymous subclass of MouseMotionAdapter
                new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent event) {
                serviceMouseEvent(MouseEvent.MOUSE_DRAGGED, event.getX(), event.getY());
            }

            @Override
            public void mouseMoved(MouseEvent event) {
                serviceMouseEvent(MouseEvent.MOUSE_MOVED, event.getX(), event.getY());
            }
        }
        );

        // add key listener
        addKeyListener(
                // use argument which is an object of an anonymous subclass of KeyAdapter
                new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                // service keyboard event
                serviceKeyEvent(event);
            }
        }
        );
    }

    /**
     * Gets the coordinates of the center of the panel.
     *
     * @return center point
     * @since JDK1.1
     */
    public Point getCentre() {
        updateCentre();
        return new Point(xCentre, yCentre);
    }

    /**
     * Updates the coordinates of the center of the panel.
     *
     * @since JDK1.1
     */
    public void updateCentre() {
        Dimension panelDimensions = getSize();

        xCentre = panelDimensions.width / 2;
        yCentre = panelDimensions.height / 2;
    }

    /*----------------------------------------------------------------------------\
    *                                                                            |
    *                       STUDENT PAINT MESSAGE SERVICING                      |
    *                                                                            |
    *----------------------------------------------------------------------------*/
    // CUSTOMISE THE "TO DO" COMMENTS AS APPROPRIATE
    /**
     * Updates panel area.
     *
     * @param g a Graphics object.
     * @since JDK1.4
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Display currently loaded image
        if (isColorImg) {
            colorImg.displayImgColor(g);
        } else if (image != null) {
            image.display(g);
        }
        g.drawString("BMW Creations", 10, this.getHeight() - 50);
    }

    /*----------------------------------------------------------------------------\
    *                                                                            |
    *                       STUDENT "ARROW KEYS" SERVICING                   |
    *                                                                            |
    *----------------------------------------------------------------------------*/
    // CUSTOMISE THE "TO DO" COMMENTS AS APPROPRIATE
    /**
     * Services "arrow keys" events
     *
     * @param event a key-event object.
     * @since JDK1.1
     */
    public void serviceKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.VK_UP:
                // TO DO: service up-key as appropriate
                break;
            case KeyEvent.VK_LEFT:
                // TO DO: service left-key as appropriate
                break;
            case KeyEvent.VK_DOWN:
                // TO DO: service down-key as appropriate
                break;
            case KeyEvent.VK_RIGHT:
                // TO DO: service right-key as appropriate
                break;
            default:
                // TO DO: service "all-other-keys" as appropriate
                break;
        }
    }

    /*----------------------------------------------------------------------------\
    *                                                                            |
    *                       STUDENT MOUSE SERVICING                              |
    *                                                                            |
    *----------------------------------------------------------------------------*/
    // CUSTOMISE THE "TO DO" COMMENTS AS APPROPRIATE
    /**
     * Services mouse events.
     *
     * @param eventID ID of mouse event.
     * @param xMousePosition x coordinate of cursor at mouse event.
     * @param yMousePosition y coordinate of cursor at mouse event.
     * @since JDK1.1
     */
    public void serviceMouseEvent(int eventID, int xMousePosition, int yMousePosition) {
        requestFocusInWindow();
        switch (eventID) {
            case MouseEvent.MOUSE_CLICKED:
                // TO DO: service MOUSE_CLICKED as appropriate
                break;
            case MouseEvent.MOUSE_DRAGGED:
                // TO DO: service MOUSE_DRAGGED as appropriate
                break;
            case MouseEvent.MOUSE_PRESSED:
                // TO DO: service MOUSE_PRESSED as appropriate
                break;
            case MouseEvent.MOUSE_RELEASED:
                // TO DO: service MOUSE_RELEASED as appropriate
                break;
            case MouseEvent.MOUSE_MOVED:
                // TO DO: service MOUSE_MOVED as appropriate
                break;
            default:
                // TO DO: service "all-other-mouse-states" as appropriate
                break;
        }
    }

    /**
     * Loads image from file.
     *
     * @param file Reference to the file
     * @since JDK1.4
     */
    public void loadRawImage(File file) {
        image = new RawImage(file); // load raw image from file
        isColorImg = false;
        repaint();  // trigger a screen refresh for displaying image just loaded
    }

    public void loadRawImageTwo(File filenew) {
        RawImage file2 = null;
        file2 = new RawImage(filenew);
        file2.loadFileTWo(filenew); // load raw image from file
        isColorImg = false;

        repaint();  // trigger a screen refresh for displaying image just loaded
    }

    public void loadColorImg(File file, String type) {
        colorImg = new ColorImage(file, type); // load color image from file
        isColorImg = true;
        repaint();  // trigger a screen refresh for displaying image just loaded
    }

    /**
     * Saves image to file.
     *
     * @param file Reference to the file
     * @since JDK1.4
     */
    public void saveImage(File file) {
        // save raw image to file
        if (isColorImg == false) {
            image.save(file);
        } else {// save color image to file
            colorImg.saveColorImg(file);
        }
    }

    public void invert() {

        if (isColorImg == true) {
            colorImg.invertColor();
            repaint();
        } else {
            image.invert();
            repaint();
        }

    }

    public void adjustBrightness() {
        //Creates new slider with changelistner

        JSlider slider = new JSlider(JSlider.HORIZONTAL, -20, 20, 0);
        JFrame frame = new JFrame();
        JLabel label = new JLabel();
        if (isColorImg == true) {
            String[] button = {"Red", "Green", "Blue", "All", "Cancel"};
            final ImageIcon icon = new ImageIcon("Info_icon.png");
            int option = JOptionPane.showOptionDialog(null, "Select a Channel", "Channels", JOptionPane.INFORMATION_MESSAGE, 0, icon, button, button[4]);
            switch (option) {
                case 0:
                    label.setOpaque(true);
                    slider.setMajorTickSpacing(5);
                    slider.setMinorTickSpacing(1);
                    slider.setPaintLabels(true);
                    slider.setPaintTicks(true);
                    slider.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent e) {
                            JSlider source = (JSlider) e.getSource();
                            final int value = source.getValue();
                            colorImg.adjustBrightnessRed(value);
                            repaint();

                        }

                    });

                    frame.add(label, BorderLayout.CENTER);
                    frame.add(slider, BorderLayout.SOUTH);
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    frame.pack();
                    frame.setVisible(true);
                    break;
                case 1:
                    label.setOpaque(true);
                    slider.setMajorTickSpacing(5);
                    slider.setMinorTickSpacing(1);
                    slider.setPaintLabels(true);
                    slider.setPaintTicks(true);
                    slider.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent e) {
                            JSlider source = (JSlider) e.getSource();
                            final int value = source.getValue();
                            colorImg.adjustBrightnessGreen(value);
                            repaint();

                        }

                    });

                    frame.add(label, BorderLayout.CENTER);
                    frame.add(slider, BorderLayout.SOUTH);
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    frame.pack();
                    frame.setVisible(true);
                    break;
                case 2:
                    label.setOpaque(true);
                    slider.setMajorTickSpacing(5);
                    slider.setMinorTickSpacing(1);
                    slider.setPaintLabels(true);
                    slider.setPaintTicks(true);
                    slider.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent e) {
                            JSlider source = (JSlider) e.getSource();
                            final int value = source.getValue();
                            colorImg.adjustBrightnessBlue(value);
                            repaint();

                        }

                    });

                    frame.add(label, BorderLayout.CENTER);
                    frame.add(slider, BorderLayout.SOUTH);
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    frame.pack();
                    frame.setVisible(true);
                    break;
                case 3:
                    label.setOpaque(true);
                    slider.setMajorTickSpacing(5);
                    slider.setMinorTickSpacing(1);
                    slider.setPaintLabels(true);
                    slider.setPaintTicks(true);
                    slider.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent e) {
                            JSlider source = (JSlider) e.getSource();
                            final int value = source.getValue();
                            colorImg.adjustBrightnessAll(value);
                            repaint();

                        }

                    });

                    frame.add(label, BorderLayout.CENTER);
                    frame.add(slider, BorderLayout.SOUTH);
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    frame.pack();
                    frame.setVisible(true);
                    break;
                default:
                    break;
            }
        } else {

            label.setOpaque(true);
            slider.setMajorTickSpacing(5);
            slider.setMinorTickSpacing(1);
            slider.setPaintLabels(true);
            slider.setPaintTicks(true);
            slider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    JSlider source = (JSlider) e.getSource();
                    final int value = source.getValue();
                    image.adjustBrightness(value);
                    repaint();

                }

            });

            frame.add(label, BorderLayout.CENTER);
            frame.add(slider, BorderLayout.SOUTH);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);

        }
    }

    public void pixellate() {

        if (isColorImg == true) {

            String val1 = JOptionPane.showInputDialog("Enter desired pixel height:");
            int c_offset = Integer.parseInt(val1);
            String val2 = JOptionPane.showInputDialog("Enter desired pixel width:");
            int r_offset = Integer.parseInt(val2);
            colorImg.pixellateColor(c_offset, r_offset);
            repaint();
        } else {
            String val1 = JOptionPane.showInputDialog("Enter desired pixel height:");
            int r_offset = Integer.parseInt(val1);
            if (640 % r_offset != 0) {
                while (640 % r_offset != 0) {

                    val1 = JOptionPane.showInputDialog("Enter again(Enter a divisor of 640!):");
                    r_offset = Integer.parseInt(val1);
                    if (640 % r_offset == 0) {
                        break;
                    }

                }

            }
            String val2 = JOptionPane.showInputDialog("Enter desired pixel width:");
            int c_offset = Integer.parseInt(val2);

            if (480 % c_offset != 0) {
                while (480 % c_offset != 0) {

                    val2 = JOptionPane.showInputDialog("Enter again(Enter a divisor of 480!):");
                    c_offset = Integer.parseInt(val2);
                    if (480 % c_offset == 0) {
                        break;
                    }

                }

            }
            image.pixellate(r_offset, c_offset);
            repaint();
        }
    }

    public void toBandW() {
        if (isColorImg == true) {
            colorImg.toBandWColor();
            repaint();
        } else {
            image.toBandW();
            repaint();
        }
    }

    public void quantize() {
        if (isColorImg == true) {

            String value = JOptionPane.showInputDialog("Enter value:");
            int threshold = Integer.parseInt(value);
            /**
             * * if (256 % threshold != 0) {
             *
             * while (256 % threshold != 0) {
             *
             * value = JOptionPane.showInputDialog("Enter again (Enter a divisor
             * of 256):"); threshold = Integer.parseInt(value); if (256 %
             * threshold == 0) { break; } }
             *
             * }
             */

            colorImg.quantizeColor(threshold);
            repaint();
        } else {
            String value = JOptionPane.showInputDialog("Enter value:");
            int threshold = Integer.parseInt(value);
            /**
             * if (256 % threshold != 0) {
             *
             * /** while (256 % threshold != 0) {
             *
             * value = JOptionPane.showInputDialog("Enter again (Enter a divisor
             * of 256):"); threshold = Integer.parseInt(value); if (256 %
             * threshold == 0) { break; } }
             *
             * }
             */
            image.quantize(threshold);
            repaint();
        }
    }

    public void move() {
        String val1 = JOptionPane.showInputDialog("Enter desired horizontal shift:");
        int move_x = Integer.parseInt(val1);
        String val2 = JOptionPane.showInputDialog("Enter desired vertical shift:");
        int move_y = Integer.parseInt(val2);
        if (isColorImg == true) {

            colorImg.moveColor(move_x, move_y);
            repaint();

        } else {
            image.move(move_x, move_y);
            repaint();
        }

    }

    public void crop() {

        JOptionPane.showMessageDialog(null, "Click Anywhere To Intiate the Cropping Process!");

        if (isColorImg == true) {
            cropMouseEvent = true;   //Cropping activated
        } else {
            cropMouseEvent = true;
        }

        repaint();

    }

    public void scale() {
        if (isColorImg == true) {
            colorImg.scaleColor();
            repaint();
        } else {
            image.scale();
            repaint();
        }

    }

    public void HistStretch() {
        // String value = JOptionPane.showInputDialog("Enter value:");
        // float shift = Float.parseFloat(value);
        if (isColorImg == true) {

            colorImg.HistStretchColor();
            repaint();

        } else {
            image.HistStretch();
            repaint();
        }
    }

    public void HistEqualize() {
     
        if (isColorImg == true) {
            colorImg.HistEqualizeColor();
            repaint();
        } else {
            image.HistEqualize();
            repaint();
        }
    }

    public void averageFilter() {
        short[][] templateAvg = {
            {1, 1, 1},
            {1, 1, 1},
            {1, 1, 1}

        };
        if (isColorImg == true) {
            colorImg.FilterColor(templateAvg);
            repaint();

        } else {
            image.Filter(templateAvg);
            repaint();
        }

    }

    public void gaussian() {
        short[][] templateGaussian = {
            {1, 2, 1},
            {2, 4, 2},
            {1, 2, 1}

        };

        if (isColorImg == true) {
            colorImg.FilterColor(templateGaussian);
            repaint();

        } else {
            image.Filter(templateGaussian);
            repaint();
        }

    }

    public void medianFilter() {
        if (isColorImg == true) {
            colorImg.medianFilterColor();
            repaint();

        } else {
            image.medianFilter();
            repaint();
        }

    }

    public void modeFilter() {

        if (isColorImg == true) {

            colorImg.modeFilterColor();
            repaint();
        } else {
            image.modeFilter();
            repaint();
        }

    }

    public void kValueFilter() {
        if (isColorImg == true) {
            colorImg.kValueFilterColor();
            repaint();

        } else {
            image.kValueFilter();
            repaint();
        }

    }

    public void laplacian() {
        final ImageIcon icon = new ImageIcon("Info_icon.png");
        String[] button = {"Template 1", "Template 2"};
        int option = JOptionPane.showOptionDialog(null, "Select an Option", "Select Option", JOptionPane.INFORMATION_MESSAGE, 0, icon, button, button[1]);
        short[][] template1 = {
            {-1, -1, -1},
            {-1, 9, -1},
            {-1, -1, -1}
        };
        short[][] template2 = {
            {0, 1, 0},
            {1, -4, 1},
            {0, 1, 0}
        };
        if (isColorImg == true) {
            if (option == 0) {
                colorImg.laplacianEdgeDitectionColor(template1);
            } else if (option == 1) {
                colorImg.laplacianEdgeDitectionColor(template2);
            } else {

            }
        } else {
            if (option == 0) {
                image.laplacianEdgeDitection(template1);
            } else if (option == 1) {
                image.laplacianEdgeDitection(template2);
            } else {

            }
        }
        repaint();

    }

    public void sobelEdgeDetection() {
        if (isColorImg == true) {
            colorImg.sobelEdgeDetectionColor();
            repaint();

        } else {
            image.sobelEdgeDetection();
            repaint();
        }

    }

    public void sharpen() {
        if (isColorImg == true) {
            colorImg.sharpenColor();
            repaint();

        } else {
            image.sharpen();
            repaint();
        }

    }

    public void unsharpenMask() {
        if (isColorImg == true) {
            colorImg.unsharpenMaskolor();
            repaint();

        } else {
            image.unsharpenMask();
            repaint();
        }

    }

    public void pencilSketch() {
        Integer[] values = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 125, 150, 175, 200, 225, 250};
        JComboBox comboBox = new JComboBox(values);
        JLabel lbltext = new JLabel();
        JFrame frame2 = new JFrame();

        lbltext.setOpaque(true);

        frame2.setTitle("Select Sketch Value");
        frame2.setLocation(200, 200);

        comboBox.setSelectedIndex(0);
        comboBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                //Execute when a selection has been made
                if (e.getSource() == comboBox) {

                    JComboBox cb = (JComboBox) e.getSource();
                    Integer point = (Integer) cb.getSelectedItem();
                    if (isColorImg == true) {

                        colorImg.pencilSketchColor(point);

                        repaint();

                    } else {

                        image.pencilSketch(point);
                        repaint();

                    }
                }

            }
        });

        frame2.add(lbltext, BorderLayout.CENTER);
        frame2.add(comboBox, BorderLayout.SOUTH);
        frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame2.pack();
        frame2.setVisible(true);

    }

    public void autoQuantize() {
        if (isColorImg == true) {
            colorImg.autoQuantizeColor();
            repaint();

        } else {
            image.autoQuantize();
            repaint();
        }
    }

    public void rotate() {
        String val1 = JOptionPane.showInputDialog("Enter desired angle:");
        double angle = Double.parseDouble(val1);
        angle = Math.toRadians(angle);
        if (isColorImg == true) {
            colorImg.rotate(angle);
            repaint();
        } else {
            image.rotate(angle);
            repaint();
        }
    }

    public void flip() {
        final ImageIcon icon = new ImageIcon("Info_icon.png");
        String[] button = {"Flip Horizontal", "Flip Vertical"};
        int option = JOptionPane.showOptionDialog(null, "Select an Option", "Flip", JOptionPane.INFORMATION_MESSAGE, 0, icon, button, button[1]);
        if (isColorImg == true) {

            colorImg.Flip(option);
            repaint();

        } else {
            image.Flip(option);
            repaint();
        }
    }

    public void fade() {

        if (isColorImg == true) {
            final ImageIcon icon = new ImageIcon("Info_icon.png");
            String[] button = {"Fade Yellow", "Fade Green", "Fade Pink", "Fade Purple", "Fade Cyan", "Cancel"};
            int option = JOptionPane.showOptionDialog(null, "Select an Option", "Fade", JOptionPane.INFORMATION_MESSAGE, 0, icon, button, button[5]);
            colorImg.FadeColor(option);
            repaint();

        } else {
            image.Fade();
            repaint();

        }

    }

    public void warp() {

        if (isColorImg == true) {
            colorImg.warpColor();
            repaint();

        } else {
            image.warp();
            repaint();

        }

    }

    public void toGreyScale() {//insert chack for raw
        if (isColorImg == true) {
            colorImg.toGreyScale();
            repaint();
        } else {

            JOptionPane.showMessageDialog(this, "Not Applicable With RAW Image Format!", "Convert to GreyScale", JOptionPane.ERROR_MESSAGE);

        }
    }

    public void colorPencil() {
        if (isColorImg == true) {
            Integer[] values = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 125, 150, 175, 200, 225, 250};
            JComboBox comboBox = new JComboBox(values);
            JLabel lbltext = new JLabel();
            JFrame frame2 = new JFrame();

            lbltext.setOpaque(true);

            frame2.setTitle("Select Sketch Value");
            frame2.setLocation(200, 200);

            comboBox.setSelectedIndex(0);
            comboBox.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    //Execute when a selection has been made
                    if (e.getSource() == comboBox) {

                        JComboBox cb = (JComboBox) e.getSource();
                        Integer point = (Integer) cb.getSelectedItem();

                        colorImg.colorPencilSketch(point);

                        repaint();

                    }

                }
            });

            frame2.add(lbltext, BorderLayout.CENTER);
            frame2.add(comboBox, BorderLayout.SOUTH);
            frame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame2.pack();
            frame2.setVisible(true);

        } else {

            JOptionPane.showMessageDialog(this, "Not Applicable With RAW Image Format!", "Color Pencil Sketch Effect", JOptionPane.ERROR_MESSAGE);

        }
    }

    public void undo() {

        if (isColorImg == true) {
            colorImg.undoColor();
            repaint();

        } else {
            image.undo();
            repaint();

        }
    }

    public void redo() {

        if (isColorImg == true) {
            colorImg.redoColor(); 
            repaint();

        } else {
            image.redo();
            repaint();

        }

    }

}
