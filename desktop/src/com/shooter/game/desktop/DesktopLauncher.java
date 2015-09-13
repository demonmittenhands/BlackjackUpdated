package com.shooter.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.shooter.game.Blackjack;

public class DesktopLauncher {
	public static int SCREEN_HEIGHT = 451;
	public static int SCREEN_WIDTH = 1096;
	
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "JUST BlackJack '98";
		config.height = 451;
        config.width = 1096;
        config.resizable = false;
		new LwjglApplication(new Blackjack(), config);
	}
}
