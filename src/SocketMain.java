import Autopilot.*;


import java.io.*;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Martijn on 13/11/2017.
 * and supervised by Bart
 */
public class SocketMain {
    public static void main(String[] args) throws IOException, InterruptedException {

        //createConnection
        ServerSocket testbedServerSocket = new ServerSocket(CONNECTION_PORT);
        Socket autoPilotSocket = new Socket(CONNECTION_NAME, CONNECTION_PORT);
        Socket testbedSocket = testbedServerSocket.accept();
        DataOutputStream autopilotOutputStream = new DataOutputStream(autoPilotSocket.getOutputStream());
        DataInputStream autopilotInputStream = new DataInputStream(autoPilotSocket.getInputStream());

        DataOutputStream testbedOutput = new DataOutputStream(testbedSocket.getOutputStream());
        DataInputStream testbedInput = new DataInputStream(testbedSocket.getInputStream());

        TestbedMain testbedMain = new TestbedMain();

        AutopilotMain autopilotMain = new AutopilotMain();

        configAutopilot_OutputPart(testbedOutput, testbedMain);
        configureAutopilot_InputPart(autopilotMain, autopilotInputStream, autopilotOutputStream);


        for(int i = 0; i != 10000; i++){
            advanceTestbed(testbedMain, testbedInput, testbedOutput);
            advanceAutoPilot(autopilotMain, autopilotInputStream, autopilotOutputStream);
        }



    }

    //write the output needed to configure the autopilot to the stream
    private static void configAutopilot_OutputPart(DataOutputStream configOutput, TestbedMain testbedMain) throws IOException, InterruptedException {
        AutopilotConfig autopilotConfig = testbedMain.getConfig();
        AutopilotConfigWriter.write(configOutput, autopilotConfig);
        //null pointer because there is no input from autopilot, method will be able to handle it. (hopefully)
        advanceTestbed(testbedMain, null, configOutput);
    }

    // configures the autopilot and writes output to stream
    private static void configureAutopilot_InputPart(AutopilotMain autopilotMain, DataInputStream configInputStream, DataOutputStream autopilotOutputStream) throws IOException {
        AutopilotConfig config = AutopilotConfigReader.read(configInputStream);
        AutopilotInputs inputs = AutopilotInputsReader.read(configInputStream);
        System.out.println("read config and inputs");
        AutopilotOutputs autopilotOutputs =  autopilotMain.autopilotConfigStep(inputs, config);
        AutopilotOutputsWriter.write(autopilotOutputStream, autopilotOutputs);
    }

    private static void advanceAutoPilot(AutopilotMain autopilotMain, DataInputStream testbedOutput, DataOutputStream autopilotOutputStream) throws IOException {
        AutopilotInputs autopilotInputs = AutopilotInputsReader.read(testbedOutput);
        AutopilotOutputs autopilotOutputs = autopilotMain.autopilotStep(autopilotInputs);
        AutopilotOutputsWriter.write(autopilotOutputStream, autopilotOutputs);

    }

    //read inputs from the autopilot and write output to the stream
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
    private static void writeFromTestbed(DataOutputStream outputStream, AutopilotInputs inputs) throws InterruptedException, IOException {
        AutopilotInputsWriter.write(outputStream, inputs);
    }

    //read the data from the autopilot
    private static AutopilotOutputs readFromAutoPilot(DataInputStream inputStream) throws IOException {
        return AutopilotOutputsReader.read(inputStream);
    }




    public final static String CONNECTION_NAME = "localhost";
    public final static int CONNECTION_PORT = 8080;

}
