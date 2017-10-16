package internal;

import Autopilot.*;

/**
 * Created by Martijn on 14/10/2017.
 * Extended by Bart on 15/10/2017.
 */
public class AutoPilot implements Autopilot{

	@Override
	public AutopilotOutputs simulationStarted(AutopilotConfig config, AutopilotInputs inputs) {

		// TODO Auto-generated method stub
		//bereken output naar drone en gebruik stream om in outputfile te plaatsen
		//geef config info door aan drone
		//AutopilotOutputsWriter.write(stream, value);
		return null;
	}


	@Override
	public AutopilotOutputs timePassed(AutopilotInputs inputs) {
		//update output drone
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void simulationEnded() {
		// TODO Auto-generated method stub
		
	}
	
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
	
	private int[] 

	private float thrust;
	private float leftWingInclination;
	private float rightWingInclination;
	private float horStabInclination;
	private float verStabInclination;
	
	

	public float getThrust() {
		return thrust;
	}
	public void setThrust(float thrust){
		this.thrust = thrust;
	}
	
	
	public float getLeftWingInclination() {
		return leftWingInclination;
	}
	private void setLeftWingInclination(float inclination){
		leftWingInclination = inclination;
	}


	public float getRightWingInclination() {
		return rightWingInclination;
	}
	private void setRightWingInclination(float inclination){
		rightWingInclination = inclination;
	}
	
	public float getHorStabInclination() {
		return horStabInclination;
	}
	public void setHorStabInclination(float inclination){
		horStabInclination = inclination;
	}

	public float getVerStabInclination() {
		return verStabInclination;
	}
	public void setVerStabInclination(float inclination){
		verStabInclination = inclination;
	}

}

