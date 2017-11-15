package gui;

import internal.HSVconverter;
import internal.Vector;
import math.Matrix4f;
import math.Vector3f;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Cube{
	
	static float[] positions = new float[]{
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
			-0.5f,  0.5f,  0.5f,
			// V6
			0.5f,  0.5f,  0.5f,
			// V7
			0.5f,  0.5f, -0.5f,
			
			// V8
			0.5f,  0.5f,  0.5f,
			// V9
			0.5f, -0.5f,  0.5f,
			// V10
			0.5f, -0.5f, -0.5f,
			// V11
			0.5f,  0.5f, -0.5f,
			
			// V12
			-0.5f, -0.5f, -0.5f,
			// V13
			-0.5f, -0.5f,  0.5f,
			// V14
			-0.5f,  0.5f,  0.5f,
			// V15
			-0.5f,  0.5f, -0.5f,
			
			// V16
			0.5f, -0.5f,  0.5f,
			// V17
			-0.5f, -0.5f,  0.5f,
			// V18
			-0.5f, -0.5f, -0.5f,
			// V19
			0.5f, -0.5f, -0.5f,
			
			// V20
			0.5f, -0.5f, -0.5f,
			// V21
			-0.5f, -0.5f, -0.5f,
			// V22
			-0.5f,  0.5f, -0.5f,
			// V23
			0.5f,  0.5f, -0.5f,
	};
	private float[] colours;
	static int[] indices = new int[]{
			// Front face
			0, 1, 3, 3, 1, 2,
			// Top Face
			4, 5, 6, 7, 4, 6,
			// Right face
			8, 9, 10, 11, 8, 10,
			// Left face
			12, 13, 14, 12, 14, 15,
			// Bottom face
			16, 17, 18, 16, 18, 19,
			// Back face
			20, 21, 22, 20, 22, 23,
	};
	
	static Graphics g;
	
	private Mesh mesh;
	private Matrix4f modelMatrix = new Matrix4f();
	private Vector3f position = new Vector3f();
	
	static public void setGraphics(Graphics graphics) {
		g = graphics;
	}
	
	public Cube(Vector3f position, Vector3f colour) {
		setColours(colour);
//		float[] col = {colour.x, colour.y, colour.z};
//		setColours(Vector3f.ArrayToVector3f(col));
		
		for (String key: g.windows.keySet()) {
			glfwMakeContextCurrent(g.windows.get(key).getHandler());
			mesh = new Mesh();
			mesh.init(positions, colours, indices);
			glfwMakeContextCurrent(NULL);
		}
		
		this.position = position;
		modelMatrix = Matrix4f.translate(position.x, position.y, position.z);
	}

	public void render() {
		mesh.render();
	}
	
	public void delete() {
		mesh.delete();
	}

	public void update(Vector3f displacement) {
		this.position = this.position.add(displacement);
		modelMatrix = Matrix4f.translate(position.x, position.y, position.z);
	}

	public Vector getPosition(){
		return Vector.vector3fToVector(this.getPos());
	}
	
	public Matrix4f getModelMatrix() {
		return modelMatrix;
	}
	
	public Vector3f getPos() {
		return this.position;
	}
	
	private void setColours(Vector3f colour) {
		Vector3f posY = colour.scale(1.00f);
		Vector3f negY = colour.scale(0.15f);
		Vector3f posX = colour.scale(0.85f);
		Vector3f negX = colour.scale(0.30f);
		Vector3f posZ = colour.scale(0.70f);
		Vector3f negZ = colour.scale(0.45f);
		
		
//		Vector3f posY = Vector3f.ArrayToVector3f(HSVconverter.HSVtoRGB2(colour.x, colour.y, 1.00f * colour.z));
//		Vector3f negY = Vector3f.ArrayToVector3f(HSVconverter.HSVtoRGB2(colour.x, colour.y, 0.15f * colour.z));
//		Vector3f posX = Vector3f.ArrayToVector3f(HSVconverter.HSVtoRGB2(colour.x, colour.y, 0.85f * colour.z));
//		Vector3f negX = Vector3f.ArrayToVector3f(HSVconverter.HSVtoRGB2(colour.x, colour.y, 0.30f * colour.z));
//		Vector3f posZ = Vector3f.ArrayToVector3f(HSVconverter.HSVtoRGB2(colour.x, colour.y, 0.70f * colour.z));
//		Vector3f negZ = Vector3f.ArrayToVector3f(HSVconverter.HSVtoRGB2(colour.x, colour.y, 0.45f * colour.z));
		
		colours = new float[]{
				posZ.x, posZ.y, posZ.z,
				posZ.x, posZ.y, posZ.z,
				posZ.x, posZ.y, posZ.z,
				posZ.x, posZ.y, posZ.z,
				
				posY.x, posY.y, posY.z,
				posY.x, posY.y, posY.z,
				posY.x, posY.y, posY.z,
				posY.x, posY.y, posY.z,
				
				posX.x, posX.y, posX.z,
				posX.x, posX.y, posX.z,
				posX.x, posX.y, posX.z,
				posX.x, posX.y, posX.z,
				
				negX.x, negX.y, negX.z,
				negX.x, negX.y, negX.z,
				negX.x, negX.y, negX.z,
				negX.x, negX.y, negX.z,
				
				negY.x, negY.y, negY.z,
				negY.x, negY.y, negY.z,
				negY.x, negY.y, negY.z,
				negY.x, negY.y, negY.z,
				
				negZ.x, negZ.y, negZ.z,
				negZ.x, negZ.y, negZ.z,
				negZ.x, negZ.y, negZ.z,
				negZ.x, negZ.y, negZ.z,
		};
	}
}
