
#define EP_COUNT 4

#include <xs1.h>
#include <print.h>

#include "xud.h"
#include "XUD_Support.h"
#include "XUD_Defines_Internal.h"
#include "XUD_USB_Defines.h"

extern unsigned char sBuffer_Datasize[];
extern unsigned sBufferSelectTable[];
extern unsigned ep_datasize_table_IN_B[];
extern unsigned ep_datasize_table_OUT_B[];
extern unsigned ep_datasize_table_IN_A[];
extern unsigned ep_datasize_table_OUT_A[];
extern unsigned bufferSelectTable2_IN[];
extern unsigned bufferSelectTable2_OUT[];
extern unsigned char ep_pid_sequence_table_IN_A[];
extern unsigned char ep_pid_sequence_table_IN_B[]; 

//unsigned serviceListOut[EP_COUNT];
//unsigned serviceListIn[EP_COUNT];

unsigned GetDatasize_OUT_A(unsigned epNum);
unsigned GetDatasize_OUT_B(unsigned epNum);

extern unsigned getUint(unsigned address, int a);
extern void     setUint(unsigned address, int a, int b);


void EpManDataSend(XUD_chan c, unsigned address, unsigned datalength);

/** @brief Service an OUT endpoint
  * @return turns zero if EP services
  **/
// returns 1 if ep successfully serviced, otherwise 0
int serviceEndpoint(XUD_chan xc, chanend c_lld_ep, unsigned cmd, unsigned epNum)
{
  unsigned address, datalength, tmp = 999;
  unsigned char pid;
  int i;
  int returnval = 0;

#pragma unsafe-arrays
  switch(cmd)
  {
    case XUD_CMD_OUT:
       
       // Check if there is out data for endpoint
       if(ep_datasize_table_OUT_B[epNum] != 0)
       {
         //printstrln("there is OUT data from ep!");
         // Get the buffer address 
         address = bufferSelectTable2_OUT[epNum];

         // Send datalength of received data
         datalength = ep_datasize_table_OUT_B[epNum];

         // Subtract -1 since always store datlength + 1
         // TODO Don't send CRC
         //datalength-=1;  
         datalength-=2;
 
         XUD_Sup_outuint(xc, datalength);
         
         // Send data
         for (i = 0; i < (datalength >> 2); i++)
         {
           tmp = getUint(address, i);
           XUD_Sup_outuint(xc, tmp);
         }

         // Tail
         if(datalength & 0x3)
         {
           tmp = getUint(address, i);
           XUD_Sup_outuint(xc, tmp);
         }

         XUD_Sup_outct(xc, XS1_CT_PAUSE);

         // Tell buffer manager that we took the data
         outuint(c_lld_ep, XUD_CMD_OUT);
         outuint(c_lld_ep, epNum);
        
         return 1;
       }
       else
       {
         //printstrln("there NO OUT data from ep!");

         return 0;
       }    



      break;

    case (XUD_CMD_SETUP): // EP wants Setup data
    { 
      // Check if there is setup data for endpoint
      if(sBuffer_Datasize[epNum] != 0)
      { 
        // Get the buffer address
        address = sBufferSelectTable[epNum];
        
        // Send data from buffer...
        XUD_Sup_outuint(xc, getUint(address, 0)); 
        XUD_Sup_outuint(xc, getUint(address, 1));
        XUD_Sup_outct(xc, XS1_CT_PAUSE);

        sBuffer_Datasize[epNum] = 0;
 
        return 1;
      } 
      else
      {
        // No data for EP, add to sericeList
        //serviceList[epNum] = XUD_CMD_SETUP;
        return 0;
      }
      break;
    }

    // EP wants to give IN data, toggle PID as normal 
    case (XUD_CMD_IN):    
    {      
      // Check if buffer B free
      if(ep_datasize_table_IN_B[epNum] == 0)
      {
        // Get secondary buffer
        address = bufferSelectTable2_IN[epNum];
      
        // Respond to to EP ok to send data
        XUD_Sup_outuint(xc, epNum);          // NOTE: could be any value
        XUD_Sup_outct(xc, XS1_CT_PAUSE);

        // Get datalength
        datalength = XUD_Sup_inuint(xc);

        // Receive data and store to endpoint buffer
        for(i = 0; i < datalength >> 2; i++)
        {
          tmp = XUD_Sup_inuint(xc);
          setUint(address, i, tmp);
        }

        // Tail
        if(datalength & 0x3)
        {
          tmp = XUD_Sup_inuint(xc);
          setUint(address, i, tmp);
        }
     
        // Do PID toggling
        ep_pid_sequence_table_IN_B[epNum] = (ep_pid_sequence_table_IN_A[epNum] ^ 0x88);
 
        // Set datalength 
        // Note we store datalength + 1 so we can do bf 
        ep_datasize_table_IN_B[epNum] = datalength+1;
 
        // Tell buffer manager that we have data to send
        outuint(c_lld_ep, XUD_CMD_IN);
        outuint(c_lld_ep, epNum);

        return 1;
      }
      else
      { 
        // No buffer available
        //printstrln("ERR: buffer full. not implemented");

        return 0;
      }
      break;
    }
    // EP wants to give IN data, RESET PID TO RECEIVED VALUE 
    case (XUD_CMD_IN_RST_PID):    
    {     
      //printstr("IN"); 
       
      // Check if buffer B free
      if(ep_datasize_table_IN_B[epNum] == 0)
      {
        // Get secondary buffer
        address = bufferSelectTable2_IN[epNum];
      
        // Respond to to EP ok to send data
        XUD_Sup_outuint(xc, epNum);          // NOTE: could be any value
        XUD_Sup_outct(xc, XS1_CT_PAUSE);

        // Receive PID
        pid = (unsigned char) XUD_Sup_inuint(xc);

        // Get datalength
        datalength = XUD_Sup_inuint(xc);

        if(datalength == 3)
          printstr("datalength is 3\n");

        // Receive data and store to endpoint buffer
        for(i = 0; i < datalength >> 2; i++)
        {
          tmp = XUD_Sup_inuint(xc);
          setUint(address, i, tmp);
        }

        // Tail
        if(datalength &0x3)
        {
          tmp = XUD_Sup_inuint(xc);
          setUint(address, i, tmp);
        }

        // DONT Do pid toggling, reset it to received value
        ep_pid_sequence_table_IN_B[epNum] = pid;
 
        // Set datalength 
        // Note we store datalength + 1 so we can do bf
        ep_datasize_table_IN_B[epNum] = datalength+1;
 
        // Tell buffer manager that we have data to send
        outuint(c_lld_ep, XUD_CMD_IN);
        outuint(c_lld_ep, epNum);

        return 1;
      }
      else
      { 
        // No buffer available
        return 0;
      }
      break;
    }
    case XUD_CMD_NULL:

      break;


    default: 
      //      printstr("ERR: Unknown command from endpoint  ");
      //printhexln(xc);
      //printintln(cmd);
      break;


  }
  return returnval;
  
  
}

int EpManSelect(chanend, XUD_chan[], int, int);

void XUD_LLD_EpManager(chanend c_lld_ep, chanend c_con_ep, chanend c_ep0_dat, XUD_chan epDatChanends[])
{
  unsigned epNum;  //TODO: Char
  unsigned cmd, result, tmp;
  //timer t;
  //int time, time2;
  unsigned char ccmd;
  unsigned char ctmp;
  int threadActive = 1;

  int epWaiting = 0;		// Records number of outstanding EP requests
  unsigned serviceList[XUD_EP_COUNT]; 
  start_streaming_slave(c_lld_ep);
  //start_streaming_master(c_ep0_out);
  //start_streaming_master(c_ep0_con);
  
  for(int i = 0; i < XUD_EP_COUNT; i++)
  {
    serviceList[i] = XUD_CMD_NULL;
  }

  while(threadActive)
  {
   
    // This essentially does a select and returns which resource fired...
    result = EpManSelect(c_con_ep,  epDatChanends, epWaiting, 3); 
    
    switch(result)
    {
       
      // Nothing has a new service request, go through service list
      // Note: don't use 0x02 since its a valid chanend ID!
      case 0:
	//      printstrln("HELP");
        threadActive = 0;
	break;

      case 3:
      {
        //if(serviceList[1] != XUD_CMD_NULL)
          //printstrln("1 can be served\n");

        for(int i = 0; i < XUD_EP_COUNT; i++)
        {
          if(serviceList[i] != XUD_CMD_NULL)
          {
            // Recover relevent chanend for endpoint and attempt service
            if(serviceEndpoint(epDatChanends[i], c_lld_ep, serviceList[i], i) == 1) 
            {
              // TODO: This should be used:
              serviceList[i] = XUD_CMD_NULL;  // Mark as service not required.
              
              epWaiting--;
              if(epWaiting < 0)
                printstrln("ERR: epWaiting cannot be less than zero!"); // sanity
            }
          } 
        }
        break;
      }

      // FIXME Seems to be a bug with wpWaiting logic (9/12/08)
      // Endpoint requests service
      // default... must have returned a chanend ID
      default:
      {
        cmd = XUD_Sup_inuint((XUD_chan) result);
        tmp = XUD_Sup_inuint((XUD_chan) result); 

        if(cmd == XUD_CMD_SETADR)
        {
                    
          outuint(c_con_ep, 1);
          outuint(c_con_ep, tmp);
          outct(c_con_ep, XS1_CT_END);
        }
        else
        { 
        // Attempt to service EP. If it cant, it gets added to list
        epNum = tmp;
        
        if(serviceEndpoint((XUD_chan) result, c_lld_ep, cmd, epNum) == 0)
        {
          serviceList[epNum] = cmd;
          //if(epNum == 1 && epWaiting != 0)
          //{  printstrln("cannot service ep");
           //  printintln(cmd);
           //  printintln(epWaiting);
          //}
          epWaiting++; 
          if(epWaiting>XUD_EP_COUNT)
            printstrln("ERR: epWaiting cannot be greater than ep count!");  // Sanity
        }
        else
        {
          //if (ep_datasize_table_IN_B[epNum] = 3+1);
          //printintln(3);
        } 
        }
        break;
      }

    }
  }

  // Kill the endpoint threads
  for(int i = 0; i < XUD_EP_COUNT; i++) 
  { 
    XUD_Sup_outct(epDatChanends[i], XUD_CT_RESET);
    XUD_Sup_outct(epDatChanends[i], XS1_CT_END);
    //    XUD_Sup_outct(epCtlChanends[i], XS1_CT_END);
    //if(i > 0)
    //{
      //printstr(" reset: ");
      //printintln(i);
    //}
  }

  //Channel shutdown
  inct(c_con_ep);
  inct(c_con_ep);
  stop_streaming_slave(c_lld_ep);

  return;
}

