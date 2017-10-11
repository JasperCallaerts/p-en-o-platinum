package internal;

import java.util.List;

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
	 * projects a vector of the drone axis on the world axis
	 * @param vector the vector to project
	 * @return a new vector containing the projection
	 * @author Martijn Sauwens
	 */
	public Vector droneOnWorld(Vector vector){
		return getDroneToWorldTransformMatrix().matrixVectorProduct(vector);
	}

	/**
	 * projects a vector in the world axis to a vector in the drone axis
	 * @param vector the vector to project on the drone axis
	 * @return a new vector containing the projection
	 * @author Martijn Sauwens
	 */
	public Vector worldOnDrone(Vector vector){
		SquareMatrix transform = this.getDroneToWorldTransformMatrix();
		SquareMatrix transpose = transform.transpose();

		return transpose.matrixVectorProduct(vector);
	}

	/**
	 * Calculates the transformation matrix for the drone to world axis
	 * @return a square matrix containing the transformation matrix for the current drone configuration;
	 * @author  Jasper Callaerts & Martijn Sauwens
	 */
	private SquareMatrix getDroneToWorldTransformMatrix(){
		SquareMatrix heading = SquareMatrix.getHeadingTransformMatrix(this.getHeading());
		SquareMatrix pitch = SquareMatrix.getPitchTransformMatrix(this.getPitch());
		SquareMatrix roll = SquareMatrix.getRollTransformMatrix(this.getRoll());

		return heading.matrixProduct(pitch).matrixProduct(roll);
	}

	/**
	 * Calculates the total external forces on the drone which are: lift, gravity and thrust
	 * @return the total external forces on the drone
	 * @author Martijn Sauwens
	 */
	public Vector getTotalExternalForces(){

		// calculate the force exerted on the wings
		Wing[] wingArray = this.getWingArray();
		int nbOfWings = wingArray.length;
		Vector[] liftVectors = new Vector[nbOfWings];

		for(int index = 0; index != nbOfWings; index++){
			liftVectors[index] = wingArray[index].getLift();
		}

		Vector totalLift = Vector.sumVectorArray(liftVectors);
		// transform the thrust vector of the drone to the world axis
		Vector thrust = this.droneOnWorld(this.getThrustVector());

		// get the gravitational force exerted on the drone
		Vector gravity = this.getGravity();

		// create array containing all the forces exerted on the drone
		Vector[] forceArray = { totalLift, thrust, gravity};

		return Vector.sumVectorArray(forceArray);

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
	 * @param velocity the desired velocity of the drone
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
	 * @Basic
	 */
	public float getThrust() {
		return thrust;
	}

	/**
	 * Gets the thrust vector of the drone in the drone axis
	 * @return a vector containing the thrust of the drone in the drone axis
	 * @author Martijn Sauwens
	 */
	public Vector getThrustVector(){
		return new Vector(-this.getThrust(), 0,0);
	}

	/**
	 * Setter for the thrust of the drone
	 * @param thrust the desired new thrust
	 * @throws IllegalArgumentException if the new thrust is not valid
	 * @author Martijn Sauwens
	 */
	public void setThrust(float thrust) throws IllegalArgumentException {
		if(this.canHaveAsThrust(thrust)){
			this.thrust = thrust;
		}else{
			throw new IllegalArgumentException(Drone.THRUST_OUT_OF_RANGE);
		}
	}

	/**
	 * Getter for the gravitational force exerted on the drone given in the world axis
	 * @return a vector containing the gravitational force exterted on the drone
	 * @author Martijn Sauwens
	 */
	public Vector getGravity(){
		Wing[] wingArray = this.getWingArray();
		float totalMass = this.getDroneMass() + getEngineMass();

		for(Wing wing: wingArray){
			totalMass +=  wing.getMass();
		}

		float scalarGravity = totalMass*GRAVITY;

		return new Vector(0.0f, - scalarGravity, 0.0f);
	}


	
	/**
	 * Checks if the new thrust is allowed
	 * @param thrust the thrust to be checked
	 * @return true if and only if the thrust is in the range [0, this.getMaxThrust]
	 */
	public boolean canHaveAsThrust(float thrust){
		
		return thrust>=0 && thrust <= this.getMaxThrust();
	}

	/**
	 * Getter for the mass of the drone
	 * @return the mass of the drone
	 */
	public float getDroneMass() {
		return droneMass;
	}

	/**
	 * Checkers if the drone mass is valid
	 * @param droneMass the mass of the drone
	 * @return true if and only if the mass > 0
	 */
	public boolean canHaveAsDroneMass(float droneMass){
		return droneMass > 0;
	}

	/** Method that checks if a suggested inclination is valid.
	 * @param inclination the inclinaton of a wing
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
	public HorizontalWing getRightWing() {
		return rightWing;
	}

	/**
	 * Martijn Sauwens
	 * Setter of the right wing of the drone, the binary relationship can only be created if
	 * the drone has no right wing yet or the wing is not attached to another drone
	 * @param rightWing the right wing of the drone
	 */
	public void setRightWing(HorizontalWing rightWing) throws NullPointerException, IllegalArgumentException{
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
	public boolean canHaveAsRightWing(HorizontalWing rightWing){
		return this.getRightWing() == null;
	}

	/**
	 * Getter for the left wing of the drone
	 */
	public HorizontalWing getLeftWing() {
		return leftWing;
	}

	/**
	 * Martijn Sauwens
	 * Setter for the left wing of the drone
	 * @param leftWing the left wing of the drone
	 */
	public void setLeftWing(HorizontalWing leftWing) throws IllegalArgumentException, NullPointerException{
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
	public boolean canHaveAsLeftWing(HorizontalWing leftWing){
		return this.getLeftWing() == null;
	}

	/**
	 * getter of the horizontal stabilizer
	 * @return
	 */
	public HorizontalWing getHorizontalStab() {
		return horizontalStab;
	}

	/**
	 * Martijn Sauwens
	 * setter for the horizontal stabilizer
	 * @param horizontalStab the horizontal stabilizer to be attached to the drone
	 */
	public void setHorizontalStab(HorizontalWing horizontalStab) throws IllegalArgumentException, NullPointerException {
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
	public boolean canHaveAsHorizontalStab(HorizontalWing horizontalStab){
		return this.getHorizontalStab() == null;
	}

	/**
	 * Getter for the vertical stabilizer
	 */
	public VerticalWing getVerticalStab() {
		return verticalStab;
	}

	/**
	 * Martijn Sauwens
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
	 * Returns true if and only if there is no vertical stabilizer attached to the drone.
	 * @param vercticalStab the vertical stabilizer to be attached
	 */
	public boolean canHaveAsVerticalStab(VerticalWing vercticalStab){
		return this.getVerticalStab() == null;
	}

	/**
	 * Creates an array contianing all the wings of the drone
	 * @return an array containing all the wings of the drone
	 */
	public Wing[] getWingArray(){
		return new Wing[]{this.getRightWing(), this.getLeftWing(), this.getHorizontalStab(), this.getVerticalStab()};
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
	 * Getter for the engine mass of the drone
	 */
	public float getEngineMass() {
		return engineMass;
	}

	/**
	 * Checker for the mass of the engine
	 * @param engineMass the mass of the engine
	 * @return true if and only if the mass is strictly positive
	 */
	public boolean canHaveAsEngineMass(float engineMass){
		return engineMass > 0;
	}

	/**
	 * Martijn Sauwens
	 * sets the engine position based on the configuration of the drone
	 * @post the center of mass is (0,0,0) in the drone's axis.
	 */
	private void setEnginePosition() throws NullPointerException{
		HorizontalWing horizontalStab = this.getHorizontalStab();
		VerticalWing verticalStab = this.getVerticalStab();
		float engineMass = this.getEngineMass();

		if(horizontalStab == null || verticalStab == null)
			throw new NullPointerException();

		if(this.getEngineMass() == 0.0f)
			throw new IllegalArgumentException(UNINITIALIZED_ENGINEMASS);

		float horizontalStabPos = horizontalStab.getRelativePosition().getzValue();
		float verticalStabPos = verticalStab.getRelativePosition().getzValue();
		float horizontalStabMass = horizontalStab.getMass();
		float verticalStabMass = verticalStab.getMass();

		this.enginePos = new Vector(0,0,-(horizontalStabMass*horizontalStabPos + verticalStabMass*verticalStabPos) / engineMass);
	}

	/**
	 * Variable containing the right wing of the drone (immutable)
	 */
	private HorizontalWing rightWing;

	/**
	 * Variable containing the left wing of the drone (immutable)
	 */
	private HorizontalWing leftWing;

	/**
	 * Variable containing the horizontal stabilizer of the drone (immutable)
	 */
	private HorizontalWing horizontalStab;

	/**
	 * Variable containing the vertical stabilizer of the drone (immutable)
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
	 * A variable containing the inertia moment of the drone (immutable)
	 */
	private float inertiaMoment;

	/**
	 * A variable containing the mass of the drone (immutable)
	 */
	private float droneMass;

	/**
	 * A variable containing the mass of the engine of the drone (immutable)
	 */
	private float engineMass;

	/**
	 * A variable containing the position of the engine of the drone (immutable)
	 */
	private Vector enginePos;
	
	/**
	 * A variable containing the maximum thrust of the drone (immutable)
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
	private static float AP_CALC_TIME = 0.1f;


	/**
	 * Constant: the gravity zone constant for Belgium (simulation place)
	 */
	private static float GRAVITY = 9.81060f;

	/*
	 * Error Messages:
	 */
	
	private final static String THRUST_OUT_OF_RANGE = "The thrust is out of range: [0, this.maxThrust]";
	private final static String INCLINATION_OUT_OF_RANGE = "The inclination is out of range: [0, 2.PI[";
	private final static String VELOCITY_ERROR = "The velocity exceeds the upper limit";
	private final static String WING_EXCEPTION = "the wings are null references or the drone has already wings" +
			"attached to it";
	private final static String UNINITIALIZED_ENGINEMASS = "The mass of the engine is uninitialized";
}
