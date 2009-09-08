#ifndef HAVE_PLATFORM_H
#define HAVE_PLATFORM_H

#include <xs1.h>

/*
 * Platform description header file.
 * Automatically generated from "/home/andrew-local/software/sandbox/Installs/Linux/configs/XDK.xn".
 */

#if !defined(__ASSEMBLER__)
/* Core array declaration. */
extern core stdcore[4];

#endif
#if !defined(__ASSEMBLER__)
#define PORT_SPI_MISO on stdcore[0]: XS1_PORT_1A
#define PORT_SPI_SS on stdcore[0]: XS1_PORT_1B
#define PORT_SPI_CLK on stdcore[0]: XS1_PORT_1C
#define PORT_SPI_MOSI on stdcore[0]: XS1_PORT_1D
#define PORT_LED_0_0 on stdcore[0]: XS1_PORT_1E
#define PORT_LED_0_1 on stdcore[0]: XS1_PORT_1F
#define PORT_LED_0_2 on stdcore[0]: XS1_PORT_1G
#define PORT_UART_TXD on stdcore[0]: XS1_PORT_1H
#define PORT_UART_RXD on stdcore[0]: XS1_PORT_1I
#define PORT_BUTTON_4 on stdcore[0]: XS1_PORT_1J
#define PORT_BUTTONLED_4 on stdcore[0]: XS1_PORT_1K
#define PORT_SRAM_WE_N on stdcore[0]: XS1_PORT_1L
#define PORT_SDC_RSV on stdcore[0]: XS1_PORT_1M
#define PORT_SDC_CLK on stdcore[0]: XS1_PORT_1N
#define PORT_SDC_DATA on stdcore[0]: XS1_PORT_1O
#define PORT_SDC_CMD on stdcore[0]: XS1_PORT_1P
#define PORT_SRAM_CTRL on stdcore[0]: XS1_PORT_4C
#define PORT_BUTTONLED_0_3 on stdcore[0]: XS1_PORT_4E
#define PORT_BUTTON_0_3 on stdcore[0]: XS1_PORT_4F
#define PORT_SRAM_DATA on stdcore[0]: XS1_PORT_8A
#define PORT_ILLUM on stdcore[0]: XS1_PORT_8D
#define PORT_SRAM_ADDR on stdcore[0]: XS1_PORT_32A
#define PORT_LED_1_0 on stdcore[1]: XS1_PORT_1E
#define PORT_LED_1_1 on stdcore[1]: XS1_PORT_1F
#define PORT_LED_1_2 on stdcore[1]: XS1_PORT_1G
#define PORT_RGB_SCLK on stdcore[2]: XS1_PORT_1A
#define PORT_RGB_SIN on stdcore[2]: XS1_PORT_1B
#define PORT_RGB_LATCH_N on stdcore[2]: XS1_PORT_1C
#define PORT_RGB_BLANK on stdcore[2]: XS1_PORT_1D
#define PORT_LED_2_0 on stdcore[2]: XS1_PORT_1E
#define PORT_LED_2_1 on stdcore[2]: XS1_PORT_1F
#define PORT_LED_2_2 on stdcore[2]: XS1_PORT_1G
#define PORT_ETH_RXCLK on stdcore[2]: XS1_PORT_1H
#define PORT_ETH_RXDV on stdcore[2]: XS1_PORT_1I
#define PORT_ETH_RXER on stdcore[2]: XS1_PORT_1J
#define PORT_ETH_TXCLK on stdcore[2]: XS1_PORT_1K
#define PORT_ETH_TXEN on stdcore[2]: XS1_PORT_1L
#define PORT_ETH_RST_N on stdcore[2]: XS1_PORT_1M
#define PORT_ETH_TXER on stdcore[2]: XS1_PORT_1N
#define PORT_ETH_MDIO on stdcore[2]: XS1_PORT_1O
#define PORT_ETH_MDC on stdcore[2]: XS1_PORT_1P
#define PORT_ETH_RXD on stdcore[2]: XS1_PORT_4A
#define PORT_ETH_TXD on stdcore[2]: XS1_PORT_4B
#define PORT_RGB_CSEL on stdcore[2]: XS1_PORT_4F
#define PORT_LCD_DCLK on stdcore[3]: XS1_PORT_1A
#define PORT_LCD_DTMG on stdcore[3]: XS1_PORT_1B
#define PORT_AUDIO_BCLK on stdcore[3]: XS1_PORT_1C
#define PORT_AUDIO_LRCIN on stdcore[3]: XS1_PORT_1D
#define PORT_LED_3_0 on stdcore[3]: XS1_PORT_1E
#define PORT_LED_3_1 on stdcore[3]: XS1_PORT_1F
#define PORT_LED_3_2 on stdcore[3]: XS1_PORT_1G
#define PORT_AUDIO_LRCOUT on stdcore[3]: XS1_PORT_1H
#define PORT_AUDIO_DIN on stdcore[3]: XS1_PORT_1I
#define PORT_AUDIO_DOUT on stdcore[3]: XS1_PORT_1J
#define PORT_AUDIO_SCLK on stdcore[3]: XS1_PORT_1K
#define PORT_AUDIO_SDIN on stdcore[3]: XS1_PORT_1L
#define PORT_TOUCH_DOUT on stdcore[3]: XS1_PORT_1O
#define PORT_TOUCH_DIN on stdcore[3]: XS1_PORT_1P
#define PORT_TOUCH_PEN on stdcore[3]: XS1_PORT_4C
#define PORT_TOUCH_DCLK on stdcore[3]: XS1_PORT_4D
#define PORT_TOUCH_CS on stdcore[3]: XS1_PORT_4E
#define PORT_LCD_HSYNC on stdcore[3]: XS1_PORT_4F
#define PORT_LCD_RGB on stdcore[3]: XS1_PORT_32A
#else
#define PORT_SPI_MISO XS1_PORT_1A
#define PORT_SPI_SS XS1_PORT_1B
#define PORT_SPI_CLK XS1_PORT_1C
#define PORT_SPI_MOSI XS1_PORT_1D
#define PORT_LED_0_0 XS1_PORT_1E
#define PORT_LED_0_1 XS1_PORT_1F
#define PORT_LED_0_2 XS1_PORT_1G
#define PORT_UART_TXD XS1_PORT_1H
#define PORT_UART_RXD XS1_PORT_1I
#define PORT_BUTTON_4 XS1_PORT_1J
#define PORT_BUTTONLED_4 XS1_PORT_1K
#define PORT_SRAM_WE_N XS1_PORT_1L
#define PORT_SDC_RSV XS1_PORT_1M
#define PORT_SDC_CLK XS1_PORT_1N
#define PORT_SDC_DATA XS1_PORT_1O
#define PORT_SDC_CMD XS1_PORT_1P
#define PORT_SRAM_CTRL XS1_PORT_4C
#define PORT_BUTTONLED_0_3 XS1_PORT_4E
#define PORT_BUTTON_0_3 XS1_PORT_4F
#define PORT_SRAM_DATA XS1_PORT_8A
#define PORT_ILLUM XS1_PORT_8D
#define PORT_SRAM_ADDR XS1_PORT_32A
#define PORT_LED_1_0 XS1_PORT_1E
#define PORT_LED_1_1 XS1_PORT_1F
#define PORT_LED_1_2 XS1_PORT_1G
#define PORT_RGB_SCLK XS1_PORT_1A
#define PORT_RGB_SIN XS1_PORT_1B
#define PORT_RGB_LATCH_N XS1_PORT_1C
#define PORT_RGB_BLANK XS1_PORT_1D
#define PORT_LED_2_0 XS1_PORT_1E
#define PORT_LED_2_1 XS1_PORT_1F
#define PORT_LED_2_2 XS1_PORT_1G
#define PORT_ETH_RXCLK XS1_PORT_1H
#define PORT_ETH_RXDV XS1_PORT_1I
#define PORT_ETH_RXER XS1_PORT_1J
#define PORT_ETH_TXCLK XS1_PORT_1K
#define PORT_ETH_TXEN XS1_PORT_1L
#define PORT_ETH_RST_N XS1_PORT_1M
#define PORT_ETH_TXER XS1_PORT_1N
#define PORT_ETH_MDIO XS1_PORT_1O
#define PORT_ETH_MDC XS1_PORT_1P
#define PORT_ETH_RXD XS1_PORT_4A
#define PORT_ETH_TXD XS1_PORT_4B
#define PORT_RGB_CSEL XS1_PORT_4F
#define PORT_LCD_DCLK XS1_PORT_1A
#define PORT_LCD_DTMG XS1_PORT_1B
#define PORT_AUDIO_BCLK XS1_PORT_1C
#define PORT_AUDIO_LRCIN XS1_PORT_1D
#define PORT_LED_3_0 XS1_PORT_1E
#define PORT_LED_3_1 XS1_PORT_1F
#define PORT_LED_3_2 XS1_PORT_1G
#define PORT_AUDIO_LRCOUT XS1_PORT_1H
#define PORT_AUDIO_DIN XS1_PORT_1I
#define PORT_AUDIO_DOUT XS1_PORT_1J
#define PORT_AUDIO_SCLK XS1_PORT_1K
#define PORT_AUDIO_SDIN XS1_PORT_1L
#define PORT_TOUCH_DOUT XS1_PORT_1O
#define PORT_TOUCH_DIN XS1_PORT_1P
#define PORT_TOUCH_PEN XS1_PORT_4C
#define PORT_TOUCH_DCLK XS1_PORT_4D
#define PORT_TOUCH_CS XS1_PORT_4E
#define PORT_LCD_HSYNC XS1_PORT_4F
#define PORT_LCD_RGB XS1_PORT_32A
#endif


#endif /* HAVE_PLATFORM_H */

