#include <xs1.h>
#include <xclib.h>
#include "global.h"
#include "audioManager.h"
#include "flashlib.h"
#include "iflash.h"

/*
 * Audio Manager thread for I2S output only
 */
#if defined I2SOUTPUT

signed short audiodata[AUDIO_BUF_SIZE];
static int audiovolume = 0;
static int count = 0;

#pragma unsafe arrays
void audioManager_48kHz(
    streaming_chanend cAudioStreaming, streaming chanend cSOFGen, streaming chanend cAudioMixer,
    s_audio &audioports, int freqcount, int frequency)
{
  
  int rptr = 0;
  int wptr = 0;  
  int nelements = 0;
  
  int running = 1;
  int audiorunning = 0;
  
  while (running)
  {
      // Left Channel
      audioports.p_sdout <: bitrev(audiodata[rptr] << 16);
#ifdef LR_CLK_1B_PORT
      audioports.p_lrclk <: 0x80000000;
#endif
#pragma xta endpoint "audio_48_left_o0"
      audioports.p_bclk <: 0xCCCCCCCC;
      audioSOFLoop(cSOFGen, count, freqcount);
      for (int i=0; i<3; i++)
      {
#pragma xta endpoint "audio_48_left_o1"
        audioports.p_bclk <: 0xCCCCCCCC;
#pragma xta label "cfd"
        checkForData(cAudioStreaming, cSOFGen, audiodata, wptr, audiorunning, nelements, audiovolume);
      }

      // Right Channel
      audioports.p_sdout <: bitrev(audiodata[rptr+1] << 16);
#ifdef LR_CLK_1B_PORT
      audioports.p_lrclk <: 0x7FFFFFFF;
#endif
      
      // BCLK0
#pragma xta endpoint "audio_48_right_o0"
      audioports.p_bclk <: 0xCCCCCCCC;
      moveDataOn(nelements, rptr, audiorunning);

      for (int i=0; i<3; i++)
      {
        checkForMixer(cAudioMixer, freqcount, audiodata, running, cSOFGen, audiovolume);
#pragma xta endpoint "audio_48_right_o1"
        audioports.p_bclk <: 0xCCCCCCCC;
        checkForData(cAudioStreaming, cSOFGen, audiodata, wptr, audiorunning, nelements, audiovolume);
      }
  }
}

#pragma unsafe arrays
void audioManager_32kHz(
    streaming_chanend cAudioStreaming, streaming chanend cSOFGen, streaming chanend cAudioMixer,
    s_audio &audioports, int freqcount, int frequency)
{
  
  int rptr = 0;
  int wptr = 0;
  int count = 0;
  int nelements = 0;
  
  int running = 1;
  int audiorunning = 0;
  
  while (running)
  {
      // Left Channel
      audioports.p_sdout <: bitrev(audiodata[rptr] << 16);
#ifdef LR_CLK_1B_PORT
      audioports.p_lrclk <: 0x80000000;
#endif
#pragma xta endpoint "audio_32_left_o0"
      audioports.p_bclk <: 0xC71C71C7;
      audioports.p_bclk :16 <: 0x1C71;
      audioSOFLoop(cSOFGen, count, freqcount);
      
      for (int i=0; i<3; i++)
      {
#pragma xta endpoint "audio_32_left_o1"
        audioports.p_bclk <: 0xC71C71C7;
        audioports.p_bclk :16 <: 0x1C71;
        checkForData(cAudioStreaming, cSOFGen, audiodata, wptr, audiorunning, nelements, audiovolume);
      }

      
      // Right Channel
      audioports.p_sdout <: bitrev(audiodata[rptr+1] << 16);
#ifdef LR_CLK_1B_PORT
      audioports.p_lrclk <: 0x7FFFFFFF;
#endif
      
      // BCLK0
#pragma xta endpoint "audio_32_right_o0"
      audioports.p_bclk <: 0xC71C71C7;
      audioports.p_bclk :16 <: 0x1C71;
      moveDataOn(nelements, rptr, audiorunning);

      for (int i=0; i<3; i++)
      {
        checkForMixer(cAudioMixer, freqcount, audiodata, running, cSOFGen, audiovolume);
#pragma xta endpoint "audio_32_right_o1"
        audioports.p_bclk <: 0xC71C71C7;
        audioports.p_bclk :16 <: 0x1C71;
        checkForData(cAudioStreaming, cSOFGen, audiodata, wptr, audiorunning, nelements, audiovolume);
      }
  }
}

#pragma unsafe arrays
void audioManager(
    streaming_chanend cAudioStreaming, streaming chanend cSOFGen, streaming chanend cAudioMixer,
    s_audio &audioports)
{
  int mode = 48000;
  timer audiotimer;
  int time;
  
  set_thread_fast_mode_on();
  
  // Tell EP manager we do not require notification on device disconnect
  cAudioStreaming <: 0;

  // Init buf
  for (int i=0; i<AUDIO_BUF_SIZE; i++)
    audiodata[i] = 0;
  
  cSOFGen <: 1;
  
  while (1)
  {
    switch (mode)
    {
    case (44100):
      audioManager_48kHz(cAudioStreaming, cSOFGen, cAudioMixer, audioports, 112896, mode);
      break;
    case (48000):
      audioManager_48kHz(cAudioStreaming, cSOFGen, cAudioMixer, audioports, 122880, mode);
      break;
    case (32000):
      audioManager_32kHz(cAudioStreaming, cSOFGen, cAudioMixer, audioports, 81920, mode);
      break;      
    default:
      vprintstr("Incorrect sample rate selected");
      vprintintln(mode);
      while (1);
      break;
    }
    cAudioMixer :> mode;
    
    // Write a few bclks to bring the output to 0
    {
      int lrval = 0x80000000;
      for (int i=0; i<256; i++)
      {
        audioports.p_sdout <: 0;
#ifdef LR_CLK_1B_PORT
        audioports.p_lrclk <: lrval;
#endif
        lrval = ~lrval;
        audioports.p_bclk <: 0xCCCCCCCC ;
        audioports.p_bclk <: 0xCCCCCCCC ;
        audioports.p_bclk <: 0xCCCCCCCC ;
        audioports.p_bclk <: 0xCCCCCCCC ;
        
        count += 2560;
        if (count > 122880)
        {
          select
          {
          case cSOFGen :> int _:
            cSOFGen <: 1;
            break;
          default:
            break;
          }
          count -= 122880;
        }
      }
    }
    
    // Ack now so the iPod thread can change the oscillator if needed
    cAudioMixer <: 0;
    
    // Stop bclk for a mS
    {
      int running = 1;
      audioports.p_bclk :1 <: 0x0;
      audiotimer :> time;
      while (running)
      {
        select
        {
        case audiotimer when timerafter(time + 1000*XS1_TIMER_MHZ) :> time:
          cSOFGen :> int _;
          cSOFGen <: 1;
          break;
        case cAudioMixer :> int _:
          running = 0;
          break;
        default:
          break;
        }
      }
    }
  }
}

// XTA Commands
//#pragma xta command "config verbosity -10"
#pragma xta command "config freq 500"
#pragma xta command "add exclusion audio_flashupgrade"
#pragma xta command "add exclusion audio_48_exit"
#pragma xta command "add exclusion audio_48_waitforinput"
#pragma xta command "add exclusion audio_48_wipebuf"
#pragma xta command "add exclusion audio_48_freqadjust"
#pragma xta command "analyse path audio_48_left_o0 audio_48_left_o1"
#pragma xta command "set required - 2600 ns"
#pragma xta command "add exclusion audio_48_right_o0"
#pragma xta command "analyse path audio_48_left_o1 audio_48_left_o1"
#pragma xta command "set required - 2600 ns"
#pragma xta command "remove exclusion audio_48_right_o0"
#pragma xta command "add exclusion audio_48_left_o1"
#pragma xta command "analyse path audio_48_left_o1 audio_48_right_o0"
#pragma xta command "set required - 2600 ns"
#pragma xta command "remove exclusion audio_48_left_o1"
#pragma xta command "analyse path audio_48_right_o0 audio_48_right_o1"
#pragma xta command "set required - 2600 ns"
#pragma xta command "add exclusion audio_48_left_o0"
#pragma xta command "analyse path audio_48_right_o1 audio_48_right_o1"
#pragma xta command "set required - 2600 ns"
#pragma xta command "remove exclusion audio_48_left_o0"
#pragma xta command "add exclusion audio_48_right_o1"
#pragma xta command "analyse path audio_48_right_o1 audio_48_left_o0"
#pragma xta command "set required - 2600 ns"
#pragma xta command "remove exclusion audio_48_right_o1"
#pragma xta command "remove exclusion audio_48_wipebuf"
#pragma xta command "remove exclusion audio_48_freqadjust"
#pragma xta command "remove exclusion audio_48_exit"
#pragma xta command "remove exclusion audio_48_waitforinput"
#pragma xta command "remove exclusion audio_flashupgrade"
#pragma xta command "print summary"

#endif
