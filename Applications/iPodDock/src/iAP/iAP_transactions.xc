#include "global.h"
#include "iAP.h"

/*
 * transmit_char_array:
 * Helper function for transmitting an array over a channel
 */
#pragma unsafe arrays
void transmit_char_array(streaming_chanend c, unsigned pktlen, unsigned char pkt[], int offset)
{
#ifndef USE_STREAMING_CHANNELS
  master
#endif
  {
    c <: pktlen;
    for (int i = 0; i < pktlen; i++)
      c <: (char)pkt[i + offset];
  }
}

/*
 * receive_char_array:
 * Helper function for receiving an array over a channel
 */
#pragma unsafe arrays
void receive_char_array(streaming_chanend c, unsigned &pktlen, unsigned char pkt[], int offset)
{
#ifndef USE_STREAMING_CHANNELS
  slave
#endif
  {
    c :> pktlen;
    for (int i = 0; i < pktlen; i++)
      c :> pkt[i + offset];
  }
}

