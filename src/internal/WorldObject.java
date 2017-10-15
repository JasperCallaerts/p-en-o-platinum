package internal;
/**
 * 
 * @author anthonyrathe
 *
 */
public abstract class WorldObject {
	public abstract void evolve(float duration);
	public boolean canHaveAsWorld(World world){
		return true;
	};
}
