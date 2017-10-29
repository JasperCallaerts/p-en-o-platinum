<<<<<<< HEAD
package tests;

import internal.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by Martijn on 21/10/2017.
 */
public class StateTest {

    public static HorizontalWing rightMain, leftMain, horizontalStabilizer;
    public static VerticalWing verticalStabilizer;
    public static Drone drone;
    public static int nbOfIterations = 10;
    public static float timeStep = 0.001f;


    @Before
    public void setupMutableFixture(){
        float droneMass = 0.0f;
        float engineMass = 10.0f;
        float maxThrust = 100.0f;
        float mainWingMass = 5.0f;
        float stabilizerMass = 2.0f;
        float mainWingStartIncl = (float) (Math.PI/12.0f);
        float stabInclination = 0.0f;
        float maxAngleOfAttack = (float) (Math.PI/2.0f-0.01f);
        float liftCoefficient = 1.0f;
        Vector leftWingPos = new Vector(-4.0f, 0.0f, 0.0f);
        Vector rightWingPos = new Vector(4.0f, 0.0f, 0.0f);
        Vector stabilizerPos = new Vector(0.0f, 0.0f, 8.0f);
        Vector startPos = new Vector(0.0f, 0.0f, 0.0f);
        Vector startVelocity = new Vector(0.0f, 0.0f, -20.490349f);
        Vector startOrientation = new Vector(0.0f, 0.0f, 0.0f);
        Vector startRotation = new Vector(0.0f, 0.0f, 0.0f);

        rightMain = new HorizontalWing(rightWingPos, liftCoefficient, mainWingMass, maxAngleOfAttack, mainWingStartIncl);
        leftMain = new HorizontalWing(leftWingPos, liftCoefficient, mainWingMass, maxAngleOfAttack, mainWingStartIncl);
        horizontalStabilizer = new HorizontalWing(stabilizerPos, liftCoefficient, stabilizerMass, maxAngleOfAttack,0.0f);
        verticalStabilizer = new VerticalWing(stabilizerPos, liftCoefficient, stabilizerMass, maxAngleOfAttack, stabInclination);

        drone = new Drone(droneMass, engineMass, maxThrust, startPos, startVelocity, startOrientation, startRotation, rightMain, leftMain, horizontalStabilizer, verticalStabilizer, null);
        drone.setThrust(63.11539f);
    }

    @Test
    public void testNextState() throws IOException{
        System.out.println("VerticalStab: " + verticalStabilizer.getLift());
        System.out.println("HorizontalStab: " + horizontalStabilizer.getLift());
        System.out.println("RightMain: " + rightMain.getLift());
        System.out.println("LeftMain: " + leftMain.getLift());
        System.out.println("Moment: " + drone.getTotalMomentDrone());
        System.out.println("AngularAcceleration: " + drone.calcAngularAcceleration());
        System.out.println("Position: " + drone.getPosition().toString() + "\nVelocity: " + drone.getVelocity().toString()
                + "\nOrientation: " + drone.getOrientation().toString() + "\n");
        try {
            for (int index = 0; index != nbOfIterations; index++) {
                drone.toNextState(timeStep);
                System.out.println("VerticalStab: " + verticalStabilizer.getLift());
                System.out.println("HorizontalStab: " + horizontalStabilizer.getLift());
                System.out.println("RightMain: " + rightMain.getLift());
                System.out.println("LeftMain: " + leftMain.getLift());
                System.out.println("Moment: " + drone.getTotalMomentDrone());
                System.out.println("AngularAcceleration: " + drone.calcAngularAcceleration());
                System.out.println("Position: " + drone.getPosition().toString() + "\nVelocity: " + drone.getVelocity().toString()
                        + "\nOrientation: " + drone.getOrientation().toString() + "\n");

            }
        }catch (IllegalArgumentException e) {
            System.out.println("VerticalStab: " + verticalStabilizer.getLift());
            System.out.println("HorizontalStab: " + horizontalStabilizer.getLift());
            System.out.println("RightMain: " + rightMain.getLift());
            System.out.println("LeftMain: " + leftMain.getLift());
            System.out.println("Moment: " + drone.getTotalMomentDrone());
            System.out.println("AngularAcceleration: " + drone.calcAngularAcceleration());
            System.out.println("Position: " + drone.getPosition().toString() + "\nVelocity: " + drone.getVelocity().toString()
                    + "\nOrientation: " + drone.getOrientation().toString() + "\n");
            System.out.println("error Exit");
        }

    }

    @Test
    public void findEquilibrium(){
        boolean equilibriumNotfound = true;
        float velocityStep = .01f;
        float velocityBase = 5.2f;
        drone.setVelocity(new Vector(0,0,-velocityBase));
        while(equilibriumNotfound){
            Vector force = drone.getTotalExternalForcesWorld();
            System.out.println("Current Force: " + force + "Current Velocity: " + velocityBase);
            if(force.getxValue() >= 0 && force.getyValue() >=0 && force.getzValue()>=0){
                equilibriumNotfound = false;
            }
            velocityBase += velocityStep;
            drone.setVelocity(new Vector(0,0, -velocityBase));

        }
    }

    @Test
    public void testForce(){
        drone.setVelocity(new Vector(0,0,-10.0f));
        Vector force = drone.getTotalExternalForcesWorld();
        System.out.println(force);
    }

    @Test
    public void testBuilder(){
        DroneBuilder builder = new DroneBuilder(true);
        Drone newDrone = builder.createDrone();
        System.out.println(newDrone.getTotalExternalForcesWorld());
        System.out.println(newDrone.getVelocity());
        System.out.println(newDrone.getThrust());
    }

}
=======
package tests;

import internal.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by Martijn on 21/10/2017.
 */
public class StateTest {

    public static HorizontalWing rightMain, leftMain, horizontalStabilizer;
    public static VerticalWing verticalStabilizer;
    public static Drone drone;
    public static int nbOfIterations = 10;
    public static float timeStep = 0.001f;


    @Before
    public void setupMutableFixture(){
        float droneMass = 0.0f;
        float engineMass = 10.0f;
        float maxThrust = 100.0f;
        float mainWingMass = 5.0f;
        float stabilizerMass = 2.0f;
        float mainWingStartIncl = (float) (Math.PI/12.0f);
        float stabInclination = 0.0f;
        float maxAngleOfAttack = (float) (Math.PI/2.0f-0.01f);
        float liftCoefficient = 1.0f;
        Vector leftWingPos = new Vector(-4.0f, 0.0f, 0.0f);
        Vector rightWingPos = new Vector(4.0f, 0.0f, 0.0f);
        Vector stabilizerPos = new Vector(0.0f, 0.0f, 8.0f);
        Vector startPos = new Vector(0.0f, 0.0f, 0.0f);
        Vector startVelocity = new Vector(0.0f, 0.0f, -20.490349f);
        Vector startOrientation = new Vector(0.0f, 0.0f, 0.0f);
        Vector startRotation = new Vector(0.0f, 0.0f, 0.0f);

        rightMain = new HorizontalWing(rightWingPos, liftCoefficient, mainWingMass, maxAngleOfAttack, mainWingStartIncl);
        leftMain = new HorizontalWing(leftWingPos, liftCoefficient, mainWingMass, maxAngleOfAttack, mainWingStartIncl);
        horizontalStabilizer = new HorizontalWing(stabilizerPos, liftCoefficient, stabilizerMass, maxAngleOfAttack,0.0f);
        verticalStabilizer = new VerticalWing(stabilizerPos, liftCoefficient, stabilizerMass, maxAngleOfAttack, stabInclination);

        drone = new Drone(droneMass, engineMass, maxThrust, startPos, startVelocity, startOrientation, startRotation, rightMain, leftMain, horizontalStabilizer, verticalStabilizer, null);
        drone.setThrust(63.11539f);
    }

    @Test
    public void testNextState(){
        System.out.println("VerticalStab: " + verticalStabilizer.getLift());
        System.out.println("HorizontalStab: " + horizontalStabilizer.getLift());
        System.out.println("RightMain: " + rightMain.getLift());
        System.out.println("LeftMain: " + leftMain.getLift());
        System.out.println("Moment: " + drone.getTotalMomentDrone());
        System.out.println("AngularAcceleration: " + drone.calcAngularAcceleration());
        System.out.println("Position: " + drone.getPosition().toString() + "\nVelocity: " + drone.getVelocity().toString()
                + "\nOrientation: " + drone.getOrientation().toString() + "\n");
        try {
            for (int index = 0; index != nbOfIterations; index++) {
                try {
                    drone.toNextState(timeStep);
                }catch (IOException e){

                }
                System.out.println("VerticalStab: " + verticalStabilizer.getLift());
                System.out.println("HorizontalStab: " + horizontalStabilizer.getLift());
                System.out.println("RightMain: " + rightMain.getLift());
                System.out.println("LeftMain: " + leftMain.getLift());
                System.out.println("Moment: " + drone.getTotalMomentDrone());
                System.out.println("AngularAcceleration: " + drone.calcAngularAcceleration());
                System.out.println("Position: " + drone.getPosition().toString() + "\nVelocity: " + drone.getVelocity().toString()
                        + "\nOrientation: " + drone.getOrientation().toString() + "\n");

            }
        }catch (IllegalArgumentException e) {
            System.out.println("VerticalStab: " + verticalStabilizer.getLift());
            System.out.println("HorizontalStab: " + horizontalStabilizer.getLift());
            System.out.println("RightMain: " + rightMain.getLift());
            System.out.println("LeftMain: " + leftMain.getLift());
            System.out.println("Moment: " + drone.getTotalMomentDrone());
            System.out.println("AngularAcceleration: " + drone.calcAngularAcceleration());
            System.out.println("Position: " + drone.getPosition().toString() + "\nVelocity: " + drone.getVelocity().toString()
                    + "\nOrientation: " + drone.getOrientation().toString() + "\n");
            System.out.println("error Exit");
        }

    }

    @Test
    public void findEquilibrium(){
        boolean equilibriumNotfound = true;
        float velocityStep = .01f;
        float velocityBase = 5.2f;
        drone.setVelocity(new Vector(0,0,-velocityBase));
        while(equilibriumNotfound){
            Vector force = drone.getTotalExternalForcesWorld();
            System.out.println("Current Force: " + force + "Current Velocity: " + velocityBase);
            if(force.getxValue() >= 0 && force.getyValue() >=0 && force.getzValue()>=0){
                equilibriumNotfound = false;
            }
            velocityBase += velocityStep;
            drone.setVelocity(new Vector(0,0, -velocityBase));

        }
    }

    @Test
    public void testForce(){
        drone.setVelocity(new Vector(0,0,-10.0f));
        Vector force = drone.getTotalExternalForcesWorld();
        System.out.println(force);
    }

    @Test
    public void testBuilder(){
        DroneBuilder builder = new DroneBuilder(true);
        Drone newDrone = builder.createDrone();
        System.out.println(newDrone.getTotalExternalForcesWorld());
        System.out.println(newDrone.getVelocity());
        System.out.println(newDrone.getThrust());
    }

}
>>>>>>> 063e7a620bfa23f1777f66200ff41a9732279f32
