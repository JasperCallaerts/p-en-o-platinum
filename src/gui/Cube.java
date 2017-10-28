package gui;

import math.Matrix4f;
import math.Vector3f;

public class Cube {
	
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
			0.5f,  0.5f, -0.5f,
			// V6
			-0.5f, -0.5f, -0.5f,
			// V7
			0.5f, -0.5f, -0.5f
	};
	private float[] colours;
	static int[] indices = new int[]{
			// Front face
			0, 1, 3, 3, 1, 2,
			// Top Face
			4, 0, 3, 5, 4, 3,
			// Right face
			3, 2, 7, 5, 3, 7,
			// Left face
			6, 1, 0, 6, 0, 4,
			// Bottom face
			2, 1, 6, 2, 6, 7,
			// Back face
			7, 6, 4, 7, 4, 5,
	};
	
	private Mesh mesh;
	private ShaderProgram program;
	private Matrix4f modelMatrix = new Matrix4f();
	private Vector3f position = new Vector3f();
	private Vector3f colour = new Vector3f();
	
	public Cube(ShaderProgram program, Vector3f position, Vector3f colour) {
		mesh = new Mesh();
		this.program = program;
		
		this.position = position;
		this.colour  = colour;
		colours = new float[]{
					colour.x, colour.y, colour.z,
					colour.x, colour.y, colour.z,
					colour.x, colour.y, colour.z,
					colour.x, colour.y, colour.z,
					colour.x, colour.y, colour.z,
					colour.x, colour.y, colour.z,
					colour.x, colour.y, colour.z,
					colour.x, colour.y, colour.z,
			};
	}

	public void init() {
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
	
	public Vector3f getPostition() {
		return this.position;
	}
}
