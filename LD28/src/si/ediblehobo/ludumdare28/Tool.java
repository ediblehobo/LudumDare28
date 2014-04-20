package si.ediblehobo.ludumdare28;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public class Tool extends Actor {
	public int damage;
	
	public boolean isShown = false;
	private TextureRegion texture;
	
	public Tool() {
		setBounds(0, 0, 16, 16);
		texture = new TextureRegion(new Texture(Gdx.files.internal("misc/hammer.png")), 16, 16);
		setOrigin(getWidth()/2, 0);
		setRotation(45);
		setTouchable(Touchable.disabled);
		
		damage = 1;
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		if(isShown) {
			batch.draw(texture, getX(), getY(), getOriginX(), getOriginY(), 
					getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
		}
	}

	@Override
	public void act(float delta) {
		super.act(delta);
	}
	
	
}
