
/*
 * TODO: Would be nicer if we could pass pointers around instead of memcpys
 * But alas, in the interests of actually getting somewhere, 
 * lets start with this.
 */

#include "bufferManager.h"
#include <string.h>

char txbuf[TRANS_STRUC_SIZE * NUM_BUFFERS];
volatile unsigned tx_rptr = 0;
volatile unsigned tx_wptr = 0;

char rxbuf[TRANS_STRUC_SIZE * NUM_BUFFERS];
volatile unsigned rx_rptr = 0;
volatile unsigned rx_wptr = 0;

#define INCREMENT(ptr) \
  { \
    int local = *ptr; \
    local++; \
    local *= !(local == NUM_BUFFERS); \
    *ptr = local; \
  }

int bufadd(char *ptr, char *buf, volatile unsigned *rptr, volatile unsigned *wptr)
{
  // Check if full
  if (*wptr == *rptr - 1)
  {
    return 1;
  }
  else if (*wptr == NUM_BUFFERS - 1 && *rptr == 0)
  {
    return 1;
  }
  else
  {
    memcpy(&buf[*wptr * TRANS_STRUC_SIZE], ptr, TRANS_STRUC_SIZE);    
    INCREMENT(wptr);
    return 0;
  }
}

int bufremove(char *ptr, char *buf, volatile unsigned *rptr, volatile unsigned *wptr)
{
  // Check if empty
  if (*wptr == *rptr)
  {
    return 1;
  }
  else
  {
    memcpy(ptr, &buf[*rptr * TRANS_STRUC_SIZE], TRANS_STRUC_SIZE);
    INCREMENT(rptr);
    return 0;
  }
}

// ---------------------------------------------------------------

int ep_add(char *ptr)
{
  return bufadd(ptr, txbuf, &tx_rptr, &tx_wptr);
}
int ep_remove(char *ptr)
{
  return bufremove(ptr, rxbuf, &rx_rptr, &rx_wptr);
}
int lld_add(char *ptr)
{
  return bufadd(ptr, rxbuf, &rx_rptr, &rx_wptr);
}
int lld_remove(char *ptr)
{
  return bufremove(ptr, txbuf, &tx_rptr, &tx_wptr);
}

