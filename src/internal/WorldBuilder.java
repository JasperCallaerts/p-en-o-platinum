package internal;

import gui.Cube;

/**
 * Created by Martijn on 26/10/2017.
 * a class to build a world, set the standard configuration here.
 */
public class WorldBuilder {

    public Drone DRONE = new DroneBuilder(true).createDrone();
    public final static Vector BLOCKPOS = new Vector(0.0f, 0.0f, -10.0f);
    public final static Vector COLOR = new Vector(1.0f, 0.0f,0.0f);

    public WorldBuilder(){
        //do nothing
    }

    public World createWorld(){
        Block block1 = new Block(BLOCKPOS);
        Cube cube1 = new Cube(BLOCKPOS.convertToVector3f(), COLOR.convertToVector3f());
        block1.setAssocatedCube(cube1);

        World world = new World();
        world.addWorldObject(block1);
        world.addWorldObject(DRONE);

        return world;
    }

}
