#ifndef _XNV_GRAB_DETECTOR_H_
#define _XNV_GRAB_DETECTOR_H_

#include <XnVNiteDefs.h>
#include <XnVPointControl.h>

struct XnVGrabContext;

class XNV_NITE_API XnVGrabDetector : public XnVPointControl {
	public:
		/**
		* Type for the grab event callbacks
		*/
		typedef void (XN_CALLBACK_TYPE *GrabCB)(void* pUserCxt);
		typedef void (XN_CALLBACK_TYPE *ReleaseCB)(void* pUserCxt);

		/**
		* Create the grab control
		*
		* @param	[in]	strName			Name of the control, for log purposes.
		*/
		XnVGrabDetector(const XnChar* strName = "XnVGrabDetector");
		~XnVGrabDetector();

		/**
		* Called when the primary point is created.
		*
		* @param	[in]	pContext	The hand context of the newly created primary point
		* @param	[in]	ptFocus		The point in which the session has started.
		*/
		void OnPrimaryPointCreate(const XnVHandPointContext* pContext, const XnPoint3D& ptFocus);
		/**
		* Called when the primary point is updated.
		* This will cause the algorithm to look for waves.
		*
		* @param	[in]	pContext	The hand context of the updated primary point
		*/
		void OnPrimaryPointUpdate(const XnVHandPointContext* pContext);
		/**
		* Called when the primary point is replaced by another.
		* This will reinitialize the algorithm for the new hand
		*
		* @param	[in]	nOldId		The ID of the old primary point
		* @param	[in]	pContext	The hand context of the new primary point
		*/
		void OnPrimaryPointReplace(XnUInt32 nOldId, const XnVHandPointContext* pContext);
		/**
		* Called when the primary point is destroyed
		*
		* @param	[in]	nID			The ID of the hand that was the primary point
		*/
		void OnPrimaryPointDestroy(XnUInt32 nID);

		/**
		* Register for the grab event
		* 
		* @param	[in]	cxt	User's context
		* @param	[in]	pCB	The Callback to call when the event is invoked.
		*
		* @return	A handle, to allow unregistration.
		*/
		XnCallbackHandle RegisterGrab(void* cxt, GrabCB pCB);

		/**
		* Register for the release event
		* 
		* @param	[in]	cxt	User's context
		* @param	[in]	pCB	The Callback to call when the event is invoked.
		*
		* @return	A handle, to allow unregistration.
		*/
		XnCallbackHandle RegisterRelease(void* cxt, ReleaseCB pCB);

		/**
		* Unregister from the grab event
		*
		* @param	[in]	handle	The handle provided on registration.
		*/
		void UnregisterGrab(XnCallbackHandle handle);

		/**
		* Unregister from the release event
		*
		* @param	[in]	handle	The handle provided on registration.
		*/
		void UnregisterRelease(XnCallbackHandle handle);

		/**
		* Reset the algorithm.
		*/
		void Reset();

		void SetFlipCount(XnInt32 nFlipCount);
		void SetMinLength(XnInt32 nMinLength);
		void SetMaxDeviation(XnInt32 nMaxDeviation);

		XnInt32 GetFlipCount() const;
		XnInt32 GetMinLength() const;
		XnInt32 GetMaxDeviation() const;
	protected:
		XnVGrabContext* m_pContext;
		XnVEvent m_GrabCBs;
}; // XnVGrabDetector

#endif // _XNV_GRAB_DETECTOR_H_
