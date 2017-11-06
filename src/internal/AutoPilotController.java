package internal;

import Autopilot.Autopilot;
import Autopilot.AutopilotInputs;
import Autopilot.AutopilotOutputs;

import javax.naming.ldap.Control;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Created by Martijn on 30/10/2017.
 * A class of Autopilot Controllers
 */
public class AutoPilotController {

    /**
     * Constructor for the autopilotController
     * @param autoPilot
     */
    public AutoPilotController(AutoPilot autoPilot){
        this.associatedAutopilot = autoPilot;
    }

    private void setThrustOut(ControlOutputs outputs){
        //Todo implement: write the output to the outputs
        float pitch = this.getCurrentInputs().getPitch();
        float cubeSize =  this.getAssociatedAutopilot().getAPCamera().getTotalQualifiedPixels();
        int threshold = Math.round(THRESHOLD_DISTANCE);

        // Thrust
        float thrust = (float) ((STANDARD_THRUST) + this.getTotalMass()*GRAVITY*sin(Math.PI - pitch));
        //System.out.println(thrust);
        outputs.setThrust(thrust);
    }

    private void startDescend(ControlOutputs outputs, float xPosCube, float yPosCube){
        outputs.setHorStabInclination(STANDARD_INCLINATION);
    }

    private void startAscend(ControlOutputs outputs,  float xPosCube, float yPosCube){
        outputs.setHorStabInclination(-STANDARD_INCLINATION);
    }

    private void stopAscendDescend(ControlOutputs outputs,  float xPosCube, float yPosCube){
        outputs.setHorStabInclination(STABILIZER_STABLE_INCLINATION);
    }

    private void startTurnRight(ControlOutputs outputs,  float xPosCube, float yPosCube){
        //outputs.setVerStabInclination(STANDARD_INCLINATION);
    	if (yPosCube >= 0) {
    		startAscend(outputs, xPosCube, yPosCube);
    	}else {
    		startDescend(outputs, xPosCube, yPosCube);
    	}
    	
    	outputs.setRightWingInclination(TURNING_INCLINATION + MAIN_STABLE_INCLINATION);
        outputs.setLeftWingInclination(-TURNING_INCLINATION + MAIN_STABLE_INCLINATION);
    }

    private void startTurnLeft(ControlOutputs outputs, float xPosCube, float yPosCube){
        //outputs.setVerStabInclination(-STANDARD_INCLINATION);
    	if (yPosCube >= 0) {
    		startAscend(outputs, xPosCube, yPosCube);
    	}else {
    		startDescend(outputs, xPosCube, yPosCube);
    	}
    	
    	startAscend(outputs, xPosCube, yPosCube);
        outputs.setRightWingInclination(-TURNING_INCLINATION + MAIN_STABLE_INCLINATION);
        outputs.setLeftWingInclination(TURNING_INCLINATION + MAIN_STABLE_INCLINATION);
    }

    private void stopTurn(ControlOutputs outputs, float xPosCube, float yPosCube){
        outputs.setVerStabInclination(STABILIZER_STABLE_INCLINATION);
        outputs.setRightWingInclination(MAIN_STABLE_INCLINATION);
        outputs.setLeftWingInclination(MAIN_STABLE_INCLINATION);
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
        APCamera.loadNextImage(currentInputs.getImage());
        float xPosition = APCamera.getDestination().getxValue();
        float yPosition = -APCamera.getDestination().getyValue();

        int cubeSize = APCamera.getTotalQualifiedPixels();
        //System.out.println(cubeSize);

        //int threshold = Math.max(Math.round(THRESHOLD_PIXELS*NORMAL_CUBE_SIZE/cubeSize),1);
        int threshold = Math.round(THRESHOLD_DISTANCE);
        int bias = 0;
        if (currentInputs.getPitch() > Math.PI/20) {
        	bias = 10;
        }else if(currentInputs.getPitch() < -Math.PI/20) {
        	bias = -10;
        }
        System.out.println(bias);

        // Thrust
       this.setThrustOut(controlOutputs);


        // Ascend/Descend
        if(yPosition < -threshold - bias){
            // Descend
            //System.out.println("This is your captain speaking: the red cube is located underneath us");
            this.startDescend(controlOutputs, xPosition, yPosition);
        }else if(yPosition >= -threshold - bias && yPosition <= threshold - bias){
            // Stop descending/ascending
            this.stopAscendDescend(controlOutputs, xPosition, yPosition);
        }else if(yPosition > threshold - bias){
            // Ascend
            //System.out.println("This is your captain speaking: the red cube is located above us");
            this.startAscend(controlOutputs, xPosition, yPosition);
        }

        // Roll
        if(xPosition > threshold){
            // Turn right
            //System.out.println("This is your captain speaking: the red cube is located at our right-hand-side");
            this.startTurnRight(controlOutputs, xPosition, yPosition);
        }else if(xPosition >= -threshold && xPosition <= threshold){
            // Stop turning
            this.stopTurn(controlOutputs, xPosition, yPosition);
        }else if(xPosition < -threshold){
            // Turn left
            //System.out.println("This is your captain speaking: the red cube is located at our left-hand-side");
            this.startTurnLeft(controlOutputs, xPosition, yPosition);
        }

        this.rollControl(controlOutputs);

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

    /*
    Getters and setters
     */


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

    /**
     * Setter for the current inputs of the drone, the old currentInputs are automatically
     * set previous inputs
     * @param currentInputs the current input for the autopilot
     */
    public void setCurrentInputs(AutopilotInputs currentInputs) {
        //first write to the previous outputs
        this.previousInputs = this.getCurrentInputs();
        //then write to the new ones.
        this.currentInputs = currentInputs;
    }

    public AutopilotInputs getPreviousInputs() {
        return previousInputs;
    }

    private AutoPilot associatedAutopilot;
    private AutopilotInputs currentInputs;
    private AutopilotInputs previousInputs;

    private static final float STANDARD_INCLINATION = (float)Math.PI/8;
    private static final float MAIN_STABLE_INCLINATION = (float)Math.PI/12;
    private static final float TURNING_INCLINATION = (float)Math.PI/10;
    private static final float THRESHOLD_DISTANCE = 5f;
    private static final float STANDARD_THRUST = 32.859283f;
    private static final float THRESHOLD_THRUST_ANGLE = (float)(Math.PI/20);
    private static final float STANDARD_CUBE_SIZE = 10f;
    private static final float STABILIZER_STABLE_INCLINATION = 0.0f;
    private static final float GRAVITY = 9.81f;
    private static final float ROLL_THESHOLD = (float) (Math.PI * 3.0f/180.0f);

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

    }
}