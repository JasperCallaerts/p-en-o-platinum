package internal;

import javax.swing.text.Position;

/**
 * a class of Immutable pixels, represented by RGB values in bytes.
 * A bias of + 128 is applied to achieve the classical range of RGB values of 0 to 255
 * Created by Martijn on 14/10/2017.
 * @author Martijn
 */
public class Pixel {
    public Pixel(byte red, byte green, byte blue){
        this.setRed(red);
        this.setGreen(green);
        this.setBlue(blue);
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

    public byte getGrayscale(){
        return 0;
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
        if(result > MAX)
            result = MAX;
        //probably redundant
        if(result < MIN)
            result = MIN;

        return (byte)(result - BIAS);
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

    /*
    Error Messages
     */
    private final static String ILLEGAL_VALUE = "The supplied integer value is not between 0 and 255";
}
