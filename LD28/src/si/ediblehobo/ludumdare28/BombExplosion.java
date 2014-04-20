package si.ediblehobo.ludumdare28;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Array;

public class BombExplosion extends Actor {
	public int tileX, tileY;
	
	private Board board;
	
	public TextureRegion texture;
	
	private float level;
	private float duration;
	private boolean exploding = false;
	
	public BombExplosion(Board board, int tileX, int tileY) {
		setBounds(tileX*Util.PLAY_SCALE, tileY*Util.PLAY_SCALE, 8, 8);
		this.tileX = tileX;
		this.tileY = tileY;
		this.board = board;

		setOrigin(getWidth()/2, getHeight()/2);
		setVisible(false);
		setTouchable(Touchable.disabled);

		texture = new TextureRegion(new Texture(Gdx.files.internal(Util.bombPath+"explosion_circle.png")), 256, 256);
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		Color oldColor = batch.getColor();
		batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a);
		batch.draw(texture, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
		batch.setColor(oldColor);
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		if(exploding) {
			addAction(sequence(
					parallel(explodeBlocksAction, scaleTo(level+1, level+1, duration, Interpolation.exp10Out), color(new Color(1, 0, 0, 0), duration, Interpolation.exp5Out)), new Action() {

						@Override
						public boolean act(float delta) {
							exploding = false;
							remove();
							return true;
						}
					}));
		}
		/*if(exploding) {
			addAction(parallel(moveTo(getX(), getY()+16, 0.5f), fadeOut(0.5f)));
		}*/
	}
	
	public void explode(int level, float duration) {
		board.addActor(this);
		setVisible(true);
		this.level = level;
		this.duration = duration;
		setColor(255/255, 255/255, 255/255, 1f);
		exploding = true;
	}
	
	private Action explodeBlocksAction = new Action() {
		boolean done = false;
		
		@Override
		public boolean act(float delta) {
			if(!done) {
				Array<Block> blocks = board.getBlocksWithin((int) (tileX-6-level),(int) (tileY-level), 
						(int) (tileX-6+level+1),(int) (tileY+level+1));
				for(Block b : blocks) {
					b.explode(0.1f);
				}
				
				done = true;
			}
			return true;
		}
	};
}
