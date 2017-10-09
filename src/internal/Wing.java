package internal;
import java.lang.Math;

class Wing {

	
	/**
	 * @param leftWingIncl The left wing's inclination (in radians)
	 * Returns the attack vector of the left wing
	 */
	public Vector leftWingAttackVector(float leftWingIncl){ 
		Vector vec = new Vector(0, (float) Math.sin(leftWingIncl),(float) -Math.cos(leftWingIncl));
		return vec;
	}

	/**
	 * 
	 * @param leftWingIncl The left wing's inclination (in radians)
	 * @return the normal of the left wing
	 */
	public Vector leftWingNormal(float leftWingIncl){
		return axisVectorWing.crossProduct(leftWingAttackVector(leftWingIncl));
	}
	
	/**
	 * @param rightWingIncl The right wing's inclination (in radians)
	 * Returns the attack vector of the right wing
	 */
	public Vector rightWingAttackVector(float rightWingIncl){ 
		Vector vec = new Vector(0, (float) Math.sin(rightWingIncl),(float) -Math.cos(rightWingIncl));
		return vec;
	}

	
	/**
	 * 
	 * @param rightWingIncl The right wing's inclination (in radians)
	 * @return the normal of the right wing
	 */
	public Vector rightWingNormal(float rightWingIncl){
		return axisVectorWing.crossProduct(rightWingAttackVector(rightWingIncl));
	}
	
	/**
	 * Axis vector for both wings
	 */
	private Vector axisVectorWing = new Vector(1,0,0);
}
