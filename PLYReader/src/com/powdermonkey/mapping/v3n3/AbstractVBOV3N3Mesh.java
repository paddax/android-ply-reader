package com.powdermonkey.mapping.v3n3;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;

import com.powdermonkey.mapping.IOpenGLMesh;

public abstract class AbstractVBOV3N3Mesh implements IOpenGLMesh {

    protected int[] vboId = new int[1];
    V3N3 sv = new V3N3();

    private FloatBuffer vertexData;
    private int pcount;

    protected abstract void generateVertexData();
    
    @Override
    public void attach() {
        generateVertexData();
        vertexData.flip();

        GLES20.glGenBuffers(0, vboId, 1);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexData.capacity() * V3N3.BYTES_PER_FLOAT, vertexData, GLES20.GL_STATIC_DRAW);

        // Put the position coordinates in attribute list 0
        GLES20.glVertexAttribPointer(0, sv.getPositionCount(), GLES20.GL_FLOAT, false, sv.getStride(),
                sv.getPositionByteOffset());

        // Put the normal coordinates in attribute list 1
        GLES20.glVertexAttribPointer(1, sv.getNormalCount(), GLES20.GL_FLOAT, false, sv.getStride(),
                sv.getNormalByteOffset());

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }
    
    protected void drawPrepare() {
        // Bind to the VAO that has all the information about the quad vertices
    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId[0]);
    	GLES20.glVertexAttribPointer(0, sv.getPositionCount(), GLES20.GL_FLOAT, false, sv.getStride(),
                sv.getPositionByteOffset());

        // Put the normal coordinates in attribute list 1
    	GLES20.glVertexAttribPointer(1, sv.getNormalCount(), GLES20.GL_FLOAT, false, sv.getStride(),
                sv.getNormalByteOffset());
    	GLES20.glEnableVertexAttribArray(0);
    	GLES20.glEnableVertexAttribArray(1);
    }
    
    protected void drawComplete() {
        // Put everything back to default (deselect)
    	GLES20.glDisableVertexAttribArray(0);
    	GLES20.glDisableVertexAttribArray(1);
        //GL30.glBindVertexArray(0);
    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    @Override
    public void detach() {
        // Disable the VBO index from the VAO attributes list
    	GLES20.glDisableVertexAttribArray(0);
    	GLES20.glDisableVertexAttribArray(1);

        // Delete the VBO
    	GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		if (vboId[0] > 0) {
			GLES20.glDeleteBuffers(vboId.length, vboId, 0);
			vboId[0] = 0;
		}
    }
    
    protected int getStride() {
        return sv.getStride();
    }
    
    protected void setVertexData(int size) {
        pcount = 0;
        vertexData = ByteBuffer.allocateDirect(size * 12 * 2).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }
    
    protected int addPoint(float[] p1, float[] n1) {
        this.vertexData.put(p1[0]);
        this.vertexData.put(p1[1]);
        this.vertexData.put(p1[2]);

        this.vertexData.put(-n1[0]);
        this.vertexData.put(-n1[1]);
        this.vertexData.put(-n1[2]);
        return pcount++;
    }
    
}
