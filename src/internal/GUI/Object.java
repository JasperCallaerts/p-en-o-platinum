package internal.GUI;

import internal.Vector;

public class Object {

    private final Mesh mesh;
    
    private Vector position;
    
    private float scale;

    private Vector rotation;

    public Object(Mesh mesh) {
        this.mesh = mesh;
        position = new Vector(0, 0, 0);
        scale = 1;
        rotation = new Vector(0, 0, 0);
    }

    public Vector getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        this.position = new Vector(x, y, z);
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Vector getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
    	this.rotation = new Vector(x, y, z);
    }
    
    public Mesh getMesh() {
        return mesh;
    }
}
