package edu.ttp.gengame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import edu.ttp.gengame.Game;

class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Gen Game";
		config.width = 800;
		config.height = 400;
//		new LwjglApplication(new Game(), config);
	}
}
