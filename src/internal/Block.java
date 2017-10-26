package internal;

import gui.Cube;

/**
 * 
 * @author Anthony Rathé & Jonathan Craessaerts &..
 *
 */
public class Block implements WorldObject{

	/**
	 * Constructor for a block
	 * @param position the position of the cube
	 * @param guiCube the gui version of the cube
	 */
	public Block(Vector position,  Cube guiCube){
		this.setPosition(position);
		this.guiCube = guiCube;
	}


	@Override
	public void toNextState(float deltaTime){
		//do nothing, cube cannot change state
	}


	/**
	 * returns the position of the cube in vector format
	 */
	public Vector getPosition(){
		return this.position;
	}

	public void setPosition(Vector position) {
		this.position = position;
	}

	//Todo implement setPosition in the Gui cube class
	/**
	 * sets the corresponding cube int the gui to the same position as this block.
	 */
	private void syncCube(){
		//this.guiCube.setPosition(this.getPosition());
	}

	/**
	 * Variables for the position of a block
	 */
	private Vector position;
	private Cube guiCube;

}
