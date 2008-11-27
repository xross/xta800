
// XDK resource defines

#ifndef _xdk_h_
#define _xdk_h_

#include <xs1_reva.h>

// All cores
#define PORT_LED0        XS1_PORT_1E
#define PORT_LED1        XS1_PORT_1F
#define PORT_LED2        XS1_PORT_1G

/*** Core 0 ***/

// Ports
#define PORT_FTDI_RXD    XS1_PORT_1I
#define PORT_FTDI_TXD    XS1_PORT_1H
#define PORT_KEYLED_0_3  XS1_PORT_4E
#define PORT_KEYLED_4    XS1_PORT_1K
#define PORT_KEY_0_3     XS1_PORT_4F
#define PORT_KEY_4       XS1_PORT_1J
#define PORT_ILLUM       XS1_PORT_8D
#define PORT_SPI_SS      XS1_PORT_1B
#define PORT_SPI_MISO    XS1_PORT_1A
#define PORT_SPI_SCLK    XS1_PORT_1C
#define PORT_SPI_MOSI    XS1_PORT_1D
#define PORT_SRAM_WE_N   XS1_PORT_1L
#define PORT_SRAM_ADDR   XS1_PORT_32A
#define PORT_SRAM_CTRL   XS1_PORT_4C
#define PORT_SRAM_DATA   XS1_PORT_8A
#define PORT_SDC_CMD     XS1_PORT_1P
#define PORT_SDC_CLK     XS1_PORT_1N
#define PORT_SDC_DAT     XS1_PORT_1O
#define PORT_SDC_RSV     XS1_PORT_1M

// Clockblocks
#define CLK_SRAM         XS1_CLKBLK_1
#define CLK_SDC          XS1_CLKBLK_2
#define CLK_FTDI_UART    XS1_CLKBLK_3


/*** Core 2 ***/

//Ports
#define PORT_MII_RXCLK   XS1_PORT_1H
#define PORT_MII_RXD     XS1_PORT_4A
#define PORT_MII_RXDV    XS1_PORT_1I
#define PORT_MII_TXCLK   XS1_PORT_1K
#define PORT_MII_TXEN    XS1_PORT_1L
#define PORT_MII_TXD     XS1_PORT_4B
#define PORT_MII_RST_N   XS1_PORT_1M
#define PORT_MII_MDIO    XS1_PORT_1O
#define PORT_MII_MDC     XS1_PORT_1P
#define PORT_MII_RXER    XS1_PORT_1J
#define PORT_MII_TXER    XS1_PORT_1N

#define PORT_RGB_SCLK    XS1_PORT_1A
#define PORT_RGB_SIN     XS1_PORT_1B
#define PORT_RGB_CSEL    XS1_PORT_4F
#define PORT_RGB_LATCH   XS1_PORT_1C
#define PORT_RGB_BLANK   XS1_PORT_1D

//Clockblocks
#define CLK_MII_RX       XS1_CLKBLK_1
#define CLK_MII_TX       XS1_CLKBLK_2
#define CLK_SMI          XS1_CLKBLK_3


/*** Core 3 ***/

// Ports
#define PORT_LCD_DTMG    XS1_PORT_1B
#define PORT_LCD_DCLK    XS1_PORT_1A
#define PORT_LCD_HSYNC   XS1_PORT_4F
#define PORT_LCD_RGB     XS1_PORT_32A

#define PORT_TOU_PEN     XS1_PORT_4C
#define PORT_TOU_DOUT    XS1_PORT_1O
#define PORT_TOU_CS      XS1_PORT_4E
#define PORT_TOU_DCLK    XS1_PORT_4D
#define PORT_TOU_DIN     XS1_PORT_1P

#define PORT_AUD_BCLK	 XS1_PORT_1C
#define PORT_AUD_LRCIN   XS1_PORT_1D
#define PORT_AUD_LRCOUT	 XS1_PORT_1H
#define PORT_AUD_DIN	 XS1_PORT_1I
#define PORT_AUD_DOUT	 XS1_PORT_1J
#define PORT_AUD_SCLK    XS1_PORT_1K
#define PORT_AUD_SDIN	 XS1_PORT_1L

// Clockblocks
#define CLK_LCD          XS1_CLKBLK_1
#define CLK_TOU          XS1_CLKBLK_2
#define CLK_AUD		 XS1_CLKBLK_3

#endif /* _xdk_h_ */
