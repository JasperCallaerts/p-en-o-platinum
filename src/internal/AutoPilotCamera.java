package internal;

import sun.awt.util.IdentityLinkedList;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.CollationElementIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Martijn on 14/10/2017.
 * @author Martijn Sauwens
 */
public class AutoPilotCamera {

    public AutoPilotCamera(byte[] image, float horizontalAngleOfView, float verticalAngleOfView, int nbRows, int nbColumns,
                           String pixelTag) throws IllegalArgumentException{

        if(!isValidAngleOfView(horizontalAngleOfView) || ! isValidAngleOfView(verticalAngleOfView))
            throw new IllegalArgumentException(VIEWINGANGLE_EXCEPTION);

        this.nbRows = nbRows;
        this.nbColumns = nbColumns;
        this.horizAngleOfView = horizontalAngleOfView;
        this.verticalAngleOfView = verticalAngleOfView;
        this.imageArray = this.convertToPixel2DArray(image, nbRows, nbColumns);
        this.world = new World();
    }

    public Vector locateRedCube(){

        List<Integer> redXCoordinates = new ArrayList<Integer>();
        List<Integer> redYCoordinates = new ArrayList<Integer>();

        findRedPixels(redXCoordinates, redYCoordinates);

        int maxXCoordinate = Collections.max(redXCoordinates);
        int maxYCoordinate = Collections.max(redYCoordinates);

        int meanXCoordinate = getMean(redXCoordinates);
        int meanYCoordinate = getMean(redYCoordinates);

        return null;

    }

    public static int getMean(List<Integer> integerList){
        int sum = 0;
        int lengthList = integerList.size();

        for(Integer element: integerList){
            sum += element;
        }

        return sum/lengthList;

    }

    private void findRedPixels(List<Integer> redXCoordinates, List<Integer> redYCoordinates) {
        int nbRows = this.getNbRows();
        int nbColumns = this.getNbColumns();

        for(int rowIndex = 0; rowIndex != nbRows; rowIndex++){
            for(int columnIndex = 0; columnIndex != nbColumns; columnIndex++){
                //select the pixel
                Pixel currentPixel = this.getImageArray().getElementAtIndex(rowIndex, columnIndex);
                //convert to HSV
                Vector HSV = new Vector(currentPixel.convertToHSV());
                float hValue = HSV.getxValue();
                float sValue = HSV.getyValue();
                float vValue = HSV.getzValue();
                //check if the HSV value is within range, if so, add pixel coordinates to the lists
                if(Pixel.isEqualFloat(hValue, RED_H_VALUE, EPSILON)&&Pixel.isEqualFloat(sValue, RED_S_VALUE, EPSILON)
                        &&Pixel.isEqualFloat(vValue, Z_AXIS_V_VALUE)) {
                    redXCoordinates.add(rowIndex);
                    redYCoordinates.add(columnIndex);
                }
            }
        }
    }

    /**
     * Sets the image array variable to the
     * @param newImageArray the byte array containing the next image
     */
    public void loadNextImage(byte[] newImageArray){
        if(! canHaveAsImageArray(newImageArray))
            throw new IllegalArgumentException(ILLEGAL_SIZE);
        Array2D<Pixel> newImage = this.convertToPixel2DArray(newImageArray, this.getNbRows(), this.getNbColumns());
        this.setImageArray(newImage);
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

    /**
     * Converts an image byte array to a 2D array containing the pixels of the image
     * @param byteImage the bytes containing the pixels of the image
     * @param nbRows the number of rows of the pixel array
     * @param nbColumns the number cf columns of the pixel array
     * @return a 2D array containing the pixels of the image
     */
    public Array2D<Pixel> convertToPixel2DArray(byte[] byteImage, int nbRows, int nbColumns){
        Pixel[] pixelArray = this.convertToPixelArray(byteImage);
        return new Array2D<Pixel>(pixelArray, nbRows, nbColumns);
    }

    public boolean isValidAngleOfView(float angle){
        return angle > 0.0f && angle <= Math.PI;
    }

    public Array2D<Pixel> getImageArray(){
        return this.imageArray;
    }

    public void setImageArray(Array2D<Pixel> imageArray){

    }

    /**
     * Checks if the given image array can be set as as image array?
     * @param imageArray the imageArray to be checked
     * @return true if and only if the imageArray is the right size
     */
    public boolean canHaveAsImageArray(byte[] imageArray){
       return this.getNbRows()*this.getNbColumns() == imageArray.length/NB_OF_BYTES_IN_PIXEL;
    }

    public int getNbRows() {
        return nbRows;
    }

    public int getNbColumns() {
        return nbColumns;
    }

    /*
        Variables
         */
    private Array2D<Pixel> imageArray;

    /**
     * The number of pixel rows the pixel image contains (immutable)
     */
    private int nbRows;
    /**
     * The number of pixel columns the image contains (immutable)
     */
    private int nbColumns;
    /**
     * horizontal viewing angle of the drone (immutable)
     */
    private float horizAngleOfView;
    /**
     * vertical viewing angle of the drone (immutable)
     */
    private float verticalAngleOfView;
    
    /**
     * @author anthonyrathe
     * @return
     */
    public World getWorld(){
    	return this.world;
    }
    
    /**
     * @author anthonyrathe
     * @return
     */
    public Vector getDestination(){
    	return this.destination;
    }
    
    /**
     * @author anthonyrathe
     * @return
     */
    public void setDestination(Vector destination){
    	this.destination = destination;
    }
    
    private World world;
    private Vector destination;


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
    private final static float RED_H_VALUE = 0.0f;
    private final static float RED_S_VALUE = 1.0f;
    private final static float Z_AXIS_V_VALUE = 0.7f;
    private final static float EPSILON  = 1E-5f;

    /*
    Error Messages
     */
    public final static String IO_EXCEPTION = "Something went wrong reading the image";
    public final static String VIEWINGANGLE_EXCEPTION = "the viewing angle is out of range (0, PI]";
    public final static String ILLEGAL_SIZE = "The byte array cannot be converted to a 2D pixel array";
}
