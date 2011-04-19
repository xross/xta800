#include <safestring.h>
#include <print.h>


#include "xud.h"
#include "XUD_USB_Defines.h"


void ParseSetupPacket(unsigned char b[], SetupPacket &p);

int min(int a, int b) {
  if (a > b) return b; else return a;
}

int XUD_DoEnumReqs(chanend c, uint8 devDesc[], int devDescLength, uint8 cfgDesc[], int cfgDescLength, 
    uint8 devQualDesc[], int devQualDescLength, uint8 strDescs[][40], SetupPacket &sp)
{
     /* Return value */
    int reqDone = 0;
    int datalength;
    int stringID = 0;

    /* Buffer for Setup data */
    uint8 buffer[120]; 

    /* Wait for Setup data */
    if (XUD_GetSetupBuffer_(c, 0, buffer) == -1) 
		{
		  //printstr("ep 0 got rst");
      return -1;
    }

    /* Parse data buffer end populate SetupPacket struct */
    XUD_ParseSetupPacket(buffer, sp);

    /* Inspect for recipent */
    switch(sp.bmRequestType.Recipient)
    {
        /* Recipient: Device */
        case BM_REQTYPE_RECIP_DEV:
          
            /* Inspect for request type */
            switch(sp.bmRequestType.Type)
            {
                /* Request Type: Standard Request */
                case BM_REQTYPE_TYPE_STANDARD:
        
                    /* Inspect for actual request */
                    switch(sp.bRequest)
                    {
                        /* Set Device Address: This is a unique set request. */
                        case B_REQ_SETADDR:
            
                            /* Status stage: Send a zero length packet */
                            XUD_SetBuffer_ResetPid_(c, 0, buffer, 0, PIDn_DATA1);

                            /* TODO We should wait until ACK is received for status stage before changing address */
                            XUD_Sup_Delay(50000);

                            /* Set device address in XUD */
                            XUD_SetDevAddr(c, sp.wValue);

                            reqDone = 1;
                            break;
            

                        /* Request: Get Descriptor */
                        case B_REQ_GETDESC:
              
                            /* Inspect for which descriptor is required (high byte of wValue) */
                            switch(sp.wValue & 0xff00)
                            {
                                /* Device descriptor */
                                case WVALUE_GETDESC_DEV:              
               
                                    /* Do get request (send descriptor then 0 length status stage) */
                                    XUD_DoGetRequest(c, 0, devDesc, devDescLength); 

                                    reqDone = 1;
                                    break;

                                /* Configuration Descriptor */
                                case WVALUE_GETDESC_CONFIG:
                   
                                    /* Do get request (send descriptor then 0 length status stage) */
                                    /* Only send amount of data requested in wLength */
				                    XUD_DoGetRequest(c, 0, cfgDesc, min(sp.wLength,cfgDescLength)); 

                                    reqDone = 1;
                                    break;

                                /* Device qualifier descriptor */
                                case WVALUE_GETDESC_DEVQUAL:
                                     
                                    /* Do get request (send descriptor then 0 length status stage) */
                                    XUD_DoGetRequest(c, 0, devQualDesc, devQualDescLength); //TODO use sizeof!
    
                                    reqDone = 1;
                                    break;

                                /* Other Speed Configiration Descriptor */
                                case WVALUE_GETDESC_OSPEED_CFG:

                                    /* TODO Currently sending default cfg descriptor */
                                    /* Do get request (send descriptor then 0 length status stage) */
                                    /* Only send amount of data requested in wLength */
				                    XUD_DoGetRequest(c, 0, cfgDesc, min(sp.wLength,cfgDescLength)); 

                                    reqDone = 1;

                                    break;

             
                                /* String Descriptor */ 
                                case WVALUE_GETDESC_STRING:
 
                                    /* Set descriptor type */
                                    buffer[1] = XUD_DESC_DEV_B_DESCTYPE_STRING;

                                    /* Send the string that was requested (low byte of wValue) */
                                    /* First, generate valid descriptor from string */
                                    /* TODO Bounds check */
                                    stringID = sp.wValue & 0xff;

                                    /* Microsoft OS String special case, send product ID string */
                                    if ( sp.wValue == 0x03ee)
                                    {
                                        stringID = 2;
                                    }

                                    datalength = safestrlen(strDescs[ stringID ] );
                
                                    /* String 0 (LangIDs) is a special case*/ 
                                    if( stringID == 0 )
                                    {
                                        buffer[0] = datalength + 2;
                                        if( sp.wLength < datalength + 2 )
                                            datalength = sp.wLength - 2; 
                        
                                        for(int i = 0; i < datalength; i += 1 )
                                        {
                                            buffer[i+2] = strDescs[stringID][i];
                                        }
                                    }
                                    else
                                    { 
                                        /* Datalength *= 2 due to unicode */
                                        datalength <<= 1;
                      
                                        /* Set data length in descriptor (+2 due to 2 byte datalength)*/
                                        buffer[0] = datalength + 2;

                                        if(sp.wLength < datalength + 2)
                                            datalength = sp.wLength - 2; 
                        
                                        /* Add zero bytes for unicode.. */
                                        for(int i = 0; i < datalength; i+=2)
                                        {
                                            buffer[i+2] = strDescs[ stringID ][i>>1];
                                            buffer[i+3] = 0;
                                        }
                                       
                                    }
                                    
                                    /* Send back string */
                                    XUD_DoGetRequest(c, 0, buffer, datalength + 2); 
                                     
                                    reqDone = 1;
                                    break;                
  
                                default:
                                    break;

                            }
                            break;

                        default:
                            break;

                    }
                    break;

                default:
                    break;
            }    
            break; 
     
        default:
        break;
    }

    return reqDone;
}
