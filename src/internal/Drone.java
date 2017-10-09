package internal;

import java.util.Map;

/**
 * 
 * @author Anthony Rathé & ...
 *
 */
public class Drone extends WorldObject{
	Drone(Autopilot AP){
		this.AP = AP;
	}

	/**
	 * transformationMatrix*Vector == projected vector
	 * @param vector
	 * @return
	 */
	public Vector projectOnWorld(Vector vector){
		return getTransformationMatrix().matrixProduct(vector.convertToMatrix()).convertToVector();
	}

	/**
	 * Calculated by Jasper Callaerts
	 * @return
	 */
	private MathMatrix<Float> getTransformationMatrix(){
		MathMatrix<Float> heading = MathMatrix.getHeadingTransformMatrix(this.getHeading());
		MathMatrix<Float> pitch = MathMatrix.getPitchTransformMatrix(this.getPitch());
		MathMatrix<Float> roll = MathMatrix.getRollTransformMatrix(this.getRoll());

		return heading.matrixProduct(pitch).matrixProduct(roll);
	}
	
	public void move(float time){
		
	}


	/**
	 * returns the center of mass for a given collection of point masses in space
	 * @param massPositions a map containing the positions and the mass of the point-masses
	 * @return sum(vector*mass)/sum(mass) (center of mass equation)
	 */
	public static Vector centerOfMass(Map<Vector, Float> massPositions){
		
		Vector sumVector = new Vector();
		float totalMass = (float) 0.0;
		for (Vector massPosition: massPositions.keySet()){
			
			 float mass = massPositions.get(massPosition);
			 totalMass += mass;
			 
			 sumVector = sumVector.vectorSum(massPosition.scalarMult(mass));
		}
		
		return sumVector.scalarMult(1/totalMass);
	}
	
	/**
	 * returns the moment of inertia for a given collection of point masses and a center of mass
	 * for formula see https://socratic.org/questions/how-do-you-find-moment-of-inertia-of-three-point-masses
	 */
	public static float momentOfInerita(Map<Vector, Float> massPositions, Vector centerOfMass){
		
		float inertia = 0;
		
		for(Vector massPosition: massPositions.keySet()){
			float mass = massPositions.get(massPosition);
			Vector diffVector = massPosition.vectorDifference(centerOfMass);
			float squaredDistance = diffVector.scalarProduct(diffVector);
			inertia += squaredDistance*mass;
		}
		return inertia;
	}
	
	
	
	/**
	 * Getter for the velocity of the drone
	 * @return
	 */
	public Vector getVelocity() {
		return velocity;
	}


	/**
	 * Setter for the velocity of the drone
	 * @param velocity
	 * @throws IllegalArgumentException if the speed is invalid
	 */
	public void setVelocity(Vector velocity) throws IllegalArgumentException {
		if(this.isValidVelocity(velocity)){
			this.velocity = velocity;
		}else{
			throw new IllegalArgumentException(VELOCITY_ERROR);
		}
	}
	
	public boolean isValidVelocity(Vector velocity){
		return velocity.getSize() <= LIGHTSPEED;
	}


	/**
	 * getter for the maximum Thrust of the drone
	 * @return
	 */
	public float getMaxThrust() {
		return maxThrust;
	}
	
	
	/**
	 * getter for the thrust of the drone
	 * @return
	 * @Basic
	 */
	public float getThrust() {
		return thrust;
	}


	/**
	 * Setter for the thrust of the drone
	 * @param thrust the desired new thrust
	 * @throws IllegalArgumentException if the new thrust is not valid
	 * @Basic
	 */
	public void setThrust(float thrust) throws IllegalArgumentException {
		if(this.canHaveAsThrust(thrust)){
			this.thrust = thrust;
		}else{
			throw new IllegalArgumentException(Drone.THRUST_OUT_OF_RANGE);
		}
	}

	
	/**
	 * Checks if the new thrust is allowed
	 * @param thrust the thrust to be checked
	 * @return true if and only if the thrust is in the range [0, this.getMaxThrust]
	 */
	public boolean canHaveAsThrust(float thrust){
		
		return thrust>=0 && thrust <= this.getMaxThrust();
	}

	/** Method that checks if a suggested inclination is valid.
	 * @param inclination
	 * @author anthonyrathe
	 */
	private boolean canHaveAsInclination(float inclination){
		return inclination >= 0 && inclination < 2*Math.PI;
	}

	/**
	 * Method that sets the new left wing inclination.
	 * @param newLeftWingInclination the new inclination at which the left wing should be set
	 * @author anthonyrathe
	 */
	private void setLeftWingInclination(float newLeftWingInclination){
		if (this.canHaveAsInclination(newLeftWingInclination)){
			this.leftWingInclination = newLeftWingInclination;
		}else{
			throw new IllegalArgumentException(Drone.INCLINATION_OUT_OF_RANGE);
		}
	}

	/**
	 * Method that gets the current left wing inclination.
	 * @author anthonyrathe
	 */
	public float getLeftWingInclination(){
		return this.leftWingInclination;
	}

	/**
	 * Method that sets the new right wing inclination.
	 * @param newRightWingInclination the new inclination at which the right wing should be set
	 * @author anthonyrathe
	 */
	private void setRightWingInclination(float newRightWingInclination){
		if (this.canHaveAsInclination(newRightWingInclination)){
			this.rightWingInclination = newRightWingInclination;
		}else{
			throw new IllegalArgumentException(Drone.INCLINATION_OUT_OF_RANGE);
		}
	}

	/**
	 * Method that gets the current right wing inclination.
	 * @author anthonyrathe
	 */
	public float getRightWingInclination(){
		return this.leftWingInclination;
	}

	/**
	 * Method that sets the new horizontal stabilizer inclination.
	 * @param newHorStabInclination the new inclination at which the horizontal stabilizer should be set
	 * @author anthonyrathe
	 */
	private void setHorStabInclination(float newHorStabInclination){
		if (this.canHaveAsInclination(newHorStabInclination)){
			this.horStabInclination = newHorStabInclination;
		}else{
			throw new IllegalArgumentException(Drone.INCLINATION_OUT_OF_RANGE);
		}
	}

	/**
	 * Method that gets the current horizontal stabilizer inclination.
	 * @author anthonyrathe
	 */
	public float getHorStabInclination(){
		return this.horStabInclination;
	}

	/**
	 * Method that sets the new vertical stabilizer inclination.
	 * @param newVerStabInclination the new inclination at which the vertical stabilizer should be set
	 * @author anthonyrathe
	 */
	private void setVerStabInclination(float newVerStabInclination){
		if (this.canHaveAsInclination(newVerStabInclination)){
			this.verStabInclination = newVerStabInclination;
		}else{
			throw new IllegalArgumentException(Drone.INCLINATION_OUT_OF_RANGE);
		}
	}

	/**
	 * Method that gets the current vertical stabilizer inclination.
	 * @author anthonyrathe
	 */
	public float getVerStabInclination(){
		return this.verStabInclination;
	}

	/**
	 * Moves the drone for a given amount of time, taking in account autopilot input.
	 * @param duration the amount of time the drone should be moved
	 * @author anthonyrathe
	 */
	public void evolve(float duration){
		while (duration > 0){
			if (this.nextStateAvailable()){
				if (duration >= this.getQueueTime()){
					this.move(this.getQueueTime());
					this.nextState();
					duration = duration - this.getQueueTime();
					this.setQueueTime((float)0.0);
				}else{
					this.move(duration);
					this.setQueueTime(getQueueTime()-duration);
					duration = (float)0.0;
				}
			}else{
				Autopilot AP = this.getAutopilot();
				this.setNextThrust(AP.getThrust());
				this.setNextLeftWingInclination(AP.getLeftWingInclination());
				this.setNextRightWingInclination(AP.getRightWingInclination());
				this.setNextHorStablInclination(AP.getHorStabInclination());
				this.setNextVerStablInclination(AP.getVerStabInclination());
				if (duration >= AP_CALC_TIME){
					duration = duration - AP_CALC_TIME;
					this.move(AP_CALC_TIME);
					this.nextState();
				}else{
					this.move(duration);
					this.setQueueTime(AP_CALC_TIME-duration);
					duration = (float)0.0;
				}
			}
		}
	}

	/**
	 * Moves the drone for a given amount of time, not taking in account any state changes.
	 * @param duration the amount of time the drone should be moved as it is in its current state
	 */
	public void move(float duration){

	}

	/**
	 * Changes the state of the drone to the next state, as calculated earlier by the autopilot.
	 * State will remain the same if there is no queue-time.
	 */
	public void nextState(){
		if (this.nextStateAvailable()){
			this.setThrust(this.getNextThrust());
			this.setLeftWingInclination(this.getNextLeftWingInclination());
			this.setRightWingInclination(this.getNextRightWingInclination());
			this.setHorStabInclination(this.getNextHorStabInclination());
			this.setVerStabInclination(this.getNextVerStabInclination());
		}
	}


	/**
	 * A variable containing the autopilot loaded onto the drone
	 */
	private final Autopilot AP;

	/**
	 * Method returning the autopilot loaded onto the drone
	 */
	public Autopilot getAutopilot(){
		return this.AP;
	}

	//Todo: comment for happiness of profs

	/**
	 * Getter for the orientation of the drone
	 * @return a vector of the following format: (heading, pitch, roll)
	 */
	public Vector getOrientation() {
		return Orientation;
	}

	/**
	 * Setter for the orientation of the drone
	 * @param orientation vector containing the orientation of the drone
	 *                    structured (heading, pitch, roll)
	 */
	public void setOrientation(Vector orientation) {
		Orientation = orientation;
	}

	public void setOrientation(float heading, float pitch, float roll){
		this.Orientation = new Vector(heading, pitch, roll);
	}

	/**
	 * @return the heading (rotation around the x-axis) of the drone
	 */
	public float getHeading(){
		return this.getOrientation().getxValue();
	}

	/**
	 * @return the pitch (rotation around the y axis) of the drone
	 */
	public float getPitch(){
		return this.getOrientation().getyValue();
	}

	/**
	 * @return the roll (rotation around the z-axis) of the drone
	 */
	public float getRoll(){
		return this.getOrientation().getzValue();
	}


	/**
	 * Getter of the right wing of the drone
	 */
	public HorizonalWing getRightWing() {
		return rightWing;
	}

	/**
	 * Setter of the right wing of the drone, the binary relationship can only be created if
	 * the drone has no right wing yet or the wing is not attached to another drone
	 * @param rightWing the right wing of the drone
	 */
	public void setRightWing(HorizonalWing rightWing) throws NullPointerException, IllegalArgumentException{
		if(rightWing == null){
			throw new NullPointerException();
		}
		if(this.canHaveAsRightWing(rightWing))
			try{
				// first try to set the wing to the drone, if this fails it means that the wing
				// cannot be attached to the drone
				rightWing.setDrone(this);
				this.rightWing = rightWing;

			} catch (IllegalArgumentException e){
				throw new IllegalArgumentException(e);
			}

	}

	/**
	 * @param rightWing the desired right wing
	 * @return true if and only if no other right wing is attached
	 */
	public boolean canHaveAsRightWing(HorizonalWing rightWing){
		return this.getRightWing() == null;
	}

	/**
	 * Getter for the left wing of the drone
	 */
	public HorizonalWing getLeftWing() {
		return leftWing;
	}

	/**
	 * Setter for the left wing of the drone
	 * @param leftWing the left wing of the drone
	 */
	public void setLeftWing(HorizonalWing leftWing) throws IllegalArgumentException, NullPointerException{
		if(leftWing == null){
			throw new NullPointerException();
		}

		if(canHaveAsLeftWing(leftWing)){
			try{
				leftWing.setDrone(this);
				this.leftWing = leftWing;
			}catch (IllegalArgumentException e){
				throw new IllegalArgumentException(e);
			}
		}
		this.leftWing = leftWing;
	}

	/**
	 * returns true if and only if there is no left wing attached to the drone
	 * @param leftWing the left wing of the drone
	 */
	public boolean canHaveAsLeftWing(HorizonalWing leftWing){
		return this.getLeftWing() == null;
	}

	/**
	 * getter of the horizontal stabilizer
	 * @return
	 */
	public HorizonalWing getHorizontalStab() {
		return horizontalStab;
	}

	/**
	 * setter for the horizontal stabilizer
	 * @param horizontalStab the horizontal stabilizer to be attached to the drone
	 */
	public void setHorizontalStab(HorizonalWing horizontalStab) throws IllegalArgumentException, NullPointerException {
		if(horizontalStab == null){
			throw new NullPointerException();
		}
		if(this.canHaveAsHorizontalStab(horizontalStab)) {
			try {

				horizontalStab.setDrone(this);
				this.horizontalStab = horizontalStab;

			}catch (IllegalArgumentException e){
				throw new IllegalArgumentException(e);
			}
		}
	}

	/**
	 * Returns true if and only if there is no horizontal stabilizer attached to the drone
	 * @param horizontalStab the horizontal stabilizer
	 */
	public boolean canHaveAsHorizontalStab(HorizonalWing horizontalStab){
		return this.getHorizontalStab() == null;
	}

	/**
	 * Getter for the vertical stabilizer
	 */
	public VerticalWing getVerticalStab() {
		return verticalStab;
	}

	/**
	 * Setter for the vertical stabilizer
	 * @param verticalStab the vertical stabilizer to be attached
	 */
	public void setVerticalStab(VerticalWing verticalStab) throws IllegalArgumentException, NullPointerException {
		if(verticalStab == null){
			throw new NullPointerException();
		}
		if(this.canHaveAsVerticalStab(verticalStab)){
			try{
				verticalStab.setDrone(this);
				this.verticalStab = verticalStab;
			} catch( IllegalArgumentException e){
				throw new IllegalArgumentException(e);
			}
		}
	}

	/**
	 * Getter for the rotation vector
	 * @return
	 */
	public Vector getRotationVector() {
		return rotationVector;
	}

	/**
	 * setter for the rotation vector
	 * @param rotationVector the desired rotation vector
	 */
	public void setRotationVector(Vector rotationVector) {
		this.rotationVector = rotationVector;
	}

	/**
	 * Variable containing the right wing of the drone
	 */
	private HorizonalWing rightWing;

	/**
	 * Variable containing the left wing of the drone
	 */
	private HorizonalWing leftWing;

	/**
	 * Variable containing the horizontal stabilizer of the drone
	 */
	private HorizonalWing horizontalStab;

	/**
	 * Variable containing the vertical stabilizer of the drone
	 */
	private VerticalWing verticalStab;

	/**
	 * A variable containing the position of the drone
	 */
	private Vector position;
	
	/**
	 * A variable containing the velocity of the drone
	 */
	private Vector velocity;
	
	/**
	 * A variable containing the acceleration of the drone
	 */
	private Vector acceleration;
	
	/**
	 * A variable containing the orientation of the drone, (heading, pitch, roll)
	 */
	private Vector Orientation;
	
	/**
	 * A variable containing the rotation vector of the drone (given in the world axis)
	 */
	private Vector rotationVector;
	
	/**
	 * A variable containing the angular acceleration of the drone
	 */
	private Vector angularAccelerationVector;
	
	/**
	 * a variable containing the center of mass of the drone
	 */
	private Vector massCenter;
	
	
	/**
	 * A variable containing the inertia moment of the drone
	 */
	private float inertiaMoment;
	
	/**
	 * A variable containing the maximum thrust of the drone 
	 * @Immutable
	 */
	private float maxThrust;
	
	/**
	 * A variable containing the current thrust of the drone
	 */
	private float thrust;
	
	/**
	 * A variable containing the amount of time (in seconds) the drone must wait before being able to call the autopilot
	 */
	private float queueTime;

	/**
	 * A getter that returns the current queueTime
	 */
	private float getQueueTime(){
		return this.queueTime;
	}

	/**
	 * A setter that sets the new queueTime
	 * @param newQueueTime the new amount of queue time (in seconds)
	 * @author anthonyrathe
	 */
	private void setQueueTime(float newQueueTime){
		this.queueTime = newQueueTime;
	}

	/**
	 * Method for telling whether or not a next state is available
	 */
	private boolean nextStateAvailable(){
		return this.getQueueTime() > 0;
	}

	/**
	 * A variable containing the new thrust of the drone, after queueTime has elapsed, as calculated by the autopilot
	 */
	private float nextThrust;

	/**
	 * A getter that returns the next amount of thrust
	 */
	private float getNextThrust(){
		return this.nextThrust;
	}

	/**
	 * A variable containing the current left wing inclination of the drone
	 */
	private float leftWingInclination;

	/**
	 * A variable containing the new left wing inclination of the drone, after queueTime has elapsed, as calculated by the autopilot
	 */
	private float nextLeftWingInclination;

	/**
	 * A getter that returns the next left wing inclination
	 */
	private float getNextLeftWingInclination(){
		return this.nextLeftWingInclination;
	}
	/**
	 * A variable containing the current right wing inclination of the drone
	 */
	private float rightWingInclination;

	/**
	 * A variable containing the new right wing inclination of the drone, after queueTime has elapsed, as calculated by the autopilot
	 */
	private float nextRightWingInclination;

	/**
	 * A getter that returns the next right wing inclination
	 */
	private float getNextRightWingInclination(){
		return this.nextRightWingInclination;
	}

	/**
	 * A variable containing the current horizontal stabilizer inclination of the drone
	 */
	private float horStabInclination;

	/**
	 * A variable containing the new horizontal stabilizers inclination of the drone, after queueTime has elapsed, as calculated by the autopilot
	 */
	private float nextHorStabInclination;

	/**
	 * A getter that returns the next horizontal stabilizer inclination
	 */
	private float getNextHorStabInclination(){
		return this.nextHorStabInclination;
	}

	/**
	 * A variable containing the new vertical stabilizers inclination of the drone, after queueTime has elapsed, as calculated by the autopilot
	 */
	private float nextVerStabInclination;

	/**
	 * A getter that returns the next vertical stabilizer inclination
	 */
	private float getNextVerStabInclination(){
		return this.nextVerStabInclination;
	}

	/*
	 * Constants
	 */
	/**
	 * Constant: light speed, used in velocity check, maybe redundant
	 */
	private static float LIGHTSPEED = 300000000;
	
	/**
	 * Constant: amount of seconds it takes for the autopilot to generate new state
	 */
	private static float AP_CALC_TIME = (float) 0.1;

	/*
	 * Error Messages:
	 */
	
	private final static String THRUST_OUT_OF_RANGE = "The thrust is out of range: [0, this.maxThrust]";
	
	private final static String INCLINATION_OUT_OF_RANGE = "The inclination is out of range: [0, 2.PI[";

	private final static String VELOCITY_ERROR = "The velocity exceeds the upper limit";

	private final static String WING_EXCEPTION = "the wings are null references or the drone has already wings" +
			"attached to it";
}
