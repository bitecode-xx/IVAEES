/*
	Engine Server
*/

import java.io.*;

public class Engine_Server {
    static int POS = 3;		/* number of items per position packet */
    static int SEL = 1;		/* number of items per selection packet */
    static int COM = 20;	/* number of items per command packet */
    
	float position[];
	int selection[];
	String command;

	Server mylink;

    public Engine_Server() throws IOException {
    	int port = 5010;
    	int dataport = -1;

    	System.out.println("Server, listening on port " + port + ", datagram port " + dataport);
    	mylink = new Server(port, dataport);

    	position = new float[POS];
    	selection = new int[SEL];
    	
		for (int i = 0; i < POS; i++) {
			position[i] = 0;
		}

		selection[0] = 0;

		command = "";

    	System.out.println("Server, waiting for connection...");
    	mylink.Connect();
    }

    void recvData() throws IOException {
    	System.out.println("Server, receiving floats");
		mylink.RecvFloats(position, POS);

		System.out.println("Server, receiving ints");
		mylink.RecvInts(selection, SEL);

		System.out.println("Server, receiving string");
		command = mylink.RecvString('\n');
    }

    void endServer() throws IOException {
		System.out.println("Server, closing connection...");
		mylink.Close();	

		System.out.println("Server, done...");
    }
}
