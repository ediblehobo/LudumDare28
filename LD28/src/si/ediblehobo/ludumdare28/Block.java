package si.ediblehobo.ludumdare28;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class Block extends Actor {
	
	public TextureRegion texture;
	public TextureRegion breakingOverlay;
	
	public float maxHealth;
	public float health;
	
	public boolean hold = false;
	public boolean pickedUp = false;
	
	public int tileX, tileY;
	
	protected Timer timer;
	private Label scoreLabel;
	private boolean scoreShown = false;
	
	protected Board board;
	public String type;
	
	
	public Block(Board board, int tileX, int tileY, String type) {
		setBounds(tileX*Util.PLAY_SCALE, tileY*Util.PLAY_SCALE, 16, 8);
		this.board = board;
		this.tileX = tileX;
		this.tileY = tileY;
		this.type = type;
		
		if(type!=null) {
			maxHealth = getHealthForType(type);
		}
		health = maxHealth;
		
		addListener(inputListener);
		
		timer = new Timer();
		
		BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/slk.fnt"), Gdx.files.internal("fonts/slk.png"), false);
		Label.LabelStyle style = new Label.LabelStyle(font, Color.YELLOW);
		scoreLabel = new Label("+"+((int)maxHealth*100), style);

		if(type!=null) {
			Texture t = new Texture(Gdx.files.internal("blocks/"+type+".png"));
			t.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
			this.texture = new TextureRegion(t, (int) getWidth(), (int) getHeight());
		}
		this.breakingOverlay = Util.getOverlayForHealth(maxHealth, health);
	}
	
	private float getHealthForType(String type) {
		if(type.equals("lblue")) {
			return 5;
		} else if(type.equals("green")) {
			return 8;
		} else if(type.equals("yellow")) {
			return 12;
		} else if(type.equals("red")) {
			return 17;
		} else if(type.equals("dblue")) {
			return 20;
		} else if(type.equals("purple")) {
			return 25;
		} else if(type.equals("gray")) {
			return 50;
		} else {
			return 1;
		}
	}
	
	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		if(health>0) {
			batch.draw(texture, getX(), getY(), getWidth(), getHeight());
		}
		
		if(health<maxHealth && health>0) {
            Color c = batch.getColor();
            batch.setColor(c.r, c.g, c.b, 0.5f);
			breakingOverlay = Util.getOverlayForHealth(maxHealth, health);
			batch.draw(breakingOverlay, getX(), getY(), getWidth(), getHeight());
            batch.setColor(c.r, c.g, c.b, c.a);
		}
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		updateHealth();
		if(health<=0 && !scoreShown) {
			scoreShown = true;
			scoreLabel.setX(getX());
			scoreLabel.setY(getY());
			scoreLabel.setTouchable(Touchable.disabled);
			board.addActor(scoreLabel);
			scoreLabel.addAction(parallel(moveTo(getX(), getY()+16, 0.5f), fadeOut(0.5f)));
		}
		
		if(scoreLabel.getActions().size==0 && scoreShown) {
			scoreLabel.remove();
			remove();
		}
	}
	
	public void explode(float timer) {
		if(!explodeTask.isScheduled()) {
			this.timer.scheduleTask(explodeTask, 0, timer/5, 5);
		}
	}
	
	//TODO: Set health back up after a while
	private void updateHealth() {
		
	}
	
	private void onHealthDecreased(float decrease, boolean silent) {
		health-=decrease;
		if(health<=0 && !pickedUp) {
			pickedUp = true;
			if(!silent) {
				board.broke.play(1f);
			}
			for(Block b : board.blocks) {
				if(b.equals(this)) {
					board.blocks.removeValue(b, false);
					break;
				}
			}
			
			board.setOccupying(null, tileX-6, tileY, (int) getWidth()/Util.PLAY_SCALE);
			board.score += maxHealth * 100;
			board.cash += maxHealth * 10;
		}
	}
	
	private InputListener inputListener = new InputListener() {
		

		@Override
		public boolean touchDown(InputEvent event, float x, float y,
				int pointer, int button) {
			if(button==0) {
				onHealthDecreased(board.tool.damage, false);
				//explode(0.2f);
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
	
	private Task explodeTask = new Task() {
		
		@Override
		public void run() {
			float decrease = maxHealth/Util.breakingOverlays.length;
			onHealthDecreased(decrease, true);
		}
	};
}
