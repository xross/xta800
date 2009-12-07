#include "global.h"
#include "iPod.h"

char cert[1920];
char major=0,minor=0;
unsigned short certlen=0;
int deviceID;

/*
 * iPodDevAuthInit:
 * Call at start of code - retrieves device ID, version info
 * from coProcessor
 */
int iPodDevAuthInit()
{
  // Retrieve device ID from coprocessor
  if (coProcessorGetDeviceID(&deviceID))
    vprint("coProcessor failed retrieve device ID");
  deviceID = byterev(deviceID);
  
  // Retrieve authentication version from coProcessor
  if (coProcessorGetAuthVersion(&major,&minor))
    vprint("Failed to get auth version");            
  
  // Retrieve certificate from coProcessor
  if (coProcessorGetAccessoryCertificate(&certlen, (unsigned char*)cert))
    vprint("Failed to get accessory certificate");
  certlen &= 0xFFFF;  
  
  if (certlen > 1920)
  {
    vprint("Retrieved cert too long");
  }
  return 0;
}

/*
 * iPodDevAuth:
 * Call on receipt of a GetDevAuthenticationInfo
 */
void iPodDevAuth(streaming_chanend ciPod, int rettransid)
{
  {
    int sectionnum = 0;
    int nsections = certlen / 500;
    int authstatus;
    int i;
    
    while (certlen)
    {
      int templen = certlen;
      
      if (templen > 500)
        templen = 500;
      for (i = 0; i < 50; i++)
      {
        // Transmit authentication info to iPod
        if (!iAP_generalLingo_RetDevAuthenticationInfo_client(ciPod, rettransid, ((major&0xFF) << 8) | (minor&0xFF), sectionnum, nsections, templen, sectionnum * 500, (unsigned char *)cert, &authstatus))
          break;
      }
      certlen -= templen;
      sectionnum++;
    }
  }
}

/*
 * iPodStartAuth:
 * Call when wanting to start accessory authentication process
 */
int iPodStartAuth(streaming_chanend ciPod)
{
  int retVal;
  // Second IDL to request auth
  while (iAP_generalLingo_IdentifyDeviceLingos_client(ciPod, gettransid(), IPOD_LINGOS_SPOKEN, IPOD_OPTIONS, deviceID, &retVal));
  if (retVal)
  {
    // iPod NACKed Second IDL
   
    // Try with no auth and no digital audio
    while (iAP_generalLingo_IdentifyDeviceLingos_client(ciPod, gettransid(), IPOD_LINGOS_SPOKEN & ~AUDIO_LINGO & ~DISPLAY_REMOTE_LINGO & ~SIMPLE_REMOTE_LINGO, POWER_OPTION << 2, 0, &retVal));
    if (retVal)
    {
      vprint("iPod NACKed non-auth IDL");
    }
  }
  return 0;
}

/*
 * iPodAuthGetDevAuthenticationSignature:
 * Call on receipt of GetDevAuthenticationSignature
 */
int iPodAuthGetDevAuthenticationSignature(streaming_chanend ciPodHID, int challen, char chal[], int rettransid)
{
  // Store challenge on coProcessor
  if (coProcessorPutChallenge((unsigned short)challen, (unsigned char*)chal))
    vprint("Failed to put challenge to coprocessor");
  
  // Trigger coProcessor to start signature generation
  if (coProcessorStartSigGeneration())
    vprint("Failed to start sig generation");         
  
  // Wait for coProcessor to finish
  while (coProcessorBusy());
  
  // Check for valid results from the coProcessor
  if (!coProcessorValidResults())
  {
    char sig[256];
    unsigned siglen;
    int retval;
    
    // Retrieve newly created signature from coprocessor
    if (coProcessorGetSignature(&siglen, (unsigned char*)sig))
      vprint("Failed to retrieve sig from coprocessor");
    
    if (siglen > 256)
    {
      vprint("Retrieved signature too long");
    }
    
    // Transmit signature to iPod
    if (iAP_generalLingo_RetDevAuthenticationSignature_client(ciPodHID, rettransid, (int)siglen, (unsigned char *)sig, &retval))
      vprint("iPod didn't ack retdevauthsig");
  }
  return 0;
}
