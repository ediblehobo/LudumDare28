package si.ediblehobo.ludumdare28;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Array;

public class Board extends Stage {
	private static final int BOMB_STR_CAP = 4;
	
	private final int bombPrice = 2500;
	private final int dynamitePrice = 5000;
	
	private int currentBombPrice = bombPrice;
	private int currentDynamitePrice = dynamitePrice;
	
	/**
	 * in seconds
	 */
	public float dropRate = 1f;
	public float tick = 0;
	
	public int level = 0;
	public int score = 0;
	public int cash = 0;
	private int bombStrength = 1;
	private int dynamiteStrength = 1;
	
	private TextureRegion uiBox;
	private TextureRegion uiBoxBomb;
	private TextureRegion uiBoxDynamite;
	private int[] uiBoxPos = {19, 13};
	
	public Array<Block> blocks;
	public TileData[][] tiles;
	
	public Sound levelUp;
	public Sound hit;
	public Sound broke;
	public Sound powerUp;
	public Sound gameOverAudio;

	public Sound bombHiss;
	public Sound bombExplosion;
	
	private boolean soundPlayed = false;

	private ShapeRenderer sr;
	public BitmapFont font;
	public Label levelUpLabel;
	public boolean levelUpShown = false;

	public Label buyLabel;
	private boolean buyLabelShown = false;
	private boolean[] hasItem = {false, false};
	
	private TextureRegion background;
	
	public Tool tool;
	
	private Label gameOverLabel1;
	private Label gameOverLabel2;
	private boolean gameOver = false;
	private boolean gameOverDisplayed = false;
	
	public Board() {
		setViewport(Util.PLAY_AREA_WIDTH, Util.PLAY_AREA_HEIGHT, true);
		getCamera().translate(-Util.PLAY_SCALE*6, 0, 0);
		Gdx.input.setInputProcessor(this);
		
		addListener(stageInputListener);
		
		tool = new Tool();
		tool.isShown = true;
		
		hit = Gdx.audio.newSound(Gdx.files.internal("audio/sfx_hit.wav"));
		levelUp = Gdx.audio.newSound(Gdx.files.internal("audio/sfx_levelup.wav"));
		broke = Gdx.audio.newSound(Gdx.files.internal("audio/sfx_broke.wav"));
		bombHiss = Gdx.audio.newSound(Gdx.files.internal("audio/sfx_bomb_timer.wav"));
		bombExplosion = Gdx.audio.newSound(Gdx.files.internal("audio/sfx_bomb_explode.wav"));
		powerUp = Gdx.audio.newSound(Gdx.files.internal("audio/sfx_got_item.wav"));
		gameOverAudio = Gdx.audio.newSound(Gdx.files.internal("audio/sfx_game_over.wav"));
		
		background = new TextureRegion(new Texture(Gdx.files.internal("misc/bg.png")), Util.PLAY_AREA_WIDTH, Util.PLAY_AREA_HEIGHT);
		uiBox = new TextureRegion(new Texture(Gdx.files.internal("misc/ui_box.png")), 0, 8, 24, 24);
		uiBoxBomb = Util.getBombTexture(0);
		uiBoxDynamite = new TextureRegion(new Texture(Gdx.files.internal("bomb/dynamite.png")), 8, 8);
		
		sr = new ShapeRenderer();
		font = new BitmapFont(Gdx.files.internal("fonts/slk.fnt"), Gdx.files.internal("fonts/slk.png"), false);
		
		blocks = new Array<Block>();
		tiles = new TileData[25][12];
		for(int i=0;i<tiles.length;i++) {
			for(int j=0;j<tiles[i].length;j++) {
				tiles[i][j] = new TileData();
			}
		}

		BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/slk.fnt"), Gdx.files.internal("fonts/slk.png"), false);
		font.setScale(2f);
		Label.LabelStyle style = new Label.LabelStyle(font, Color.YELLOW);
		levelUpLabel = new Label("+LEVEL UP", style);
		levelUpLabel.setTouchable(Touchable.disabled);
		resetLevelUpLabel();
		
		font = new BitmapFont(Gdx.files.internal("fonts/slk.fnt"), Gdx.files.internal("fonts/slk.png"), false);
		style = new Label.LabelStyle(font, Color.WHITE);
		buyLabel = new Label("", style);
		
		Label help = new Label(Util.HELP, style);
		help.setPosition(-(Util.PLAY_SCALE*5), Util.PLAY_SCALE*2);
		addActor(help);
		
		/*int top = tiles.length;
		addActor(new Block(this, 6, top-1, "green"));
		addActor(new Block(this, 8, top-1, "lblue"));
		addActor(new Block(this, 10, top-1, "green"));
		addActor(new Block(this, 12, top-1, "lblue"));
		addActor(new Block(this, 14, top-1, "lblue"));
		addActor(new Block(this, 16, top-1, "green"));

		//Block b = new Block(13, top-6, "green");
		//b.hold = true;
		//addActor(b);
		
		addActor(new Block(this, 15, top-4, "red"));*/
	}
	
	@Override
	public void addActor(Actor actor) {
		super.addActor(actor);
		if(actor instanceof Block) {
			Block b = (Block) actor;
			if(b.tileY<tiles.length) {
				setOccupying(b, b.tileX-6, b.tileY, (int) b.getWidth()/Util.PLAY_SCALE);
			}
			blocks.add((Block) actor);
		}
	}
	
	/**
	 * width in tile blocks
	 * @param tileX
	 * @param tileY
	 * @param width
	 */
	public void setOccupying(Block b, int tileX, int tileY, int width) {
		for(int i=0;i<width;i++) {
			tiles[tileY][tileX+i].occupyingBlock = b;
		}
	}
	
	private void resetLevelUpLabel() {
		levelUpLabel.setX((Util.PLAY_AREA_WIDTH/2) - (levelUpLabel.getWidth()/2));
		levelUpLabel.setY((Util.PLAY_AREA_WIDTH/2) - (levelUpLabel.getHeight()/2));
		levelUpLabel.setVisible(false);
	}

	@Override
	public void draw() {
		SpriteBatch sb = getSpriteBatch();
		sb.begin();
		sb.draw(background, 0 ,0, Util.PLAY_AREA_WIDTH, Util.PLAY_AREA_HEIGHT);
		sb.draw(uiBox, uiBoxPos[0]*Util.PLAY_SCALE, uiBoxPos[1]*Util.PLAY_SCALE, 24, 24);
		sb.draw(uiBox, uiBoxPos[0]*Util.PLAY_SCALE, uiBoxPos[1]*Util.PLAY_SCALE-32, 24, 24);
		
		sb.draw(uiBoxBomb, uiBoxPos[0]*Util.PLAY_SCALE+8, uiBoxPos[1]*Util.PLAY_SCALE+8, 8, 8);
		sb.draw(uiBoxDynamite, uiBoxPos[0]*Util.PLAY_SCALE+8, uiBoxPos[1]*Util.PLAY_SCALE-32+8, 8, 8);

		String text = "ITEM: ";
		if(hasItem[0]) {
			text = text+"BOMB LV "+(bombStrength-1);
		} else if(hasItem[1]) {
			text = text+"TNT LV"+(dynamiteStrength-1);
		} else {
			text = text+"none";
		}
		
		font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		font.setScale(1.1f);
		
		font.draw(sb, "Score: "+cash+"$", -(Util.PLAY_SCALE*5), Util.PLAY_AREA_HEIGHT-(Util.PLAY_SCALE*2));
		font.draw(sb, text, -(Util.PLAY_SCALE*5), Util.PLAY_AREA_HEIGHT-(Util.PLAY_SCALE*4));
		font.draw(sb, "Level: "+level, Util.PLAY_AREA_WIDTH - (Util.PLAY_SCALE*5), Util.PLAY_AREA_HEIGHT-(Util.PLAY_SCALE*2));
		font.setScale(0.6f);
		font.draw(sb, currentBombPrice+"$ (LV "+bombStrength+")", uiBoxPos[0]*Util.PLAY_SCALE, uiBoxPos[1]*Util.PLAY_SCALE+32);
		font.draw(sb, currentDynamitePrice+"$ (LV "+dynamiteStrength+")", uiBoxPos[0]*Util.PLAY_SCALE, uiBoxPos[1]*Util.PLAY_SCALE);
		sb.end();
		super.draw();
		sb.begin();
		tool.draw(sb, 1.0f);
		sb.end();
		
		sr.setColor(Color.WHITE);
		sr.begin(ShapeType.Line);
		Vector2 dim = stageToScreenCoordinates(new Vector2(getWidth(), getHeight()));
		sr.line(0, 0, dim.x, 0);
		sr.line(0, 0, 0, dim.y);
		sr.line(dim.x, dim.y, dim.x, 0);
		sr.line(dim.x, dim.y, 0, dim.y);
		sr.end();

	}
	
	public void explodeBomb(Bomb bomb) {
		BombExplosion explosion = new BombExplosion(this, bomb.tileX, bomb.tileY);
		bombHiss.stop();
		bombExplosion.play();
		bomb.remove();
		
		//reset prices
		bombStrength = 1;
		dynamiteStrength = 1;
		currentBombPrice = bombPrice;
		currentDynamitePrice = dynamitePrice;
		
		setOccupying(null, bomb.tileX-6, bomb.tileY, (int) bomb.getWidth()/Util.PLAY_SCALE);
		explosion.explode(bomb.level, 0.6f);
	}
	
	@Override
	public void act() {
		super.act();
		Vector2 coords = screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
		int[] tiles = getTileFromPosition((int) coords.x, (int) coords.y);
		if(tiles[0]<6 || tiles[0]>=this.tiles[0].length+6 || gameOver) {
			tool.isShown = false;
		} else {
			tool.isShown = true;
		}
		
		tool.setPosition(coords.x+tool.getWidth()/4, coords.y+tool.getHeight()/4);
		if(tool.isShown && !gameOver) {
			if(Gdx.input.isButtonPressed(0) && tool.getRotation()==45) {
				if(!soundPlayed) {
					hit.play(1.0f);
					soundPlayed = true;
				}
				tool.setRotation(90);
			} else if(!Gdx.input.isButtonPressed(0) && tool.getRotation()==90) {
				soundPlayed = false;
				tool.setRotation(45);
			}
		}
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		if(gameOver) {
			gameOver();
		} else {
			int oldLevel = level;
			level = Util.getLevelFromScore(score);
			if(oldLevel<level) {
				dropRate -= 0.075f;
				tool.damage = level;
				animateLevelUp();
			}
			
			if(tick<dropRate) {
				tick += delta;
			} else {
				dropBlocks();

				int top = Util.PLAY_AREA_HEIGHT/8;
				Random r = new Random();
				int x = r.nextInt(11);
				int y = top-1;
				boolean occupied = false;
				int width = 2;
				for(int j=0;j<width;j++) {
					occupied = isOccupied(x+j, y);
					if(occupied) {
						break;
					}
				}
				if(!occupied) {
					addActor(Util.rndBlock(this, x+6, y));
				} else {
					gameOver = true;
				}
				
				tick = 0;
			}
		}
	}
	
	private void gameOver() {
		gameOver = true;
		
		if(!gameOverDisplayed) {
			gameOverDisplayed = true;
			BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/slk.fnt"), Gdx.files.internal("fonts/slk.png"), false);
			font.setScale(1.5f);
			gameOverLabel1 = new Label("    GAME OVER    \nYOUR SCORE: "+score+"\n(press space to restart)", new Label.LabelStyle(font, Color.RED));
			gameOverLabel2 = new Label("    GAME OVER    \nYOUR SCORE: "+score+"\n(press space to restart)", new Label.LabelStyle(font, Color.BLACK));
			gameOverLabel1.setAlignment(Align.center);
			gameOverLabel2.setAlignment(Align.center);
			gameOverLabel1.setX((Util.PLAY_AREA_WIDTH/2) - (gameOverLabel1.getWidth()/2));
			gameOverLabel1.setY((Util.PLAY_AREA_WIDTH/2) - (gameOverLabel1.getHeight()/2));
			
			gameOverLabel2.setX((Util.PLAY_AREA_WIDTH/2) - (gameOverLabel1.getWidth()/2)+2);
			gameOverLabel2.setY((Util.PLAY_AREA_WIDTH/2) - (gameOverLabel1.getHeight()/2)-2);

			addActor(gameOverLabel2);
			addActor(gameOverLabel1);
			
			gameOverAudio.play(1.0f);
		}
	}
	
	public void animateLevelUp() {
		if(!levelUpShown) {
			addActor(levelUpLabel);
			levelUp.play(1.0f);
			levelUpShown = true;
			levelUpLabel.setVisible(true);
			levelUpLabel.addAction(sequence(parallel(moveTo(levelUpLabel.getX(), levelUpLabel.getY()+48, 1f), fadeOut(1f)), new Action() {
				public boolean act(float delta) {
					levelUpShown = false;
					levelUpLabel.getColor().a = 1.0f;
					resetLevelUpLabel();
					levelUpLabel.remove();
					return true;
				}
			}));
		}
	}
	
	public void animateBuyLabel(Color c, String text) {
		if(!buyLabelShown) {
			buyLabel.setColor(c);
			buyLabel.setText(text);
			addActor(buyLabel);
			buyLabelShown = true;
			buyLabel.setVisible(true);
			buyLabel.addAction(sequence(parallel(moveTo(buyLabel.getX(), buyLabel.getY()+32, 1.5f), fadeOut(1.5f)), new Action() {
				public boolean act(float delta) {
					buyLabelShown = false;
					buyLabel.getColor().a = 1.0f;
					buyLabel.remove();
					return true;
				}
			}));
		}
	}
	
	public void dropBlocks() {
		for(int i=0;i<blocks.size;i++) {
			Actor a = blocks.get(i);
			if(a instanceof Block) {
				Block bl = (Block) a;
				int tileX = bl.tileX - 6;
				int tileY = bl.tileY;
				boolean occupied = false;
				if(tileY>0) {
					int width = (int) (bl.getWidth()/Util.PLAY_SCALE);
					for(int j=0;j<width;j++) {
						occupied = isOccupied(tileX+j, tileY-1);
						if(occupied) {
							break;
						}
					}
				} else {
					occupied = true;
				}
				
				if(!occupied &&  !bl.hold && !bl.pickedUp) {
					setOccupying(null, tileX, tileY, (int) bl.getWidth()/Util.PLAY_SCALE);
					bl.setY(bl.getY()-Util.PLAY_SCALE);
					bl.tileY--;
					setOccupying(bl, tileX, tileY-1, (int) bl.getWidth()/Util.PLAY_SCALE);
				}
			}
		}
	}
	
	public boolean isOccupied(int tileX, int tileY) {
		return tiles[tileY][tileX].occupyingBlock!=null;
	}
	
	public boolean isWithin(int tileX, int tileY) {
		return tileX>=0 && tileX<tiles[0].length && tileY>=0 && tileY<tiles.length;
	}
	
	public Array<Block> getBlocksWithin(int startTileX, int startTileY, int endTileX, int endTileY) {
		Array<Block> blocksWithin = new Array<Block>();
		if(startTileX<0) {
			startTileX = 0;
		} else if(startTileX>=tiles[0].length) {
			startTileX = tiles[0].length;
		}
		
		if(endTileX<0) {
			endTileX = 0;
		} else if(endTileX>tiles[0].length) {
			endTileX = tiles[0].length;
		}
		
		if(startTileY<0) {
			startTileY = 0;
		} else if(startTileY>=tiles.length) {
			startTileY = tiles.length;
		}

		if(endTileY<0) {
			endTileY = 0;
		} else if(endTileY>tiles.length) {
			endTileY = tiles.length;
		}
		
		for(int i=startTileY;i<endTileY;i++) {
			for(int j=startTileX;j<endTileX;j++) {
				if(isOccupied(j, i) && !blocksWithin.contains(tiles[i][j].occupyingBlock, false)) {
					blocksWithin.add(tiles[i][j].occupyingBlock);
				}
			}
		}
		
		return blocksWithin;
	}
	
	public int[] getTileFromPosition(int x, int y) {
		
		int xTile = Util.PLAY_AREA_WIDTH/Util.PLAY_SCALE;
		for(int i=0;i<Util.PLAY_AREA_WIDTH-Util.PLAY_SCALE;i+=Util.PLAY_SCALE) {
			if(x>=i && x<i+Util.PLAY_SCALE) {
				xTile = i/Util.PLAY_SCALE;
				break;
			}
		}

		int yTile = Util.PLAY_AREA_HEIGHT/Util.PLAY_SCALE;
		for(int i=0;i<Util.PLAY_AREA_HEIGHT-Util.PLAY_SCALE;i+=Util.PLAY_SCALE) {
			if(y>=i && y<i+Util.PLAY_SCALE) {
				yTile = i/Util.PLAY_SCALE;
				break;
			}
		}
		
		return new int[] {xTile, yTile};
	}
	
	public void reset() {
		for(int i=0;i<tiles.length;i++) {
			for(int j=0;j<tiles[i].length;j++) {
				tiles[i][j].occupyingBlock = null;
			}
		}
		for(Block b : blocks) {
			b.remove();
		}
		
		blocks.clear();
		
		//reset prices
		bombStrength = 1;
		dynamiteStrength = 1;
		currentBombPrice = bombPrice;
		currentDynamitePrice = dynamitePrice;
		
		tool.damage = 1;
		
		level = 0;
		score = 0;
		cash = 0;
		dropRate = 1f;
		tick = 0;
		
		gameOver = false;
		gameOverDisplayed = false;
		
		gameOverLabel1.remove();
		gameOverLabel2.remove();
	}
	
	public void buyItem(boolean bomb) {
		Vector2 pos = screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
		buyLabel.setX(pos.x);
		buyLabel.setY(pos.y);
		if(bomb) {
			if(cash>currentBombPrice && !hasItem[1] && bombStrength<BOMB_STR_CAP) {
				cash -= currentBombPrice;
				hasItem[0] = true;
				powerUp.play(1.0f);
				
				currentDynamitePrice = dynamitePrice;
				dynamiteStrength = 1;
				
				bombStrength++;
				currentBombPrice = bombPrice * bombStrength;
				String text = "GOT BOMB";
				if(bombStrength>2) {
					text = "UPGRADED BOMB";
				}
				
				animateBuyLabel(new Color(0f, 1f, 0f, 1f), text);
			} else if(hasItem[1]) {
				animateBuyLabel(new Color(1f, 0f, 0f, 1f), "ALREADY GOT ITEM");
			} else if(bombStrength==BOMB_STR_CAP) {
				animateBuyLabel(new Color(1f, 0f, 0f, 1f), "MAX LEVEL REACHED");
			} else {
				animateBuyLabel(new Color(1f, 0f, 0f, 1f), "NOT ENOUGH $");
			}
		} else {
			if(cash>currentDynamitePrice && !hasItem[0] && dynamiteStrength<BOMB_STR_CAP) {
				cash -= currentDynamitePrice;
				hasItem[1] = true;
				powerUp.play(1.0f);

				currentBombPrice = bombPrice;
				bombStrength = 1;
				
				dynamiteStrength++;
				currentDynamitePrice = dynamitePrice * dynamiteStrength;
				String text = "GOT DYNAMITE";
				if(bombStrength>2) {
					text = "UPGRADED DYNAMITE";
				}
				
				animateBuyLabel(new Color(0f, 1f, 0f, 1f), text);
			} else if(hasItem[0]) {
				animateBuyLabel(new Color(1f, 0f, 0f, 1f), "ALREADY GOT ITEM");
			} else if(dynamiteStrength==BOMB_STR_CAP) {
				animateBuyLabel(new Color(1f, 0f, 0f, 1f), "MAX LEVEL REACHED");
			} else {
				animateBuyLabel(new Color(1f, 0f, 0f, 1f), "NOT ENOUGH $");
			}
		}
	}
	
	private InputListener stageInputListener = new InputListener() {

		@Override
		public boolean touchDown(InputEvent event, float x, float y,
				int pointer, int button) {
			/*int[] tiles = getTileFromPosition((int) x,(int) y);
			System.out.println("["+tiles[0]+", "+tiles[1]+"]");
			Array<Block> neighbours = getNeighbours(tiles[0], tiles[1]);
			for(Block b : neighbours) {
				System.out.println("Neighbour at ["+b.tileX+", "+b.tileY+"]");
			}*/
			if(gameOver) {
				event.cancel();
				return true;
			}

			int[] tiles = getTileFromPosition((int) x,(int) y);
			//System.out.println("["+tiles[0]+", "+tiles[1]+"]");
			if(isWithin(tiles[0]-6, tiles[1]) && !isOccupied(tiles[0]-6, tiles[1]) && button==1) {
				if(hasItem[0]) {
					hasItem[0] = false;
					int strength = bombStrength-1;
					if(strength<=0) {
						strength = 1;
					}
					Bomb b = new Bomb(Board.this, tiles[0], tiles[1], bombStrength);
					addActor(b);
					b.place(tiles[0], tiles[1], 2);
				} else if(hasItem[1]) {
					hasItem[1] = false;
					int strength = dynamiteStrength-1;
					if(strength<=0) {
						strength = 1;
					}
					Dynamite d = new Dynamite(Board.this, tiles[0], tiles[1], dynamiteStrength);
					addActor(d);
					d.place(tiles[0], tiles[1], 0);
				}
			} else if(tiles[0]>=uiBoxPos[0] && tiles[0]<uiBoxPos[0]+3 && tiles[1]>=uiBoxPos[1] && tiles[1]<uiBoxPos[1]+3) {
				if(!buyLabelShown && button==0) {
					buyItem(true);
				}
			} else if(tiles[0]>=uiBoxPos[0] && tiles[0]<uiBoxPos[0]+3 && tiles[1]>=uiBoxPos[1]-4 && tiles[1]<uiBoxPos[1]+3-4) {
				if(!buyLabelShown && button==0) {
					buyItem(false);
				}
			}
			return false;
		}

		@Override
		public void touchUp(InputEvent event, float x, float y, int pointer,
				int button) {
			super.touchUp(event, x, y, pointer, button);
		}

		@Override
		public void touchDragged(InputEvent event, float x, float y, int pointer) {
			// TODO Auto-generated method stub
			super.touchDragged(event, x, y, pointer);
		}

		@Override
		public boolean keyDown(InputEvent event, int keycode) {
			if(keycode==Keys.SPACE && gameOver && gameOverDisplayed) {
				reset();
			}
			return super.keyDown(event, keycode);
		}
		
		
	};
}
