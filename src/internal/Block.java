package internal;

import java.io.IOException;

import gui.Cube;
import math.Vector3f;

public class Block extends Cube implements WorldObject {
	
	public Block(Vector3f position, Vector3f colour) {
		super(position, colour);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void toNextState(float deltaTime) throws IOException {
		update(new Vector3f());
	}

	@Override
	public Vector getPosition() {
		return Vector3f.toVector(getPos());
	}

}
