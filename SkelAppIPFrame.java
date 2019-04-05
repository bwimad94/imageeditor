/*----------------------------------------------------------------------------*\
 *
 * @(#)SkelAppIPFrame.java  2.0
 *
 * Version 2.0 - original version modified to use Swing components
 *
 * The <code>SkelAppIPFrame</code> class represents a window frame fitted
 * with a window panel for drawing on, and with a simple menu.
 *
 * @version 1.0, 24/02/99
 * @version SkelAppIPFrame 1.0, 27/09/02
 *          --> mods from SkelApp2DGFrame v1.0:	change of file name stub from SkelApp2DGFrame to SkelAppIPFrame
 * @version 2.0, 11/01/09
 *          --> modified by Cathy French to use Swing components
 * @author  Claude C. Chibelushi
 * @since   JDK1.4
 *----------------------------------------------------------------------------*/

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * A <code>SkelAppIPFrame</code> is a window with a title and a border. It is
 * fitted with a window panel for drawing on, and with a simple menu.
 *
 * @version 2.0, 11/01/09
 * @author Claude C. Chibelushi, modified Cathy French
 * @since JDK1.4
 */
public class SkelAppIPFrame extends JFrame {

    // drawing area
    private SkelAppIPPanel displayPanel;

    // menu items
    private JMenuItem fileOpen,
           
            saveAs,
            exitApp,
            undo,
            undoNew,
            redo,
            /* VARIABLES FOR STUDENT MENUS */
            imageInverse,
            adjustBrightness,
            bandw,
            quantize,
            pixellate,
            histStretch,
            geoTransform,
            move,
            crop,
            histEqualization,
            smoothing,
            avgFilter,
            median,
            mode,
            kValue,
            gaussian,
            edgeDetect,
            laplacian,
            sobel,
            sharpen,
            filter,
            unsharpenMask,
            pencilSketch,
            geoTransformationsAdvanced,
            scale,
            rotate,
            autoQuantize,
            fading,
            warp,
            morph,
            imageFlip,
            colorPencilSketch;
    /* END OF VARIABLES FOR STUDENT MENUS */

    // file chooser
    JFileChooser fc = new JFileChooser();
   // private boolean isColorImg=false;
    //private boolean fadeSelected=false;
    public static File file;
    /**
     * Creates a window
     *
     * @param commandArgs command line arguments for application.
     * @since JDK1.1
     */
    public static void main(String[] commandArgs) {
        new SkelAppIPFrame();

    }

    /**
     * Constructs a window (frame + panel + menu)
     *
     * @since JDK1.4
     */
    public static void setUIFont(javax.swing.plaf.FontUIResource f) {
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, f);
            }
        }

    }

    public SkelAppIPFrame() {
        // set frame title

        super("Pixel™");
        setUIFont(new javax.swing.plaf.FontUIResource("Georgia", Font.PLAIN, 15));
        setIconImage(new ImageIcon("logo4.png").getImage());
        // set application to exit when frame is closed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // add window listener
        addWindowListenerToFrame();

        // add menu
        addMenu();

        // set frame size
        Dimension screenDimensions = getToolkit().getScreenSize();
        setSize(screenDimensions.width, screenDimensions.height);

        // add drawing area in centre of frame
        displayPanel = new SkelAppIPPanel();
        getContentPane().add(displayPanel);

        // show frame
        setVisible(true);
    }

    /**
     * Adds a window listener to the window frame
     *
     * @since JDK1.1
     */
    public void addWindowListenerToFrame() {
        addWindowListener(
                // use argument which is an object of an anonymous subclass of WindowAdapter
                new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    /**
     * Adds a menu to the window frame
     *
     * @since JDK1.4
     */
    public void addMenu() {
        // create and set menu bar
        JMenuBar frameMenuBar = new JMenuBar();
        setJMenuBar(frameMenuBar);

        // create pull-down menu and attach to menu bar
        JMenu fileMenu = new JMenu("File");
        frameMenuBar.add(fileMenu);

        // create menu items for "File" pull-down menu
        fileOpen = new JMenuItem("Open File ", KeyEvent.VK_O);
       undo = new JMenuItem("Reload ", KeyEvent.VK_O);
       
        saveAs = new JMenuItem("Save As ...", KeyEvent.VK_S);
        exitApp = new JMenuItem("Exit", KeyEvent.VK_X);

        // attach menu items to pull-down menu
        fileMenu.add(fileOpen);
        fileMenu.addSeparator();
        fileMenu.add(undo);
        fileMenu.addSeparator();
        fileMenu.add(saveAs);
        fileMenu.addSeparator();
        fileMenu.add(exitApp);

        // create menu-action listener, and add it to each menu item
        SkelAppMenuDispatcher menuListener = new SkelAppMenuDispatcher();
        fileOpen.addActionListener(menuListener);
       
        undo.addActionListener(menuListener);
        saveAs.addActionListener(menuListener);
        exitApp.addActionListener(menuListener);

        addStudentMenus(frameMenuBar, menuListener);
    }

    /*----------------------------------------------------------------------------\
     *                                                                            |
     *                             STUDENT MENUS                                  |
     *                                                                            |
     *----------------------------------------------------------------------------*/
    /**
     * Adds student menus to the frame's menu bar
     *
     * @param frameMenuBar menu bar.
     * @param menuListener menu listener.
     * @since JDK1.4
     */
    public void addStudentMenus(JMenuBar frameMenuBar, SkelAppMenuDispatcher menuListener) {
        // STRINGS THAT WILL APPEAR ON THE MENU
        // TODO: CUSTOMISE THE TEXT BETWEEN DOUBLE QUOTES AS APPROPRIATE

        String basicTechLabel = "Basic Techniques",
                imageInverseLabel = "Invert Colors",
                adjustBrightnessLabel = "Adjust Brightness",
                bandwLabel = "Binarize",
                quantizeLabel = "Quantize",
                pixellationLabel = "Pixellation",
                histStretchLabel = "Histogram Stretch",
                geoTransformLabel = "Geometric Transformations",
                moveLabel = "Move",
                cropLabel = "Crop",
                furtherTechLabel = "Further Techniques",
                histEqualizationLabel = "Histogram Equalization",
                smoothingLabel = "Smoothing",
                avgFilterLabel = "Average Filter",
                medianLabel = "Median Filter",
                modeLabel = "Mode Filter",
                kValueLabel = "K-Value Filter",
                gaussianLabel = "Gaussian Filter",
                edgeDetectLabel = "Edge Detection",
                laplacianLabel = "Laplacian Filter",
                sobelLabel = "Sobel Filter",
                sharpenLabel = "Sharpen",
                filterLabel = "Filter",
                unsharpenMaskLabel = "Unsharp Masking",
                advancedTechLabel = "Advanced Techniques",
                pencilSketchLabel = "Pencil Sketch",
                geoTransformationsAdvanced = "Geometric Transformations",
                scaleupLabel = "Scaling",
                rotateLabel = "Rotation",
                autoQuantizeLabel = "Automatic Bunching",
                fadingLabel = "Fading",
                challengingTechLabel = "Challenging Techniques",
                warpLabel = "Warping",
                morphLabel = "Grey Scale",
                imageFlipLabel = "Image Flip",
                colorPencilSketchLabel = "Color Pencil Sketch"
                ,undoNewLabel="Undo",
                redoLabel="Redo",
                undoredoLabel="Undo/Redo";
        // END OF MENU STRINGS
        /*----------------------------------------------------------------------------*/

        // create pull-down menus and attach to menu bar
        JMenu basic = new JMenu(basicTechLabel);
        JMenu further = new JMenu(furtherTechLabel);
        JMenu advanced = new JMenu(advancedTechLabel);
        JMenu challenge = new JMenu(challengingTechLabel);
        frameMenuBar.add(basic);
        frameMenuBar.add(further);
        frameMenuBar.add(advanced);
        frameMenuBar.add(challenge);

        // create menu items for <studentMenu1> pull-down menu
        imageInverse = new JMenuItem(imageInverseLabel, KeyEvent.VK_U);
        adjustBrightness = new JMenuItem(adjustBrightnessLabel, KeyEvent.VK_V);

        bandw = new JMenuItem(bandwLabel, KeyEvent.VK_X);
        quantize = new JMenuItem(quantizeLabel, KeyEvent.VK_X);
        pixellate = new JMenuItem(pixellationLabel, KeyEvent.VK_X);
        histStretch = new JMenuItem(histStretchLabel, KeyEvent.VK_X);
        move = new JMenuItem(moveLabel, KeyEvent.VK_R);
        crop = new JMenuItem(cropLabel, KeyEvent.VK_R);

        JMenu studentMenu1_8 = new JMenu(geoTransformLabel);

        histEqualization = new JMenuItem(histEqualizationLabel, KeyEvent.VK_S);
        JMenu studentMenu2_2 = new JMenu(smoothingLabel);
        avgFilter = new JMenuItem(avgFilterLabel, KeyEvent.VK_S);
        median = new JMenuItem(medianLabel, KeyEvent.VK_S);
        mode = new JMenuItem(modeLabel, KeyEvent.VK_S);
        kValue = new JMenuItem(kValueLabel, KeyEvent.VK_S);
        gaussian = new JMenuItem(gaussianLabel, KeyEvent.VK_S);
        JMenu studentMenu2_3 = new JMenu(edgeDetectLabel);
        laplacian = new JMenuItem(laplacianLabel, KeyEvent.VK_R);
        sobel = new JMenuItem(sobelLabel, KeyEvent.VK_R);

        JMenu studentMenu2_4 = new JMenu(sharpenLabel);
        sharpen = new JMenuItem(filterLabel, KeyEvent.VK_S);
        unsharpenMask = new JMenuItem(unsharpenMaskLabel, KeyEvent.VK_S);

        pencilSketch = new JMenuItem(pencilSketchLabel, KeyEvent.VK_A);
        JMenu studentMenu3_2 = new JMenu(geoTransformationsAdvanced);
        scale = new JMenuItem(scaleupLabel, KeyEvent.VK_A);
        // scaledown = new JMenuItem(scaledownLabel, KeyEvent.VK_M);
        rotate = new JMenuItem(rotateLabel, KeyEvent.VK_A);
        autoQuantize = new JMenuItem(autoQuantizeLabel, KeyEvent.VK_A);
        fading = new JMenuItem(fadingLabel, KeyEvent.VK_A);

        warp = new JMenuItem(warpLabel, KeyEvent.VK_W);
        morph = new JMenuItem(morphLabel, KeyEvent.VK_M);
        imageFlip = new JMenuItem(imageFlipLabel, KeyEvent.VK_M);
        undoNew=new JMenuItem(undoNewLabel, KeyEvent.VK_M);
        redo=new JMenuItem(redoLabel, KeyEvent.VK_M);
        colorPencilSketch = new JMenuItem(colorPencilSketchLabel, KeyEvent.VK_C);
        // attach menu items and cascaded menus to pull-down menus

        basic.add(imageInverse);
        basic.add(adjustBrightness);
        basic.add(bandw);
        basic.add(quantize);
        basic.add(pixellate);
        basic.add(histStretch);
        basic.add(studentMenu1_8);
        studentMenu1_8.add(move);
        studentMenu1_8.add(crop);

        further.add(histEqualization);
        further.add(studentMenu2_2);
        studentMenu2_2.add(avgFilter);
        studentMenu2_2.add(median);
        studentMenu2_2.add(mode);
        studentMenu2_2.add(kValue);
        studentMenu2_2.add(gaussian);
        further.add(studentMenu2_3);
        studentMenu2_3.add(laplacian);
        studentMenu2_3.add(sobel);

        further.add(studentMenu2_4);
        studentMenu2_4.add(sharpen);
        studentMenu2_4.add(unsharpenMask); //Unsharp Masking

        advanced.add(pencilSketch);
        advanced.add(studentMenu3_2);
        studentMenu3_2.add(scale);
        //studentMenu3_2.add(scaledown);
        studentMenu3_2.add(rotate);
        advanced.add(autoQuantize);
        advanced.add(fading);

        challenge.add(warp);
        challenge.add(morph);
        challenge.add(imageFlip);
        JMenu studentMenu4_1=new JMenu(undoredoLabel);
        challenge.add(studentMenu4_1);
        studentMenu4_1.add(undoNew);
        studentMenu4_1.add(redo);
       // challenge.add(undoNew);
       // challenge.add(redo);
        challenge.add(colorPencilSketch);

        // add action listener for menu items
        imageInverse.addActionListener(menuListener);
        adjustBrightness.addActionListener(menuListener);
        bandw.addActionListener(menuListener);
        quantize.addActionListener(menuListener);
        pixellate.addActionListener(menuListener);
        histStretch.addActionListener(menuListener);
        move.addActionListener(menuListener);
        crop.addActionListener(menuListener);
        histEqualization.addActionListener(menuListener);
        avgFilter.addActionListener(menuListener);
        median.addActionListener(menuListener);
        mode.addActionListener(menuListener);
        kValue.addActionListener(menuListener);
        gaussian.addActionListener(menuListener);
        laplacian.addActionListener(menuListener);
        sobel.addActionListener(menuListener);
        sharpen.addActionListener(menuListener);

        unsharpenMask.addActionListener(menuListener);
        pencilSketch.addActionListener(menuListener);
        scale.addActionListener(menuListener);
        //scaledown.addActionListener(menuListener);
        rotate.addActionListener(menuListener);
        autoQuantize.addActionListener(menuListener);
        fading.addActionListener(menuListener);
        warp.addActionListener(menuListener);
        morph.addActionListener(menuListener);
        imageFlip.addActionListener(menuListener);
        undoNew.addActionListener(menuListener);
        redo.addActionListener(menuListener);
        colorPencilSketch.addActionListener(menuListener);

    }

    /**
     * Defines menu dispatcher (as an inner class)
     *
     * @since JDK1.1
     */
    class SkelAppMenuDispatcher implements ActionListener {

        /**
         * Receives menu-selection events and forwards them for servicing.
         *
         * @param event an event.
         * @since JDK1.4
         */
        public void actionPerformed(ActionEvent event) {
            if (event.getSource() instanceof JMenuItem) {
                JMenuItem menuItemObject = (JMenuItem) event.getSource();

                serviceMenuEvent(menuItemObject);
            }
        }

        /**
         * Services menu-selection event.
         *
         * @param menuItemObject a JMenuItem object.
         * @since JDK1.4
         */
        public void serviceMenuEvent(JMenuItem menuItemObject) {
            /*----------------------------------------------------------------------------\
             *                                                                            |
             *                             STUDENT MENU SERVICING                         |
             *                                                                            |
             *----------------------------------------------------------------------------*/

            // CUSTOMISE THE "TO DO" COMMENTS AS APPROPRIATE
            //-----------------------------------------------
            if (menuItemObject == fileOpen) {
                int retVal = fc.showOpenDialog(SkelAppIPFrame.this);
                if (retVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    // load image from file
                    String fileName = file.getName();
                    String type = FilenameUtils.getExtension(fileName);//fileName.substring(fileName.lastIndexOf(".") + 1);
                    if ("raw".equals(type.toLowerCase())) {
                        displayPanel.loadRawImage(file);  //passes only file
                    } else {
                        displayPanel.loadColorImg(file, type); //Passes file and extension
                    }
                   
                }
                
            }   else if (menuItemObject == undo) {
               
                    File file = fc.getSelectedFile();
                    // load image from file
                    String fileName = file.getName();
                    String type = FilenameUtils.getExtension(fileName);//fileName.substring(fileName.lastIndexOf(".") + 1);
                    if ("raw".equals(type.toLowerCase())) {
                        displayPanel.loadRawImage(file);  //passes only file
                    } else {
                        displayPanel.loadColorImg(file, type); //Passes file and extension
                    }
                   
                
                
            } else if (menuItemObject == saveAs) {
                int retVal = fc.showSaveDialog(SkelAppIPFrame.this);
                if (retVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    displayPanel.saveImage(file);
                }
            } else if (menuItemObject == exitApp) {
                // confirmation message box not implemented
                System.exit(0);

            } else if (menuItemObject == imageInverse) {

                displayPanel.invert();
                // TO DO: ADD RELEVANT MENU SERVICING CODE HERE
                // FOR  <studentMenu1_1>
            } else if (menuItemObject == adjustBrightness) {

                displayPanel.adjustBrightness();
                // TO DO: ADD RELEVANT MENU SERVICING CODE HERE
                // FOR  <studentMenu1_2>
            } else if (menuItemObject == pixellate) {
                // TO DO: ADD RELEVANT MENU SERVICING CODE HERE
                // FOR  <studentMenu1_3>
                displayPanel.pixellate();
            } else if (menuItemObject == bandw) {
                // TO DO: ADD RELEVANT MENU SERVICING CODE HERE
                // FOR  <studentMenu1_4>
                displayPanel.toBandW();
            } else if (menuItemObject == quantize) {
                // TO DO: ADD RELEVANT MENU SERVICING CODE HERE
                // FOR  <studentMenu1_4>
                displayPanel.quantize();

            } else if (menuItemObject == move) {
                // TO DO: ADD RELEVANT MENU SERVICING CODE HERE
                // FOR  <studentMenu1_4>
                displayPanel.move();

            } else if (menuItemObject == crop) {
                // TO DO: ADD RELEVANT MENU SERVICING CODE HERE
                // FOR  <studentMenu1_4>
                displayPanel.crop();

            } else if (menuItemObject == scale) {
                // TO DO: ADD RELEVANT MENU SERVICING CODE HERE
                // FOR  <studentMenu1_4>
                displayPanel.scale();

            } else if (menuItemObject == histStretch) {
                // TO DO: ADD RELEVANT MENU StERVICING CODE HERE
                // FOR  <studentMenu1_4>
                displayPanel.HistStretch();

            } else if (menuItemObject == histEqualization) {
                // TO DO: ADD RELEVANT MENU SERVICING CODE HERE
                // FOR  <studentMenu1_4>
                displayPanel.HistEqualize();

            } else if (menuItemObject == avgFilter) {
                // TO DO: ADD RELEVANT MENU SERVICING CODE HERE
                // FOR  <studentMenu2_1_1>
                displayPanel.averageFilter();
            } else if (menuItemObject == autoQuantize) {
                // TO DO: ADD RELEVANT MENU SERVICING CODE HERE
                // FOR  <studentMenu2_1_2>

                displayPanel.autoQuantize();
            } else if (menuItemObject == rotate) {
                // TO DO: ADD RELEVANT MENU SERVICING CODE HERE
                // FOR  <studentMenu2_1_3>
                displayPanel.rotate();
            } else if (menuItemObject == mode) {
                // TO DO: ADD RELEVANT MENU SERVICING CODE HERE
                // FOR  <studentMenu2_2_1>
                displayPanel.modeFilter();
            } else if (menuItemObject == median) {
                // TO DO: ADD RELEVANT MENU SERVICING CODE HERE
                // FOR  <studentMenu2_2_2>
                displayPanel.medianFilter();
            } else if (menuItemObject == kValue) {
                // TO DO: ADD RELEVANT MENU SERVICING CODE HERE
                // FOR  <studentMenu2_2_2>
                displayPanel.kValueFilter();
            } else if (menuItemObject == gaussian) {
                // TO DO: ADD RELEVANT MENU SERVICING CODE HERE
                // FOR  <studentMenu2_2_2>
                displayPanel.gaussian();
            } else if (menuItemObject == laplacian) {
                // TO DO: ADD RELEVANT MENU SERVICING CODE HERE
                // FOR  <studentMenu2_2_2>
                displayPanel.laplacian();
            } else if (menuItemObject == sobel) {
                // TO DO: ADD RELEVANT MENU SERVICING CODE HERE
                // FOR  <studentMenu2_2_2>
                displayPanel.sobelEdgeDetection();
            } else if (menuItemObject == sharpen) {
                // TO DO: ADD RELEVANT MENU SERVICING CODE HERE
                // FOR  <studentMenu2_2_2>
                displayPanel.sharpen();
            } else if (menuItemObject == unsharpenMask) {
                // TO DO: ADD RELEVANT MENU SERVICING CODE HERE
                // FOR  <studentMenu2_2_2>
                displayPanel.unsharpenMask();
            } else if (menuItemObject == pencilSketch) {
                // TO DO: ADD RELEVANT MENU SERVICING CODE HERE
                // FOR  <studentMenu2_2_2>
                displayPanel.pencilSketch();
            } else if (menuItemObject == fading) {
                // TO DO: ADD RELEVANT MENU SERVICING CODE HERE
                // FOR  <studentMenu2_2_2>
                
                 displayPanel.fade();
            } else if (menuItemObject == warp) {
                // TO DO: ADD RELEVANT MENU SERVICING CODE HERE
                // FOR  <studentMenu2_2_2>
                displayPanel.warp();
            } else if (menuItemObject == morph) {
                // TO DO: ADD RELEVANT MENU SERVICING CODE HERE
                // FOR  <studentMenu2_2_2>
                displayPanel.toGreyScale();
            } else if (menuItemObject == imageFlip) {
                // TO DO: ADD RELEVANT MENU SERVICING CODE HERE
                // FOR  <studentMenu2_2_2>
                displayPanel.flip();
            }else if (menuItemObject == undoNew) {
                // TO DO: ADD RELEVANT MENU SERVICING CODE HERE
                // FOR  <studentMenu2_2_2>
                displayPanel.undo();
            } else if (menuItemObject == redo) {
                // TO DO: ADD RELEVANT MENU SERVICING CODE HERE
                // FOR  <studentMenu2_2_2>
                displayPanel.redo();
            } else if (menuItemObject == colorPencilSketch) {
                // TO DO: ADD RELEVANT MENU SERVICING CODE HERE
                // FOR  <studentMenu2_2_2>
                displayPanel.colorPencil();
            }
        }
    }
}
