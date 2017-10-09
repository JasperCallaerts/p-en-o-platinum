package internal.GUI;

import internal.Matrix;
import internal.Vector;

public class Transformation {

    private final Matrix projectionMatrix;

    private final Matrix viewMatrix;
    
    public Transformation() {
        viewMatrix = new Matrix();
        projectionMatrix = new Matrix();
    }

    public final Matrix getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        float aspectRatio = width / height;        
        projectionMatrix.identity();
        projectionMatrix.perspective(fov, aspectRatio, zNear, zFar);
        return projectionMatrix;
    }
    
    public final Matrix getViewMatrix(Vector right, Vector front, Vector up) {
    	viewMatrix.identity();
    	return viewMatrix;
    }
}
