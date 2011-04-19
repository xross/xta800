/** @file XUD_EP_Functions.xc
  * @brief Implementation of user API fuctions.  See xud.h for documentation.
  * @author Ross Owen
  * @version 0.1
  * @date 05/02/09
  **/

#include <xs1.h>
#include <print.h>

#include "xud.h"
#include "XUD_Defines_Internal.h"
#include "XUD_USB_Defines.h"
#include "XUD_UIFM_Defines.h"
extern int GetSetupBufferLength(int epNum);
extern void SetSetupBufferLength(int epNum, int x);
extern int GetSetupData(int epNum, int index);

extern int GetOutBufferLength(int epNum);
extern void SetOutBufferLength(int epNum, int x);
extern int GetOutData(int epNum, int index);

extern int GetInBufferLength(int epNum);
extern int SetInData(int epNum, int index, unsigned x);
extern int SetInBufferLength(int epNum, int x);

#ifndef XUD_DEBUG_PRINTS
#define printstrln(x) /**/
#define printstr(x) /**/
#define printhexln(x) /**/
#endif

/* Prototypes for main assembly functions for data transfer to/from USB I/O thread */
int XUD_GetData(chanend c, unsigned char buffer[]);
int XUD_GetSetupData(chanend c, unsigned char buffer[]); // TODO Use GetData for this
int XUD_SetData(chanend c, unsigned char buffer[], unsigned datalength, unsigned pidToggle, unsigned startIndex);


/** XUD_GetSetupBuffer() 
  * @brief Request setup data from usb buffer for a specific EP, pauses until data is available.  
  * @param c Data channel to/from XUD
  * @param c_ctl Control channel from XUD
  * @param epNum EP number 
  * @param buffer char buffer passed by ref into which data is returned
  * @return datalength in bytes (always 8), -1 if kill received
  **/
//TODO this could return a SetupPacket
int XUD_GetSetupBuffer(chanend c_out, unsigned char buffer[])
{
    return XUD_GetSetupData(c_out, buffer);
}

/** XUD_GetBuffer_()
  * @brief        Requests data from the endpoint related to the passed channel
  * @param c      Channel to USB I/O thread for this endpoint 
  * @param buffer Buffer for returned data
  * @return       datalength of received data in bytes
  **/
int XUD_GetBuffer(chanend c, unsigned char buffer[])
{
    return XUD_GetData(c, buffer);
}


/** XUD_SetBuffer_ResetPid(()
  * @brief      Sends USB data with PID reset to specified
  * @param      c
  * @param      epNum
  * @param      buffer
  * @param      datalengh in bytes!
  **/
int XUD_SetBuffer_ResetPid_(chanend c, unsigned epNum, unsigned char buffer[], unsigned datalength, unsigned char pid)
{
    XUD_SetData(c, buffer, datalength, pid, 0);  
    return 1;
}

int XUD_SetBuffer_ResetPid(chanend c, unsigned char buffer[], unsigned datalength, unsigned char pid)
{
    XUD_SetData(c, buffer, datalength, pid, 0);  
    return 1;
}




/* Datalength in bytes */
int XUD_SetBuffer_ResetPid_EpMax(chanend c, unsigned epNum, unsigned char buffer[], 
  unsigned datalength, unsigned epMax, unsigned char pid)
{
    int tmp, i = 0, j = 0;
 
 
    /* Note: We could encompass this in the SetData function */ 
    if(datalength <= epMax)
    {
        /* Datalength is less than the maximum per transaction of the EP, so just send */
        XUD_SetData(c, buffer, datalength, pid, 0); 
   
    }
    else
    {
        while(1)
	      {
	          pid ^= 0x88;
	  
            if(datalength > epMax)
	          {
	              XUD_SetData(c, buffer, epMax, 0, i); 
                datalength-=epMax;
                i += epMax;
   
	          }
	          else
	          {
	             	XUD_SetData(c, buffer, datalength, 0, i); 
   
	            break; //out of while loop
	        }
	    }
    }
   
    return 1;
}

void XUD_ParseSetupPacket(unsigned char b[], SetupPacket &p)
{
  // Byte 0: bmRequestType.
  p.bmRequestType.Recipient = b[0] & 0x1f;
  p.bmRequestType.Type      = (b[0] & 0x60) >> 5;
  p.bmRequestType.Direction = b[0] >> 7;

  // Byte 1:  bRequest 
  p.bRequest = b[1];

  // Bytes [2:3] wValue
  p.wValue = (b[3] << 8) | (b[2]);

  // Bytes [4:5] wIndex
  p.wIndex = (b[5] << 8) | (b[4]);

  // Bytes [6:7] wLength
  p.wLength = (b[7] << 8) | (b[6]);

}

void XUD_PrintSetupPacket(SetupPacket sp)
{
  printstr("Setup data\n");
  printstr("bmRequestType.Recipient: ");
  printhexln(sp.bmRequestType.Recipient);
  printstr("bmRequestType.Type: ");
  printhexln(sp.bmRequestType.Type);
  printstr("bmRequestType.Direction: ");
  printhexln(sp.bmRequestType.Direction);  
  printstr("bRequest: ");
  printhexln(sp.bRequest); 
  printstr("bmRequestType.wValue: ");
  printhexln(sp.wValue);   
  printstr("bmRequestType.wIndex: ");
  printhexln(sp.wIndex);
  printstr("bmRequestType.wLength: ");
  printhexln(sp.wLength);
}


int XUD_DoGetRequest_(chanend c, chanend c_in, unsigned epNum, uint8 buffer[], unsigned datalength)
{
    /* TODO: Size = EP 0 Max. Datalength */
    unsigned char tmpBuffer[1024];
    int d;

    XUD_SetBuffer_ResetPid_EpMax(c_in, epNum, buffer, datalength, 64, PIDn_DATA1);
    
    d = XUD_GetData(c, tmpBuffer);
 
    return 0;
}


/* Send 0 length status 
 * Simply sends a 0 length packet */
int XUD_DoSetRequestStatus(chanend c, unsigned epNum)
{
    unsigned char tmp[8];

    XUD_SetData(c, tmp, 0, PIDn_DATA1, 0);
    return 0;
}

void XUD_Error(char errString[])
{
    printstr("XUD Err: ");
    printstr(errString);
    while(1);
}

void XUD_Error_hex(char errString[], int i_err)
{
    printstr("XUD Err: ");
    printstr(errString);
    printhexln(i_err);
    while(1);
}
