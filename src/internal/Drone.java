
package internal;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Math;

import Autopilot.AutopilotConfig;
import Autopilot.AutopilotConfigWriter;
import Autopilot.AutopilotInputs;
import Autopilot.AutopilotInputsWriter;
import Autopilot.AutopilotOutputs;
import gui.Cube;
import math.Vector3f;

/**
 * @author Anthony Rathé & MartijnSauwens & Bart
 * Immutable variables: maxThrust, engineMass, enginePosition, leftWing, rightWing,
 * 						horizontalStab, verticalStab, inertiaTensor
 * 	note: Orientation = (heading, pitch, roll) (in that order)
 * 	the orientation has always values in the range [-PI, PI]
 */
public class Drone implements WorldObject {

	/*
	########################## Methods used for initialisation ##########################
	 */
	/**
	 * Constructor for a drone class object
	 * @param engineMass the mass of the engine
	 * @param maxThrust the maximum thrust the engine of the drone can generate
	 * @param position the position of the drone in space
	 * @param velocity the velocity of the drone
	 * @param orientation the orientation of the drone (roll, pitch heading)
	 * @param rotationVector the rotational vector of the drone
	 * @param rightMainWing the right main wing to be attached to the drone
	 * @param leftMainWing the left main wing to be attached to the drone
	 * @param horizontalStab the horizontal stabilizer tail wing of the drone
	 * @param verticalStab the vectical stabilizet tail wing of the drone
	 * @param AP the autopilot of the drone
	 */
	public Drone(float engineMass, float maxThrust, Vector position, Vector velocity, Vector orientation,
		  Vector rotationVector, Wing rightMainWing, Wing leftMainWing, Wing horizontalStab, Wing verticalStab, AutoPilot AP) {


		if (!this.canHaveAsEngineMass(engineMass) || !this.canHaveAsMaxThrust(maxThrust)) {
			throw new IllegalArgumentException(ILLEGAL_CONFIG);
		}

		// set the immutable variables of the drone
		this.AP = AP;
		this.maxThrust = maxThrust;
		this.engineMass = engineMass;

		// set variable variables of the drone
		this.setPosition(position);
		this.setVelocity(velocity);
		this.setOrientation(orientation);
		this.setRotationVector(rotationVector);

		// attach the wings of the drone
		try {
			this.setRightWing((HorizontalWing) rightMainWing);
			this.setLeftWing((HorizontalWing) leftMainWing);
			this.setHorizontalStab((HorizontalWing) horizontalStab);
			this.setVerticalStab((VerticalWing) verticalStab);
		} catch (ClassCastException | NullPointerException e){
			throw new IllegalArgumentException(ILLEGAL_CONFIG);
		}

		//these variables are calculated from the ones above
		this.setEnginePosition();
		this.setInertiaTensor();

		//create the cube representing the drone
		this.setAssociatedCube(new Cube(position.convertToVector3f(), new Vector3f()));
		// TODO config stream
	}

	/**
	 * setter for the autopilot Configuration
	 * @param autopilotConfig the autopilot configuration
	 * @author Martijn Sauwens
	 */
	public void configureAutopilot(AutopilotConfig autopilotConfig) throws IllegalArgumentException{
		if(this.autopilotConfig != null){
			throw new IllegalArgumentException(AUTOPILOT_CONFIG);
		}
		this.autopilotConfig = autopilotConfig;
	}


	/*
	########################## Transformations and projections ##########################
	 */

	/**
	 * projects a vector of the drone axis on the world axis
	 *
	 * @param vector the vector to project
	 * @return a new vector containing the projection
	 * @author Martijn Sauwens
	 */
	public Vector droneOnWorld(Vector vector) {
		return getDroneToWorldTransformMatrix().matrixVectorProduct(vector);
	}

	/**
	 * projects a vector in the world axis to a vector in the drone axis
	 *
	 * @param vector the vector to project on the drone axis
	 * @return a new vector containing the projection
	 * @author Martijn Sauwens
	 */
	public Vector worldOnDrone(Vector vector) {
		SquareMatrix transform = this.getDroneToWorldTransformMatrix();
		SquareMatrix transpose = transform.transpose();

		return transpose.matrixVectorProduct(vector);
	}

	/**
	 * Calculates the transformation matrix for the drone to world axis
	 *
	 * @return a square matrix containing the transformation matrix for the current drone configuration;
	 * @author Jasper Callaerts & Martijn Sauwens
	 */
	private SquareMatrix getDroneToWorldTransformMatrix() {
		SquareMatrix heading = SquareMatrix.getHeadingTransformMatrix(this.getHeading());
		SquareMatrix pitch = SquareMatrix.getPitchTransformMatrix(this.getPitch());
		SquareMatrix roll = SquareMatrix.getRollTransformMatrix(this.getRoll());

		return heading.matrixProduct(pitch).matrixProduct(roll);
	}


	/**
	 * Calculates the projection of the provided rotation vector
	 * @param rotationVector the rotation vector to be projected
	 * @return a vector containing the heading, pitch and roll rotation vector
	 * 		   the vector has the following format:
	 * 		   Vector(headingRotation, pitchRotation, rollRotation)
	 * 		   for more info concering the projection see the individual transformation functions
	 * note: see notes on the calculations for clarification
	 * @author Martijn Sauwens
	 */
	public Vector getRotationHPR(Vector rotationVector){
		Vector headingRotation = this.getHeadingRotationVector(rotationVector);
		Vector pitchRotation = this.getPitchRotationVector(rotationVector);
		Vector rollRotation = this.getRollRotationVector(rotationVector);

		Vector sumHeadingPitch = headingRotation.vectorSum(pitchRotation);
		return sumHeadingPitch.vectorSum(rollRotation);
	}


	/**
	 * Calculates the heading rotation vector component for a given rotation vector
	 * the heading vector stands always with the y-axis in the world axis system
	 * @param rotationVector the rotation vector to be projected
	 * @return a vector containing the heading rotation (given in the same format as the orientation vectors)
	 * 		   return new Vector( projectedHeading, 0, 0 )
	 * @author Martijn Sauwens
	 */
	public Vector getHeadingRotationVector(Vector rotationVector){
		float heading = this.getHeading();
		float pitch = this.getPitch();

		float part1 = rotationVector.getyValue();
		float part2 = (float) (rotationVector.getzValue()*Math.cos(heading)*Math.tan(pitch));
		float part3 = (float) (rotationVector.getxValue()*Math.sin(heading)*Math.tan(pitch));

		float result = part1 + part2 + part3;

		return new Vector(result, 0.0f, 0.0f);
	}


	/**
	 * Calculates the pitch rotation vector component for a given rotation vector
	 * the pitch vector stands parallel to the x axis after the heading transformation
	 * has been applied to the world axis system
	 * @param rotationVector the rotation vector to be projected
	 * @return a vector containing the pitch rotation, given in the heading transformed world axis system
	 * 		   return new Vector (0 , projectedPitch, 0)
	 * @author Martijn Sauwens
	 */
	public Vector getPitchRotationVector(Vector rotationVector){
		float heading = this.getHeading();

		float part1 = (float) (rotationVector.getxValue()*Math.cos(heading));
		float part2 = (float) (- rotationVector.getzValue()*Math.sin(heading));

		float result = part1 + part2;

		return new Vector(0.0f, result, 0.0f);
	}

	/**
	 * Calculates the roll rotation vector component for a given rotation vector
	 * the roll vector stands parallel to the the z- axis after the heading and the pitch transformation
	 * has been applied to the world axis system
	 * @param rotationVector the rotation vector to be projected
	 * @return a vector containing the roll rotation, given in the double transformed world axis system
	 * 		   return new Vector(0, 0, rollRotationVector)
	 * @author Martijn Sauwens
	 */
	public Vector getRollRotationVector(Vector rotationVector){
		float heading = this.getHeading();
		float pitch = this.getPitch();

		float part1 = (float) (rotationVector.getxValue()*Math.sin(heading));
		float part2 = (float) (rotationVector.getzValue()*Math.cos(heading));

		float result = (float) ((part1 + part2)/Math.cos(pitch));

		return new Vector(0.0f, 0.0f, result);

	}


	/**
	 * Calculates the angular acceleration in the heading, pitch, roll system for a given
	 * angular acceleration vector
	 * @param angularAccelerationVector the angular acceleration vector in the world axis system
	 * @return a vector of format (HeadingAcceleration, PitchAcceleration, RollAcceleration)
	 * @author Martijn Sauwens
	 */
	public Vector getAngularAccelerationHPR(Vector angularAccelerationVector){
		float headingAcceleration = this.getHeadingAngularAcceleration(angularAccelerationVector);
		float pitchAcceleration = this.getPitchAngularAcceleration(angularAccelerationVector);
		float rollAcceleration = this.getRollAngularAcceleration(angularAccelerationVector);

		return new Vector(headingAcceleration, pitchAcceleration, rollAcceleration);
	}

	/**
	 * Calculates the heading angular acceleration component for the given angular acceleration vector
	 * the heading acceleration vector stands parallel to the y-axis in the world axis system
	 * @param angularAccelerationVector the vector containing the angular acceleration given in the
	 *                                  world-axis system
	 * @return the heading angular acceleration
	 * @author Martijn Sauwens
	 */
	public float getHeadingAngularAcceleration(Vector angularAccelerationVector){
		float heading = this.getHeading();
		float pitch = this.getPitch();
		Vector rotationVector = this.getRotationVector();
		Vector AAVector = angularAccelerationVector;

		float headingRotation = this.getHeadingRotationVector(rotationVector).getxValue();
		float pitchRotation = this.getPitchRotationVector(rotationVector).getyValue();

		//split up in parts for convenience
		float part1 = (float) (AAVector.getyValue() + AAVector.getzValue()*Math.cos(heading)*Math.tan(pitch));
		float part2 = (float) (-rotationVector.getzValue()*headingRotation*Math.sin(heading)*Math.tan(pitch));
		float part3 = (float) (rotationVector.getzValue()*pitchRotation*Math.cos(heading)/(Math.cos(pitch)*Math.cos(pitch)));
		float part4 = (float) (AAVector.getxValue()*Math.sin(heading)*Math.tan(pitch));
		float part5 = (float) (rotationVector.getxValue()*headingRotation*Math.cos(heading)*Math.tan(pitch));
		float part6 = (float) (rotationVector.getxValue()*pitchRotation*Math.sin(heading)/(Math.cos(pitch)*Math.cos(pitch)));

		return part1 + part2 + part3 + part4 + part5 + part6;
	}
	/**
	 * Calculates the pitch angular acceleration component for the given angular acceleration vector
	 * the pitch acceleration vector stands parallel to the x-axis after the heading transformation
	 * of the world axis system
	 * @param angularAccelerationVector the vector containing the angular acceleration
	 *                                  given in the world-axis system
	 * @return the pitch angular acceleration
	 * @author Martijn Sauwens
	 */
	public float getPitchAngularAcceleration(Vector angularAccelerationVector){
		float heading = this.getHeading();
		Vector rotationVector = this.getRotationVector();
		Vector AAVector = angularAccelerationVector;

		float headingRotation = this.getHeadingRotationVector(rotationVector).getxValue();
		//split up in parts for convenience
		float part1 = (float) (AAVector.getxValue()*Math.cos(heading) - rotationVector.getxValue()*headingRotation*Math.sin(heading));
		float part2 = (float) (- AAVector.getzValue()*Math.sin(heading) - rotationVector.getzValue()*headingRotation*Math.cos(heading));
		return part1 + part2;
	}

	/**
	 * Calculates the roll angular acceleration component for the given angular acceleration vector
	 * the roll acceleration stands parallel to the z-axis after the heading and the pitch transformation
	 * of the world axis system (in that order)
	 * @param angularAccelerationVector the vector containing the angular acceleration
	 *                                  given in the world-axis system
	 * @return the roll angular acceleration
	 * @author Martijn Sauwens
	 */
	public float getRollAngularAcceleration(Vector angularAccelerationVector){
		float heading = this.getHeading();
		float pitch = this.getPitch();
		Vector rotationVector = this.getRotationVector();
		Vector AAVector = angularAccelerationVector;

		float headingRotation = this.getHeadingRotationVector(rotationVector).getxValue();
		float pitchRotation = this.getPitchRotationVector(rotationVector).getyValue();

		//split up for convenience
		float part1 = (float) (AAVector.getzValue()*Math.cos(heading)/Math.cos(pitch) -
				rotationVector.getzValue()*headingRotation*Math.sin(heading)/Math.cos(pitch));
		float part2 = (float) (rotationVector.getzValue()*pitchRotation*Math.sin(heading)*Math.sin(pitch)/(Math.cos(pitch)*Math.cos(pitch)));
		float part3 = (float) (AAVector.getxValue()*Math.sin(heading)/Math.cos(pitch) + rotationVector.getxValue()
				*headingRotation*Math.cos(heading)/Math.cos(pitch));
		float part4 = (float) (rotationVector.getxValue()*pitchRotation*Math.sin(heading)*Math.sin(pitch)/(Math.cos(pitch)*Math.cos(pitch)));

		return part1 + part2 + part3 + part4;
	}

	/*
	########################## Next state methods ##########################
	 */

	/**
	 * advances the drone for a given time step, it changes the position, velocity, orientation and rotation
	 * variables
	 * @param deltaTime the time step
	 * @throws IOException
	 * @author Martijn Sauwens & Bart Jacobs
	 */
	@Override
	public void toNextState(float deltaTime) throws IOException{
		if(!WorldObject.isValidTimeStep(deltaTime))
			throw new IllegalArgumentException(INVALID_TIMESTEP);
		float INSIGNIFICANCE = 0.01f;

		//engage autopilot
		AutopilotInputs input = updateAutopilotInput(deltaTime);// TODO input stream
		AutoPilot AP = this.getAutopilot();
		AutopilotOutputs APO;

		if(!AP.isConfiguredAP()){
			// if the AP isn't configured yet, take the config of the autopilot
			// and configure it
			APO = AP.simulationStarted(autopilotConfig, input);
		}
		else{
			//else just calculate the next action
			APO = AP.timePassed(input);
		}
		setThrust(APO.getThrust());
		setLeftWingInclination(APO.getLeftWingInclination());
		setRightWingInclination(APO.getRightWingInclination());
		setHorStabInclination(APO.getHorStabInclination());
		setVerStabInclination(APO.getVerStabInclination());


		//--- calculations for the physics model
		//set the next state of the position & velocity of the center of mass of the drone
		Vector acceleration = this.calcAcceleration();
		Vector velocity = this.getNextVelocity(deltaTime, acceleration);
		Vector position = this.getNextPosition(deltaTime, acceleration);

		//set the next state of the orientation & rotation of the drone
		Vector angularAcceleration = this.calcAngularAcceleration();
		Vector angularAccelerationWorld = this.droneOnWorld(angularAcceleration);
		Vector rotation = this.getNextRotationVector(deltaTime, angularAccelerationWorld);
		Vector orientation = this.getNextOrientation(deltaTime, angularAccelerationWorld);

		Vector oldVelocity = this.getVelocity();
		Vector oldPosition = this.getPosition();
		Vector oldOrientation = this.getOrientation();
		Vector oldRotation = this.getOrientation();

		System.out.println("horizontalLift: " + horizontalStab.getLift());
		System.out.println("verticalLift: " + verticalStab.getLift());
		System.out.println("rightMainLift: " + rightWing.getLift());
		System.out.println("leftMainLift: " + leftWing.getLift());

		this.setVelocity(oldVelocity.driftRejection(velocity, deltaTime*INSIGNIFICANCE));
		this.setPosition(oldPosition.driftRejection(position, deltaTime*INSIGNIFICANCE));
		this.setOrientation(oldOrientation.driftRejection(orientation, deltaTime*INSIGNIFICANCE));
		this.setRotationVector(oldRotation.driftRejection(rotation, deltaTime*INSIGNIFICANCE));

		System.out.println("Velocity : " + this.getVelocity().vectorDifference(oldVelocity));
		System.out.println("Position: " + this.getPosition().vectorDifference(oldPosition));
		System.out.println("Orientation: " + this.getOrientation().vectorDifference(oldOrientation));
		System.out.println("Rotiation: " + this.getRotationVector().vectorDifference(oldRotation));
		System.out.println("Thrust: " + this.getThrust());

		Vector differencePos = this.getPosition().vectorDifference(oldPosition);

		// move the cube representing the drone
		this.getAssociatedCube().update(differencePos.convertToVector3f());

	}


	/**
	 * calculates the next orientation based on the current orientation and the angular acceleration
	 * @param deltaTime the time step taken
	 * @param angularAcceleration the angular acceleration
	 * @return a vector containing the orientation for the next step
	 */
	public Vector getNextOrientation(float deltaTime, Vector angularAcceleration){
		//set up the needed variables
		Vector currentOrientation = this.getOrientation();
		Vector currentRotation = this.getRotationVector();
		Vector RotationHPR = this.getRotationHPR(currentRotation);
		Vector angularAccelerationHPR = this.getAngularAccelerationHPR(angularAcceleration);

		//set up the next rotations
		Vector rotationVelocity = RotationHPR.scalarMult(deltaTime);
		Vector rotationAcceleration = angularAccelerationHPR.scalarMult(deltaTime*deltaTime/2.0f);

		//return the next Orientation
		return currentOrientation.vectorSum(rotationVelocity).vectorSum(rotationAcceleration);
	}

	/**
	 * Calculates the rotation vector for the next time interval expressed in the world axis
	 * @param deltaTime the time step to be taken
	 * @param angularAcceleration the angular acceleration given in the world axis
	 * @return a vector containing the next rotation vector given in the world axis
	 */
	public Vector getNextRotationVector(float deltaTime, Vector angularAcceleration){
		Vector currentRotation = this.getRotationVector();
		Vector deltaRotation = angularAcceleration.scalarMult(deltaTime);

		return currentRotation.vectorSum(deltaRotation);
	}

	/**
	 * Calculates the new velocity under the assumption that the acceleration remains constant
	 * @param deltaTime the time between the steps
	 * @param acceleration the (constant) acceleration between the steps
	 * @return a vector containing the velocity of the next time step
	 */
	public Vector getNextVelocity(float deltaTime, Vector acceleration){
		Vector currentVelocity = this.getVelocity();
		Vector deltaVelocity = acceleration.scalarMult(deltaTime);
		return currentVelocity.vectorSum(deltaVelocity);

	}


	/**
	 * Calculates the next position under the assumption that the acceleration remains constant
	 * @param deltaTime the time between the steps
	 * @param acceleration the acceleration of the drone
	 * @return a vector containing the position of the next time step
	 */
	public Vector getNextPosition(float deltaTime, Vector acceleration){
		Vector currentPosition = this.getPosition();
		Vector currentVelocity = this.getVelocity();
		Vector deltaPosVelocityPart = currentVelocity.scalarMult(deltaTime);
		Vector deltaPosAccelerationPart = acceleration.scalarMult(deltaTime*deltaTime/2.0f);

		return currentPosition.vectorSum(deltaPosVelocityPart).vectorSum(deltaPosAccelerationPart);

	}

	/**
	 * Calculates the angular acceleration based on the moment, moment of inertia and rotation vector
	 * the resulting vector is projected onto the drone axis system
	 *
	 * @return a vector containing the angular acceleration of the drone
	 * @author Martijn Sauwens
	 */
	public Vector calcAngularAcceleration() {
		SquareMatrix inverseInertiaTensor = this.getInertiaTensor().invertDiagonal();
		Vector moment = this.getTotalMomentDrone();
		Vector rotationDrone = this.worldOnDrone(this.getRotationVector());
		Vector rotationXImpulseMoment = rotationDrone.crossProduct(this.getImpulseMoment());

		Vector momentDiff = moment.vectorDifference(rotationXImpulseMoment);
		return inverseInertiaTensor.matrixVectorProduct(momentDiff);
	}

	/**
	 * calculates the acceleration vector of the drone in the world axis system
	 * @return a vector containing the acceleration of the drone in the world axis system
	 */
	public Vector calcAcceleration(){
		Vector externalForce = getTotalExternalForcesWorld();
		float totalMass = this.getTotalMass();

		return externalForce.scalarMult(1/totalMass);

	}

	/**
	 * Calculates the total external forces on the drone which are: lift, gravity and thrust
	 *
	 * @return the total external forces on the drone
	 * @author Martijn Sauwens
	 */
	public Vector getTotalExternalForcesWorld() {

		// calculate the force exerted on the wings
		Wing[] wingArray = this.getWingArray();
		int nbOfWings = wingArray.length;
		Vector[] liftVectors = new Vector[nbOfWings];

		for (int index = 0; index != nbOfWings; index++) {
			liftVectors[index] = wingArray[index].getLift();
		}

		Vector totalLift = Vector.sumVectorArray(liftVectors);

		// transform the thrust vector of the drone to the world axis
		Vector thrust = this.droneOnWorld(this.getThrustVector());

		// get the gravitational force exerted on the drone
		Vector gravity = this.getGravity();

		// create array containing all the forces exerted on the drone
		Vector[] forceArray = {totalLift, thrust, gravity};

		return Vector.sumVectorArray(forceArray);

	}

	/**
	 * Calculates the total moment exerted on the drone in the drone axis system
	 *
	 * @return a vector containing the total exerted moment on the drone in the drone axis system
	 * @author Martijn Sauwens
	 * note: the gravity and thrust force are ignored because they are parallel to their force arms
	 */
	public Vector getTotalMomentDrone() {
		Wing[] wingArray = this.getWingArray();
		int nbOfWings = wingArray.length;
		//the array containing the lift vectors projected on the drone axis
		Vector[] momentVectorsDrone = new Vector[nbOfWings];

		for (int index = 0; index != nbOfWings; index++) {
			Wing currentWing = wingArray[index];
			Vector liftOnDrone = this.worldOnDrone(currentWing.getLift());
			Vector positionWing = currentWing.getRelativePosition();
			Vector momentOnDrone = positionWing.crossProduct(liftOnDrone);
			momentVectorsDrone[index] = momentOnDrone;
		}

		return Vector.sumVectorArray(momentVectorsDrone);
	}

	/**
	 * calculates the moment of inertia of the drone on a given point in time, the impulse moment is given in the
	 * drone axis system
	 *
	 * @return a vector containing the moment of inertia
	 * @throws NullPointerException thrown if the Inertia tensor is not initialized
	 * @author Martijn Sauwens
	 */
	public Vector getImpulseMoment() throws NullPointerException {
		SquareMatrix inertiaTensor = this.getInertiaTensor();
		if (inertiaTensor == null) {
			throw new NullPointerException();
		}

		//calculate the angular velocity of the drone in the drone axis system
		Vector rotationDrone = this.worldOnDrone(this.getRotationVector());

		return inertiaTensor.matrixVectorProduct(rotationDrone);

	}



	/*
	########################## Update methods for drone and autopilot ##########################
	 */

	/**
	 * Method that checks if a suggested inclination is valid.
	 *
	 * @param inclination the inclinaton of a wing
	 * @author anthonyrathe
	 */
	private boolean canHaveAsInclination(float inclination) {
		return inclination >= -Math.PI/2 && inclination <= Math.PI/2;
	}

	/**
	 * Method that sets the new left wing inclination.
	 *
	 * @param newLeftWingInclination the new inclination at which the left wing should be set
	 * @author anthonyrathe
	 */
	private void setLeftWingInclination(float newLeftWingInclination) {
		if (this.canHaveAsInclination(newLeftWingInclination)) {
			this.getLeftWing().setWingInclination(newLeftWingInclination);
		} else {
			throw new IllegalArgumentException(Drone.INCLINATION_OUT_OF_RANGE);
		}
	}

	/**
	 * Method that gets the current left wing inclination.
	 *
	 * @author anthonyrathe & Martijn Sauwens
	 */
	public float getLeftWingInclination() {
		return this.getLeftWing().getWingInclination();
	}

	/**
	 * Method that sets the new right wing inclination.
	 *
	 * @param newRightWingInclination the new inclination at which the right wing should be set
	 * @author anthonyrathe & Martijn Sauwens
	 */
	private void setRightWingInclination(float newRightWingInclination) {
		if (this.canHaveAsInclination(newRightWingInclination)) {
			this.getRightWing().setWingInclination(newRightWingInclination);
		} else {
			throw new IllegalArgumentException(Drone.INCLINATION_OUT_OF_RANGE);
		}
	}

	/**
	 * Method that gets the current right wing inclination.
	 *
	 * @author anthonyrathe & Martijn Sauwens
	 */
	public float getRightWingInclination() {
		return this.getRightWing().getWingInclination();
	}

	/**
	 * Method that sets the new horizontal stabilizer inclination.
	 *
	 * @param newHorStabInclination the new inclination at which the horizontal stabilizer should be set
	 * @author anthonyrathe & Martijn Sauwens
	 */
	private void setHorStabInclination(float newHorStabInclination) {
		if (this.canHaveAsInclination(newHorStabInclination)) {
			this.getHorizontalStab().setWingInclination(newHorStabInclination);
		} else {
			throw new IllegalArgumentException(Drone.INCLINATION_OUT_OF_RANGE);
		}
	}

	/**
	 * Method that gets the current horizontal stabilizer inclination.
	 *
	 * @author anthonyrathe & Martijn Sauwens
	 */
	public float getHorStabInclination() {
		return this.getHorizontalStab().getWingInclination();
	}


	/**
	 * Method that sets the new vertical stabilizer inclination.
	 *
	 * @param newVerStabInclination the new inclination at which the vertical stabilizer should be set
	 * @author anthonyrathe & Martijn Sauwens
	 */

	private void setVerStabInclination(float newVerStabInclination) {
		if (this.canHaveAsInclination(newVerStabInclination)) {
			this.getVerticalStab().setWingInclination(newVerStabInclination);
		} else {
			throw new IllegalArgumentException(Drone.INCLINATION_OUT_OF_RANGE);
		}
	}


	/**
	 * Method that gets the current vertical stabilizer inclination.
	 *
	 * @author anthonyrathe & Martijn Sauwens
	 */

	public float getVerStabInclination() {
		return this.getVerticalStab().getWingInclination();
	}

	/**
	 * Method returning the autopilot loaded onto the drone
	 */
	public AutoPilot getAutopilot() {
		return this.AP;
	}

	/**
	 * @author Bart
	 * @param duration the amount of time elapsed
	 * @return autopilotinput for autopilot
	 */
	public AutopilotInputs updateAutopilotInput(float duration){

		AutopilotInputs input;
		return  new AutopilotInputs() {

			@Override
			public float getZ() {
				return getPosition().getElementAt(2);
			}

			@Override
			public float getY() {
				return getPosition().getElementAt(1);
			}

			@Override
			public float getX() {
				return getPosition().getElementAt(0);
			}

			@Override
			public float getRoll() {
				return getOrientation().getElementAt(2);
			}

			@Override
			public float getPitch() {
				return getOrientation().getElementAt(1);
			}

			@Override
			public byte[] getImage() {
				return getAPImage();
			}

			@Override
			public float getHeading() {
				return getOrientation().getElementAt(0);
			}

			@Override
			public float getElapsedTime() {
				return duration;
			}
		};


	}


	public void setAPImage(byte[] image){
		APImage = image;
	}

	private byte[] getAPImage(){
		return APImage;
	}

	/*
	 ############################# Gui configuration methods #############################
	 */

	public Cube getAssociatedCube() {
		return droneCube;
	}

	/**
	 * Setter for the drone cube
	 * @param droneCube the cube representing the drone
	 * Immutable!
	 */
	private void setAssociatedCube(Cube droneCube) {
		this.droneCube = droneCube;
	}

	/**
	 * checks of the drone can have the given cube representing it
	 * @return true if and only if the
	 */
	private boolean canHaveAsDroneCube(Cube droneCube){
		return this.getAssociatedCube() == null && droneCube.getPosition().rangeEquals(this.getPosition(), maxPosDifference);
	}
	/*
	 ############################# Drone configuration methods #############################
	  */

	/**
	 * Getter for the orientation of the drone
	 *
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
		Vector tempOrientation = normalizeOrientation(orientation);
		Orientation = tempOrientation;
	}

	/**
	 * Setter for the orientation of the drone
	 * @param heading the heading value of the rotation
	 * @param pitch the pitch value of the rotation
	 * @param roll the roll value of the rotation
	 * @author Martijn Sauwens
	 */
	public void setOrientation(float heading, float pitch, float roll) {
		this.Orientation = normalizeOrientation(new Vector(heading, pitch, roll));
	}

	/**
	 * Normalizes the given orientation, with normalization meaning setting
	 * the range of the orientation elements to the range [-PI, PI]
	 * @param orientation the vector containing the orientation of the drone (heading, pitch, roll)
	 * @return a new vector with all elements rescaled to the range [-PI, PI]
	 * @author Martijn Sauwens
	 */
	public Vector normalizeOrientation(Vector orientation){
		float[] vectorArray = new float[Vector.VECTOR_SIZE];
		for(int index = 0; index != Vector.VECTOR_SIZE; index++){
			float tempValue = orientation.getElementAt(index);
			//first set the value between the range [0, 2PI]
			tempValue = (float) (tempValue%(2*Math.PI));
			//then if the value is larger than PI subtract PI*2
			//so the range becomes [-PI, PI]
			if(tempValue > Math.PI){
				tempValue = (float) (tempValue-Math.PI*2);
			}
			vectorArray[index] = tempValue;
		}

		return new Vector(vectorArray);
	}

	/**
	 * @return the heading (rotation around the x-axis) of the drone
	 */
	public float getHeading() {
		return this.getOrientation().getxValue();
	}

	/**
	 * @return the pitch (rotation around the y axis) of the drone
	 */
	public float getPitch() {
		return this.getOrientation().getyValue();
	}

	/**
	 * @return the roll (rotation around the z-axis) of the drone
	 */
	public float getRoll() {
		return this.getOrientation().getzValue();
	}


	/**
	 * Getter of the right wing of the drone
	 */
	public HorizontalWing getRightWing() {
		return rightWing;
	}

	/**
	 * Setter of the right wing of the drone, the binary relationship can only be created if
	 * the drone has no right wing yet or the wing is not attached to another drone
	 *
	 * @param rightWing the right wing of the drone
	 * @author Martijn Sauwens
	 */
	public void setRightWing(HorizontalWing rightWing) throws NullPointerException, IllegalArgumentException {
		if (rightWing == null) {
			throw new NullPointerException();
		}
		if (this.canHaveAsRightWing(rightWing))
			try {
				// first try to set the wing to the drone, if this fails it means that the wing
				// cannot be attached to the drone
				rightWing.setDrone(this);
				this.rightWing = rightWing;

			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(e);
			}
		this.rightWing = rightWing;
	}

	/**
	 * @param rightWing the desired right wing
	 * @return true if and only if no other right wing is attached
	 */
	public boolean canHaveAsRightWing(HorizontalWing rightWing) {
		return this.getRightWing() == null;
	}

	/**
	 * Getter for the left wing of the drone
	 */
	public HorizontalWing getLeftWing() {
		return leftWing;
	}

	/**
	 * Setter for the left wing of the drone
	 * @param leftWing the left wing of the drone
	 * @author Martijn Sauwens
	 */
	public void setLeftWing(HorizontalWing leftWing) throws IllegalArgumentException, NullPointerException {
		if (leftWing == null) {
			throw new NullPointerException();
		}

		if (canHaveAsLeftWing(leftWing)) {
			try {
				leftWing.setDrone(this);
				this.leftWing = leftWing;
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(e);
			}
		}
		this.leftWing = leftWing;
	}

	/**
	 * returns true if and only if there is no left wing attached to the drone
	 *
	 * @param leftWing the left wing of the drone
	 */
	public boolean canHaveAsLeftWing(HorizontalWing leftWing) {
		return this.getLeftWing() == null;
	}

	/**
	 * getter of the horizontal stabilizer
	 *
	 * @return the horizontal stabilizer wing
	 */
	public HorizontalWing getHorizontalStab() {
		return horizontalStab;
	}

	/**
	 * setter for the horizontal stabilizer
	 *
	 * @param horizontalStab the horizontal stabilizer to be attached to the drone
	 * @author Martijn Sauwens
	 */
	public void setHorizontalStab(HorizontalWing horizontalStab) throws IllegalArgumentException, NullPointerException {
		if (horizontalStab == null) {
			throw new NullPointerException();
		}
		if (this.canHaveAsHorizontalStab(horizontalStab)) {
			try {

				horizontalStab.setDrone(this);
				this.horizontalStab = horizontalStab;

			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(e);
			}
		}
	}

	/**
	 * Returns true if and only if there is no horizontal stabilizer attached to the drone
	 *
	 * @param horizontalStab the horizontal stabilizer
	 * @author Martijn Sauwens
	 */
	public boolean canHaveAsHorizontalStab(HorizontalWing horizontalStab) {
		return this.getHorizontalStab() == null;
	}

	/**
	 * Getter for the vertical stabilizer
	 * @author Martijn Sauwens
	 */
	public VerticalWing getVerticalStab() {
		return verticalStab;
	}

	/**
	 * Setter for the vertical stabilizer
	 *
	 * @param verticalStab the vertical stabilizer to be attached
	 * @author Martijn Sauwens
	 */
	public void setVerticalStab(VerticalWing verticalStab) throws IllegalArgumentException, NullPointerException {
		if (verticalStab == null) {
			throw new NullPointerException();
		}
		if (this.canHaveAsVerticalStab(verticalStab)) {
			try {
				verticalStab.setDrone(this);
				this.verticalStab = verticalStab;
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(e);
			}
		}
	}

	/**
	 * Returns true if and only if there is no vertical stabilizer attached to the drone.
	 *
	 * @param vercticalStab the vertical stabilizer to be attached
	 * @author Martijn Sauwens
	 */
	public boolean canHaveAsVerticalStab(VerticalWing vercticalStab) {
		return this.getVerticalStab() == null;
	}

	/**
	 * Creates an array containing all the wings of the drone
	 *
	 * @return an array containing all the wings of the drone
	 * @author Martijn Sauwens
	 */
	public Wing[] getWingArray() {
		return new Wing[]{this.getRightWing(), this.getLeftWing(), this.getHorizontalStab(), this.getVerticalStab()};
	}
	/**
	 * Getter for the position variable
	 * @return a vector containing the position
	 */
	@Override
	public Vector getPosition() {
		return position;
	}

	/**
	 * Setter for the position of the drone
	 * @param position the position of the drone
	 */
	public void setPosition(Vector position) {
		this.position = position;
	}

	/**
	 * Getter for the velocity of the drone
	 * @author Martijn Sauwens
	 */
	public Vector getVelocity() {
		return velocity;
	}


	/**
	 * Setter for the velocity of the drone
	 *
	 * @param velocity the desired velocity of the drone
	 * @throws IllegalArgumentException if the speed is invalid
	 * @author Martijn Sauwens
	 */
	public void setVelocity(Vector velocity) throws IllegalArgumentException {
		if (this.isValidVelocity(velocity)) {
			this.velocity = velocity;
		} else {
			throw new IllegalArgumentException(VELOCITY_ERROR);
		}
	}

	public boolean isValidVelocity(Vector velocity) {
		return velocity.getSize() <= LIGHTSPEED;
	}


	/**
	 * getter for the maximum Thrust of the drone
	 *
	 * @return
	 */
	public float getMaxThrust() {
		return maxThrust;
	}

	/**
	 * Checks if the given max thrust can be assigned as maximum thrust of the drone
	 *
	 * @param maxThrust the desired maximum thrust of the drone
	 * @return true if and only if the maxThrust > 0
	 */
	public boolean canHaveAsMaxThrust(float maxThrust) {
		return maxThrust > 0;
	}

	/**
	 * getter for the thrust of the drone
	 *
	 * @author Martijn Sauwens
	 */
	public float getThrust() {
		return thrust;
	}

	/**
	 * Gets the thrust vector of the drone in the drone axis
	 *
	 * @return a vector containing the thrust of the drone in the drone axis
	 * @author Martijn Sauwens
	 */
	public Vector getThrustVector() {
		return new Vector(0, 0, -this.getThrust());
	}

	/**
	 * Setter for the thrust of the drone
	 *
	 * @param thrust the desired new thrust
	 * @throws IllegalArgumentException if the new thrust is not valid
	 * @author Martijn Sauwens
	 */
	public void setThrust(float thrust) throws IllegalArgumentException {
		if (this.canHaveAsThrust(thrust)) {
			this.thrust = thrust;
		} else {
			throw new IllegalArgumentException(Drone.THRUST_OUT_OF_RANGE);
		}
	}

	/**
	 * Getter for the gravitational force exerted on the drone given in the world axis
	 *
	 * @return a vector containing the gravitational force exterted on the drone
	 * @author Martijn Sauwens
	 */
	public Vector getGravity() {

		float scalarGravity = this.getTotalMass() * GRAVITY;

		return new Vector(0.0f, -scalarGravity, 0.0f);
	}


	/**
	 * Checks if the new thrust is allowed
	 *
	 * @param thrust the thrust to be checked
	 * @return true if and only if the thrust is in the range [0, this.getMaxThrust]
	 */
	public boolean canHaveAsThrust(float thrust) {

		return thrust >= 0 && thrust <= this.getMaxThrust();
	}

	/**
	 * Getter for the rotation vector
	 *
	 * @return the rotation vector of the drone
	 * @author Martijn Sauwens
	 */
	public Vector getRotationVector() {
		return rotationVector;
	}

	/**
	 * setter for the rotation vector
	 *
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
	 *
	 * @param engineMass the mass of the engine
	 * @return true if and only if the mass is strictly positive
	 */
	public boolean canHaveAsEngineMass(float engineMass) {
		return engineMass > 0;
	}

	/**
	 * Getter for the engige position
	 *
	 * @return a vector containing the engine position
	 */
	public Vector getEnginePos() {
		return this.enginePos;
	}


	/**
	 * sets the engine position based on the configuration of the drone
	 *
	 * @post the center of mass is (0,0,0) in the drone's axis.
	 * @author Martijn Sauwens
	 */
	private void setEnginePosition() throws NullPointerException {
		HorizontalWing horizontalStab = this.getHorizontalStab();
		VerticalWing verticalStab = this.getVerticalStab();
		float engineMass = this.getEngineMass();

		if (horizontalStab == null || verticalStab == null)
			throw new NullPointerException();

		if (this.getEngineMass() == 0.0f)
			throw new IllegalArgumentException(UNINITIALIZED_ENGINEMASS);

		float horizontalStabPos = horizontalStab.getRelativePosition().getzValue();
		float verticalStabPos = verticalStab.getRelativePosition().getzValue();
		float horizontalStabMass = horizontalStab.getMass();
		float verticalStabMass = verticalStab.getMass();

		this.enginePos = new Vector(0, 0, -(horizontalStabMass * horizontalStabPos + verticalStabMass * verticalStabPos) / engineMass);
	}

	/**
	 * Getter for the inertia tensor, the tensor is given in the drone axis system
	 * @return a square (diagonal) matrix containing the inertia tensor of the drone
	 */
	public SquareMatrix getInertiaTensor() {
		return this.inertiaTensor;
	}

	/**
	 * Calculates the inertia tensor based on the point masses given to the system
	 * The inertia tensor is calculated in the drone axis system.
	 *
	 * @throws IllegalArgumentException thrown if not all the parts of the drone are initialized
	 * post: new inertiaTensor == SquareMatrix({Ixx, 0.0f, 0.0f,
	 * 0.0f, Iyy, 0.0f,
	 * 0.0f, 0.0f, Izz});
	 * with Ixx sum(mi*(yi^2 + zi^2), Iyy = mi*(xi^2 + zi^2), Izz = mi*(xi^2 + yi^2)
	 * and  mi the mass of the selected point, xi, yi, zi the coordinate of the point mass
	 * for more info: https://nl.wikipedia.org/wiki/Traagheidsmoment
	 * @author Martijn Sauwens
	 */
	private void setInertiaTensor() throws IllegalArgumentException {
		float Ixx = 0;
		float Iyy = 0;
		float Izz = 0;

		if (!this.canCalcInertiaTensor())
			throw new IllegalArgumentException(UNINITIALIZED_POINTMASS);

		Wing[] wingArray = this.getWingArray();

		// calculate the inertia caused by the wings
		for (Wing wing : wingArray) {
			float mass = wing.getMass();
			Vector wingPos = wing.getRelativePosition();
			float xValue = wingPos.getxValue();
			float yValue = wingPos.getyValue();
			float zValue = wingPos.getzValue();

			Ixx += mass * (yValue * yValue + zValue * zValue);
			Iyy += mass * (xValue * xValue + zValue * zValue);
			Izz += mass * (xValue * xValue + yValue * yValue);
		}

		//calculate the inertia caused by the engine
		float engineMass = this.getEngineMass();
		Vector enginePos = this.getEnginePos();
		float zValueEngine = enginePos.getzValue();


		//the engine is always located on the z axis
		Ixx += engineMass * zValueEngine * zValueEngine;
		Iyy += engineMass * zValueEngine * zValueEngine;

		float[] inertiaArray = {Ixx, 0.0f, 0.0f,
				0.0f, Iyy, 0.0f,
				0.0f, 0.0f, Izz};
		this.inertiaTensor = new SquareMatrix(inertiaArray);

	}

	/**
	 * checks if the drone is in the right state to calculate the inertia tensor
	 *
	 * @return true if and only if all the point masses of the drone are initialized and the
	 * inertia tensor hasn't been calculated before (immutable character of the tensor)
	 * @author Martijn Sauwens
	 */
	private boolean canCalcInertiaTensor() {
		if (this.inertiaTensor != null)
			return false;
		if (this.getEnginePos() == null)
			return false;

		for (Wing wing : this.getWingArray()) {
			if (wing == null)
				return false;
		}

		return true;
	}

	/**
	 * Calculates the total mass of the drone
	 * @return the mass of the wings and the engine
	 * @author Martijn Sauwens
	 */
	public float getTotalMass(){
		float totalMass = this.getEngineMass();

		Wing[] wingArray = this.getWingArray();
		for(Wing wing: wingArray){
			totalMass += wing.getMass();
		}

		return totalMass;
	}


	/**
	 * A variable containing the autopilot loaded onto the drone
	 */
	private final AutoPilot AP;

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
	 * A variable that stores the Inertia tensor of the drone
	 */
	private SquareMatrix inertiaTensor;

	/**
	 * A variable containing the next image set for the autopilot
	 */
	private byte[] APImage;

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


	/**
	 * Variable that stores the configuration of the autopilot
	 */
	private AutopilotConfig autopilotConfig;

	/**
	 * Variable that stores the cube representing the drone
	 */
	private Cube droneCube;

	/**
	 * variables used for the configuration of the streams
	 */
	private static final int nbRows = 200;
	private static final int nbColumns = 200;
	private static final float Angleofview = (float) (4*Math.PI / 6);

	/**
	 * variable used for the max allowed error on the position between de drone and the cube
	 */
	private final static float maxPosDifference = 1E-6f;

	//---- START OF STREAM STUFF ---//
	/**
	 * Creates an AutopilotConfig file which contains all values from the Autopilot config interface
	 * @throws IOException
	 * @author Jonathan Craessaerts
	 */
	public void setupAutopilotConfig()throws IOException{
	    	DataOutputStream dataOutputStream =
	                new DataOutputStream(new FileOutputStream(dataStreamLocationConfig));


	        AutopilotConfig value = new AutopilotConfig() {
	            public float getGravity() { return Drone.GRAVITY; }
	            public float getWingX() { return  Math.abs(getRightWing().getRelativePosition().getxValue()); }
	            public float getTailSize() { return Math.abs(getHorizontalStab().getRelativePosition().getzValue()); }
	            public float getEngineMass() { return getEngineMass(); }
	            public float getWingMass() { return getLeftWing().getMass(); }
	            public float getTailMass() { return getLeftWing().getMass(); }
	            public float getMaxThrust() { return getMaxThrust(); }
	            public float getMaxAOA() { return getLeftWing().getAngleOfAttack(); }
	            public float getWingLiftSlope() { return getLeftWing().getLiftSlope(); }
	            public float getHorStabLiftSlope() { return getHorizontalStab().getLiftSlope(); }
	            public float getVerStabLiftSlope() { return getVerticalStab().getLiftSlope(); }
	            public float getHorizontalAngleOfView() { return getHorizontalAngleOfView(); }
	            public float getVerticalAngleOfView() { return getVerticalAngleOfView(); }
	            public int getNbColumns() { return nbColumns; }
	            public int getNbRows() { return nbRows; }
	        };
	        AutopilotConfigWriter.write(dataOutputStream, value);
	    	dataOutputStream.close();
	    }
		/**
		 * Creates an AutopilotInputs file which contains all values from the AutopilotInput interface
		 * @throws IOException
		 * @author Jonathan Craessaerts
		 */
	    public void setupAutopilotInputs()throws IOException{
	    	DataOutputStream dataOutputStream =
	                new DataOutputStream(new FileOutputStream(dataStreamLocationInputs));
	    	
	    	Vector pos = getPosition();
	    	Vector posOnWorld = droneOnWorld(pos);
	    	float x = posOnWorld.getxValue();
	    	float y = posOnWorld.getyValue();
	    	float z = posOnWorld.getzValue();
	    	
	    	float heading = getOrientation().getxValue();
	    	float pitch = getOrientation().getyValue();
	    	float roll = getOrientation().getzValue();
	    	
	    	
	    	AutopilotInputs value = new AutopilotInputs() {
	            public byte[] getImage() { 
					return getAPImage(); }
	            public float getX() { return x; }
	            public float getY() { return y; }
	            public float getZ() { return z; }
	            public float getHeading() { return heading; }
	            public float getPitch() { return pitch; }
	            public float getRoll() { return roll; }
	            public float getElapsedTime() { // TODO
	    				return (Float) null; }
	        };

	        AutopilotInputsWriter.write(dataOutputStream, value);
	        
	    	dataOutputStream.close();
	    }
	    /**
	     * Variable for the filename that's created when making the AutopilotConfig datastream 
	     */
	    private String dataStreamLocationConfig = "APConfig.txt";
	    
	    /**
	     * Variable for the filename that's created when making the AutopilotInputs datastream 
	     */
	    private String dataStreamLocationInputs = "APInputs.txt";

	// ---- END OF STREAM STUFF ---- //
	    
	/*
	 * Error Messages:
	 */

	private final static String THRUST_OUT_OF_RANGE = "The thrust is out of range: [0, this.maxThrust]";
	private final static String INCLINATION_OUT_OF_RANGE = "The inclination is out of range: [0, 2.PI[";
	private final static String VELOCITY_ERROR = "The velocity exceeds the upper limit";
	private final static String WING_EXCEPTION = "the wings are null references or the drone has already wings" +
			"attached to it";
	private final static String UNINITIALIZED_ENGINEMASS = "The mass of the engine is uninitialized";
	private final static String UNINITIALIZED_POINTMASS = "one or more of the point masses of the drone " +
			"have not been initialized yet";
	private final static String ILLEGAL_CONFIG = "The given configuration contains illegal values and or arguments";
	private final static String INVALID_TIMESTEP = "The provided time needs to be strictly positive";
	private final static String AUTOPILOT_CONFIG = "the autopilot has already been initialized";

	/*
	code graveyard:
	 */


	//------- Drone Controlling Methods -------
	/**
	 * @author anthonyrathe
	 */
	public void clockRollStart(){
		this.getLeftWing().setWingInclination(-this.getLeftWing().getMaximumAngleOfAttack());
		this.getRightWing().setWingInclination(this.getRightWing().getMaximumAngleOfAttack());
	}

	/**
	 * @author anthonyrathe
	 */
	public void counterClockRollStart(){
		this.getLeftWing().setWingInclination(this.getLeftWing().getMaximumAngleOfAttack());
		this.getRightWing().setWingInclination(-this.getRightWing().getMaximumAngleOfAttack());
	}

	/**
	 * @author anthonyrathe
	 */
	public void stopRoll(){
		this.getLeftWing().setWingInclination(0f);
		this.getRightWing().setWingInclination(0f);
	}

	/**
	 * @author anthonyrathe
	 */
	public void startTurnLeft(){
		this.getVerticalStab().setWingInclination(this.getVerticalStab().getMaximumAngleOfAttack());
	}

	/**
	 * @author anthonyrathe
	 */
	public void startTurnRight(){
		this.getVerticalStab().setWingInclination(-this.getVerticalStab().getMaximumAngleOfAttack());
	}

	/**
	 * @author anthonyrathe
	 */
	public void stopTurn(){
		this.getVerticalStab().setWingInclination(0f);
	}

	/**
	 * @author anthonyrathe
	 */
	public void startAscend(){
		this.getHorizontalStab().setWingInclination(this.getHorizontalStab().getMaximumAngleOfAttack());
	}

	/**
	 * @author anthonyrathe
	 */
	public void startDescend(){
		this.getHorizontalStab().setWingInclination(-this.getHorizontalStab().getMaximumAngleOfAttack());

	}

	/**
	 * @author anthonyrathe
	 */
	public void stopAscendDescend(){
		this.getHorizontalStab().setWingInclination(0f);
	}

//
//	/**
//	 * Changes the state of the drone to the next state, as calculated earlier by the autopilot.
//	 * State will remain the same if there is no queue-time.
//	 * @author anthonyrathe
//	 */
//	public void nextState() {
//		if (this.nextStateAvailable()) {
//			this.setThrust(this.getNextThrust());
//			this.getLeftWing().setWingInclination(this.getNextLeftWingInclination());
//			this.getRightWing().setWingInclination(this.getNextRightWingInclination());
//			this.getHorizontalStab().setWingInclination(this.getNextHorStabInclination());
//			this.getVerticalStab().setWingInclination(this.getNextVerStabInclination());
//		}
//	}
//
//	/**
//	 * A variable containing the amount of time (in seconds) the drone must wait before being able to call the autopilot
//	 */
//	private float queueTime;
//
//	/**
//	 * A getter that returns the current queueTime
//	 */
//	private float getQueueTime() {
//		return this.queueTime;
//	}
//
//	/**
//	 * A setter that sets the new queueTime
//	 *
//	 * @param newQueueTime the new amount of queue time (in seconds)
//	 * @author anthonyrathe
//	 */
//	private void setQueueTime(float newQueueTime) {
//		this.queueTime = newQueueTime;
//	}
//
//	/**
//	 * Method for telling whether or not a next state is available
//	 */
//	private boolean nextStateAvailable() {
//		return this.getQueueTime() > 0;
//	}
//
//	/**
//	 * A variable containing the new thrust of the drone, after queueTime has elapsed, as calculated by the autopilot
//	 */
//	private float nextThrust;
//
//	/**
//	 * A getter that returns the next amount of thrust
//	 */
//	private float getNextThrust() {
//		return this.nextThrust;
//	}
//
//	private void setNextThrust(float thrust){
//		this.nextThrust = thrust;
//	}
//
//	/**
//	 * A variable containing the current left wing inclination of the drone
//	 */
//	private float leftWingInclination;
//
//	/**
//	 * A variable containing the new left wing inclination of the drone, after queueTime has elapsed, as calculated by the autopilot
//	 */
//	private float nextLeftWingInclination;
//
//	/**
//	 * A getter that returns the next left wing inclination
//	 */
//	private float getNextLeftWingInclination() {
//		return this.nextLeftWingInclination;
//	}
//
//	private void setNextLeftWingInclination(float leftWingInclination){
//		this.nextLeftWingInclination = leftWingInclination;
//	}
//
//	/**
//	 * A variable containing the current right wing inclination of the drone
//	 */
//	private float rightWingInclination;
//
//	/**
//	 * A variable containing the new right wing inclination of the drone, after queueTime has elapsed, as calculated by the autopilot
//	 */
//	private float nextRightWingInclination;
//
//	/**
//	 * A getter that returns the next right wing inclination
//	 */
//	private float getNextRightWingInclination() {
//		return this.nextRightWingInclination;
//	}
//
//	private void setNextRightWingInclination(float rightWingInclination){
//		this.nextRightWingInclination = rightWingInclination;
//	}
//
//
//
//	/**
//	 * A variable containing the new horizontal stabilizers inclination of the drone, after queueTime has elapsed, as calculated by the autopilot
//	 */
//	private float nextHorStabInclination;
//
//	private void setNextHorStabInclination(float horStabInclination){
//		this.nextHorStabInclination = horStabInclination;
//	}
//
//	/**
//	 * A getter that returns the next horizontal stabilizer inclination
//	 */
//	private float getNextHorStabInclination() {
//		return this.nextHorStabInclination;
//	}
//
//	/**
//	 * A variable containing the new vertical stabilizers inclination of the drone, after queueTime has elapsed, as calculated by the autopilot
//	 */
//	private float nextVerStabInclination;
//
//	/**
//	 * A getter that returns the next vertical stabilizer inclination
//	 */
//	private float getNextVerStabInclination() {
//		return this.nextVerStabInclination;
//	}
//
//	private void setNextVerStabInclination(float verStabInclination){
//		this.nextVerStabInclination = verStabInclination;
//	}
//
//	/**
//	 * Moves the drone for a given amount of time, taking in account autopilot input.
//	 *
//	 * @param duration the amount of time the drone should be moved
//	 * @author anthonyrathe
//	 * @throws IOException
//	 */
//	public void evolve(float duration) throws IOException {
//		while (duration > 0) {
//			if (this.nextStateAvailable()) {
//				if (duration >= this.getQueueTime()) {
//					this.move(this.getQueueTime());
//					this.nextState();
//					duration = duration - this.getQueueTime();
//					this.setQueueTime((float) 0.0);
//				} else {
//					this.move(duration);
//					this.setQueueTime(getQueueTime() - duration);
//					duration = (float) 0.0;
//				}
//			} else {
//				AutopilotInputs input = updateAutopilotInput(duration);// TODO input stream
//				AutoPilot AP = this.getAutopilot();
//				AutopilotOutputs APO = AP.simulationStarted(autopilotConfig, input);
//				this.setThrust(APO.getThrust());
//				this.setLeftWingInclination(APO.getLeftWingInclination());
//				this.setRightWingInclination(APO.getRightWingInclination());
//				this.setHorStabInclination(APO.getHorStabInclination());
//				this.setNextVerStabInclination(APO.getVerStabInclination());
//				if (duration >= AP_CALC_TIME) {
//					duration = duration - AP_CALC_TIME;
//					this.move(AP_CALC_TIME);
//					this.nextState();
//				} else {
//					this.move(duration);
//					this.setQueueTime(AP_CALC_TIME - duration);
//					duration = (float) 0.0;
//				}
//			}
//		}
//	}



}
