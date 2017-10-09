package internal;
import java.lang.Math;

public abstract class Wing {

    //Todo implement the binary relation between the wings and the drone

	/**
	 * Calculates the normal vector of the wing in the Drone's coordinate system
	 */
	public abstract Vector getNormalInWorld();

	/**
	 * returns the attackVector
	 */
	public abstract Vector getAttackVector();

    /**
     * prokects the vector onto the axis of the drone
     * @return
     */
	public abstract Vector projectOnDrone(Vector vector);

    public float getWingInclination() {
        return wingInclination;
    }

    //Todo add checks
    public void setWingInclination(float wingInclination) {
        this.wingInclination = wingInclination;
    }

    //Todo implement canHaveAsWingInclination
    public boolean canHaveAsWingInclintion(float inclination){
        return true;
    }

    /**
     * Variable that holds the inclination of the wing
     */
    private float wingInclination;
}
