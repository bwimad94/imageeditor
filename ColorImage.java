/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author SHADOW
 */
import java.util.Random;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import java.awt.event.*;
import java.util.Arrays;

public class ColorImage {

    /**
     * The display image.
     */
    private ImageIcon displayColorImage;

    /**
     * The image width.
     */
    private int colorWidth;

    /**
     * The image height.
     */
    private int colorHeight;

    /**
     * The buffered Image.
     */
    private BufferedImage colorImage;

    /**
     * The number of color channels. (RGB)
     */
    private int channels;

    /**
     * The pixel array.
     */
    private short[][][] undoArray;
    private short[][][] outputImgColor;
    private short[][][] effectsArrayColor;

    /**
     * The constructor.
     *
     * @param file The file object.
     * @param extension The file extension.
     */
    public ColorImage(File file, String extension) {
        try {
            loadColor(file, extension);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    /**
     * Draws the image using provided Graphics object.
     *
     * @param graphics The Graphics object.
     */
    public void displayImgColor(Graphics graphics) {
        graphics.drawImage(displayColorImage.getImage(), 0, 0, null);
    }

    /**
     * Loads the file.
     *
     * @param file The file object.
     * @param extension The extension.
     * @throws IOException The exception.
     */
    public void loadColor(File file, String extension) throws IOException {
        Iterator imageReaders = ImageIO.getImageReadersBySuffix(extension);

        if (!imageReaders.hasNext()) {
            throw new IOException("Unsupported image format");
        }

        ImageReader imageReader = (ImageReader) imageReaders.next();
        FileImageInputStream fileImageInputStream = new FileImageInputStream(file);

        imageReader.setInput(fileImageInputStream);

        colorWidth = imageReader.getWidth(0);
        colorHeight = imageReader.getHeight(0);
        colorImage = imageReader.read(0);
        fileImageInputStream.close();

        displayColorImage = new ImageIcon(colorImage);
        arrayLoad();
        arrayToDispImage();
    }

    /**
     * Gets the samples.
     */
    private void arrayLoad() {
        WritableRaster writableRaster = colorImage.getRaster();

        channels = writableRaster.getNumBands();
        undoArray = new short[colorWidth][colorHeight][channels];
        outputImgColor = new short[colorWidth][colorHeight][channels];
        effectsArrayColor = new short[colorWidth][colorHeight][channels];
        // Gets the samples and normalize.
        for (int col = 0; col < colorWidth; col++) {
            for (int row = 0; row < colorHeight; row++) {
                for (int channel = 0; channel < channels; channel++) {
                    outputImgColor[col][row][channel] = (short) (writableRaster.getSample(col, row, channel));
                    undoArray[col][row][channel] = outputImgColor[col][row][channel];

                }
            }
        }
    }

    /**
     * updates the display image. Call this method after each operation.
     */
    private void arrayToDispImage() {//try modifying this
        WritableRaster writableRaster = colorImage.getRaster();

        for (int col = 0; col < colorWidth; col++) {
            for (int row = 0; row < colorHeight; row++) {
                for (int channel = 0; channel < channels; channel++) {
                    writableRaster.setSample(col, row, channel, (short) Math.round(outputImgColor[col][row][channel]));
                }
            }
        }
        displayColorImage = new ImageIcon(colorImage);
    }

    public void saveColorImg(File colorFile) {
        try {
            File file = colorFile;
            String filename = file.getName();
            String ext = FilenameUtils.getExtension(filename);

            BufferedImage saveColor = new BufferedImage(colorWidth, colorHeight, BufferedImage.TYPE_INT_RGB);
            /*Delaring and initiating new bufferedImage object*/

            int[] imConvBuf = new int[colorHeight * colorWidth];

            for (int row = 0; row < colorHeight; row++) {
                int colOffset = row * colorWidth;
                for (int col = 0; col < colorWidth; col++) {
                    /*Convert outputImgColor 3D array into imConvBuf 1D array*/
                    imConvBuf[col + colOffset] = (outputImgColor[col][row][0] << 16
                            | outputImgColor[col][row][1] << 8
                            | outputImgColor[col][row][2]);
                }
            }
            saveColor.setRGB(0, 0, colorWidth, colorHeight, imConvBuf, 0, colorWidth);
            /*set imConvBuf to buffered image*/

            ImageIO.write(saveColor, ext, file);
        } catch (IOException e) {
            System.out.println("Error writing output to file ");
        }
    }

    public void adjustBrightnessAll(float value) {

        short minIntensity = 0;
        short maxIntensity = 255;
        for (int col = 0; col < colorWidth; col++) {
            for (int row = 0; row < colorHeight; row++) {
                for (int channel = 0; channel < channels; channel++) {
                    short newIntensity = (short) (outputImgColor[col][row][channel] + value);

                    if (newIntensity > maxIntensity) {
                        outputImgColor[col][row][channel] = maxIntensity;
                    } else if (newIntensity < minIntensity) {
                        outputImgColor[col][row][channel] = minIntensity;
                    } else {
                        outputImgColor[col][row][channel] = newIntensity;
                    }
                }

            }
        }

        redoArrayColor(outputImgColor, effectsArrayColor);

        arrayToDispImage();

    }

    public void adjustBrightnessRed(float value) {
        short minIntensity = 0;
        short maxIntensity = 255;
        for (int col = 0; col < colorWidth; col++) {
            for (int row = 0; row < colorHeight; row++) {
                for (int channel = 0; channel < channels; channel++) {
                    short newIntensity = (short) (outputImgColor[col][row][0] + value);

                    if (newIntensity > maxIntensity) {
                        outputImgColor[col][row][0] = maxIntensity;
                    } else if (newIntensity < minIntensity) {
                        outputImgColor[col][row][0] = minIntensity;
                    } else {
                        outputImgColor[col][row][0] = newIntensity;
                    }
                }
            }
        }

        redoArrayColor(outputImgColor, effectsArrayColor);

        arrayToDispImage();

    }

    public void adjustBrightnessGreen(float value) {
        short minIntensity = 0;
        short maxIntensity = 255;
        for (int col = 0; col < colorWidth; col++) {
            for (int row = 0; row < colorHeight; row++) {
                for (int channel = 0; channel < channels; channel++) {
                    short newIntensity = (short) (outputImgColor[col][row][1] + value);

                    if (newIntensity > maxIntensity) {
                        outputImgColor[col][row][1] = maxIntensity;
                    } else if (newIntensity < minIntensity) {
                        outputImgColor[col][row][1] = minIntensity;
                    } else {
                        outputImgColor[col][row][1] = newIntensity;
                    }
                }
            }
        }

        redoArrayColor(outputImgColor, effectsArrayColor);

        arrayToDispImage();

    }

    public void adjustBrightnessBlue(float value) {
        short minIntensity = 0;
        short maxIntensity = 255;
        for (int col = 0; col < colorWidth; col++) {
            for (int row = 0; row < colorHeight; row++) {
                for (int channel = 0; channel < channels; channel++) {
                    short newIntensity = (short) (outputImgColor[col][row][2] + value);

                    if (newIntensity > maxIntensity) {
                        outputImgColor[col][row][2] = maxIntensity;
                    } else if (newIntensity < minIntensity) {
                        outputImgColor[col][row][2] = minIntensity;
                    } else {
                        outputImgColor[col][row][2] = newIntensity;
                    }
                }
            }
        }

        redoArrayColor(outputImgColor, effectsArrayColor);

        arrayToDispImage();

    }

    public void toBandWColor() {

        final short THRESHOLD = 128;
        short min = 0;
        short max = 255;

        for (int col = 0; col < colorWidth; col++) {
            for (int row = 0; row < colorHeight; row++) {
                for (int channel = 0; channel < channels; channel++) {

                    outputImgColor[col][row][0] = (outputImgColor[col][row][0]) > THRESHOLD ? max : min;
                        outputImgColor[col][row][1] = (outputImgColor[col][row][1]) > THRESHOLD ? max : min;

                            outputImgColor[col][row][channel] = (outputImgColor[col][row][channel]) > THRESHOLD ? max : min;

          

                }
            }
        }
        redoArrayColor(outputImgColor, effectsArrayColor);
        arrayToDispImage();
    }

    public void invertColor() {

        for (int col = 0; col < colorWidth; col++) {
            for (int row = 0; row < colorHeight; row++) {
                 for (int channel = 0; channel < channels; channel++) {
                outputImgColor[col][row][channel] = (short) (255 - outputImgColor[col][row][channel]);
               
                 }
            }

        }

        redoArrayColor(outputImgColor, effectsArrayColor);
        arrayToDispImage();//Convert pixel array to image object
    }

    public void pixellateColor(int c_offset, int r_offset) {


// Loop through every r_offset by  c_offset  pixels, in both x and y directions
        for (int col = 0; col < colorWidth; col += c_offset) {
            for (int row = 0; row < colorHeight; row += r_offset) {
                for (int channel = 0; channel < channels; channel++) {

                    short[] pixel = new short[3];
                    // Copy the pixel
                    pixel[channel] = outputImgColor[col][row][channel];
                

                    //Paste the pixel onto the surrounding  r_offset by  c_offset neighbors
                    // Also make sure that our loop never goes outside the bounds of the image
                    for (int out_c = col; (out_c < col + c_offset) && (out_c < colorWidth); out_c++) {
                        for (int out_r = row; (out_r < row + r_offset) && (out_r < colorHeight); out_r++) {

                            outputImgColor[out_c][out_r][channel] = pixel[channel];
                       

                        }
                    }
                }
            }
        }
        redoArrayColor(outputImgColor, effectsArrayColor);
        arrayToDispImage();

    }

    public void quantizeColor(int threshold) {

        for (int col = 0; col < colorWidth; col++) {
            for (int row = 0; row < colorHeight; row++) {
                for (int channel = 0; channel < channels; channel++) {

                    outputImgColor[col][row][channel] = (short) ((int) (outputImgColor[col][row][channel] / threshold) * threshold);
                  
                }
            }
        }
        redoArrayColor(outputImgColor, effectsArrayColor);

        arrayToDispImage();

    }

    public void HistStretchColor() {

        int min = (int) (outputImgColor[0][0][0]);
        int max = (int) (outputImgColor[0][0][0]);
        int stretch_min = 0;
        int stretch_max = 255;

        for (int col = 0; col < colorWidth; col++) {
            for (int row = 0; row < colorHeight; row++) {
                for (int channel = 0; channel < channels; channel++) {
                    if (min > outputImgColor[col][row][channel]) {
                        min = outputImgColor[col][row][channel];
                    }
                    if ((max < outputImgColor[col][row][channel])) {

                        max = outputImgColor[col][row][channel];

                    }
                }
            }
        }

        for (int col = 0; col < colorWidth; col++) {
            for (int row = 0; row < colorHeight; row++) {
                for (int channel = 0; channel < channels; channel++) {

                    outputImgColor[col][row][channel] = (short) ((outputImgColor[col][row][channel] - min) * ((stretch_max - stretch_min) / (max - min)) + stretch_min);
                    if (outputImgColor[col][row][channel] > 255) {
                        outputImgColor[col][row][channel] = 255;
                    }
                    if (outputImgColor[col][row][channel] < 0) {
                        outputImgColor[col][row][channel] = 0;
                    }
                }
            }
        }
        redoArrayColor(outputImgColor, effectsArrayColor);
        arrayToDispImage();

    }

    public void HistEqualizeColor() {

        int[] histogram = new int[256];//Changed from the size of imWidth

        for (int col = 0; col < colorWidth; col++) {
            for (int row = 0; row < colorHeight; row++) {
                for (int channel = 0; channel < channels; channel++) {

                    ++histogram[outputImgColor[col][row][channel]];
                }
            }

        }

        int mass = colorHeight * colorWidth;
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

        for (int col = 0; col < colorWidth; col++) {
            for (int row = 0; row < colorHeight; row++) {
                for (int channel = 0; channel < channels; channel++) {

                    outputImgColor[col][row][channel] = (short) (histogram[outputImgColor[col][row][channel]]);
                }
            }
        }
        redoArrayColor(outputImgColor, effectsArrayColor);
        arrayToDispImage();
    }

    public void autoQuantizeColor() {

        int sum = 0;
        int count = 0;

        int[] histogram = new int[256];

        for (int col = 0; col < colorWidth; col++) {
            for (int row = 0; row < colorHeight; row++) {
                for (int channel = 0; channel < channels; channel++) {

                    ++histogram[outputImgColor[col][row][channel]];
                    count++;
                }
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

        for (int col = 0; col < colorWidth; col++) {
            for (int row = 0; row < colorHeight; row++) {
                for (int channel = 0; channel < channels; channel++) {

                    outputImgColor[col][row][channel] = (short) ((int) (outputImgColor[col][row][channel] / avg) * avg);
                    
               
                }
            }
        }
        redoArrayColor(outputImgColor, effectsArrayColor);
        arrayToDispImage();
    }

    public void FilterColor(short template[][]) {

        int sum = calculateTempSum(template);

        short avg_val;
        for (int col = 1; col < colorWidth - 1; col++) {
            for (int row = 1; row < colorHeight - 1; row++) {
                for (int channel = 0; channel < channels; channel++) {
                    avg_val = 0;
                    for (int r = -1; r < template.length - 1; r++) {

                        for (int c = -1; c < template.length - 1; c++) {

                            avg_val += (short) ((template[r + 1][c + 1] * outputImgColor[col + c][row + r][channel]));

                        }
                    }
                    outputImgColor[col][row][channel] = (short) (avg_val / sum);
                }

            }

        }
        redoArrayColor(outputImgColor, effectsArrayColor);
        arrayToDispImage();
    }

    public void medianFilterColor() {

        int w_width = 3;
        int w_height = 3;
        short window[] = new short[w_width * w_height];

        int count;

        for (int col = 1; col < colorWidth - 1; col++) {
            for (int row = 1; row < colorHeight - 1; row++) {
                for (int channel = 0; channel < channels; channel++) {

                    count = 0;
                    for (int r = -1; r < w_height - 1; r++) {

                        for (int c = -1; c < w_width - 1; c++) {

                            window[count] = outputImgColor[col + c][row + r][channel];
                            count++;

                        }

                    }
                    Arrays.sort(window);
                    outputImgColor[col][row][channel] = window[Math.round(w_width * w_height / 2)];//Is it math.floor?
                }
            }
        }
        redoArrayColor(outputImgColor, effectsArrayColor);
        arrayToDispImage();
    }

    public void modeFilterColor() {

        int w_width = 3;
        int w_height = 3;
        int window[] = new int[w_width * w_height];
        short tempArray[][][] = new short[colorWidth][colorHeight][channels];

        int count;

        for (int col = 1; col < colorWidth - 1; col++) {
            for (int row = 1; row < colorHeight - 1; row++) {
                for (int channel = 0; channel < channels; channel++) {

                    count = 0;
                    for (int r = -1; r < w_height - 1; r++) {

                        for (int c = -1; c < w_width - 1; c++) {

                            window[count] = outputImgColor[col + c][row + r][channel];
                            count++;

                        }

                    }

                    float value = findModeColor(window);//Finding the mode for the window

                    tempArray[col][row][channel] = (short) (value);

                }
            }

        }
        outputImgColor = tempArray;
        redoArrayColor(outputImgColor, effectsArrayColor);

        arrayToDispImage();
    }

    public float findModeColor(int window[]) {

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

    public void kValueFilterColor() {

        int kVal = 6;
        int w_width = 3;
        int w_height = 3;
        int window[] = new int[w_width * w_height];
        short[][][] temp = new short[colorWidth][colorHeight][channels];
        int count;

        for (int col = 1; col < colorWidth - 1; col++) {
            for (int row = 1; row < colorHeight - 1; row++) {
                for (int channel = 0; channel < channels; channel++) {

                    count = 0;
                    for (int r = -1; r < w_height - 1; r++) {

                        for (int c = -1; c < w_width - 1; c++) {

                            window[count] = outputImgColor[col + c][row + r][channel];
                            count++;

                        }

                    }

                    bubbleSort(window);

                    float mean = getKclosest(window, window[4], kVal, window.length);

                    temp[col][row][channel] = (short) mean;
                }
            }
        }

        outputImgColor = temp;

        redoArrayColor(outputImgColor, effectsArrayColor);
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

    public void sobelEdgeDetectionColor() {
        short[][][] newArray = new short[colorWidth][colorHeight][channels];
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
        for (int col = 1; col < colorWidth - 2; col++) {
            for (int row = 1; row < colorHeight - 2; row++) {
                for (int channel = 0; channel < channels; channel++) {
                    int[][] tempArray = new int[temp_height][temp_width];

                    int mag_X = 0;
                    int mag_Y = 0;
                    for (int x = 0; x < temp_height; x++) {
                        for (int y = 0; y < temp_width; y++) {

                            int xn = col + y - 1;
                            int yn = row + x - 1;

                            tempArray[x][y] = outputImgColor[xn][yn][channel];
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
                    newArray[col][row][channel] = (short) final_magnitude;
                }
            }
        }
        outputImgColor = newArray;

        redoArrayColor(outputImgColor, effectsArrayColor);
        arrayToDispImage();

    }

    public void laplacianEdgeDitectionColor(short temp[][]) {

        short[][][] newArray = new short[colorWidth][colorHeight][channels];
        short avg_val;

        for (int col = 1; col < colorWidth - 1; col++) {
            for (int row = 1; row < colorHeight - 1; row++) {
                for (int channel = 0; channel < channels; channel++) {
                    avg_val = 0;
                    for (int r = -1; r < temp.length - 1; r++) {

                        for (int c = -1; c < temp.length - 1; c++) {

                            avg_val += (short) ((temp[r + 1][c + 1] * outputImgColor[col + c][row + r][channel]));

                        }
                    }
                    newArray[col][row][channel] = (short) (avg_val);
                }

            }

        }

        for (int r = 0; r < colorWidth; r++) {
            for (int c = 0; c < colorHeight; c++) {
                for (int channel = 0; channel < channels; channel++) {
                    if (newArray[r][c][channel] > 0) {
                        newArray[r][c][channel] = 0;
                    } else {
                        newArray[r][c][channel] = (short) (Math.abs(newArray[r][c][channel]));

                    }
                }
            }

        }
        outputImgColor = newArray;
        redoArrayColor(outputImgColor, effectsArrayColor);
        arrayToDispImage();
    }

    public void sharpenColor() {

        short[][] templateLaplassian = {
            {-1, -1, -1},
            {-1, 9, -1},
            {-1, -1, -1}

        };

        int sum = calculateTempSum(templateLaplassian);
        short[][][] finalImg = new short[colorWidth][colorHeight][channels];
        short[][][] filteredImg = new short[colorWidth][colorHeight][channels];
        short avg_val;
        for (int col = 1; col < colorWidth - 1; col++) {

            for (int row = 1; row < colorHeight - 1; row++) {
                for (int channel = 0; channel < channels; channel++) {
                    avg_val = 0;
                    for (int r = -1; r < templateLaplassian.length - 1; r++) {

                        for (int c = -1; c < templateLaplassian.length - 1; c++) {

                            avg_val += (short) ((templateLaplassian[r + 1][c + 1] * outputImgColor[col + r][row + c][channel]));

                        }

                    }

                    filteredImg[col][row][channel] = (short) (avg_val / sum);
                }
            }

        }

        for (int r = 0; r < colorWidth; r++) {
            for (int c = 0; c < colorHeight; c++) {
                for (int channel = 0; channel < channels; channel++) {
                    finalImg[r][c][channel] = (short) (filteredImg[r][c][channel] + outputImgColor[r][c][channel]);
                    if (finalImg[r][c][channel] >= 255) {
                        finalImg[r][c][channel] = 255;
                    } else if (finalImg[r][c][channel] < 0) {
                        finalImg[r][c][channel] = 0;
                    }
                }
            }
        }

        outputImgColor = finalImg;
        redoArrayColor(outputImgColor, effectsArrayColor);
        arrayToDispImage();
    }

    public void unsharpenMaskolor() {

        short[][][] tempArray = new short[colorWidth][colorHeight][channels];
        short[][][] mask = new short[colorWidth][colorHeight][channels];
        for (int r = 0; r < colorWidth; r++) {
            for (int c = 0; c < colorHeight; c++) {
                for (int channel = 0; channel < channels; channel++) {

                    tempArray[r][c][channel] = outputImgColor[r][c][channel];
                }

            }
        }
        short[][] templateGaussian = {
            {1, 2, 1},
            {2, 4, 2},
            {1, 2, 1}

        };
        FilterColor(templateGaussian);

        for (int r = 0; r < colorWidth; r++) {
            for (int c = 0; c < colorHeight; c++) {
                for (int channel = 0; channel < channels; channel++) {

                    mask[r][c][channel] = (short) (tempArray[r][c][channel] - outputImgColor[r][c][channel]);
                }

            }
        }
        for (int r = 0; r < colorWidth; r++) {
            for (int c = 0; c < colorHeight; c++) {
                for (int channel = 0; channel < channels; channel++) {

                    outputImgColor[r][c][channel] = (short) (tempArray[r][c][channel] + mask[r][c][channel]);
                    if (outputImgColor[r][c][channel] > 255) {
                        outputImgColor[r][c][channel] = 255;
                    }
                    if (outputImgColor[r][c][channel] < 0) {
                        outputImgColor[r][c][channel] = 0;
                    }
                }
            }

        }
        redoArrayColor(outputImgColor, effectsArrayColor);
        arrayToDispImage();
    }

    public void toGreyScale() {

        for (int col = 0; col < colorWidth; col++) {
            for (int row = 0; row < colorHeight; row++) {
                for (int channel = 0; channel < channels; channel++) {
                    float avg = (outputImgColor[col][row][0] + outputImgColor[col][row][1] + outputImgColor[col][row][2]) / 3;

                    outputImgColor[col][row][0] = (short) avg;
                    outputImgColor[col][row][1] = (short) avg;
                    outputImgColor[col][row][2] = (short) avg;

                }

            }
        }

        redoArrayColor(outputImgColor, effectsArrayColor);

        arrayToDispImage();

    }

    public void pencilSketchColor(int pointSize) {
        toGreyScale();//convert to greyscale
        medianFilterColor();//smotthens
        sobelEdgeDetectionColor();//detects edges
        invertColor();//colors inverted
        for (int r = 0; r < colorWidth; r++) {
            for (int c = 0; c < colorHeight; c++) {
                for (int channel = 0; channel < channels; channel++) {
                    if (outputImgColor[r][c][channel] > 0) {
                        outputImgColor[r][c][channel] = (short) (pointSize - outputImgColor[r][c][channel]);
                    }
                    if (outputImgColor[r][c][channel] < 0) {
                        outputImgColor[r][c][channel] = 0;
                    } else {
                        outputImgColor[r][c][channel] = 200;
                    }
                }
            }
        }
      
        modeFilterColor();//smoothened again
        redoArrayColor(outputImgColor, effectsArrayColor);
        arrayToDispImage();

    }

    public void scaleColor() {

        String[] button = {"Maximize", "Minimize"};
        JFrame frame = new JFrame();
        final ImageIcon icon = new ImageIcon("Info_icon.png");
        int option = JOptionPane.showOptionDialog(null, "Select an Option", "Select Option", JOptionPane.INFORMATION_MESSAGE, 0, icon, button, button[1]);
        int srcRow, srcCol;
     
        if (option == 0) {
            short[][][] temp = new short[colorWidth][colorHeight][channels];
            String val = JOptionPane.showInputDialog("Enter Scale Height:");
            int height = Integer.parseInt(val);
            String val2 = JOptionPane.showInputDialog("Enter Scale Width:");
            int width = Integer.parseInt(val2);
         
            for (int row = 0; row < colorWidth; row++) {

                for (int col = 0; col < colorHeight; col++) {
                    for (int channel = 0; channel < channels; channel++) {

                        srcRow = row / width;
                        srcCol = col / height;
                        if ((srcRow >= 0 && srcRow < colorWidth) && (srcCol >= 0 && srcCol < colorHeight)) {
                            temp[row][col][channel] = outputImgColor[srcRow][srcCol][channel];
                        } else {
                            temp[row][col][channel] = 255;
                        }
                    }
                }
            }

            outputImgColor = temp;
            redoArrayColor(outputImgColor, effectsArrayColor);

            arrayToDispImage();
        } else if (option == 1) {

            String minval = JOptionPane.showInputDialog("Enter Scale Height:");
            int minheight = Integer.parseInt(minval);
            String minval2 = JOptionPane.showInputDialog("Enter Scale Width:");
            int minwidth = Integer.parseInt(minval2);
            short[][][] tempmin = new short[colorWidth][colorHeight][channels];
            for (int r = 0; r < colorWidth; r++) {
                for (int c = 0; c < colorHeight; c++) {
                    for (int channel = 0; channel < channels; channel++) {

                        tempmin[r][c][channel] = 255;//Setting the background to white

                    }
                }
            }

            for (int r = 0; r < colorWidth; r++) {
                for (int c = 0; c < colorHeight; c++) {
                    for (int channel = 0; channel < channels; channel++) {

                        srcRow = r * minwidth;
                        srcCol = c * minheight;
                        if ((srcRow >= 0 && srcRow < colorWidth) && (srcCol >= 0 && srcCol < colorHeight)) {
                            tempmin[r][c][channel] = outputImgColor[srcRow][srcCol][channel];
                        }
                    }
                }

            }

            outputImgColor = tempmin;
            redoArrayColor(outputImgColor, effectsArrayColor);//Saving pixel values for redo operation

            arrayToDispImage();
        } else {

        }

    }

    public void rotate(double angle) {

        int srcRow;
        int srcCol;

        double cosVal = Math.cos(angle);
        double sinVal = Math.sin(angle);
        double x = 0.5 * (colorWidth - 1);//point to rotate around
        double y = 0.5 * (colorHeight - 1);//centre of the image
        short[][][] temp = new short[colorWidth][colorHeight][channels];
        for (int row = 0; row < colorWidth; row++) {

            for (int col = 0; col < colorHeight; col++) {
                for (int channel = 0; channel < channels; channel++) {
                    double temp_val_a = row - x;
                    double temp_val_b = col - y;
                    srcRow = (int) (+temp_val_a * cosVal - temp_val_b * sinVal + x);
                    srcCol = (int) (+temp_val_a * sinVal + temp_val_b * cosVal + y);
                    if (((srcCol >= 0) && (srcCol < colorHeight)) && ((srcRow >= 0) && (srcRow < colorWidth))) {
                        temp[row][col] = outputImgColor[srcRow][srcCol];
                    } else {
                        temp[row][col][channel] = 255;
                    }
                }
            }
        }

        outputImgColor = temp;
        redoArrayColor(outputImgColor, effectsArrayColor);//Saving pixel values for redo operation

        arrayToDispImage();

    }

    public void cropColor(int rowPoint, int colPoint) {
        String val1 = JOptionPane.showInputDialog("Enter desired height:");
        int cropHeight = Integer.parseInt(val1);
        String val2 = JOptionPane.showInputDialog("Enter desired width:");
        int cropWidth = Integer.parseInt(val2);
        short[][][] temp = new short[colorWidth][colorHeight][channels];

        for (int r = 0; r < colorWidth; r++) {
            for (int c = 0; c < colorHeight; c++) {
                for (int channel = 0; channel < channels; channel++) {

                    temp[r][c][channel] = 255;//Setting the background to white
                }

            }
        }

        for (int row = rowPoint; row < cropHeight + rowPoint; row++) {

            for (int col = colPoint; col < cropWidth + colPoint; col++) {
                for (int channel = 0; channel < channels; channel++) {

                    temp[row - rowPoint][col - colPoint][channel] = outputImgColor[row][col][channel];
                }

            }

        }

        outputImgColor = temp;
        redoArrayColor(outputImgColor, effectsArrayColor);
        arrayToDispImage();

    }

    public void moveColor(int R_offset, int C_offset) {

        int srcrow, srccol, h, w;

        short[][][] temp = new short[colorWidth][colorHeight][channels];

        for (int row = 0; row < colorWidth; row++) {

            for (int col = 0; col < colorHeight; col++) {
                for (int channel = 0; channel < channels; channel++) {

                    srcrow = row - R_offset;
                    srccol = col - C_offset;

                    if ((srcrow >= 0 && srcrow < colorWidth) && (srccol >= 0 && srccol < colorHeight)) {
                        temp[row][col][channel] = outputImgColor[srcrow][srccol][channel];
                    } else {
                        temp[row][col][channel] = 255;
                    }
                }
            }
        }
        outputImgColor = temp;
        redoArrayColor(outputImgColor, effectsArrayColor);

        arrayToDispImage();
    }

    public void Flip(int option) {
        short[][][] newArray = new short[colorWidth][colorHeight][channels];
        int val = 1;
        if (option == 1) {
            for (int row = 0; row < colorHeight; row++) {

                for (int col = 0; col < colorWidth; col++) {
                    for (int channel = 0; channel < channels; channel++) {

                        newArray[col][row][channel] = outputImgColor[col][colorHeight - row - val][channel];

                    }
                }
            }
            outputImgColor = newArray;

            redoArrayColor(outputImgColor, effectsArrayColor);

            arrayToDispImage();
        } else if (option == 0) {

            for (int row = 0; row < colorHeight; row++) {

                for (int col = 0; col < colorWidth; col++) {
                    for (int channel = 0; channel < channels; channel++) {

                        newArray[col][row][channel] = outputImgColor[colorWidth - col - val][row][channel];

                    }
                }
            }
            outputImgColor = newArray;

            redoArrayColor(outputImgColor, effectsArrayColor);

            arrayToDispImage();
        } else {
        }

    }

    public void FadeColor(int option) {

        short[][][] blendArrayRed = new short[colorWidth][colorHeight][channels];
        short[][][] blendArrayGreen = new short[colorWidth][colorHeight][channels];
        short[][][] blendArrayBlue = new short[colorWidth][colorHeight][channels];

        double amount = 0;
        String val1 = "";

        short[][][] tempArray = new short[colorWidth][colorHeight][channels];
        try{
        switch (option) {
            case 0:
                populateArrayColor(blendArrayRed, (short) 255, 0);
                populateArrayColor(blendArrayBlue, (short) 255, 2);
                populateArrayColor(blendArrayGreen, (short) 255, 1);
                val1 = JOptionPane.showInputDialog("Enter amount:(between 0-1)");
                amount = Float.parseFloat(val1);
                for (int col = 0; col < colorWidth; ++col) {
                    for (int row = 0; row < colorHeight; ++row) {
                        for (int channel = 0; channel < channels; channel++) {
                            tempArray[col][row][0] = (short) ((1 - amount) * outputImgColor[col][row][0] + amount * blendArrayRed[col][row][0]);
                            tempArray[col][row][1] = (short) ((1 - amount) * outputImgColor[col][row][1] + amount * blendArrayGreen[col][row][1]);
                            tempArray[col][row][1] = (short) ((1 - amount) * outputImgColor[col][row][2] + amount * blendArrayBlue[col][row][2]);
                        }
                    }
                }
                outputImgColor = tempArray;
                redoArrayColor(outputImgColor, effectsArrayColor);
                arrayToDispImage();

                break;
            case 1:
                populateArrayColor(blendArrayRed, (short) 100, 0);
                populateArrayColor(blendArrayBlue, (short) 100, 2);
                populateArrayColor(blendArrayGreen, (short) 255, 1);
                val1 = JOptionPane.showInputDialog("Enter amount:(between 0-1)");
                amount = Float.parseFloat(val1);
                for (int col = 0; col < colorWidth; ++col) {
                    for (int row = 0; row < colorHeight; ++row) {
                        for (int channel = 0; channel < channels; channel++) {
                            tempArray[col][row][1] = (short) ((1 - amount) * outputImgColor[col][row][0] + amount * blendArrayRed[col][row][0]);
                            tempArray[col][row][1] = (short) ((1 - amount) * outputImgColor[col][row][1] + amount * blendArrayGreen[col][row][1]);
                            tempArray[col][row][1] = (short) ((1 - amount) * outputImgColor[col][row][2] + amount * blendArrayBlue[col][row][2]);
                        }
                    }
                }
                outputImgColor = tempArray;
                redoArrayColor(outputImgColor, effectsArrayColor);
                arrayToDispImage();

                break;
            case 2:
                val1 = JOptionPane.showInputDialog("Enter amount:(between 0-1)");
                amount = Float.parseFloat(val1);
                populateArrayColor(blendArrayRed, (short) 255, 0);
                populateArrayColor(blendArrayBlue, (short) 100, 2);
                populateArrayColor(blendArrayGreen, (short) 100, 1);
                for (int col = 0; col < colorWidth; ++col) {
                    for (int row = 0; row < colorHeight; ++row) {
                        for (int channel = 0; channel < channels; channel++) {

                            tempArray[col][row][0] = (short) ((1 - amount) * outputImgColor[col][row][0] + amount * blendArrayRed[col][row][0]);
                            tempArray[col][row][2] = (short) ((1 - amount) * outputImgColor[col][row][1] + amount * blendArrayGreen[col][row][1]);
                            tempArray[col][row][1] = (short) ((1 - amount) * outputImgColor[col][row][2] + amount * blendArrayBlue[col][row][2]);
                        }
                    }
                }
                outputImgColor = tempArray;
                redoArrayColor(outputImgColor, effectsArrayColor);
                arrayToDispImage();

                break;
            case 3:

                val1 = JOptionPane.showInputDialog("Enter amount:(between 0-1)");
                amount = Float.parseFloat(val1);
                populateArrayColor(blendArrayRed, (short) 255, 0);
                populateArrayColor(blendArrayBlue, (short) 100, 2);
                populateArrayColor(blendArrayGreen, (short) 100, 1);
                for (int col = 0; col < colorWidth; ++col) {
                    for (int row = 0; row < colorHeight; ++row) {
                        for (int channel = 0; channel < channels; channel++) {

                            tempArray[col][row][0] = (short) ((1 - amount) * outputImgColor[col][row][0] + amount * blendArrayRed[col][row][0]);
                            tempArray[col][row][2] = (short) ((1 - amount) * outputImgColor[col][row][1] + amount * blendArrayGreen[col][row][1]);
                            tempArray[col][row][0] = (short) ((1 - amount) * outputImgColor[col][row][2] + amount * blendArrayBlue[col][row][2]);
                        }
                    }
                }
                outputImgColor = tempArray;
                redoArrayColor(outputImgColor, effectsArrayColor);
                arrayToDispImage();

                break;
            case 4:
                val1 = JOptionPane.showInputDialog("Enter amount:(between 0-1)");
                amount = Float.parseFloat(val1);
                populateArrayColor(blendArrayRed, (short) 255, 0);
                populateArrayColor(blendArrayBlue, (short) 255, 2);
                populateArrayColor(blendArrayGreen, (short) 255, 1);
                for (int col = 0; col < colorWidth; ++col) {
                    for (int row = 0; row < colorHeight; ++row) {
                        for (int channel = 0; channel < channels; channel++) {

                            tempArray[col][row][2] = (short) ((1 - amount) * outputImgColor[col][row][0] + amount * blendArrayRed[col][row][0]);
                            tempArray[col][row][1] = (short) ((1 - amount) * outputImgColor[col][row][1] + amount * blendArrayGreen[col][row][1]);
                            tempArray[col][row][1] = (short) ((1 - amount) * outputImgColor[col][row][2] + amount * blendArrayBlue[col][row][2]);
                        }
                    }
                }
                outputImgColor = tempArray;
                redoArrayColor(outputImgColor, effectsArrayColor);
                arrayToDispImage();
            default:
                break;

        }
        }
        catch(NullPointerException e){
        
        
        
        }

    }

    public void warpColor() {

        double x = 0;
        double y = 0;
        double midWPoint = 0.5 * (colorHeight - 1);
        double midHPoint = 0.5 * (colorWidth - 1);
        for (int col = 0; col < colorHeight; ++col) {
            for (int row = 0; row < colorWidth; ++row) {
                for (int channel = 0; channel < channels; channel++) {
                    double tempmidWPoint = col - midWPoint;
                    double tempmidHPoint = row - midHPoint;

                    double radius = Math.sqrt(tempmidWPoint * tempmidWPoint + tempmidHPoint * tempmidHPoint);
                    double angle = Math.sqrt(radius);//Math.atan2(tempmidWPoint, tempmidHPoint);// Math.PI/256*radius; //Math.atan2(tempmidWPoint, tempmidHPoint);

                    x = (double) (+tempmidWPoint * Math.cos(angle) - tempmidHPoint * Math.sin(angle) + midWPoint);
                    y = (double) (+tempmidWPoint * Math.sin(angle) + tempmidHPoint * Math.cos(angle) + midHPoint);
                    if (x >= 0 && x < colorHeight && y >= 0 && y < colorWidth) {
                        outputImgColor[row][col][channel] = outputImgColor[(short) y][(short) x][channel];
                    } else {
                        outputImgColor[row][col][channel] = 255;
                    }
                }
            }
        }

        redoArrayColor(outputImgColor, effectsArrayColor);
        arrayToDispImage();

    }

    public void colorPencilSketch(int pointsize) {

        medianFilterColor();
        sobelEdgeDetectionColor();
        invertColor();
        for (int r = 0; r < colorWidth; r++) {
            for (int c = 0; c < colorHeight; c++) {
                for (int channel = 0; channel < channels; channel++) {
                    if (outputImgColor[r][c][channel] > 0) {
                        outputImgColor[r][c][channel] = (short) (pointsize - outputImgColor[r][c][channel]);
                    }
                    if (outputImgColor[r][c][channel] < 0) {
                        outputImgColor[r][c][channel] = 0;
                    } else {
                        outputImgColor[r][c][channel] = 200;
                    }
                }
            }
        }
        modeFilterColor();

        arrayToDispImage();

    }

    public void undoColor() {

        for (int col = 0; col < colorWidth; col++) {
            for (int row = 0; row < colorHeight; row++) {
                for (int channel = 0; channel < channels; channel++) {

                    outputImgColor[col][row][channel] = undoArray[col][row][channel];
                }

            }
        }

        arrayToDispImage();

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

    public void redoColor() {

        for (int col = 0; col < colorWidth; col++) {
            for (int row = 0; row < colorHeight; row++) {
                for (int channel = 0; channel < channels; channel++) {

                    outputImgColor[col][row][channel] = effectsArrayColor[col][row][channel];
                }

            }
        }
        arrayToDispImage();

    }

    public void redoArrayColor(short[][][] outputImgColor, short[][][] effectsArrayColor) {

        for (int col = 0; col < colorWidth; col++) {
            for (int row = 0; row < colorHeight; row++) {
                for (int channel = 0; channel < channels; channel++) {
                    effectsArrayColor[col][row][channel] = outputImgColor[col][row][channel];
                }

            }
        }
    }

    public void populateArrayColor(short[][][] array, short value, int c) {

        for (int x = 0; x < colorWidth; x++) {
            for (int y = 0; y < colorHeight; y++) {

                array[x][y][c] = value;

            }
        }

    }

}
