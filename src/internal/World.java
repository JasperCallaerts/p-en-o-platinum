package internal;
import internal.Drone;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiPredicate;

/**
 * 
 * @author anthonyrathe
 *
 */
public class World {
	
	public World(){
		Xsize = 0;	//max groottes initialiseren
		Ysize = 0;
		Zsize = 0;

	}
	
	private Set<WorldObject> objects = new HashSet<>();
	
	/**
	 * Method that returns a set of all the objects in the world
	 * @author anthonyrathe
	 */
	public Set<WorldObject> getObjectSet(){
		return this.objects;
	}
	
	/**
	 * Method that adds a given worldobject to the world
	 * @param object the object to be added
	 * @author anthonyrathe
	 */
	public void addWorldObject(WorldObject object) throws IllegalArgumentException{
		if (this.canHaveAsObject(object)){
			this.objects.add(object);
		}else{
			throw new IllegalArgumentException(ADD_WORLD_OBJECT_ERROR);
		}
	}
	
	/**
	 * Method that checks if an object can be added to this world
	 * @param object object to perform the check on
	 * @return true if the world can accept the object
	 * @author anthonyrathe
	 */
	public boolean canHaveAsObject(WorldObject object){
		return WorldObject.canHaveAsWorld(this);
	}
	
	/**
	 * Method that removes a given worldobject from the world
	 * @param object the object to be added
	 * @author anthonyrathe
	 */
	public void removeWorldObject(WorldObject object) throws IllegalArgumentException{
		if (this.hasWorldObject(object)){
			this.objects.remove(object);
		}else{
			throw new IllegalArgumentException(WORLD_OBJECT_404);
		}
	}
	
	/**
	 * Method that checks whether the world contains a given object or not
	 * @param object the object to be found in the world
	 * @author anthonyrathe
	 */
	public boolean hasWorldObject(WorldObject object){
		return this.getObjectSet().contains(object);
	}
	
	/**
	 * Method that returns a set of all the objects in the world that belong to a given subclass
	 * @param type the class to which the requested objects should belong
	 * @author anthonyrathe
	 */
	public <type> Set<type> getSet(Class<? extends WorldObject> type){
		Set<type> objects = new HashSet<type>();
		for (WorldObject object : this.getObjectSet()) {
			if (type.isInstance(object)){
				objects.add((type)object);
			}
		}
		return objects;
	}
	
	/**
	 * Method that returns a set containing all the drones in the world
	 * @author anthonyrathe
	 */
	public Set<Drone> getDroneSet(){
		return this.getSet(Drone.class);
	}
	
	/**
	 * Method that returns a set containing all the blocks in the world
	 * @author anthonyrathe
	 */
	public Set<Block> getBlockSet(){
		return this.getSet(Block.class);
	}
	


	//Todo evolve the states of the world, for the given time interval, until the stop criteria is met

	/**
	 * Advances the world state with a given time interval
	 * if the goal has been reached, the world will stop advancing
	 * @param timeInterval the time interval
	 * @throws IllegalArgumentException thrown if the provided time interval is invalid
	 * @author Martijn Sauwens
	 * @throws IOException 
	 */
	public void advanceWorldState(float timeInterval, int nbIntervals) throws IllegalArgumentException, IOException{

		if(!isValidTimeInterval(timeInterval))
			throw new IllegalArgumentException(INVALID_TIME_INTERVAL);

		Set<Block> blockSet = this.getBlockSet();
		Set<Drone> droneSet = this.getDroneSet();
		Set<WorldObject> worldObjectSet = this.getObjectSet();

		System.out.println("nb Intervals: " + nbIntervals);

		for(int index = 0; index != nbIntervals; index++) {

			// first check if the goal is reached
			for (Block block : blockSet) {
				for (Drone drone : droneSet) {
					//if the goal is reached, exit the loop by throwing throwing an exception
					if (this.goalReached(block, drone)) {
						//don't forget to notify the autopilot first
						drone.getAutopilot().simulationEnded();
						throw new SimulationEndedException();
					}
				}
			}
			// if the goal was not reached, set the new state
			for (WorldObject worldObject : worldObjectSet) {
				worldObject.toNextState(timeInterval);
			}
		}

	}

	/**
	 * Checks if the the provided drone and block are within 4meter radius
	 * @param block the block to be checked
	 * @param drone the drone to be checked
	 * @return true if and only if the block and the drone are within 4m radius
	 * @author Martijn Sauwens
	 */
	public boolean goalReached(Block block, Drone drone){
		return block.getPosition().distanceBetween(drone.getPosition()) <=4.0f;
	}

	/**
	 * Checks if the provided time interval is valid
	 * @param timeInterval the time interval to be checked
	 * @return true if and only if the time interval is valid
	 * @author Martijn Sauwens
	 */
	public boolean isValidTimeInterval(float timeInterval){
		return timeInterval > 0.0f;
	}

	private final int Xsize;
	private final int Ysize;
	private final int Zsize;

	public int getXsize(){
		return Xsize;
	}
	public int getYsize() {
		return Ysize;
	}
	public int getZsize(){
		return Zsize;
	}
	
	// Error strings
	private final static String ADD_WORLD_OBJECT_ERROR = "The object couldn't be added to the world";
	private final static String WORLD_OBJECT_404 = "The object couldn't be found";
	private final static String INVALID_TIME_INTERVAL = "The time interval is <= 0, please provide a strictly positive number";


}

