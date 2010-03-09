#include <xs1.h>
#include <xclib.h>
#include "global.h"
#include "audioManager.h"

/*
 * Audio Manager thread for both I2S and S/PDIF Output
 */
#if defined I2SOUTPUT_SPDIFOUTPUT

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

signed short audiodata[AUDIO_BUF_SIZE];
static int audiovolume = 0;
static int count = 0;

#pragma unsafe arrays
void audioManager_48kHz(
    streaming_chanend cAudioStreaming, streaming chanend cSOFGen, streaming chanend cAudioMixer,
    s_audio &audioports, int freqcount, int flags)
{
  int rptr = 0;
  int wptr = 0;  
  unsigned word;
  unsigned xor;
  unsigned encoded_preamble, encoded_byte;
  unsigned sample=0, control, parity;
  
  int controlLeft;
  int controlRight;

  int nelements = 0;
  int newblock = 2;
  
  int running = 1;
  int audiorunning = 0;
  
  controlLeft = flags | (1 << 21);
  controlRight = flags | (1 << 22);
  
  xor = 0x0;

  sample = (audiodata[rptr] << 12) & 0x0FFFFFF0 ;
  control = (controlLeft & 2);
  parity = parity32(sample | control | ((!audiorunning) << 24));
  control = bitrev(control | parity) | ((!audiorunning) << 24)| newblock | sample;
  
  while (running)
  {
    int loopcount = 192;
    
    while (loopcount)
    {
      
      // Left Channel
      word = control;
      audioports.p_sdout <: bitrev(audiodata[rptr] << 16);
#ifdef LR_CLK_1B_PORT
      audioports.p_lrclk <: 0x80000000;
#endif
      encoded_preamble = preamble_words_48[(word & 0xF)*2+1] ^ xor;
      
      // BCLK0
#pragma xta endpoint "audio_48_left_o0"
      audioports.p_bclk <: 0xCCCCCCCC;
      audioports.pSPDIF :8 <: encoded_preamble;
      encoded_preamble = preamble_words_48[(word & 0xF)*2] ^ xor;
#pragma xta endpoint "audio_48_left_o1"
      audioports.pSPDIF :8 <: encoded_preamble;
      xor = mysext(encoded_preamble, 8) >> 8;  
      word = word >> 4;
      controlLeft >>=1;
      control = (controlRight & 2);
      sample = (audiodata[rptr+1] << 12) & 0x0FFFFFF0 ;
      encoded_byte = data_words_48[(word & 0xF) * 2] ^ xor;  // Xor to invert data if lsab of last data was a 1
#pragma xta endpoint "audio_48_left_o2"
      audioports.pSPDIF :8 <: data_words_48[(word & 0xF)*2+1] ^ xor;
      parity = parity32(sample | control | ((!audiorunning) << 24));
      control = bitrev(parity | control) | ((!audiorunning) << 24);
#pragma xta endpoint "audio_48_left_o3"
      audioports.pSPDIF :8 <: encoded_byte;
      
      audioSOFLoop(cSOFGen, count, freqcount);
      xor = mysext(encoded_byte, 8) >> 8;        

      for (int i=0; i<3; i++)
      {
        word = word >> 4;
        encoded_byte = data_words_48[(word & 0xF)*2+1] ^ xor;  // Xor to invert data if lsab of last data was a 1
#pragma xta endpoint "audio_48_left_o4"
        audioports.p_bclk <: 0xCCCCCCCC;
        audioports.pSPDIF :8 <: encoded_byte;
        encoded_byte = data_words_48[(word & 0xF) * 2] ^ xor;  // Xor to invert data if lsab of last data was a 1
        xor = mysext(encoded_byte, 8) >> 8;  
        word = word >> 4;
#pragma xta endpoint "audio_48_left_o5"
        audioports.pSPDIF :8 <: encoded_byte;
        checkForData(cAudioStreaming, cSOFGen, audiodata, wptr, audiorunning, nelements, audiovolume);
        sample = sample | control | 1;
#pragma xta endpoint "audio_48_left_o6"
        audioports.pSPDIF :8 <: data_words_48[(word & 0xF)*2+1] ^ xor;        
        encoded_byte = data_words_48[(word & 0xF) * 2] ^ xor;  // Xor to invert data if lsab of last data was a 1
#pragma xta endpoint "audio_48_left_o7"
        audioports.pSPDIF :8 <: encoded_byte;        
        xor = mysext(encoded_byte, 8) >> 8;        
      }

      
      // Right Channel
      word = sample;
      audioports.p_sdout <: bitrev(audiodata[rptr+1] << 16);
#ifdef LR_CLK_1B_PORT
      audioports.p_lrclk <: 0x7FFFFFFF;
#endif
      
      // BCLK0
      encoded_preamble = preamble_words_48[(word & 0xF)*2+1] ^ xor;
#pragma xta endpoint "audio_48_right_o0"
      audioports.p_bclk <: 0xCCCCCCCC;
      audioports.pSPDIF :8 <: encoded_preamble;
      encoded_preamble = preamble_words_48[(word & 0xF)*2] ^ xor;
      xor = mysext(encoded_preamble, 8) >> 8;  
      word = word >> 4;
      moveDataOn(nelements, rptr, audiorunning);

      #pragma xta endpoint "audio_48_right_o1"
      audioports.pSPDIF :8 <: encoded_preamble;      
      loopcount--;
      if (loopcount == 0)
      {
        controlLeft = flags | (1 << 21);
        controlRight = flags | (1 << 22);
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
      parity = parity32(sample | control | ((!audiorunning) << 24));
      control = bitrev(control | parity) | ((!audiorunning) << 24)| newblock | sample;
      xor = mysext(encoded_byte, 8) >> 8;        
      
      for (int i=0; i<3; i++)
      {
        word = word >> 4;
        encoded_byte = data_words_48[(word & 0xF)*2+1] ^ xor;  // Xor to invert data if lsab of last data was a 1
        checkForMixer(cAudioMixer, freqcount, audiodata, running, cSOFGen, audiovolume);
        audioports.p_bclk <: 0xCCCCCCCC;
#pragma xta endpoint "audio_48_right_o4"
        audioports.pSPDIF :8 <: encoded_byte;
        encoded_byte = data_words_48[(word & 0xF) * 2] ^ xor;  // Xor to invert data if lsab of last data was a 1
        xor = mysext(encoded_byte, 8) >> 8;  
        word = word >> 4;
#pragma xta endpoint "audio_48_right_o5"
        audioports.pSPDIF :8 <: encoded_byte;
        checkForData(cAudioStreaming, cSOFGen, audiodata, wptr, audiorunning, nelements, audiovolume);
#pragma xta endpoint "audio_48_right_o6"
        audioports.pSPDIF :8 <: data_words_48[(word & 0xF)*2+1] ^ xor;        
        encoded_byte = data_words_48[(word & 0xF) * 2] ^ xor;  // Xor to invert data if lsab of last data was a 1
#pragma xta endpoint "audio_48_right_o7"
        audioports.pSPDIF :8 <: encoded_byte;
        xor = mysext(encoded_byte, 8) >> 8;        
      }
    }
  }
#pragma xta label "audio_48_exit"
  return;
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
    s_audio &audioports, int freqcount, int flags)
{
  int rptr = 0;
  int wptr = 0;  
  unsigned word;
  unsigned xor;
  unsigned encoded_preamble, encoded_byte;
  unsigned sample=0, control, parity;
  
  int controlLeft;
  int controlRight;
  int newblock = 2;
  int nelements = 0;
  
  int running = 1;
  int audiorunning = 0;
  
  controlLeft = flags | (1 << 21);
  controlRight = flags | (1 << 22);

  xor = 0x0;

  sample = (audiodata[rptr] << 12) & 0x0FFFFFF0 ;
  control = (controlLeft & 2);
  parity = parity32(sample | control | ((!audiorunning) << 24));
  control = bitrev(control | parity) | ((!audiorunning) << 24)| newblock | sample;
  
  while (running)
  {
    int loopcount = 192;
    
    while (loopcount)
    {
      
      // Left Channel
      word = control;
      audioports.p_sdout <: bitrev(audiodata[rptr] << 16);
#ifdef LR_CLK_1B_PORT
      audioports.p_lrclk <: 0x80000000;
#endif
      encoded_preamble = preamble_words_32[(word & 0xF)*2+1] ^ xor;
      
      // BCLK0
#pragma xta endpoint "audio_32_left_o0"
      audioports.pSPDIF :12 <: encoded_preamble;
      audioports.p_bclk <: 0xC71C71C7;
      encoded_preamble = preamble_words_32[(word & 0xF)*2] ^ xor;
#pragma xta endpoint "audio_32_left_o1"
      audioports.pSPDIF :12 <: encoded_preamble;
      xor = mysext(encoded_preamble, 12) >> 12;  
      word = word >> 4;
      controlLeft >>=1;
      control = (controlRight & 2);
      sample = (audiodata[rptr+1] << 12) & 0x0FFFFFF0 ;
      encoded_byte = data_words_32[(word & 0xF) * 2] ^ xor;  // Xor to invert data if lsab of last data was a 1
#pragma xta endpoint "audio_32_left_o2"
      audioports.pSPDIF :12 <: data_words_32[(word & 0xF)*2+1] ^ xor;
      audioports.p_bclk :16 <: 0x1C71;
      parity = parity32(sample | control | ((!audiorunning) << 24));
      control = bitrev(parity | control) | ((!audiorunning) << 24);
#pragma xta endpoint "audio_32_left_o3"
      audioports.pSPDIF :12 <: encoded_byte;
      audioSOFLoop(cSOFGen, count, freqcount);
      xor = mysext(encoded_byte, 12) >> 12;        

      for (int i=0; i<3; i++)
      {
        word = word >> 4;
        encoded_byte = data_words_32[(word & 0xF)*2+1] ^ xor;  // Xor to invert data if lsab of last data was a 1
#pragma xta endpoint "audio_32_left_o4"
        audioports.pSPDIF :12 <: encoded_byte;
        audioports.p_bclk <: 0xC71C71C7;
        encoded_byte = data_words_32[(word & 0xF) * 2] ^ xor;  // Xor to invert data if lsab of last data was a 1
        xor = mysext(encoded_byte, 12) >> 12;  
        word = word >> 4;
#pragma xta endpoint "audio_32_left_o5"
        audioports.pSPDIF :12 <: encoded_byte;
        checkForData(cAudioStreaming, cSOFGen, audiodata, wptr, audiorunning, nelements, audiovolume);
        sample = sample | control | 1;
#pragma xta endpoint "audio_32_left_o6"
        audioports.pSPDIF :12 <: data_words_32[(word & 0xF)*2+1] ^ xor;        
        audioports.p_bclk :16 <: 0x1C71;
        encoded_byte = data_words_32[(word & 0xF) * 2] ^ xor;  // Xor to invert data if lsab of last data was a 1
#pragma xta endpoint "audio_32_left_o7"
        audioports.pSPDIF :12 <: encoded_byte;        
        xor = mysext(encoded_byte, 12) >> 12;        
      }

      
      // Right Channel
      word = sample;
      audioports.p_sdout <: bitrev(audiodata[rptr+1] << 16);
#ifdef LR_CLK_1B_PORT
      audioports.p_lrclk <: 0x7FFFFFFF;
#endif
      
      // BCLK0
      encoded_preamble = preamble_words_32[(word & 0xF)*2+1] ^ xor;
#pragma xta endpoint "audio_32_right_o0"
      audioports.pSPDIF :12 <: encoded_preamble;
      audioports.p_bclk <: 0xC71C71C7;
      encoded_preamble = preamble_words_32[(word & 0xF)*2] ^ xor;
      xor = mysext(encoded_preamble, 12) >> 12;  
      word = word >> 4;
      moveDataOn(nelements, rptr, audiorunning);
      
#pragma xta endpoint "audio_32_right_o1"
      audioports.pSPDIF :12 <: encoded_preamble;      
      loopcount--;
      if (loopcount == 0)
      {
        controlLeft = flags | (1 << 21);
        controlRight = flags | (1 << 22);
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
      audioports.p_bclk :16 <: 0x1C71;
      control = (controlLeft & 2);
      encoded_byte = data_words_32[(word & 0xF) * 2] ^ xor;  // Xor to invert data if lsab of last data was a 1
#pragma xta endpoint "audio_32_right_o3"
      audioports.pSPDIF :12 <: encoded_byte;
      parity = parity32(sample | control | ((!audiorunning) << 24));
      control = bitrev(control | parity) | ((!audiorunning) << 24)| newblock | sample;
      xor = mysext(encoded_byte, 12) >> 12;        
      
      for (int i=0; i<3; i++)
      {
        word = word >> 4;
        encoded_byte = data_words_32[(word & 0xF)*2+1] ^ xor;  // Xor to invert data if lsab of last data was a 1
        checkForMixer(cAudioMixer, freqcount, audiodata, running, cSOFGen, audiovolume);
        audioports.p_bclk <: 0xC71C71C7;
#pragma xta endpoint "audio_32_right_o4"
        audioports.pSPDIF :12 <: encoded_byte;
        encoded_byte = data_words_32[(word & 0xF) * 2] ^ xor;  // Xor to invert data if lsab of last data was a 1
        xor = mysext(encoded_byte, 12) >> 12;  
        word = word >> 4;
#pragma xta endpoint "audio_32_right_o5"
        audioports.pSPDIF :12 <: encoded_byte;
        checkForData(cAudioStreaming, cSOFGen, audiodata, wptr, audiorunning, nelements, audiovolume);
#pragma xta endpoint "audio_32_right_o6"
        audioports.pSPDIF :12 <: data_words_32[(word & 0xF)*2+1] ^ xor;        
        audioports.p_bclk :16 <: 0x1C71;
        encoded_byte = data_words_32[(word & 0xF) * 2] ^ xor;  // Xor to invert data if lsab of last data was a 1
#pragma xta endpoint "audio_32_right_o7"
        audioports.pSPDIF :12 <: encoded_byte;
        xor = mysext(encoded_byte, 12) >> 12;        
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
      audioManager_48kHz(cAudioStreaming, cSOFGen, cAudioMixer, audioports, 112896, getSpdifControl(44100));
      break;
    case (48000):
      audioManager_48kHz(cAudioStreaming, cSOFGen, cAudioMixer, audioports, 122880, getSpdifControl(48000));
      break;
    case (32000):
      audioManager_32kHz(cAudioStreaming, cSOFGen, cAudioMixer, audioports, 81920, getSpdifControl(44100));
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
#pragma xta command "add exclusion audio_48_exit"
#pragma xta command "add exclusion audio_48_waitforinput"
#pragma xta command "add exclusion audio_48_wipebuf"
#pragma xta command "add exclusion audio_48_freqadjust"
#pragma xta command "add exclusion audio_flashupgrade"
#pragma xta command "config freq 0 500"
#pragma xta command "analyse path audio_48_left_o0 audio_48_left_o2"
#pragma xta command "set required - 1300 ns"
#pragma xta command "analyse path audio_48_left_o1 audio_48_left_o3"
#pragma xta command "set required - 1300 ns"
#pragma xta command "analyse path audio_48_left_o2 audio_48_left_o4"
#pragma xta command "set required - 1300 ns"
#pragma xta command "analyse path audio_48_left_o3 audio_48_left_o5"
#pragma xta command "set required - 1300 ns"
#pragma xta command "analyse path audio_48_left_o4 audio_48_left_o6"
#pragma xta command "set required - 1300 ns"
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
#pragma xta command "remove exclusion audio_48_exit"
#pragma xta command "remove exclusion audio_48_waitforinput"
#pragma xta command "remove exclusion audio_flashupgrade"
#pragma xta command "print summary"
#pragma xta command "exit"
#endif
