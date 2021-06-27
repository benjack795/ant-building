package com.mygdx.leptosim.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;


public class DesktopLauncher {


	//This class contains the main method for running a
	//libGDX desktop application.
	public static void main (String[] arg) {
		//setting up parameters for the window
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "antSim";
		config.width = SimMain.WIDTH;
		config.height = SimMain.HEIGHT;
		config.forceExit = false;
		config.addIcon("anticon.png", Files.FileType.Internal);
		//initialise the main ScreenHandler class
		new LwjglApplication(new ScreenHandler(), config);

	}
}
