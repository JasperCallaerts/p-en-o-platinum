import Autopilot.*;
import internal.AutoPilot;
import internal.AutoPilotConfig;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Martijn on 13/11/2017.
 * supervised by bart
 */
public class AutopilotMain implements Runnable {

    public AutopilotMain() {
        this.setAutoPilot(new AutoPilot());
    }

    public AutopilotOutputs autopilotStep(AutopilotInputs inputs) throws IOException {

        AutopilotOutputs outputs;

        outputs = this.getAutoPilot().timePassed(inputs);

        return outputs;
    }

    public AutopilotOutputs autopilotConfigStep(AutopilotInputs inputs, AutopilotConfig config) throws IOException {
        AutopilotOutputs outputs;

        outputs = this.getAutoPilot().simulationStarted(config, inputs);

        return outputs;
    }

    @Override
    public void run() {
        try {
            autopilotMainLoop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void autopilotMainLoop() throws IOException {
        Socket autopilotClientSocket = new Socket(CONNECTION_NAME, CONNECTION_PORT);

        DataInputStream inputStream = new DataInputStream(autopilotClientSocket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(autopilotClientSocket.getOutputStream());
        boolean firstRun = true;
        //first configure the autopilot
        while(firstRun){
            try{
                AutopilotConfig config = AutopilotConfigReader.read(inputStream);
                AutopilotInputs inputs = AutopilotInputsReader.read(inputStream);
                AutopilotOutputs outputs = this.getAutoPilot().simulationStarted(config, inputs);
                AutopilotOutputsWriter.write(outputStream, outputs);
                firstRun = false;
            }catch(NullPointerException e){
                // let the exception fly
            }
        }

        // now enter the real main loop, also find way to terminate correctly
        while(true){
            try{
                AutopilotInputs inputs = AutopilotInputsReader.read(inputStream);
                AutopilotOutputs outputs = this.getAutoPilot().timePassed(inputs);
                AutopilotOutputsWriter.write(outputStream, outputs);
            }catch(NullPointerException e){
                //let the exception fly
            }

        }

    }



    public AutoPilot getAutoPilot() {
        return autoPilot;
    }

    public void setAutoPilot(AutoPilot autoPilot) {
        this.autoPilot = autoPilot;
    }

    public AutopilotConfig getAutopilotConfig() {
        return autopilotConfig;
    }

    public void setAutopilotConfig(AutopilotConfig autopilotConfig) {
        this.autopilotConfig = autopilotConfig;
    }

    public AutopilotInputs getAutopilotInputs() {
        return autopilotInputs;
    }

    public void setAutopilotInputs(AutopilotInputs autopilotInputs) {
        this.autopilotInputs = autopilotInputs;
    }

    private AutoPilot autoPilot;
    private AutopilotConfig autopilotConfig;
    private AutopilotInputs autopilotInputs;

    public final static String CONNECTION_NAME = "localhost";
    public final static int CONNECTION_PORT = 8080;

}
