import Autopilot.*;
import internal.AutoPilot;
import internal.AutoPilotConfig;
import internal.FlightRecorder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Martijn on 13/11/2017.
 * supervised by bart
 */
public class AutopilotMain implements Runnable {

    public AutopilotMain(String connectionName, int connectionPort, AutoPilot autopilot) {
        this.setAutoPilot(autopilot);
        this.setConnectionName(connectionName);
        this.setConnectionPort(connectionPort);
    }


    @Override
    public void run() {
        try {
            autopilotMainLoop();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void autopilotMainLoop() throws IOException, InterruptedException {

        //set the flight recorder
        this.getAutoPilot().setFlightRecorder(this.getFlightRecorder());
        
        Socket autopilotClientSocket = null;
        DataInputStream inputStream = null;
        DataOutputStream outputStream = null;
        boolean firstRun = true;
        int connectionTries = 0;
        int maxConnectionTries = 1000;
        //first configure the autopilot
        while(firstRun){
            try{
                autopilotClientSocket = new Socket(this.getConnectionName(), this.getConnectionPort());

                inputStream = new DataInputStream(autopilotClientSocket.getInputStream());
                outputStream = new DataOutputStream(autopilotClientSocket.getOutputStream());
                AutopilotConfig config = AutopilotConfigReader.read(inputStream);
                AutopilotInputs inputs = AutopilotInputsReader.read(inputStream);
                AutopilotOutputs outputs = this.getAutoPilot().simulationStarted(config, inputs);
                AutopilotOutputsWriter.write(outputStream, outputs);
                firstRun = false;

            }/*catch(NullPointerException e){
                //let the exception fly
                System.out.println("Catching Exception: waiting for config input");
            }*/catch(java.net.ConnectException e){
                Thread.sleep(200);
               //if we tried to much, throw exception
                if(connectionTries == maxConnectionTries)
                    throw new java.net.ConnectException(e.getMessage());
                connectionTries +=1;
            }
        }

        // now enter the real main loop, also find way to terminate correctly
        while(true){

            try{
                AutopilotInputs inputs = AutopilotInputsReader.read(inputStream);
                AutopilotOutputs outputs = this.getAutoPilot().timePassed(inputs);
                AutopilotOutputsWriter.write(outputStream, outputs);
            /*}catch(NullPointerException e){
                //let the exception fly
                System.out.println("Catching Exception: waiting for testbed input");
                System.out.println(e);*/
            }catch(java.io.EOFException e){
                System.out.println("Closing down Autopilot Client");
                //the stream has stopped, close the socket
                inputStream.close();
                outputStream.close();
                autopilotClientSocket.close();
                //nothing to do anymore... just close
                break;
            }
        }
    }

    private AutoPilot getAutoPilot() {
        return autoPilot;
    }

    private void setAutoPilot(AutoPilot autoPilot) {
        this.autoPilot = autoPilot;
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

    public FlightRecorder getFlightRecorder() {
        return flightRecorder;
    }

    public void setFlightRecorder(FlightRecorder flightRecorder) {
        this.flightRecorder = flightRecorder;
    }

    private AutoPilot autoPilot;
    private FlightRecorder flightRecorder;
    private String connectionName;
    private int connectionPort;

}