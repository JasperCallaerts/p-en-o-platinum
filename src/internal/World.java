package internal;
import internal.Drone;

import java.util.HashSet;
import java.util.Set;

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
	
	private Set<WorldObject> objects;
	
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
		return object.canHaveAsWorld(this);
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
	
	
	/**
	 * Method that evolves the world for a given amount of time
	 */
	public void evolve(float duration){
		for(WorldObject object : this.getObjectSet()){
			object.evolve(duration);
		}
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
}
