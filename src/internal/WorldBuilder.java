package internal;


import gui.Cube;
/*
*//**
 * Created by Martijn on 26/10/2017.
 * a class to build a world, set the standard configuration here.
 */
public class WorldBuilder {

    public Drone DRONE = new DroneBuilder(true).createDrone();

    // Deprecated: all blocks are generated in main loop
    public final static Vector BLOCKPOS = new Vector(0.0f, 0.489f, -6.9098f);
    public final static Vector COLOR = new Vector(0.0f, 1.0f, 70.0f);
    private static int numberOfBlocks = 5;
    public final static WorldGenerator wg = new WorldGenerator(numberOfBlocks);


    public WorldBuilder(FlightRecorder flightRecorder) {
        DRONE.getPhysXEngine().setFlightRecorder(flightRecorder);
    }

    public World createWorld() {
        //Block block1 = new Block(BLOCKPOS);
        //Cube cube1 = new Cube(BLOCKPOS.convertToVector3f(), COLOR.convertToVector3f());
        //block1.setAssocatedCube(cube1);

        //World world = new World();
        World world = wg.createWorld();
        //world.addWorldObject(block1);
        world.addWorldObject(DRONE);

        return world;
    }

    /**
     * Creates an empty world only containing the drone, made for testing purposes only
     * @return a world only containing the drone
     */
    public World createSimpleworld(){
        World world = new World(World.REACH_CUBE_OBJECTIVE);
        world.addWorldObject(DRONE);
        Block block1 = new Block(BLOCKPOS);
        Cube cube1 = new Cube(BLOCKPOS.convertToVector3f(), COLOR.convertToVector3f());
        block1.setAssocatedCube(cube1);
        world.addWorldObject(block1);

        return world;
    }

    private FlightRecorder flightRecorder;

}