import internal.SimulationEndedException;

import java.io.*;


/**
 * Created by Martijn on 13/11/2017.
 * and supervised by Bart
 */
public class SocketMain {
    public static void main(String[] args) throws IOException, InterruptedException {

        //first create both threads
        //TestbedMain mainTestbed = new TestbedMain();
        Thread testbedThread = new Thread(MAIN_TESTBED);
        Thread autopilotThread = new Thread(MAIN_AUTOPILOT);

        testbedThread.setUncaughtExceptionHandler(HANDLER);

        testbedThread.start();
        Thread.sleep(1000);
        autopilotThread.start();

        //mainTestbed.testbedMainLoop();


    }

    static Thread.UncaughtExceptionHandler HANDLER =  new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread th, Throwable ex) {
            switch(ex.toString()){
                case SimulationEndedException.TAG:
                    System.out.println("Handling ended simulation");


                default:
                    ex.printStackTrace();
            }
        }
    };

    public final static String CONNECTION_NAME = "localhost";
    public final static int CONNECTION_PORT = 21212;

    private static TestbedMain MAIN_TESTBED = new TestbedMain(CONNECTION_NAME, CONNECTION_PORT);
    private static AutopilotMain MAIN_AUTOPILOT = new AutopilotMain(CONNECTION_NAME, CONNECTION_PORT);

}

/*
//        //createConnection
//        ServerSocket testbedServerSocket = new ServerSocket(CONNECTION_PORT);
//        Socket autoPilotSocket = new Socket(CONNECTION_NAME, CONNECTION_PORT);
//        Socket testbedSocket = testbedServerSocket.accept();
//        DataOutputStream autopilotOutputStream = new DataOutputStream(autoPilotSocket.getOutputStream());
//        DataInputStream autopilotInputStream = new DataInputStream(autoPilotSocket.getInputStream());
//
//        DataOutputStream testbedOutput = new DataOutputStream(testbedSocket.getOutputStream());
//        DataInputStream testbedInput = new DataInputStream(testbedSocket.getInputStream());
//
//        TestbedMain testbedMain = new TestbedMain();
//
//        AutopilotMain autopilotMain = new AutopilotMain();
//
//        configAutopilot_OutputPart(testbedOutput, testbedMain);
//        configureAutopilot_InputPart(autopilotMain, autopilotInputStream, autopilotOutputStream);
//
//
//       while(true){
//            advanceTestbed(testbedMain, testbedInput, testbedOutput);
//            advanceAutoPilot(autopilotMain, autopilotInputStream, autopilotOutputStream);
//        }
//    }

    */
/**
     * Outputs the configuration of the autopilot
     * @param configOutput the outputstream where the config & autopilot input wil be written on
     * @param testbedMain the main loop for the testbed
     * @throws IOException
     * @throws InterruptedException
     *//*

    private static void configAutopilot_OutputPart(DataOutputStream configOutput, TestbedMain testbedMain) throws IOException, InterruptedException {
        AutopilotConfig autopilotConfig = testbedMain.getConfig();
        AutopilotConfigWriter.write(configOutput, autopilotConfig);
        //null pointer because there is no input from autopilot, method will be able to handle it. (hopefully)
        advanceTestbed(testbedMain, null, configOutput);
    }

    // configures the autopilot and writes output to stream

    */
/**
     * Configures the autopilot according to the values on the given input stream
     * @param autopilotMain the autopilot main loop
     * @param configInputStream the input stream for the configuration
     * @param autopilotOutputStream the output stream for the first control action
     * @throws IOException
     *//*

    private static void configureAutopilot_InputPart(AutopilotMain autopilotMain, DataInputStream configInputStream, DataOutputStream autopilotOutputStream) throws IOException {
        AutopilotConfig config = AutopilotConfigReader.read(configInputStream);
        AutopilotInputs inputs = AutopilotInputsReader.read(configInputStream);
        System.out.println("read config and inputs");
        AutopilotOutputs autopilotOutputs =  autopilotMain.autopilotConfigStep(inputs, config);
        AutopilotOutputsWriter.write(autopilotOutputStream, autopilotOutputs);
    }

    */
/**
     * Advance the autopilot one step for the given inputs
     * @param autopilotMain the mainloop of the autopilot
     * @param autopilotInputStream the output of the testbed, the input data for the autopilot
     * @param autopilotOutputStream the output stream of the autopilot, contains instructions for the
     *                              testbed
     * @throws IOException
     *//*

    private static void advanceAutoPilot(AutopilotMain autopilotMain, DataInputStream autopilotInputStream, DataOutputStream autopilotOutputStream) throws IOException {
        AutopilotInputs autopilotInputs = AutopilotInputsReader.read(autopilotInputStream);
        AutopilotOutputs autopilotOutputs = autopilotMain.autopilotStep(autopilotInputs);
        AutopilotOutputsWriter.write(autopilotOutputStream, autopilotOutputs);

    }

    //read inputs from the autopilot and write output to the stream

    */
/**
     * Advance the testbed one step for the given streams
     * @param testbedMain the testbed main loop
     * @param inputStream the input stream of the testbed, containing the output of the autopilot
     * @param outputStream the output of the testbed, containing data for the autopilot
     * @throws IOException
     * @throws InterruptedException
     *//*

    private static void advanceTestbed(TestbedMain testbedMain, DataInputStream inputStream, DataOutputStream outputStream) throws IOException, InterruptedException {
        AutopilotInputs autopilotInputs;
        if(testbedMain.isFirstRun()){

            autopilotInputs = testbedMain.testbedStep(null);
        }else{
            AutopilotOutputs autopilotOutputs = readFromAutoPilot(inputStream);
            autopilotInputs = testbedMain.testbedStep(autopilotOutputs);
        }

        writeFromTestbed(outputStream, autopilotInputs);
    }


    //write the results from the testbed to the stream

    */
/**
     * Write the results from the testbed to the output stream
     * @param outputStream the output stream to write on
     * @param inputs the inputs for the autopilot
     * @throws InterruptedException
     * @throws IOException
     *//*

    private static void writeFromTestbed(DataOutputStream outputStream, AutopilotInputs inputs) throws InterruptedException, IOException {
        System.out.println("outputStream: " +  outputStream + "autopilotInputs: " + inputs);
        AutopilotInputsWriter.write(outputStream, inputs);
    }

    //read the data from the autopilot

    */
/**
     * Reads the output from the autopilot
     * @param inputStream the stream to write the data on
     * @return the outputs from the autopilot for the testbed
     * @throws IOException
     *//*

    private static AutopilotOutputs readFromAutoPilot(DataInputStream inputStream) throws IOException {
        return AutopilotOutputsReader.read(inputStream);
    }
*/

