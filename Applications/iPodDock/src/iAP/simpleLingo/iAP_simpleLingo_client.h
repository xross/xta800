#ifndef __IAP_simpleLingo_CLIENT_H__

#define __IAP_simpleLingo_CLIENT_H__

// Client functions for higher layers

#include "iAP.h"

/*
 * iAP_simpleLingo_CONTEXTBUTTONSTATUS_client:
 * Client command for CONTEXTBUTTONSTATUS
 */
int iAP_simpleLingo_CONTEXTBUTTONSTATUS_client(streaming_chanend cCtrl, int transid, unsigned buttonstatus);

/*
 * iAP_simpleLingo_IMAGEBUTTONSTATUS_client:
 * Client command for IMAGEBUTTONSTATUS
 */
int iAP_simpleLingo_IMAGEBUTTONSTATUS_client(streaming_chanend cCtrl, int transid, unsigned buttonstatus, REFERENCE_PARAM(int, retval));

/*
 * iAP_simpleLingo_VIDEOBUTTONSTATUS_client:
 * Client command for VIDEOBUTTONSTATUS
 */
int iAP_simpleLingo_VIDEOBUTTONSTATUS_client(streaming_chanend cCtrl, int transid, unsigned buttonstatus, REFERENCE_PARAM(int, retval));

/*
 * iAP_simpleLingo_AUDIOBUTTONSTATUS_client:
 * Client command for AUDIOBUTTONSTATUS
 */
int iAP_simpleLingo_AUDIOBUTTONSTATUS_client(streaming_chanend cCtrl, int transid, unsigned buttonstatus, REFERENCE_PARAM(int, retval));


#endif //__IAP_simpleLingo_CLIENT_H__

