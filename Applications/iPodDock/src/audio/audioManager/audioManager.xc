
#include <xs1.h>
#include <xclib.h>
#include "global.h"

#include "audioManager.h"

int nval = 99;
void audioWaitForInput(streaming chanend cSOFGen, streaming chanend cAudioMixer)
{
  timer tSOF;
  int now;
  cAudioMixer <: 0;
  tSOF :> now;
  while (1)
  {
    newnval(1);
    select
    {
    case tSOF when timerafter(now + XS1_TIMER_KHZ) :> int _:
      now += XS1_TIMER_KHZ;
      cSOFGen :> int _;
      cSOFGen <: 1;
      break;
    case cAudioMixer :> int _:
      cAudioMixer <: 0;
      return;
    }
  }
}

void audioPlayerInit(out port p32, s_audio &audioports)
{
  stop_clock(audioports.clk_audio_mclk);
  stop_clock(audioports.clk_audio_bclk);
  
  configure_clock_src(audioports.clk_audio_mclk, audioports.p_mclk);
  
  configure_out_port_no_ready(audioports.p_bclk, audioports.clk_audio_mclk, 0);
  
  configure_out_port_no_ready(audioports.pSPDIF, audioports.clk_audio_mclk, 0);
  
  // set delay to align SPDIF output to the clock at the external flop
  set_clock_fall_delay(audioports.clk_audio_mclk, 7);  

  // Generate bit clock block from pin 
  configure_clock_src(audioports.clk_audio_bclk, audioports.p_bclk);
    
  // Clock ports from clock block 
  configure_out_port_no_ready(audioports.p_sdout, audioports.clk_audio_bclk, 0);  
#ifdef LR_CLK_1B_PORT
  configure_out_port_no_ready(audioports.p_lrclk, audioports.clk_audio_bclk, 0);  
#else
  configure_out_port_no_ready(p32, audioports.clk_audio_bclk, 0);  
#endif
  
  clearbuf(audioports.p_sdout);
  clearbuf(p32);
  clearbuf(audioports.p_bclk);

  start_clock(audioports.clk_audio_bclk);
  start_clock(audioports.clk_audio_mclk);
}

