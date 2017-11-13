package internal;


import gui.Cube;

/**
 * Created by Martijn on 26/10/2017.
 * a class to build a world, set the standard configuration here.
 */
public class WorldBuilder {

    public Drone DRONE = new DroneBuilder(true).createDrone();

<<<<<<< HEAD
    // Deprecated: all blocks are generated in main loop
    //public final static Vector BLOCKPOS = new Vector(0.0f, 0.489f, -6.9098f);
=======
    public final static Vector BLOCKPOS = new Vector(0.0f, 3.0f, -20.0f);

>>>>>>> 264df8ad701bb0138a6999e9bae90e95fd6c2851
    public final static Vector COLOR = new Vector(1.0f, 0.0f,0.0f);

    public WorldBuilder(){
        //do nothing
    }

    public World createWorld(){
        //Block block1 = new Block(BLOCKPOS);
        //Cube cube1 = new Cube(BLOCKPOS.convertToVector3f(), COLOR.convertToVector3f());
        //block1.setAssocatedCube(cube1);

        World world = new World();
        //world.addWorldObject(block1);
        world.addWorldObject(DRONE);

        return world;
    }

}
