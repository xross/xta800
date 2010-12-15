/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */

#ifndef SPIFLASH_H_
#define SPIFLASH_H_

#define SPI_FLASH_READ          0
#define SPI_FLASH_WRITEFIRMWARE 1
#define SPI_FLASH_NEWFIRMWARE   2
#define SPI_FLASH_NEWGAMMA      3
#define SPI_FLASH_READGAMMA     4

#ifdef __XC__
void spiFlash(chanend cSpiFlash,
    buffered in port:8 p_flash_miso, out port p_flash_ss, buffered out port:32 p_flash_clk, buffered out port:8 p_flash_mosi, clock b_flash_clk, clock b_flash_data);
#endif

#endif /*SPIFLASH_H_*/
