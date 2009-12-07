#include <platform.h>
#include <xs1.h>
#include "UsbHostLLD.h"
#include "epManager.h"
#include "iPodMain.h"
#include "portManager.h"
#include "audioManager.h"
#include "remote.h"
#include "global.h"
#include "WolfsonSonaptic.h"
#include "Srs.h"


#define USB_CORE 0
#define AUDIO_CORE 0

// InfraRed Receiver Port
in port   pIR                               = PORT_IR;

// 32bit Port for low-speed outputs
out port p32                                 = XS1_PORT_32A;
in port pRegWrite0                           = XS1_PORT_4E;
in port pRegWrite1                           = XS1_PORT_4F;

// IIC ports
port PORT_IIC_SCL                            = PORT_IIC_CLK;
port PORT_IIC_SDA                            = PORT_IIC_DATA;

// ULPI ports + supporting ports
in port  p_usb_clk                   = UIFM_USB_CLK_PORT;
out port reg_write_port              = UIFM_REG_WRITE_PORT;
in  port reg_read_port               = UIFM_REG_READ_PORT;
in  port flag0_port                  = UIFM_FLAG_0_PORT;
in  port flag1_port                  = UIFM_FLAG_1_PORT;
in  port flag2_port                  = UIFM_FLAG_2_PORT;
out buffered port:32 p_usb_txd       = UIFM_TX_DATA_PORT;
buffered port:32 p_usb_rxd           = UIFM_RX_DATA_PORT;
clock    clk                         = UIFM_CLKBLK;

in port pSS                          = XS1_PORT_1B;

// Audio output ports (I2S + SPDIF)
s_audio audioports =
{
    PORT_SPDIF,
    PORT_DAC_DATA,
    PORT_DAC_BCLK,
    PORT_DAC_MCLK,
#ifdef LR_CLK_1B_PORT
    PORT_DAC_LRCLK,
#endif
    AUDIO_CLKBLK_0,
    AUDIO_CLKBLK_1,   
};

void dummy(int n)
{
  switch (n)
  {
  case 1:
    par
    {
      { set_thread_fast_mode_on(); while (1); }
    }
    break;
  case 2:
    par
    {
      { set_thread_fast_mode_on(); while (1); }
      { set_thread_fast_mode_on(); while (1); }
    }
    break;
  case 3:
    par
    {
      { set_thread_fast_mode_on(); while (1); }
      { set_thread_fast_mode_on(); while (1); }
      { set_thread_fast_mode_on(); while (1); }
    }
    break;
  }
}

int main ()
{
  chan cUSBCtrl;
#ifdef USE_STREAMING_CHANNELS
  streaming chan cCtrl[NUM_CTRL_CHANENDS];
#else
  chan cCtrl[NUM_CTRL_CHANENDS];
#endif
  streaming chan cOsc, cUSBReset, cLED, cI2CReset, cIR;
  streaming chan cUSBSOF;
  streaming chan cAudioConfig[3];
  streaming chan cAudioData;
  streaming chan cUSBReg;
  par
  {
    iPodMain(cCtrl[2], cI2CReset, cAudioConfig, cOsc, cCtrl[0], cIR, cLED);
    USBHostStackWrapper(cCtrl, cUSBCtrl);
    usbHostLLD(cUSBReset, cUSBCtrl, cUSBSOF, cUSBReg);
#ifdef WOLFSON_SONAPTIC
    portManagerWrapper(
        p32, cOsc, cUSBReset, cLED, cI2CReset, cIR, pIR, // portManager stuff
        cAudioData, cUSBSOF, cAudioConfig[0], 
        audioports,
        pRegWrite0, pRegWrite1, cUSBReg);
    WolfsonSonapticVirtualizer(cCtrl[1], cAudioData, cAudioConfig[1]);
#ifdef ENFORCE_FAST_MODE
    dummy(2);
#endif
#elif defined SRS
    portManagerWrapper(
        p32, cOsc, cUSBReset, cLED, cI2CReset, cIR, pIR, // portManager stuff
        cAudioData, cUSBSOF, cAudioConfig[0], 
        audioports,
        pRegWrite0, pRegWrite1, cUSBReg);
    srs_run(cCtrl[1], cAudioData, cAudioConfig[1]);
#ifdef ENFORCE_FAST_MODE
    dummy(2);
#endif
#else
    portManagerWrapper(
        p32, cOsc, cUSBReset, cLED, cI2CReset, cIR, pIR, // portManager stuff
        cCtrl[1], cUSBSOF, cAudioConfig[0], audioports,
        pRegWrite0, pRegWrite1, cUSBReg);
#ifdef ENFORCE_FAST_MODE
    dummy(3);
#endif
#endif
    
  }
  return 0;    
};
