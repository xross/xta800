#include "epManager.h"
#include "global.h"

// Support functions to interface to XS1 functionality
// not supported natively in C.
// This is the direct channel interface and timers.

timer tlocal;

/*
 * getTime:
 * Get a reference time value from the local 100Mhz clock
 */
int getTime() {
  int time;
  tlocal :> time;
  return time;
};

/*
 * pause:
 * Pause the current thread for a number of ticks from the
 * local 100Mhz clock
 */
void pause(int ticks)
{
  int start;
  tlocal :> start;
  tlocal when timerafter(start + ticks) :> int _;
  return;
}

/*
 * startChans
 * Receive flags from every channel end
 * These say if the channel requires knowledge of device disconnection
 */
void startChans(streaming_chanend cClients[NUM_CTRL_CHANENDS], int disconnectflag[NUM_CTRL_CHANENDS])
{
  int flags[NUM_CTRL_CHANENDS];
  int ndone=NUM_CTRL_CHANENDS;

  for (int i=0; i<NUM_CTRL_CHANENDS; i++)
    flags[i]=1;
  
  while (1)
  {
    select
    {
    case (int i=0; i<NUM_CTRL_CHANENDS; i++) (flags[i]) => cClients[i] :> disconnectflag[i]:
      flags[i] = 0;
      ndone--;
      if (ndone == 0)
        return;
      break;
    }
  }
}

/*
 * endChans:
 * Called on device disconnection
 * Only channels which require knowledge of device disconnection will be told
 */
void endChans(streaming_chanend cClients[NUM_CTRL_CHANENDS], int disconnectflag[NUM_CTRL_CHANENDS])
{
  int flags[NUM_CTRL_CHANENDS];
  int ndone=0;

  for (int i=0; i<NUM_CTRL_CHANENDS; i++)
  {
    flags[i]=disconnectflag[i];
    if (flags[i])
      ndone++;
  }
  
  while (1)
  {
    select
    {
    case (int i=0; i<NUM_CTRL_CHANENDS; i++) (flags[i]) => cClients[i] :> int _:
      cClients[i] <: 1;
      flags[i] = 0;
      ndone--;
      if (ndone == 0)
        return;
      break;
    }
  }
}

/*
 * pollChans:
 * Poll for requests on the client chanels
 */
void pollChans(streaming_chanend cClients[NUM_CTRL_CHANENDS], int &ctrlchan, int &val)
{
  ctrlchan = -1;
  select
  {
  case (int i=0; i<NUM_CTRL_CHANENDS; i++) cClients[i] :> val:
    cClients[i] <: 0;
    if (val != -1)
    {
      cClients[i] :> val;
      ctrlchan = i;
    }
  break;
  default:
    break;
  }
}

/*
 * USB_Ctrl
 * Signal control information to the USB LLD.
 * This includes power cycle
 */
int USB_Ctrl(chanend cUSBCtrl, unsigned data)
{
  int ret;
  cUSBCtrl <: data;
  cUSBCtrl :> ret;
  return ret;
};

/*
 * USBHostStackWrapper:
 * Wraps USBHostStack with fast mode if required
 */
void USBHostStackWrapper(streaming_chanend cClients[NUM_CTRL_CHANENDS], chanend cUSBCtrl)
{
#ifdef ENFORCE_FAST_MODE
  set_thread_fast_mode_on();
#endif
  USBHostStack(cClients, cUSBCtrl);
}
