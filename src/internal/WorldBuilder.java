package internal;


import java.io.IOException;
import java.util.List;

import gui.Cube;
import math.Vector3f;
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
    public final static boolean LOAD_COORDINATES = true;
    private static int numberOfBlocks = 5;
    public final static WorldGenerator wg = new WorldGenerator(numberOfBlocks);
    public final BlockCoordinatesParser parser = new BlockCoordinatesParser("src/internal/blockCoordinates.txt");


    public WorldBuilder() {
        //do nothing
    }

    public World createWorld() throws IOException{
        //Block block1 = new Block(BLOCKPOS);
        //Cube cube1 = new Cube(BLOCKPOS.convertToVector3f(), COLOR.convertToVector3f());
        //block1.setAssocatedCube(cube1);

        World world = new World(World.VISIT_ALL_OBJECTIVE);
    	if (!LOAD_COORDINATES) {
    		world = wg.createWorld();
    	}
    	else {
    		List<Vector> positions = parser.getCoordinates();
    		List<Vector> colors = wg.colorGenerator(positions.size());
    		for (int i = 0 ; i < positions.size(); i++) {
    			Vector position = positions.get(i);
    			Vector color = colors.get(i);
    			Block block = new Block(position);
            	Cube cube = new Cube(position.convertToVector3f(), color.convertToVector3f());
            	cube.setSize(1f);
            	block.setAssocatedCube(cube);
            	world.addWorldObject(block);
    		}
    	}
        
        //world.addWorldObject(block1);
        world.addWorldObject(DRONE);

        return world;
    }

    /**
     * Creates an empty world only containing the drone, made for testing purposes only
     * @return a world only containing the drone
     */
    public World createSimpleWorld(){
        World world = new World(World.REACH_CUBE_OBJECTIVE);
        world.addWorldObject(DRONE);
        Block block1 = new Block(BLOCKPOS);
        Cube cube1 = new Cube(BLOCKPOS.convertToVector3f(), COLOR.convertToVector3f());
        block1.setAssocatedCube(cube1);
        world.addWorldObject(block1);

        return world;
    }

}