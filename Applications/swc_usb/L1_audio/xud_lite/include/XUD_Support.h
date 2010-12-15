/** Various support functions used in XUD 
 *
 **/

#ifndef _XUD_SUPPORT_H_
#define _XUD_SUPPORT_H_ 1

// Typedefs
typedef unsigned lock;
typedef unsigned XUD_chan;

lock XUD_Sup_GetLock();
void XUD_Sup_ClaimLock(lock l);
void XUD_Sup_FreeLock(lock l);
void XUD_Sup_FreerLock(lock l);

// Delay execution (Uses timer)
void XUD_Sup_Delay(unsigned x);

// SR
void XUD_Sup_EnableThreadInterrupts(void);
void XUD_Sup_DisableThreadInterrupts(void);

// Outpw
void XUD_Sup_Outpw8(out port p, unsigned x);
void XUD_Sup_Outpw16(out port p, unsigned x);
void XUD_Sup_Outpw24(out port p, unsigned x);

unsigned XUD_Sup_GetResourceId(chanend c);

// Inpw
unsigned XUD_Sup_Inpw24(in port p);
unsigned XUD_Sup_Inpw8( in port p);

// In
unsigned XUD_Sup_inuint(XUD_chan);
unsigned char XUD_Sup_inct(XUD_chan);
unsigned char XUD_Sup_testct(XUD_chan);

unsigned XUD_Sup_clear(XUD_chan c) ;

// Out
void XUD_Sup_outuint(XUD_chan, unsigned x);
void XUD_Sup_outct(XUD_chan, unsigned char x);

void XUD_Sup_stop_streaming_master(XUD_chan);

unsigned GetArrayAddress(unsigned char a[]);

int XUD_Sup_getd(XUD_chan c);

#endif
