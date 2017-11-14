import Autopilot.*;
import gui.Cube;
import gui.Graphics;
import gui.Time;
import gui.Window;
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
    public TestbedMain(String connectionName, int connectionPort) {
        this.setConnectionName(connectionName);
        this.setConnectionPort(connectionPort);
        //this.setQueue(queue);
    }

    /**
     * Initialize the testbed
     * Testbed is not initialized in the constructor because graphics need to be created
     * within one thread of control
     */
    private void initTestbed(){
        // initialize graphics capabilities
        this.setGraphics(new Graphics());

        // Cube needs graphics to be able to initialize cubes
        Cube.setGraphics(this.getGraphics());

        // Construct the windows
        this.setDroneCam(new Window(200, 200, 0.5f, 0.4f, "bytestream window", new Vector3f(1.0f, 1.0f, 1.0f), false));
        this.setDroneView(new Window(960, 1000, 0.0f, 0.4f, "Drone simulator 2017", new Vector3f(1.0f, 1.0f, 1.0f), true));
        this.setPersonView(new Window(960, 1000, 1f, 0.4f, "Drone simulator 2017", new Vector3f(0.5f, 0.8f, 1.0f), true));

        // add the windows to graphics
        this.getGraphics().addWindow("camera", this.getDroneCam());
        this.getGraphics().addWindow("third person view", this.getPersonView());
        this.getGraphics().addWindow("drone view", this.getDroneView());
//		graphics.addWindow("textWindow", textWindow); // Not implemented yet


        // drone builder covers all the stuff involving building the drone, adjust parameters there
        WorldBuilder worldBuilder = new WorldBuilder();
        this.setDrone(worldBuilder.DRONE);
        this.setWorld(worldBuilder.createWorld());

        // initialize second, third, fourth and fifth block, for testing purposes
        Vector BLOCKPOS = new Vector(2.0f, 6.0f, -40.0f);
        Vector BLOCKPOS2 = new Vector(4.0f, 10.0f, -60.0f);
        Vector BLOCKPOS3 = new Vector(5.0f, 8.0f, -80.0f);
        Vector BLOCKPOS4 = new Vector(0.0f, 0.0f, -100.0f);
        Vector COLOR = new Vector(1.0f, 0.0f, 0.0f);

        block1 = new Block(BLOCKPOS);
        Cube cube1 = new Cube(BLOCKPOS.convertToVector3f(), COLOR.convertToVector3f());
        block1.setAssocatedCube(cube1);

        block2 = new Block(BLOCKPOS2);
        Cube cube2 = new Cube(BLOCKPOS2.convertToVector3f(), COLOR.convertToVector3f());
        block2.setAssocatedCube(cube2);

        block3 = new Block(BLOCKPOS3);
        Cube cube3 = new Cube(BLOCKPOS3.convertToVector3f(), COLOR.convertToVector3f());
        block3.setAssocatedCube(cube3);

        block4 = new Block(BLOCKPOS4);
        Cube cube4 = new Cube(BLOCKPOS4.convertToVector3f(), COLOR.convertToVector3f());
        block4.setAssocatedCube(cube4);

        block0 = world.getRandomBlock();
        world.addWorldObject(block1);
        // END for testing purposes

        // Initialize the windows
        this.getDroneCam().initWorldWindow(this.getWorld(), true);
        this.getPersonView().initWorldWindow(this.getWorld(), false);
        this.getDroneView().initWorldWindow(this.getWorld(), true);
//      textWindow.initTextWindow(droneCam);


        Time.initTime();
    }


    //Todo configure the autopilot in the autopilot main
    //open seperate threads for both mainloops, where the first one waits for the other one.
    public void testbedMainLoop() throws IOException, InterruptedException {

        this.initTestbed();

        ServerSocket testbedServer = new ServerSocket(this.getConnectionPort());
        Socket testbedClientSocket = testbedServer.accept();
        DataInputStream inputStream = new DataInputStream(testbedClientSocket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(testbedClientSocket.getOutputStream());
        //write the configuration to the autopilot
        configAutopilot(outputStream);

        while(true) {
            //wait until the input is not null and advance if possible
            try {
                AutopilotOutputs output = AutopilotOutputsReader.read(inputStream);
                AutopilotInputsWriter.write(outputStream, this.testbedStep(output));
            } catch (NullPointerException e) {
                //let the null pointer fly
                System.out.println("Catching Exception: waiting for autopilot output");
            } catch(java.io.EOFException | SimulationEndedException ex ){
                //this exception means that the client socket has closed, close own sockets
                System.out.println("closing server sockets");
                testbedClientSocket.close();
                testbedServer.close();
                break;

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
    private void configAutopilot(DataOutputStream outputStream) throws IOException, InterruptedException {
        AutopilotConfigWriter.write(outputStream, this.getConfig());
        //instert a null pointer, will be ignored on the first step of the testbedstep method
        AutopilotInputs autopilotInputs = this.testbedStep(null);
        AutopilotInputsWriter.write(outputStream, autopilotInputs);
    }


    /**
     * Simulates one step of the testbed
     *
     * @param autopilotOutputs the outputs of the autopilot
     * @return the inputs for the next step of the autopilot
     * @throws InterruptedException
     * @throws IOException
     */
    public AutopilotInputs testbedStep(AutopilotOutputs autopilotOutputs) throws InterruptedException, IOException {
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
                if (!firstRun) {
                    drone.setAutopilotOutputs(autopilotOutputs);
                    this.getWorld().advanceWorldState(TIME_STEP, STEPS_PER_ITERATION);
                } else {
                    firstRun = false;
                }
                // render the windows and terminate graphics if all windows are closed
                //possibly set this part apart from the try-catch


            } catch (SimulationEndedException e) {
                goalNotReached = false;

                // Entire else clause is for testing purposes only
                if (world.hasWorldObject(block0)) {
                    world.removeBlocks();
                    world.addWorldObject(block1);
                    world.addWorldObject(block2);
                    goalNotReached = true;
                } else if (world.hasWorldObject(block1)) {
                    world.removeBlocks();
                    world.addWorldObject(block2);
                    world.addWorldObject(block3);
                    goalNotReached = true;
                } else if (world.hasWorldObject(block2)) {
                    world.removeBlocks();
                    world.addWorldObject(block3);
                    world.addWorldObject(block4);
                    goalNotReached = true;
                } else if (world.hasWorldObject(block3)) {
                    world.removeBlocks();
                    world.addWorldObject(block4);
                    goalNotReached = true;
                    throw new SimulationEndedException();
                }
            } catch (IOException e) {
                System.out.println("IO exception");
            }

            this.getGraphics().renderWindows();
            byte[] cameraImage = droneCam.getCameraView();
            autopilotInputs = new MainAutopilotInputs(drone, cameraImage, (float) Time.getTimePassed());
        }

        long timeLeft = (long) (FRAME_MILLIS - Time.timeSinceLastUpdate());
        if (timeLeft > 0)
            Thread.sleep(timeLeft);
        System.out.println(timeLeft);

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

    // Todo documentatie toevoegen (als je een katje bent)
    private static class MainAutopilotInputs implements AutopilotInputs {

        public MainAutopilotInputs(Drone drone, byte[] cameraImage, float elapsedTime) {
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

    private boolean isGoalNotReached() {
        return goalNotReached;
    }

    private void setGoalNotReached(boolean goalNotReached) {
        this.goalNotReached = goalNotReached;
    }

    public BlockingQueue getQueue() {
        return queue;
    }

    public void setQueue(BlockingQueue queue) {
        this.queue = queue;
    }

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

    private String connectionName;
    private int connectionPort;
    private BlockingQueue queue;
//	Window textWindow = new Window(500, 500, 0.5f, 0.5f, "text window", new Vector3f(0.0f, 0.0f, 0.0f), true, droneCam); // Not implemented yet


    /*
    flags
     */
    private boolean goalNotReached = true;
    private boolean firstRun = true;

}
