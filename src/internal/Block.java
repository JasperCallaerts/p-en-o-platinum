package internal;
/**
 * 
 * @author r0637882
 *
 */
public class Block {
	/**
	 * Constructor for a block
	 * @param InitialX the X-Position of the block
	 * @param InitialY the Y-Position of the block
	 * @param InitialZ the Z-Position of the block
	 * @param H the hue of a block
	 * @param S the saturation of a block
	 * @param V the value of a block
	 */
	Block(float InitialX, float InitialY, float InitialZ,
			int H, int S, int V){
		if (!isValidHue(H)){
			throw new IllegalArgumentException();
		}
		if (!isValidValue(V)){
			throw new IllegalArgumentException();
		}
		if (!isValidSaturation(S)){
			throw new IllegalArgumentException();
		}
		this.PositionX = InitialX;
		this.PositionY = InitialY;
		this.PositionZ = InitialZ;
		this.Hue = H;
		this.Saturation = S;
		this.Value = V;
	}
	
	/**
	 * Returns the X-Position of a cube in the world
	 */
	public float getPositionX(){
		return this.PositionX;
	}
	
	/**
	 * Returns the Y-Position of a cube in the world
	 */
	public float getPositionY(){
		return this.PositionY;
	}
	
	/**
	 * Returns the Z-Position of a cube in the world
	 */
	public float getPositionZ(){
		return this.PositionZ;
	}
	
	/**
	 * Returns the Hue of a block
	 */
	public int getHue(){
		return this.Hue;
	}
	
	/**
	 * Returns the Saturation of a block
	 */
	public int getSaturation(){
		return this.Saturation;
	}
	
	/**
	 * Returns the Value of a block
	 */
	public int getValue(){
		return this.Value;
	}
	
	/**
	 * Checks if the given hue is valid (360 >= hue >= 0)
	 */
	public boolean isValidHue(int hue){
		return ((0 <= hue) && (hue <= 360));
	}
	
	/**
	 * Checks if the given saturation is valid (100 >= S >= 0)
	 */
	public boolean isValidSaturation(int sat){
		return ((0 <= sat) && (sat <= 100));
	}
	
	/**
	 * Checks if the given value is valid (100 >= S >= 0)
	 */
	public boolean isValidValue(int val){
		return ((0 <= val) && (val <= 100));
	}
	
	/**
	 * Variables for the position of a block
	 */
	private final float PositionX;
	private final float PositionY;
	private final float PositionZ;
	
	/**
	 * Variables for the HSV (colour) of a block
	 */
	private int Hue;
	private int Saturation;
	private int Value;
}
