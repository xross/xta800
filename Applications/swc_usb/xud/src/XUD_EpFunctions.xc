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


/** XUD_GetSetupBuffer() 
  * @brief Request setup data from usb buffer for a specific EP, pauses until data is available.  
  * @param c Data channel to/from XUD
  * @param c_ctl Control channel from XUD
  * @param epNum EP number 
  * @param buffer char buffer passed by ref into which data is returned
  * @return datalength in bytes (always 8), -1 if kill received
  **/
//TODO this could return a SetupPacket
int XUD_GetSetupBuffer(chanend c, chanend c_con, unsigned epNum, unsigned char buffer[])
{
  unsigned char cmd;

  // Request data
  outuint(c, XUD_CMD_SETUP);
  outuint(c, epNum);
  outct(c, XS1_CT_PAUSE); // Don't keep the route open

  // Receive setup data into buffer (8 bytes)
  #pragma loop unroll(2)
  for(int i = 0; i < 2; i++)
  {
    if (testct(c)) {
      inct_byref(c, cmd);
      chkct(c,1);
      return -1;
    } else {
      select {
      case inuint_byref(c, (buffer, unsigned[])[i]):
	break;
      }
    }
  }
 
  // Return datalength (always 8 in the case of setup data)
  return 8;			  
}

int XUD_GetSetupBuffer_(chanend c, unsigned epNum, unsigned char buffer[])
{
  unsigned char cmd;

  // Request data
  outuint(c, XUD_CMD_SETUP);
  outuint(c, epNum);
  outct(c, XS1_CT_PAUSE); // Don't keep the route open

  // Receive setup data into buffer (8 bytes)
  #pragma loop unroll(2)
  for(int i = 0; i < 2; i++)
    {
      if (testct(c)) {
	inct(c);
	chkct(c,1);
	return -1;
      } else {
	inuint_byref(c, (buffer, unsigned[])[i]);
      }
  }
 
  // Return datalength (always 8 in the case of setup data)
  return 8;			  
}


int XUD_GetBuffer(chanend c, chanend c_ctl, unsigned epNum, unsigned char buffer[])
{
  unsigned char cmd;
  unsigned datalength;
  int i;

  // Request data
  outuint(c, XUD_CMD_OUT);
  outuint(c, epNum);
  outct(c, XS1_CT_PAUSE);		

  if (testct(c)) {
    inct(c);
    chkct(c,1);
    return -1;
  }
  inuint_byref(c, datalength);
   
#pragma unsafe arrays
      for(i = 0; i < (datalength >> 2); i+=1)
      { 
        (buffer, unsigned[])[i] = inuint(c);
      } 
    
     // Deal with tail
     if(datalength & 0x3)
        (buffer, unsigned[])[i] = inuint(c);

     return datalength;
}

int XUD_GetBuffer_(chanend c, unsigned epNum, unsigned char buffer[])
{
  unsigned char cmd;
  unsigned datalength;
  int i;

  // Request data
  outuint(c, XUD_CMD_OUT);
  outuint(c, epNum);
  outct(c, XS1_CT_PAUSE);		

  if (testct(c)) {
    inct(c);
    chkct(c,1);
    return -1;
  }
    inuint_byref(c, datalength);
    

#pragma unsafe arrays
      for(i = 0; i < (datalength >> 2); i+=1)
      { 
        (buffer, unsigned[])[i] = inuint(c);
      } 
    
     // Deal with tail
     if(datalength & 0x3)
        (buffer, unsigned[])[i] = inuint(c);

     return datalength;

}

int XUD_GetBuffer_int(chanend c, unsigned epNum, int buffer[])
{
  unsigned char cmd;
  unsigned datalength;
  int i;

  // Request data
  outuint(c, XUD_CMD_OUT);
  outuint(c, epNum);
  outct(c, XS1_CT_PAUSE);		

  if (testct(c)) {
    inct(c);
    chkct(c,1);
    return -1;
  }
  select 
  {
    //case inct_byref(c_ctl, cmd):
    //{
      //printstrln("get buff got reset");
      //return -1;
    
    //}
    case inuint_byref(c, datalength):
    {

#pragma unsafe arrays
      for(i = 0; i < (datalength >> 2); i+=1)
      { 
        buffer[i] = inuint(c);
      } 
    
     // Deal with tail
     if(datalength & 0x3)
        buffer[i] = inuint(c);

     return datalength;
    }
  }
}

int XUD_SetBuffer(chanend c, chanend c_con, unsigned epNum, unsigned char buffer[], unsigned datalength)
{
  int tmp, i;
  unsigned char cmd;

  outuint(c, XUD_CMD_IN);            // IN cmd
  outuint(c, epNum);		
 // outct(c, XS1_CT_PAUSE);

  if (testct(c)) {
    inct(c);
    chkct(c,1);
    return -1;
  }
  select
  {
    case inuint_byref(c, tmp):                // Wait for response from EP manager
    {  
      // Send datalength
      outuint(c, datalength);   
      
      // Send buffer
#pragma unsafe arrays
      for (i = 0; i < datalength >> 2; i++)
      {
        outuint(c, (buffer, unsigned[])[i]); 
      }

      // Deal with tail
      if(datalength & 0x3)
        outuint(c,(buffer, unsigned[])[i]);

      outct(c, XS1_CT_PAUSE);

      break;
    }

    //case inct_byref(c_con, cmd):
      //return -1;
  }
  return 1;
}

int XUD_SetBuffer_(chanend c, unsigned epNum, unsigned char buffer[], unsigned datalength)
{
    int tmp, i;
    unsigned char cmd;

    outuint(c, XUD_CMD_IN);            // IN cmd
    outuint(c, epNum);	
    //outct(c, XS1_CT_PAUSE);
    //outct(c, XS1_CT_PAUSE);	

  if (testct(c)) {
    inct(c);
    chkct(c,1);
    return -1;
  }
    tmp = inuint(c);                // Wait for response from EP manager
    
    // Send datalength
    outuint(c, datalength);   
    
      
    // Send buffer
#pragma unsafe arrays
    for (i = 0; i < datalength >> 2; i++)
    {
        outuint(c, (buffer, unsigned[])[i]); 
    }

    // Deal with tail
    if(datalength & 0x3)
        outuint(c,(buffer, unsigned[])[i]);

    outct(c, XS1_CT_PAUSE);

    return 1;
}


int XUD_SetBuffer_ResetPid(chanend c, chanend c_con, unsigned epNum, unsigned char buffer[], unsigned datalength, unsigned char pid)
{
  int tmp, i;
  unsigned char cmd;

  outuint(c, XUD_CMD_IN_RST_PID);            // IN cmd
  outuint(c, epNum);   
  outct(c, XS1_CT_PAUSE); 
 
  if (testct(c)) {
    inct(c);
    chkct(c,1);
    return -1;
  }
  inuint_byref(c, tmp);                // Wait for response from EP manager
      
  outuint(c, (unsigned) pid);   
  outuint(c, datalength);   // Send datalength

  // Send buffer
  for (i = 0; i < datalength >> 2; i++)
  {
    outuint(c, (buffer, unsigned[])[i]); 
  }

  // TODO Tail
  if(datalength & 0x3)
  outuint(c,(buffer, unsigned[])[i]);

  outct(c, XS1_CT_PAUSE);
 
  return 1;
}

int XUD_SetBuffer_ResetPid_(chanend c, unsigned epNum, unsigned char buffer[], unsigned datalength, unsigned char pid)
{
  int tmp, i;
  unsigned char cmd;

  // TODO what if these pause?
  outuint(c, XUD_CMD_IN_RST_PID);            // IN cmd
  outuint(c, epNum);   
  outct(c, XS1_CT_PAUSE); 
 
  if (testct(c)) {
    inct(c);
    chkct(c,1);
    return -1;
  }
  inuint_byref(c, tmp);                // Wait for response from EP manager
     
  outuint(c, (unsigned) pid);   
  outuint(c, datalength);   // Send datalength

  // Send buffer
  for (i = 0; i < datalength >> 2; i++)
  {
    outuint(c, (buffer, unsigned[])[i]); 
  }

  // TODO Tail
  if(datalength & 0x3)
  outuint(c,(buffer, unsigned[])[i]);

  outct(c, XS1_CT_PAUSE);
 
  return 1;
}



int XUD_SetBuffer_ResetPid_EpMax(chanend c, unsigned epNum, unsigned char buffer[], 
  unsigned datalength, unsigned epMax, unsigned char pid)
{
  unsigned tmp, i = 0, j = 0;
  unsigned char cmd;
  
  if(datalength <= epMax)
    {
      // TODO what if these pause?
      outuint(c, XUD_CMD_IN_RST_PID);            // IN cmd
      outuint(c, epNum);    
      outct(c, XS1_CT_PAUSE);
      
      if (testct(c)) {
	inct(c);
	chkct(c,1);
	return -1;
      }
      inuint_byref(c, tmp);                // Wait for response from EP manager
      
      outuint(c, (unsigned) pid);  
      
      outuint(c, datalength);   // Send datalength
      
      // Send buffer
      for (i = 0; i < datalength >> 2; i++)
	{
	  outuint(c, (buffer, unsigned[])[i]); 
	}
      
      /* Tail tidy up */
      i++;
      i <<= 2;
      
      switch(datalength & 0x3)
	{
	case 0: 
	  break; // word aligned
	  
	case 1:
	  tmp = buffer[i-4];
	  outuint(c, tmp);
	  break;
	  
	case 2:
	  tmp = buffer[i-3];
	  tmp <<= 8;
	  tmp |= buffer[i-4];
	  outuint(c, tmp);
	  
	  break;
	  
	case 3:
	  tmp = buffer[i-2];
	  tmp <<= 8;
	  tmp |= buffer[i-3];
	  tmp <<= 8;
	  tmp |= buffer[i-4];
	  outuint(c, tmp);
	  break;
	}
      
      
    }
  else
    {
      j = 0;
      while(1)
	{
	  outuint(c, XUD_CMD_IN_RST_PID);
	  outuint(c, epNum);
	  
	  if (testct(c)) {
	    inct(c);
	    chkct(c,1);
	    return -1;
	  }
	  inuint(c);
	  
	  outuint(c, (unsigned)pid);
	  pid ^= 0x88;
	  //printint(datalength);
	  if(datalength > epMax)
	    {
	      /* Output datalength */
	      outuint(c, epMax);
	      
	      /* Send data: NOTE: We assume word aligned length here */
	      for (int x = 0; x < epMax >> 2; x++)
		{
		  outuint(c, (buffer, unsigned[])[j]);
		  j++;
		}
	      datalength-=epMax;
	    }
	  else
	    {
	      outuint(c, datalength);
	      for (int x = 0; x < datalength >> 2; x++)
		{
		  outuint(c, (buffer, unsigned[])[j]);
		  j++;
		}
	      
	      /* Tail tidy up */
	      i = j ; // TODO - rename.

	      i++;

	      i <<= 2;
	      
	      switch(datalength & 0x3)
		{
		case 0: 
		  break; // word aligned
		  
		case 1:
		  tmp = buffer[i-4];
		  outuint(c, tmp);
		  break;
		  
		case 2:
		  tmp = buffer[i-3];
		  tmp <<= 8;
		  tmp |= buffer[i-4];
		  outuint(c, tmp);
		  
		  break;
		  
		case 3:
		  tmp = buffer[i-2];
		  tmp <<= 8;
		  tmp |= buffer[i-3];
		  tmp <<= 8;
		  tmp |= buffer[i-4];
		  outuint(c, tmp);
		  break;
		}
	  
	  
	  
	  
	      break; //out of while loop
	    }
	}
    }
  
  outct(c, XS1_CT_PAUSE);
  
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

int XUD_DoGetRequest(chanend c, unsigned epNum, uint8 buffer[], unsigned datalength)
{
  /* TODO: Size = EP 0 Max. Datalength */
  unsigned char tmpBuffer[1024];

  
  
  /* Send data (Reset PID sequencing for start of req) */
  //for(int i = 0; i < datalength; i++) // TODO, Copying because reading out in words, may trap 
    //tmpBuffer[i] = buffer[i]; 
      
//if(datalength > 100)
  //  printintln(datalength);
              
  XUD_SetBuffer_ResetPid_EpMax(c, 0, buffer, datalength, 64, PIDn_DATA1);

  /* Status stage (0 length OUT) TODO: Check this is valid */
  XUD_GetBuffer_(c, 0, tmpBuffer);

}

/** XUD_SetDevAddr()
  * @brief Sets device address
  */
void XUD_SetDevAddr(chanend c, unsigned addr)
{
  // TODO Use tok/ctoks for cmds
  outuint(c, XUD_CMD_SETADR);
  // only really need tok here:
  outuint(c, addr);
  outct(c, XS1_CT_PAUSE);
}

/* Send 0 length status */
int XUD_DoSetRequestStatus(chanend c, unsigned epNum)
{
   // We could do:
   //int buffer[] = {}
   //XUD_SetBuffer_ResetPid_(c_ep0, c_ep0_con, 0, buffer, 0, PIDn_DATA1);
   // But the follwoing is faster:

   /* Send command */
   outuint(c, XUD_CMD_IN_RST_PID);           

   /* Send EP number */
   outuint(c, epNum);   
   
   outct(c, XS1_CT_PAUSE); 
 
   /* Wait for response */
  if (testct(c)) {
    inct(c);
    chkct(c,1);
    return -1;
  }
   inuint(c);                // Wait for response from EP manager
     
   /* Send PID */
   outuint(c, PIDn_DATA1);  

   /* Send datalength */
   outuint(c, 0);   

   /* Free up route */
   outct(c, XS1_CT_PAUSE);
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
