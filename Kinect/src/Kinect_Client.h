#define POS 3		 /* number of items per position packet */
#define SEL 1		 /* number of items per selection packet */
#define COM 20       /* number of items per command packet */

#include "Client.h"

#include <string>

class Kinect_Client {
	public:
		Kinect_Client();
		~Kinect_Client();

		void sendData(float x, float y, float depth, int circle, char *action);
		void endClient();

	private:
		float position[POS];
		int selection[SEL];
		char command[COM];

		Client *mylink;
};