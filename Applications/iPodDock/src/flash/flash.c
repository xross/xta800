#include <xs1.h>
#include <xs1_user.h>
#include "print.h"
#include "flashlib.h"
#include "global.h"
#include "coProcessor.h"

#define settw(a,b) {__asm__ __volatile__ ("settw res[%0], %1": : "r" (a) , "r" (b));}
#define setc(a,b) {__asm__ __volatile__ ("setc res[%0], %1": : "r" (a) , "r" (b));}
#define setclk(a,b) {__asm__ __volatile__ ("setclk res[%0], %1": : "r" (a) , "r" (b));}
#define portin(a,b) {__asm__ __volatile__ ("in %0, res[%1]": "=r" (b) : "r" (a));}
#define portout(a,b) {__asm__ __volatile__ ("out res[%0], %1": : "r" (a) , "r" (b));}

fl_PortHolderStruct p_flash = {
    XS1_PORT_1A,
    XS1_PORT_1B,
    XS1_PORT_1C,
    XS1_PORT_1D
};
int store_offset = 0x18000;

int flash_pre()
{
  setclk(p_flash.spiMISO, XS1_CLKBLK_REF);
  setclk(p_flash.spiCLK, XS1_CLKBLK_REF);
  setclk(p_flash.spiMOSI, XS1_CLKBLK_REF);
  setclk(p_flash.spiSS, XS1_CLKBLK_REF);

  
  setc(p_flash.spiMISO, XS1_SETC_INUSE_OFF);
  setc(p_flash.spiCLK, XS1_SETC_INUSE_OFF);
  setc(p_flash.spiMOSI, XS1_SETC_INUSE_OFF);
  setc(p_flash.spiSS, XS1_SETC_INUSE_OFF);
  
  setc(p_flash.spiMISO, XS1_SETC_INUSE_ON);
  setc(p_flash.spiCLK, XS1_SETC_INUSE_ON);
  setc(p_flash.spiMOSI, XS1_SETC_INUSE_ON);
  setc(p_flash.spiSS, XS1_SETC_INUSE_ON);
  
  setc(p_flash.spiMISO, XS1_SETC_BUF_BUFFERS);
  setc(p_flash.spiMOSI, XS1_SETC_BUF_BUFFERS);
  setc(p_flash.spiSS, XS1_SETC_BUF_BUFFERS);
  
  settw(p_flash.spiMISO, 8);
  settw(p_flash.spiSS, 8);
  settw(p_flash.spiMOSI, 8);
  
  return fl_connect(&p_flash);
}

void flash_post()
{
  fl_disconnect(&p_flash);
  
  setc(p_flash.spiMISO, XS1_SETC_INUSE_OFF);
  setc(p_flash.spiCLK, XS1_SETC_INUSE_OFF);
  setc(p_flash.spiMOSI, XS1_SETC_INUSE_OFF);
  setc(p_flash.spiSS, XS1_SETC_INUSE_OFF);
  
  setc(p_flash.spiMISO, XS1_SETC_INUSE_ON);
  setc(p_flash.spiCLK, XS1_SETC_INUSE_ON);
  setc(p_flash.spiMOSI, XS1_SETC_INUSE_ON);
  setc(p_flash.spiSS, XS1_SETC_INUSE_ON);
  
  setc(p_flash.spiMISO, XS1_SETC_BUF_BUFFERS);
  settw(p_flash.spiMISO, 32);
  setclk(p_flash.spiMISO, AUDIO_CLKBLK_0);
  
  setclk(p_flash.spiCLK, XS1_CLKBLK_REF);
  setclk(p_flash.spiMOSI, XS1_CLKBLK_REF);
  setclk(p_flash.spiSS, XS1_CLKBLK_REF);

}

int flash_read(unsigned cAudio, unsigned int offset, unsigned int size, unsigned char buffer[], unsigned cI2CReset)
{
  int store_offset, i;
  int nullval;
  
  portout(cAudio, 3);
  portin(cAudio, nullval);
  
  portout(cI2CReset, 0);
  portin(cI2CReset, nullval);
  
  flash_pre();
  
  store_offset = fl_getStorePartitionBase();
  
  for (i=0; i<size; i+=256)
  {
    fl_readPage( store_offset + i, buffer + i );
  }
  
  flash_post();
  
  portout(cI2CReset, 1);
  portin(cI2CReset, nullval);
  
  portout(cAudio, 3);
  portin(cAudio, nullval);
  
  return 0;
}

int flash_write(unsigned cAudio, unsigned int offset, unsigned int size, unsigned char buffer[], unsigned cI2CReset )
{
  int i;
  int nullval;
  
  portout(cAudio, 3);
  portin(cAudio, nullval);
  
  portout(cI2CReset, 0);
  portin(cI2CReset, nullval);
  flash_pre();
  
  for (i=0; i<fl_getNumSectors(); i++)
  {
    if (fl_getSectorAddress(i))
    {
      fl_setSectorProtection( i, 0 );
      fl_eraseSector(i);
    }
  }
  
  for (i=0; i<size; i+=256)
  {
    fl_writePage( store_offset + i, buffer + i );
  }
  fl_setSectorProtection( i, 1 );
  
  flash_post();
  
  portout(cI2CReset, 1);
  portin(cI2CReset, nullval);
  
  portout(cAudio, 3);
  portin(cAudio, nullval);
  
  return 0;
}


