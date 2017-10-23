package internal;

/**
 * 
 * @author Anthony Rathé & Jonathan Craessaerts &..
 *
 */
public class Block extends WorldObject{
	/**
	 * Constructor for a block
	 * @param InitialX the X-Position of the block
	 * @param InitialY the Y-Position of the block
	 * @param InitialZ the Z-Position of the block
	 * @param H the hue of a block
	 * @param S the saturation of a block
	 * @param V the value of a block
	 */
	public Block(float InitialX, float InitialY, float InitialZ,
			float H, float S, float V){
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
	
	public void addVertices() {
		float[] newVertices = new float[]{
				// VO
	            -0.5f,  0.5f,  0.5f,
	            // V1
	            -0.5f, -0.5f,  0.5f,
	            // V2
	             0.5f, -0.5f,  0.5f,
	            // V3
	             0.5f,  0.5f,  0.5f,
	            // V4
	            -0.5f,  0.5f, -0.5f,
	            // V5
	             0.5f,  0.5f, -0.5f,
	            // V6
	            -0.5f, -0.5f, -0.5f,
	            // V7
	             0.5f, -0.5f, -0.5f,
		};
		//vertices = (getVertices(), newVertices);
	}
	
	public void addColours() {
		
	}
	
	public void addIndices() {
		
	}

	@Override
	public void toNextState(float deltaTime){
		//do nothing, cube cannot change state
	}


	/**
	 * returns the position of the cube in vector format
	 */
	public Vector getPosition(){
		return new Vector(this.getPositionX(), this.getPositionY(), this.getPositionZ());
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
	public float getHue(){
		return this.Hue;
	}
	
	/**
	 * Returns the Saturation of a block
	 */
	public float getSaturation(){

		return (float)this.Saturation;

	}
	
	/**
	 * Returns the Value of a block
	 */
	public float getValue(){

		return (float)this.Value;

	}
	
	/**
	 * Checks if the given hue is valid (360 >= hue >= 0)
	 */
	public boolean isValidHue(float hue){
		return ((0 <= hue) && (hue <= 360));
	}
	
	/**
	 * Checks if the given saturation is valid (1 >= S >= 0)
	 */
	public boolean isValidSaturation(float sat){
		return ((0 <= sat) && (sat <= 1));
	}
	
	/**
	 * Checks if the given value is valid (1 >= S >= 0)
	 */
	public boolean isValidValue(float val){
		return ((0 <= val) && (val <= 1));
	}
	
	public static float[] getVertices() {
		return vertices;
	}
	
	public static float[] getColours() {
		return colours;
	}
	
	public static int[] getIndices() {
		return indices;
	}
	
	/**
	 * Implementation of inherited method
	 * @param duration
	 * @author anthonyrathe
	 */
	public void evolve(float duration){
		
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
	private float Hue;
	private float Saturation;
	private float Value;
	
	/**
	 * Data from all blocks.
	 * Will be given to the GUI.
	 */
	static float[] vertices;
	static float[] colours;
	static int[] indices;
}
