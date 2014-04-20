package si.ediblehobo.ludumdare28;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Util {
	//public static final float FPS = 1/30f;
	public static final int PLAY_SCALE = 8;
	public static final int PLAY_AREA_WIDTH = 24 * PLAY_SCALE;
	public static final int PLAY_AREA_HEIGHT = 24 * PLAY_SCALE;
	
	public static final String[] breakingOverlays = {"breaking0.png", "breaking1.png","breaking2.png", "breaking3.png", "breaking4.png"};
	public static final String breakingOverlaysPath = "blocks/breaking/";
	private static TextureRegion[] breakingOverlaysTexture;

	public static final String[] bombTextureNames = {"bomb0.png", "bomb1.png","bomb2.png", "bomb3.png", "bomb4.png", "bomb5.png", "bomb6.png"};
	public static final String bombPath = "bomb/";
	private static TextureRegion[] bombTextures;

	public static final String HELP = "How to play:\n\nLMB - Break gem\nRMB - Place item\n\nYou can only \nhave one item in \nyour inventory.\nThe game is over\nwhen the board \nis full. Whack \nTNT to blow it.";
	
	
	public static Block rndBlock(Board b, int x, int y) {
		int rand = new Random().nextInt(100);
		
		switch(b.level) {
			case 0:
				return getBlockFromProbability(b, x, y, rand, 50, 50, 0, 0, 0, 0, 0);
			case 1:
				return getBlockFromProbability(b, x, y, rand, 50, 25, 25, 0, 0, 0, 0);
			case 2:
				return getBlockFromProbability(b, x, y, rand, 35, 35, 30, 0, 0, 0, 0);
			case 3:
				return getBlockFromProbability(b, x, y, rand, 30, 30, 30, 10, 0, 0, 0);
			case 4:
				return getBlockFromProbability(b, x, y, rand, 25, 25, 25, 25, 0, 0, 0);
			case 5:
				return getBlockFromProbability(b, x, y, rand, 20, 25, 25, 25, 5, 0, 0);
			case 6:
				return getBlockFromProbability(b, x, y, rand, 20, 20, 20, 25, 15, 0, 0);
			case 7:
				return getBlockFromProbability(b, x, y, rand, 20, 20, 20, 15, 20, 5, 0);
			case 8:
				return getBlockFromProbability(b, x, y, rand, 15, 20, 20, 20, 15, 10, 0);
			case 9:
				return getBlockFromProbability(b, x, y, rand, 10, 15, 15, 15, 20, 15, 0);
			case 10:
				return getBlockFromProbability(b, x, y, rand, 10, 10, 10, 20, 25, 25, 0);
			case 11:
				return getBlockFromProbability(b, x, y, rand, 10, 10, 10, 15, 25, 25, 5);
			case 12:
				return getBlockFromProbability(b, x, y, rand, 5, 10, 10, 15, 20, 20, 20);
			case 13:
				return getBlockFromProbability(b, x, y, rand, 5, 10, 15, 15, 10, 20, 25);
			default:
				//Dirty hack
				return new Block(b, x, y, "yellow");
		}
	}
	
	private static Block getBlockFromProbability(Board b, int x, int y, int rnd, int percBlue, int percGreen, int percYellow, int percRed, int percDBlue, int percPurple, int percGray)  {
		if(rnd<percBlue) {
			return new Block(b, x, y, "lblue");
		} else if(rnd<percBlue+percGreen) {
			return new Block(b, x, y, "green");
		} else if(rnd<percBlue+percGreen+percYellow) {
			return new Block(b, x, y, "yellow");
		} else if(rnd<percBlue+percGreen+percYellow+percRed) {
			return new Block(b, x, y, "red");
		} else if(rnd<percBlue+percGreen+percYellow+percRed+percDBlue) {
			return new Block(b, x, y, "dblue");
		} else if(rnd<percBlue+percGreen+percYellow+percRed+percDBlue+percPurple) {
			return new Block(b, x, y, "purple");
		} else if(rnd<percBlue+percGreen+percYellow+percRed+percDBlue+percPurple+percGray) {
			return new Block(b, x, y, "gray");
		} else {
			//Dirty hack
			return new Block(b, x, y, "yellow");
		}
	}
	
 	public static TextureRegion getOverlayForHealth(float maxHealth, float health) {
		if(breakingOverlaysTexture==null) {
			breakingOverlaysTexture = new TextureRegion[breakingOverlays.length];
			for(int i=0;i<breakingOverlaysTexture.length;i++) {
				breakingOverlaysTexture[i] = new TextureRegion(new Texture(Gdx.files.internal(breakingOverlaysPath+breakingOverlays[i])), PLAY_SCALE*2, PLAY_SCALE);
			}
		}
		
		float perc = health/maxHealth;
		if(perc==1.0f) {
			return null;
		}
		
		//float split = maxHealth/breakingOverlaysTexture.length;
		if(perc>0.8) {
			return breakingOverlaysTexture[0];
		} else if(perc>0.6) {
			return breakingOverlaysTexture[1];
		} else if(perc>0.4) {
			return breakingOverlaysTexture[2];
		} else if(perc>0.2) {
			return breakingOverlaysTexture[3];
		} else {
			return breakingOverlaysTexture[4];
		}
	}
	
	public static int getLevelFromScore(int score) {
		int level = 0;
		
		if(score<5000) {
			level = 0;
		} else if(score<12500) {
			level = 1;
		} else if(score<25000) {
			level = 2;
		} else if(score<50000) {
			level = 3;
		} else if(score<75000) {
			level = 4;
		} else if(score<100000) {
			level = 5;
		} else if(score<125000) {
			level = 6;
		} else if(score<175000) {
			level = 7;
		} else if(score<250000) {
			level = 8;
		} else if(score<350000) {
			level = 9;
		} else if(score<500000) {
			level = 10;
		} else if(score<1000000) {
			level = 11;
		} else {
			level = 12;
		}
		
		return level;
	}
	
	public static TextureRegion getBombTexture(int index) {
		if(bombTextures==null) {
			bombTextures = new TextureRegion[bombTextureNames.length];
			for(int i=0;i<bombTextures.length;i++) {
				bombTextures[i] = new TextureRegion(new Texture(Gdx.files.internal(bombPath+bombTextureNames[i])), PLAY_SCALE, PLAY_SCALE);
			}
		}
		
		return bombTextures[index];
	}
	
	public static TextureRegion[] getBombTextures() {
		if(bombTextures==null) {
			getBombTexture(0);
		}
		
		return bombTextures;
	}
}
