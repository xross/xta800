#include "iPod.h"

#include "audio_client.h"
#include "remote_cmds.h"
#include "iic.h"

timer tIPod;

int volume = 256;
int iPodfirstGAI;
int iPodusingIDPS;
int audio_enabled = 0;

/*
 * iPodGAI:
 * Called on receipt of a GetAccessoryInfo
 */
int iPodGAI(streaming_chanend ciPod, int rettransid)
{
  int infoType, len;
  unsigned char parameters[7];
  int retval;
  ciPod :> infoType;
  ciPod :> len;
  for (int i = 0; i < len; i++)
    ciPod :> parameters[i];
  
  switch (infoType)
  {
    case (0): // Accessory Info Caps
      if (iAP_generalLingo_RetAccessoryInfo_0_client(ciPod, rettransid, infoType, 0b11110011, retval))
        vprint("iPod failed to respond to RAI 0");
      break;
    case (1): // Accessory Name
    {
      char name[] = ACCESSORY_NAME;
      if (iAP_generalLingo_RetAccessoryInfo_1_client(ciPod, rettransid, infoType, safestrlen(name) + 1, name, retval))
        vprint("iPod failed to respond to RAI 1");
    }
    break;
    case (4): // Accessory Firmware Version
    {
      int fwversion[3] = ACCESSORY_FIRMWARE_VERSION;
      if (iAP_generalLingo_RetAccessoryInfo_4_client(ciPod, rettransid, infoType, fwversion, retval))
        vprint("iPod failed to respond to RAI 4");
    }
    break;
    case (5): // Accessory Hardware Version
    {
      int hwversion[3] = ACCESSORY_HARDWARE_VERSION;
      if (iAP_generalLingo_RetAccessoryInfo_4_client(ciPod, rettransid, infoType, hwversion, retval))
        vprint("iPod failed to respond to RAI 4");
    }
    break;
    case (6): // Accessory Manufacturer
    {
      char name[] = ACCESSORY_MANUFACTURER;
      if (iAP_generalLingo_RetAccessoryInfo_1_client(ciPod, rettransid, infoType, safestrlen(name) + 1, name, retval))
        vprint("iPod failed to respond to RAI 6");
    }
    break;
    case (7): // Accessory Model Number
    {
      char name[] = ACCESSORY_MODEL_NUMBER;
      if (iAP_generalLingo_RetAccessoryInfo_1_client(ciPod, rettransid, infoType, safestrlen(name) + 1, name, retval))
        vprint("iPod failed to respond to RAI 7");
    }
    break;
  }

  if (iPodfirstGAI && !iPodusingIDPS)
  {
    iPodStartAuth(ciPod);
  }
  iPodfirstGAI = 0;
  return 0;
}

/*
 * iPodInit
 * Called on every iPod connection
 */
int iPodInit(streaming_chanend ciPod, streaming chanend cI2CReset)
{
  int time;
  int retVal;
  
  transid = 0;
  audio_enabled = 0;
  
  // Retrieve Auth protocol, version and device ID from CP
  if (coProcessorInit())
    vprint("coProcessor failed init");

  if (iPodDevAuthInit())
    while (1);
  
  iPodfirstGAI = 1;

  tIPod :> time;
  // Complete 80mS wait for iPod to boot  
  tIPod when timerafter(time + 80 * XS1_TIMER_KHZ) :> int _;
  
#ifdef IPOD_UART
  // Send sync byte
  iAP_HAL_syncbyte();
#endif

  // Try IDPS
  if (iPodTryIDPS(ciPod))
  {
    iPodusingIDPS = 0;
    transid = -1;
    
    // First IDL to cancel current auth
    if (iAP_generalLingo_IdentifyDeviceLingos_client(ciPod, gettransid(), GENERAL_LINGO, 0x0, 0x0, retVal))
    {
      vprint("iPod ignored comms");    
      iAP_ctrlLingo_reset(ciPod);
    }
    if (retVal)
      vprint("iPod NACKed First IDL");  
  }

  return 0;
}

/*
 * iPodPostAuthSteps
 * Called when authentication completes
 */
int iPodPostAuthSteps(streaming_chanend ciPodHID, streaming chanend cAudioMixer[3], streaming chanend cLED, streaming chanend cOsc, streaming_chanend ciPodAudio, streaming chanend cI2CReset)
{
  int retval;
#if DISPLAY_REMOTE_LINGO != 0
  {
    int retlen;
    char ret[2];
    if (iAP_displayLingo_GetiPodStateInfo_client(ciPodHID, gettransid(), 4, retlen, ret))
      vprint("iPod didn't ack GISI");
    if (retlen == 0)
      vprint("iPod nacked GISI");
      
    volume = ret[1] * (ret[0]==0);
  }
  
  if (iAP_displayLingo_SetRemoteEventNotification_client(ciPodHID, gettransid(), 1 << 4, retval))
    vprint("iPod didn't ack setremoveeventnote");
  
  // Enable streaming audio
  
  if (iPod_UserPostAuth(ciPodHID, cAudioMixer, cLED, cI2CReset))
    return 1;
#endif
  return 0;
}

void iPodChangeSample(int rate, int rettransid, streaming_chanend ciPodHID, streaming chanend cAudioMixer[3], streaming chanend cOsc, streaming_chanend ciPodAudio)
{
  int now;
  
  cAudioMixer[0] <: 2; cAudioMixer[0] <: rate;
  cAudioMixer[0] :> int _;
  switch(rate)
  {
  case 44100:
    cOsc <: 0;
    break;
  case 32000:
    cOsc <: 1;
    break;
  case 48000:
    cOsc <: 1;
    break;    
  default:
    cAudioMixer[0] <: 0;
    return 1;
  }
  cOsc :> int _;
  tIPod :> now;
  tIPod when timerafter(now + 5 * XS1_TIMER_KHZ) :> int _;
  cAudioMixer[0] <: 0;
  // Wait for osc to change
  audio_client_set_sample_rate(ciPodAudio, rate);
#ifdef DSP_ENABLED
  cAudioMixer[1] <: rate;      
#endif
  iAP_audioLingo_AccAck_client(ciPodHID, rettransid, 0x00, 0x04);
}

/*
 * iPodMainLoop:
 * Called in a while (1)
 */
int iPodMainLoop(streaming_chanend ciPodHID, streaming chanend cI2CReset, streaming chanend cAudioMixer[3], streaming chanend cOsc, streaming_chanend ciPodAudio, streaming chanend cIR, streaming chanend cLED)
{
  int authenticated = 0;
  int now;
  
  authenticated = 0;      
  tIPod :> now;  
  cLED <: 1;
  cLED :> int _;
    
  while (1)
  {
    int retVal;
    
    if (authenticated)
    {
      select
      {
      case cIR :> int irval:
        iPod_IR(ciPodHID, irval, cAudioMixer, cLED, cI2CReset);
        break;
      case tIPod when timerafter(now + IPOD_TIME_INTERVAL) :> now:
        iPod_timer(ciPodHID, cAudioMixer, cLED, cI2CReset);
        break;
      default:
        break;
      }
    }
    
    // Check for unsolicited messages
    {
      int ret;
      ciPodHID <: 0;
      ciPodHID :> ret;
      if (ret)
        return 1;
      ciPodHID <: unsolicitedLingo;
      ciPodHID :> retVal;
    }
    
    if (retVal != CMD_none)
    {
      int rettransid;
      ciPodHID :> rettransid;
      switch (retVal)
      {
        case (CMD_none):
          break;
        case (CMD_generalLingo_GetDevAuthenticationInfo):
          iPodDevAuth(ciPodHID, rettransid);
          break;
        case (CMD_generalLingo_NotifyiPodStateChange):
          ciPodHID :> int _;
          break;
        case (CMD_generalLingo_GetDevAuthenticationSignature):
        {
          {
            int challen;
            char chal[128];
            int retrycount;
            ciPodHID :> challen;
    
            if (challen > 128)
            {
              vprint("Challenge too long");
            }
            for (int i=0; i<challen; i++)
              ciPodHID :> chal[i];
            ciPodHID :> retrycount;
            
            if (iPodAuthGetDevAuthenticationSignature(ciPodHID, challen, chal, rettransid))
              vprint("GDAS Failed");
          }
          authenticated = 1;

          if (iPodPostAuthSteps(ciPodHID, cAudioMixer, cLED, cOsc, ciPodAudio, cI2CReset))
            return 1;
        }
        break;
    #if AUDIO_LINGO != 0
        case (CMD_audioLingo_GetAccSampleRateCaps):
        {
          int rates[3] = {0x7D0000, 0x44AC0000, 0x80BB0000};
          int nrates = 3;
          if (iAP_audioLingo_RetAccSampleRateCaps_client(ciPodHID, rettransid, nrates, rates))
            vprint("iPOD failed to ack RetAccSampleRateCaps");
        }
        break;
        case (CMD_audioLingo_NewiPodTrackInfo):
        {
          int samplerate, soundcheck, trackvol;
          ciPodHID :> samplerate;
          ciPodHID :> soundcheck;
          ciPodHID :> trackvol;
          rettransid = gettransid();
          if (samplerate == 44100)
          {
            iPodChangeSample(44100, rettransid, ciPodHID, cAudioMixer, cOsc, ciPodAudio);
          }
          else if (samplerate == 48000)
          {
            iPodChangeSample(48000, rettransid, ciPodHID, cAudioMixer, cOsc, ciPodAudio);
          }
          else if (samplerate == 32000)
          {
            iPodChangeSample(32000, rettransid, ciPodHID, cAudioMixer, cOsc, ciPodAudio);
          }
          else
          {
            // NACK
            iAP_audioLingo_AccAck_client(ciPodHID, rettransid, 0x04, 0x04);            
          }
          
          if (!audio_enabled)
          {
            iAP_ctrlLingo_enableAlternate(ciPodHID);
            audio_enabled = 1;
          }
        }
        break;
    #endif
        case (CMD_generalLingo_GetAccessoryInfo):
        {
          if (iPodGAI(ciPodHID, rettransid))
            return 1;
        }
        break;
        case (CMD_displayLingo_RemoteEventNotification):
        {
          int dlen;
          char data[8];
          ciPodHID :> dlen;
          if (dlen > 8)
          {
            vprint("REN Data too long");
          }
          for (int i=0; i<dlen; i++)
          {
            ciPodHID :> data[i];
          }
          switch (data[0])
          {
          case (4):
            volume = data[2] * (data[1]==0);
            // Mute/volume setting
            cAudioMixer[0] <: 4;
            cAudioMixer[0] <: volume;
            break;
          }
        }
        break;
        default:
          if (iPod_UserUnsolicited(ciPodHID, retVal, rettransid, cAudioMixer, cLED, cI2CReset))
            return 1;
        break;
      }
    }
  }
  return 0;
}

/*
 * iPodMain
 * Main iPod Control Stack
 */
void iPodMain(streaming_chanend ciPodHID, streaming chanend cI2CReset, streaming chanend cAudioMixer[3], streaming chanend cOsc, streaming_chanend ciPodAudio, streaming chanend cIR, streaming chanend cLED)
{  
  int time;
#ifdef ENFORCE_FAST_MODE
  set_thread_fast_mode_on();
#endif
  
  iic_initialise();
  
  // Put I2C devices in reset
  tIPod :> time;
  cI2CReset <: 0;
  cI2CReset :> int _;
  tIPod when timerafter(time + 50 * XS1_TIMER_KHZ) :> time;
  
  // Bring I2C devices out of reset
  cI2CReset <: 1;
  cI2CReset :> int _;
  tIPod when timerafter(time + 50 * XS1_TIMER_KHZ) :> time;
  
  // Tell EP manager we require notification on device disconnect
  // on HID channel, not on audio channel.
  ciPodHID <: 1;
  ciPodAudio <: 0;

  cAudioMixer[0] <: 4;
  cAudioMixer[0] <: 256;
  while (1)
  {    
    // Wipe buffer
    cAudioMixer[0] <: 1;
    cLED <: 0;
    cLED :> int _;
    
    if (!iPodInit(ciPodHID, cI2CReset))
    {
      iPodMainLoop(ciPodHID, cI2CReset, cAudioMixer, cOsc, ciPodAudio, cIR, cLED);
    }
  }
}

