// General headers
#include <stdio.h>
// OpenNI headers
#include <XnOpenNI.h>
// NITE headers
#include <XnVSessionManager.h>
#include <XnvPointDenoiser.h>

// Detector headers
#include <XnVPushDetector.h>
#include <XnVSteadyDetector.h>
#include <XnVWaveDetector.h>

// Other headers
#include "XnVSecondaryFilter.cpp"

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
XnBool isRecording = false;
XnBool isLogging = false;

// Don't modify
XnBool record = false;
XnBool inSession = false;
XnBool isGesture = false;

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

float handOneX = 0.0;
float handOneY = 0.0;
float handOneZ = 0.0;
float handTwoX = 0.0;
float handTwoY = 0.0;
float handTwoZ = 0.0;

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

	if (isLogging) {
		logGestures();
	}
}

// Callback for session end
void XN_CALLBACK_TYPE SessionEnd(void* UserCxt) {
	printf("Session ended\n");

	action = "sessionend\n";
	
	if (isConnected) {
		kc->sendData(0.0, 0.0, 0.0, 1, action);
	}

	inSession = false;

	if (isRecording) {
		stopCapture();
	}

	if (isLogging) {
		logGestures();
	}
}

// Callback for push detection of the first hand
void XN_CALLBACK_TYPE OnPushCB(XnFloat velocity, XnFloat angle, void* cxt) {
	if (mapID.size() <= 0) {
		return;
	}

	action = "push\n";

	isGesture = true;

	if (angle > 25.0) {
		isGesture = false;
	}

	if (isLogging) {
		logGestures();
	}
}

// Callback for push detection of the second hand
void XN_CALLBACK_TYPE OnPushSecondCB(XnFloat velocity, XnFloat angle, void* cxt) {
	if (mapID.size() <= 1) {
		return;
	}

	action = "pushsecond\n";

	isGesture = true;

	if (angle > 25.0) {
		isGesture = false;
	}

	if (isLogging) {
		logGestures();
	}
}

// Callback for steady detection of the first hand
void XN_CALLBACK_TYPE OnSteadyCB(XnFloat velocity, void* cxt) {
	if (mapID.size() <= 0) {
		return;
	}

	action = "steady\n";

	isGesture = true;
	
	if (isLogging) {
		logGestures();
	}
}

// Callback for steady detection of the second hand
void XN_CALLBACK_TYPE OnSteadySecondCB(XnFloat velocity, void* cxt) {
	if (mapID.size() <= 1) {
		return;
	}

	action = "steadysecond\n";

	isGesture = true;

	if (isLogging) {
		logGestures();
	}
}

// Callback for wave detection
void XN_CALLBACK_TYPE OnWaveCB(void* cxt) {
	action = "wave\n";

	isGesture = true;

	if (isLogging) {
		logGestures();
	}
}

// callback for primarypointcreate
void XN_CALLBACK_TYPE OnPrimaryPointCreateCB(const XnVHandPointContext* pContext, const XnPoint3D& ptFocus, void* cxt) {
	if (mapID.find(1) == mapID.end()) {
		mapID.insert(std::make_pair(1, pContext->nID));

		action = "primarypointcreate\n";

		if (isConnected) {
			kc->sendData(0.0, 0.0, 0.0, 1, action);
		}

		action = "none\n";
	}
	else {
	}
}

// callback for primarypointdestroy
void XN_CALLBACK_TYPE OnPrimaryPointDestroyCB(XnUInt32 nID, void* cxt) {
	mapID.erase(1);

	action = "primarypointdestroy\n";

	if (isConnected) {
		kc->sendData(-100.0, 0.0, 0.0, 1, action);
	}

	action = "none\n";
}

// callback for primarypointreplace
void XN_CALLBACK_TYPE OnPrimaryPointReplaceCB(XnUInt32 nOldId, const XnVHandPointContext* pContext, void* cxt) {
	if (mapID.find(1)->second == nOldId) {
		XnUInt32 temp = mapID.find(2)->second;

		mapID.clear();

		if (temp == pContext->nID) {
			mapID.insert(std::make_pair(1, temp));

			handOneX = handTwoX;
			handOneY = handTwoY;
			handOneZ = handTwoZ;

			action = "primarypointreplace\n";

			if (isConnected) {
				kc->sendData(handOneX, handOneY, handOneZ, 1, action);
			}

			action = "none\n";
		}
	}
}

// callback for pointcreate
void XN_CALLBACK_TYPE OnPointCreateCB(const XnVHandPointContext* pContext, void* cxt) {
	if (mapID.find(1) != mapID.end()) {
		if (mapID.find(2) == mapID.end()) {
			mapID.insert(std::make_pair(2, pContext->nID));

			action = "pointcreate\n";

			if (isConnected) {
				kc->sendData(0.0, 0.0, 0.0, 2, action);
			}

			action = "none\n";
		}
		else {
		}
	}
}

// callback for pointdestroy
void XN_CALLBACK_TYPE OnPointDestroyCB(XnUInt32 nID, void* cxt) {
	if (mapID.find(2)->second == nID) {
		mapID.erase(2);

		action = "pointdestroy\n";

		if (isConnected) {
			kc->sendData(-100.0, 0.0, 0.0, 2, action);
		}

		action = "none\n";
	}
}

// callback for a new position of the first hand
void XN_CALLBACK_TYPE OnPointUpdateCB(const XnVHandPointContext* pContext, void* cxt) {
	if (mapID.size() <= 0) {
		return;
	}

	int select = 0;

	XnPoint3D ptProjective(pContext->ptPosition);

	depthGenerator.ConvertRealWorldToProjective(1, &ptProjective, &ptProjective);

	if (mapID.find(1)->second == pContext->nID) {
		select = 1;

		handOneX = ptProjective.X;
		handOneY = ptProjective.Y;
		handOneZ = ptProjective.Z;
	}
	if (mapID.find(2)->second == pContext->nID) {
		select = 2;

		handTwoX = ptProjective.X;
		handTwoY = ptProjective.Y;
		handTwoZ = ptProjective.Z;
	}
	if (action == "pushsecond\n" || action == "steadysecond\n") {
		select = 2;
	}

	if (!isGesture) {
	  action = "none\n";
	}

	if (isConnected) {
		if (select == 1) {
			kc->sendData(handOneX, handOneY, handOneZ, select, action);
		}
		else {
			kc->sendData(handTwoX, handTwoY, handTwoZ, select, action);
		}
	}

	isGesture = false;

	action = "none\n";

	select = 0;
}

/*// callback for a new position of the second hand
void XN_CALLBACK_TYPE OnPointUpdateSecondCB(const XnVHandPointContext* pContext, void* cxt) {
	if (mapID.find(2) == mapID.end()) {
		return;
	}
	if (mapID.size() <= 1 || mapID.find(2)->second != pContext->nID) {
		return;
	}

	int select = 2;

	XnPoint3D ptProjective(pContext->ptPosition);

	depthGenerator.ConvertRealWorldToProjective(1, &ptProjective, &ptProjective);

	if (!isGesture) {
	  action = "none\n";
	}

	if (isConnected) {
		kc->sendData(ptProjective.X, ptProjective.Y, ptProjective.Z, select, action);
	}

	isGesture = false;

	action = "none\n";

	select = 0;
}*/

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
	sessionManager->SetQuickRefocusTimeout(1000 * 30);

	// Push Detector
	XnVPushDetector push, pushSecond;
	push.RegisterPush("Primary Steady", OnPushCB);
	//push.RegisterPointUpdate("Primary Push Point Update", OnPointUpdateCB);
	push.RegisterPointUpdate(NULL, OnPointUpdateCB);

	pushSecond.RegisterPush("Secondary Push", OnPushSecondCB);
	//pushSecond.RegisterPointUpdate("Secondary Push Point Update", OnPointUpdateSecondCB);
	pushSecond.RegisterPointUpdate(NULL, OnPointUpdateCB);

	// Steady Detector
	XnVSteadyDetector steady, steadySecond;
	steady.RegisterSteady("Primary Steady", OnSteadyCB);
	//steady.RegisterPointUpdate("Primary Steady Point Update", OnPointUpdateCB);
	steady.RegisterPointUpdate(NULL, OnPointUpdateCB);

	steadySecond.RegisterSteady("Secondary Steady", OnSteadySecondCB);
	//steadySecond.RegisterPointUpdate("Secondary Steady Point Update", OnPointUpdateSecondCB);
	steadySecond.RegisterPointUpdate(NULL, OnPointUpdateCB);

	XnVPointDenoiser denoiser, denoiserSecond;

	denoiser.AddListener(&push);
	denoiser.AddListener(&steady);
	denoiser.RegisterPrimaryPointCreate(NULL, OnPrimaryPointCreateCB);
	denoiser.RegisterPrimaryPointDestroy(NULL, OnPrimaryPointDestroyCB);
	denoiser.RegisterPrimaryPointReplace(NULL, OnPrimaryPointReplaceCB);
	denoiser.RegisterPointCreate(NULL, OnPointCreateCB);
	denoiser.RegisterPointDestroy(NULL, OnPointDestroyCB);

	//denoiserSecond.AddListener(&pushSecond);
	//denoiserSecond.AddListener(&steadySecond);

	// Secondary Filter Point
	XnVSecondaryFilter sf;
	//sf.AddListener(&denoiserSecond);
	sf.AddListener(&pushSecond);
	sf.AddListener(&steadySecond);

	denoiserSecond.AddListener(&sf);

	sessionManager->AddListener(&denoiser);
	sessionManager->AddListener(&denoiserSecond);
	//sessionManager->AddListener(&sf);

	// Start up client
	if (isConnected) {
		kc = new Kinect_Client();
	}

	// Start up file writer
	if (isLogging) {
		output = new std::ofstream(GESTURE_LOG);
	}

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

	if (isRecording) {
		stopCapture();
	}

	if (isLogging) {
		output->close();
		delete output;
	}
	
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

/*
  Get the location of the midpoint of all visible labels
*/
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

/*
  Depth Map video recording
*/
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