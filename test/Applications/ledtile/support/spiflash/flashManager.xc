/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */

#include <xs1.h>
#include "flashManager.h"
#include "flashPhy.h"
#include "xbhandler.h"
#include "print.h"

#ifdef OTPSUPPORT
extern in port otp_data;
extern out port otp_addr;
extern out port otp_ctrl;
#endif

void spiFlash(chanend cSpiFlash,
    buffered in port:8 p_flash_miso, out port p_flash_ss, buffered out port:32 p_flash_clk, buffered out port:8 p_flash_mosi, clock b_flash_clk, clock b_flash_data)
{
  int sector;
  
  spi_init(p_flash_ss, p_flash_miso, p_flash_clk, p_flash_mosi, b_flash_clk, b_flash_data);
  
  while (1)
  {
    unsigned command;
    cSpiFlash :> command;
    switch (command)
    {
      case (SPI_FLASH_NEWFIRMWARE):
      {
        unsigned besttimestamp;
        int besttimestamplocation=0;
        unsigned worsttimestamp;
        int worsttimestamplocation=0;
        
        // Look through the avaliable sectors for the best and worst images
        for (int i=1; i<4; i++)
        {
          if (!checkXbCrc(i*SPI_NUM_BYTES_IN_SECTOR, 1, 
#ifdef OTPSUPPORT
              otp_data, otp_addr, otp_ctrl, 
#endif
              p_flash_ss, p_flash_miso, p_flash_clk, p_flash_mosi)) 
          {
            unsigned newtstamp[1];
            spi_wordread((i*SPI_NUM_BYTES_IN_SECTOR) >> 2, newtstamp, 1,  p_flash_ss, p_flash_miso, p_flash_clk, p_flash_mosi);
            if ((besttimestamplocation && (newtstamp[0] > besttimestamp)) || (!besttimestamplocation))
            {
              besttimestamplocation = i+1;
              besttimestamp = newtstamp[0];
            }
            if ((worsttimestamplocation && (newtstamp[0] < worsttimestamp)) || (!worsttimestamplocation))
            {
              worsttimestamplocation = i+1;
              worsttimestamp = newtstamp[0];
            }
          }
          else
          {
            worsttimestamplocation = i+1;
            worsttimestamp = 0;
          }
        }
        
        // Overwrite worst sector
        sector = worsttimestamplocation - 1;
        spi_unprotect(sector * SPI_NUM_BYTES_IN_SECTOR, p_flash_ss, p_flash_miso, p_flash_clk, p_flash_mosi); 
        spi_erase(sector, p_flash_ss, p_flash_miso, p_flash_clk, p_flash_mosi);
        while (spi_poll_progress (p_flash_ss, p_flash_miso, p_flash_clk, p_flash_mosi));
        
        // Set new timestamp
        {
          unsigned newtstamp[1];
          if (!besttimestamplocation)
            newtstamp[0] = 1;
          else
            newtstamp[0] = besttimestamp + 1;
          
          spi_wordwrite((sector * SPI_NUM_BYTES_IN_SECTOR) >> 2, newtstamp, 1, p_flash_ss, p_flash_miso, p_flash_clk, p_flash_mosi);
        }
        spi_protect(sector * SPI_NUM_BYTES_IN_SECTOR, p_flash_ss, p_flash_miso, p_flash_clk, p_flash_mosi); 
        
        // Tell master the sector we overwrote
        cSpiFlash <: sector;
      }
      break;
      case (SPI_FLASH_WRITEFIRMWARE):
      {
        unsigned addr, length;
        unsigned char writebuffer[1024];
        
        slave
        {
          cSpiFlash :> addr;
          addr += (sector * SPI_NUM_BYTES_IN_SECTOR) + 4;
          cSpiFlash :> length;

          spi_unprotect((addr / SPI_NUM_BYTES_IN_SECTOR) * SPI_NUM_BYTES_IN_SECTOR, p_flash_ss, p_flash_miso, p_flash_clk, p_flash_mosi); 
          while (length)
          {
            unsigned tmplength;
            if ((addr % SPI_BYTES_IN_PAGE) + length > SPI_BYTES_IN_PAGE)
            {
              tmplength = SPI_BYTES_IN_PAGE - (addr % SPI_BYTES_IN_PAGE);
            }
            else
            {
              tmplength = length;
            }
            
            for (int i=0; i<tmplength; i++)
            {
              unsigned int val;
              cSpiFlash :> val;
              writebuffer[i] = (unsigned char)val;
            }
            
            spi_write(addr, writebuffer, tmplength, p_flash_ss, p_flash_miso, p_flash_clk, p_flash_mosi);
            
            addr+=tmplength;
            length-=tmplength;
          }
          spi_protect((addr / SPI_NUM_BYTES_IN_SECTOR) * SPI_NUM_BYTES_IN_SECTOR, p_flash_ss, p_flash_miso, p_flash_clk, p_flash_mosi); 
        }
      }
      break;
      case (SPI_FLASH_READGAMMA):
      {
        unsigned blocknum, length;
        unsigned short rbuffer[256];
        slave
        {
          cSpiFlash :> blocknum;
          cSpiFlash :> length;
          spi_read((blocknum * 4096) + (4 * SPI_NUM_BYTES_IN_SECTOR), (rbuffer, unsigned char[]), length*2, p_flash_ss, p_flash_miso, p_flash_clk, p_flash_mosi);
          for (int i=0; i<length; i++)
            cSpiFlash <: (unsigned int)rbuffer[i];
        }
      }
      break;
      case (SPI_FLASH_NEWGAMMA):
      {
         unsigned addr, blocknum, bytelength;
         unsigned short writebuffer[128];
         
         slave
         {
           cSpiFlash :> blocknum;
           addr = (blocknum * 4096) + (4 * SPI_NUM_BYTES_IN_SECTOR);
           cSpiFlash :> bytelength;
           bytelength <<= 1;

           spi_unprotect((addr / SPI_NUM_BYTES_IN_SECTOR) * SPI_NUM_BYTES_IN_SECTOR, p_flash_ss, p_flash_miso, p_flash_clk, p_flash_mosi); 
           spi_blockerase(addr, p_flash_ss, p_flash_miso, p_flash_clk, p_flash_mosi);
           while (spi_poll_progress (p_flash_ss, p_flash_miso, p_flash_clk, p_flash_mosi));
  
           while (bytelength)
           {
             unsigned tmplength;
             if ((addr % SPI_BYTES_IN_PAGE) + bytelength > SPI_BYTES_IN_PAGE)
             {
               tmplength = SPI_BYTES_IN_PAGE - (addr % SPI_BYTES_IN_PAGE);
             }
             else
             {
               tmplength = bytelength;
             }
             
             for (int i=0; i<(tmplength>>1); i++)
             {
               unsigned val;
               cSpiFlash :> val;
               writebuffer[i] = (unsigned short)val;
             }
             
             spi_write(addr, (writebuffer, unsigned char[]), tmplength, p_flash_ss, p_flash_miso, p_flash_clk, p_flash_mosi);
             
             addr+=tmplength;
             bytelength-=tmplength;
           }
           spi_protect((addr / SPI_NUM_BYTES_IN_SECTOR) * SPI_NUM_BYTES_IN_SECTOR, p_flash_ss, p_flash_miso, p_flash_clk, p_flash_mosi); 
         
         }
      }
      break;
    }
  }
}