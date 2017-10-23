package internal;

import javax.swing.text.Position;

/**
 * a class of Immutable pixels, represented by RGB values in bytes.
 * A bias of + 128 is applied to achieve the classical range of RGB values of 0 to 255
 * Created by Martijn on 14/10/2017.
 * @author Martijn
 */
public class Pixel {

    /**
     * Constructor for a pixel class object
     * @param red the red value of the pixel
     * @param green the green value of the pixel
     * @param blue the blue value of the pixel
     */
    public Pixel(byte red, byte green, byte blue){
        this.setRed(red);
        this.setGreen(green);
        this.setBlue(blue);
    }

    /**
     * Constructor for a pixel class object
     * @param pixelArray array containing the RGB values (red, green, blue) in that specific order
     */
    public Pixel(byte[] pixelArray){
        if(!canBeConvertedToPixel(pixelArray))
            throw new IllegalArgumentException(INVALID_ARRAY_SIZE);

        byte red = pixelArray[0];
        byte green = pixelArray[1];
        byte blue = pixelArray[2];

        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    private boolean canBeConvertedToPixel(byte[] byteArray){
        return byteArray.length == NB_OF_BYTES_IN_PIXEL;
    }

    /**
     * Calculates the sum of two pixels
     * @param other the other pixel to sum
     * @return defined like vector dum with vector1 = (red1, green1, blue1)
     * and vector2 = (red2, green2, blue2) and the sum is defined in
     * valueSum
     */
    public Pixel pixelSum(Pixel other){
        byte red = valueSum(this.getRed(), other.getRed());
        byte green = valueSum(this.getGreen(), other.getGreen());
        byte blue = valueSum(this.getBlue(), this.getGreen());

        return new Pixel(red, green, blue);
    }

    /**
     * Converts the pixel's RGB value to HSV values
     * @return an array Containing the (H, S, V);
     */
    public float[] convertToHSV(){
        int R = this.getRedInt();
        int G = this.getGreenInt();
        int B = this.getBlueInt();
        return HSVconverter.RGBtoHSV(R, G, B);
    }

    /**
     * Converts a pixel to gray scale
     * @return the gray scale pixel based on luminosity
     */
    public byte convertToGrayscale(){
        int red = this.getRed() + BIAS;
        int green  = this.getGreen() + BIAS;
        int blue = this.getBlue() + BIAS;

        float tempFloat = 0.21f*red + 0.72f*green + 0.07f*blue;
        int tempInt = Math.round(tempFloat);

        tempInt = roundInt(tempInt);

        return (byte)(tempInt - BIAS);
    }

    /**
     * Calculates the sum of two byte values
     * @param value1 the first value
     * @param value2 the second value
     * @return a byte containing the sum of the two values, if the sum was out of bounds
     *         the result was set to either Byte.MIN_VALUE for negative overflow and
     *         Byte.MAX_VALUE for positive overflow (range -128 to 127)
     */
    private static byte valueSum(byte value1, byte value2){
        int firstInt = value1 + BIAS;
        int secondInt = value2 + BIAS;

        int result = firstInt + secondInt;

        //set the calculations within bounds
        result = roundInt(result);

        return (byte)(result - BIAS);
    }

    /**
     * round the integer to the range [0, 255]
     * @param integer the integer to be rounded
     * @return an integer between 0 and 255, numbers smaller than zero are set to 0
     * and number larger than 255 are set to 255
     */
    private static int roundInt(int integer){
        int result = integer;
        if(integer > MAX)
            result = MAX;
        //probably redundant
        if(integer < MIN)
            result = MIN;

        return result;
    }

    /**
     * checks if a floating point value is equal to the other within the margin of error
     * @param value1 the first value
     * @param value2 the second value
     * @param errorMargin the margin of error for the floating point values
     * @return returns true if and only if.
     *         value1 >= value2 - errorMargin && value1 <= value2 + errorMargin
     */
    public static boolean isEqualFloat(float value1, float value2, float errorMargin){
        return value1 >= value2 - errorMargin && value1 <= value2 + errorMargin;
    }

    /**
     * Calculates the sum of two byte values
     * @param value1 the first value
     * @param value2 the second value
     * see implementation of value sum, only difference bias is added to result to
     * become range of 0 to 255
     */
    private static int valueSumInt(byte value1, byte value2){
       return Pixel.valueSum(value1, value2) + BIAS;
    }

    /**
     * Checker if the value is a valid RGB value for one color component
     * @param value the desired value of the pixel
     * @return true if and only if the value is in range (0, 255)
     */
    public boolean isValidColorValue(int value){
        return value >= MIN && value <= MAX;
    }

    /**
     * Getter for the red value of the pixel
     * @return a byte value, range (-128, 127)
     */
    public byte getRed() {
        return this.red;
    }

    /**
     * Getter for the red value of the pixel in integer value
     * @return an integer value, range (0, 255)
     */
    public int getRedInt(){
        return this.getRed() + BIAS;
    }

    /**
     * Setter for the red value of the pixel
     * @param red the desired red value between -128 and 127
     */
    public void setRed(byte red) {
        this.red = red;
    }

    /**
     * Setter for the red value of a pixel
     * @param red the desired red value between 0 and 255
     */
    public void setRed(int red){
        if(!isValidColorValue(red))
            throw new IllegalArgumentException(ILLEGAL_VALUE);

        this.setRed((byte)(red - BIAS));
    }

    /**
     * Getter for the green value of the pixel
     * @return a byte value, range (-128, 127)
     */
    public byte getGreen() {
        return this.green;
    }

    /**
     * Getter for the green value of the pixel
     * @return an integer value, range (0, 255)
     */
    public int getGreenInt(){
        return this.getGreen() + BIAS;
    }

    /**
     * Setter for the green value of the pixel
     * @param green the desired green value between -128 and 127
     */
    public void setGreen(byte green) {
        this.green = green;
    }

    /**
     * Setter for the green value of the pixel
     * @param green the desired green value between 0 and 255
     */
    public void setGreen(int green){
        if(!isValidColorValue(green))
            throw new IllegalArgumentException(ILLEGAL_VALUE);

        this.setGreen((byte)(green - BIAS));
    }

    /**
     * Getter for the blue value of the pixel
     * @return a byte value, range(-128, 127)
     */
    public byte getBlue() {
        return blue;
    }

    /**
     * getter for the blue value of the pixel
     * @return an integer value, range(0,255)
     */
    public int getBlueInt(){
        return this.getBlue() + BIAS;
    }

    /**
     * Setter for the blue value of the pixel
     * @param blue the desired blue value between -128 and 127
     */
    public void setBlue(byte blue) {
        this.blue = blue;
    }

    /**
     * Setter for the blue value of a pixel
     * @param blue the desired blue value between 0 and 255
     */
    public void setBlue(int blue){
        if(!isValidColorValue(blue))
            throw new IllegalArgumentException(ILLEGAL_VALUE);
        this.setBlue((byte)(blue - BIAS));
    }

    /*
    Instance Variables
     */
    /**
     * A variable containing the red value of the pixel
     */
    byte red;

    /**
     * A variable containing the green value of the pixel
     */
    byte green;

    /**
     * A variable containing the blue value of the pixel
     */
    byte blue;

    /*
    Constants
     */
    public final static int BIAS = 128;
    public final static int MAX = 255;
    public final static int MIN = 0;
    public final static int NB_OF_BYTES_IN_PIXEL = 3;

    /*
    Error Messages
     */
    private final static String ILLEGAL_VALUE = "The supplied integer value is not between 0 and 255";
    private final static String INVALID_ARRAY_SIZE = "The supplied array is not size 3";
}
