import Autopilot.*;
import gui.*;
import internal.*;
import math.Vector3f;



import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;


/**
 * Created by Martijn on 13/11/2017.
 * supervised by Bart
 */
public class TestbedMain implements Runnable{

    /**
     * Initialize the main method for the testbed
     * see old mainloop for more info
     */
    public TestbedMain(String connectionName, int connectionPort, boolean showAllWidows) {
        this.setConnectionName(connectionName);
        this.setConnectionPort(connectionPort);
        this.showAllWindows = showAllWidows;
        //this.setQueue(queue);
    }

    /**
     * Initialize the testbed
     * Testbed is not initialized in the constructor because graphics need to be created
     * within one thread of control
     */
    private void initTestbed() throws IOException {

        // initialize graphics capabilities
        this.setGraphics(new Graphics());

        // Cube needs graphics to be able to initialize cubes
        Cube.setGraphics(this.getGraphics());

        this.setDroneCam(new Window(200, 200, 0.5f, 0.4f, "bytestream window", new Vector3f(1.0f, 1.0f, 1.0f), false));
        
        if(this.getShowAllWindows()) {
        	this.setDroneView(new Window(960, 510, 0.0f, 0.05f, "Drone view", new Vector3f(1.0f, 1.0f, 1.0f), true));
            this.setTopDownView(new Window(960, 510, 1f, 0.05f, "Top down view", new Vector3f(1.0f, 1.0f, 1.0f), true));
            this.setSideView(new Window(960, 510, 1f, 1f, "Side view", new Vector3f(1.0f, 1.0f, 1.0f), true));
            this.setChaseView(new Window(960, 510, 0f, 1f, "Chase view", new Vector3f(1.0f, 1.0f, 1.0f), true));
        } else {
        	this.setDroneView(new Window(1920, 1080, 0.5f, 1f, "Drone view", new Vector3f(1.0f, 1.0f, 1.0f), true));
        }
        	

//		Window independentView = new Window(960, 1000, 1f, 0.4f, "Independent camera", new Vector3f(0.5f, 0.8f, 1.0f), true);
//		Window textWindow = new Window(500, 500, 0.5f, 0.5f, "text window", new Vector3f(0.0f, 0.0f, 0.0f), true); // Not implemented yet

        // add the windows to graphics
        this.getGraphics().addWindow("bytestream window", droneCam);
        this.getGraphics().addWindow("Drone view", droneView);
        //only needed for demo
        if(this.getShowAllWindows()) {
            this.getGraphics().addWindow("Top down view", topDownView);
            this.getGraphics().addWindow("Side view", sideView);
            this.getGraphics().addWindow("Chase view", chaseView);
        } else {
        	
        }


        // drone builder covers all the stuff involving building the drone, adjust parameters there
        WorldBuilder worldBuilder = new WorldBuilder();
        this.setDrone(worldBuilder.DRONE);
        this.setWorld(worldBuilder.createWorld());//.createSimpleWorld();
        this.getDrone().addFlightRecorder(this.getFlightRecorder());

        // Initialize the windows
        droneCam.initWindow(world, Settings.DRONE_CAM);
        droneView.initWindow(world, Settings.DRONE_CAM);
        if(this.getShowAllWindows()) {
            topDownView.initWindow(world, Settings.DRONE_TOP_DOWN_CAM);
            chaseView.initWindow(world, Settings.DRONE_CHASE_CAM);
            sideView.initWindow(world, Settings.DRONE_SIDE_CAM);
        }
//      textWindow.initTextWindow(droneCam);


        Time.initTime();
    }


    //Todo configure the autopilot in the autopilot main
    //open seperate threads for both mainloops, where the first one waits for the other one.
    public void testbedMainLoop() throws IOException, InterruptedException {

        this.initTestbed();
        int step = 1;
        ServerSocket testbedServer = new ServerSocket(this.getConnectionPort());
        Socket testbedClientSocket = testbedServer.accept();
        DataInputStream inputStream = new DataInputStream(testbedClientSocket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(testbedClientSocket.getOutputStream());
        //write the configuration to the autopilot
        configAutopilot(outputStream, step);

        while(true) {
            //wait until the input is not null and advance if possible
            try {
                step ++;
                AutopilotOutputs output = AutopilotOutputsReader.read(inputStream);
                AutopilotInputsWriter.write(outputStream, this.testbedStep(output, step));
            } catch (NullPointerException e) {
                //let the null pointer fly
                System.out.println("Catching Exception: waiting for autopilot output");
            } catch(java.io.EOFException | SimulationEndedException ex ){
                //this exception means that the client socket has closed, close own sockets
                System.out.println("Closing down Testbed Server");
                inputStream.close();
                outputStream.close();
                testbedClientSocket.close();
                testbedServer.close();
                break;

            } catch(AngleOfAttackException ex){
                //angle of attack exception has occurred close down the server
                System.out.println("Closing down Testbed Server");
                inputStream.close();
                outputStream.close();
                testbedClientSocket.close();
                testbedServer.close();
                throw new AngleOfAttackException(ex.getCauseWing());
            }
        }
    }

    /**
     * Run method used for multiThreading purposes
     */
    @Override
    public void run() {
        try {
            this.testbedMainLoop();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write the configuration data to the autopilot
     * @param outputStream the stream containing the config and the input for the autopilot
     * @throws IOException
     * @throws InterruptedException
     */
    private void configAutopilot(DataOutputStream outputStream, int step) throws IOException, InterruptedException {
        AutopilotConfigWriter.write(outputStream, this.getConfig());
        //instert a null pointer, will be ignored on the first step of the testbedstep method
        AutopilotInputs autopilotInputs = this.testbedStep(null, step);
        AutopilotInputsWriter.write(outputStream, autopilotInputs);
    }


    /**
     * Simulates one step of the testbed
     *
     * @param autopilotOutputs the outputs of the autopilot
     * @param step  the n th step in the simulation
     * @return the inputs for the next step of the autopilot
     * @throws InterruptedException
     * @throws IOException
     */
    public AutopilotInputs testbedStep(AutopilotOutputs autopilotOutputs, int step) throws InterruptedException, IOException {
        AutopilotInputs autopilotInputs = null;
        // update time
        Time.update();

        // warning: assure every iteration terminates with a non null autopilotInputs, the
        // writer and handler can't handle it
        if (goalNotReached && !this.getDroneCam().isTerminated()) {
            //pass the outputs to the drone
            byte[] oldImage = this.getDroneCam().getCameraView();
            //try if the world can be advanced to the next state
            try {
                // elapsedTime replace by getTimePassed()
                if (!isFirstRun()) {
                    drone.setAutopilotOutputs(autopilotOutputs);
                    this.getWorld().advanceWorldState(TIME_STEP, STEPS_PER_ITERATION);
                } else {
                    firstRun = false;
                }
                // render the windows and terminate graphics if all windows are closed
                //possibly set this part apart from the try-catch

            } catch (IOException e) {
                System.out.println("IO exception");
            }

            this.getGraphics().renderWindows();
            byte[] cameraImage = droneCam.getCameraView();
            autopilotInputs = new MainAutopilotInputs(drone, cameraImage, (float) step*TIME_STEP * STEPS_PER_ITERATION);
        }

        long timeLeft = (long) (FRAME_MILLIS - Time.timeSinceLastUpdate());
        if (timeLeft > 0)
            Thread.sleep(timeLeft);
        //System.out.println(timeLeft);
        return autopilotInputs;
    }



    public AutopilotConfig getConfig() {
        return drone.getAutopilotConfig();
    }

    public boolean isFirstRun() {
        return firstRun;
    }


    // configuration for 20 fps
    private final static float TIME_STEP = 0.001f;
    private final static float FRAMERATE = 20.0f;
    private final static int STEPS_PER_ITERATION = Math.round((1 / FRAMERATE) / TIME_STEP);
    private final static long FRAME_MILLIS = 50;

    /**
     * Class that contains the autopilotInputs implemented separately for cleaner code
     */
    private static class MainAutopilotInputs implements AutopilotInputs {

        /**
         * Constructor for the class
         * @param drone the drone of the testbed
         * @param cameraImage the byte array containing the image for the autopilot
         * @param elapsedTime the time that has elapsed
         */
        private MainAutopilotInputs(Drone drone, byte[] cameraImage, float elapsedTime) {
            this.drone = drone;
            this.cameraImage = cameraImage;
            this.elapsedTime = elapsedTime;
        }

        @Override
        public byte[] getImage() {
            return cameraImage;
        }

        @Override
        public float getX() {
            return drone.getPosition().getxValue();
        }

        @Override
        public float getY() {
            return drone.getPosition().getyValue();
        }

        @Override
        public float getZ() {
            return drone.getPosition().getzValue();
        }

        @Override
        public float getHeading() {
            return drone.getHeading();
        }

        @Override
        public float getPitch() {
            return drone.getPitch();
        }

        @Override
        public float getRoll() {
            return drone.getRoll();
        }

        @Override
        public float getElapsedTime() {
            return elapsedTime;
        }

        private Drone drone;

        private byte[] cameraImage;

        private float elapsedTime;
    }


    private World getWorld() {
        return world;
    }

    private void setWorld(World world) {
        this.world = world;
    }

    private Graphics getGraphics() {
        return graphics;
    }

    private void setGraphics(Graphics graphics) {
        this.graphics = graphics;
    }

    private Drone getDrone() {
        return drone;
    }

    private void setDrone(Drone drone) {
        this.drone = drone;
    }

    private String getConnectionName() {
        return connectionName;
    }

    private void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    private int getConnectionPort() {
        return connectionPort;
    }

    private void setConnectionPort(int connectionPort) {
        this.connectionPort = connectionPort;
    }

    private Window getDroneCam() {
        return droneCam;
    }

    private void setDroneCam(Window droneCam) {
        this.droneCam = droneCam;
    }

    private Window getDroneView() {
        return droneView;
    }

    private void setDroneView(Window droneView) {
        this.droneView = droneView;
    }

    private Window getPersonView() {
        return personView;
    }

    private void setPersonView(Window personView) {
        this.personView = personView;
    }

    public Window getTopDownView() {
        return topDownView;
    }

    public void setTopDownView(Window topDownView) {
        this.topDownView = topDownView;
    }

    public Window getChaseView() {
        return chaseView;
    }

    public void setChaseView(Window chaseView) {
        this.chaseView = chaseView;
    }

    public Window getSideView() {
        return sideView;
    }

    public void setSideView(Window sideView) {
        this.sideView = sideView;
    }

    public boolean getShowAllWindows() {
        return showAllWindows;
    }

    public FlightRecorder getFlightRecorder() {
        return flightRecorder;
    }

    public void setFlightRecorder(FlightRecorder flightRecorder) {
        if(this.getFlightRecorder() != null)
            throw new IllegalStateException(ALREADY_RECORDING);
        this.flightRecorder = flightRecorder;
    }

    /*  private boolean isGoalNotReached() {
        return goalNotReached;
    }

    private void setGoalNotReached(boolean goalNotReached) {
        this.goalNotReached = goalNotReached;
    }*/



    private World world;
    private Graphics graphics;
    private Drone drone;

    private Block block0;
    private Block block1;
    private Block block2;
    private Block block3;
    private Block block4;

    private Window droneCam;
    private Window droneView;
    private Window personView;
    private Window topDownView;
    private Window chaseView;
    private Window sideView;

    private String connectionName;
    private int connectionPort;
    private boolean showAllWindows;
    private FlightRecorder flightRecorder;
//	Window textWindow = new Window(500, 500, 0.5f, 0.5f, "text window", new Vector3f(0.0f, 0.0f, 0.0f), true, droneCam); // Not implemented yet


    /*
    flags
     */
    private boolean goalNotReached = true;
    private boolean firstRun = true;

    /*
    Error Messages
     */
    private final static String ALREADY_RECORDING = "there is already a flight recorder recording";

}
