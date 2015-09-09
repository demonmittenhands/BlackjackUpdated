package com.shooter.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.shooter.game.Blackjack;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "JUST BlackJack '98";
		config.height = 451;
        config.width = 1096;
		new LwjglApplication(new Blackjack(), config);
	}
}
