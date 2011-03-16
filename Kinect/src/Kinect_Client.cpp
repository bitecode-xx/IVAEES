/* 
	Kinect Client
*/

#define VERBOSE 0      // turn on or off debugging output

#include "Kinect_Client.h"

Kinect_Client::Kinect_Client() {
	char hostname[8192];

	gethostname(hostname, sizeof(hostname));

	int port = 5010;
	int dataport = -1;
	int rev = 1;
	bool bResult = false;

	if (VERBOSE) {
		printf("Client, system '%s', port %d, datagram port %d, reverse bytes %d\n", hostname, port, dataport, rev);
	}
	fflush(NULL);
  	mylink = new Client(port, dataport, hostname, rev, &bResult);

	if (!bResult) {
		printf("Failed to create Client object!\n");
		abort();
	}

	if (VERBOSE) {
		printf("Client, made connection...\n");
	}
	fflush(NULL);

	memset(position, 0, POS);
	selection[0] = 0;
	memset(command, 0, COM);
}

Kinect_Client::~Kinect_Client() {
	delete mylink;
}

void Kinect_Client::sendData(float x, float y, float depth, int select, char *action) {
	position[0] = x;
	position[1] = y;
	position[2] = depth;

	selection[0] = select;

	sprintf_s(command, action);

	if (VERBOSE) {
		printf("Client, sending floats\n");
	}
	fflush(NULL);
	mylink->SendFloats(position, POS);

	if (VERBOSE) {
		printf("Client, sending ints\n");
	}
	fflush(NULL);
	mylink->SendInts(selection, SEL);

	if (VERBOSE) {
		printf("Client, sending string\n");
	}
	fflush(NULL);
	mylink->SendString(command);
}

void Kinect_Client::endClient() {
	printf("Client, closing connection...\n");
	fflush(NULL);
	mylink->Close();

	printf("Client, done...\n");
	fflush(NULL);
	exit(0);
}