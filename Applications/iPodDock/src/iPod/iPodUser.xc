#include "iPod.h"
#include "remote_cmds.h"
#include "flash_local.h"

int dspon = 0;
int ledval = 0;
int extended_mode = 0;

/*
 * iPod_UserPostAuth:
 * Called when authentication has completed
 */
int iPod_UserPostAuth(streaming_chanend ciPodHID, streaming chanend cAudioMixer[3], streaming chanend cLED, streaming chanend cI2CReset)
{
  // Reset extended mode status
  in_extended_mode = 0;
  
  // Post authentication commands
#ifdef DSP_ENABLED
    dspon = 0;
    cAudioMixer[1] <: dspon;
    ledval &= ~2;
    cLED <: ledval;
    cLED :> int _;
#endif
    
  // Check digital audio lingo version
  {
    int majorversion, minorversion;
    int retval;
    
    if (iAP_generalLingo_RequestLingoProtocolVersion_client(ciPodHID, gettransid(), audioLingo, majorversion, minorversion))
      vprint("RLPV Failed");
    
    if (majorversion <= 1 && minorversion <= 1)
    {
#if EXT_REMOTE_LINGO != 0
      // Enter extended mode
      if (iPodEnterExtended(ciPodHID, cAudioMixer, cLED, cI2CReset))
        return 1;

      if (iPodExtendedDisplayImage(ciPodHID, cAudioMixer, cLED, cI2CReset))
        return 1;

       // Tell the ipod to play
       if (iAP_extendedLingo_PlayCurrentSelection_client(ciPodHID, gettransid(), 0, retval)) {
         vprint("Play current selection failed");
       };
#endif
    }
  }

  return 0;
}

/*
 * iPod_IR:
 * Called on receipt of an Infra-Red command
 */
int iPod_IR(streaming_chanend ciPodHID, unsigned irval, streaming chanend cAudioMixer[3], streaming chanend cLED, streaming chanend cI2CReset)
{
  if (irval >= nButtons)
    return 0;
  
  if (irval == remoteMenu)
  {
#ifdef DSP_ENABLED
    dspon = !dspon;
    cAudioMixer[1] <: dspon;
    ledval ^= 2;
    cLED <: ledval;
    cLED :> int _;
#endif
  }
  else
  {
    int retval;
    if (in_extended_mode)
    {
      if (irval == remoteVolumeUp)
      {
        volume += 25;
        volume = volume > 255 ? 255 : volume;
        cAudioMixer[0] <: 4;
        cAudioMixer[0] <: volume;
      }
      else if (irval == remoteVolumeDown)
      {
        volume -= 25;
        volume = volume < 0 ? 0 : volume;
        cAudioMixer[0] <: 4;
        cAudioMixer[0] <: volume;
      }
      else
      {
#if EXT_REMOTE_LINGO != 0
        irval = extendedButtonValues[irval];
        if (irval)
        {
          if(iAP_extendedLingo_PlayControl_client(ciPodHID, gettransid(), irval, retval))
          {
            return 1;
          }
        }
#endif
      }
    }
    else
    {
      irval = simpleContextButtonValues[irval];
      if (irval)
      {
        if (iAP_simpleLingo_CONTEXTBUTTONSTATUS_client(ciPodHID, gettransid(), byterev(irval)))
        {
          return 1;
        }
      }
    }
  }

  return 0;
}

/*
 * Called every IPOD_TIME_INTERVAL by the iPod stack
 */ 
void iPod_timer(streaming_chanend ciPodHID, streaming chanend cAudioMixer[3], streaming chanend cLED, streaming chanend cI2CReset)
{
  char data[128];
  

  ledval ^= 1;
  cLED <: ledval;  
  cLED :> int _;
}

/*
 * iPod_UserUnsolicited:
 * Called when an unknown unsolicited iAP message was received
 */
int iPod_UserUnsolicited(streaming_chanend ciPodHID, unsigned cmdval, int rettransid, streaming chanend cAudioMixer[3], streaming chanend cLED, streaming chanend cI2CReset)
{
  switch (cmdval)
  {
    case CMD_equalizerLingo_GetCurrentEQIndex:
    case CMD_equalizerLingo_SetCurrentEQIndex:
    case CMD_equalizerLingo_GetEQSettingCount:
    case CMD_equalizerLingo_GetEQIndexName:
      iPod_Equalizer_Rx(ciPodHID, cmdval, rettransid, cAudioMixer);
      break;
    default:
      // Unrecognised Unsolicited Command
#if 0
      vprintstrln("Unsupported IAP command");
      vprintintln(retVal);
      while (1);
#endif
      break;
  }
  return 0;
}

