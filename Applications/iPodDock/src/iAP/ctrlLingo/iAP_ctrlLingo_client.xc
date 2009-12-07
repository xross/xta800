#include "iAP.h"

void iAP_ctrlLingo_enableAlternate(streaming_chanend ciPod)
{
  int retval;
  
  ciPod <: 0;
  ciPod :> retval;
  if (!retval)
  {
    ciPod <: ctrlLingo;
    ciPod <: 0;
    ciPod :> int _;
  }
}


void iAP_ctrlLingo_reset(streaming_chanend ciPod)
{
  int retval;
  
  ciPod <: 0;
  ciPod :> retval;
  if (!retval)
  {
    ciPod <: ctrlLingo;
    ciPod <: 1;
    ciPod :> int _;
  }
}

