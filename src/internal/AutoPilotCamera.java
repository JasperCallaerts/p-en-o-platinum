package internal;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by Martijn on 14/10/2017.
 * @author Martijn Sauwens
 */
public class AutoPilotCamera {

    public AutoPilotCamera(byte[] image, float horizontalAngleOfView, float verticalAngleOfView, int nbRows, int nbColumns,
                           String pixelTag){

    }


    /**
     * Converts an array of bytes containing the RGB color scheme to an array consisting of pixels
     * @param image the byte array containing the data for the pixels
     * @return an array containing the pixels of the image
     * byte array ={ R1, G1, B1, R2, G2, B2, R3, G3, B3} then
     * pixel array = { P1(RGB), P2(RGB), P3(RGB)}
     */
    public Pixel[] convertToPixelArray(byte[] image){
        int imageArraySize = image.length;
        int pixelArraySize = imageArraySize/NB_OF_BYTES_IN_PIXEL;
        Pixel[] pixelArray = new Pixel[pixelArraySize];

        //loop through the indices of the pixel array
        for(int pixelIndex = 0; pixelIndex != pixelArraySize; pixelIndex++){
            byte[] onePixel = new byte[NB_OF_BYTES_IN_PIXEL];

            // the byte array is three times longer than the pixel array, place every three bytes in one pixel
            for(int byteExtraIndex = 0; byteExtraIndex != NB_OF_BYTES_IN_PIXEL; byteExtraIndex++){

                onePixel[byteExtraIndex] = image[pixelIndex*NB_OF_BYTES_IN_PIXEL + byteExtraIndex];
            }
            pixelArray[pixelIndex] = new Pixel(onePixel);
        }

        return pixelArray;
    }

    /*
    Variables
     */
    private byte[] imageBytes;

    private int nbRows;

    private int nbColumns;


    /*
    Constant tags
     */
    public final static String RED_PIXEL = "red";
    public final static String BLUE_PIXEL = "blue";
    public final static String GREEN_PIXEL = "green";
    /*
    Constants
     */
    private final static int NB_OF_BYTES_IN_PIXEL = 3;

    /*
    Error Messages
     */
    public final static String IO_EXCEPTION = "Something went wrong reading the image";
}
