package internal.GUI;

import internal.Matrix;
import static org.lwjgl.opengl.GL11.*;

public class Renderer {

/**
 * Field of View in Radians
 */
private static final float FOV = (float) Math.toRadians(60.0f);

private static final float Z_NEAR = 0.01f;

private static final float Z_FAR = 1000.f;

private final Transformation transformation;

private ShaderProgram shaderProgram;

public Renderer() {
    transformation = new Transformation();
}

public void init(Window window) throws Exception {
    // Create shader
    shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(
        		"#version 330\n"

        		+ "layout (location=0) in vec3 position;\n"
        		+ "layout (location=1) in vec3 inColor;\n"

        		+ "out vec3 Color;\n"

        		+ "uniform mat4 worldMatrix;\n"
        		+ "uniform mat4 projectionMatrix;\n"

        		+ "void main()\n"
        		+ "{\n"
        		+ "    gl_Position = projectionMatrix * worldMatrix * vec4(position, 1.0);\n"
        		+ "    Color = inColor;\n"
        		+ "}\n"
		    );
        shaderProgram.createFragmentShader(
        		"#version 330 core\n"

        		+ "in vec3 Color;\n"

    	        + "out vec4 exColor;\n"

    	        + "void main()\n"
    	        + "{\n"
    	        + "    exColor = vec4(Color, 1.0f);\n"
    	        + "}\n"
    			    );
            shaderProgram.link();
            
            // Create uniforms for world and projection matrices
            shaderProgram.createUniform("projectionMatrix");
            shaderProgram.createUniform("worldMatrix");
        }

        public void clear() {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        }

        public void render(Window window, Mesh mesh) {
            clear();

            if ( window.isResized() ) {
                glViewport(0, 0, window.getWidth(), window.getHeight());
                window.setResized(false);
            }

            shaderProgram.bind();
            
            // Update projection Matrix
            Matrix projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
            shaderProgram.setUniform("projectionMatrix", projectionMatrix);
            
            
            // Set world matrix for this mesh
            Matrix worldMatrix = new Matrix();
            worldMatrix.identity();
            shaderProgram.setUniform("worldMatrix", worldMatrix);
            // Render the mesh 
            mesh.render();


            shaderProgram.unbind();
        }

        public void cleanup() {
            if (shaderProgram != null) {
                shaderProgram.cleanup();
            }
        }
    }