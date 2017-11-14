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

    public AutopilotMain(String connectionName, int connectionPort) {
        this.setAutoPilot(new AutoPilot());
        this.setConnectionName(connectionName);
        this.setConnectionPort(connectionPort);
    }



    @Override
    public void run() {
        try {
            autopilotMainLoop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void autopilotMainLoop() throws IOException {
        Socket autopilotClientSocket = new Socket(this.getConnectionName(), this.getConnectionPort());

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
                //let the exception fly
                System.out.println("Catching Exception: waiting for config input");
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
                System.out.println("Catching Exception: waiting for testbed input");
            }catch(java.io.EOFException e){
                //the stream has stopped, close the socket
                autopilotClientSocket.close();
                //nothing to do anymore... just close
                break;
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

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public int getConnectionPort() {
        return connectionPort;
    }

    public void setConnectionPort(int connectionPort) {
        this.connectionPort = connectionPort;
    }

    private AutoPilot autoPilot;
    private AutopilotConfig autopilotConfig;
    private AutopilotInputs autopilotInputs;

    private String connectionName;
    private int connectionPort;
    public boolean simulationEnded = false;
}

  /*  *//**
     * Configure the autopilot
     * @param inputs
     * @return
     * @throws IOException
     *//*
    public AutopilotOutputs autopilotStep(AutopilotInputs inputs) throws IOException {

        AutopilotOutputs outputs;

        outputs = this.getAutoPilot().timePassed(inputs);

        return outputs;
    }

    public AutopilotOutputs autopilotConfigStep(AutopilotInputs inputs, AutopilotConfig config) throws IOException {
        AutopilotOutputs outputs;

        outputs = this.getAutoPilot().simulationStarted(config, inputs);

        return outputs;
    }*/
