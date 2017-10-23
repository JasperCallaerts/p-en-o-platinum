package internal;

/**
 * Whole class Made by Martijn
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//import be.kuleuven.cs.som.annotate.*;

/**
 * A class of Immutable Vectors in 3D space
 * Each vector has a x, y and z coordinate
 * The size of each component is represented by a floating point number
 */

//@Value
public class Vector {
	
	/**
	 * Constructor for a vector
	 * @param xValue the x coordinate of the vector
	 * @param yValue the y coordinate of the vector
	 * @param zValue the z coordinate of the vector
	 */
	public Vector(float xValue, float yValue, float zValue){
		this.xValue = xValue;
		this.yValue = yValue;
		this.zValue = zValue;
	}
	
	/**
	 * Default constructor for a vector, creates a null-vector
	 */
	public Vector(){
		
		this(0,0,0);
	}

	/**
	 * Contructor for a vector given a length 3 array
	 * @param vectorArray a length 3 array contianing the values for the vector
	 */
	public Vector(float[] vectorArray){
		if(!isValidArray(vectorArray))
			throw new IllegalArgumentException(INVALID_ARRAY);

		this.xValue = vectorArray[0];
		this.yValue = vectorArray[1];
		this.zValue = vectorArray[2];

	}

	public boolean isValidArray(float[] array){
		return array.length == 3;
	}
	
	
	/**
	 * returns a new vector containing the vector sum of this and other
	 * @Param other the other vector in the sum
	 */
	public Vector vectorSum(Vector other){
		float x_part = this.getxValue() + other.getxValue();
		float y_part = this.getyValue() + other.getyValue();
		float z_part = this.getzValue() + other.getzValue();
		
		return new Vector(x_part, y_part, z_part);
	}
	
	/**
	 * Returns the vector difference: this - other
	 * @param other the other vector
	 * @return the difference between two vectors
	 */
	public Vector vectorDifference(Vector other){
		Vector other_negative = other.scalarMult(-1);
		return this.vectorSum(other_negative);
		
	}
	
	/**
	 * returns a new vector containing the scalar multiplication of this and the scalar
	 * @Param scalar the scalar to re scale the vector
	 * @return new Vector(this.getxValue()*scalar, this.getyValue()*scalar, this.getzvalue()*scalar)
	 */
	public Vector scalarMult(float scalar){
		float x_part = this.getxValue()*scalar;
		float y_part = this.getyValue()*scalar;
		float z_part = this.getzValue()*scalar;
		
		return new Vector(x_part, y_part, z_part);
	}
	
	/**
	 * Calculates the scalar product of two vectors
	 * @param other the other vector
	 * 
	 * @return 	the scalar product of the two vectors
	 * 			with xi, yi, zi the components of each vector
	 * 			| result == x1*x2 + y1*y2 + z1*z2
	 */
	public float scalarProduct(Vector other) throws NullPointerException{
		
		if(other == null){
			throw new NullPointerException();
		}
		
		float x_part = other.getxValue()*this.getxValue();
		float y_part = other.getyValue()*this.getyValue();
		float z_part = other.getzValue()*this.getzValue();
		
		return x_part + y_part + z_part;
	}
	
	/**
	 * returns a rescaled version of the given vector with 2-norm == 1
	 * @return |result == this.scalarMult(1/(this.getSize())
	 */
	public Vector normalizeVector(){
		float size = this.getSize();
		return this.scalarMult(1/size);
	}
	
	/**
	 * returns the two norm of the vector
	 * @return see implementation
	 */
	public float getSize(){
	
		return (float) Math.sqrt(this.scalarProduct(this));
	}
	
	/**
	 * Calculates enclosed angle between two vectors
	 * @param other the other vector
	 * @return the angle between the two given vectors
	 */
	public float getAngleBetween(Vector other) throws NullPointerException{
		
		if(other == null){
			throw new NullPointerException();
		}
		
		// the numerator is a scalar product of the two vectors
		float numerator = this.scalarProduct(other);
		// the denominator is a product of the 2-norms of the vectors
		float denominator = this.getSize()*other.getSize();
		
		return (float)Math.acos(numerator/denominator);
	}
	
	
	/**
	 * @author anthonyrathe
	 */
	public float distanceBetween(Vector other) throws NullPointerException{
		
		if(other == null){
			throw new NullPointerException();
		}
		else{
			return (float)Math.sqrt(Math.pow(this.getxValue()-other.getxValue(), 2) + Math.pow(this.getyValue()-other.getyValue(), 2) + Math.pow(this.getzValue()-other.getzValue(), 2));
		}
	}
	
	/**
	 * calculates the cross product of two vectors and returns a new vector
	 * object containing the cross product results.
	 * @param other
	 * @return the cross product of the two vectors.
	 * 		| a = this, b = other
	 * 		| result == new Vector( ay*bz - az*by,
	 * 		|						az*bx - ax*bz,
	 * 		|						ax*by - ay*bx )
	 * 
	 */
	public Vector crossProduct(Vector other){
		
		if(other == null){
			throw new NullPointerException();
		}
		
		float x_part = this.getyValue()*other.getzValue() - this.getzValue()*other.getyValue();
		float y_part = this.getzValue()*other.getxValue() - this.getxValue()*other.getzValue();
		float z_part = this.getxValue()*other.getyValue() - this.getyValue()*other.getxValue();
		
		return new Vector(x_part, y_part, z_part);
	}
	
	/**
	 * Returns a string describing the Vector
	 */
	@Override
	public String toString() {
		return "Vector [xValue=" + xValue + ", yValue=" + yValue + ", zValue=" + zValue + "]";
	}

	
	/**
	 * Auto override of the  hash code, done by Eclipse
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(xValue);
		result = prime * result + Float.floatToIntBits(yValue);
		result = prime * result + Float.floatToIntBits(zValue);
		return result;
	}



	/**
	 * checks if the given object is equal to the instance against which it is invoked
	 * @return |result = (obj == this)
	 * 
	 * @return true if the x, y and z values of both vectors are the same
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj == null){
			return false;
		}
		if(obj == this){
			return true;
		}
		if(obj instanceof Vector){
			Vector other = (Vector)obj;
			return other.getxValue() == this.getxValue() &&
				   other.getyValue() == this.getyValue() &&
				   other.getzValue() == this.getzValue();
		}else{
			return false;
		}
	}

	/**
	 * calculates a orthogonal Projection of the given vector against the normal vector
	 */
	public Vector orthogonalProjection(Vector normalVector){

		Vector normalizedNormal = normalVector.normalizeVector();
		float numerator = this.scalarProduct(normalizedNormal);
		float denominator = normalizedNormal.scalarProduct(normalizedNormal);

		Vector tempVector = normalizedNormal.scalarMult(numerator/denominator);

		return this.vectorDifference(tempVector);


	}


	/**
	 * selects the element at a given index of the vector
	 * @param index the index of the element to be obtained
	 * @return the element at position of the index
	 */
	public float getElementAt(int index){
		switch(index) {
			case 0:
				return this.getxValue();
			case 1:
				return this.getyValue();
			case 2:
				return this.getzValue();
			default:
				throw new IndexOutOfBoundsException();
		}
	}


	/*
	Static methods
	 */

	/**
	 * Sums  all the vectors in the given array
	 * @param vectorArray the array containing all the vectors
	 * @return a vector containing the sum of all the vectors in the array
	 */
	public static Vector sumVectorArray(Vector[] vectorArray){
		Vector tempVector = new Vector();
		for(Vector vector: vectorArray){

			tempVector = tempVector.vectorSum(vector);
		}

		return tempVector;
	}

	/**
	 * Sums  all the vectors in the given list
	 * @param vectorList the list containing all the vectors
	 * @return a vector containing the sum of all the vectors in the list
	 */
	public static Vector sumVectorList(List<Vector> vectorList){
		Vector tempVector = new Vector();

		for(Vector vector: vectorList){
			tempVector = tempVector.vectorSum(vector);
		}

		return tempVector;
	}
	
	/**
	 * @author anthonyrathe
	 * @return
	 */
	public float[] toArray(){
		 float[] array = {getxValue(), getyValue(), getzValue()};
		 return array;
	}
	
	/**
	 * @author anthonyrathe
	 */
	public int[] toIntArray(){
		int[] array = {(int)getxValue(), (int)getyValue(), (int)getzValue()};
		return array;
	}


	/*
	Getters and Setters
	 */

    /**
	 * Getter for the x Value of the vector
	 */
	public float getxValue() {
		return xValue;
	}

	/**
	 * Getter for the y Value of the vector
	 */
	public float getyValue() {
		return yValue;
	}

	/**
	 * Getter for the z value of the vector
	 */
	public float getzValue() {
		return zValue;
	}


	/**
	 * The variable containing the x-coordinate for the vector
	 */
	private float xValue;
	
	/**
	 * The variable containing the y-coordinate for the vector
	 */
	private float yValue;
	
	
	/**
	 * The variable containing the z-coordinate for the vector
	 */
	private float zValue;

	/*
	Error Messages
	 */

	public final static String INVALID_ARRAY = "The provided array is not of length 3";

	/*
	Constants
	 */
	public final static int VECTOR_SIZE = 3;

}
