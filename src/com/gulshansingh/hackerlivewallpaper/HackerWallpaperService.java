package com.gulshansingh.hackerlivewallpaper;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Canvas;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

public class HackerWallpaperService extends WallpaperService {
    
	@Override
	public Engine onCreateEngine() {
		return new HackerWallpaperEngine();
	}
	
	public class HackerWallpaperEngine extends Engine {

		private Handler handler = new Handler();
		private boolean visible = true;
		
		/** The sequences to draw on the screen */
		private List<BitSequence> sequences = new LinkedList<BitSequence>();

		/**
		 * The main runnable that is given to the Handler to draw the animation
		 */
		private Runnable drawRunnable = new Runnable() {
			public void run() {
				Log.d("TAG", "Running");
				draw();
			}
		};
		
		/** Draws all of the bit sequences on the screen */
		private void draw() {
			SurfaceHolder holder = getSurfaceHolder();
			Canvas c = holder.lockCanvas();
			try {
				if (c != null) {
					c.drawARGB(255, 0, 0, 0);

					for (BitSequence sequence : sequences) {
						sequence.draw(c);
					}
				}
			} finally {
				if (c != null) {
					holder.unlockCanvasAndPost(c);
				}
			}

			// Remove the runnable, and only schedule the next run if visible
			handler.removeCallbacks(drawRunnable);
			if (visible) {
				handler.post(drawRunnable);
			}
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			super.onSurfaceCreated(holder);
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			handler.removeCallbacks(drawRunnable);
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);

			handler.removeCallbacks(drawRunnable);
			BitSequence.configure(width, height);

			int numSequences = (int) (width / BitSequence.getWidth());

			// Initialize BitSequences
			sequences.clear();
			for (int i = 0; i < numSequences; i++) {
				sequences.add(new BitSequence(
						(int) (i * BitSequence.getWidth())));
			}

			handler.post(drawRunnable);
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			super.onVisibilityChanged(visible);
			if (visible) {
				handler.post(drawRunnable);
			} else {
				handler.removeCallbacks(drawRunnable);
			}
			this.visible = visible;
		}
	}
}