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

/**
 * Created by Martijn on 14/10/2017.
 * Extended by Bart on 15/10/2017.
 * Extended by Anthony Rathé on 16/10/2017 and later
 */
public class AutoPilot implements Autopilot {

    public AutoPilot() {
    }

    @Override
    public AutopilotOutputs simulationStarted(AutopilotConfig config, AutopilotInputs inputs) throws IOException {
            configureAutopilot(config, inputs);
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

        this.configuredAP = true;

    }

    /**
     * @param inputs
     * @return Autopilotoutputs {Thrust, L/Rwinginclination, hor/verstabinclination}
     * @throws IOException
     */
    private AutopilotOutputs calculate(AutopilotInputs inputs) throws IOException {
        update(inputs);
        AutopilotOutputs output = new AutopilotOutputs() {


            @Override
            public float getThrust() {
                return getThrustOut();
            }

            @Override
            public float getLeftWingInclination() {
                return getLeftWingInclinationOut();
            }

            @Override
            public float getRightWingInclination() {
                return getRightWingInclinationOut();
            }

            @Override
            public float getHorStabInclination() {
                return getHorStabInclinationOut();
            }

            @Override
            public float getVerStabInclination() {
                return getVerStabInclinationOut();
            }
        };
        //DataOutputStream dataInputStream = new DataOutputStream(new FileOutputStream(dataStreamLocationOutputs));
        //AutopilotOutputsWriter.write(dataInputStream, output);
        return output;
    }

    /**
     * @throws IOException
     */
    public void setupAutopilotOutputs() throws IOException {
       // DataOutputStream dataOutputStream =
       //         new DataOutputStream(new FileOutputStream(dataStreamLocationOutputs));

        AutopilotOutputs value = new AutopilotOutputs() {
            public float getThrust() {
                return getThrustOut();
            }

            public float getLeftWingInclination() {
                return getLeftWingInclinationOut();
            }

            public float getRightWingInclination() {
                return getRightWingInclinationOut();
            }

            public float getHorStabInclination() {
                return getHorStabInclinationOut();
            }

            public float getVerStabInclination() {
                return getVerStabInclinationOut();
            }
        };

        //AutopilotOutputsWriter.write(dataOutputStream, value);

        //dataOutputStream.close();
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

    /**
     * checks if the supplied max thrust is valid
     * @param maxThrust the max thrust to be checked
     * @return true if and only if the maximum thrust > 0.0
     * @author Martijn Sauwens
     */
    public static boolean isValidMaxThrust(float maxThrust){
        return maxThrust > 0.0f;
    }

    /**
     * @return true if and only if the autopilot is configured
     * @author Martijn
     */
    public boolean isConfiguredAP() {
        return configuredAP;
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
    private float maxThrust;
    private boolean configuredAP = false;

    private List<Vector> currentPath;
    private AutoPilotCamera APCamera;

    //------- Simple Controlling Methods -------

    /**
     * @author anthonyrathe
     */
    private void clockRollStart() {
        this.setLeftWingInclinationOut((float) -STANDARD_INCLINATION);
        this.setRightWingInclinationOut((float) STANDARD_INCLINATION);
    }

    /**
     * @author anthonyrathe
     */
    private void counterClockRollStart() {
        this.setLeftWingInclinationOut((float) STANDARD_INCLINATION);
        this.setRightWingInclinationOut((float) -STANDARD_INCLINATION);
    }

    /**
     * @author anthonyrathe
     */
    private void stopRoll() {
        this.setLeftWingInclinationOut((float) 0);
        this.setRightWingInclinationOut((float) 0);
    }

    /**
     * @author anthonyrathe
     */
    private void startTurnLeft() {
        this.setVerStabInclinationOut((float) STANDARD_INCLINATION);
    }

    /**
     * @author anthonyrathe
     */
    private void startTurnRight() {
        this.setVerStabInclinationOut((float) -STANDARD_INCLINATION);
    }

    /**
     * @author anthonyrathe
     */
    private void stopTurn() {
        this.setVerStabInclinationOut(0f);
    }

    /**
     * @author anthonyrathe
     */
    private void startAscend() {
        this.setHorStabInclinationOut((float) STANDARD_INCLINATION);
    }

    /**
     * @author anthonyrathe
     */
    private void startDescend() {
        this.setHorStabInclinationOut((float) -STANDARD_INCLINATION);
    }

    /**
     * @author anthonyrathe
     */
    private void stopAscendDescend() {
        this.setHorStabInclinationOut((float) Math.PI / 12);
    }

    /**
     * @author anthonyrathe
     */
    private void thrustOn() {
        this.setThrustOut(maxThrust);
    }

    /**
     * @author anthonyrathe
     */
    private void thrustOff() {
        this.setThrustOut(0.0f);
    }

    //------- END Drone Controlling Methods -------

    //------- Pathfinding -------

    /**
     * @author anthonyrathe
     */
    private void updatePath(AutopilotInputs inputs) throws IOException {
        int[] start = this.getPosition(inputs).toIntArray();
        int[] end = this.getDestinationPosition().toIntArray();
        Pathfinding pathFinding = new Pathfinding(new World());
        List<int[]> pathInt = pathFinding.searchPath(start, end);
        List<Vector> newPath = new ArrayList<Vector>();
        for (int[] position : pathInt) {
            newPath.add(new Vector(position[0], position[1], position[2]));
        }
        this.currentPath = newPath;
    }

    /**
     * @return
     * @author anthonyrathe
     */
    private List<Vector> getPath() {
        return this.currentPath;
    }

    /**
     * @author anthonyrathe
     */
    private void removeNodeFromPath(Vector node) {
        this.currentPath.remove(node);
    }

    /**
     * @return
     * @author anthonyrathe
     */
    private Vector getPosition(AutopilotInputs inputs) {
        return new Vector(inputs.getX(), inputs.getY(), inputs.getZ());
    }

    /**
     * @author anthonyrathe
     */
    private AutoPilotCamera getAPCamera() throws NullPointerException {
        if (this.APCamera == null) {
            throw new NullPointerException("No APCamera was assigned to this AutoPilot");
        }
        return this.APCamera;
    }

    /**
     * @author anthonyrathe
     */
    public void setAPCamera(AutoPilotCamera newAPCamera) {
        this.APCamera = newAPCamera;
    }

    /**
     * @return
     * @author anthonyrathe
     */
    private Vector getDestinationPosition() {
        return getAPCamera().getDestination();
    }

    /**
     * @return
     * @author anthonyrathe
     */
    private World getWorld() {
        return getAPCamera().getWorld();
    }
    //------- END Pathfinding -------

    //------- Actual Autopilot -------

    /**
     * @return the node that currently is closest to the drone
     * @author anthonyrathe
     */
    private Vector getNextNode(AutopilotInputs inputs) throws IOException {
        updatePath(inputs);
        Vector destination = getDestinationPosition();
        Vector currentPosition = this.getPosition(inputs);
        Vector nextNode = destination;
        float smallestDistance = currentPosition.distanceBetween(getDestinationPosition());
        for (Vector node : this.getPath()) {
            if (node.distanceBetween(currentPosition) <= NODE_REACHED_DISTANCE) {
                this.removeNodeFromPath(node);
            } else if (node.distanceBetween(currentPosition) < smallestDistance
                    && node.distanceBetween(destination) <= currentPosition.distanceBetween(destination)) {
                smallestDistance = node.distanceBetween(currentPosition);
                nextNode = node;
            }
        }
        return nextNode;
    }

    /**
     * @param pitch the pitch of the drone
     * @param yaw the yaw of the drone
     * @param roll the roll of the drone
     * @return
     * @author anthonyrathe
     */
    private Vector pitchRollYawToWorld(float pitch, float roll, float yaw) {
        double z = -Math.cos((double) yaw) * Math.cos((double) pitch);
        double x = -Math.sin((double) yaw) * Math.cos((double) pitch);
        double y = Math.sin((double) pitch);
        return new Vector((float) x, (float) y, (float) z);
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

    /**
     * Method that updates the desired inclinations and thrust
     * Strategy applied:
     * - If destination is located on the upper half of the screen, start ascending. Start descending if located on the lower half of the screen.
     * - If destination is located on the right half of the screen, start rolling clockwise. Start rolling counterclockwise if located on the left half.
     *
     * @author anthonyrathe
     */
    public void update(AutopilotInputs inputs) throws IOException {

        APCamera.loadNextImage(inputs.getImage());
        float xPosition = APCamera.getDestination().getxValue();
        float yPosition = APCamera.getDestination().getyValue();

        // Ascend/Descend
        if (yPosition < -THRESHOLD_PIXELS) {
            // Descend
            this.startDescend();
        } else if (yPosition >= -THRESHOLD_PIXELS && yPosition <= THRESHOLD_PIXELS) {
            // Stop descending/ascending
            this.stopAscendDescend();
        } else if (yPosition > THRESHOLD_PIXELS) {
            // Ascend
            this.startAscend();
        }

        // Roll
        if (xPosition > THRESHOLD_PIXELS) {
            // Roll clockwise
            this.clockRollStart();
        } else if (xPosition >= -THRESHOLD_PIXELS && xPosition <= THRESHOLD_PIXELS) {
            // Stop rolling
            this.stopRoll();
        } else if (xPosition < -THRESHOLD_PIXELS) {
            // Roll counterclockwise
            this.counterClockRollStart();
        }

    }


    //------- END Actual Autopilot -------


    public float getThrustOut() {
        return thrust;
    }

    public void setThrustOut(float thrust) {
        if(!this.canHaveAsThrust(thrust))
            throw new IllegalArgumentException(INVALID_THRUST);
        this.thrust = thrust;
    }

    /**
     * Checks if the given thrust is valid
     * @param thrust the thrust to test
     * @return true if and only if the thrust is in bounds (0, maxThrust)
     * @aurhor Martijn Sauwens
     */
    public boolean canHaveAsThrust(float thrust){
        return thrust >= 0.0 && thrust <= this.getMaxThrust();
    }


    public float getLeftWingInclinationOut() {
        return leftWingInclination;
    }

    private void setLeftWingInclinationOut(float inclination) {
        leftWingInclination = inclination;
    }


    public float getRightWingInclinationOut() {
        return rightWingInclination;
    }

    private void setRightWingInclinationOut(float inclination) {
        rightWingInclination = inclination;
    }

    public float getHorStabInclinationOut() {
        return horStabInclination;
    }

    public void setHorStabInclinationOut(float inclination) {
        horStabInclination = inclination;
    }

    public float getVerStabInclinationOut() {
        return verStabInclination;
    }

    public void setVerStabInclinationOut(float inclination) {
        verStabInclination = inclination;
    }

    //------- Parameters -------
    private static final float STANDARD_INCLINATION = (float) Math.PI / 6;
    private static final float THRESHOLD_ANGLE = (float) Math.PI / 36;
    private static final float THRESHOLD_PIXELS = 5f;
    private static final float NODE_REACHED_DISTANCE = 4f;

    /*
    Error messages
     */
    public final static String INVALID_THRUST = "The supplied thrust is out of bounds";

}

