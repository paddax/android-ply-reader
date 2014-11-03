package com.powdermonkey.mapping.v3n3t2;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;

import com.powdermonkey.mapping.IOpenGLMesh;

/**
 * Simple immutable object derivatives must supply three methods.
 * <ol>
 * <li>getVertices to generate a suitable vertex array buffer
 * <li>getIndices an array of indexes into the vertex buffer to create triangles
 * <li>getIndexCount total number of indices
 * </ol
 * ?
 * 
 * @author pdavis@winbrogroup.com
 * 
 */
public abstract class AbstractVBOIndexedV3N3T2Mesh implements IOpenGLMesh {

	private int[] vboId = new int[1];
	private int[] vboiId = new int[1];
	private int indicesCount;

	protected abstract FloatBuffer getVertices();

	protected abstract ShortBuffer getIndices();

	protected abstract int getIndexCount();

	@Override
	public void detach() {
		// Disable the VBO index from the VAO attributes list
		GLES20.glDisableVertexAttribArray(0);
		GLES20.glDisableVertexAttribArray(1);

		// Delete the vertex VBO
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glDeleteBuffers(vboId.length, vboId, 0);

		// Delete the index VBO
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		GLES20.glDeleteBuffers(vboiId.length, vboiId, 0);

	}

	@Override
	public void attach() {
		FloatBuffer verticesBuffer = getVertices();
		ShortBuffer indicesBuffer = getIndices();
		indicesCount = getIndexCount();

		// Create a new Vertex Buffer Object in memory and select it (bind)
		GLES20.glGenBuffers(1, vboId, 0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, verticesBuffer.capacity() * V3N3T2.BYTES_PER_FLOAT, verticesBuffer,
				GLES20.GL_STATIC_DRAW);

		// Put the position coordinates in attribute list 0
		GLES20.glVertexAttribPointer(0, V3N3T2.POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, V3N3T2.STRIDE,
				V3N3T2.POSITION_DATA_OFFSET);

		// Put the normal coordinates in attribute list 1
		GLES20.glVertexAttribPointer(1, V3N3T2.NORMAL_DATA_SIZE, GLES20.GL_FLOAT, false, V3N3T2.STRIDE,
				V3N3T2.NORMAL_DATA_OFFSET);

		// Put the texture coordinates in attribute list 2
		GLES20.glVertexAttribPointer(2, V3N3T2.TEXTURE_COORDINATE_DATA_SIZE, GLES20.GL_FLOAT, false, V3N3T2.STRIDE,
				V3N3T2.TEXTURE_COORDINATE_DATA_OFFSET);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

		// Create a new VBO for the indices and select it (bind) - INDICES
		GLES20.glGenBuffers(1, vboiId, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vboiId[0]);
		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.capacity() * V3N3T2.BYTES_PER_INT,
				indicesBuffer, GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	/**
	 * The attribute values for position, normal and texture are hard-coded to
	 * 0,1 and 2. This is potentially a bad guess.
	 */
	@Override
	public void draw() {

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId[0]);
		// Put the position coordinates in attribute list 0
		GLES20.glVertexAttribPointer(0, V3N3T2.POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, V3N3T2.STRIDE,
				V3N3T2.POSITION_DATA_OFFSET);

		// Put the normal coordinates in attribute list 1
		GLES20.glVertexAttribPointer(1, V3N3T2.NORMAL_DATA_SIZE, GLES20.GL_FLOAT, false, V3N3T2.STRIDE,
				V3N3T2.NORMAL_DATA_OFFSET);

		// Put the texture coordinates in attribute list 2
		GLES20.glVertexAttribPointer(2, V3N3T2.TEXTURE_COORDINATE_DATA_SIZE, GLES20.GL_FLOAT, false, V3N3T2.STRIDE,
				V3N3T2.TEXTURE_COORDINATE_DATA_OFFSET);

		// Bind to the VAO that has all the information about the vertices
		GLES20.glEnableVertexAttribArray(0);
		GLES20.glEnableVertexAttribArray(1);
		GLES20.glEnableVertexAttribArray(2);

		// Bind to the index VBO that has all the information about the order of
		// the vertices
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vboiId[0]);

		// Draw the vertices
		// GLES20.glPolygonMode(GLES20.GL_FRONT_AND_BACK, GLES20.GL_POINT);
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, indicesCount, GLES20.GL_UNSIGNED_SHORT, 0);
		// GLES20.glDrawElements(GLES20.GL_POINTS, indicesCount,
		// GLES20.GL_UNSIGNED_SHORT, 0);

		// Put everything back to default (deselect)
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		GLES20.glDisableVertexAttribArray(0);
		GLES20.glDisableVertexAttribArray(1);
		GLES20.glDisableVertexAttribArray(2);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	}
}
