#include <xs1.h>
#include <xclib.h>
#include "global.h"
#include "audioManager.h"


/*
 * Audio Manager thread for S/PDIF Output only
 */
#if defined SPDIFOUTPUT

#define increment(v,a) { v = (v+a) & MASK; }
#define incrementwptr(a) { increment(wptr, a); nelements+=a; }
#define incrementrptr(a) { increment(rptr, a); nelements-=a; }

static int inline parity32(unsigned x)
{
  crc32(x, 0, 1);
  return (x & 1);
}

unsigned char data_words_48[32] = {
    0x0F, 0x0F,
    0xF0, 0xF3,
    0xF0, 0xCF,
    0x0F, 0x33,
    0xF3, 0x0F,
    0x0C, 0xF3,
    0x0C, 0xCF,
    0xF3, 0x33,
    0xCF, 0x0F,
    0x30, 0xF3,
    0x30, 0xCF,
    0xCF, 0x33,
    0x33, 0x0F,
    0xCC, 0xF3,
    0xCC, 0xCF,
    0x33, 0x33

};

unsigned char preamble_words_48[6] = {
    0x30, 0x3F,
    0x0C, 0x3F,
    0x03, 0x3F
};

#define CONTROL_LEFT    (0x00100004 << 1)
#define CONTROL_RIGHT   (0x00200004 << 1)
#define VALIDITY        (0x00000000 << 24)

signed short audiodata[AUDIO_BUF_SIZE];
static int audiovolume = 0;
static int count = 0;

#pragma unsafe arrays
void audioManager_48kHz(
    streaming_chanend cAudioStreaming, streaming chanend cSOFGen, streaming chanend cAudioMixer,
    s_audio &audioports, int freqcount)
{
  int rptr = 0;
  int wptr = 0;  
  unsigned word;
  unsigned xor;
  unsigned encoded_preamble, encoded_byte;
  unsigned sample=0, control, parity;
  
  int controlLeft  = CONTROL_LEFT;
  int controlRight = CONTROL_RIGHT;
  int nelements = 0;
  int newblock = 2;
  
  int running = 1;
  int audiorunning = 0;

  xor = 0x0;

  sample = (audiodata[rptr] << 12) & 0x0FFFFFF0 ;
  control = (controlLeft & 2);
  parity = parity32(sample | control | VALIDITY);
  control = bitrev(control | parity) | VALIDITY | newblock | sample;
  
  while (running)
  {
    int loopcount = 192;
    
    while (loopcount)
    {
      
      // Left Channel
      word = control;
      encoded_preamble = preamble_words_48[(word & 0xF)*2+1] ^ xor;
      
#pragma xta endpoint "audio_48_left_o0"
      audioports.pSPDIF :8 <: encoded_preamble;
      encoded_preamble = preamble_words_48[(word & 0xF)*2] ^ xor;
#pragma xta endpoint "audio_48_left_o1"
      audioports.pSPDIF :8 <: encoded_preamble;
      xor = __builtin_sext(encoded_preamble, 8) >> 8;  
      word = word >> 4;
      controlLeft >>=1;
      control = (controlRight & 2);
      sample = (audiodata[rptr+1] << 12) & 0x0FFFFFF0 ;
      encoded_byte = data_words_48[(word & 0xF) * 2] ^ xor;  // Xor to invert data if lsab of last data was a 1
#pragma xta endpoint "audio_48_left_o2"
      audioports.pSPDIF :8 <: data_words_48[(word & 0xF)*2+1] ^ xor;
      parity = parity32(sample | control | VALIDITY);
      control = bitrev(parity | control) | VALIDITY;
#pragma xta endpoint "audio_48_left_o3"
      audioports.pSPDIF :8 <: encoded_byte;
      
      count += 2560;
      if (count > freqcount)
      {
        select
        {
        case cSOFGen :> int _:
          cSOFGen <: 1;
          break;
        default:
          break;
        }
        count -= freqcount;
      }
      xor = __builtin_sext(encoded_byte, 8) >> 8;        

      for (int i=0; i<3; i++)
      {
        word = word >> 4;
        encoded_byte = data_words_48[(word & 0xF)*2+1] ^ xor;  // Xor to invert data if lsab of last data was a 1
#pragma xta endpoint "audio_48_left_o4"
        audioports.pSPDIF :8 <: encoded_byte;
        encoded_byte = data_words_48[(word & 0xF) * 2] ^ xor;  // Xor to invert data if lsab of last data was a 1
        xor = __builtin_sext(encoded_byte, 8) >> 8;  
        word = word >> 4;
#pragma xta endpoint "audio_48_left_o5"
        audioports.pSPDIF :8 <: encoded_byte;
        {
          unsigned val;
          select
          {
#ifdef USE_STREAMING_CHANNELS
            case cAudioStreaming :> val:
#else
            case inuint_byref(cAudioStreaming, val):              
#endif
              audiodata[wptr] = ((int)val * audiovolume) >> 8;
              audiorunning |= (nelements > (AUDIO_BUF_SIZE >> 1));
              if (nelements > AUDIO_BUF_SIZE - 2 )
              {
#ifdef AUDIO_BUFFER_CHECKS
                vprintstrln("Audio buffer overflow");
#endif
              }
              else
              {
                incrementwptr(1);
              }

              break;
            default:
              break;
          }
        }
        sample = sample | control | 1;
#pragma xta endpoint "audio_48_left_o6"
        audioports.pSPDIF :8 <: data_words_48[(word & 0xF)*2+1] ^ xor;        
        encoded_byte = data_words_48[(word & 0xF) * 2] ^ xor;  // Xor to invert data if lsab of last data was a 1
#pragma xta endpoint "audio_48_left_o7"
        audioports.pSPDIF :8 <: encoded_byte;        
        xor = __builtin_sext(encoded_byte, 8) >> 8;        
      }

      
      // Right Channel
      word = sample;
      
      encoded_preamble = preamble_words_48[(word & 0xF)*2+1] ^ xor;
#pragma xta endpoint "audio_48_right_o0"
      audioports.pSPDIF :8 <: encoded_preamble;
      encoded_preamble = preamble_words_48[(word & 0xF)*2] ^ xor;
      xor = __builtin_sext(encoded_preamble, 8) >> 8;  
      word = word >> 4;
      if (audiorunning && nelements > 0)
      {
        incrementrptr(2);
      }
#ifdef AUDIO_BUFFER_CHECKS
      else if (audiorunning)
      {
        vprintintln(nelements);
        vprintstrln("Audio buffer underflow");
      }
#endif
      else
      {
        audiorunning = 0;
      }
#pragma xta endpoint "audio_48_right_o1"
      audioports.pSPDIF :8 <: encoded_preamble;      
      loopcount--;
      if (loopcount == 0)
      {
        controlLeft  = CONTROL_LEFT;
        controlRight = CONTROL_RIGHT;
        newblock = 2;
      }
      else
      {
        controlRight >>= 1;
        newblock = 0;
      }
      sample = (audiodata[rptr] << 12) & 0x0FFFFFF0 ;

#pragma xta endpoint "audio_48_right_o2"
      audioports.pSPDIF :8 <: data_words_48[(word & 0xF)*2+1] ^ xor;
      control = (controlLeft & 2);
      encoded_byte = data_words_48[(word & 0xF) * 2] ^ xor;  // Xor to invert data if lsab of last data was a 1
#pragma xta endpoint "audio_48_right_o3"
      audioports.pSPDIF :8 <: encoded_byte;
      parity = parity32(sample | control | VALIDITY);
      control = bitrev(control | parity) | VALIDITY | newblock | sample;
      xor = __builtin_sext(encoded_byte, 8) >> 8;        
      
      for (int i=0; i<3; i++)
      {
        word = word >> 4;
        encoded_byte = data_words_48[(word & 0xF)*2+1] ^ xor;  // Xor to invert data if lsab of last data was a 1
        if (running)
        {
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
        }
#pragma xta endpoint "audio_48_right_o4"
        audioports.pSPDIF :8 <: encoded_byte;
        encoded_byte = data_words_48[(word & 0xF) * 2] ^ xor;  // Xor to invert data if lsab of last data was a 1
        xor = __builtin_sext(encoded_byte, 8) >> 8;  
        word = word >> 4;
#pragma xta endpoint "audio_48_right_o5"
        audioports.pSPDIF :8 <: encoded_byte;
        {
          unsigned val;
          select
          {
#ifdef USE_STREAMING_CHANNELS
            case cAudioStreaming :> val:
#else
            case inuint_byref(cAudioStreaming, val):              
#endif
              audiodata[wptr] = ((int)val * audiovolume) >> 8;
              audiorunning |= (nelements > (AUDIO_BUF_SIZE >> 1));
              if (nelements > AUDIO_BUF_SIZE - 2 )
              {
#ifdef AUDIO_BUFFER_CHECKS
                vprintstrln("Audio buffer overflow");
#endif
              }
              else
              {
                incrementwptr(1);
              }
              break;
            default:
              break;
          }
        }
#pragma xta endpoint "audio_48_right_o6"
        audioports.pSPDIF :8 <: data_words_48[(word & 0xF)*2+1] ^ xor;        
        encoded_byte = data_words_48[(word & 0xF) * 2] ^ xor;  // Xor to invert data if lsab of last data was a 1
#pragma xta endpoint "audio_48_right_o7"
        audioports.pSPDIF :8 <: encoded_byte;
        xor = __builtin_sext(encoded_byte, 8) >> 8;        
      }
    }
  }
  
}

unsigned short data_words_32[32] = {
    0x003F, 0x003F,
    0x0FC0, 0x0FC7,
    0x0FC0, 0x0E3F,
    0x003F, 0x01C7,
    0x0FC7, 0x003F,
    0x0038, 0x0FC7,
    0x0038, 0x0E3F,
    0x0FC7, 0x01C7,
    0x0E3F, 0x003F,
    0x01C0, 0x0FC7,
    0x01C0, 0x0E3F,
    0x0E3F, 0x01C7,
    0x01C7, 0x003F,
    0x0E38, 0x0FC7,
    0x0E38, 0x0E3F,
    0x01C7, 0x01C7
};

unsigned short preamble_words_32[6] = {
    0x01C0, 0x01FF,
    0x0038, 0x01FF,
    0x0007, 0x01FF
};

#pragma unsafe arrays
void audioManager_32kHz(
    streaming_chanend cAudioStreaming, streaming chanend cSOFGen, streaming chanend cAudioMixer,
    s_audio &audioports, int freqcount)
{
  int rptr = 0;
  int wptr = 0;  
  unsigned word;
  unsigned xor;
  unsigned encoded_preamble, encoded_byte;
  unsigned sample=0, control, parity;
  
  int controlLeft  = CONTROL_LEFT;
  int controlRight = CONTROL_RIGHT;
  int newblock = 2;
  int nelements = 0;
  
  int running = 1;
  int audiorunning = 0;

  xor = 0x0;

  sample = (audiodata[rptr] << 12) & 0x0FFFFFF0 ;
  control = (controlLeft & 2);
  parity = parity32(sample | control | VALIDITY);
  control = bitrev(control | parity) | VALIDITY | newblock | sample;
  
  while (running)
  {
    int loopcount = 192;
    
    while (loopcount)
    {
      
      // Left Channel
      word = control;
      encoded_preamble = preamble_words_32[(word & 0xF)*2+1] ^ xor;
      
#pragma xta endpoint "audio_32_left_o0"
      audioports.pSPDIF :12 <: encoded_preamble;
      encoded_preamble = preamble_words_32[(word & 0xF)*2] ^ xor;
#pragma xta endpoint "audio_32_left_o1"
      audioports.pSPDIF :12 <: encoded_preamble;
      xor = __builtin_sext(encoded_preamble, 8) >> 8;  
      word = word >> 4;
      controlLeft >>=1;
      control = (controlRight & 2);
      sample = (audiodata[rptr+1] << 12) & 0x0FFFFFF0 ;
      encoded_byte = data_words_32[(word & 0xF) * 2] ^ xor;  // Xor to invert data if lsab of last data was a 1
#pragma xta endpoint "audio_32_left_o2"
      audioports.pSPDIF :12 <: data_words_32[(word & 0xF)*2+1] ^ xor;
      parity = parity32(sample | control | VALIDITY);
      control = bitrev(parity | control) | VALIDITY;
#pragma xta endpoint "audio_32_left_o3"
      audioports.pSPDIF :12 <: encoded_byte;
      
      count += 3840;
      if (count > freqcount)
      {
        select
        {
        case cSOFGen :> int _:
          cSOFGen <: 1;
          break;
        default:
          break;
        }
        count -= freqcount;
      }
      xor = __builtin_sext(encoded_byte, 8) >> 8;        

      for (int i=0; i<3; i++)
      {
        word = word >> 4;
        encoded_byte = data_words_32[(word & 0xF)*2+1] ^ xor;  // Xor to invert data if lsab of last data was a 1
#pragma xta endpoint "audio_32_left_o4"
        audioports.pSPDIF :12 <: encoded_byte;
        encoded_byte = data_words_32[(word & 0xF) * 2] ^ xor;  // Xor to invert data if lsab of last data was a 1
        xor = __builtin_sext(encoded_byte, 8) >> 8;  
        word = word >> 4;
#pragma xta endpoint "audio_32_left_o5"
        audioports.pSPDIF :12 <: encoded_byte;
        {
          unsigned val;
          select
          {
#ifdef USE_STREAMING_CHANNELS
            case cAudioStreaming :> val:
#else
            case inuint_byref(cAudioStreaming, val):              
#endif
              audiodata[wptr] = ((int)val * audiovolume) >> 8;
              audiorunning |= (nelements > (AUDIO_BUF_SIZE >> 1));
#ifdef AUDIO_BUFFER_CHECKS
              if (nelements > AUDIO_BUF_SIZE - 2 )
              {
                vprintstrln("Audio buffer overflow");
              }
              else
              {
                incrementwptr(1);
              }
#endif
              break;
            default:
              break;
          }
        }
        sample = sample | control | 1;
#pragma xta endpoint "audio_32_left_o6"
        audioports.pSPDIF :12 <: data_words_32[(word & 0xF)*2+1] ^ xor;        
        encoded_byte = data_words_32[(word & 0xF) * 2] ^ xor;  // Xor to invert data if lsab of last data was a 1
#pragma xta endpoint "audio_32_left_o7"
        audioports.pSPDIF :12 <: encoded_byte;        
        xor = __builtin_sext(encoded_byte, 8) >> 8;        
      }

      
      // Right Channel
      word = sample;
      audioports.p_sdout <: bitrev(audiodata[rptr+1] << 16);
#ifdef LR_CLK_1B_PORT
      audioports.p_lrclk <: 0x7FFFFFFF;
#endif
      
      encoded_preamble = preamble_words_32[(word & 0xF)*2+1] ^ xor;
#pragma xta endpoint "audio_32_right_o0"
      audioports.pSPDIF :12 <: encoded_preamble;
      encoded_preamble = preamble_words_32[(word & 0xF)*2] ^ xor;
      xor = __builtin_sext(encoded_preamble, 8) >> 8;  
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
#pragma xta endpoint "audio_32_right_o1"
      audioports.pSPDIF :12 <: encoded_preamble;      
      loopcount--;
      if (loopcount == 0)
      {
        controlLeft  = CONTROL_LEFT;
        controlRight = CONTROL_RIGHT;
        newblock = 2;
      }
      else
      {
        controlRight >>= 1;
        newblock = 0;
      }
      sample = (audiodata[rptr] << 12) & 0x0FFFFFF0 ;

#pragma xta endpoint "audio_32_right_o2"
      audioports.pSPDIF :12 <: data_words_32[(word & 0xF)*2+1] ^ xor;
      control = (controlLeft & 2);
      encoded_byte = data_words_32[(word & 0xF) * 2] ^ xor;  // Xor to invert data if lsab of last data was a 1
#pragma xta endpoint "audio_32_right_o3"
      audioports.pSPDIF :12 <: encoded_byte;
      parity = parity32(sample | control | VALIDITY);
      control = bitrev(control | parity) | VALIDITY | newblock | sample;
      xor = __builtin_sext(encoded_byte, 8) >> 8;        
      
      for (int i=0; i<3; i++)
      {
        word = word >> 4;
        encoded_byte = data_words_32[(word & 0xF)*2+1] ^ xor;  // Xor to invert data if lsab of last data was a 1
        if (running)
        {
          select
          {
            case cAudioMixer :> int cmd:
       #pragma xta label "audio_32_freqadjust"
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
       #pragma xta label "audio_32_wipebuf"
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
                audioWaitForInput(cSOFGen, cAudioMixer);
                break;
              case 4:
       #pragma xta label "audio_32_voladjust"
                cAudioMixer :> audiovolume;
                if (audiovolume == 255)
                  audiovolume = 256;
                break;
              }
            break;
          default:
            break;
          }
        }
#pragma xta endpoint "audio_32_right_o4"
        audioports.pSPDIF :12 <: encoded_byte;
        encoded_byte = data_words_32[(word & 0xF) * 2] ^ xor;  // Xor to invert data if lsab of last data was a 1
        xor = __builtin_sext(encoded_byte, 8) >> 8;  
        word = word >> 4;
#pragma xta endpoint "audio_32_right_o5"
        audioports.pSPDIF :12 <: encoded_byte;
        {
          unsigned val;
          select
          {
#ifdef USE_STREAMING_CHANNELS
            case cAudioStreaming :> val:
#else
            case inuint_byref(cAudioStreaming, val):              
#endif
              audiodata[wptr] = ((int)val * audiovolume) >> 8;
              audiorunning |= (nelements > (AUDIO_BUF_SIZE >> 1));
#ifdef AUDIO_BUFFER_CHECKS
              if (nelements > AUDIO_BUF_SIZE - 2 )
              {
                vprintstrln("Audio buffer overflow");
              }
              else
              {
                incrementwptr(1);
              }
#endif
              break;
            default:
              break;
          }
        }
#pragma xta endpoint "audio_32_right_o6"
        audioports.pSPDIF :12 <: data_words_32[(word & 0xF)*2+1] ^ xor;        
        encoded_byte = data_words_32[(word & 0xF) * 2] ^ xor;  // Xor to invert data if lsab of last data was a 1
#pragma xta endpoint "audio_32_right_o7"
        audioports.pSPDIF :12 <: encoded_byte;
        xor = __builtin_sext(encoded_byte, 8) >> 8;        
      }
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
      audioManager_48kHz(cAudioStreaming, cSOFGen, cAudioMixer, audioports, 112896);
      break;
    case (48000):
      audioManager_48kHz(cAudioStreaming, cSOFGen, cAudioMixer, audioports, 122880);
      break;
    case (32000):
      audioManager_32kHz(cAudioStreaming, cSOFGen, cAudioMixer, audioports, 122880);
      break;      
    default:
      vprintstr("Incorrect sample rate selected");
      vprintintln(mode);
      while (1);
      break;
    }
    cAudioMixer :> mode;
    cAudioMixer <: 0;    
  }
}

// XTA Commands
#pragma xta command "config verbosity -10"
#pragma xta command "add exclusion audio_48_wipebuf"
#pragma xta command "add exclusion audio_48_freqadjust"
#pragma xta command "analyse path audio_48_left_o0 audio_48_left_o2"
#pragma xta command "set required - 1300 ns"
#pragma xta command "analyse path audio_48_left_o1 audio_48_left_o3"
#pragma xta command "set required - 1300 ns"
#pragma xta command "analyse path audio_48_left_o2 audio_48_left_o4"
#pragma xta command "set required - 1300 ns"
#pragma xta command "analyse path audio_48_left_o3 audio_48_left_o5"
#pragma xta command "set required - 1300 ns"
#pragma xta command "analyse path audio_48_left_o4 audio_48_left_o6"
#pragma xta command "set required - 1400 ns"
#pragma xta command "analyse path audio_48_left_o5 audio_48_left_o7"
#pragma xta command "set required - 1300 ns"
#pragma xta command "add exclusion audio_48_right_o0"
#pragma xta command "analyse path audio_48_left_o6 audio_48_left_o4  "
#pragma xta command "set required - 1300 ns"
#pragma xta command "remove exclusion audio_48_right_o0"
#pragma xta command "add exclusion audio_48_left_o4"
#pragma xta command "analyse path audio_48_left_o6 audio_48_right_o0"
#pragma xta command "set required - 1300 ns"
#pragma xta command "analyse path audio_48_left_o7 audio_48_right_o1"
#pragma xta command "set required - 1300 ns"
#pragma xta command "remove exclusion audio_48_left_o4"
#pragma xta command "analyse path audio_48_right_o0 audio_48_right_o2"
#pragma xta command "set required - 1300 ns"
#pragma xta command "analyse path audio_48_right_o1 audio_48_right_o3"
#pragma xta command "set required - 1300 ns"
#pragma xta command "analyse path audio_48_right_o2 audio_48_right_o4"
#pragma xta command "set required - 1300 ns"
#pragma xta command "analyse path audio_48_right_o3 audio_48_right_o5"
#pragma xta command "set required - 1300 ns"
#pragma xta command "analyse path audio_48_right_o4 audio_48_right_o6"
#pragma xta command "set required - 1300 ns"
#pragma xta command "analyse path audio_48_right_o5 audio_48_right_o7"
#pragma xta command "set required - 1300 ns"
#pragma xta command "add exclusion audio_48_left_o0"
#pragma xta command "analyse path audio_48_right_o6 audio_48_right_o4"
#pragma xta command "set required - 1300 ns"
#pragma xta command "analyse path audio_48_right_o7 audio_48_right_o5"
#pragma xta command "set required - 1300 ns"
#pragma xta command "remove exclusion audio_48_left_o0"
#pragma xta command "add exclusion audio_48_right_o4"
#pragma xta command "analyse path audio_48_right_o6 audio_48_left_o0"
#pragma xta command "set required - 1300 ns"
#pragma xta command "analyse path audio_48_right_o7 audio_48_left_o1"
#pragma xta command "set required - 1300 ns"
#pragma xta command "remove exclusion audio_48_right_o4"
#pragma xta command "remove exclusion audio_48_wipebuf"
#pragma xta command "remove exclusion audio_48_freqadjust"
#pragma xta command "print summary"
#endif
