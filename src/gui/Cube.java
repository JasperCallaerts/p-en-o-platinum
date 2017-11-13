package gui;

import internal.Vector;
import math.Matrix3f;
import math.Matrix4f;
import math.Vector3f;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.system.MemoryUtil.NULL;

import internal.HSVconverter;

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
		setColours(HSVconverter.RGBtoHSV(colour.x, colour.y, colour.z));
		
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
	
	public Matrix4f getAdvancedModelMatrix(Vector3f orientation) {
		
		Matrix3f pitchMatrix = new Matrix3f(new Vector3f(1, 0, 0), new Vector3f(0, (float) Math.cos(orientation.y), (float) -Math.sin(orientation.y)), new Vector3f(0, (float) Math.sin(orientation.y), (float) Math.cos(orientation.y)));
        Matrix3f yawMatrix = new Matrix3f(new Vector3f((float) Math.cos(orientation.x), 0, (float) Math.sin(orientation.x)), new Vector3f(0, 1, 0), new Vector3f((float) -Math.sin(orientation.x), 0, (float) Math.cos(orientation.x)));
        Matrix3f rollMatrix = new Matrix3f(new Vector3f((float) Math.cos(orientation.z), (float) Math.sin(orientation.z), 0), new Vector3f((float) -Math.sin(orientation.z), (float) Math.cos(orientation.z), 0), new Vector3f(0, 0, 1));
        		
        Matrix3f transformationMatrix = yawMatrix.multiply(pitchMatrix).multiply(rollMatrix);
        transformationMatrix = transformationMatrix.transpose();
        
        Vector3f right = transformationMatrix.multiply(new Vector3f(1,0,0));
        Vector3f up = transformationMatrix.multiply(new Vector3f(0, 1,0));
        Vector3f look = transformationMatrix.multiply(new Vector3f(0,0, -1));
        		
		return modelMatrix.multiply(Matrix4f.viewMatrix(right, up, look, new Vector3f()));
	}
	
	public Vector3f getPos() {
		return this.position;
	}
	
	private void setColours(float[] HSV) {
		Vector3f posY = Vector3f.ArrayToVector3f(HSVconverter.HSVtoRGB(HSV[0], HSV[1], 1.00f * HSV[2]));
		Vector3f negY = Vector3f.ArrayToVector3f(HSVconverter.HSVtoRGB(HSV[0], HSV[1], 0.15f * HSV[2]));
		Vector3f posX = Vector3f.ArrayToVector3f(HSVconverter.HSVtoRGB(HSV[0], HSV[1], 0.85f * HSV[2]));
		Vector3f negX = Vector3f.ArrayToVector3f(HSVconverter.HSVtoRGB(HSV[0], HSV[1], 0.30f * HSV[2]));
		Vector3f posZ = Vector3f.ArrayToVector3f(HSVconverter.HSVtoRGB(HSV[0], HSV[1], 0.70f * HSV[2]));
		Vector3f negZ = Vector3f.ArrayToVector3f(HSVconverter.HSVtoRGB(HSV[0], HSV[1], 0.45f * HSV[2]));
		
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
