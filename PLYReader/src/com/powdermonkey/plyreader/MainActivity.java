package com.powdermonkey.plyreader;

import java.io.IOException;
import java.io.InputStream;

import com.powdermonkey.common.PLYReader;
import com.powdermonkey.mapping.v3n3t2.V3N3T2PLYMesh;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class MainActivity extends Activity {

	private float previousX;
	private float previousY;
	private float scaling = 0.15f;
	private boolean lastDoubleTouch;
	private float previous2X;
	private float previous2Y;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		this.scaleDetector.onTouchEvent(event);

		float x = event.getX();
		float y = event.getY();

		if (event.getAction() == MotionEvent.ACTION_MOVE && event.getPointerCount() == 1) {
			if (!scaleDetector.isInProgress()) {
				if (renderer != null) {
					float deltaX = (x - previousX) * scaling;
					float deltaY = (y - previousY) * scaling;

					renderer.getCamera().cameraRotate(deltaX, deltaY);
				}
			}
		}
		
		if (event.getPointerCount() > 1) {
			if (lastDoubleTouch) {

				float previousSlopeX = previous2X - previousX;
				float previousSlopeY = previous2Y - previousY;

				float thisSlopeX = event.getX(1) - x;
				float thisSlopeY = event.getY(1) - y;

				float angle1 = (float) Math.atan2(previousSlopeX, previousSlopeY);
				angle1 -= (float) Math.atan2(thisSlopeX, thisSlopeY);
				angle1 = (float) Math.toDegrees(angle1);
				if (angle1 < 20) {
					renderer.getCamera().roll(angle1);
				}
			}
			lastDoubleTouch = true;
			previous2X = event.getX(1);
			previous2Y = event.getY(1);

		} else {
			lastDoubleTouch = false;
		}
		

		previousX = x;
		previousY = y;

		return true;
	}

	private PlySurface glSurface;
	private PlyRenderer renderer;
	private ScaleGestureDetector scaleDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		glSurface = new PlySurface(this);
		setContentView(glSurface);
		scaleDetector = new ScaleGestureDetector(this, new ScaleListener());

		final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
		final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

		if (supportsEs2) {
			// Request an OpenGL ES 2.0 compatible context.
			glSurface.setEGLContextClientVersion(2);
			renderer = new PlyRenderer(this);
			glSurface.setRenderer(renderer);
			InputStream inputStream = getResources().openRawResource(R.raw.test);
			try {
				try {
					Log.i("PLY", "reading PLY file: test.ply");
					PLYReader ply = new PLYReader(inputStream);
					renderer.setPLY(new V3N3T2PLYMesh(ply));
				} finally {
					inputStream.close();
				}
			} catch (IOException e) {
				Log.e("PLY", "Unable to read PLY file");
			}

		} else {
			// Nothing sensible to do if we don't have ES2 renderer
		}
	}

	@Override
	protected void onResume() {
		// The activity must call the GL surface view's onResume() on activity
		// onResume().
		super.onResume();
		glSurface.onResume();
	}

	@Override
	protected void onPause() {
		// The activity must call the GL surface view's onPause() on activity
		// onPause().
		super.onPause();
		glSurface.onPause();
	}

	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			float tmp = detector.getScaleFactor();
			renderer.getCamera().zoom(1 / tmp);
			return true;
		}
	}

}
