package internal.GUI;

import internal.Matrix;
import internal.Vector;

public class Transformation {

    private final Matrix projectionMatrix;

    private final Matrix worldMatrix;
    
    public Transformation() {
        worldMatrix = new Matrix();
        projectionMatrix = new Matrix();
    }

    public final Matrix getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        float aspectRatio = width / height;        
        projectionMatrix.identity();
        projectionMatrix.perspective(fov, aspectRatio, zNear, zFar);
        return projectionMatrix;
    }
    
    public Matrix getWorldMatrix(Vector offset, Vector rotation, float scale) {
        worldMatrix.identity();
        worldMatrix.translate(offset);
        worldMatrix.rotate(rotation);
        worldMatrix.scale(scale);
        return worldMatrix;
    }
}
