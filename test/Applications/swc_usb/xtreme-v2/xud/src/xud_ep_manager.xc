
//#define EP_COUNT 4

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
static int serviceEndpoint(XUD_chan xc, chanend c_lld_ep, unsigned cmd, unsigned epNum)
{
    unsigned address, datalength, tmp = 999;
    unsigned char pid;
    int i;

#pragma unsafe arrays
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
         
                /* Send data to endpoint */
                for (i = 0; i < (datalength >> 2); i++)
                {
                    tmp = getUint(address, i);
                    XUD_Sup_outuint(xc, tmp);
                }

                /* Tail */
                if(datalength & 0x3)
                {
                    tmp = getUint(address, i);
                    XUD_Sup_outuint(xc, tmp);
                }

                /* Close down route and sync */
                XUD_Sup_outct(xc, XS1_CT_END);

                // Tell buffer manager that we took the data
                outuint(c_lld_ep, XUD_CMD_OUT);
                outuint(c_lld_ep, epNum);
        
                return 1;
            }
            else
            {
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
       
        // Send Datalength 
        XUD_Sup_outuint(xc, 8);
        
        // Send data from buffer...
        XUD_Sup_outuint(xc, getUint(address, 0)); 
        XUD_Sup_outuint(xc, getUint(address, 1));

        /* Close down route and sync */
        XUD_Sup_outct(xc, XS1_CT_END);

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
      
        // Respond to to EP ok to send data (NOTE: could be any value)
        XUD_Sup_outuint(xc, 100);         
        XUD_Sup_outct(xc, XS1_CT_END); 

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

        /* Close down route and sync */
        XUD_Sup_inct(xc);
     
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

      // Check if buffer B free
      if(ep_datasize_table_IN_B[epNum] == 0)
      {

        // Get secondary buffer
        address = bufferSelectTable2_IN[epNum];
      
        // Respond to to EP ok to send data
        XUD_Sup_outuint(xc, epNum);          // NOTE: could be any value
        XUD_Sup_outct(xc, XS1_CT_END);

        // Receive PID 
        pid = (unsigned char) XUD_Sup_inuint(xc);

          if(XUD_Sup_testct((XUD_chan) xc))
        {
            printstr("Unexpected control token from: r");
            printhexln(xc);
            printint(epNum);
}
        // Get datalength
        datalength = XUD_Sup_inuint(xc);

        // Receive data and store to endpoint buffer
        for(i = 0; i < datalength >> 2; i++)
        {
          tmp = XUD_Sup_inuint(xc);
          setUint(address, i, tmp);
        }

        // Tail
        if( datalength & 0x3 )
        {
          tmp = XUD_Sup_inuint(xc);
          setUint(address, i, tmp);
        }

        //XUD_Sup_outct(xc, XS1_CT_END);
        XUD_Sup_inct(xc);
        



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
            printstr("ERR: Unknown command from endpoint  ");
      printintln(epNum);
      printintln(cmd);
      break;


  }
  return 0;
  
  
}

int EpManSelect(chanend, XUD_chan[], int, int);

void XUD_LLD_EpManager(chanend c_lld_ep, XUD_chan epDatChanends[], chanend c_ctl_ep, XUD_EpType epTypeTableOut[], XUD_EpType epTypeTableIn[])
{
    unsigned epNum;  //TODO: Char
    unsigned epNumIndex;
    unsigned cmd, result, tmp;
    int threadActive = 1;
    unsigned char ctmp;

    /* Records number of outstanding endpoints waiting for service */
    int epWaiting = 0;		                        
   
    /* Service list records which endpoints want servicing and what they want */
    unsigned serviceList[XUD_EP_COUNT * 2]; 
    
    //start_streaming_slave(c_lld_ep);
  
    /* Initialise serviceList */
    for(int i = 0; i < (XUD_EP_COUNT * 2); i++)
    {
        serviceList[i] = XUD_CMD_NULL;
    }
 
    while(threadActive)
    {
        /* This essentially does a select and returns which resource fired, 3 for none, 0 for kill.  If epWaiting == 0 then waits.*/
        /* Note, don't use 0x02 since valid resource ID */
        result = EpManSelect(c_lld_ep,  epDatChanends, epWaiting, (XUD_EP_COUNT * 2)); 
    
        switch(result)
        {
            case 0:
                /* Kill thread */
                threadActive = 0;
	              break;

            /* No event fires i.e. default case */
            case 3:
                /* NOTE: If epWaiting == 0 we never get here (pauses in EpManSelect) */
                for(int i = 0; i < (XUD_EP_COUNT*2); i++)
                {
                    if(serviceList[i] != XUD_CMD_NULL)
                    {
                        /* Attempt to service endpoint */
                        if(i < XUD_EP_COUNT)
                        {
                            tmp = serviceEndpoint(epDatChanends[i], c_lld_ep, serviceList[i], i);
                        }
                        else
                        {
                            tmp = serviceEndpoint(epDatChanends[i], c_lld_ep, serviceList[i], i-XUD_EP_COUNT);
                        }
                        
                        /* Check if service successful */
                        if(tmp == 1) 
                        {
                            /* Mark endpoint as serviced */
                            serviceList[i] = XUD_CMD_NULL;  
              
                            /* Decrement "requires servicing" count */
                            epWaiting--;
                            
                            /* Quick sanity check.... */
                            if(epWaiting < 0)
                            {
                                XUD_Error("XUD_EpManager: epWaiting cannot be less than zero!");
                            }
                        }
                    } 
                 }
                break;
        

            /* Endpoint requests service, EpManSelect returns a channel ID */
            default:
            {
                /* Receive cmd and ep num */
                epNum = XUD_Sup_inuint((XUD_chan) result);
                XUD_Sup_inct((XUD_chan) result);
            
                /* Extract command and EP num */
                cmd = epNum & 0xff;   
                epNum = epNum >> 8;     

                /* Check for SETADR special case */
                if(cmd == XUD_CMD_SETADR)
                {
                    /* TODO Possibility of out of order delvery here */
                    outuint(c_ctl_ep, 1);
                    outuint(c_ctl_ep, epNum);
                    outct(c_ctl_ep, XS1_CT_END);
                }
                else
                {
                    /* Check for IN and used correct chanend */ 
                    if(cmd == XUD_CMD_IN || cmd == XUD_CMD_IN_RST_PID)
                        epNumIndex = epNum + XUD_EP_COUNT;
                    else
                        epNumIndex = epNum;

                    /* Attempt to service EP. If it cant, it gets added to service list */ 
                    if(serviceEndpoint(epDatChanends[epNumIndex], c_lld_ep, cmd, epNum ) == 0)
                    {
                        /* Sanity check that EP marked as requires service */
                        if(serviceList[epNumIndex] != XUD_CMD_NULL)
                            printstrln("XUD Ep Manager: Command overrun");
          
                        /* Set EP as requiring service */
                        serviceList[epNumIndex] = cmd;
  
                        epWaiting++; 
             
                        if(epWaiting>XUD_EP_COUNT_2)
                            printstrln("ERR: epWaiting cannot be greater than ep count!");  // Sanity
                    }
                }
                break;
            }

        }

        if(threadActive == 0)
        {
            //printstr("ep man killed");
            break;
        }
    }

    /* Internal channel shutdown */
    chkct(c_lld_ep, XS1_CT_END);
    outct(c_lld_ep, XS1_CT_END);

    /* Kill the endpoint threads */
    for(int i = 0; i < (XUD_EP_COUNT*2); i++) 
    { 
        if(i >= XUD_EP_COUNT
           ? (epTypeTableIn[i - XUD_EP_COUNT] != XUD_EPTYPE_DIS)
           : (epTypeTableOut[i] != XUD_EPTYPE_DIS)) {
            //XUD_Sup_outct(epDatChanends[i], XUD_CT_RESET);
            XUD_Sup_outuint(epDatChanends[i], -1);
            XUD_Sup_outct(epDatChanends[i], XS1_CT_END);
            while(1)
            {
       
                if(XUD_Sup_testct(epDatChanends[i]))
                {
                    ctmp = XUD_Sup_inct(epDatChanends[i]);
                    if(ctmp == XUD_CT_ACK)
                    {
                        XUD_Sup_inct(epDatChanends[i]);
                        break;
                    }
                }
                else
                {
                    XUD_Sup_inuint(epDatChanends[i]);
                }
            }
        }
    } 

    //printstr("ep man killed");

    return;
}

