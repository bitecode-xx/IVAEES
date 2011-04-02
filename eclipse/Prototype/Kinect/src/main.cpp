// General headers
#include <stdio.h>
// OpenNI headers
#include <XnOpenNI.h>
// NITE headers
#include <XnVSessionManager.h>
#include <XnvPointDenoiser.h>

// Detector headers
#include <XnVCircleDetector.h>
#include <XnVPushDetector.h>
#include <XnVSteadyDetector.h>
#include <XnVWaveDetector.h>

// Client
#include "Kinect_Client.h"
#include "main.h"

#include <ctime>
#include <iostream>
#include <fstream>
#include <utility>
#include <map>

// xml to initialize OpenNI
#define SAMPLE_XML_FILE "../Data/Sample-Tracking.xml"
#define GESTURE_LOG "../Logs/Gestures.log"

char *action;

int mode = 0;

bool isConnected = true;
bool isGesture = false;
bool circlePP = false;

Kinect_Client *kc;

std::ofstream *output;

std::map<int, XnUInt32> mapID;

xn::DepthGenerator depthGenerator;

xn::SceneMetaData sceneMD;
xn::SceneAnalyzer sceneAnalyzer;

xn::HandsGenerator handsGenerator;
xn::UserGenerator userGenerator;

int lastID = 0;

// Callback for when the focus is in progress
void XN_CALLBACK_TYPE SessionProgress(const XnChar* strFocus, const XnPoint3D& ptFocusPoint, XnFloat fProgress, void* UserCxt) {
	//printf("Session progress (%6.2f,%6.2f,%6.2f) - %6.2f [%s]\n", ptFocusPoint.X, ptFocusPoint.Y, ptFocusPoint.Z, fProgress,  strFocus);
}

// callback for session start
void XN_CALLBACK_TYPE SessionStart(const XnPoint3D& ptFocusPoint, void* UserCxt) {
	printf("Session started\n");

	action = "sessionstart\n";

	logGestures();
}

// Callback for session end
void XN_CALLBACK_TYPE SessionEnd(void* UserCxt) {
	printf("Session ended\n");

	action = "sessionend\n";

	logGestures();
}

// Callback for wave detection
void XN_CALLBACK_TYPE OnCircleCB(XnFloat times, XnBool confident, const XnVCircle* circle, void* cxt) {
	action = "circle\n";
	isGesture = true;

	logGestures();

	if (circlePP) {
		printf("CirclePP\n");
	}
	else {
		printf("Circle\n");
	}
}

// Callback for wave detection
void XN_CALLBACK_TYPE OnNoCircleCB(XnFloat lastValue, XnVCircleDetector::XnVNoCircleReason reason, void* cxt) {
	action = "nocircle\n";
	isGesture = true;
	circlePP = false;

	logGestures();

	printf("No Circle\n");
}

// Callback for wave detection
void XN_CALLBACK_TYPE OnPushCB(XnFloat velocity, XnFloat angle, void* cxt) {
	action = "push\n";
	isGesture = true;

	std::map<int, XnUInt32>::iterator it = mapID.begin();

	printf("map size: %d\n", mapID.size());

	while (it != mapID.end()) {
		printf("first: %d\n", it->first);
		printf("second: %d\n", it->second);
		it++;
	}

	logGestures();

	printf("Push\n");
}

// Callback for wave detection
void XN_CALLBACK_TYPE OnSteadyCB(XnFloat velocity, void* cxt) {
	action = "steady\n";
	isGesture = true;

	logGestures();

	//printf("Steady\n");
}

// Callback for wave detection
void XN_CALLBACK_TYPE OnWaveCB(void* cxt) {
	action = "wave\n";
	isGesture = true;

	logGestures();

	printf("Wave\n");
}

void XN_CALLBACK_TYPE OnPrimaryPointCreateCB(const XnVHandPointContext* pContext, const XnPoint3D& ptFocus, void* cxt) {
	if (mapID.find(1) == mapID.end()) {
		mapID.insert(std::make_pair(1, pContext->nID));
	}
	else {
	}

	printf("map size: %d\n", mapID.size());

	printf("PrimaryPointCreate\n");
}

void XN_CALLBACK_TYPE OnPrimaryPointDestroyCB(XnUInt32 nID, void* cxt) {
	mapID.erase(1);

	printf("map size: %d\n", mapID.size());

	printf("PrimaryPointDestroy\n");
}

void XN_CALLBACK_TYPE OnPointCreateCB(const XnVHandPointContext* pContext, void* cxt) {
	if (mapID.find(1) != mapID.end()) {

		if (mapID.find(2) == mapID.end()) {
			mapID.insert(std::make_pair(2, pContext->nID));
		}
		else {
		}

	}

	printf("map size: %d\n", mapID.size());

	printf("PointCreate\n");
}

void XN_CALLBACK_TYPE OnPointDestroyCB(XnUInt32 nID, void* cxt) {
	if (mapID.find(1)->second != nID) {
		mapID.erase(2);
	}

	printf("map size: %d\n", mapID.size());

	printf("PointDestroy\n");
}

// callback for a new position of any hand
void XN_CALLBACK_TYPE OnPointUpdateCB(const XnVHandPointContext* pContext, void* cxt) {
	int select;
	bool send = true;

	XnPoint3D ptProjective(pContext->ptPosition);

	depthGenerator.ConvertRealWorldToProjective(1, &ptProjective, &ptProjective);

	if (!isGesture) {
	  action = "none\n";
	}

	if (circlePP) {
	  action = "none\n";
	}

	if (mapID.find(1)->second == pContext->nID) {
		select = 1;
	}
	else if (mapID.find(2)->second == pContext->nID) {
		select = 2;
	}
	else {
		send = false;
	}

	if (isConnected && send) {
		kc->sendData(ptProjective.X, ptProjective.Y, ptProjective.Z, select, action);
	}

	if (send == false) {
 		send = true;
	}

	if (strcmp(action, "circle\n") == 0) {
		circlePP = true;
	}

	isGesture = false;
}

/*
  Kinect hand tracking and gesture recognition
*/
int main(int argc, char** argv) {
	xn::Context context;

	XnVSessionManager* sessionManager;

	// Create context
	XnStatus rc = context.InitFromXmlFile(SAMPLE_XML_FILE);

	if (rc != XN_STATUS_OK) {
		printf("Couldn't initialize: %s\n", xnGetStatusString(rc));
		return 1;
	}

	// Setup for depth generation
	rc = context.FindExistingNode(XN_NODE_TYPE_DEPTH, depthGenerator);

	if (rc != XN_STATUS_OK) {
		printf("Depth Generator couldn't initialize: %s\n", xnGetStatusString(rc));
		return 1;
	}

	/*rc = context.FindExistingNode(XN_NODE_TYPE_USER, userGenerator);

	if (rc != XN_STATUS_OK) {
		printf("User Generator couldn't initialize: %s\n", xnGetStatusString(rc));
		return 1;
	}*/

	rc = context.FindExistingNode(XN_NODE_TYPE_SCENE, sceneAnalyzer);

	if (rc != XN_STATUS_OK) {
		printf("Scene Analyzer couldn't initialize: %s\n", xnGetStatusString(rc));
		return 1;
	}

	// Create the Session Manager
	sessionManager = new XnVSessionManager();

	rc = sessionManager->Initialize(&context, "Wave", "RaiseHand");

	if (rc != XN_STATUS_OK) {
		printf("Session Manager couldn't initialize: %s\n", xnGetStatusString(rc));
		delete sessionManager;
		return 1;
	}

	// Initialization done. Start generating
	context.StartGeneratingAll();

	// Register session callbacks
	sessionManager->RegisterSession(NULL, &SessionStart, &SessionEnd, &SessionProgress);

	// Circle Detector
	XnVCircleDetector circle;
	circle.RegisterCircle(NULL, OnCircleCB);
	circle.RegisterNoCircle(NULL, OnNoCircleCB);
	circle.RegisterPointUpdate(NULL, OnPointUpdateCB);

	// Push Detector
	XnVPushDetector push;
	push.RegisterPush(NULL, OnPushCB);
	push.RegisterPointUpdate(NULL, OnPointUpdateCB);

	// Steady Detector
	XnVSteadyDetector steady;
	steady.RegisterSteady(NULL, OnSteadyCB);
	steady.RegisterPointUpdate(NULL, OnPointUpdateCB);

	// Wave Detector
	//XnVWaveDetector wave;
	//wave.RegisterWave(NULL, OnWaveCB);
	//wave.RegisterPointUpdate(NULL, OnPointUpdateCB);

	XnVPointDenoiser denoiser;

	denoiser.AddListener(&circle);
	denoiser.AddListener(&push);
	denoiser.AddListener(&steady);
	denoiser.RegisterPrimaryPointCreate(NULL, OnPrimaryPointCreateCB);
	denoiser.RegisterPrimaryPointDestroy(NULL, OnPrimaryPointDestroyCB);
	denoiser.RegisterPointCreate(NULL, OnPointCreateCB);
	denoiser.RegisterPointDestroy(NULL, OnPointDestroyCB);

	sessionManager->AddListener(&denoiser);

	// Start up client
	if (isConnected) {
		kc = new Kinect_Client();
	}

	// Start up file writer
	output = new std::ofstream(GESTURE_LOG);

	while (1) {
		sceneAnalyzer.GetMetaData(sceneMD);

		context.WaitAndUpdateAll();
		sessionManager->Update(&context);
	}

	// Shut down client
	if (isConnected) {
		kc->endClient();
	}

	output->close();

	delete output;
	
	delete sessionManager;

	context.Shutdown();

	return 0;
}

/*
  Logs each time a gesture is detected using timestamps
*/
void logGestures() {
	time_t seconds;
	struct tm *timeinfo;

	time(&seconds);

	timeinfo = localtime(&seconds);

	*output << asctime(timeinfo) << action << std::endl;

	return;
}