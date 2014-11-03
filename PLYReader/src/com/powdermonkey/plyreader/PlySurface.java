package com.powdermonkey.plyreader;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;

public class PlySurface extends GLSurfaceView {
	
	public PlySurface(Context context) {
		super(context);
		//setEGLConfigChooser(8, 8, 8, 8, 0, 0); 
	    //getHolder().setFormat(PixelFormat.RGBA_8888); 
	}

}
