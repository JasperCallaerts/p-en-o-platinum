package tests;

import Autopilot.AutopilotOutputs;
import internal.*;
import org.junit.Before;
import org.junit.Test;
import sun.awt.SunGraphicsCallback;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Martijn on 16/11/2017.
 * created to test the different differentiation techniques
 */
public class DifferentiationTest {

    public static Drone drone;
    public static float INTERVAL1 = 1E-3f;
    public static float INTERVAL2 = 1E-4f;
    public static int NB_ITERATIONS1 = 10000;
    public static int NB_ITERATIONS2 = 100000;


    @Before
    public void setupMutableFixture(){
        WorldBuilder builder = new WorldBuilder(null);
        drone = builder.DRONE;
    }

    @Test
    public void testDifferenceEuler() throws IOException {
        String firstTestString = "";
        drone.setDiffMethod(PhysXEngine.EULER_METHOD);
        drone.setAutopilotOutputs(new AutopilotOutputs() {
            @Override
            public float getThrust() {
                return 32.857273f;
            }

            @Override
            public float getLeftWingInclination() {
                return (float) Math.PI / 12;
            }

            @Override
            public float getRightWingInclination() {
                return (float) Math.PI / 12;
            }

            @Override
            public float getHorStabInclination() {
                return 0;
            }

            @Override
            public float getVerStabInclination() {
                return 0;
            }
        });
        for (int i = 0; i != NB_ITERATIONS1; i++) {
            drone.toNextState(INTERVAL1);
            //every hundred ms add an entry to the string
            if (i % 100 == 0) {
                Vector dronePos = drone.getPosition();
                firstTestString += Float.toString(dronePos.getxValue()) + ";" +
                        Float.toString(dronePos.getyValue()) + ";" + Float.toString(dronePos.getzValue()) + ";";
            }
        }

        PrintWriter printerOutput = new PrintWriter("EulerTest1.txt");
        printerOutput.print(firstTestString);
        printerOutput.close();
    }
    @Test
    public void testDifferenceEuler2() throws IOException {
        drone.setDiffMethod(PhysXEngine.EULER_METHOD);
        String secondTestString = "";
        drone.setAutopilotOutputs(new AutopilotOutputs() {
            @Override
            public float getThrust() {
                return 32.857273f;
            }

            @Override
            public float getLeftWingInclination() {
                return (float) Math.PI / 12;
            }

            @Override
            public float getRightWingInclination() {
                return (float) Math.PI / 12;
            }

            @Override
            public float getHorStabInclination() {
                return 0;
            }

            @Override
            public float getVerStabInclination() {
                return 0;
            }
        });
        for(int i = 0; i != NB_ITERATIONS2; i++) {
            drone.toNextState(INTERVAL2);
            //every hundred ms add an entry to the string
            if(i%1000 == 0){
                Vector dronePos = drone.getPosition();
                secondTestString += Float.toString(dronePos.getxValue()) + ";" +
                        Float.toString(dronePos.getyValue()) + ";" + Float.toString(dronePos.getzValue())+";";
            }
        }

        PrintWriter printerOutput = new PrintWriter("EulerTest2.txt");
        printerOutput.print(secondTestString);
        printerOutput.close();

    }
    @Test
    public void testDifferenceCauchy() throws IOException {
        String firstTestString = "";
        drone.setDiffMethod(PhysXEngine.CAUCHY_METHOD);
        drone.setAutopilotOutputs(new AutopilotOutputs() {
            @Override
            public float getThrust() {
                return 32.857273f;
            }

            @Override
            public float getLeftWingInclination() {
                return (float) Math.PI / 12;
            }

            @Override
            public float getRightWingInclination() {
                return (float) Math.PI / 12;
            }

            @Override
            public float getHorStabInclination() {
                return 0;
            }

            @Override
            public float getVerStabInclination() {
                return 0;
            }
        });
        for (int i = 0; i != NB_ITERATIONS1; i++) {
            drone.toNextState(INTERVAL1);
            //every hundred ms add an entry to the string
            if (i % 100 == 0) {
                Vector dronePos = drone.getPosition();
                firstTestString += Float.toString(dronePos.getxValue()) + ";" +
                        Float.toString(dronePos.getyValue()) + ";" + Float.toString(dronePos.getzValue()) + ";";
            }
        }

        PrintWriter printerOutput = new PrintWriter("CauchyTest1.txt");
        printerOutput.print(firstTestString);
        printerOutput.close();
    }
    @Test
    public void testDifferenceCauchy2() throws IOException {
        drone.setDiffMethod(PhysXEngine.CAUCHY_METHOD);
        String secondTestString = "";
        drone.setAutopilotOutputs(new AutopilotOutputs() {
            @Override
            public float getThrust() {
                return 32.857273f;
            }

            @Override
            public float getLeftWingInclination() {
                return (float) Math.PI / 12;
            }

            @Override
            public float getRightWingInclination() {
                return (float) Math.PI / 12;
            }

            @Override
            public float getHorStabInclination() {
                return 0;
            }

            @Override
            public float getVerStabInclination() {
                return 0;
            }
        });
        for(int i = 0; i != NB_ITERATIONS2; i++) {
            drone.toNextState(INTERVAL2);
            //every hundred ms add an entry to the string
            if(i%1000 == 0){
                Vector dronePos = drone.getPosition();
                secondTestString += Float.toString(dronePos.getxValue()) + ";" +
                        Float.toString(dronePos.getyValue()) + ";" + Float.toString(dronePos.getzValue())+";";
            }
        }

        PrintWriter printerOutput = new PrintWriter("CauchyTest2.txt");
        printerOutput.print(secondTestString);
        printerOutput.close();

    }

    @Test
    public void testDifferenceRK4() throws IOException {
        String firstTestString = "";
        drone.setDiffMethod(PhysXEngine.RK4_METHOD);
        drone.setAutopilotOutputs(new AutopilotOutputs() {
            @Override
            public float getThrust() {
                return 32.857273f;
            }

            @Override
            public float getLeftWingInclination() {
                return (float) Math.PI / 12;
            }

            @Override
            public float getRightWingInclination() {
                return (float) Math.PI / 12;
            }

            @Override
            public float getHorStabInclination() {
                return 0;
            }

            @Override
            public float getVerStabInclination() {
                return 0;
            }
        });
        for (int i = 0; i != NB_ITERATIONS1; i++) {
            drone.toNextState(INTERVAL1);
            //every hundred ms add an entry to the string
            if (i % 100 == 0) {
                Vector dronePos = drone.getPosition();
                firstTestString += Float.toString(dronePos.getxValue()) + ";" +
                        Float.toString(dronePos.getyValue()) + ";" + Float.toString(dronePos.getzValue()) + ";";
            }
        }

        PrintWriter printerOutput = new PrintWriter("RkTest1.txt");
        printerOutput.print(firstTestString);
        printerOutput.close();
    }
    @Test
    public void testDifferenceRK42() throws IOException {
        drone.setDiffMethod(PhysXEngine.RK4_METHOD);
        String secondTestString = "";
        drone.setAutopilotOutputs(new AutopilotOutputs() {
            @Override
            public float getThrust() {
                return 32.857273f;
            }

            @Override
            public float getLeftWingInclination() {
                return (float) Math.PI / 12;
            }

            @Override
            public float getRightWingInclination() {
                return (float) Math.PI / 12;
            }

            @Override
            public float getHorStabInclination() {
                return 0;
            }

            @Override
            public float getVerStabInclination() {
                return 0;
            }
        });
        for(int i = 0; i != NB_ITERATIONS2; i++) {
            drone.toNextState(INTERVAL2);
            //every hundred ms add an entry to the string
            if(i%1000 == 0){
                Vector dronePos = drone.getPosition();
                secondTestString += Float.toString(dronePos.getxValue()) + ";" +
                        Float.toString(dronePos.getyValue()) + ";" + Float.toString(dronePos.getzValue())+";";
            }
        }

        PrintWriter printerOutput = new PrintWriter("RkTest2.txt");
        printerOutput.print(secondTestString);
        printerOutput.close();

    }



}
