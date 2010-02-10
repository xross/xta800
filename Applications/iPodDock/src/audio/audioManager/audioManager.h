#ifndef AUDIOMANAGER_H_
#define AUDIOMANAGER_H_

// Include User Options for which audio interface to use
#include "global.h"
#include "flash_local.h"
#include "iflash.h"

// Buffer size for the audio manager
#define AUDIO_BUF_SIZE 512
#define MASK 0x1FF

// Enable or disable run-time audio buffer underflow/overflow checks
//#define AUDIO_BUFFER_CHECKS

// SPDIF Options
#define ENABLE_SPDIF_COPY_PROTECTION            0x01
#define SPDIF_CATAGORY_CODE                     0x7A

// Structure for containing audio ports and channels
typedef struct 
{
  buffered out port:32 pSPDIF; 
  buffered out port:32 p_sdout;
  buffered out port:32 p_bclk;
  port p_mclk;
#ifdef LR_CLK_1B_PORT
  buffered out port:32 p_lrclk;
#endif     
  clock clk_audio_mclk;
  clock clk_audio_bclk;
} s_audio;

#include "audioflash.h"

#ifdef __XC__
void audioWaitForInput(streaming chanend cSOF, streaming chanend cAudioMixer);
#elif defined __STDC__
void audioWaitForInput(unsigned cSOF, streaming_chanend cAudioMixer);
#endif

/*
 * audioManager
 * Generic audio manager thread for outputing audio data
 * Also supports volume control and SOF generation from Master Clock
 */
#ifdef __XC__
void audioManager(
		  streaming_chanend cAudioStreaming, streaming chanend cSOFGen, streaming chanend cAudioMixer,
    s_audio &audioports);
#elif defined __STDC__
streaming_chanend cAudioStreaming, unsigned cSOFGen, unsigned cAudioMixer,
s_audio &audioports);
#endif

/*
 * audioPlayerInit
 * Initialise audio ports and clocks
 */
void audioPlayerInit(out port p32, s_audio &audioports);

#define increment(v,a) { v = (v+a) & MASK; }
#define incrementwptr(a) { increment(wptr, a); nelements+=a; }
#define incrementrptr(a) { increment(rptr, a); nelements-=a; }

extern int nval;
static inline void newnval(int a)
{
  nval = a;
}

#pragma unsafe arrays
static inline void check_flash(streaming_chanend cAudioStreaming, streaming chanend cSOFGen, int val)
{  
  if (FLASH_MAGIC[iflash_state] == val)
  {
    iflash_state ++;
    if (iflash_state == FLASH_MAGIC_LEN)
    {
      //      printstrln("Magic found");
      
#pragma xta label "audio_flashupgrade"
      {        
        if (flash_pre())
          vprintstrln("Failed to connect to flash");
        iflash_upgrade(cAudioStreaming, cSOFGen);
        flash_post();
      }
      iflash_state = 0;
    }
  }
  else
  {
    iflash_state = 0;
  }
};

static inline int getSpdifControl(int frequency)
{
  switch (frequency)
  {
  case 44100:
    return (0x00 << 24) | (0) | (SPDIF_CATAGORY_CODE << 8) | ((~ENABLE_SPDIF_COPY_PROTECTION & 1) << 2);
  case 48000:
    return (0x02 << 24) | (0) | (SPDIF_CATAGORY_CODE << 8) | ((~ENABLE_SPDIF_COPY_PROTECTION & 1) << 2);
  case 32000:
    return (0x03 << 24) | (0) | (SPDIF_CATAGORY_CODE << 8) | ((~ENABLE_SPDIF_COPY_PROTECTION & 1) << 2);
  default:
    return 0;
  }
}

#pragma unsafe arrays
static inline void checkForMixer(streaming chanend cAudioMixer, int &freqcount, signed short audiodata[],
    int &running, streaming chanend cSOFGen, int &audiovolume)
{
  if (running)
  {
    newnval(2);
    select
    {
      case cAudioMixer :> int cmd:
    #pragma xta label "audio_48_freqadjust"
        switch (cmd)
        {
        case 0:
        {
          int val;
          cAudioMixer :> val;
          switch (val)
          {
          case 0:
            freqcount = 112896;
            break;
          case 1:
            freqcount = 122880;
            break;
          }
        }
        break;
        case 1:
        {
    #pragma xta label "audio_48_wipebuf"
          for (int i=0; i<AUDIO_BUF_SIZE; i++)
            audiodata[i] = 0;
        }
        break;
        case 2:
        {
          running = 0;
        }
        break;
        case 3:
          #pragma xta label "audio_48_waitforinput"
          audioWaitForInput(cSOFGen, cAudioMixer);
          break;
        case 4:
    #pragma xta label "audio_48_voladjust"
          cAudioMixer :> audiovolume;
          if (audiovolume == 255)
            audiovolume = 256;
          break;
        }
      break;
    default:
      break;
    }
    newnval(3);
  }
}

#pragma unsafe arrays
static inline void moveDataOn(int &nelements, int &rptr, int &audiorunning)
{
  if (audiorunning && nelements > 0)
  {
    incrementrptr(2);
  }
  #ifdef AUDIO_BUFFER_CHECKS
  else if (audiorunning)
  {
    vprintstrln("Audio buffer underflow");
  }
  #endif
  else
  {
    audiorunning = 0;
  }
}

static inline void checkForData(streaming_chanend cAudioStreaming, streaming chanend cSOFGen, signed short audiodata[], int &wptr, int &audiorunning, int &nelements, int audiovolume)
{
  unsigned val;
  newnval(4);
  select
  {
#ifdef USE_STREAMING_CHANNELS
    case cAudioStreaming :> val:
#else
    case inuint_byref(cAudioStreaming, val):              
#endif
      newnval(0x41);
      check_flash(cAudioStreaming, cSOFGen, val);
      
      audiodata[wptr] = ((int)val * audiovolume) >> 8;
#ifdef USE_STREAMING_CHANNELS
      cAudioStreaming :> val;
#else
      inuint_byref(cAudioStreaming, val);
#endif
      newnval(0x42);
      check_flash(cAudioStreaming, cSOFGen, val);

      audiodata[wptr + 1] = ((int)val * audiovolume) >> 8;
      if (nelements > AUDIO_BUF_SIZE - 2 )
      {
#ifdef AUDIO_BUFFER_CHECKS
        vprintstrln("Audio buffer overflow");
#endif
      }
      else
      {
        incrementwptr(2);
      }
      audiorunning |= (nelements > (AUDIO_BUF_SIZE >> 1));
      break;
    default:
      break;
  }
  newnval(5);
}

static inline int mysext(int x, int n)
{
#if 1
  __builtin_sext(x,n);
  return x;
#else
  int retval;
  __asm__ ("sext %1, %2; mov %0, %1": "=r" (retval): "r" (x), "r" (n));
  return retval;
#endif
}

static inline int parity32(unsigned x)
{
  crc32(x, 0, 1);
  return (x & 1);
}

static inline void audioSOFLoop(streaming chanend cSOFGen, int &count, int freqcount)
{
  count += 2560;
  if (count > freqcount)
  {
    newnval(6);
    select
    {
    case cSOFGen :> int _:
      cSOFGen <: 1;
      break;
    default:
      break;
    }
    count -= freqcount;
    newnval(7);
  }
}

#endif /*AUDIOMANAGER_H_*/
