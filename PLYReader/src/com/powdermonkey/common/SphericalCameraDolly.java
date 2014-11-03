package com.powdermonkey.common;

import android.opengl.Matrix;
import android.util.Log;

public class SphericalCameraDolly {

	private float[] eye = new float[4];

	private float[] up = new float[4];

	private float[] right = new float[4];

	private float[] out = new float[4];

	private float[] target = new float[4];

	private float[] workMatrix = new float[16];

	private float[] tmp = new float[4];
	
	private float nearField;
	
	private float farField;

	public SphericalCameraDolly() {
		Matrix.setIdentityM(workMatrix, 0);
		eye[0] = 0;
		eye[1] = 0;
		eye[2] = 10;
		eye[3] = 0;

		up[0] = 0;
		up[1] = 1;
		up[2] = 0;
		up[3] = 0;

		right[0] = 1;
		right[1] = 0;
		right[2] = 0;
		right[3] = 0;

		out[0] = 0;
		out[1] = 0;
		out[2] = 1;
		out[3] = 0;

		target[0] = 0;
		target[1] = 0;
		target[2] = 0;
		target[3] = 0;
		
		nearField = Float.MIN_VALUE;
		
		farField = Float.MAX_VALUE;
	}

	public void cameraRotate(float anglex, float angley) {
		Matrix.setIdentityM(workMatrix, 0);
		Matrix.rotateM(workMatrix, 0, -anglex, up[0], up[1], up[2]);
		Matrix.rotateM(workMatrix, 0, -angley, right[0], right[1], right[2]);
		System.arraycopy(up, 0, tmp, 0, 4);
		Matrix.multiplyMV(up, 0, workMatrix, 0, tmp, 0);
		System.arraycopy(right, 0, tmp, 0, 4);
		Matrix.multiplyMV(right, 0, workMatrix, 0, tmp, 0);
		System.arraycopy(out, 0, tmp, 0, 4);
		Matrix.multiplyMV(out, 0, workMatrix, 0, tmp, 0);
		float l = Matrix.length(eye[0], eye[1], eye[2]);
		eye[0] = l * out[0];
		eye[1] = l * out[1];
		eye[2] = l * out[2];
	}

	public void setLookAtM(float[] mat) {
		Matrix.setLookAtM(mat, 0, eye[0], eye[1], eye[2], target[0], target[1], target[2], up[0], up[1], up[2]);
	}

	public void zoom(float f) {
		float l = Matrix.length(eye[0], eye[1], eye[2]);
		l *= f;
		
		if(l > farField)
			l = farField;
		
		if(l < nearField)
			l = nearField;
		
		eye[0] = l * out[0];
		eye[1] = l * out[1];
		eye[2] = l * out[2];
		
		Log.w("Zoom: ", "Zoom: " +  eye[2]);
	}

	public void roll(float angle) {
		Matrix.setIdentityM(workMatrix, 0);
		Matrix.rotateM(workMatrix, 0, angle, out[0], out[1], out[2]);
		System.arraycopy(up, 0, tmp, 0, 4);
		Matrix.multiplyMV(up, 0, workMatrix, 0, tmp, 0);
		System.arraycopy(right, 0, tmp, 0, 4);
		Matrix.multiplyMV(right, 0, workMatrix, 0, tmp, 0);
		System.arraycopy(out, 0, tmp, 0, 4);
		Matrix.multiplyMV(out, 0, workMatrix, 0, tmp, 0);
		float l = Matrix.length(eye[0], eye[1], eye[2]);
		eye[0] = l * out[0];
		eye[1] = l * out[1];
		eye[2] = l * out[2];
	}

	public void applyMatrix(float[] rotation) {
		System.arraycopy(rotation, 0, workMatrix, 0, 16);
		tmp[0] = 0;
		tmp[1] = 1;
		tmp[2] = 0;
		tmp[3] = 0;
		Matrix.multiplyMV(up, 0, workMatrix, 0, tmp, 0);
		
		tmp[0] = 1;
		tmp[1] = 0;
		tmp[2] = 0;
		tmp[3] = 0;
		Matrix.multiplyMV(right, 0, workMatrix, 0, tmp, 0);

		
		tmp[0] = 0;
		tmp[1] = 0;
		tmp[2] = 1;
		tmp[3] = 0;
		Matrix.multiplyMV(out, 0, workMatrix, 0, tmp, 0);
		float l = Matrix.length(eye[0], eye[1], eye[2]);
		eye[0] = l * out[0];
		eye[1] = l * out[1];
		eye[2] = l * out[2];
	}

	public void setOrientation(float[] mOrientation) {
		
	}

	public void setNearField(float f) {
		nearField = f;
	}

	public void setFarField(float f) {
		farField = f;
	}

	public void getEyeVector(float[] result) {
		System.arraycopy(out, 0, result, 0, 3);
	}
	
	public float getDistance() {
		return eye[2];
	}
}
