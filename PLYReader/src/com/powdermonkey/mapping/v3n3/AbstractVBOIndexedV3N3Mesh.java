package com.powdermonkey.mapping.v3n3;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import android.opengl.GLES20;

import com.powdermonkey.mapping.IOpenGLMesh;

/**
 * Simple immutable object derivates must supply three methods.
 * <ol>
 * <li> getVertices to generate a suitable vertex array buffer
 * <li> getIndices an array of indexes into the vertex buffer to create triangles
 * <li> getIndexCount total number of indices
 * </ol?
 * @author pdavis@winbrogroup.com
 *
 */
public abstract class AbstractVBOIndexedV3N3Mesh implements IOpenGLMesh {

    private int[] vboId = new int[1];
    private int[] vboiId = new int[1];
    private int indicesCount;
    private V3N3 sv;
    
    protected abstract FloatBuffer getVertices();
    
    protected abstract IntBuffer getIndices();
    
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
        sv = new V3N3();
        FloatBuffer verticesBuffer = getVertices();
        IntBuffer indicesBuffer = getIndices();
        indicesCount = getIndexCount();

        // Create a new Vertex Buffer Object in memory and select it (bind)
        GLES20.glGenBuffers(0, vboId, 1);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, verticesBuffer.capacity() * V3N3.BYTES_PER_FLOAT, verticesBuffer, GLES20.GL_STATIC_DRAW);


        // Put the position coordinates in attribute list 0
        GLES20.glVertexAttribPointer(0, sv.getPositionCount(), GLES20.GL_FLOAT, false, sv.getStride(),
                sv.getPositionByteOffset());

        // Put the normal coordinates in attribute list 1
        GLES20.glVertexAttribPointer(1, sv.getNormalCount(), GLES20.GL_FLOAT, false, sv.getStride(),
                sv.getNormalByteOffset());
        
        
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        // Create a new VBO for the indices and select it (bind) - INDICES
        GLES20.glGenBuffers(0, vboiId, 1);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vboiId[0]);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer.capacity() * V3N3.BYTES_PER_FLOAT, indicesBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    @Override
    public void draw() {
        // Put the position coordinates in attribute list 0
    	GLES20.glVertexAttribPointer(0, sv.getPositionCount(), GLES20.GL_FLOAT, false, sv.getStride(),
                sv.getPositionByteOffset());

        // Put the normal coordinates in attribute list 1
    	GLES20.glVertexAttribPointer(1, sv.getNormalCount(), GLES20.GL_FLOAT, false, sv.getStride(),
                sv.getNormalByteOffset());
        // Bind to the VAO that has all the information about the vertices
    	GLES20.glEnableVertexAttribArray(0);
    	GLES20.glEnableVertexAttribArray(1);

        // Bind to the index VBO that has all the information about the order of
        // the vertices
    	GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vboiId[0]);

        // Draw the vertices
    	//GLES20.glPolygonMode(GLES20.GL_FRONT_AND_BACK, GLES20.GL_POINT);
    	GLES20.glDrawElements(GLES20.GL_TRIANGLES, indicesCount, GLES20.GL_UNSIGNED_INT, 0);

        // Put everything back to default (deselect)
    	GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    	GLES20.glDisableVertexAttribArray(0);
    	GLES20.glDisableVertexAttribArray(1);
    }
}
