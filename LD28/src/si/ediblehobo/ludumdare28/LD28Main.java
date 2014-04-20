package si.ediblehobo.ludumdare28;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;

public class LD28Main implements ApplicationListener {
	
	private Board board;
	
	@Override
	public void create() {
		board = new Board();
		
		Pixmap pixmap = new Pixmap(Gdx.files.internal("misc/cursor_default.png"));
		Gdx.input.setCursorImage(pixmap, 8, 8);
	}

	@Override
	public void dispose() {
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		board.act();
		board.draw();
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
