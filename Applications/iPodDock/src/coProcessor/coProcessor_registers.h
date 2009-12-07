#ifndef COPROCESSOR_REGISTERS_H_
#define COPROCESSOR_REGISTERS_H_

#define COPROCESSOR_VERSION 0x03

/*
 * Apple Authentication Coprocessor Registers
 * Ref 2.0B Specification
 */

#define COPRO_IIC_ADDRESS           0x11

typedef enum
{
  ProcControl_StartSigGeneration = 0x01,
  ProcControl_StartChallGeneration = 0x02,
  ProcControl_StartSigVerification = 0x03,
  ProcControl_StartCertValidation = 0x04,
} coProcessor_ProcControl;

typedef enum
{
  Register_DeviceVersion = 0x00,
  Register_FirmwareVersion = 0x01,
  Register_AuthProtocolMajorVersion = 0x02,
  Register_AuthProtocolMinorVersion = 0x03,
  Register_DeviceID = 0x04,
  Register_ErrorCode = 0x05,
  Register_AuthControlStatus = 0x10,
  Register_SigDataLength = 0x11,
  Register_SigData = 0x12,
  Register_ChallDataLength = 0x20,
  Register_ChallData = 0x21,
  Register_AccessoryCertDataLen = 0x30,
  Register_AccessoryCertDataPtr = 0x31,
  Register_SelfTestControlStatus = 0x40,
  Register_iPodCertDataLen = 0x50,
  Register_iPodCertDataPtr = 0x51
} coProcessor_Registers;

#endif /*COPROCESSOR_REGISTERS_H_*/
