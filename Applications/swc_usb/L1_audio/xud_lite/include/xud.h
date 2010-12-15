
#ifndef XUD_H
#define XUD_H 1

#include "XUD_Support.h"
#include "XUD_Defines_Internal.h"

typedef unsigned char 	uint8;
typedef unsigned int 	uint32;

/* Typedef for endpoint types.  Note: its is importanct that ISO is 0 */
typedef enum 
{
    XUD_EPTYPE_ISO=0,   
    XUD_EPTYPE_INT,
    XUD_EPTYPE_BUL,
    XUD_EPTYPE_CTL,
    XUD_EPTYPE_DIS,
} XUD_EpType;


/* Typedef for BmRequestType structure */
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


/* Typedef for SetupPacket structure */
typedef struct setupPacket
{ 
  BmRequestType bmRequestType;   // (1 byte) Specifies direction of dataflow, type of rquest and recipient
  unsigned char bRequest;        // Specifies the request
  unsigned short wValue;         // (2 bytes) Host can use this to pass info to the device in its own way
  unsigned short wIndex;         // (2 bytes) Typically used to pass index/offset such as interface or EP no
  unsigned short wLength;        // (2 Bytes) Number of data bytes in the data stage (for Host -> Device this                                   // this is exact count, for Dev->Host is a max.
} SetupPacket;


/* Maximum number of endpoints. Do not modify unless you know what you are doing! */
#define XUD_EP_COUNT 4  
#define XUD_EP_COUNT_2 ( XUD_EP_COUNT * 2 )


/** XUD_Manager()
  * @brief This performs the low level USB I/O operations.  This takes channels to endpoint threads as arguments.
  * Due to I/O performance requirements run with 6+ threads at your own risk!
  */
int XUD_Manager(chanend c_ep0_out, chanend c_ep0_in, chanend, chanend, chanend, chanend, chanend, chanend,  chanend c_sof, XUD_EpType EpTypeTableOut[], XUD_EpType EpTypeTableIn[]);

/** XUD_ParseSetupPacket
  * @brief Parses a setup data buffer into passed SetupPacket structure 
  */
void XUD_ParseSetupPacket(unsigned char b[], SetupPacket &p);

/** XUD_PringSetupPacket 
 *  @brief Prints out passed SetupPacket struct using debug IO
 */
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
int XUD_DoEnumReqs(chanend c, chanend c_in, unsigned char devDesc[], int devDescLength, uint8 cfgDesc[], 
    int cfgDescLength, uint8 devQualDesc[], int devQualLength,  uint8 strDescs[][40], SetupPacket &sp);

/* Error printing functions */
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
int XUD_GetSetupBuffer(chanend c_out, unsigned char buffer[]);


/** XUD_GetBuffer() / XUD_GetBuffer_int() 
  * @brief Request data from USB buffer for specified EP, pauses untill data is available
  * @return datalength in bytes, -1 if kill received
  **/
int XUD_GetBuffer(chanend c, unsigned char buffer[]);
#define XUD_GetBuffer_u32(CHAN, BUFF)  XUD_GetBuffer(CHAN, (BUFF, char[]));


#define XUD_SetBuffer(CHAN, BUFF, LENGTH)   XUD_SetData(CHAN, BUFF, LENGTH, 0, 0); // no pid toggle, 0 start index


int XUD_SetBuffer_ResetPid(chanend c, unsigned char buffer[], unsigned datalength, unsigned char pid);

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
  *	  - Zero-length out transaction status stage
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
//int XUD_DoGetRequest(chanend c, unsigned epNum, uint8 buffer[], unsigned datalength);
int XUD_DoGetRequest_(chanend c, chanend c_in, unsigned epNum, uint8 buffer[], unsigned datalength);
int XUD_DoSetRequestStatus(chanend c, unsigned epnNum);

/** XUD_SetDevAddr()
 * @brief Sets device address
 * @param c Chanend to XUD
 * @param addr Address to be set
 * @return void
 */
void XUD_SetDevAddr(unsigned addr);


/* DEPRECATE: */
int XUD_GetBuffer_int(chanend c, unsigned epNum, int buffer[]);
int XUD_SetBuffer_ResetPid_(chanend c, unsigned epNum, unsigned char buffer[], unsigned datalength, unsigned char pid);
#define XUD_GetBuffer_(CHAN, EPNUM, BUFF)   XUD_GetBuffer(CHAN, (BUFF, char[]));

#endif /* _XUD_H_ */
