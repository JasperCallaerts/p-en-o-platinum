package internal;

import java.io.IOException;

import gui.Cube;
import math.Vector3f;

public class Block implements WorldObject {
	
	public Block(Vector position) {
		this.position = position;
	}

	@Override
	public void toNextState(float deltaTime) throws IOException {
		// do nothing
	}

	@Override
	public Vector getPosition() {
		return this.position;
	}

	@Override
	public Cube getAssociatedCube(){
		return this.blockCube;
	}

	/**
	 * Sets the associated cube of the block
	 * @param cube the cube to be associated
	 */
	public void setAssocatedCube(Cube cube){
		if(!canHaveAsCube(cube))
			throw new IllegalArgumentException(INVALID_CUBE);
		this.blockCube = cube;
	}

	/**
	 * checks if the provided cube can be associated
	 * @param cube the cube to be associated
	 * @return true if and only if cube is not initialized and the position of the cube and the block are the same
	 */
	private boolean canHaveAsCube(Cube cube){
		return this.getAssociatedCube() == null && cube.getPosition().rangeEquals(this.getPosition(), maxPosDifference);
	}

	/**
	 * getter for the is visited flag
	 * @return
	 */
	public boolean isVisited(){
		return this.isVisited;
	}

	/**
	 * set the is visited flag to true
	 */
	public void setVisited(){
		this.isVisited = true;
	}

	@Override
	public String toString() {
		return "Block{" +
				"position=" + position +
				", isVisited=" + isVisited +
				'}';
	}

	private Vector position;

	private Cube blockCube;

	/**
	 * Flag that stores if the block was visited by the drone.
	 */
	private boolean isVisited = false;

	/**
	 * variable used for the max allowed error on the position between de block and the cube
	 */
	private final static float maxPosDifference = 1E-6f;

	private final static String INVALID_CUBE = "the provided cube does not have the same position as the block";
}
