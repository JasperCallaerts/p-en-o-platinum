package internal;
import internal.Drone;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiPredicate;

/**
 * Class for creating a World object.
 * @author anthonyrathe & ...
 */
public class World {
	
	public World(String objective){
		Xsize = 0;	//max groottes initialiseren
		Ysize = 0;
		Zsize = 0;
		this.setObjective(objective);
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
	 * Method that removes all blocks from the world
	 * @author Anthony Rathe
	 */
	public void removeBlocks() {
		for (Block block : this.getBlockSet()) {
			removeWorldObject(block);
		}
	}
	
	/**
	 * Method that returns a block from the world
	 * @return
	 */
	public Block getRandomBlock() {
		for (Block block : this.getBlockSet()) {
			return block;
		}
		return null;
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
	 * Method that returns the drone in the world
	 * @author Anthony Rathe
	 * @throws IOException 
	 */
	public Drone getDrone() throws IOException {
		for (Drone drone : this.getDroneSet()) {
			return drone;
		}
		throw new IOException("No drone was found");
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

		//System.out.println("nb Intervals: " + nbIntervals);

		for(int index = 0; index != nbIntervals; index++) {

			// first check if the goal is reached
			for (Block block : blockSet) {
				for (Drone drone : droneSet) {
					//if the goal is reached, exit the loop by throwing throwing an exception
					if (this.goalReached(block, drone)) {
						//don't forget to notify the autopilot first
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
	 * Checks if the current objective is completed
	 * @param block the selected block
	 * @param drone the selected drone
	 * @return true if and only if the specified goal is reached
	 * @author Martijn Sauwens
	 */
	private boolean goalReached(Block block, Drone drone){
		boolean withinFourMeters = block.getPosition().distanceBetween(drone.getPosition()) <=4.0f;
		switch (this.getObjective()){
			case REACH_CUBE_OBJECTIVE:
				return withinFourMeters;
			case VISIT_ALL_OBJECTIVE:
				//first check if the current cube has been reached, if not we cannot have visited them all
				if(!withinFourMeters)
					return false;

				block.setVisited();

				for(Block block1: this.getBlockSet()){
					if(block1.isVisited()) {
						this.removeWorldObject(block1);
						this.getBlockSet().remove(block);
						//System.out.print("Visited block: " + block1);
					}
				}

				//check if all the cubes are visited
				for(Block currentBlock: this.getBlockSet()){
					// if not return false
					if(!currentBlock.isVisited())
						return false;
				}
				//only if all the cubes are visited the for loop will terminate
				return true;
		}
		return false;
	}

	/**
	 * Getter for the currenr objective
	 * @return a string with the current objective
	 * @author Martijn Sauwens
	 */
	private String getObjective() {
		return objective;
	}

	/**
	 * Setter for the current objective
	 * @param objective the current objective
	 * @author Martijn Sauwens
	 */
	private void setObjective(String objective) {
		if(!canHaveAsObjective(objective))
			throw new IllegalArgumentException(INVALID_OBJECTIVE);
		this.objective = objective;
	}

	/**
	 * Checker if the provided objective is valid
	 * @param objective the objective to check
	 * @author Martijn Sauwens
	 */
	public static boolean canHaveAsObjective(String objective){
		switch(objective){
			case REACH_CUBE_OBJECTIVE:
				return true;
			case VISIT_ALL_OBJECTIVE:
				return true;
			default:
				return false;
		}
	}

	/**
	 * @return an array containing all the possible objectives
	 * @author Martijn Sauwens
	 */
	public String[] getAllPossibleObjectives(){
		return new String[]{VISIT_ALL_OBJECTIVE, REACH_CUBE_OBJECTIVE};
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

	/**
	 * Variable containing the current objective (atm only vist all cubes and reach cube)
	 */
	private String objective;

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
	private final static String INVALID_OBJECTIVE = "The specified objective is not possible";

	/**
	 * Objectives
	 */
	public final static String REACH_CUBE_OBJECTIVE = "reach cube";
	public final static String VISIT_ALL_OBJECTIVE = "visit all the cubes";



}

