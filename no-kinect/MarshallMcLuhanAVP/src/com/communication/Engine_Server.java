package com.communication;
/*
	Engine Server
*/

import java.io.*;

public class Engine_Server {
    static int POS = 3;		/* number of items per position packet */
    static int SEL = 1;		/* number of items per selection packet */
    static int COM = 10;	/* number of items per command packet */
    
	private double position[];
	private int selection[];
	String command;

	Server mylink;

    public Engine_Server() throws IOException {
    	int port = 5010;
    	int dataport = -1;

    	System.out.println("Server, listening on port " + port + ", datagram port " + dataport);
    	mylink = new Server(port, dataport);

    	setPosition(new double[POS]);
    	setSelection(new int[SEL]);

    	System.out.println("Server, waiting for connection...");
    	mylink.Connect();
    }

    public void recvData() throws IOException {
    	System.out.println("Server, receiving doubles");
		mylink.RecvDoubles(getPosition(), POS);

		System.out.println("Server, receiving ints");
		mylink.RecvInts(getSelection(), SEL);

		System.out.println("Server, receiving string");
		command = mylink.RecvString('\n');
		
		System.out.println("pos x: " + getPosition()[0]);
		System.out.println("pos y: " + getPosition()[1]);
		System.out.println("pos d: " + getPosition()[2]);
		System.out.println("pos c: " + getSelection()[0]);
		System.out.println("pos a: " + command);
		
		

		for (int i = 0; i < POS; i++) {
			getPosition()[i] = 0;
		}

		getSelection()[0] = 0;

		command = "";
    }

    public void endServer() throws IOException {
		System.out.println("Server, closing connection...");
		mylink.Close();	

		System.out.println("Server, done...");
    }

	public void setPosition(double position[]) {
		this.position = position;
	}

	public double[] getPosition() {
		return position;
	}

	public void setSelection(int selection[]) {
		this.selection = selection;
	}

	public int[] getSelection() {
		return selection;
	}
}
