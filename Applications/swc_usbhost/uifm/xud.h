
#ifndef XUD_H
#define XUD_H 1

#include "XUD_Support.h"
#include "XUD_Defines_Internal.h"

typedef unsigned char 	uint8;
typedef unsigned int 	uint32;

typedef enum 
{
    XUD_EPTYPE_ISO=0,   /* Note: it is important that ISO is 0 */
    XUD_EPTYPE_INT,
    XUD_EPTYPE_BUL,
    XUD_EPTYPE_CTL,
    XUD_EPTYPE_DIS,
} XUD_EpType;

#define XUD_EP_COUNT 6 // This gets *2 for in/out
#define XUD_EP_COUNT_2 ( XUD_EP_COUNT * 2 )

void XUD_RequestSendBuffer(chanend c, unsigned epNum);


int XUD_SetBuffer_int(chanend c, unsigned epNum, int buffer[], unsigned datalength);


/** XUD_Manager()
  * @brief This performs the low level I/O operations and required buffering.  This pars 4 threads
  * and should be run on a core on its own due to MIPS requirements.
  *
  **/
int XUD_Manager(chanend c_ep0,
                chanend, chanend, chanend, chanend, chanend, 
                chanend, chanend, chanend, chanend, chanend, 
                out port, out port,
                XUD_EpType EpTypeTableOut[], XUD_EpType EpTypeTableIn[], chanend);

/** Typedef for BmRequestType structure
  *
  **/
typedef struct bmRequestType
{
  unsigned char Recipient;       // [4..0]   Request directed to:
                                 //          0b00000: Device
                                 //          0b00001: Specific interface
                                 //          0b00010: Specific endpoint
                                 //          0b00011: Other element in device                           
  unsigned char Type;            // [6..5]   0b00: Standard request
                                 //          0b01: Class specific request
                                 //          0b10: Request by vendor specific driver 
  unsigned char Direction;       // [7]      0 (Host->Dev)
                                 //          1 (Dev->Host)
} BmRequestType;


/** Typedef for SetupPacket structure 
  *
  **/
typedef struct setupPacket
{ 
  BmRequestType bmRequestType;   // (1 byte) Specifies direction of dataflow, type of rquest and recipient
  unsigned char bRequest;        // Specifies the request
  unsigned short wValue;         // (2 bytes) Host can use this to pass info to the device in its own way
  unsigned short wIndex;         // (2 bytes) Typically used to pass index/offset such as interface or EP no
  unsigned short wLength;        // (2 Bytes) Number of data bytes in the data stage (for Host -> Device this                                   // this is exact count, for Dev->Host is a max.
} SetupPacket;

void XUD_ParseSetupPacket(unsigned char b[], SetupPacket &p);

/** XUD_PringSetupPacket 
 *  @ brief Prints out passed SetupPacket struct
 **/
void XUD_PrintSetupPacket(SetupPacket sp);


/** XUD_DoEnumReqs()
  * @brief This function performs some of the common USB standard requests in order to reduce the number 
  * of requests the developer must implement.  It automatically handles the following standard requests 
  * appropriately using values passed to it:
  *
  *   - Get Device Descriptor (Using devDesc argument)
  *   - Get Configuration Descriptor (Using cfgDesc argument)
  *   - Set Address
  *   - String requests (using strDesc argument)
  *   - Get Micro$oft OS String Descriptor (Usings product ID string) 
  *   - Get Device_Qualifier Descriptor 
  *   - Get Other-Speed Configuration Descriptor (Currently sends default config descriptor)
  *   
  *  This function returns 1 if the request has been dealt with successfully, 0 if not.  The SetupPacket
  *  structure should then be examined for device specific requests.
  *
  * @param c Channel to XUD (ep 0) 
  * @param devDesc Device descriptor
  * @param cfgDesc Configuration descriptor
  * @param devQualDesc Device Qualification Descriptor
  * @param sp SetupPacket (passed by ref) in which the setup data is returned
  * @return 1 if dealt with else 
  *
  * TODO It would be nice not to have to explictly pass array lengths. 
  **/
int XUD_DoEnumReqs(chanend c, unsigned char devDesc[], int devDescLength, uint8 cfgDesc[], 
  int cfgDescLength, uint8 devQualDesc[], int devQualLength,  uint8 strDescs[][40], SetupPacket &sp);


void XUD_Error(char errString[]);
void XUD_Error_hex(char errString[], int i_err);


/** XUD_GetSetupBuffer() 
  * @brief Request setup data from usb buffer for a specific EP, pauses until data is available.  
  * @param c Data channel to/from XUD
  * @param c_ctl Control channel from XUD
  * @param epNum EP number 
  * @param buffer char buffer passed by ref into which data is returned
  * @return datalength in bytes (always 8), -1 if kill received
  **/
//TODO this could return a SetupPacket struct
int XUD_GetSetupBuffer_(chanend c, unsigned epNum, unsigned char buffer[]);


/** XUD_GetBuffer() / XUD_GetBuffer_int() 
  * @brief Request data from USB buffer for specified EP, pauses untill data is available
  * @return datalength in bytes, -1 if kill received
  **/
int XUD_GetBuffer_(chanend c,  unsigned epNum, unsigned char buffer[]);
int XUD_GetBuffer_int(chanend c, unsigned epNum, int buffer[]);



int XUD_SetBuffer_(chanend c, unsigned epNum, unsigned char buffer[], unsigned datalength);
//int XUD_SetBuffer_ResetPid(chanend c, chanend c_con, unsigned epNum, unsigned char buffer[], unsigned datalength, unsigned char pid);

int XUD_SetBuffer_ResetPid_(chanend c, unsigned epNum, unsigned char buffer[], unsigned datalength, unsigned char pid);
int XUD_SetBuffer_ResetPid_int(chanend c, unsigned epNum, int buffer[], unsigned datalength, unsigned char pid);
/* Same as above but takes a max packet size for the endpoint, breaks up data to transfers of no 
 * greater than this.
 *
 * TODO This could be done better in the EP Manager, however, the logic would be a bit more complicated. 
 * But this would remove some complexity from end developer.
 *
 * NOTE: This function reasonably assumes the max transfer size for an EP is word aligned  
 **/
int XUD_SetBuffer_ResetPid_EpMax(chanend c, unsigned epNum, unsigned char buffer[], 
  unsigned datalength, unsigned epMax, unsigned char pid);


/** XUD_DoGetReuest()
  * @brief Does a "get" request.  These take the form:
  * 	- Send data (with reset pid sequencing)
  *	- Zero-length out transaction status stage
  * 
  * @param c 		Chanend to/from XUD
  * @param epNum 	Endpoint number to which this request relates
  * @param buffer 	Data buffer to send
  * @param dataLength	Length of data to be sent
  * 
  * @return		Returns non-zero on error	
  *
  * TODO: We can assume ep 0 
  **/
int XUD_DoGetRequest(chanend c, unsigned epNum, uint8 buffer[], unsigned datalength);

int XUD_DoSetRequestStatus(chanend c, unsigned epnNum);

/** XUD_SetDevAddr()
 * @brief Sets device address
 * @param c Chanend to XUD
 * @param addr Address to be set
 * @return void
 */
void XUD_SetDevAddr(chanend c, unsigned addr);

#endif /* _XUD_H_ */
