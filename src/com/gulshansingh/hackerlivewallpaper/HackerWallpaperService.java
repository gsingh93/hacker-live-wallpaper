package com.gulshansingh.hackerlivewallpaper;

import java.util.Vector;

import android.graphics.Canvas;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

public class HackerWallpaperService extends WallpaperService {
    
	@Override
	public Engine onCreateEngine() {
		return new HackerWallpaperEngine();
	}
	
	public class HackerWallpaperEngine extends Engine {
		private Handler handler = new Handler();
		private boolean visible = true;
		
		private Runnable drawRunnable = new Runnable() {
			public void run() {
				draw();
			}
		};
		
		private Vector<BitSequence> sequences = new Vector<BitSequence>();

		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			super.onSurfaceCreated(holder);
		}
		
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
			handler.removeCallbacks(drawRunnable);
			if (visible) {
				handler.post(drawRunnable);
			}
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			handler.removeCallbacks(drawRunnable);
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			handler.removeCallbacks(drawRunnable);

			super.onSurfaceChanged(holder, format, width, height);
			BitSequence.configure(width, height);
			final int numSequences = (int) (width / BitSequence.getWidth());

			sequences.ensureCapacity(numSequences);

			// Initialize BitSequences
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