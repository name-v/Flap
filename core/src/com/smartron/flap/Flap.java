package com.smartron.flap;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Flap extends InputAdapter implements ApplicationListener {

	final float TIME_PER_FRAME = 150;
	final float speed = 100;
	final int tolerance = 3;
	final int amplitude = 40;
	final int waveLength = 200;

	private int FRAMES;

	SpriteBatch batch;
	Texture wallpaper;
	Texture shadow;
	private Array<Sprite> butterflies;

	private Vector2 velocity = new Vector2();
	float timeline = 0;
	float bX, bY, angle = 0;
	int pathPoint = 0;
	final int[][] path = new int[5][2];

	@Override
	public void create() {
		int w = Gdx.graphics.getWidth();
		int h = Gdx.graphics.getHeight();

		resetposition();

		path[0][0] = 0;
		path[0][1] = h / 5 + amplitude;
		path[1][0] = w / 4;
		path[1][1] = h / 5;
		path[2][0] = w / 2;
		path[2][1] = h / 5 - amplitude;
		path[3][0] = 3 * w / 4;
		path[3][1] = h / 5;
		path[4][0] = w;
		path[4][1] = h / 5 + amplitude;

		batch = new SpriteBatch();
		wallpaper = new Texture("gfx/wallpaper.png");
		shadow = new Texture("gfx/shadow.png");
		butterflies = new TextureAtlas("gfx/butterfly.sheet").createSprites();
		FRAMES = butterflies.size;
		for (int i = 0; i < FRAMES; i++) {
			butterflies.get(i).setPosition(bX, bY);
			butterflies.get(i).setOriginCenter();
			butterflies.get(i).setRotation(angle);
		}
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render() {
		final float dt = Gdx.graphics.getDeltaTime();
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(wallpaper, 0, 0);
		batch.draw(shadow, 0, 0);
		Sprite key = butterflies.get(getkeyFrame(dt));
		updateAndDraw(key, dt);
		batch.end();
	}

	private void updateAndDraw(Sprite sprite, float dt) {
		int width = Gdx.graphics.getWidth();
		if (bX > width)
			return;
		int slope = pathPoint % 2 == 0 ? -1 : 1;
		angle = slope
				* (float) Math.atan2(path[pathPoint][0] - sprite.getX(),
						path[pathPoint][1] - sprite.getY());
		velocity.set((float) Math.cos(angle) * speed, (float) Math.sin(angle)
				* speed);
		bX -= velocity.x * dt;
		bY -= velocity.y * dt;
		Gdx.app.log("Flap", " updated position x = " + bX + " y = " + bY
				+ " angle  = " + MathUtils.radiansToDegrees * angle);
		for (int k = 0; k < butterflies.size; k++) {
			butterflies.get(k).setPosition(bX, bY);
			butterflies.get(k).setRotation(
					MathUtils.radiansToDegrees * angle + 90);
		}
		if (isWaypointReached()) {
			if (pathPoint + 1 >= path.length)
				pathPoint = 0;
			else
				pathPoint++;
		}
		sprite.draw(batch);
	}

	private boolean isWaypointReached() {
		return path[pathPoint][0] - bX <= speed / tolerance
				* Gdx.graphics.getDeltaTime()
				&& path[pathPoint][1] - bY <= speed / tolerance
						* Gdx.graphics.getDeltaTime();
	}

	private int getkeyFrame(float dt) {
		timeline += dt * 1000;
		if (timeline > FRAMES * TIME_PER_FRAME) {
			timeline = 0;
			return 0;
		}
		return (int) (timeline / TIME_PER_FRAME);
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
		resetposition();
	}

	@Override
	public void dispose() {
		shadow.dispose();
		wallpaper.dispose();
		batch.dispose();
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (bX < Gdx.graphics.getWidth())
			return false;
		resetposition();
		pathPoint = 0;
		for (int k = 0; k < butterflies.size; k++) {
			butterflies.get(k).setPosition(bX, bY);
			butterflies.get(k).setRotation(-45);
		}
		return super.touchUp(screenX, screenY, pointer, button);
	}

	private void resetposition() {
		bX = -Gdx.graphics.getWidth() / 4;
		bY = Gdx.graphics.getHeight() / 5;
		angle = -45;
	}
}
