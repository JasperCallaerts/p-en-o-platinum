package internal;
import internal.Drone;

import java.util.List;

/**
 * 
 * @author r0637882
 *
 */
public class World {
	
	public World(){
		Xsize = 0;	//max groottes initialiseren
		Ysize = 0;
		Zsize = 0;
		
	}
	
	private List<Drone> Drones;
	private List<Block> Blocks;
	
	/**
	 * 
	 */
	public void evolve(float time){
		
		
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
	
	
}
