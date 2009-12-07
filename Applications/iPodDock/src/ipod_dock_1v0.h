#ifndef IPOD_DOCK_1V0_H_
#define IPOD_DOCK_1V0_H_

#define PORT_IR                 XS1_PORT_1J
#define PORT_SPDIF              XS1_PORT_1I
#define PORT_IIC_CLK            XS1_PORT_1C
#define PORT_IIC_DATA           XS1_PORT_1D
#define PORT_DAC_DATA           XS1_PORT_1M
#define PORT_DAC_LRCLK          XS1_PORT_1K
#define PORT_DAC_BCLK           XS1_PORT_1A
#define PORT_DAC_MCLK           XS1_PORT_1L

#define PORT_32B_OSC_BITSHIFT             2
#define PORT_32B_USBRESET_BITSHIFT        3
#define PORT_32B_LED_BITSHIFT             4
#define PORT_32B_COPRORESET_BITSHIFT      6

#define LR_CLK_1B_PORT

#define CS4351
#define DAC_IIC_ADDRESS                0x4C

#define UIFM_CLKBLK               XS1_CLKBLK_5
#define AUDIO_CLKBLK_0            XS1_CLKBLK_3
#define AUDIO_CLKBLK_1            XS1_CLKBLK_4

#endif /*IPOD_DOCK_1V0_H_*/
