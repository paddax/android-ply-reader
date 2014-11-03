package com.powdermonkey.plyreader;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.powdermonkey.common.PLYReader;
import com.powdermonkey.common.RawResourceReader;
import com.powdermonkey.common.ShaderHelper;
import com.powdermonkey.common.SphericalCameraDolly;
import com.powdermonkey.common.TextureHelper;
import com.powdermonkey.mapping.IOpenGLMesh;
import com.powdermonkey.mapping.v3n3t2.V3N3T2PLYMesh;
import com.powdermonkey.mapping.v3n3t2.V3N3T2;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

/**
 * Renderer view for single Mesh
 * 
 * @author paddax@gmail.com
 *
 */
public class PlyRenderer implements GLSurfaceView.Renderer {

	private SphericalCameraDolly camera;
	private Context context;
	private int program;

	/**
	 * Store the projection matrix. This is used to project the scene onto a 2D
	 * viewport.
	 */
	private float[] projectionMatrix = new float[16];

	/**
	 * Store the view matrix. This can be thought of as our camera. This matrix
	 * transforms world space to eye space; it positions things relative to our
	 * eye.
	 */
	private float[] viewMatrix = new float[16];

	
	/**
	 * Store the model matrix. This matrix is used to move models from object
	 * space (where each model can be thought of being located at the center of
	 * the universe) to world space.
	 */
	private final float[] modelMatrix = new float[16];

	/**
	 * Allocate storage for the final combined Model View matrix. This will be passed into
	 * the shader program.
	 */
	private final float[] mvMatrix = new float[16];
	
	/**
	 * Allocate storage for the final combined matrix. This will be passed into
	 * the shader program.
	 */
	private final float[] mvpMatrix = new float[16];
	
	/**
	 * Used to hold the current position of the light in world space (after
	 * transformation via model matrix).
	 */
	private float[] lightPosInWorldSpace = new float[3];

	private V3N3T2 attrib;
	private IOpenGLMesh mesh;
	private int tex;

	public PlyRenderer(Context context) {
		this.context = context;
		camera = new SphericalCameraDolly();
		camera.setNearField(1);
		camera.setFarField(90);
		attrib = new V3N3T2();
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		
	    String vertexShader = RawResourceReader.readTextFileFromRawResource(context,
				R.raw.v3n3t2vert);
		String fragmentShader = RawResourceReader.readTextFileFromRawResource(context,
				R.raw.v3n3t2frag);

		int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
		int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);

		program = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, new String[] {
				V3N3T2.POSITION_ATTRIBUTE, V3N3T2.NORMAL_ATTRIBUTE, V3N3T2.TEX_ATTRIBUTE });

		// Set the background clear color to black.
		GLES20.glClearColor(0.1f, 0.1f, 0.1f, 0.0f);

		// Enable depth testing
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glUseProgram(program);
		attrib.prepare(program);
		GLES20.glUseProgram(0);
		tex = TextureHelper.loadTexture(context, R.drawable.map_of_world); //.me_256_256);
		Log.i("PLY", "Surface created");
		if(mesh !=null) {
			mesh.attach();
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// Set the OpenGL viewport to the same size as the surface.
		GLES20.glViewport(0, 0, width, height);

		// Create a new perspective projection matrix. The height will stay the
		// same while the width will vary as per aspect ratio.

		float scaleup = 0.5f;
		float ratio = (float) height / width;
		ratio = ratio * scaleup;
		final float left = -scaleup;
		final float right = scaleup;
		float bottom = -ratio;
		final float top = ratio;
		final float near = 2f;
		final float far = 800.0f;

		Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		// Set our per-vertex lighting program.
		GLES20.glUseProgram(program);

		// Set our up vector. This is where our head would be pointing were we
		// holding the camera.
		camera.setLookAtM(viewMatrix);

		// Calculate position of the light. Push into the distance.
		lightPosInWorldSpace[0] = 0;
		lightPosInWorldSpace[1] = 2.5f;
		lightPosInWorldSpace[2] = 3f;
		GLES20.glUniform3f(attrib.lightPosUniform, lightPosInWorldSpace[0], lightPosInWorldSpace[1],
				lightPosInWorldSpace[2]);

		Matrix.setIdentityM(modelMatrix, 0);
		Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, modelMatrix, 0);

		// Pass in the modelview matrix.
		GLES20.glUniformMatrix4fv(attrib.mvMatrixUniform, 1, false, mvMatrix, 0);

		// This multiplies the modelview matrix by the projection matrix,
		// and stores the result in the MVP matrix
		// (which now contains model * view * projection).
		Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvMatrix, 0);

		// Pass in the combined matrix.
		GLES20.glUniformMatrix4fv(attrib.mvpMatrixUniform, 1, false, mvpMatrix, 0);
		//Log.i("PLY", "Draw frame");
		GLES20.glUniform4f(attrib.colorUniform, 0.9f, 0.9f, 0.9f, 0);
		
        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        
        // Bind our texture as a 2d texture to the active texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex);
        
        // Tell the shader which texture unit contains our texture 0=GL_TEXTURE0
        GLES20.glUniform1i(attrib.textureUniform, 0);
        
        // Draw the mesh
		mesh.draw();
		GLES20.glUseProgram(0);
	}

	/**
	 * @param rid
	 */
	public void setPLY(IOpenGLMesh m) {
		mesh = m;
	}

	public SphericalCameraDolly getCamera() {
		return camera;
	}

}
