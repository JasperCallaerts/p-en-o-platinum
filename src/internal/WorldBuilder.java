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

    public Drone DRONE; //new DroneBuilder(true).createDrone();

    // Deprecated: all blocks are generated in main loop
    public final static Vector BLOCKPOS = new Vector(0.0f, 0.489f, -6.9098f);
    public final static Vector COLOR = new Vector(0.0f, 1.0f, 70.0f);
    public final static boolean LOAD_COORDINATES = false;
    public static int numberOfBlocks = 5;
    public final static WorldGenerator wg = new WorldGenerator(numberOfBlocks);
    public final Parser coordinatesParser = new Parser("src/internal/blockCoordinates.txt");
    public final Parser dataParser = new Parser("src/internal/blockData.txt");


    public WorldBuilder() {
        //do nothing
    }

    /**
     * Creates a world with random colourized blocks.
     */
    public World createRandomWorld(String config) throws IOException{
        //Block block1 = new Block(BLOCKPOS);
        //Cube cube1 = new Cube(BLOCKPOS.convertToVector3f(), COLOR.convertToVector3f());
        //block1.setAssocatedCube(cube1);

        World world = new World(World.VISIT_ALL_OBJECTIVE);
    	if (!LOAD_COORDINATES) {
    		world = wg.createWorld();
    	}
    	else {
    		List<Vector> positions = coordinatesParser.getCoordinates();
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
        DRONE = new DroneBuilder(true).createDrone(config);
        world.addWorldObject(DRONE);

        return world;
    }
    
    /**
     * Creates a world with predefined colourized blocks.
     */
    public World createWorld(String config) throws IOException{
        //Block block1 = new Block(BLOCKPOS);
        //Cube cube1 = new Cube(BLOCKPOS.convertToVector3f(), COLOR.convertToVector3f());
        //block1.setAssocatedCube(cube1);

        World world = new World(World.VISIT_ALL_OBJECTIVE);

        List<Vector> data = dataParser.getBlockData();
        for (int i = 0 ; i < data.size(); i += 2) {
        	Vector position = data.get(i);
        	Vector color = data.get(i+1);
        	Block block = new Block(position);
        	Cube cube = new Cube(position.convertToVector3f(), color.convertToVector3f());
        	cube.setSize(1f);
        	block.setAssocatedCube(cube);
        	world.addWorldObject(block);

    	}
        
        //world.addWorldObject(block1);
        DRONE = new DroneBuilder(true).createDrone(config);
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

}