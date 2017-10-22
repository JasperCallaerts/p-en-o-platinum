package internal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import Autopilot.*;

/**
 * Created by Martijn on 14/10/2017.
 * Extended by Bart on 15/10/2017.
 */
public class AutoPilot implements Autopilot{

	@Override
	public AutopilotOutputs simulationStarted(AutopilotConfig config, AutopilotInputs inputs) throws IOException {
		return calculate(inputs);
	}


	@Override
	public AutopilotOutputs timePassed(AutopilotInputs inputs) throws IOException {
		return calculate(inputs);
	}

	@Override
	public void simulationEnded() {
		// TODO close world?
		
		
	}
	/**
	 * 
	 * @param inputs
	 * @return Autopilotoutputs {Thrust, L/Rwinginclination, hor/verstabinclination}
	 * 
	 * @throws IOException
	 */
	private AutopilotOutputs calculate(AutopilotInputs inputs) throws IOException{
		AutopilotOutputs output = new AutopilotOutputs() {

			@Override
			public float getThrust() {
				return calculateThrust(inputs);
			}

			@Override
			public float getLeftWingInclination() {
				return calculateLeftWingInclination(inputs);
			}

			@Override
			public float getRightWingInclination() {
				return calculateRightWingInclination(inputs);
			}

			@Override
			public float getHorStabInclination() {
				return calculateHorStabInclination(inputs);
			}

			@Override
			public float getVerStabInclination() {
				return calculateVerStabInclination(inputs);
			}
		};
		DataOutputStream dataInputStream = new DataOutputStream(new FileOutputStream(dataStreamLocationOutputs));
		AutopilotOutputsWriter.write(dataInputStream,output);
		return output;
	}
	
	private float calculateThrust(AutopilotInputs inputs){
		return 0; //TODO
	}
	private float calculateLeftWingInclination(AutopilotInputs inputs){
		return 0; //TODO
	}
	private float calculateRightWingInclination(AutopilotInputs inputs){
		return 0; //TODO
	}
	private float calculateHorStabInclination(AutopilotInputs inputs){
		return 0; //TODO
	}
	private float calculateVerStabInclination(AutopilotInputs inputs){
		return 0; //TODO
	}
	/**
	 * 
	 * @throws IOException
	 */
    public void setupAutopilotOutputs()throws IOException{
    	DataOutputStream dataOutputStream =
                new DataOutputStream(new FileOutputStream(dataStreamLocationOutputs));
    	
    	 AutopilotOutputs value = new AutopilotOutputs() {
             public float getThrust() { return getThrustOut(); }
             public float getLeftWingInclination() { return getLeftWingInclinationOut(); }
             public float getRightWingInclination() { return getRightWingInclinationOut(); }
             public float getHorStabInclination() { return getHorStabInclinationOut(); }
             public float getVerStabInclination() { return getVerStabInclinationOut(); }
         };

        AutopilotOutputsWriter.write(dataOutputStream, value);
        
    	dataOutputStream.close();
    } 
    
    /**
     * Variable for the filename that's created when making the AutopilotOutputs datastream 
     */
    private String dataStreamLocationOutputs = "APOutputs.txt";
	private float thrust;
	private float leftWingInclination;
	private float rightWingInclination;
	private float horStabInclination;
	private float verStabInclination;
	
	//------- Simple Controlling Methods -------
	/**
	 * @author anthonyrathe
	 */
	private void clockRollStart(){
		this.setLeftWingInclination((float)-Math.PI/2);
		this.setRightWingInclination((float)Math.PI/2);
	}
	
	/**
	 * @author anthonyrathe
	 */
	private void counterClockRollStart(){
		this.setLeftWingInclination((float)Math.PI/2);
		this.setRightWingInclination((float)-Math.PI/2);
	}
	
	/**
	 * @author anthonyrathe
	 */
	private void stopRoll(){
		this.setLeftWingInclination((float)0);
		this.setRightWingInclination((float)0);
	}
	
	/**
	 * @author anthonyrathe
	 */
	private void startTurnLeft(){
		this.setVerStabInclination((float)Math.PI/2);
	}
	
	/**
	 * @author anthonyrathe
	 */
	private void startTurnRight(){
		this.setVerStabInclination((float)-Math.PI/2);
	}
	
	/**
	 * @author anthonyrathe
	 */
	private void stopTurn(){
		this.setVerStabInclination(0f);
	}
	
	/**
	 * @author anthonyrathe
	 */
	private void startAscend(){
		this.setHorStabInclination((float)Math.PI/2);
	}
	
	/**
	 * @author anthonyrathe
	 */
	private void startDescend(){
		this.setHorStabInclination((float)-Math.PI/2);
	}
	
	/**
	 * @author anthonyrathe
	 */
	private void stopAscendDescend(){
		this.setHorStabInclination(0f);
	}
	
	/**
	 * @author anthonyrathe
	 */
	private void thrustOn(){
		this.setThrust(1);
	}
	
	/**
	 * @author anthonyrathe
	 */
	private void thrustOff(){
		this.setThrust(0);
	}
	
	//------- END Drone Controlling Methods -------
	
	//------- Pathfinding -------
	/**
	 * @author anthonyrathe
	 */
	private void updatePath(){
		int[] start = this.getPosition().toIntArray();
		int[] end = this.getDestinationPosition().toIntArray();
		List<int[]> pathInt = getPathfinding().searchPath(start, end);
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
	 * @return
	 */
	private Vector getPosition(){
		return new Vector(getX(), getY(), getZ());
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
	private Vector getNextNode(){
		updatePath();
		Vector nextNode = getDestinationPosition();
		float smallestDistance = this.getPosition().distanceBetween(getDestinationPosition());
		for (Vector node : this.getPath()){
			if (node.distanceBetween(this.getPosition()) < smallestDistance){
				smallestDistance = node.distanceBetween(this.getPosition());
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
	 */
	public void update(){
		Vector longitudinalAxis = Vector();
		Vector perpendicularAxis = Vector();
		Vector lateralAxis = Vector();
		
		Vector directionToNode = getPosition().vectorDifference(getNextNode());
		
		float horizontalAngle = longitudinal
	}
	
	
	
	//------- END Actual Autopilot -------
	

	public float getThrustOut() {
		return thrust;
	}
	public void setThrustOut(float thrust){
		this.thrust = thrust;
	}
	
	
	public float getLeftWingInclinationOut() {
		return leftWingInclination;
	}
	private void setLeftWingInclinationOut(float inclination){
		leftWingInclination = inclination;
	}


	public float getRightWingInclinationOut() {
		return rightWingInclination;
	}
	private void setRightWingInclinationOut(float inclination){
		rightWingInclination = inclination;
	}
	
	public float getHorStabInclinationOut() {
		return horStabInclination;
	}
	public void setHorStabInclinationOut(float inclination){
		horStabInclination = inclination;
	}

	public float getVerStabInclinationOut() {
		return verStabInclination;
	}
	public void setVerStabInclinationOut(float inclination){
		verStabInclination = inclination;
	}

}

