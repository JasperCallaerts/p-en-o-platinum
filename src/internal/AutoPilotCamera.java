package internal;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by Martijn on 14/10/2017.
 */
public class AutoPilotCamera {

    public AutoPilotCamera(byte[] image, float horizontalAngleOfView, float verticalAngleOfView, int nbRows, int nbColumns,
                           String pixelTag){

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
    Error Messages
     */
    public final static String IO_EXCEPTION = "Something went wrong reading the image";
}
