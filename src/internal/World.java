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
	
	World(){
		
	}
	
	private Set<WorldObject> objects;
	
	/**
	 * Method that returns a set of all the objects in the world
	 */
	public Set<WorldObject> getObjectSet(){
		return this.objects;
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
	
}
