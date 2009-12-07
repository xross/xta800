#include <xs1.h>
#include "global.h"
#include "remote.h"
#include "audioManager.h"
#include "portManager.h"

/*
 * portManager
 * Multiplexing thread controlling low speed outputs on the 32bit port
 * Also responsible for IR receive
 */
void portManager(out port p32,
    streaming chanend cOsc, streaming chanend cUSBReset, streaming chanend cLED,
    streaming chanend cI2CReset, streaming chanend cIR, in port pIR,
    in port pRegWrite0, in port pRegWrite1,
    streaming chanend cUSBReg)
{
  int portstate = 0b0000000;
  int irval = 0;
  timer t;
#ifndef LR_CLK_1B_PORT
  int lrclkval = 0;
#endif
  int now;
  int irenabled = 1;
  int irportval;
  
#ifdef ENFORCE_FAST_MODE
  set_thread_fast_mode_on();
#endif
  
  pIR :> irportval;
  
  while (1)
  {
    int i = 16;

#ifndef LR_CLK_1B_PORT
    portstate ^= (1 << PORT_32B_LRCLK_BITSHIFT);
#endif
    
    while (i)
    {
      i--;
      p32 <: portstate;
      p32 <: portstate;
      
      // Check for inputs on any channel
      select
      {
        case cUSBReg :> int _:
          set_port_use_off(pRegWrite0);
          set_port_use_off(pRegWrite1);
          cUSBReg <: 1;
          cUSBReg :> int _;
          set_port_use_on(pRegWrite0);
          set_port_use_on(pRegWrite1);
          break;
        case cOsc :> int val:
          cOsc <: 1;
          portstate &= ~(1 << PORT_32B_OSC_BITSHIFT);
          portstate |= (val & 1) << PORT_32B_OSC_BITSHIFT;
          break;
        case cUSBReset :> int val:
          cUSBReset <: 1;
          portstate &= ~(1 << PORT_32B_USBRESET_BITSHIFT);
          portstate |= (val & 1) << PORT_32B_USBRESET_BITSHIFT;
          break;
        case cLED :> int val:
          cLED <: 1;
          portstate &= ~(3 << PORT_32B_LED_BITSHIFT);
          portstate |= (val & 3) << PORT_32B_LED_BITSHIFT;
          break;
        case cI2CReset :> int val:
          cI2CReset <: 1;
          portstate &= ~(1 << PORT_32B_COPRORESET_BITSHIFT);
          portstate |= (val & 1) << PORT_32B_COPRORESET_BITSHIFT;
          break;
#define IR_PORT_BIT 0
        case (irenabled) => pIR when pinsneq(irportval) :> irportval:
        {
          int retVal;

          if (((irportval >> IR_PORT_BIT) & 1) == 0)
          {
            irportval |= (1 << IR_PORT_BIT);
            // Completely receive IR command
            retVal = ir_receive(cIR, pIR, IR_PORT_BIT, t);
            
            if (retVal == -1)
            {
              // Repeat of previous command
              cIR <: irval;            
            }
            else if (retVal > 0)
            {
              // New IR command
              irval = retVal;
              cIR <: irval;
            }
            else
            {
            }
            
            // Set up delay before accepting new IR command
            irenabled = 0;
            t :> now;
          }
        }
        break;
        case (!irenabled) => t when timerafter(now + 200 * XS1_TIMER_KHZ) :> int _:
          // Delay complete, accept new IR command
          irenabled = 1;
          break;
#ifndef LR_CLK_1B_PORT
        default:
          break;
#endif
      }
    }
  }
}

/*
 * portManagerWrapper:
 * Wraps the audio manager and port manager threads
 */
void portManagerWrapper(
    out port p, streaming chanend cOsc, streaming chanend cUSBReset, streaming chanend cLED, streaming chanend cI2CReset, streaming chanend cIR, in port pIR,
    streaming_chanend cAudioStreaming, streaming chanend cSOFGen, streaming chanend cAudioMixer,
    s_audio &audioports,
    in port pRegWrite0, in port pRegWrite1,
    streaming chanend cUSBReg)
{
  // Set up the audio ports
  audioPlayerInit(p, audioports);
  
  // Par up the two threads
  par
  {
    portManager(p, cOsc, cUSBReset, cLED, cI2CReset, cIR, pIR,
        pRegWrite0, pRegWrite1,
        cUSBReg);
    audioManager(cAudioStreaming, cSOFGen, cAudioMixer, audioports);
  }
}
