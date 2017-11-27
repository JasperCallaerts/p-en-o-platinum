package internal;

import Autopilot.Autopilot;
import Autopilot.AutopilotInputs;
import Autopilot.AutopilotOutputs;

import javax.naming.ldap.Control;

import java.awt.datatransfer.FlavorListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.*;

/**
 * Created by Martijn on 30/10/2017.
 * Appended and edited by Anthony Rath� on 6/11/2017
 * A class of Autopilot Controllers
 */
public class AutoPilotController {

    /**
     * Constructor for the autopilotController
     * @param autoPilot
     */
    public AutoPilotController(AutoPilot autoPilot){
        this.associatedAutopilot = autoPilot;
        this.setPreviousInputs(dummyData);
        this.currentInputs = dummyData;
    }

    private void setThrustOut(ControlOutputs outputs){
        //Todo implement: write the output to the outputs
        float pitch = this.getCurrentInputs().getPitch();
        float cubeSize =  this.getAssociatedAutopilot().getAPCamera().getTotalQualifiedPixels();
        int threshold = Math.round(THRESHOLD_DISTANCE);

        // Thrust
        float thrust = (float) ((STANDARD_THRUST) + THRUST_FACTOR*this.getTotalMass()*GRAVITY*sin(PI - pitch));
        //System.out.println(thrust);
        outputs.setThrust(Math.max(Math.min(thrust, MAXTHRUST), 0));
    }

    private void startDescend(ControlOutputs outputs, float xPosCube, float yPosCube){
        PhysXEngine.PhysXOptimisations optimisations = this.getAssociatedAutopilot().getPhysXOptimisations();
        AutopilotInputs inputs = this.getCurrentInputs();
        Vector orientation = extractOrientation(inputs);
        Vector rotation = this.getRotationApprox();
        Vector velocity = this.getVelocityApprox();
        float angleOfAttack = this.getAssociatedAutopilot().getConfig().getMaxAOA();

        float inclination1 = optimisations.getMaxHorStabInclination(orientation, rotation, velocity, angleOfAttack);
        float inclination2 = optimisations.getMaxHorStabInclination(orientation, rotation, velocity, -angleOfAttack);

        float inclination = max(inclination1, inclination2);

        outputs.setHorStabInclination(Math.min(STANDARD_INCLINATION*Math.abs(yPosCube)/20f,inclination - ERROR_INCLINATION_MARGIN));

        if(this.getFlightRecorder() != null) {
            this.getFlightRecorder().appendControlLog("Starting descend: " + outputs.getHorStabInclination() * RAD2DEGREE +
            "; Possible Inclinations: "  + inclination1*RAD2DEGREE + "; " + inclination2*RAD2DEGREE  + "; Approx velocity: " + velocity + "; Approx rotation : " + rotation);
            this.getFlightRecorder().appendVelApproxLog(velocity);
            this.getFlightRecorder().appendRotApproxLog(rotation);
        }

        System.out.println("Descending");
    }

    private void startAscend(ControlOutputs outputs,  float xPosCube, float yPosCube){

        PhysXEngine.PhysXOptimisations optimisations = this.getAssociatedAutopilot().getPhysXOptimisations();
        AutopilotInputs inputs = this.getCurrentInputs();
        Vector orientation = extractOrientation(inputs);
        Vector rotation = this.getRotationApprox();
        Vector velocity = this.getVelocityApprox();
        float angleOfAttack = this.getAssociatedAutopilot().getConfig().getMaxAOA();

        float inclination1 = optimisations.getMaxHorStabInclination(orientation, rotation, velocity, angleOfAttack);
        float inclination2 = optimisations.getMaxHorStabInclination(orientation, rotation, velocity, -angleOfAttack);


        float inclination = max(inclination1, inclination2);

        outputs.setHorStabInclination(-Math.min(STANDARD_INCLINATION*Math.abs(yPosCube)/20f, abs(inclination) - ERROR_INCLINATION_MARGIN));
        if(this.getFlightRecorder() != null) {
            this.getFlightRecorder().appendControlLog("Starting Ascend: " + outputs.getHorStabInclination() * RAD2DEGREE
            + "; Possible Inclinations: " + inclination1*RAD2DEGREE + "; " + inclination2*RAD2DEGREE + "; Approx velocity: " + velocity + "; Approx rotation : " + rotation);
            this.getFlightRecorder().appendVelApproxLog(velocity);
            this.getFlightRecorder().appendRotApproxLog(rotation);
        }
    }

    private void startTurnDescend(ControlOutputs outputs, float xPosCube, float yPosCube){
        PhysXEngine.PhysXOptimisations optimisations = this.getAssociatedAutopilot().getPhysXOptimisations();
        AutopilotInputs inputs = this.getCurrentInputs();
        Vector orientation = extractOrientation(inputs);
        Vector rotation = this.getRotationApprox();
        Vector velocity = this.getVelocityApprox();
        float angleOfAttack = this.getAssociatedAutopilot().getConfig().getMaxAOA();

        float inclination1= optimisations.getMaxHorStabInclination(orientation, rotation, velocity, angleOfAttack);
        float inclination2 = optimisations.getMaxHorStabInclination(orientation, rotation, velocity, -angleOfAttack);

        float inclination = max(inclination1, inclination2);

        outputs.setHorStabInclination(Math.min(STANDARD_INCLINATION*Math.abs(yPosCube)/30f, abs(inclination) - ERROR_INCLINATION_MARGIN));
    }

    private void startTurnAscend(ControlOutputs outputs,  float xPosCube, float yPosCube){
        PhysXEngine.PhysXOptimisations optimisations = this.getAssociatedAutopilot().getPhysXOptimisations();
        AutopilotInputs inputs = this.getCurrentInputs();
        Vector orientation = extractOrientation(inputs);
        Vector rotation = this.getRotationApprox();
        Vector velocity = this.getVelocityApprox();
        float angleOfAttack = this.getAssociatedAutopilot().getConfig().getMaxAOA();

        float inclination1 = optimisations.getMaxHorStabInclination(orientation, rotation, velocity, angleOfAttack);
        float inclination2 = optimisations.getMaxHorStabInclination(orientation, rotation, velocity, -angleOfAttack);

        float inclination = min(inclination1, inclination2);

        outputs.setHorStabInclination(-Math.min(STANDARD_INCLINATION*Math.abs(yPosCube)/30f, Math.abs(inclination) - ERROR_INCLINATION_MARGIN));
    }

    private void stopAscendDescend(ControlOutputs outputs,  float xPosCube, float yPosCube){
        outputs.setHorStabInclination(STABILIZER_STABLE_INCLINATION);
        if(this.getFlightRecorder() != null) {
            this.getFlightRecorder().appendControlLog("Stopping Ascending or Descending");
        }
    }

    private void startTurnRight(ControlOutputs outputs,  float xPosCube, float yPosCube){
        //outputs.setVerStabInclination(STANDARD_INCLINATION);
    	/*if (yPosCube >= 0) {
    		startTurnAscend(outputs, xPosCube, yPosCube);

    	}else {
    		startTurnDescend(outputs, xPosCube, yPosCube);

    	}*/
        PhysXEngine.PhysXOptimisations optimisations = this.getAssociatedAutopilot().getPhysXOptimisations();
        AutopilotInputs inputs = this.getCurrentInputs();
        Vector orientation = extractOrientation(inputs);
        Vector rotation = this.getRotationApprox();
        Vector velocity = this.getVelocityApprox();
        float angleOfAttack = this.getAssociatedAutopilot().getConfig().getMaxAOA();

        float inclination1 = optimisations.getMaxVerStabInclination(orientation, rotation, velocity, angleOfAttack);
        float inclination2 = optimisations.getMaxVerStabInclination(orientation, rotation, velocity, -angleOfAttack);

        float inclination = min(inclination1, inclination2);

        float minIncl = min(STANDARD_INCLINATION, abs(inclination) -  ERROR_INCLINATION_MARGIN);

        outputs.setVerStabInclination(minIncl);

        if(this.getFlightRecorder() != null){
            this.getFlightRecorder().appendControlLog("Turn Right, Vertical Stabilizer: " + outputs.getVerStabInclination()*RAD2DEGREE +
            "; Possible inclinations: " + inclination1*RAD2DEGREE + "; " + inclination2*RAD2DEGREE  + "; Approx velocity: " + velocity + "; Approx rotation : " + rotation);
                    //", Horizontal Stabilizer: " + outputs.getHorStabInclination()*RAD2DEGREE);
            this.getFlightRecorder().appendVelApproxLog(velocity);
            this.getFlightRecorder().appendRotApproxLog(rotation);
        }

    }

    private void startTurnLeft(ControlOutputs outputs, float xPosCube, float yPosCube){
        //outputs.setVerStabInclination(-STANDARD_INCLINATION);
    	/*if (yPosCube >= 0) {
    		startTurnAscend(outputs, xPosCube, yPosCube);

    	}else {
    		startTurnDescend(outputs, xPosCube, yPosCube);

    	}*/

        PhysXEngine.PhysXOptimisations optimisations = this.getAssociatedAutopilot().getPhysXOptimisations();
        AutopilotInputs inputs = this.getCurrentInputs();
        Vector orientation = extractOrientation(inputs);
        Vector rotation = this.getRotationApprox();
        Vector velocity = this.getVelocityApprox();

        float angleOfAttack = this.getAssociatedAutopilot().getConfig().getMaxAOA();

        // negative because we need a negative result (maybe update to MinNeg)
        float inclination1 = optimisations.getMaxVerStabInclination(orientation, rotation, velocity, angleOfAttack);
        float inclination2 = optimisations.getMaxVerStabInclination(orientation, rotation, velocity, -angleOfAttack);

        float inclination = min(inclination1, inclination2);

        float minIncl = -min(STANDARD_INCLINATION, Math.abs(inclination) - ERROR_INCLINATION_MARGIN);

        outputs.setVerStabInclination(minIncl);

        if(this.getFlightRecorder() != null){
            this.getFlightRecorder().appendControlLog("Turn Left, Vertical Stabilizer: " + outputs.getVerStabInclination()*RAD2DEGREE +
                    "; Possible Inclinations: " + inclination1*RAD2DEGREE + "; " + inclination2*RAD2DEGREE + "; Approx velocity: " + velocity + "; Approx rotation : " + rotation);
            this.getFlightRecorder().appendVelApproxLog(velocity);
            this.getFlightRecorder().appendRotApproxLog(rotation);
        }

    }

    private void stopTurn(ControlOutputs outputs, float xPosCube, float yPosCube){
        outputs.setVerStabInclination(STABILIZER_STABLE_INCLINATION);
        outputs.setRightWingInclination(MAIN_STABLE_INCLINATION);
        outputs.setLeftWingInclination(MAIN_STABLE_INCLINATION);
        //stopAscendDescend(outputs, xPosCube, yPosCube);
        //System.out.println("Turn NOT descending nor ascending");

        if(this.getFlightRecorder() != null){
            this.getFlightRecorder().appendControlLog("No Turn");
        }

    }

    private void rollControl(ControlOutputs outputs){
        AutopilotInputs input = this.getCurrentInputs();
        float roll = input.getRoll();

        if(roll >= ROLL_THESHOLD){
            outputs.setRightWingInclination(-MAIN_STABLE_INCLINATION);
        }
        else if(roll <= - ROLL_THESHOLD){
            outputs.setLeftWingInclination(-MAIN_STABLE_INCLINATION);
        }else{
            // change nothing
        }
    }

    /**
     * Generates the appropriate control actions for the drone
     * @return the outputs for the autopilot
     */
    public AutopilotOutputs getControlActions(){

        ControlOutputs controlOutputs = new ControlOutputs();
        AutoPilotCamera APCamera = this.getAssociatedAutopilot().getAPCamera();
        AutopilotInputs currentInputs = this.getCurrentInputs();
        //APCamera.loadNextImage(currentInputs.getImage());
        APCamera.loadNewImage(currentInputs.getImage());
        
        Vector center = APCamera.getCenterOfNCubes(1).scalarMult(2f).vectorSum(APCamera.getCenterOfNCubes(5).scalarMult(1f)).scalarMult(3f);
//        Vector center = APCamera.getCenterOfNCubes(1);
//        Vector center = APCamera.getCenterOfNCubes(1);
        float xPosition = center.getxValue();
        float yPosition = -center.getyValue();

        //System.out.println("Center location: " + center);

        int cubeSize = Math.round(center.getzValue());
        //System.out.println(cubeSize);

        //int threshold = Math.max(Math.round(THRESHOLD_PIXELS*NORMAL_CUBE_SIZE/cubeSize),1);
        int threshold = Math.round(THRESHOLD_DISTANCE);
        int bias = 0;
        if (currentInputs.getPitch() > PI/20) {
        	bias = BIAS;
        }else if(currentInputs.getPitch() < -PI/20) {
        	bias = -BIAS;
        }
        //System.out.println(bias);

        // Thrust
       this.setThrustOut(controlOutputs);


        // Roll
        if(xPosition > threshold){
            // Turn right
            //System.out.println("This is your captain speaking: the red cube is located at our right-hand-side");
            this.startTurnRight(controlOutputs, xPosition, yPosition);
        }else if(xPosition >= -threshold && xPosition <= threshold){
            // Stop turning
            this.stopTurn(controlOutputs, xPosition, yPosition);

            // Start Ascending/Descending
            // Ascend/Descend
            if(yPosition < -threshold - bias && (xPosition >= -threshold && xPosition <= threshold)){
                // Descend
                //System.out.println("This is your captain speaking: the red cube is located underneath us");
                this.startDescend(controlOutputs, xPosition, yPosition);

            }else if((yPosition >= -threshold - bias && yPosition <= threshold - bias) && (xPosition >= -threshold && xPosition <= threshold)){
                // Stop descending/ascending
                this.stopAscendDescend(controlOutputs, xPosition, yPosition);

            }else if(yPosition > threshold - bias && (xPosition >= -threshold && xPosition <= threshold)){
                // Ascend
                //System.out.println("This is your captain speaking: the red cube is located above us");
                this.startAscend(controlOutputs, xPosition, yPosition);

            }
        }else if(xPosition < -threshold){
            // Turn left
            //System.out.println("This is your captain speaking: the red cube is located at our left-hand-side");
            this.startTurnLeft(controlOutputs, xPosition, yPosition);
        }

        this.rollControl(controlOutputs);

        //System.out.println("Controls delivered");
        //System.out.println(controlOutputs);

        return controlOutputs;
    }
    /*
    Helper methods
     */
    private float getTotalMass(){
        AutoPilot autopilot = this.getAssociatedAutopilot();
        float mainWings = autopilot.getMainWingMass()*2;
        float stabilizers = autopilot.getStabilizerMass()*2;
        float engine = autopilot.getEngineMass();

        return mainWings + stabilizers + engine;
    }

    /**
     * Calculate an approximation of the velocity
     * @return the approximation of the velocity
     * elaboration: see textbook numerical math for derivative methods, the
     * derivative of f(k+1) - f(k-1) / (2*timeStep) has O(h²) correctness
     */
    private Vector getVelocityApprox(){
        //get the inputs at moment k - 1 for the derivative
        AutopilotInputs prevInputs = this.getPreviousInputs();
        //get the inputs at moment k
        AutopilotInputs currentInputs = this.getCurrentInputs();
        float prevTime = prevInputs.getElapsedTime();
        float currentTime = currentInputs.getElapsedTime();

        Vector prevPos = extractPosition(prevInputs);
        Vector currentPos = extractPosition(currentInputs);

        Vector posDiff = currentPos.vectorDifference(prevPos);
        float timeDiff = currentTime - prevTime;

        return posDiff.scalarMult(1/timeDiff);
    }

    private Vector getRotationApprox(){
        AutopilotInputs prevInputs = this.getPreviousInputs();
        //get the inputs at moment k
        AutopilotInputs currentInputs = this.getCurrentInputs();

        float prevTime = prevInputs.getElapsedTime();
        float currentTime = currentInputs.getElapsedTime();

        Vector prevOrient = extractOrientation(prevInputs);
        Vector currentOrient = extractOrientation(currentInputs);

        Vector orientDiff = currentOrient.vectorDifference(prevOrient);
        float timeDiff = currentTime - prevTime;

        // the given rotation vector is given in heading pitch and roll components
        Vector rotationHPR = orientDiff.scalarMult(1/timeDiff);
        // convert back to the world axis rotation vector
        return PhysXEngine.HPRtoRotation(rotationHPR, currentOrient);
    }

    /**
     * determines the largest value of both entries, if both are negative an exception is thrown
     * @param entry1 the first entry to check
     * @param entry2 the second entry to check
     * @return the largest entry of both
     * @throws IllegalArgumentException thrown if both entries are negative
     */
    private float getMaxPos(float entry1, float entry2) throws IllegalArgumentException{
        if(entry1 >= entry2 && entry1 >= 0){
            return entry1;
        }else if(entry2 >= 0){
            return entry2;
        }else{
            return 0.0f;
        }
    }

    /**
     * determines the smallest value of both entries, if both are positive an exception is thrown
     * @param entry1 the first entry to check
     * @param entry2 the second entry to check
     * @return the smallest entry of both
     * @throws IllegalArgumentException thrown if both entries are positive
     */
    private float getMinNeg(float entry1, float entry2) throws IllegalArgumentException{
        if(entry1 <= entry2 && entry1 <= 0){
            return entry1;
        }else if(entry2 <= 0){
            return entry2;
        }else{
            return 0.0f;
        }
    }

    /*
    Getters and setters
     */

    /**
     * Extractor of the orientation in vector format
     * @param inputs the autopilotInput object containing the current inputs
     * @return a vector containing the orientation of the drone in vector format
     */
    private static Vector extractOrientation(AutopilotInputs inputs){
        return new Vector(inputs.getHeading(), inputs.getPitch(), inputs.getRoll());
    }

    /**
     * Extractor of the orientation in vector format
     * @param inputs the autopilotInput object containing the current inputs
     * @return a vector containing the position of the drone in vector format
     */
    private static Vector extractPosition(AutopilotInputs inputs){
        return new Vector(inputs.getX(), inputs.getY(), inputs.getZ());
    }

    /**
     * getter for the associated Autopilot
     * @return the associated autopilot
     */
    public AutoPilot getAssociatedAutopilot() {
        return associatedAutopilot;
    }

    public AutopilotInputs getCurrentInputs() {
        return currentInputs;
    }

    public FlightRecorder getFlightRecorder() {
        return flightRecorder;
    }

    public void setFlightRecorder(FlightRecorder flightRecorder) {
        this.flightRecorder = flightRecorder;
    }

    /**
     * Setter for the current inputs of the drone, the old currentInputs are automatically
     * set previous inputs
     * @param currentInputs the current input for the autopilot
     */
    public void setCurrentInputs(AutopilotInputs currentInputs) {
        //first write to the previous outputs
        this.setPreviousInputs(this.getCurrentInputs());

        //then write to the new ones.
        this.currentInputs = currentInputs;
    }

    /**
     * Returns the previous Autopilot Inputs
     */
    public AutopilotInputs getPreviousInputs() {
        return previousInputs;
    }

    /**
     * Setter for the autopilot inputs
     * @param previousInputs the pervious inputs
     */
    private void setPreviousInputs(AutopilotInputs previousInputs){
        this.previousInputs = previousInputs;
    }

    private AutoPilot associatedAutopilot;
    private AutopilotInputs currentInputs;
    private AutopilotInputs previousInputs;
    private FlightRecorder flightRecorder;
    private final static int NB_OF_PREV_INPUTS = 2;

    private static final float STANDARD_INCLINATION = (float) PI/8;
    public static final float MAIN_STABLE_INCLINATION = (float) PI/12;
    private static final float MAX_HOR_STAB_INCLINATION = (float) PI/4;
    private static final float TURNING_INCLINATION = (float) PI/8;
    private static final float ERROR_INCLINATION_MARGIN = (float) (5*PI/180);
    private static final int BIAS = 0;
    private static final float THRESHOLD_DISTANCE = 5f;
    private static final float STANDARD_THRUST = 32.859283f*2;
    private static final float THRUST_FACTOR = 2.0f;
    private static final float THRESHOLD_THRUST_ANGLE = (float)(PI/20);
    private static final float STANDARD_CUBE_SIZE = 10f;
    public static final float STABILIZER_STABLE_INCLINATION = 0.0f;
    private static final float GRAVITY = 9.81f;
    private static final float ROLL_THESHOLD = (float) (PI * 3.0f/180.0f);
    private static final float MAXTHRUST = 250.0f;
    private static final float RAD2DEGREE = (float) (180f/ PI);

    /*
    Error messages
     */
    private final static String NO_POS_MAX = "No positive maximum found";
    private final static String NO_NEG_MIN = "No negative minimum found";

    private  static AutopilotInputs dummyData = new AutopilotInputs() {
        @Override
        public byte[] getImage() {
            return new byte[0];
        }

        @Override
        public float getX() {
            return 0;
        }

        @Override
        public float getY() {
            return 0;
        }

        @Override
        public float getZ() {
            return 0;
        }

        @Override
        public float getHeading() {
            return 0;
        }

        @Override
        public float getPitch() {
            return 0;
        }

        @Override
        public float getRoll() {
            return 0;
        }

        @Override
        public float getElapsedTime() {
            return 0;
        }
    };


    private class ControlOutputs implements AutopilotOutputs{

        public ControlOutputs(){
            //do nothing, everything stays initialized on zero
        }

        @Override
        public float getThrust() {
            return this.thrust;
        }

        @Override
        public float getLeftWingInclination() {
            return this.leftWingInclination;
        }

        @Override
        public float getRightWingInclination() {
            return this.rightWingInclination;
        }

        @Override
        public float getHorStabInclination() {
            return this.horStabInclination;
        }

        @Override
        public float getVerStabInclination() {
            return this.verStabInclination;
        }

        /**
         * Setter for the Thrust
         * @param thrust the desired thrust
         */
        public void setThrust(float thrust) {
            this.thrust = thrust;
        }

        /**
         * Setter for the left wing inclination
         * @param leftWingInclination
         */
        public void setLeftWingInclination(float leftWingInclination) {
            this.leftWingInclination = leftWingInclination;
        }

        /**
         * Setter for the right wing inclination
         * @param rightWingInclination the desired right wing inclination
         */
        public void setRightWingInclination(float rightWingInclination) {
            this.rightWingInclination = rightWingInclination;
        }

        /**
         * Setter for the horizontal stabilizer inclination
         * @param horStabInclination the desired horizontal stabilizer inclination
         */
        public void setHorStabInclination(float horStabInclination) {
            this.horStabInclination = horStabInclination;
        }

        /**
         * Setter for the vertical stabilizer inclination
         * @param verStabInclination the desired vertical stabilizer inclination
         */
        public void setVerStabInclination(float verStabInclination) {
            this.verStabInclination = verStabInclination;
        }

        //initialize the writes to the stable state of the drone
        private float thrust = STANDARD_THRUST;
        private float leftWingInclination = MAIN_STABLE_INCLINATION;
        private float rightWingInclination = MAIN_STABLE_INCLINATION;
        private float horStabInclination = STABILIZER_STABLE_INCLINATION;
        private float verStabInclination = STABILIZER_STABLE_INCLINATION;

        @Override
        public String toString() {
            return "ControlOutputs{" +
                    "thrust=" + thrust +
                    ", leftWingInclination=" + leftWingInclination +
                    ", rightWingInclination=" + rightWingInclination +
                    ", horStabInclination=" + horStabInclination +
                    ", verStabInclination=" + verStabInclination +
                    '}';
        }
    }

}
