package gui;

import internal.Vector;
import math.Matrix4f;
import math.Vector3f;
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
			12, 13, 14, 12, 13, 15,
			// Bottom face
			16, 17, 18, 16, 18, 19,
			// Back face
			20, 21, 22, 20, 22, 23,
	};
	
	private Mesh mesh;
	private ShaderProgram program;
	private Matrix4f modelMatrix = new Matrix4f();
	private Vector3f position = new Vector3f();
	
	public Cube(Vector3f position, Vector3f colour) {
		mesh = new Mesh();
		
		this.position = position;
		
		setColours(HSVconverter.RGBtoHSV(colour.x, colour.y, colour.z));
	}

	public void init(ShaderProgram program) {
		setShaderProgram(program);
		mesh.init(positions, colours, indices);
		modelMatrix = Matrix4f.translate(position.x, position.y, position.z);
	}
	
	public void render() {
		program.setUniform("modelMatrix", modelMatrix);
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
	
	public void setShaderProgram(ShaderProgram program) {
		this.program = program;
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
