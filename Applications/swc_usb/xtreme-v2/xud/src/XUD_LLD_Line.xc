/**
 * @file linestate_mon.xc
 * @brief Monitors line state for activity to detect suspend in by polling uifm registers
 * @author Ross Owen, XMOS Semiconductor Ltd
 
 * @version 1.00
 **/

/*
 * HS Suspend is detected if SE0 detected on the bus for more that 3ms i.e. 3,000,000 ns  = 300,000 timerticks
 *
 * Put in FS mode (XvcrSelect and TermSelect = 1)
 *
 * Uifm should already have DECODE_LS enabled and TODO flag bits sticky for j,k se0
 */

#include <xs1.h>
#include <print.h>
#include "ssctrl.h"

#include "XUD_UIFM_Defines.h"
#include "XUD_USB_Defines.h"
#include "XUD_Defines_Internal.h"
#include "XUD_Support.h"
#include "XUD_UIFM_Functions.h"

#define reg_write_port p_ifm_wr

#define SUSPEND_TIMEOUT 300000 // 300,000 timer ticks


//extern out port txd_port;
//extern  port rxd_port;
extern in port flag0_port;
extern in port flag2_port;
extern in port flag1_port;

int LineSelect(timer t,   unsigned time, chanend c_con_ep); 

unsigned XUD_LLD_LineMonitor(in port p_ifm_rd, out port p_ifm_wr, chanend c_ctl_io, chanend  c_ctl_ep)
{
  unsigned tmp;
  timer t;
  unsigned lastSof = 0; 
  unsigned time, sofTime, nextEdge;
  
  t :> time; 
  sofTime = time;
  nextEdge = time + 100100;

  time+=SUSPEND_TIMEOUT;
  
  while(1)
  {
    // This function effectively implements a select with a default		// Select with default now supported in tools! (as of 9.2)
    //tmp = LineSelect(t, time, c_con_ep);
    //switch(tmp) 
    select
    {
      // Timer:
      //case (0):
      case t when timerafter(time) :> int _:
      { 
 
        //printstrln("Suspend condition detected");

        
        /*time += readInterval;

        // Re-read IFM flags reg
        regVal = XUD_UIFM_RegRead(p_ifm_wr, p_ifm_rd, UIFM_REG_FLAGS);
    
        // Clear the J, K and SE0 bits in IFM_FLAGS register
        tmp2 = regVal & 0xc7;

        // Write back
        XUD_UIFM_RegWrite(p_ifm_wr, UIFM_REG_FLAGS, tmp2);

        // Extract linestate bits (J & K)
        regVal &= 0x18;
        
        // Check if multiple line state bits set.
        // This inspects j and k bits, if returned not 0 then seen j/k states
        //tmp = CountLineStateBits(tmp);
        //printhexln(tmp);
        tmp &= 0b00011000;
        if(regVal)
        {
          // Saw J or K in the period, reset count
          count = 0;
        }
        else
        {
          // SE0 state, increment count
          count ++;

          if(count == suspendCount)
            printstr("suspend condition detected");

        }
    
        // Inspect count to see if reached suspend timeout
        if(count == suspendCount)
        {*/
 
          XUD_UIFM_RegWrite(p_ifm_wr, UIFM_REG_FLAGS, 0);  // TODO: should read and only update correct bit
      
          // Put in FS mode (XcvrSelect and TermSelect = 1)
          //tmp = XUD_UIFM_RegRead(p_ifm_wr,  p_ifm_rd, UIFM_REG_PHYCON);
          //tmp |= 0x6;
          XUD_UIFM_RegWrite(p_ifm_wr, UIFM_REG_PHYCON, 0x7);

          XUD_Sup_Delay(SUSPEND_T_WTWRSTHS);

          // Check for J state. If so the initial SE0 had been due to suspend state and we remain in HS
          tmp = XUD_UIFM_RegRead(p_ifm_wr, p_ifm_rd, UIFM_REG_FLAGS);

          // Check se0 bit
          if((tmp & 0x20))
          {
	    //      printstr("reset detected\n"); 

            // inform other threads that Reset condition has been encountered      
            //tmp = ssctrl_read(6);
            //ssctrl_write(6, tmp);
            //outct(c_con_ep, XUD_CT_RESET);
            //outct(c_con_buf, XUD_CT_RESET);
            //outct(c_ctl_io, XUD_CT_RESET);
//            outct(c_sof, XUD_CT_RESET);

            
            //outct(c_con_ep, XS1_CT_END);
            //outct(c_con_buf, XS1_CT_END);
            outct(c_ctl_io, XS1_CT_END);

            return 0;
          }
          else
          {
            //printstrln("Suspend detected");
            //t:> time;
            //time += SUSPEND_TIMEOUT;
          }
        //}*/
        break;
      }
      //case (1):   // Control channel
      case inuint_byref(c_ctl_ep, tmp):
      {
        //tmp = inuint(c_con_ep);
        tmp = inuint(c_ctl_ep);
        chkct(c_ctl_ep, 1);
 
        // Currently only one command... SetAddress
        XUD_UIFM_RegWrite(p_ifm_wr, UIFM_REG_ADDRESS, tmp);    
        break;
      }
      
      default: 
      {
        

        // Read SOF count
        tmp = XUD_UIFM_RegRead(p_ifm_wr, p_ifm_rd, UIFM_REG_SOF0);
      
        /* Sof clock filter due to bad (CRC) or missed SOFs */ 
        if(tmp != lastSof)
        {  
          lastSof = tmp; 
 
          // If we received a SOF then bus not idle, reset idle timer
          t :> time;

          time += SUSPEND_TIMEOUT;
 
        } 
        break;
      }
    }
  }
  return 0;
}
