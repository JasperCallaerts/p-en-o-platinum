package internal;

/**
 * Created by Martijn & Jasper on 9/10/2017.
 */
public class VerticalWing extends Wing {

    public VerticalWing(Vector relativePosition, float liftSlope, float mass, float maximumAngleOfAttack, float wingInclination){
        super(relativePosition, liftSlope, mass, maximumAngleOfAttack, wingInclination);

    }

    @Override
    public Vector getNormal() {
        return this.normal;
    }

    /**
     * (-sin(verStabInclination), 0, -cos(verStabInclination))
     * @return
     */
    @Override
    public Vector getAttackVector() {
        float inclination = this.getWingInclination();

        float x_part = -(float)Math.sin(inclination);
        float y_part = 0.0f;
        float z_part = -(float)Math.cos(inclination);

        return new Vector(x_part, y_part, z_part);
    }

    @Override
    public Vector projectOnDrone(Vector vector){

        float inclination = this.getWingInclination();

        float x_part = (float) Math.cos(inclination) * vector.getxValue()
                + (float)Math.sin(inclination)*vector.getzValue();
        float y_part = vector.getyValue();
        float z_part = -(float)Math.sin(inclination)*vector.getxValue()
                + (float)Math.cos(inclination)*vector.getzValue();

        return new Vector(x_part, y_part, z_part);
    }

    public final Vector normal = new Vector(1,0,0);

    @Override
    public Vector getLift(){
        Vector lift = super.getLift();
        System.out.println(lift);
        return lift;

    }
}
