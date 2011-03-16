#include "XnVGrabDetector.h"

XnVGrabDetector::XnVGrabDetector(const XnChar* strName) {

}

XnVGrabDetector::~XnVGrabDetector() {

}

void XnVGrabDetector::OnPrimaryPointCreate(const XnVHandPointContext* pContext, const XnPoint3D& ptFocus) {

}

void XnVGrabDetector::OnPrimaryPointUpdate(const XnVHandPointContext* pContext) {

}

void XnVGrabDetector::OnPrimaryPointReplace(XnUInt32 nOldId, const XnVHandPointContext* pContext) {

}

void XnVGrabDetector::OnPrimaryPointDestroy(XnUInt32 nID) {

}

XnCallbackHandle XnVGrabDetector::RegisterGrab(void* cxt, GrabCB pCB) {

}

XnCallbackHandle XnVGrabDetector::RegisterRelease(void* cxt, ReleaseCB pCB) {

}
		
void XnVGrabDetector::UnregisterGrab(XnCallbackHandle handle) {

}

void XnVGrabDetector::UnregisterRelease(XnCallbackHandle handle) {

}

void XnVGrabDetector::Reset() {

}

void XnVGrabDetector::SetFlipCount(XnInt32 nFlipCount) {

}

void XnVGrabDetector::SetMinLength(XnInt32 nMinLength) {

}

void XnVGrabDetector::SetMaxDeviation(XnInt32 nMaxDeviation) {

}

XnInt32 XnVGrabDetector::GetFlipCount() const {

}

XnInt32 XnVGrabDetector::GetMinLength() const {

}

XnInt32 XnVGrabDetector::GetMaxDeviation() const {

}