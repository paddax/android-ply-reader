package com.powdermonkey.common;

import java.util.Locale;

import android.opengl.Matrix;

public class Vector {

	public static void cross(float[] p1, float[] p2, float[] result) {
		result[0] = p1[1] * p2[2] - p2[1] * p1[2];
		result[1] = p1[2] * p2[0] - p2[2] * p1[0];
		result[2] = p1[0] * p2[1] - p2[0] * p1[1];
	}

	public static void scale(float[] p1, float scale, float[] result) {
		result[0] = p1[0] * scale;
		result[1] = p1[1] * scale;
		result[2] = p1[2] * scale;
	}

	public static void normalise(float[] p1, float[] result) {
		float tmp = Matrix.length(p1[0], p1[1], p1[2]);

		result[0] = p1[0] / tmp;
		result[1] = p1[1] / tmp;
		result[2] = p1[2] / tmp;
	}

	public static float dot(float[] p1, float[] p2) {
		return (p1[0] * p2[0] + p1[1] * p2[1] + p1[2] * p2[2]);
	}
	
	public static void subtract(float[] lhs, float[] rhs, float[] result) {
		result[0] = lhs[0] - rhs[0];
		result[1] = lhs[1] - rhs[1];
		result[2] = lhs[2] - rhs[2];
	}

	public static float length(float[] v1) {
		return (float) Math.sqrt(v1[0] * v1[0] + v1[1] * v1[1] + v1[2] * v1[2]);
	}

	public static void add(float[] lhs, float[] rhs, float[] result) {
		result[0] = lhs[0] + rhs[0];
		result[1] = lhs[1] + rhs[1];
		result[2] = lhs[2] + rhs[2];
	}

	public static String toString(float[] p1) {
        return String.format(Locale.UK, "V [%.3f,%.3f,%.3f]", p1[0], p1[1], p1[2]);
	}

}
