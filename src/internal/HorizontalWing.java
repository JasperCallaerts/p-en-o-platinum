package internal;

/**
 * Created by Martijn & Jasper on 9/10/2017.
 */

public class HorizontalWing extends Wing {

    public HorizontalWing(Vector relativePosition, float liftSlope, float mass, float maximumInclination, float wingInclination){
        super(relativePosition, liftSlope,  mass, maximumInclination, wingInclination);
    }

    @Override
    public Vector getNormalInWorld() {
        return null;
    }


    /**
     * (0, sin(leftWingInclination), -cos(leftWingInclination))
     * @return
     */
    @Override
    public Vector getAttackVector() {
        float inclination = this.getWingInclination();

        float x_part = 0.0f;
        float y_part = (float)Math.sin(inclination);
        float z_part = - (float)Math.cos(inclination);

        return new Vector(x_part, y_part, z_part);
    }

    /**
     * See pitch transformation matrix
     * @param vector
     * @return
     */
    public Vector projectOnDrone(Vector vector){

        float inclination = this.getWingInclination();

        float x_part = vector.getxValue();
        float y_part = (float) Math.cos(inclination) * vector.getyValue()
                - (float)Math.sin(inclination)*vector.getzValue();
        float z_part = (float)Math.sin(inclination)*vector.getyValue()
                +(float)Math.cos(inclination)*vector.getzValue();

        return new Vector(x_part, y_part, z_part);

    }

    public final Vector normal = new Vector(0,1,0);


}
