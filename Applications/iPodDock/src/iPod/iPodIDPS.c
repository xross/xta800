#include "global.h"
#include "iPod.h"

/*
 * gettransid:
 * Call when requesting to send a new command
 * and a new transaction ID is needed
 */
int transid=0;
int gettransid()
{
  int retval;
  
  if (transid<0)
  {
    retval = -1;
  }
  else
  {
    retval = transid;
    transid++;
  }
  return retval;
}

/*
 * iPodTryIDPS:
 * Call when attempting to authenticate using IDPS
 * Returns 1 is NACKd
 */
int iPodTryIDPS(streaming_chanend ciPod)
{
  int retrycount = 3;
  int retVal;
  while (iAP_generalLingo_StartIDPS_client(ciPod, gettransid(), &retVal) && retrycount)
  {
    retrycount--;
  }
  
  if (retrycount)
  {
    if (retVal == 0)
    {
      iPodusingIDPS = 1;
  
      {
        char lingos[12];
        int lptr=0;
        int i;
        
        for (i=0; i<12; i++)
        {
          if (IPOD_LINGOS_SPOKEN & (1<<i))
          {
            lingos[lptr] = i;
            lptr++;
          }
        }
        if (iAP_generalLingo_SetFIDTokenValues_Identify_client(ciPod, gettransid(), lptr, lingos, IPOD_OPTIONS, deviceID, &retVal))
        {
          vprint("iPod failed to respond to SFTV_Identify");
        }
        if (retVal)
        {
          vprint("iPod NAKd SFTV_Identify");
        }
      }
      
      {
        if (iAP_generalLingo_SetFIDTokenValues_accCaps_client(ciPod, gettransid(), 1, 0, 1, 1, 0, 1, &retVal))
        {
          vprint("iPod failed to respond to SFTV_Identify");
        }
        if (retVal)
        {
          vprint("iPod NAKd accCaps");
        }
      }

      {
        // Enable LINE Out
        if (iAP_generalLingo_SetFIDTokenValues_iPodPreference_client(ciPod, gettransid(), 0x03, 0x01, 0x00, &retVal))
        {
          vprint("iPod failed to respond to SFTV_Pref");
        }
        if (retVal)
        {
          vprint("iPod NAKd iPodPref");
        }
      }
      
      {
        // Accessory Name
        char s[] = ACCESSORY_NAME;
        if (iAP_generalLingo_SetFIDTokenValues_accinfo_client(ciPod, gettransid(), 1, safestrlen(s) + 1, s, &retVal))
        {
          vprint("iPod failed to respond to SFTV_Identify");
        }
        if (retVal)
        {
          vprint("iPod NAKd accInfo 1");
        }
      }
      {
        // Firmware Version
        char s[] = ACCESSORY_FIRMWARE_VERSION;
        if (iAP_generalLingo_SetFIDTokenValues_accinfo_client(ciPod, gettransid(), 4, 3, s, &retVal))
        {
          vprint("iPod failed to respond to SFTV_Identify");
        }
        if (retVal)
        {
          vprint("iPod NAKd accInfo 4");
        }
      }    
      {
        // Hardware Version
        char s[] = ACCESSORY_HARDWARE_VERSION;
        if (iAP_generalLingo_SetFIDTokenValues_accinfo_client(ciPod, gettransid(), 5, 3, s, &retVal))
        {
          vprint("iPod failed to respond to SFTV_Identify");
        }
        if (retVal)
        {
          vprint("iPod NAKd accInfo 5");
        }
      }    
      {
        // Manufacturer
        char s[] = ACCESSORY_MANUFACTURER;
        if (iAP_generalLingo_SetFIDTokenValues_accinfo_client(ciPod, gettransid(), 6, safestrlen(s) + 1, s, &retVal))
        {
          vprint("iPod failed to respond to SFTV_Identify");
        }
        if (retVal)
        {
          vprint("iPod NAKd accInfo 6");
        }
      }    
      {
        // Model Number
        char s[] = ACCESSORY_MODEL_NUMBER;
        if (iAP_generalLingo_SetFIDTokenValues_accinfo_client(ciPod, gettransid(), 7, safestrlen(s) + 1, s, &retVal))
        {
          vprint("iPod failed to respond to SFTV_Identify");
        }
        if (retVal)
        {
          vprint("iPod NAKd accCaps");
        }
      }
      
      if (iAP_generalLingo_EndIDPS_client(ciPod, gettransid(), 0, &retVal))
      {
        vprint("iPod failed to respond to SFTV_Identify");
      }
      if (retVal)
      {
        vprint("iPod NAKd endIDPS");
      }
      return 0;  
    }
    
  }
  return 1;
}
