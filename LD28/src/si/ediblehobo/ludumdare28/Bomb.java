package si.ediblehobo.ludumdare28;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Timer;

public class Bomb extends Block {
	public int level = 1;
	
	private boolean exploded = false;
	private boolean delayed = false;
	private int delayTextureIndex = 1;
	private float tick;
	
	private static final float ANIM_DELAY_FRAME = 0.01f;
	
	public Bomb(Board board, int tileX, int tileY, int level) {
		super(board, tileX, tileY, null);
		setBounds(tileX*Util.PLAY_SCALE, tileY*Util.PLAY_SCALE, 8, 8);
		this.level = level;
		this.hold = true;
		this.setTouchable(Touchable.disabled);
		
		addListener(inputListener);
		
		texture = Util.getBombTexture(0);
	}
	
	public void place(int tileX, int tileY, float delaySeconds) {
		setBounds(tileX*Util.PLAY_SCALE, tileY*Util.PLAY_SCALE, 8, 8);
		this.tileX = tileX;
		this.tileY = tileY;
		
		if(delaySeconds>0) {
			board.bombHiss.play(0.25f);
			delayed = true;
			this.timer.scheduleTask(bombTask, delaySeconds);
		}
	}
	
	

	@Override
	public void act(float delta) {
		if(tick<ANIM_DELAY_FRAME) {
			tick += delta;
		} else {
			tick = 0;
			if(delayed) {
				texture = Util.getBombTexture(delayTextureIndex);
				if(delayTextureIndex==1) {
					delayTextureIndex++;
				} else {
					delayTextureIndex--;
				}
			}
		}
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		if(!exploded) {
			batch.draw(texture, getX(), getY(), getWidth(), getHeight());
		}
	}
	
	private InputListener inputListener = new InputListener() {

		@Override
		public boolean touchDown(InputEvent event, float x, float y,
				int pointer, int button) {
			if(button==0 && !exploded) {
				exploded = true;
				board.explodeBomb(Bomb.this);
			}
			return true;
		}

		@Override
		public void touchUp(InputEvent event, float x, float y, int pointer,
				int button) {
		}

		@Override
		public void touchDragged(InputEvent event, float x, float y, int pointer) {
		}
		
	};
	
	private Timer.Task bombTask = new Timer.Task() {
		
		@Override
		public void run() {
			if(!exploded) {
				exploded = true;
				board.explodeBomb(Bomb.this);
			}
		}
	};
}
