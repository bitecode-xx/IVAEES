// General headers
#include <stdio.h>
// OpenNI headers
#include <XnOpenNI.h>
// NITE headers
#include <XnVSessionManager.h>

// Detector headers
#include <XnVCircleDetector.h>
#include <XnVPushDetector.h>
#include <XnVSteadyDetector.h>
#include <XnVSwipeDetector.h>
#include <XnVWaveDetector.h>

// Custom headers
//#include "XnVGrabDetector.h"
//#include "GrabGesture.h"

// Client
#include "Kinect_Client.h"

// xml to initialize OpenNI
#define SAMPLE_XML_FILE "../Data/Sample-Tracking.xml"

char *action = "none\n";

bool isGesture = false;
bool circlePP = false;

Kinect_Client *kc;

xn::DepthGenerator depthGenerator;

// Callback for when the focus is in progress
void XN_CALLBACK_TYPE SessionProgress(const XnChar* strFocus, const XnPoint3D& ptFocusPoint, XnFloat fProgress, void* UserCxt) {
	//printf("Session progress (%6.2f,%6.2f,%6.2f) - %6.2f [%s]\n", ptFocusPoint.X, ptFocusPoint.Y, ptFocusPoint.Z, fProgress,  strFocus);
}

// callback for session start
void XN_CALLBACK_TYPE SessionStart(const XnPoint3D& ptFocusPoint, void* UserCxt) {
	printf("Session started. Please wave (%6.2f,%6.2f,%6.2f)...\n", ptFocusPoint.X, ptFocusPoint.Y, ptFocusPoint.Z);
}

// Callback for session end
void XN_CALLBACK_TYPE SessionEnd(void* UserCxt) {
	printf("Session ended. Please perform focus gesture to start session\n");
}

// Callback for wave detection
void XN_CALLBACK_TYPE OnCircleCB(XnFloat times, XnBool confident, const XnVCircle* circle, void* cxt) {
	action = "circle\n";
	isGesture = true;

	printf("Circle\n");
}

// Callback for wave detection
void XN_CALLBACK_TYPE OnNoCircleCB(XnFloat lastValue, XnVCircleDetector::XnVNoCircleReason reason, void* cxt) {
	action = "nocircle\n";
	isGesture = true;
	circlePP = false;

	printf("No Circle\n");
}

// Callback for wave detection
void XN_CALLBACK_TYPE OnGrabCB(void* cxt) {
	action = "grab\n";
	isGesture = true;

	printf("Grab\n");
}

// Callback for wave detection
void XN_CALLBACK_TYPE OnReleaseCB(void* cxt) {
	action = "release\n";
	isGesture = true;

	printf("Release\n");
}

// Callback for wave detection
void XN_CALLBACK_TYPE OnPushCB(XnFloat velocity, XnFloat angle, void* cxt) {
	action = "push\n";
	isGesture = true;

	printf("Push\n");
}

// Callback for wave detection
void XN_CALLBACK_TYPE OnStabilizedCB(XnFloat velocity, void* cxt) {
	action = "stabilized\n";
	isGesture = true;

	printf("Stablized\n");
}

// Callback for wave detection
void XN_CALLBACK_TYPE OnSteadyCB(XnFloat velocity, void* cxt) {
	action = "steady\n";
	isGesture = true;

	printf("Steady\n");
}

// Callback for wave detection
void XN_CALLBACK_TYPE OnSwipeUpCB(XnFloat velocity, XnFloat angle, void* cxt) {
	action = "swipeup\n";
	isGesture = true;

	printf("Swipe Up\n");
}

// Callback for wave detection
void XN_CALLBACK_TYPE OnSwipeDownCB(XnFloat velocity, XnFloat angle, void* cxt) {
	action = "swipedown\n";
	isGesture = true;

	printf("Swipe Down\n");
}

// Callback for wave detection
void XN_CALLBACK_TYPE OnSwipeLeftCB(XnFloat velocity, XnFloat angle, void* cxt) {
	action = "swipeleft\n";
	isGesture = true;

	printf("Swipe Left\n");
}

// Callback for wave detection
void XN_CALLBACK_TYPE OnSwipeRightCB(XnFloat velocity, XnFloat angle, void* cxt) {
	action = "swiperight\n";
	isGesture = true;

	printf("Swipe Right\n");
}

// Callback for wave detection
void XN_CALLBACK_TYPE OnWaveCB(void* cxt) {
	action = "wave\n";
	isGesture = true;

	printf("Wave\n");
}

// callback for a new position of any hand
void XN_CALLBACK_TYPE OnPointUpdate(const XnVHandPointContext* pContext, void* cxt) {
	XnPoint3D ptProjective(pContext->ptPosition);

	depthGenerator.ConvertRealWorldToProjective(1, &ptProjective, &ptProjective);

	if (!isGesture) {
	  action = "none\n";
	}

	/*if (circlePP) {
	  action = "none\n";
	}*/

	//printf("Action: %s\n", action);

	kc->sendData(ptProjective.X, ptProjective.Y, ptProjective.Z, 1, action);

	/*if (strcmp(action, "circle\n") == 0) {
		circlePP = true;
	}*/

	isGesture = false;
}


int main(int argc, char** argv) {
	xn::Context context;

	XnVSessionManager* sessionManager;

	// Create context
	XnStatus rc = context.InitFromXmlFile(SAMPLE_XML_FILE);

	if (rc != XN_STATUS_OK) {
		printf("Couldn't initialize: %s\n", xnGetStatusString(rc));
		return 1;
	}

	//
	rc = context.FindExistingNode(XN_NODE_TYPE_DEPTH, depthGenerator);

	if (rc != XN_STATUS_OK) {
		printf("Depth Generator couldn't initialize: %s\n", xnGetStatusString(rc));
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
	circle.RegisterPointUpdate(NULL, OnPointUpdate);
	sessionManager->AddListener(&circle);

	/*
	// Grab Detector
	XnVGrabDetector grab;
	grab.RegisterGrab(NULL, OnGrabCB);
	grab.RegisterRelease(NULL, OnReleaseCB);
	grab.RegisterPointUpdate(NULL, OnPointUpdate);
	sessionManager->AddListener(&grab);
	*/

	// Push Detector
	XnVPushDetector push;
	push.RegisterPush(NULL, OnPushCB);
	push.RegisterStabilized(NULL, OnStabilizedCB);
	push.RegisterPointUpdate(NULL, OnPointUpdate);
	sessionManager->AddListener(&push);

	// Steady Detector
	XnVSteadyDetector steady;
	steady.RegisterSteady(NULL, OnSteadyCB);
	steady.RegisterPointUpdate(NULL, OnPointUpdate);
	sessionManager->AddListener(&steady);

	// Swipe Detector
	XnVSwipeDetector swipe;
	swipe.RegisterSwipeUp(NULL, OnSwipeUpCB);
	swipe.RegisterSwipeDown(NULL, OnSwipeDownCB);
	swipe.RegisterSwipeLeft(NULL, OnSwipeLeftCB);
	swipe.RegisterSwipeRight(NULL, OnSwipeRightCB);
	swipe.RegisterPointUpdate(NULL, OnPointUpdate);
	sessionManager->AddListener(&swipe);

	// Wave Detector
	XnVWaveDetector wave;
	wave.RegisterWave(NULL, OnWaveCB);
	wave.RegisterPointUpdate(NULL, OnPointUpdate);
	sessionManager->AddListener(&wave);

	// Start up client
	kc = new Kinect_Client();

	while (1) {
		context.WaitAndUpdateAll();
		sessionManager->Update(&context);
	}

	// Shut down client
	kc->endClient();
	
	delete sessionManager;

	context.Shutdown();

	return 0;
}