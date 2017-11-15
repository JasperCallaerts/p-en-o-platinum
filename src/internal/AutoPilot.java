package internal;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Autopilot.*;

//TODO: only recalculate after the new frame is rendered or make seperate controls for the case no new
//visual input was generated

/**
 * Created by Martijn on 14/10/2017.
 * Extended by Bart on 15/10/2017.
 * Extended by Anthony Rathé on 16/10/2017 and later
 */
public class AutoPilot implements Autopilot {

    public AutoPilot() {

    	// set the controller of the autopilot
    	this.setController(new AutoPilotController(this));

    }

    @Override
    public AutopilotOutputs simulationStarted(AutopilotConfig config, AutopilotInputs inputs) throws IOException {
            configureAutopilot(config, inputs);
        return getControlOutputs(inputs);
    }


    @Override
    public AutopilotOutputs timePassed(AutopilotInputs inputs) throws IOException {
        return getControlOutputs(inputs);
    }

    @Override
    public void simulationEnded() {

    }
    

    /**
     * configures the autopilot at the start of the simulation
     * @param configuration the configuration of the autopilot
     * @param inputs the inputs of the autopilot
     * @author Martijn Sauwens
     */
    public void configureAutopilot(AutopilotConfig configuration, AutopilotInputs inputs) {

        //Initialize the autopilot camera
        byte[] inputImage = inputs.getImage();
        int nbRows = configuration.getNbRows();
        int nbColumns = configuration.getNbColumns();
        float horizViewAngle = configuration.getHorizontalAngleOfView();
        float verticViewAngle = configuration.getVerticalAngleOfView();
        this.setAPCamera(new AutoPilotCamera(inputImage, horizViewAngle, verticViewAngle, nbRows, nbColumns));


        //initialize other parameters
        this.setMaxThrust(configuration.getMaxThrust());
		this.setEngineMass(configuration.getEngineMass());
		this.setMainWingMass(configuration.getWingMass());
		this.setStabilizerMass(configuration.getTailMass());

        this.configuredAP = true;

    }



    private AutopilotOutputs getControlOutputs(AutopilotInputs inputs){
    	AutoPilotController controller = this.getController();
    	controller.setCurrentInputs(inputs);
    	return controller.getControlActions();
	}


    /**
     * getter for the maximum thrust
     * @return the maximum thrust
     * @author Martijn Sauwens
     */
    public float getMaxThrust() {
        return maxThrust;
    }
    /**
     * setter for the maximum thrust
     * @param maxThrust the maximum thrust the AP may issue on the drone
     * @author Martijn Sauwens
     */
    public void setMaxThrust(float maxThrust) {
        this.maxThrust = maxThrust;
    }


	private List<Vector> currentPath;
	private AutoPilotCamera APCamera;


    private boolean configuredAP;
    private float maxThrust = 1f;


	//------- Pathfinding -------
	/**
	 * @author anthonyrathe
	 */
	private void updatePath(AutopilotInputs inputs) throws IOException{
		int[] start = this.getPosition(inputs).toIntArray();
		int[] end = this.getDestinationPosition().toIntArray();
		Pathfinding pathFinding = new Pathfinding(new World());
		List<int[]> pathInt = pathFinding.searchPath(start, end);
		List<Vector> newPath = new ArrayList<Vector>();
		for (int[] position : pathInt){
			newPath.add(new Vector(position[0], position[1], position[2]));
		}
		this.currentPath = newPath;
	}
	
	/**
	 * @author anthonyrathe
	 * @return
	 */
	private List<Vector> getPath(){
		return this.currentPath;
	}
	
	/**
	 * @author anthonyrathe
	 */
	private void removeNodeFromPath(Vector node){
		this.currentPath.remove(node);
	}
	
	/**
	 * @author anthonyrathe
	 * @return
	 */
	private Vector getPosition(AutopilotInputs inputs){
		return new Vector(inputs.getX(), inputs.getY(), inputs.getZ());
	}
	
	/**
	 * @author anthonyrathe
	 */
	protected AutoPilotCamera getAPCamera() throws NullPointerException{
		if (this.APCamera == null){
			throw new NullPointerException("No APCamera was assigned to this AutoPilot");
		}
		return this.APCamera;
	}
	
	/**
	 * @author anthonyrathe
	 */
	public void setAPCamera(AutoPilotCamera newAPCamera){
		this.APCamera = newAPCamera;
	}
	
	/**
	 * @author anthonyrathe
	 * @return
	 */
	private Vector getDestinationPosition(){
		return getAPCamera().getDestination();
	}
	
	/**
	 * @author anthonyrathe
	 * @return
	 */
	private World getWorld(){
		return getAPCamera().getWorld();
	}
	//------- END Pathfinding -------
	
	//------- Actual Autopilot -------
	/**
	 * @author anthonyrathe
	 * @return the node that currently is closest to the drone
	 */
	private Vector getNextNode(AutopilotInputs inputs) throws IOException{
		updatePath(inputs);
		Vector destination = getDestinationPosition();
		Vector currentPosition = this.getPosition(inputs);
		Vector nextNode = destination;
		float smallestDistance = currentPosition.distanceBetween(getDestinationPosition());
		for (Vector node : this.getPath()){
			if(node.distanceBetween(currentPosition) <= NODE_REACHED_DISTANCE){
				this.removeNodeFromPath(node);
			}else if (node.distanceBetween(currentPosition) < smallestDistance 
					&& node.distanceBetween(destination) <= currentPosition.distanceBetween(destination)){
				smallestDistance = node.distanceBetween(currentPosition);
				nextNode = node;
			}
		}
		return nextNode;
	}

	
	/**
	 * Method that updates the desired inclinations and thrust
	 * Strategy applied:
	 * 	- find the closest node
	 * 	- determine if node lies in the left or right half-space in relation to the drone (and the horizontal angle it makes with the orientationvector of the drone, ranging from -PI to 0 if node is to the left, and from 0 to +PI if node is to the right) 
	 * 	- determine if node lies in the upper or lower half-space in relation to the drone (and the vertical angle it makes with the orientationvector of the drone, ranging from -PI to 0 if node is underneath the drone, and from 0 to +PI if node is above the drone)
	 * 	- determine whether to roll clockwise, counterclockwise or not at all (based on previously determined angles)
	 *  - determine whether to climb, descend or do nothing at all (based on previously determined angles)
	 * @author anthonyrathe
	 * @note this version of update will be used later on, when our image recognition allows for a 3D-mapping of the world
	 * For the version currently in use, we will be basing our oriëntation on the relative position of red pixels on the 2D
	 * surface of the screen.
	 */

	//TODO Max angle of attack error uitwerken
	/*public void update(AutopilotInputs inputs) throws IOException{

		Vector perpendicularAxis = pitchRollYawToWorld(inputs.getPitch()-(float)Math.PI/2, inputs.getRoll(), inputs.getHeading()); //pointed to the roof of the drone
		Vector lateralAxis = pitchRollYawToWorld(inputs.getPitch(), inputs.getRoll(), inputs.getHeading()+(float)Math.PI/2); //pointed to the left of the drone
		
		Vector directionToNode = getPosition(inputs).vectorDifference(getDestinationPosition());
		float verticalAngle = perpendicularAxis.getAngleBetween(directionToNode);
		float horizontalAngle = lateralAxis.getAngleBetween(directionToNode);
		
		// Ascend/Descend
		if (verticalAngle > Math.PI){
			throw new IOException("Undefined angles");
		}else if(verticalAngle > Math.PI + THRESHOLD_ANGLE && verticalAngle <= Math.PI){
			// Descend
			this.startDescend();
		}else if(verticalAngle <= Math.PI + THRESHOLD_ANGLE && verticalAngle >= Math.PI - THRESHOLD_ANGLE){
			// Stop descending/ascending
			this.stopAscendDescend();
		}else if(verticalAngle >= 0f && verticalAngle < Math.PI - THRESHOLD_ANGLE){
			// Ascend
			this.startAscend();
		}

		
		// Roll
		if (horizontalAngle > Math.PI){
			throw new IOException("Undefined angles");
		}else if(horizontalAngle > Math.PI + THRESHOLD_ANGLE && horizontalAngle <= Math.PI){
			// Roll clockwise
			this.clockRollStart();
		}else if(horizontalAngle <= Math.PI + THRESHOLD_ANGLE && horizontalAngle >= Math.PI - THRESHOLD_ANGLE){
			// Stop rolling
			this.stopRoll();
		}else if(horizontalAngle >= 0f && horizontalAngle < Math.PI - THRESHOLD_ANGLE){
			// Roll counterclockwise
			this.counterClockRollStart();
		} 
		
	}*/


	
	
	
	//------- END Actual Autopilot -------



	/*
    Getters & Setters
     */

	/**
	 * Getter for the autopilot controller
	 * @return the controller of the autopilot
	 */
	public AutoPilotController getController() {
		return controller;
	}

	/**
	 * setter for the autopilotController other part of the bidirectional relationship
	 * @param controller the desired controller
	 */
	public void setController(AutoPilotController controller) {
		if(!this.canHaveAsController(controller))
			throw new IllegalArgumentException(INVALID_CONTROLLER);
		this.controller = controller;
	}

	public boolean canHaveAsController(AutoPilotController controller){

		return controller.getAssociatedAutopilot() == this && this.controller == null;
	}

	/**
	 * Getter for the main wing mass of the drone
	 * @return a floating point number containing the mass of the main wing
	 */
	public float getMainWingMass() {
		return mainWingMass;
	}

	/**
	 * Setter for the main wing mass of the drone
	 * @param mainWingMass floating point number containing the mass of the main wing
	 */
	public void setMainWingMass(float mainWingMass) {
		this.mainWingMass = mainWingMass;
	}

	/**
	 * Getter for the mass of the stabilizer
	 * @return floating point number containing the stabilizer mass
	 */
	public float getStabilizerMass() {
		return stabilizerMass;
	}

	/**
	 * Setter for the mass of the stabilizer
	 * @param stabilizerMass the mass of the stabilizer
	 */
	public void setStabilizerMass(float stabilizerMass) {
		this.stabilizerMass = stabilizerMass;
	}

	/**
	 * Getter for the mass of the engine
	 * @return floating point containing the mass of the enige
	 */
	public float getEngineMass() {
		return engineMass;
	}

	/**
	 * Setter for the engine mass
	 * @param engineMass the mass of the engine
	 */
	public void setEngineMass(float engineMass) {
		this.engineMass = engineMass;
	}

	/**
	 * Object that stores the autopilot controller
	 */
	private AutoPilotController controller;
	/**
	 * variable that stores the mass of the main wings
	 */
	private float mainWingMass;
	/**
	 * variable that stores the mass of the stabilizers
	 */
	private float stabilizerMass;

	/**
	 * variable that stores the mass of the engine
	 */
	private float engineMass;

	//------- Parameters -------
	private static final float STANDARD_INCLINATION = (float)Math.PI/8;
	private static final float SHARP_INCLINATION = (float)Math.PI/4;
	private static final float STABLE_INCLINATION = (float)Math.PI/12;
	private static final float THRESHOLD_ANGLE = (float)Math.PI/36;
	private static final float THRESHOLD_PIXELS = 5f;
	private static final float INCREASE_THRUST_ANGLE = (float)(Math.PI*0.025);
	private static final int STANDARD_CUBE_SIZE = 10;
	private static final float NODE_REACHED_DISTANCE = 4f;
	private static final float STANDARD_THRUST = 32.859283f;
	private static final float CUBE_LOCATION_DELTA_THRESHOLD = 0.5f;


    /*
    Error messages
     */
    public final static String INVALID_THRUST = "The supplied thrust is out of bounds";
	public final static String INVALID_CONTROLLER = "The controller is already initialized";

}

