package com.navmenu;

import java.io.IOException;

import javax.media.opengl.GLProfile;

import com.communication.Engine_Server;

/**
 * 
 * Main thread to initialize the McLuhan program and initialize the 
 * communication to listen for kinect interaction. 
 * 
 * @author Jozef, Brandon, Kyle
 *
 */
public class McLuhanServer {
	private static Engine_Server eserver;
	private static boolean isConnected = true;

	public static void main(String[] args) {
		GLProfile.initSingleton(true);
		
		McLuhanMain ml = new McLuhanMain();
		ml.setVisible(true);
		
		// If the server is going to connect via sockets then wait for Kinect data in a loop
		if (isConnected) {
			openConnection();
		
			try {
				while (true) {
					eserver.recvData();
					
					switch(ml.mode) {
						case 0:
							ml.recvGrabberData(eserver.getPosition()[0], eserver.getPosition()[1], eserver.getPosition()[2],
									 		   eserver.getSelection()[0], eserver.getCommand());
							break;
						case 1:
							ml.recvThemeData(eserver.getPosition()[0], eserver.getPosition()[1], eserver.getPosition()[2],
											 eserver.getSelection()[0], eserver.getCommand());
							break;
						case 2:
							ml.recvPhysicsData(eserver.getPosition()[0], eserver.getPosition()[1], eserver.getPosition()[2],
											   eserver.getSelection()[0], eserver.getCommand());
							break;
						default:
							closeConnection();
							System.exit(-1);
					}
				}
			} catch (IOException ioe) {
				closeConnection();
				System.exit(-1);
			}
			
			closeConnection();
		}
	}
	
	/*
	  Starts up the server and opens the socket connection
	*/
	private static void openConnection() {
		try {
			eserver = new Engine_Server();
		} catch (IOException ioe) {
			System.exit(-1);
		}
	}
	
	/*
	  Shuts down the server and closes the socket connection
	*/
	private static void closeConnection() {
		try {
			eserver.endServer();
		} catch (IOException ioe) {
			System.exit(-1);
		}
	}
}


