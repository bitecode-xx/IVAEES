package com.communication;
import java.io.IOException;

public class test {
	public static void main(String args[]) {
		try {
			Engine_Server test = new Engine_Server();
			
			test.recvData();
			
			test.endServer();
		} catch (IOException ioe) {
			System.exit(-1);
		}
	}
}
