package si.ediblehobo.ludumdare28;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "LD28";
		cfg.useGL20 = false;
		cfg.resizable = false;
		cfg.width = 1280;
		cfg.height = 800;
		
		new LwjglApplication(new LD28Main(), cfg);
	}
}
