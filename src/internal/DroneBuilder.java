package internal;


import Autopilot.AutopilotConfig;

/**
 * Created by Martijn on 23/10/2017.
 * @author Martijn Sauwens
 */
public class DroneBuilder {

    /**
     * Constants used to create the drone & configure the autopilot
     */

    public final static float ENGINE_MASS = 5.0f;
    public final static float MAX_THRUST = 250.0f;
    public final static float MAIN_WING_MASS = 2.5f;
    public final static float STABILIZER_MASS = 1.25f;
    public final static float MAINWING_START_INCL = (float) Math.PI/12.0f;
    public final static float STABS_START_INCL = 0.0f;
    public final static float MAX_ANGLE_OF_ATTACK = (float) ( Math.PI/2.0 - 0.001f);
    public final static float LIFT_COEFFICIENT = 5.0f;
    public final static float LIFT_COEFFICIENT_STAB =1.0f;
    public final static Vector LEFTWING_POS = new Vector(-4.0f, 0.0f, 0.0f);
    public final static Vector RIGHTWING_POS = new Vector(4.0f, 0.0f, 0.0f);
    public final static Vector STABILIZE_POS = new Vector(0.0f, 0.0f, 8.0f);
    public final static Vector STARTPOS = new Vector();
    public final static Vector START_VEL = new Vector();
    public final static Vector START_ORIENTATION = new Vector();
    public final static Vector START_ROTATION = new Vector();


    public DroneBuilder(boolean balanced) {
        this.balanced = true;
    }


    public Drone createDrone() {
        HorizontalWing rightMain, leftMain, horizontalStabilizer;
        VerticalWing verticalStabilizer;
        Drone drone;

        rightMain = new HorizontalWing(RIGHTWING_POS, LIFT_COEFFICIENT, MAIN_WING_MASS, MAX_ANGLE_OF_ATTACK, MAINWING_START_INCL);
        leftMain = new HorizontalWing(LEFTWING_POS, LIFT_COEFFICIENT, MAIN_WING_MASS, MAX_ANGLE_OF_ATTACK, MAINWING_START_INCL);
        horizontalStabilizer = new HorizontalWing(STABILIZE_POS, LIFT_COEFFICIENT_STAB, STABILIZER_MASS, MAX_ANGLE_OF_ATTACK, STABS_START_INCL);
        verticalStabilizer = new VerticalWing(STABILIZE_POS, LIFT_COEFFICIENT_STAB, STABILIZER_MASS, MAX_ANGLE_OF_ATTACK, STABS_START_INCL);

        drone = new Drone(ENGINE_MASS, MAX_THRUST, STARTPOS, START_VEL, START_ORIENTATION, START_ROTATION, rightMain, leftMain, horizontalStabilizer, verticalStabilizer);

        // if the drone needs to be balanced, do so (balancing is the act of setting the vertical force to 0
        // and the Z value to 0
        if(this.isBalanced()){
            balanceDrone(drone);
        }

        System.out.println("Drone velocity: " + drone.getVelocity());
        System.out.println("Drone thrust: " + drone.getThrust());

        return drone;
    }

    /**
     * Constants to create the autopilotConfig & Autopilot
     */
    private final static float HORIZONTALVIEW = (float) (120.0f*Math.PI/180.0f);
    private final static float VERTICALVIEW = (float) (120.0f*Math.PI/180.0f);
    private final static int NB_ROWS = 200;
    private final static int NB_COLS= 200;

    /**
     * Creates an un configured autopilot
     * @return an autopilot class object
     */
    public AutoPilot createAutoPilot(){
       return  new AutoPilot();
    }

    /**
     * Pseudo constructor for a configuration of an autopilotConfig
     * @return an Autopilot config
     */
    public AutopilotConfig createConfig(){
        return new AutopilotConfig(){

            /**
             * not used
             * @return 0.0f
             */
            @Override
            public float getGravity() {
                return 0;
            }

            /**
             * not used
             * @return
             */
            @Override
            public float getWingX() {
                return RIGHTWING_POS.getxValue();
            }

            /**
             * not used
             * @return
             */
            @Override
            public float getTailSize() {
                return STABILIZE_POS.getzValue();
            }

            /**
             * not used
             * @return
             */
            @Override
            public float getEngineMass() {
                return ENGINE_MASS;
            }

            /**
             * not used
             * @return
             */
            @Override
            public float getWingMass() {
                return MAIN_WING_MASS;
            }

            /**
             * not used
             * @return
             */
            @Override
            public float getTailMass() {
                return STABILIZER_MASS;
            }

            /**
             * returns the maximum thrust of the drone
             * @return
             */
            @Override
            public float getMaxThrust() {
                return MAX_THRUST;
            }

            /**
             * not used
             * @return 0.0f
             */
            @Override
            public float getMaxAOA() {
                return 0;
            }

            /**
             * not used
             * @return 0.0f
             */
            @Override
            public float getWingLiftSlope() {
                return LIFT_COEFFICIENT;
            }

            /**
             * not used
             * @return 0.0f
             */
            @Override
            public float getHorStabLiftSlope() {
                return LIFT_COEFFICIENT;
            }

            /**
             * not used
             * @return 0.0f
             */
            @Override
            public float getVerStabLiftSlope() {
                return LIFT_COEFFICIENT;
            }


            @Override
            public float getHorizontalAngleOfView() {
                return HORIZONTALVIEW;
            }

            @Override
            public float getVerticalAngleOfView() {
                return VERTICALVIEW;
            }

            @Override
            public int getNbColumns() {
                return NB_COLS;
            }

            @Override
            public int getNbRows() {
                return NB_ROWS;
            }
        };

    }

    public void balanceDrone(Drone drone){
        // todo implement such that the drone is balanced on start of the execution.
        // use interval reduction to find the zero point for the lift, and initialize the thrust to
        // be equal to the "drag" experienced by the aircraft.
        float stepsize = 1.0f;
        float velocity = 0.0f;
        float firstPositive;
        boolean positiveLift = false;

        // first get the point where the total external forces are larger than zero
        while(! positiveLift) {
            if(drone.getTotalExternalForcesWorld().getyValue() > 0.0f) {
                positiveLift = true;
                firstPositive = velocity;
            }else{
                velocity += stepsize;
                drone.setVelocity(new Vector(0, 0, -velocity));
            }
        }

        velocity = this.findZero(drone, velocity-stepsize, velocity);
        System.out.println("returned velocity:" + velocity);
        drone.setVelocity(new Vector(0,0, -velocity));
        drone.setThrust(drone.getTotalExternalForcesWorld().getzValue());

    }

    public float findZero(Drone drone, float lowerBound, float upperBound){

        float epsilon = 1E-6f;
        float nbOfSteps = 10000;
        float prevYValue = Float.MAX_VALUE;
        float velocityCenter = upperBound;

        for(int index = 0; index != nbOfSteps; index++){
            //get the value for this iteration
            velocityCenter = (lowerBound + upperBound)/2.0f;
            drone.setVelocity(new Vector(0,0,-velocityCenter));
            float yNextValue = drone.getTotalExternalForcesWorld().getyValue();
            //System.out.println(drone.getTotalExternalForcesWorld());

            //check if the precision is good enough
            if(yNextValue == 0.0f){
                System.out.println("exit with good approx");
                return velocityCenter;
            // if not replace the borders
            }else{
                if(yNextValue < 0){
                    lowerBound = velocityCenter;
                    prevYValue = yNextValue;
                }else{
                    upperBound = velocityCenter;
                    prevYValue = yNextValue;
                }
            }
        }
        System.out.println("Exit with bad approx");
        System.out.println(drone.getTotalExternalForcesWorld());
        System.out.println("Velocity: " + velocityCenter);
        return velocityCenter;
    }



    public boolean isBalanced() {
        return balanced;
    }

    /**
     * Varaible that stores if the drone needs to be balanced before flight
     */
    private boolean balanced;



}
/*
        float droneMass = 0.0f;
        float engineMass = 10.0f;
        float maxThrust = 100.0f;
        float mainWingMass = 5.0f;
        float stabilizerMass = 2.0f;
        float mainWingStartIncl = (float) (Math.PI / 12.0f);
        float stabInclination = 0.0f;
        float maxAngleOfAttack = (float) (Math.PI / 2.0f - 0.01f);
        float liftCoefficient = 1.0f;
        Vector leftWingPos = new Vector(-4.0f, 0.0f, 0.0f);
        Vector rightWingPos = new Vector(4.0f, 0.0f, 0.0f);
        Vector stabilizerPos = new Vector(0.0f, 0.0f, 8.0f);
        Vector startPos = new Vector(0.0f, 0.0f, 0.0f);
        Vector startVelocity = new Vector(0.0f, 0.0f, -20.490349f);
        Vector startOrientation = new Vector(0.0f, 0.0f, 0.0f);
        Vector startRotation = new Vector(0.0f, 0.0f, 0.0f);

        rightMain =new HorizontalWing(rightWingPos, liftCoefficient, mainWingMass, maxAngleOfAttack, mainWingStartIncl);

        leftMain =new HorizontalWing(leftWingPos, liftCoefficient, mainWingMass, maxAngleOfAttack, mainWingStartIncl);

        horizontalStabilizer =new HorizontalWing(stabilizerPos, liftCoefficient, stabilizerMass, maxAngleOfAttack,0.0f);

        verticalStabilizer =new VerticalWing(stabilizerPos, liftCoefficient, stabilizerMass, maxAngleOfAttack, stabInclination);

        drone =new Drone(droneMass, engineMass, maxThrust, startPos, startVelocity, startOrientation, startRotation, rightMain, leftMain, horizontalStabilizer, verticalStabilizer, null);

        return drone;
 */



