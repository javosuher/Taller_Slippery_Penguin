package com.me.slipperyPenguin;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Slippery Penguin";
		cfg.useGL20 = true;
		cfg.width = 800;
		cfg.height = 400;
		
		new LwjglApplication(new slipperyPenguin(), cfg);
	}
}
