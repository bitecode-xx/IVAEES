// General headers
#include <stdio.h>
// OpenNI headers
#include <XnOpenNI.h>
// NITE headers
#include <XnVSessionManager.h>
#include <XnvPointDenoiser.h>
#include <XnVPointArea.h>
#include <XnVSelectableSlider1D.h>

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
#include "main.h"

#include <ctime>
#include <iostream>
#include <fstream>

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

xn::DepthGenerator depthGenerator;
xn::SceneMetaData sceneMD;
xn::SceneAnalyzer sceneAnalyzer;

// Callback for when the focus is in progress
void XN_CALLBACK_TYPE SessionProgress(const XnChar* strFocus, const XnPoint3D& ptFocusPoint, XnFloat fProgress, void* UserCxt) {
	//printf("Session progress (%6.2f,%6.2f,%6.2f) - %6.2f [%s]\n", ptFocusPoint.X, ptFocusPoint.Y, ptFocusPoint.Z, fProgress,  strFocus);
}

// callback for session start
void XN_CALLBACK_TYPE SessionStart(const XnPoint3D& ptFocusPoint, void* UserCxt) {
	printf("Session started. Please wave (%6.2f,%6.2f,%6.2f)...\n", ptFocusPoint.X, ptFocusPoint.Y, ptFocusPoint.Z);

	action = "sessionstart\n";

	logGestures();
}

// Callback for session end
void XN_CALLBACK_TYPE SessionEnd(void* UserCxt) {
	printf("Session ended. Please perform focus gesture to start session\n");

	action = "sessionend\n";

	logGestures();
}

//Type for callbacks to be called when a point is silenced
void XN_CALLBACK_TYPE PointSilencedCB(XnUInt32 nID, void* cxt) {
	printf("Point Silenced\n");
}

//Type for callbacks to be called when a previously silenced point is revived
void XN_CALLBACK_TYPE PointRevivedCB(XnUInt32 nID, void* cxt) {
	printf("Point Revived\n");
}

//Type for callbacks to be called when a previously silenced point is really removed
void XN_CALLBACK_TYPE SilentPointRemovedCB(XnUInt32 nID, void* cxt) {
	printf("Silent Point Removed\n");
}

// Type for hover event callback. Receives the index of the hovered item
void XN_CALLBACK_TYPE ItemHoverCB(XnInt32 nItemIndex, void* pUserCxt) {
	printf("Item Hover\n");
}

// Type for select event callback. Receives the index of the selected item and the direction of the selection.
void XN_CALLBACK_TYPE ItemSelectCB(XnInt32 nItemIndex, XnVDirection nDirection, void* pUserCxt) {
	printf("Item Select\n");
}

// Type for scroll event callback. Receives the number between -1 and 1, indicating where in the border the point is
void XN_CALLBACK_TYPE ScrollCB(XnFloat fScrollValue, void* pUserCxt) {
	printf("Scroll\n");
}

// Type for value change event callback.
// Receives a number between 0 and 1, indicating where in the slider the point is
void XN_CALLBACK_TYPE ValueChangeCB(XnFloat fValue, void* pUserCxt) {
	printf("Value Change\n");
}

//Type for off axis event callback. Receives the direction of the off axis movement
void XN_CALLBACK_TYPE OffAxisMovementCB(XnVDirection dir, void* pUserCxt) {
	printf("Off Axis Movement\n");
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
void XN_CALLBACK_TYPE OnGrabCB(void* cxt) {
	action = "grab\n";
	isGesture = true;

	logGestures();

	printf("Grab\n");
}

// Callback for wave detection
void XN_CALLBACK_TYPE OnReleaseCB(void* cxt) {
	action = "release\n";
	isGesture = true;

	logGestures();

	printf("Release\n");
}

// Callback for wave detection
void XN_CALLBACK_TYPE OnPushCB(XnFloat velocity, XnFloat angle, void* cxt) {
	action = "push\n";
	isGesture = true;

	logGestures();

	printf("Push\n");
}

// Callback for wave detection
void XN_CALLBACK_TYPE OnStabilizedCB(XnFloat velocity, void* cxt) {
	action = "stabilized\n";
	isGesture = true;

	logGestures();

	printf("Stablized\n");
}

// Callback for wave detection
void XN_CALLBACK_TYPE OnSteadyCB(XnFloat velocity, void* cxt) {
	action = "steady\n";
	isGesture = true;

	logGestures();

	printf("Steady\n");
}

// Callback for wave detection
void XN_CALLBACK_TYPE OnSwipeUpCB(XnFloat velocity, XnFloat angle, void* cxt) {
	action = "swipeup\n";
	isGesture = true;

	logGestures();

	printf("Swipe Up\n");
}

// Callback for wave detection
void XN_CALLBACK_TYPE OnSwipeDownCB(XnFloat velocity, XnFloat angle, void* cxt) {
	action = "swipedown\n";
	isGesture = true;

	logGestures();

	printf("Swipe Down\n");
}

// Callback for wave detection
void XN_CALLBACK_TYPE OnSwipeLeftCB(XnFloat velocity, XnFloat angle, void* cxt) {
	action = "swipeleft\n";
	isGesture = true;

	logGestures();

	printf("Swipe Left\n");
}

// Callback for wave detection
void XN_CALLBACK_TYPE OnSwipeRightCB(XnFloat velocity, XnFloat angle, void* cxt) {
	action = "swiperight\n";
	isGesture = true;

	logGestures();

	printf("Swipe Right\n");
}

// Callback for wave detection
void XN_CALLBACK_TYPE OnWaveCB(void* cxt) {
	action = "wave\n";
	isGesture = true;

	logGestures();

	printf("Wave\n");
}

// callback for a new position of any hand
void XN_CALLBACK_TYPE OnPointUpdate(const XnVHandPointContext* pContext, void* cxt) {
	XnPoint3D ptProjective(pContext->ptPosition);

	XnPoint3D ptReal(pContext->ptPosition);

	depthGenerator.ConvertRealWorldToProjective(1, &ptProjective, &ptProjective);

	if (!isGesture) {
	  action = "none\n";
	}

	if (circlePP) {
	  action = "none\n";
	}

	printf("hand id: %u", pContext->nID);

	if (isConnected) {
		kc->sendData(ptProjective.X, ptProjective.Y, ptProjective.Z, (int)pContext->nID, action);
	}

	printf("depth: %f\n", ptProjective.Z);

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

	/*XnPoint3D lbnSlider;
	lbnSlider.X = 0.0;
	lbnSlider.Y = 100.0;
	lbnSlider.Z = 0.0;

	XnPoint3D rtfSlider;
	rtfSlider.X = 640.0;
	rtfSlider.Y = 0.0;
	rtfSlider.Z = 1000.0;

	XnBoundingBox3D sliderBox;
	sliderBox.LeftBottomNear = lbnSlider;
	sliderBox.RightTopFar = rtfSlider;

	XnPoint3D lbnNormal;
	lbnNormal.X = 0.0;
	lbnNormal.Y = 480.0;
	lbnNormal.Z = 0.0;

	XnPoint3D rtfNormal;
	rtfNormal.X = 640.0;
	rtfNormal.Y = 100.1;
	rtfNormal.Z = 1000.0;

	XnBoundingBox3D normalBox;
	normalBox.LeftBottomNear = lbnNormal;
	normalBox.RightTopFar = rtfNormal;*/

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

	/*rc = context.FindExistingNode(XN_NODE_TYPE_SCENE, sceneAnalyzer);

	if (rc != XN_STATUS_OK) {
		printf("Scene Analyzer couldn't initialize: %s\n", xnGetStatusString(rc));
		return 1;
	}*/

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

	/*
	XnVPointArea sliderArea(sliderBox, false, "sliderArea");
	sliderArea.RegisterPointSilenced(NULL, PointSilencedCB);
	sliderArea.RegisterPointRevived(NULL, PointRevivedCB);
	sliderArea.RegisterSilentPointRemoved(NULL, SilentPointRemovedCB);
	sliderArea.RegisterPointUpdate(NULL, OnPointUpdate);
	*/

	/*
	XnVPointArea normalArea(normalBox, false, "normalArea");
	normalArea.RegisterPointSilenced(NULL, PointSilencedCB);
	normalArea.RegisterPointRevived(NULL, PointRevivedCB);
	normalArea.RegisterSilentPointRemoved(NULL, SilentPointRemovedCB);
	normalArea.RegisterPointUpdate(NULL, OnPointUpdate);
	*/

	/*
	//SelectableSlider1D
	XnVSelectableSlider1D slider(10);
    slider.RegisterItemHover(NULL, ItemHoverCB);
	slider.RegisterItemSelect(NULL, ItemSelectCB);
	slider.RegisterScroll(NULL, ScrollCB);
	slider.RegisterValueChange(NULL, ValueChangeCB);
	slider.RegisterOffAxisMovement(NULL, OffAxisMovementCB);
	slider.RegisterPointUpdate(NULL, OnPointUpdate);
	*/

	// Circle Detector
	XnVCircleDetector circle;
	circle.RegisterCircle(NULL, OnCircleCB);
	circle.RegisterNoCircle(NULL, OnNoCircleCB);
	circle.RegisterPointUpdate(NULL, OnPointUpdate);
	//sessionManager->AddListener(&circle);
	//normalArea.AddListener(&circle);

	/*
	// Grab Detector
	XnVGrabDetector grab;
	grab.RegisterGrab(NULL, OnGrabCB);
	grab.RegisterRelease(NULL, OnReleaseCB);
	grab.RegisterPointUpdate(NULL, OnPointUpdate);
	*/

	// Push Detector
	XnVPushDetector push;
	push.RegisterPush(NULL, OnPushCB);
	//push.RegisterStabilized(NULL, OnStabilizedCB);
	push.RegisterPointUpdate(NULL, OnPointUpdate);

	// Steady Detector
	XnVSteadyDetector steady;
	steady.RegisterSteady(NULL, OnSteadyCB);
	steady.RegisterPointUpdate(NULL, OnPointUpdate);

	// Swipe Detector
	//XnVSwipeDetector swipe;
	//swipe.RegisterSwipeUp(NULL, OnSwipeUpCB);
	//swipe.RegisterSwipeDown(NULL, OnSwipeDownCB);
	//swipe.RegisterSwipeLeft(NULL, OnSwipeLeftCB);
	//swipe.RegisterSwipeRight(NULL, OnSwipeRightCB);
	//swipe.RegisterPointUpdate(NULL, OnPointUpdate);

	// Wave Detector
	//XnVWaveDetector wave;
	//wave.RegisterWave(NULL, OnWaveCB);
	//wave.RegisterPointUpdate(NULL, OnPointUpdate);

	XnVPointDenoiser denoiser;

	//sliderArea->AddListener(&slider);
	//sessionManager->AddListener(sliderArea);

	// this works alone
	//sessionManager->AddListener(&slider);

	
	//sessionManager->AddListener(&normalArea);
	//sessionManager->AddListener(&sliderArea);

	denoiser.AddListener(&circle);
	denoiser.AddListener(&push);
	denoiser.AddListener(&steady);

	sessionManager->AddListener(&denoiser);
	

	// Start up client
	if (isConnected) {
		kc = new Kinect_Client();
	}

	// Start up file writer
	output = new std::ofstream(GESTURE_LOG);

	while (1) {
		//sceneAnalyzer.GetMetaData(sceneMD);
		


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