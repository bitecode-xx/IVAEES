/****************************************************************************
*                                                                           *
*   NITE 1.3 - Multiple Primary Hands Sample                                *
*   Please make sure MultipleHands support is activated in the              *
*   Nite.ini file                                                           *
*                                                                           *
*   Authors:     Oz Magal / Lior Cohen                                      *
*                                                                           *
****************************************************************************/

/****************************************************************************
*                                                                           *
*   NITE 1.3	                                                            *
*   Copyright (C) 2006 PrimeSense Ltd. All Rights Reserved.                 *
*                                                                           *
*   This file has been provided pursuant to a License Agreement containing  *
*   restrictions on its use. This data contains valuable trade secrets      *
*   and proprietary information of PrimeSense Ltd. and is protected by law. *
*                                                                           *
****************************************************************************/

//-----------------------------------------------------------------------------
// Headers
//-----------------------------------------------------------------------------
#include <stdio.h>

//OpenNI includes
#include <XnOpenNI.h>

//NITE includes
#include <XnVSessionManager.h>
#include <XnVPointFilter.h>

//
// Secondary filter.
// This class replaces the primary point with another. The other point will remain primary for all its subtree
// until it is either removed or becomes the real primary. In these cases another point will become the primary
// for the subtree.
// Notice: If there is only one point in the system, it will be the primary for both the original tree and
// the filter's subtree, until another point is recognized. The filter will then replace the primary for
// its subtree.
class XnVSecondaryFilter : public XnVPointFilter
{
public:
	// Constructor.
	XnVSecondaryFilter() :
	  m_nNewPrimary(0)
	  {}

	// Create a local copy of all the hands, and set a primary for the subtree.
	void Update(const XnVMultipleHands& hands)
	{
		// Copy all the hands
		hands.Clone(m_LocalCopy);

		const XnVHandPointContext* pPrimary = hands.GetPrimaryContext();
		if (pPrimary == NULL)
		{
			// There are no points at all.
			return;
		}

		XnUInt32 nPrimary = pPrimary->nID;

		if (hands.GetContext(m_nNewPrimary) == 0)
		{
			// The secondary point we considered primary for the subtree is no longer available.
			m_nNewPrimary = 0;
		}

		// If we don't remember a secondary point as primary, or that secondary just became the primary for 
		// the entire tree, try to locate another secondary.
		if (m_nNewPrimary == 0 || m_nNewPrimary == nPrimary)
		{
			// local primary unavailable
			for (XnVMultipleHands::ConstIterator iter = hands.begin(); iter != hands.end(); ++iter)
			{
				if (iter.IsActive() && (*iter)->nID != nPrimary)
				{
					// Found a point that is active, but is not the primary. This will be the primary
					// for the subtree
					m_nNewPrimary = (*iter)->nID;
					break;
				}
			}
		}
		// Adjust the hand list with our chosen primary for the subtree
		m_LocalCopy.ReassignPrimary(m_nNewPrimary);
	}

	// Received a new message
	void Update(XnVMessage* pMessage)
	{
		// Deal with point message (calls Update(const XnVMultipleHands&)). This will fill the local copy.
		XnVPointControl::Update(pMessage);

		// Replace the points part of the message with the local copy.
		GenerateReplaced(pMessage, m_LocalCopy);

		// If there are no active entries (which means - no points), a new point will be looked for next time.
		if (m_LocalCopy.ActiveEntries() == 0)
		{
			m_nNewPrimary = 0;
		}
	}
protected:
	XnUInt32 m_nNewPrimary;
	XnVMultipleHands m_LocalCopy;
};