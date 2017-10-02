package internal;
/**
 * 
 * @author r0637882
 *
 */
public class Block {
	
	Block(int InitialX, int InitialY, int InitialZ){
		PositionX = InitialX;
		PositionY = InitialY;
		PositionZ = InitialZ;
	}
	
	private final int PositionX;
	private final int PositionY;
	private final int PositionZ;
}
