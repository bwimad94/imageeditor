/*----------------------------------------------------------------------------*\
 *
 * @(#)RawImage.java    2.0
 *
 * Version 2.0
 *
 * The <code>RawImage</code> class represents a grey-scale image.
 *
 * @version 1.0, 15/10/02
 * @author  Claude C. Chibelushi
 * @version 2.0, 11/01/09
 * @author  Cathy French
 *          --> mods from  v1.0:    update to use Swing Components
 * @since     JDK1.r
 *----------------------------------------------------------------------------*/

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.Arrays;
import java.util.Scanner;
import javax.swing.*;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;

/**
 * A <code>RawImage</code> is a grey-scale image.
 *
 * @version 1.0, 15/10/02
 * @author Claude C. Chibelushi
 * @since JDK1.1
 */
public class RawImage {

    private int imWidth = 640;  // default image width
    private int imHeight = 480; // default image height
    private int imDepth = 256;  // default image depth
    private int newWidth = 5000;
    private int newHeight = 5000;
    private int newDepth = 5000;
    private short rawPixelArray2D[][];  // src pixel array
    private short blendArray[][];
    public short outputImg[][];//array of image pixels (to contain grey-scale bitmap)
    public short effectsArray[][];//pixel array with effects applied 

    // buffer for converting between raw image bitmap and display image object
    // buffer entries are pixel colour values computed using colour model
    private int imConvBuf[];
    private int imConvBufNew[];
    private Image dispIm;  // display image
    private ColorModel colMod = ColorModel.getRGBdefault(); //colour model of display image

    /**
     * Constructs and initializes a grey-scale image object
     *
     * @param file Reference to input file
     * @since JDK1.r
     */
    public RawImage(File file) {
        outputImg = new short[imHeight][imWidth];
        rawPixelArray2D = new short[newHeight][newWidth]; // create pixel array
        effectsArray = new short[newHeight][newWidth];
        blendArray = new short[newHeight][newWidth];
        imConvBuf = new int[imHeight * imWidth];

        load(file); // read data from file into raw bitmap array
        arrayToDispImage();// convert raw bitmap into display image object

    }

    /**
     * Constructs and initializes a grey-scale image object
     *
     * @param rawGreyBitMap 2D array containing raw bitmap.
     * @param width width of 2D array.
     * @param height height of 2D array.
     * @since JDK1.1
     */
    public RawImage(short rawGreyBitMap[][], int width, int height) {
        outputImg = rawGreyBitMap;
        rawPixelArray2D = rawGreyBitMap;
        effectsArray = rawGreyBitMap;

        imWidth = width;
        imHeight = height;

        imConvBuf = new int[imHeight * imWidth];

        arrayToDispImage(); // convert raw bitmap into display image object
    }

    /**
     * Displays image
     *
     * @param gc graphic object.
     * @since JDK1.1
     */
    public void display(Graphics gc) {
        gc.drawImage(dispIm, 0, 0, null);
    }

    /**
     * Loads image from file
     *
     * @param file
     * @since JDK1.1
     */
    public void load(File file) {
        // load pixel array from file
        try {
            DataInputStream inputStream = new DataInputStream(new FileInputStream(file));

            try {

                for (int row = 0; row < newHeight; row++) {

                    for (int col = 0; col < newWidth; col++) {

                        rawPixelArray2D[row][col] = 255;
                    }

                }
                // read each byte from binary file into raw bitmap
                for (int row = 0; row < imHeight; ++row) {
                    for (int col = 0; col < imWidth; ++col) {
                        outputImg[row][col] = (short) inputStream.readUnsignedByte();
                        rawPixelArray2D[row][col] = outputImg[row][col];
                    }
                }
            } catch (EOFException e) {
                System.out.println("File shorter than expected. End of reading from file ");
            }

            inputStream.close();
        } catch (IOException e2) {
            System.out.println("Error reading input from file ");
        }
    }

    public void loadFileTWo(File file) {
        // load pixel array from file
        try {
            DataInputStream inputStream = new DataInputStream(new FileInputStream(file));

            try {

                // read each byte from binary file into raw bitmap
                for (int row = 0; row < imHeight; ++row) {
                    for (int col = 0; col < imWidth; ++col) {
                        blendArray[row][col] = (short) inputStream.readUnsignedByte();

                    }
                }
            } catch (EOFException e) {
                System.out.println("File shorter than expected. End of reading from file ");
            }

            inputStream.close();
        } catch (IOException e2) {
            System.out.println("Error reading input from file ");
        }
    }

    /**
     * Saves image to file
     *
     * @param file Reference to input file
     * @since JDK1.r
     */
    public void save(File file) {
        // save pixel array to file
        try {
            DataOutputStream outputStream
                    = new DataOutputStream(new FileOutputStream(file));

            // write each byte from raw bitmap into binary file
            for (int row = 0; row < imHeight; ++row) {
                for (int col = 0; col < imWidth; ++col) {
                    outputStream.writeByte((int) outputImg[row][col]);
                }
            }
            outputStream.close();
        } catch (IOException e) {
            System.out.println("Error writing output to file ");
        }
    }

    /**
     * Converts raw grey pixel array into display Image object
     *
     * @param none.
     * @since JDK1.1
     */
    private void arrayToDispImage() {
        for (int row = 0; row < imHeight; row++) {
            int colOffset = row * imWidth;
            for (int col = 0; col < imWidth; col++) {
                imConvBuf[col + colOffset] = 0xFF000000
                        | (outputImg[row][col] << 16)
                        | (outputImg[row][col] << 8)
                        | outputImg[row][col];
            }
        }
        MemoryImageSource MemSource = new MemoryImageSource(imWidth, imHeight, colMod, imConvBuf, 0, imWidth);
        dispIm = Toolkit.getDefaultToolkit().createImage(MemSource);
    }

    public void invert() {

        for (int row = 0; row < imHeight; row++) {

            for (int col = 0; col < imWidth; col++) {
                outputImg[row][col] = (short) (255 - outputImg[row][col]);

            }

        }

        redoArray(outputImg, effectsArray);
        arrayToDispImage();//Convert pixel array to image object
    }

    public void adjustBrightness(float value) {//take input

        for (int row = 0; row < imHeight; row++) {

            for (int col = 0; col < imWidth; col++) {

                if (outputImg[row][col] + value < 0) {
                    outputImg[row][col] = 0;
                } else if (outputImg[row][col] + value > 255) {
                    outputImg[row][col] = 255;
                } else {
                    outputImg[row][col] += value;
                }

            }
        }
        redoArray(outputImg, effectsArray);
        arrayToDispImage();

    }

    public void pixellate(int r_offset, int c_offset) {

        int k = 0;
        int n = 1;

// Loop through every r_offset by  c_offset  pixels, in both x and y directions
        for (int row = 0; row < imHeight; row += r_offset) {
            for (int col = 0; col < imWidth; col += c_offset) {

                short[] pixel = new short[n];
                // Copy the pixel
                pixel[k] = outputImg[row][col];

                //Paste the pixel onto the surrounding  r_offset by  c_offset neighbors
                // Also make sure that our loop never goes outside the bounds of the image
                for (int out_r = row; (out_r < row + r_offset) && (out_r < imHeight); out_r++) {
                    for (int out_c = col; (out_c < col + c_offset) && (out_c < imWidth); out_c++) {

                        outputImg[out_r][out_c] = pixel[k];

                    }

                }
            }
        }
        redoArray(outputImg, effectsArray);
        arrayToDispImage();

    }

    public void toBandW() {

        final short THRESHOLD = 128;
        short min = 0;
        short max = 255;

        for (int row = 0; row < imHeight; row++) {

            for (int col = 0; col < imWidth; col++) {

                outputImg[row][col] = (outputImg[row][col]) > THRESHOLD ? max : min;

            }
        }
        redoArray(outputImg, effectsArray);
        arrayToDispImage();
    }

    public void quantize(int threshold) {

        for (int row = 0; row < imHeight; row++) {

            for (int col = 0; col < imWidth; col++) {

                outputImg[row][col] = (short) ((int) (outputImg[row][col] / threshold) * threshold);

            }
        }
        redoArray(outputImg, effectsArray);
        arrayToDispImage();

    }

    public void move(int R_offset, int C_offset) {

        int srcrow, srccol, h, w;
        h = 2000;//(int) (imHeight * R_offset) gives a heap space error;
        w = 2000;//(int) (imWidth * C_offset);
        short[][] temp = new short[h][w];

        for (int row = 0; row < h; row++) {

            for (int col = 0; col < w; col++) {

                srcrow = row - R_offset;
                srccol = col - C_offset;

                if ((srcrow >= 0 && srcrow < imHeight) && (srccol >= 0 && srccol < imWidth)) {
                    temp[row][col] = outputImg[srcrow][srccol];
                } else {
                    temp[row][col] = 255;
                }

            }
        }
        imHeight = h;
        imWidth = w;
        outputImg = temp;
        redoArray(outputImg, effectsArray);
        imConvBuf = new int[imHeight * imWidth];// Panel size is altered to match the output image

        arrayToDispImage();
    }

    public void crop(int rowPoint, int colPoint) {//Recives inputs from the mouse event

        String val1 = JOptionPane.showInputDialog("Enter desired height:");
        int cropHeight = Integer.parseInt(val1);
        String val2 = JOptionPane.showInputDialog("Enter desired width:");
        int cropWidth = Integer.parseInt(val2);
        short[][] temp = new short[imHeight][imWidth];

        for (int r = 0; r < imHeight; r++) {
            for (int c = 0; c < imWidth; c++) {

                temp[r][c] = 255;//Setting the background to white

            }
        }

        for (int row = rowPoint; row < cropHeight + rowPoint; row++) {

            for (int col = colPoint; col < cropWidth + colPoint; col++) {

                temp[row - rowPoint][col - colPoint] = rawPixelArray2D[row][col];

            }

        }

        outputImg = temp;
        redoArray(outputImg, effectsArray);
        arrayToDispImage();
    }

    public void scale() {

        String[] button = {"Maximize", "Minimize"};

        final ImageIcon icon = new ImageIcon("Info_icon.png");
        int option = JOptionPane.showOptionDialog(null, "Select an Option", "Select Option", JOptionPane.INFORMATION_MESSAGE, 0, icon, button, button[1]);
        int h, w, srcRow, srcCol;
        short[][] temp;
        if (option == 0) {

            String val = JOptionPane.showInputDialog("Enter Scale Height:");
            int height = Integer.parseInt(val);
            String val2 = JOptionPane.showInputDialog("Enter Scale Height:");
            int width = Integer.parseInt(val2);
            h = (int) (imHeight * height);
            w = (int) (imWidth * width);
            temp = new short[h][w];
            for (int row = 0; row < h; row++) {

                for (int col = 0; col < w; col++) {

                    srcRow = row / height;
                    srcCol = col / width;

                    temp[row][col] = outputImg[srcRow][srcCol];

                }

            }
            imHeight = h;
            imWidth = w;
            outputImg = temp;
            redoArray(outputImg, effectsArray);
            imConvBuf = new int[imHeight * imWidth];

            arrayToDispImage();
        } else if (option == 1) {

            String minval = JOptionPane.showInputDialog("Enter Scale Height:");
            int minheight = Integer.parseInt(minval);
            String minval2 = JOptionPane.showInputDialog("Enter Scale Height:");
            int minwidth = Integer.parseInt(minval2);
            h = (int) (imHeight / minheight);
            w = (int) (imWidth / minwidth);
            temp = new short[imHeight][imWidth];
            for (int r = 0; r < imHeight; r++) {
                for (int c = 0; c < imWidth; c++) {

                    temp[r][c] = 255;//Setting the background to white

                }
            }

            for (int row = 0; row < h; row++) {

                for (int col = 0; col < w; col++) {

                    srcRow = row * minheight;
                    srcCol = col * minwidth;

                    temp[row][col] = outputImg[srcRow][srcCol];

                }

            }

            outputImg = temp;
            redoArray(outputImg, effectsArray);//Saving pixel values for redo operation

            arrayToDispImage();
        } else {

        }

    }

    public void HistStretch() {

        int min = (int) (outputImg[0][0]);
        int max = (int) (outputImg[0][0]);
        int stretch_min = 0;
        int stretch_max = 255;

        for (int row = 0; row < imHeight; row++) {

            for (int col = 0; col < imWidth; col++) {
                if (min > outputImg[row][col]) {
                    min = outputImg[row][col];
                }
                if ((max < outputImg[row][col])) {

                    max = outputImg[row][col];

                }

            }
        }

        for (int row = 0; row < imHeight; row++) {

            for (int col = 0; col < imWidth; col++) {

                outputImg[row][col] = (short) ((outputImg[row][col] - min) * ((stretch_max - stretch_min) / (max - min)) + stretch_min);
                if (outputImg[row][col] > 255) {
                    outputImg[row][col] = 255;
                }
                if (outputImg[row][col] < 0) {
                    outputImg[row][col] = 0;
                }

            }
        }
        redoArray(outputImg, effectsArray);
        arrayToDispImage();

    }

    public void HistEqualize() {

        int[] histogram = new int[256];//Changed from the size of imWidth

        for (int row = 0; row < imHeight; row++) {

            for (int col = 0; col < imWidth; col++) {

                ++histogram[outputImg[row][col]];

            }

        }

        int mass = imHeight * imWidth;
        long sum = 0;
        float scale_factor = (float) (255.0 / mass);
        for (int r = 0; r < histogram.length; r++) {
            sum += histogram[r];
            int level = (int) (scale_factor * sum);
            if (level > 255) {
                level = 255;
            }
            histogram[r] = level;

        }

        for (int row = 0; row < imHeight; row++) {

            for (int col = 0; col < imWidth; col++) {

                outputImg[row][col] = (short) (histogram[outputImg[row][col]]);

            }
        }
        redoArray(outputImg, effectsArray);
        arrayToDispImage();
    }

    public void Filter(short template[][]) {

        int sum = calculateTempSum(template);

        short avg_val;
        for (int row = 1; row < imHeight - 1; row++) {

            for (int col = 1; col < imWidth - 1; col++) {
                avg_val = 0;
                for (int r = -1; r < template.length - 1; r++) {

                    for (int c = -1; c < template.length - 1; c++) {

                        avg_val += (short) ((template[r + 1][c + 1] * outputImg[row + r][col + c]));

                    }

                }

                outputImg[row][col] = (short) (avg_val / sum);
            }

        }
        redoArray(outputImg, effectsArray);
        arrayToDispImage();
    }

    public void medianFilter() {

        int w_width = 3;
        int w_height = 3;
        short window[] = new short[w_width * w_height];

        int count;

        for (int row = 1; row < imHeight - 1; row++) {

            for (int col = 1; col < imWidth - 1; col++) {
                count = 0;
                for (int r = -1; r < w_height - 1; r++) {

                    for (int c = -1; c < w_width - 1; c++) {

                        window[count] = outputImg[row + r][col + c];
                        count++;

                    }

                }
                Arrays.sort(window);
                outputImg[row][col] = window[Math.round(w_width * w_height / 2)];//Is it math.floor?
            }
        }
        redoArray(outputImg, effectsArray);
        arrayToDispImage();
    }

    public void modeFilter() {

        int w_width = 3;
        int w_height = 3;
        int window[] = new int[w_width * w_height];
        short[][] tempArray = new short[imHeight][imWidth];

        int count;

        for (int row = 1; row < imHeight - 1; row++) {

            for (int col = 1; col < imWidth - 1; col++) {
                count = 0;
                for (int r = -1; r < w_height - 1; r++) {

                    for (int c = -1; c < w_width - 1; c++) {

                        window[count] = outputImg[row + r][col + c];
                        count++;

                    }

                }

                float value = findMode(window);//Finding the mode for the window

                tempArray[row][col] = (short) (value);

            }
        }
        outputImg = tempArray;
        redoArray(outputImg, effectsArray);

        arrayToDispImage();
    }

    public float findMode(int window[]) {

        int second_counter, counter;
        int mode = 0;
        int modeCount = 0;
        int i = 0;
        int j = 0;
        
        counter = -1;

        for (i = 0; i < window.length; i++) {

            second_counter = 0;
            for (j = 0; j < window.length; j++) {
                if (window[i] == window[j]) {
                    second_counter++;
                }
            }
            if (counter < second_counter) {
                counter = second_counter;
            }
            if (counter == second_counter) {
                mode += window[i];
                modeCount++;

            }

        }

        return mode / modeCount;

    }

    public void kValueFilter() {

        int kVal = 6;
        int w_width = 3;
        int w_height = 3;
        int window[] = new int[w_width * w_height];
        short[][] temp = new short[imHeight][imWidth];
        int count;

        for (int row = 1; row < imHeight - 1; row++) {

            for (int col = 1; col < imWidth - 1; col++) {
                count = 0;
                for (int r = -1; r < w_height - 1; r++) {

                    for (int c = -1; c < w_width - 1; c++) {

                        window[count] = outputImg[row + r][col + c];
                        count++;

                    }

                }

                bubbleSort(window);

                float mean = getKclosest(window, window[4], kVal, window.length);

                temp[row][col] = (short) mean;

            }
        }

        outputImg = temp;

        redoArray(outputImg, effectsArray);
        arrayToDispImage();
    }

    int findMidPoint(int window[], int min, int max, int val) {//Finds the point before which the elements are <=val and after which > val

        if (window[max] <= val) // val is greater than all
        {
            return max;
        }
        if (window[min] > val) // val is smaller than all
        {
            return min;
        }

        // Find the middle point
        int mid = (min + max) / 2;

        /* If val is same as middle element, then return mid */
        if (window[mid] <= val && window[mid + 1] > val) {
            return mid;
        }

        /* If val is greater than window[mid], then either window[mid + 1]
          is ceiling of val or ceiling lies in window[mid+1...high] */
        if (window[mid] < val) {
            return findMidPoint(window, mid + 1, max, val);
        }

        return findMidPoint(window, min, mid - 1, val);

    }

    // This function gets the average of k closest elements in window[].
    // length is the number of elements in window[]
    float getKclosest(int window[], int midPix, int kVal, int length) {
        // Find the crossover point

        int sum = 0;
        int lIndex = findMidPoint(window, 0, length - 1, midPix);

        int rIndex = lIndex + 1;   // Right index to search
        int count = 0; // To keep track of count of elements
        // already scanned

        // Compare elements on left and right of midpoint
        // point to find the k closest elements
        while (lIndex >= 0 && rIndex < length && count < kVal) {
            if (midPix - window[lIndex] < window[rIndex] - midPix) {

                sum += window[lIndex--];
            } else {

                sum += window[rIndex++];
            }
            count++;
        }

        // If there are no more elements on right side, then
        // print left elements
        while (count < kVal && lIndex >= 0) {

            sum += window[lIndex--];
            count++;
        }

        // If there are no more elements on left side, then
        // scan right-side of the array
        while (count < kVal && rIndex < length) {

            sum += window[rIndex++];
            count++;
        }
        float mean = sum / kVal;
        return mean;
    }

    public void sobelEdgeDetection() {
        short[][] newArray = new short[imHeight][imWidth];
        int temp_height = 3;
        int temp_width = 3;
        int[][] templateX = {
            {-1, 0, 1},
            {-2, 0, 2},
            {-1, 0, 1}
        };
        int[][] templateY = {
            {-1, -2, -1},
            {0, 0, 0},
            {1, 2, 1}
        };
        for (int row = 1; row < imHeight - 2; row++) {
            for (int col = 1; col < imWidth - 2; col++) {
                int[][] tempArray = new int[temp_height][temp_width];

                int mag_X = 0;
                int mag_Y = 0;
                for (int x = 0; x < temp_height; x++) {
                    for (int y = 0; y < temp_width; y++) {

                        int xn = row + y - 1;
                        int yn = col + x - 1;

                        tempArray[x][y] = outputImg[xn][yn];
                        mag_X += tempArray[x][y] * templateX[x][y];
                        mag_Y += tempArray[x][y] * templateY[x][y];
                    }
                }

                int magnitude = (int) (Math.sqrt((mag_X * mag_X) + (mag_Y * mag_Y)));
                if (magnitude > 255) {
                    magnitude = 255;
                } else if (magnitude < 0) {      //controling underflow and overflow
                    magnitude = 0;
                }

                int final_magnitude = 255 - magnitude;
                newArray[row][col] = (short) final_magnitude;
            }
        }
        outputImg = newArray;

        redoArray(outputImg, effectsArray);
        arrayToDispImage();

    }

    public void laplacianEdgeDitection(short temp[][]) {

        short[][] newArray = new short[imHeight][imWidth];
        short avg_val;
        for (int row = 1; row < imHeight - 1; row++) {

            for (int col = 1; col < imWidth - 1; col++) {
                avg_val = 0;
                for (int r = -1; r < temp.length - 1; r++) {

                    for (int c = -1; c < temp.length - 1; c++) {

                        avg_val += (short) ((temp[r + 1][c + 1] * outputImg[row + r][col + c]));

                    }

                }

                newArray[row][col] = (short) (avg_val);

            }

        }

        for (int r = 0; r < imHeight; r++) {
            for (int c = 0; c < imWidth; c++) {

                if (newArray[r][c] > 0) {
                    newArray[r][c] = 0;
                } else {
                    newArray[r][c] = (short) (Math.abs(newArray[r][c]));

                }

            }

        }
        outputImg = newArray;
        redoArray(outputImg, effectsArray);
        arrayToDispImage();
    }

    public void sharpen() {

        short[][] templateLaplassian = {
            {-1, -1, -1},
            {-1, 9, -1},
            {-1, -1, -1}

        };

        int sum = calculateTempSum(templateLaplassian);
        short[][] finalImg = new short[imHeight][imWidth];
        short[][] filteredImg = new short[imHeight][imWidth];
        short avg_val;
        for (int row = 1; row < imHeight - 1; row++) {

            for (int col = 1; col < imWidth - 1; col++) {
                avg_val = 0;
                for (int r = -1; r < templateLaplassian.length - 1; r++) {

                    for (int c = -1; c < templateLaplassian.length - 1; c++) {

                        avg_val += (short) ((templateLaplassian[r + 1][c + 1] * outputImg[row + r][col + c]));

                    }

                }

                filteredImg[row][col] = (short) (avg_val / sum);

            }

        }

        for (int row = 0; row < imHeight; ++row) {
            for (int col = 0; col < imWidth; ++col) {
                finalImg[row][col] = (short) (filteredImg[row][col] + outputImg[row][col]);
                if (finalImg[row][col] >= 255) {
                    finalImg[row][col] = 255;
                } else if (finalImg[row][col] < 0) {
                    finalImg[row][col] = 0;
                }

            }
        }

        outputImg = finalImg;
        redoArray(outputImg, effectsArray);
        arrayToDispImage();
    }

    public void unsharpenMask() {

        short[][] tempArray = new short[imHeight][imWidth];
        short[][] mask = new short[imHeight][imWidth];
        for (int row = 0; row < imHeight; ++row) {
            for (int col = 0; col < imWidth; ++col) {

                tempArray[row][col] = outputImg[row][col];
            }

        }

        short[][] templateGaussian = {
            {1, 2, 1},
            {2, 4, 2},
            {1, 2, 1}

        };
        Filter(templateGaussian);

        for (int row = 0; row < imHeight; ++row) {
            for (int col = 0; col < imWidth; ++col) {

                mask[row][col] = (short) (tempArray[row][col] - outputImg[row][col]);
            }

        }

        for (int row = 0; row < imHeight; ++row) {
            for (int col = 0; col < imWidth; ++col) {

                outputImg[row][col] = (short) (tempArray[row][col] + mask[row][col]);
                if (outputImg[row][col] > 255) {
                    outputImg[row][col] = 255;
                }
                if (outputImg[row][col] < 0) {
                    outputImg[row][col] = 0;
                }
            }

        }
        redoArray(outputImg, effectsArray);
        arrayToDispImage();
    }

    public void autoQuantize() {

        int sum = 0;
        int count = 0;

        int[] histogram = new int[256];

        for (int row = 0; row < imHeight; row++) {

            for (int col = 0; col < imWidth; col++) {

                ++histogram[outputImg[row][col]];
                count++;

            }

        }

        for (int i = 0; i < histogram.length; i++) {

            sum += i * histogram[i];

        }

        int small_sum = 0;
        int large_sum = 0;
        float small_mean = 0;
        float large_mean = 0;
        int small_count = 0;
        int large_count = 0;
        float temp_mean = 0;

        float temp_avg = 0;
        float avg = sum / count;
        // System.out.println(avg);
        int j, k;
        float a;
        while (avg - temp_mean >= 1 || (int) (avg) != (int) (temp_mean)) {
            for (j = 0; j < avg; j++) {

                small_sum += j * histogram[j];
                small_count += histogram[j];

            }
            small_mean = small_sum / small_count;
            //System.out.println(small_mean);
            for (k = (short) (avg + 1); k < 256; k++) {

                large_sum += k * histogram[k];
                large_count += histogram[k];
            }
            large_mean = large_sum / large_count;
            //System.out.println(large_mean);
            temp_avg = (large_mean + small_mean) / 2;
            //System.out.println(temp_avg);
            temp_mean = avg;
            avg = temp_avg;
            //System.out.println(avg);
            //System.out.println(temp_mean);
            if ((int) (temp_mean) == (int) (avg)) {
                break;
            }
        }

        for (int row = 0; row < imHeight; row++) {

            for (int col = 0; col < imWidth; col++) {

                outputImg[row][col] = (short) ((int) (outputImg[row][col] / avg) * avg);

            }
        }
        redoArray(outputImg, effectsArray);
        arrayToDispImage();
    }

    public void rotate(double angle) {

        int srcRow;
        int srcCol;

        double cosVal = Math.cos(angle);
        double sinVal = Math.sin(angle);
        double x = 0.5 * (imHeight - 1);//point to rotate around
        double y = 0.5 * (imWidth - 1);//centre of the image
        short[][] temp = new short[imHeight][imWidth];
        for (int row = 0; row < imHeight; row++) {

            for (int col = 0; col < imWidth; col++) {
                double temp_val_a = row - x;
                double temp_val_b = col - y;
                srcRow = (int) (+temp_val_a * cosVal - temp_val_b * sinVal + x);
                srcCol = (int) (+temp_val_a * sinVal + temp_val_b * cosVal + y);
                if (((srcCol >= 0) && (srcCol < imWidth)) && ((srcRow >= 0) && (srcRow < imHeight))) {
                    temp[row][col] = outputImg[srcRow][srcCol];
                } else {
                    temp[row][col] = 255;
                }

            }
        }

        outputImg = temp;
        redoArray(outputImg, effectsArray);//Saving pixel values for redo operation

        arrayToDispImage();

    }

    public void pencilSketch(int pointSize) {

        medianFilter();
        sobelEdgeDetection();
        invert();
        for (int row = 0; row < imHeight; row++) {

            for (int col = 0; col < imWidth; col++) {
                if (outputImg[row][col] > 0) {
                    outputImg[row][col] = (short) (pointSize - outputImg[row][col]);
                }
                if (outputImg[row][col] < 0) {
                    outputImg[row][col] = 0;
                } else {
                    outputImg[row][col] = 200;
                }

            }
        }

        modeFilter();
        redoArray(outputImg, effectsArray);
        arrayToDispImage();

    }

    public void warp() {

        double x = 0;
        double y = 0;
        double midWPoint = 0.5 * (imWidth - 1);
        double midHPoint = 0.5 * (imHeight - 1);
        for (int row = 0; row < imWidth; row++) {
            for (int col = 0; col < imHeight; col++) {
                double tempmidWPoint = row - midWPoint;
                double tempmidHPoint = col - midHPoint;

                double radius = Math.sqrt(tempmidWPoint * tempmidWPoint + tempmidHPoint * tempmidHPoint);
                double angle = Math.sqrt(radius);

                x = (double) (+tempmidWPoint * Math.cos(angle) - tempmidHPoint * Math.sin(angle) + midWPoint);
                y = (double) (+tempmidWPoint * Math.sin(angle) + tempmidHPoint * Math.cos(angle) + midHPoint);
                if (x >= 0 && x < imWidth && y >= 0 && y < imHeight) {
                    outputImg[col][row] = outputImg[(short) y][(short) x];
                } else {
                    outputImg[col][row] = 255;
                }
            }
        }
        redoArray(outputImg, effectsArray);
        arrayToDispImage();

    }

    public void Flip(int option) {
        short[][] newArray = new short[imHeight][imWidth];
        int val = 1;
        if (option == 0) {
            for (int x = 0; x < imHeight; x++) {
                for (int y = 0; y < imWidth; y++) {
                    {
                        newArray[x][y] = outputImg[x][imWidth - y - val];
                    }
                }
            }
        } else {

            for (int x = 0; x < imHeight; x++) {
                for (int y = 0; y < imWidth; y++) {
                    {
                        newArray[x][y] = outputImg[imHeight - x - val][y];
                    }
                }
            }

        }

        outputImg = newArray;

        redoArray(outputImg, effectsArray);

        arrayToDispImage();

    }

    public void Fade() {
        final ImageIcon icon = new ImageIcon("Info_icon.png");
        String[] button = {"Fade Black", "Fade White", "Cancel"};
        int option = JOptionPane.showOptionDialog(null, "Select an Option", "Fade", JOptionPane.INFORMATION_MESSAGE, 0, icon, button, button[2]);
        short[][] blendArrayBlack = new short[imHeight][imWidth];
        populateArray(blendArray, (short) 255);
        populateArray(blendArrayBlack, (short) 0);
        float amount = 0;
        String val1 = "";

        short[][] tempArray = new short[imHeight][imWidth];

        switch (option) {
            case 0:
                val1 = JOptionPane.showInputDialog("Enter amount:(between 0-1)");
                amount = Float.parseFloat(val1);
                for (int row = 0; row < imHeight; ++row) {
                    for (int col = 0; col < imWidth; ++col) {
                        tempArray[row][col] = (short) ((1 - amount) * outputImg[row][col] + amount * blendArrayBlack[row][col]);
                    }
                }
                 outputImg = tempArray;
        redoArray(outputImg, effectsArray);
        arrayToDispImage();
                break;
            case 1:
                val1 = JOptionPane.showInputDialog("Enter amount:(between 0-1)");
                amount = Float.parseFloat(val1);
                for (int row = 0; row < imHeight; ++row) {
                    for (int col = 0; col < imWidth; ++col) {
                        tempArray[row][col] = (short) ((1 - amount) * outputImg[row][col] + amount * blendArray[row][col]);
                    }
                }
                 outputImg = tempArray;
        redoArray(outputImg, effectsArray);
        arrayToDispImage();
                break;
            default:
                break;

        }
       

    }

    public int calculateTempSum(short template[][]) {

        int sum = 0;
        for (int r = 0; r < template.length; r++) {
            for (int c = 0; c < template.length; c++) {
                sum += template[r][c];
            }
        }

        return sum;
    }

    private void bubbleSort(int[] window) {

        int length = window.length;
        int hold = 0;

        for (int i = 0; i < length; i++) {
            for (int j = 1; j < (length - i); j++) {

                if (window[j - 1] > window[j]) {
                    //swap the elements!
                    hold = window[j - 1];
                    window[j - 1] = window[j];
                    window[j] = hold;
                }

            }
        }

    }

    public void undo() {

        for (int row = 0; row < imHeight; row++) {

            for (int col = 0; col < imWidth; col++) {

                outputImg[row][col] = rawPixelArray2D[row][col];
            }

        }

        arrayToDispImage();

    }

    public void redo() {

        for (int row = 0; row < imHeight; row++) {

            for (int col = 0; col < imWidth; col++) {

                outputImg[row][col] = effectsArray[row][col];
            }

        }

        arrayToDispImage();

    }

    public void redoArray(short[][] outputImg, short[][] effectsArray) {

        for (int row = 0; row < imHeight; row++) {

            for (int col = 0; col < imWidth; col++) {

                effectsArray[row][col] = outputImg[row][col];
            }

        }

    }

    public void populateArray(short[][] array, short value) {

        for (int x = 0; x < imHeight; x++) {
            for (int y = 0; y < imWidth; y++) {
                {
                    array[x][y] = value;
                }
            }
        }

    }

}
