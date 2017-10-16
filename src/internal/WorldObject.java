package internal;

import java.io.IOException;

/**
 * 
 * @author anthonyrathe
 *
 */
public abstract class WorldObject {
	public abstract void evolve(float duration) throws IOException;
	public boolean canHaveAsWorld(World world){
		return true;
	};
}
