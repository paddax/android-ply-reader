package com.powdermonkey.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PLYReader {

	private int vertexCount;
	private int faceCount;
	private float[] vertices;
	private short[] faces;

	public PLYReader(InputStream is) throws IOException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		try {

			String nl = reader.readLine();
			if (!nl.equals("ply"))
				throw new IOException("Not a PLY file");

			nl = reader.readLine();
			header: while (nl != null) {
				String[] parts = nl.split(" ");
				if (parts.length > 0) {
					if (parts[0].equals("format")) {
						if (!parts[1].equals("ascii"))
							throw new IOException("PLY reader can only read ascii format");
					} else if (parts[0].equals("property")) {
						// currently not parsed, it is currently assumed the
						// values are x,y,z,nx,ny,nz with optional s,t
					} else if (parts[0].equals("element")) {
						if (parts[1].equals("vertex")) {
							vertexCount = Integer.parseInt(parts[2]);
							System.out.println("Vertex count: " + vertexCount);
						} else if (parts[1].equals("face")) {
							faceCount = Integer.parseInt(parts[2]);
							System.out.println("Face count: " + faceCount);
						}
					} else if (parts[0].equals("end_header")) {
						break header;
					}
				}
				nl = reader.readLine();
			}
			vertices = new float[vertexCount * 8];
			for (int i = 0; i < vertexCount; i++) {
				nl = reader.readLine();
				if (nl == null)
					throw new IOException("Unexpected end of file at vertex: " + i);
				String[] parts = nl.split(" ");
				vertices[i * 8 + 0] = Float.parseFloat(parts[0]);
				vertices[i * 8 + 1] = Float.parseFloat(parts[1]);
				vertices[i * 8 + 2] = Float.parseFloat(parts[2]);
				vertices[i * 8 + 3] = Float.parseFloat(parts[3]);
				vertices[i * 8 + 4] = Float.parseFloat(parts[4]);
				vertices[i * 8 + 5] = Float.parseFloat(parts[5]);
				
				if(parts.length > 6) {
					vertices[i * 8 + 6] = Float.parseFloat(parts[6]);
					vertices[i * 8 + 7] = Float.parseFloat(parts[7]);
				}
			}
			faces = new short[faceCount * 3];
			for (int i = 0; i < faceCount; i++) {
				nl = reader.readLine();
				if (nl == null)
					throw new IOException("Unexpected end of file at vertex: " + i);
				String[] parts = nl.split(" ");
				if (parts.length != 4)
					throw new IOException("Only capable of reading triangle data at side: " + i);
				faces[i * 3 + 0] = Short.parseShort(parts[1]);
				faces[i * 3 + 1] = Short.parseShort(parts[2]);
				faces[i * 3 + 2] = Short.parseShort(parts[3]);
			}
		} finally {
			reader.close();
		}
	}

	public static void main(String[] args) {
		try {
			new PLYReader(new FileInputStream(new File("test.ply")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getVertexCount() {
		return this.vertexCount;
	}
	
	public int getFaceCount() {
		return this.faceCount;
	}
	
	public float[] getVertices() {
		return vertices;
	}
	
	public short[] getIndices() {
		return faces;
	}
}
