/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */

#include <xs1.h>
#include <xclib.h>
#include <print.h>
#include <syscall.h>
#include "flashPhy.h"

#define CLOCKDATA 0xAAAA


#define clockbyte(a, b) { p_flash_mosi <: (unsigned)a; p_flash_clk :16 <:  CLOCKDATA; p_flash_miso :> b;}
#define clockword(a, b) { p_flash_mosi <: >> a; p_flash_clk :16 <:  CLOCKDATA; p_flash_miso :> >> b; \
                          p_flash_mosi <: >> a; p_flash_clk :16 <:  CLOCKDATA; p_flash_miso :> >> b; \
                          p_flash_mosi <: >> a; p_flash_clk :16 <:  CLOCKDATA; p_flash_miso :> >> b; \
                          p_flash_mosi <: >> a; p_flash_clk :16 <:  CLOCKDATA; p_flash_miso :> >> b; }

#define clock3byte(a, b) { p_flash_mosi <: >> a; p_flash_clk :16 <:  CLOCKDATA; p_flash_miso :> >> b; \
                          p_flash_mosi <: >> a; p_flash_clk :16 <:  CLOCKDATA; p_flash_miso :> >> b; \
                          p_flash_mosi <: >> a; p_flash_clk :16 <:  CLOCKDATA; p_flash_miso :> >> b; b >>= 8;}


void spi_init(out port p_flash_ss, buffered in port:8 p_flash_miso
    , buffered out port:32 p_flash_clk, buffered out port:8 p_flash_mosi
    , clock b_clk, clock b_data)
{ 

  
  p_flash_clk :1 <: 1;
  stop_clock(b_clk);
  stop_clock(b_data);
  set_clock_ref(b_clk);
  
  set_clock_div(b_clk, 3);
  set_port_clock(p_flash_clk, b_clk);
  set_clock_src(b_data, p_flash_clk);
  
  set_port_clock(p_flash_miso, b_data);
  set_port_clock(p_flash_mosi, b_data);
  
  clearbuf(p_flash_miso);
  clearbuf(p_flash_mosi);
  clearbuf(p_flash_clk);

  start_clock(b_data);
  start_clock(b_clk);
  
  // Make sure we start with write disabled
  spi_write_disable(p_flash_ss, p_flash_miso, p_flash_clk, p_flash_mosi);
}

void spi_end(out port p_flash_ss, buffered in port:8 p_flash_miso,
    buffered out port:32 p_flash_clk, buffered out port:8 p_flash_mosi)
{
  spi_write_disable(p_flash_ss, p_flash_miso, p_flash_clk, p_flash_mosi);      
}

// Read device ID
void spi_device_id(unsigned &device_id_ref, out port p_flash_ss, buffered in port:8 p_flash_miso,
    buffered out port:32 p_flash_clk, buffered out port:8 p_flash_mosi)
{
  unsigned device_id;
  unsigned null;
  p_flash_ss <: 0;
  clockbyte(byterev(bitrev(SPI_RDID)), null);

  null = 0;
  clockword(null, device_id);
  device_id_ref = bitrev(device_id);
  
  p_flash_ss <: 1;

}

// Read status register
void spi_status_reg(unsigned &status_reg_ref, out port p_flash_ss, buffered in port:8 p_flash_miso,
    buffered out port:32 p_flash_clk, buffered out port:8 p_flash_mosi)
{
  unsigned status_reg;
  unsigned char null;
  p_flash_ss <: 0;
  clockbyte(byterev(bitrev(SPI_RDSR)), null);
  clockbyte(0, status_reg);
  
  p_flash_ss <: 1;
  
  status_reg_ref = byterev(bitrev(status_reg));
  
}

// Read bytes from a given page
// Page size is SPI_BYTES_IN_PAGE
// Reading and writing LSb first because LSb first is XCore ports native order
// LSb first is not standard
void spi_read(int ptr, unsigned char buffer[], int nbytes, out port p_flash_ss, buffered in port:8 p_flash_miso,
    buffered out port:32 p_flash_clk, buffered out port:8 p_flash_mosi)
{
  unsigned null;
  unsigned addr = bitrev(ptr) >> 8;
  p_flash_ss <: 0;
  clockbyte(byterev(bitrev(SPI_READ_FAST)), null);
  clock3byte(addr, null);
  clockbyte(0, null);
  for (int i = 0; i < nbytes; i++)
    clockbyte(0, buffer[i]);
  
  p_flash_ss <: 1;
}

void spi_wordread(int wordptr, unsigned buffer[], int nwords,  out port p_flash_ss, buffered in port:8 p_flash_miso,
    buffered out port:32 p_flash_clk, buffered out port:8 p_flash_mosi)
{
  unsigned null;
  unsigned addr = bitrev(wordptr<<2) >> 8;
  p_flash_ss <: 0;
  sync(p_flash_ss);
  clockbyte(byterev(bitrev(SPI_READ_FAST)), null);
  clock3byte(addr, null);
  clockbyte(0, null);
  null = 0;
  for (int i = 0; i < nwords; i++)
  {
    unsigned val;
    clockword(null, val);
    buffer[i] = val;
  }
  p_flash_ss <: 1;
  sync(p_flash_ss);
}

// Write bytes to a given page
// Page size is SPI_BYTES_IN_PAGE
// Note: Sector must be made unprotected first
// Note: Page must be erased wfirst
// Note: Write enable is handled inside
// Reading and writing LSb first because LSb first is XCore ports native order
// LSb first is not standard
void spi_write(int baddr, const unsigned char buffer[], int nbytes, out port p_flash_ss, buffered in port:8 p_flash_miso,
    buffered out port:32 p_flash_clk, buffered out port:8 p_flash_mosi)
{
  unsigned null;
  unsigned addr = bitrev(baddr) >> 8;
  spi_write_enable(p_flash_ss, p_flash_miso, p_flash_clk, p_flash_mosi);

  p_flash_ss <: 0;
  clockbyte(byterev(bitrev(SPI_PP)), null);
  clock3byte(addr, null);
  for (int i = 0; i < nbytes; i++)
    clockbyte(buffer[i], null);
  
  p_flash_ss <: 1;
  
  while (spi_poll_progress(p_flash_ss, p_flash_miso, p_flash_clk, p_flash_mosi));
}

void spi_wordwrite(int waddr, const unsigned buffer[], int nwords, out port p_flash_ss, buffered in port:8 p_flash_miso,
    buffered out port:32 p_flash_clk, buffered out port:8 p_flash_mosi)
{
  unsigned null;
  unsigned addr = bitrev(waddr << 2) >> 8;
  spi_write_enable(p_flash_ss, p_flash_miso, p_flash_clk, p_flash_mosi);

  p_flash_ss <: 0;
  clockbyte(byterev(bitrev(SPI_PP)), null);
  clock3byte(addr, null);
  for (int i = 0; i < nwords; i++)
  {
    int bufdata = buffer[i];
    clockword(bufdata, null);
  }
  
  p_flash_ss <: 1;
  
  while (spi_poll_progress(p_flash_ss, p_flash_miso, p_flash_clk, p_flash_mosi));
}

// Write enable
// Normally don't need to be explicitly called
void spi_write_enable(out port p_flash_ss, buffered in port:8 p_flash_miso,
    buffered out port:32 p_flash_clk, buffered out port:8 p_flash_mosi)
{
  unsigned char null;
  p_flash_ss <: 0;
  clockbyte(byterev(bitrev(SPI_WREN)), null);
  
  p_flash_ss <: 1;
}

// Write disable
// Normally don't need to be explicitly called
void spi_write_disable(out port p_flash_ss, buffered in port:8 p_flash_miso,
    buffered out port:32 p_flash_clk, buffered out port:8 p_flash_mosi)
{
  unsigned null;
  p_flash_ss <: 0;
  clockbyte(byterev(bitrev(SPI_WRDI)), null);
  
  p_flash_ss <: 1;
}

// Unprotect sector at given address
// Takes byte address not sector number!
// This is because some devices have uneven sector protection layout
void spi_unprotect(unsigned addr, out port p_flash_ss, buffered in port:8 p_flash_miso,
    buffered out port:32 p_flash_clk, buffered out port:8 p_flash_mosi)
{
  unsigned null;
  spi_write_enable(p_flash_ss, p_flash_miso, p_flash_clk, p_flash_mosi);
  addr = bitrev(addr) >> 8;
  p_flash_ss <: 0;
  clockbyte(byterev(bitrev(SPI_SU)), null);
  clock3byte(addr, null);
  p_flash_ss <: 1;
  spi_write_disable(p_flash_ss, p_flash_miso, p_flash_clk, p_flash_mosi);
}

// Protect sector at given address
// Takes byte address not sector number!
// This is because some devices have uneven sector protection layout
void spi_protect(unsigned addr, out port p_flash_ss, buffered in port:8 p_flash_miso,
    buffered out port:32 p_flash_clk, buffered out port:8 p_flash_mosi)
{
  unsigned null;
  spi_write_enable(p_flash_ss, p_flash_miso, p_flash_clk, p_flash_mosi);
  addr = bitrev(addr) >> 8;
  p_flash_ss <: 0;
  clockbyte(byterev(bitrev(SPI_SP)), null);
  clock3byte(addr, null);
  p_flash_ss <: 1;
  spi_write_disable(p_flash_ss, p_flash_miso, p_flash_clk, p_flash_mosi);
}

// Erase given sector
// Sector size is SPI_NUM_BYTES_IN_SECTOR
// Note: Sector must be made unprotected first
void spi_erase(int sector, out port p_flash_ss, buffered in port:8 p_flash_miso,
    buffered out port:32 p_flash_clk, buffered out port:8 p_flash_mosi)
{
  unsigned null;
  unsigned addr = bitrev(SPI_NUM_BYTES_IN_SECTOR * sector) >> 8;
  spi_write_enable(p_flash_ss, p_flash_miso, p_flash_clk, p_flash_mosi);
  
  p_flash_ss <: 0;  
  clockbyte(byterev(bitrev(SPI_SE)), null);
  clock3byte(addr, null);
  p_flash_ss <: 1;
}

// Erase given 4kB block
// Note: Sector must be made unprotected first
void spi_blockerase(int baddr, out port p_flash_ss, buffered in port:8 p_flash_miso,
    buffered out port:32 p_flash_clk, buffered out port:8 p_flash_mosi)
{
  unsigned null;
  unsigned addr = bitrev(baddr) >> 8;
  spi_write_enable(p_flash_ss, p_flash_miso, p_flash_clk, p_flash_mosi);
  
  p_flash_ss <: 0;  
  clockbyte(byterev(bitrev(SPI_BE)), null);
  clock3byte(addr, null);
  p_flash_ss <: 1;
}


// Erase/write progress
// Returns 1 if device is still busy with last erase/write
// Will try 3 times with 1us delay between each try
// Typical use: "while(spi_poll_progress())"
int spi_poll_progress(out port p_flash_ss, buffered in port:8 p_flash_miso,
    buffered out port:32 p_flash_clk, buffered out port:8 p_flash_mosi)
{
  timer tmr;
  unsigned t;
  int busy = 1;
  for (int timeout = 3; busy && timeout >= 0; timeout--)
  {
    unsigned status_reg;
    spi_status_reg(status_reg,p_flash_ss,  p_flash_miso, p_flash_clk, p_flash_mosi);
    tmr :> t;
    busy = status_reg & SPI_WIP_BIT_MASK;
    if (busy)
      tmr when timerafter(t + 100) :> t;
  }
  return busy;
}

// Unprotect all memory
// Some devices have uneven sector protection layout, so we have #define list
static const unsigned sectors[] =
{
  0x00000, 0x10000, 0x20000, 0x30000, 0x40000, 0x50000,
  0x60000, 0x70000, 0x78000, 0x7A000, 0x7C000
};

void spi_unprotect_all(out port p_flash_ss, buffered in port:8 p_flash_miso,
    buffered out port:32 p_flash_clk, buffered out port:8 p_flash_mosi)
{
  const int nsectors = sizeof(sectors) / sizeof(unsigned);
  for (int i = 0; i < nsectors; i++)
  {
    spi_unprotect(sectors[i], p_flash_ss, p_flash_miso, p_flash_clk, p_flash_mosi);
  }
}

// Protect all memory
// Some devices have uneven sector protection layout, so we have a list.
void spi_protect_all(out port p_flash_ss, buffered in port:8 p_flash_miso,
    buffered out port:32 p_flash_clk, buffered out port:8 p_flash_mosi)
{
  const int nsectors = sizeof(sectors) / sizeof(unsigned);
  for (int i = 0; i < nsectors; i++)
  {
    spi_protect(sectors[i], p_flash_ss, p_flash_miso, p_flash_clk, p_flash_mosi);
  }
}

void spi_erase_all(out port p_flash_ss, buffered in port:8 p_flash_miso,
    buffered out port:32 p_flash_clk, buffered out port:8 p_flash_mosi)
{
  // Erase whole chip
  spi_unprotect_all(p_flash_ss, p_flash_miso, p_flash_clk, p_flash_mosi);
  for (int i = 0; i < SPI_NUM_SECTORS; i++)
  {
    spi_erase(i, p_flash_ss, p_flash_miso, p_flash_clk, p_flash_mosi);
    while (spi_poll_progress(p_flash_ss, p_flash_miso, p_flash_clk, p_flash_mosi));
  }
  spi_protect_all(p_flash_ss, p_flash_miso, p_flash_clk, p_flash_mosi);
}

