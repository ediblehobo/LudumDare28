package si.ediblehobo.ludumdare28;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public class Dynamite extends Bomb {

	public Dynamite(Board board, int tileX, int tileY, int level) {
		super(board, tileX, tileY, level);
		this.setTouchable(Touchable.enabled);
		
		texture = new TextureRegion(new Texture(Gdx.files.internal("bomb/dynamite.png")), 8, 8);
	}

}
