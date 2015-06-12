package com.smartron.flap.android;


import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidLiveWallpaperService;
import com.smartron.flap.Flap;

public class FlapLiveWallpaper extends AndroidLiveWallpaperService {

	@Override
	public void onCreateApplication() {
		super.onCreateApplication();
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new Flap(), config);
	}
}
