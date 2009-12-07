#include "coProcessor.h"
#include "iic.h"
#include "global.h"

/*
 * Apple Authentication Coprocessor Functions
 * Ref 2.0B Specification
 */

int coProcessorAddress = COPRO_IIC_ADDRESS;
#ifdef CS4351
int dacAddress = DAC_IIC_ADDRESS;
#endif

int coProcessorInit()
{
  unsigned char retVal[1];
  
#ifdef CS4351
  if (iic_read(dacAddress, 0x1, retVal, 1))
    return 1;
  
  retVal[0] = 0x40;
  if (iic_write(dacAddress, 0x8, retVal, 1))
    return 1;
  
  retVal[0] = 0x1 << 4;
  if (iic_write(dacAddress, 0x2, retVal, 1))
    return 1;
#endif
  
  if (iic_read(coProcessorAddress, Register_DeviceVersion, retVal, 1))
    return 1;

  if (retVal[0] != COPROCESSOR_VERSION)
    return 1;
  
  return 0;
}

int coProcessorGetAuthVersion(char *major, char *minor)
{
  if (iic_read(coProcessorAddress, Register_AuthProtocolMajorVersion, (unsigned char *)major, 1))
    return 1;
  if (iic_read(coProcessorAddress, Register_AuthProtocolMinorVersion, (unsigned char *)minor, 1))
    return 1;
  
  return 0;
  
}

int coProcessorGetDeviceID(int *retVal)
{ 
  if (iic_read(coProcessorAddress, Register_DeviceID, (unsigned char *)retVal, 4))
    return 1;
  
  return 0;
}

int coProcessorGetAccessoryCertificate(unsigned short *len, unsigned char *data)
{
  unsigned char val[2];
  
  if (iic_read(coProcessorAddress, Register_AccessoryCertDataLen, val, 2))
    return 1;
  
  *len = val[1] | (val[0] << 8);
  
  if (*len > 1920)
    return 1;
  
  if (iic_read(coProcessorAddress, Register_AccessoryCertDataPtr, data, *len))
    return 1;
  
  return 0;
}

int coProcessorGetSignature(unsigned *len, unsigned char *data)
{
  unsigned char lenarray[2];
  
  if (iic_read(coProcessorAddress, Register_SigDataLength, lenarray, 2))
    return 3;
  
  *len = (lenarray[0] << 8) | lenarray[1];

  if (*len > 128)
    return 4;
  
  if (*len == 0)
    return 5;
  
  if (iic_read(coProcessorAddress, Register_SigData, data, *len))
    return 6;
    
  return 0;
}

int coProcessorGetChallenge(unsigned short *len, unsigned char *data)
{
  if (iic_read(coProcessorAddress, Register_ChallDataLength, (unsigned char *)len, 2))
    return 1;
  
  if (*len > 20)
    return 1;
  
  if (iic_read(coProcessorAddress, Register_ChallData, data, *len))
    return 1;
  
  return 0;
}

int coProcessorPutChallenge(unsigned short len, unsigned char *data)
{
  char lenarray[2];
  if (len > 20)
    return 1;

  lenarray[0] = (len >> 8) & 0xFF;
  lenarray[1] = (len & 0xFF);
  
  if (iic_write(coProcessorAddress, Register_ChallDataLength, (unsigned char*)lenarray, 2))
    return 1;

  if (iic_write(coProcessorAddress, Register_ChallData, data, len))
    return 1;
  return 0;
}

int coProcessorStartSigGeneration()
{  
  unsigned char retVal = ProcControl_StartSigGeneration;
  
  if (iic_write(coProcessorAddress, Register_AuthControlStatus, &retVal, 1))
    return 1;
  
  return 0;
}

int coProcessorStartSigVerification()
{  
  unsigned char retVal = ProcControl_StartSigVerification;
  
  if (iic_write(coProcessorAddress, Register_AuthControlStatus, &retVal, 1))
    return 1;
  
  return 0;
}

int coProcessorPutSignature(unsigned short len, unsigned char *data)
{  
  if (iic_write(coProcessorAddress, Register_SigDataLength, (unsigned char *)&len, 2))
    return 1;
  
  if (len > 20)
    return 1;
  
  if (iic_write(coProcessorAddress, Register_SigData, data, len))
    return 1;
  
  return 0;
}

int coProcessorBusy()
{
  unsigned char retVal;;
  
  if (iic_read(coProcessorAddress, Register_DeviceVersion, &retVal, 1))
    return 1;
  
  return 0;
}

int coProcessorValidResults()
{
  unsigned char retVal;
  
  if (iic_read(coProcessorAddress, Register_AuthControlStatus, &retVal, 1))
    return 1;

  // Check PROC_RESULTS for non-zero value (indicates success)
  if ((retVal >> 4) & 0x7)
  {
    return 0;
  }
  else
  {
    return 1;
  }  
  
  return 0;
}


int coProcessorGenerateChallenge()
{
  unsigned char retVal = ProcControl_StartChallGeneration;
  
  if (iic_write(coProcessorAddress, Register_AuthControlStatus, &retVal, 1))
    return 1;
  
  return 0;
}

int coProcessorPutiPodCert(unsigned short len, unsigned char *data)
{
  int i;
  
  if (iic_write(coProcessorAddress, Register_iPodCertDataLen, (unsigned char *)&len, 2))
    return 1;
  
  if (len > 1024)
    return 1;
  
  for (i = 0; i < len / 128; i++)
  {
    if (iic_write(coProcessorAddress, Register_iPodCertDataPtr + i, data, len))
      return 1;
    
    data += 128;    
  }
  
  return 0;
}

int coProcessorStartCertValidation()
{
  unsigned char retVal = ProcControl_StartCertValidation;
  
  if (iic_write(coProcessorAddress, Register_AuthControlStatus, &retVal, 1))
    return 1;
  
  return 0;
}
