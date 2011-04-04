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

// Modify functionality
XnBool isConnected = true;
XnBool isRecording = true;

// Don't modify
XnBool record = false;
XnBool inSession = false;
XnBool isGesture = false;
XnBool circlePP = false;

Kinect_Client *kc;

std::ofstream *output;

std::map<int, XnUInt32> mapID;

XnStatus rc;
xn::Context context;

xn::DepthMetaData depthMD;
xn::DepthGenerator depthGenerator;

xn::SceneMetaData sceneMD;
xn::SceneAnalyzer sceneAnalyzer;

xn::Recorder *recorder;

//xn::ImageGenerator imageGenerator;

int lastID = 0;

// Callback for when the focus is in progress
void XN_CALLBACK_TYPE SessionProgress(const XnChar* strFocus, const XnPoint3D& ptFocusPoint, XnFloat fProgress, void* UserCxt) {
}

// callback for session start
void XN_CALLBACK_TYPE SessionStart(const XnPoint3D& ptFocusPoint, void* UserCxt) {
	printf("Session started\n");

	action = "sessionstart\n";

	if (isConnected) {
		kc->sendData(0.0, 0.0, 0.0, 1, action);
	}

	inSession = true;

	if (isRecording) {
		startCapture();
	}

	logGestures();
}

// Callback for session end
void XN_CALLBACK_TYPE SessionEnd(void* UserCxt) {
	printf("Session ended\n");

	action = "sessionend\n";
	
	if (isConnected) {
		kc->sendData(0.0, 0.0, 0.0, 1, action);
	}

	inSession = false;

	stopCapture();

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

		action = "primarypointcreate\n";

		if (isConnected) {
			kc->sendData(0.0, 0.0, 0.0, 1, action);
		}
	}
	else {
	}

	printf("PrimaryPointCreate\n");
}

void XN_CALLBACK_TYPE OnPrimaryPointDestroyCB(XnUInt32 nID, void* cxt) {
	mapID.erase(1);

	action = "primarypointdestroy\n";

	if (isConnected) {
		kc->sendData(0.0, 0.0, 0.0, 1, action);
	}

	printf("PrimaryPointDestroy\n");
}

void XN_CALLBACK_TYPE OnPointCreateCB(const XnVHandPointContext* pContext, void* cxt) {
	if (mapID.find(1) != mapID.end()) {
		if (mapID.find(2) == mapID.end()) {
			mapID.insert(std::make_pair(2, pContext->nID));

			action = "pointcreate\n";

			if (isConnected) {
				kc->sendData(0.0, 0.0, 0.0, 2, action);
			}
		}
		else {
		}
	}

	printf("PointCreate\n");
}

void XN_CALLBACK_TYPE OnPointDestroyCB(XnUInt32 nID, void* cxt) {
	if (mapID.find(1)->second != nID) {
		mapID.erase(2);

		action = "pointcreate\n";

		if (isConnected) {
			kc->sendData(0.0, 0.0, 0.0, 2, action);
		}
	}

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
	XnVSessionManager* sessionManager;

	// Create context
	rc = context.InitFromXmlFile(SAMPLE_XML_FILE);

	if (rc != XN_STATUS_OK) {
		printf("Couldn't initialize: %s\n", xnGetStatusString(rc));
		return 1;
	}

	// Setup for image generation
	/*rc = context.FindExistingNode(XN_NODE_TYPE_IMAGE, imageGenerator);

	if (rc != XN_STATUS_OK) {
		printf("Image Generator couldn't initialize: %s\n", xnGetStatusString(rc));
		return 1;
	}*/

	// Setup for depth generation
	rc = context.FindExistingNode(XN_NODE_TYPE_DEPTH, depthGenerator);

	if (rc != XN_STATUS_OK) {
		printf("Depth Generator couldn't initialize: %s\n", xnGetStatusString(rc));
		return 1;
	}

	// Setup for scene analysis
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
	//XnVCircleDetector circle;
	//circle.RegisterCircle(NULL, OnCircleCB);
	//circle.RegisterNoCircle(NULL, OnNoCircleCB);
	//circle.RegisterPointUpdate(NULL, OnPointUpdateCB);

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
		if (!inSession) {
			getGrabberLocation();
		}

		context.WaitAndUpdateAll();
		sessionManager->Update(&context);
	}

	// Shut down client
	if (isConnected) {
		kc->endClient();
	}

	stopCapture();

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

void getGrabberLocation() {
	sceneAnalyzer.GetMetaData(sceneMD);

	const XnLabel *sceneLabels = sceneMD.Data();

	XnLabel label = 0;
	XnLabel labelSend = 0;

	XnUInt32 lowX = 1024;
	XnUInt32 highX = 0;
	XnUInt32 labelX = 0;

	XnUInt32 lowY = 640;
	XnUInt32 highY = 0;
	XnUInt32 labelY = 0;

	XnUInt32 xres = sceneMD.XRes();
	XnUInt32 yres = sceneMD.YRes();

	for (XnUInt32 y = 0;y < yres;y++) {
		for (XnUInt32 x = 0;x < xres;x++) {
			label = *sceneLabels;

			if (label != 0) {
				if (x < lowX) {
					lowX = x;
				}
				if (x > highX) {
					highX = x;
				}

				if (y < lowY) {
					lowY = y;
				}
				if (y > highY) {
					highY = y;
				}

				labelSend = 1;
			}

			sceneLabels++;
		}
	}

	if (isConnected && labelSend) {
		labelX = (highX + lowX) / 2;
		labelY = (highY + lowY) / 2;

		action = "none\n";

		kc->sendData((float)labelX, (float)labelY, 0, 0, action);
	}
}

void startCapture() {
	char recordFile[256] = {0};
	time_t rawtime;
	struct tm *timeinfo;

	time(&rawtime);
	timeinfo = localtime(&rawtime);

	XnUInt32 size;

	xnOSStrFormat(recordFile, sizeof(recordFile)-1, &size, "%d_%02d_%02d[%02d_%02d_%02d].oni",
				  timeinfo->tm_year + 1900, timeinfo->tm_mon + 1, timeinfo->tm_mday, timeinfo->tm_hour, timeinfo->tm_min, timeinfo->tm_sec);

	if (recorder != NULL) {
		stopCapture();
	}

	recorder = new xn::Recorder;

	rc = context.CreateAnyProductionTree(XN_NODE_TYPE_RECORDER, NULL, *recorder);

	if (rc != XN_STATUS_OK) {
		printf("Recorder couldn't initialize: %s\n", xnGetStatusString(rc));
		stopCapture();
		return;
	}

	rc = recorder->SetDestination(XN_RECORD_MEDIUM_FILE, recordFile);

	if (rc != XN_STATUS_OK) {
		printf("Destination couldn't initialize: %s\n", xnGetStatusString(rc));
		stopCapture();
		return;
	}

	rc = recorder->AddNodeToRecording(depthGenerator, XN_CODEC_16Z_EMB_TABLES);

	if (rc != XN_STATUS_OK) {
		printf("Node couldn't initialize: %s\n", xnGetStatusString(rc));
		stopCapture();
		return;
	}

	record = true;
}

void stopCapture() {
	record = false;

	if (recorder != NULL) {
		recorder->RemoveNodeFromRecording(depthGenerator);
		recorder->Unref();
		delete recorder;
	}

	recorder = NULL;
}