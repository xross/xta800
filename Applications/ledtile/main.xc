/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */

#include <xs1.h>
#include <platform.h>
#include "ledbuffer.h"
#include "leddriver.h"
#include "led.h"
#include "miimain.h"
#include "ledref.h"
#include "ethSwitch.h"
#include "ethServer.h"
#include "pktbuffer.h"
#include "watchdog.h"
#include "flashManager.h"
#include "ledprocess.h"

#define WDOG_CORE 1
#define FLASH_CORE 0


// Ethernet Ports and Clock Blocks
// -----------------------
#ifdef SIMULATION
on stdcore[ETHERNET_CORE]: port p_mii_txcsn             = PORT_MII_CS_N;
#endif
on stdcore[ETHERNET_CORE]: clock clk_mii_ref                = XS1_CLKBLK_REF;
on stdcore[ETHERNET_CORE]: clock clk_mii_rx_0               = XS1_CLKBLK_1;
on stdcore[ETHERNET_CORE]: clock clk_mii_tx_0               = XS1_CLKBLK_2;
on stdcore[ETHERNET_CORE]: in port p_mii_rxclk_0            = PORT_MII_RXCLK_0;
on stdcore[ETHERNET_CORE]: buffered in port:32 p_mii_rxd_0  = PORT_MII_RXD_0;
on stdcore[ETHERNET_CORE]: in port p_mii_rxdv_0             = PORT_MII_RXDV_0;
on stdcore[ETHERNET_CORE]: in port p_mii_rxer_0             = PORT_MII_RXER_0;
on stdcore[ETHERNET_CORE]: in port p_mii_txclk_0            = PORT_MII_TXCLK_0;
on stdcore[ETHERNET_CORE]: buffered out port:32 p_mii_txd_0 = PORT_MII_TXD_0;
on stdcore[ETHERNET_CORE]: out port p_mii_txen_0            = PORT_MII_TXEN_0;
on stdcore[ETHERNET_CORE]: clock clk_smi                    = XS1_CLKBLK_3;
on stdcore[ETHERNET_CORE]: port p_smi_mdio_0                = PORT_MII_MDIO_0;
on stdcore[ETHERNET_CORE]: out port p_smi_mdc_0             = PORT_MII_MDC_0;
on stdcore[ETHERNET_CORE]: out port p_mii_resetn            = PORT_MII_RST_N;

on stdcore[ETHERNET_CORE]: clock clk_mii_rx_1               = XS1_CLKBLK_4;
on stdcore[ETHERNET_CORE]: clock clk_mii_tx_1               = XS1_CLKBLK_5;
on stdcore[ETHERNET_CORE]: in port p_mii_rxclk_1            = PORT_MII_RXCLK_1;
on stdcore[ETHERNET_CORE]: buffered in port:32 p_mii_rxd_1  = PORT_MII_RXD_1;
on stdcore[ETHERNET_CORE]: in port p_mii_rxdv_1             = PORT_MII_RXDV_1;
on stdcore[ETHERNET_CORE]: in port p_mii_rxer_1             = PORT_MII_RXER_1;
on stdcore[ETHERNET_CORE]: in port p_mii_txclk_1            = PORT_MII_TXCLK_1;
on stdcore[ETHERNET_CORE]: buffered out port:32 p_mii_txd_1 = PORT_MII_TXD_1;
on stdcore[ETHERNET_CORE]: out port p_mii_txen_1            = PORT_MII_TXEN_1;
on stdcore[ETHERNET_CORE]: port p_smi_mdio_1                = PORT_MII_MDIO_1;
on stdcore[ETHERNET_CORE]: out port p_smi_mdc_1               = PORT_MII_MDC_1;

// LED Tile Ports
// -----------------------
on stdcore[LED_CORE]: buffered out port:32 p_led_out_r0 = XS1_PORT_1F;
on stdcore[LED_CORE]: buffered out port:32 p_led_out_g0 = XS1_PORT_1G;
on stdcore[LED_CORE]: buffered out port:32 p_led_out_b0 = XS1_PORT_1H;
on stdcore[LED_CORE]: buffered out port:32 p_led_out_r1 = XS1_PORT_1I;
on stdcore[LED_CORE]: buffered out port:32 p_led_out_g1 = XS1_PORT_1J;
on stdcore[LED_CORE]: buffered out port:32 p_led_out_b1 = XS1_PORT_1K;
on stdcore[LED_CORE]: out port p_led_out_addr = XS1_PORT_4A;
on stdcore[LED_CORE]: buffered out port:32 p_led_out_clk = XS1_PORT_1L;
on stdcore[LED_CORE]: buffered out port:32 p_led_out_ltch = XS1_PORT_1M;
on stdcore[LED_CORE]: out port p_led_out_oe = XS1_PORT_1N;
on stdcore[LED_CORE]: clock b_clk = XS1_CLKBLK_2;
on stdcore[LED_CORE]: clock b_data = XS1_CLKBLK_3;
on stdcore[LED_CORE]: clock b_gsclk = XS1_CLKBLK_4;
on stdcore[LED_CORE]: clock b_ref = XS1_CLKBLK_REF;
on stdcore[FLASH_CORE]: clock b_flash_clk = XS1_CLKBLK_1;
on stdcore[FLASH_CORE]: clock b_flash_data = XS1_CLKBLK_5;
on stdcore[FLASH_CORE]: buffered in port:8 p_flash_miso  = XS1_PORT_1A;
on stdcore[FLASH_CORE]: out port p_flash_ss   = XS1_PORT_1B;
on stdcore[FLASH_CORE]: buffered out port:32 p_flash_clk  = XS1_PORT_1C;
on stdcore[FLASH_CORE]: buffered out port:8 p_flash_mosi = XS1_PORT_1D;

int main(void)
{
  chan c_led_data_in, c_led_data_out;
  chan c_mii_client_0, c_mii_tx_0, c_mii_client_1, c_mii_tx_1;
  chan c_local_tx, c_local_rx_in, c_local_rx_out;
  chan c_led_cmds_in, c_led_cmds_out, c_led_processed;
  chan cSpiFlash;
  chan cWdogSwitch, cWdogServ, cWdogLed;
  
  par
  {
    // Threads constrained by I/O or latency requirements
    on stdcore[ETHERNET_CORE]: miimain(c_mii_client_0, c_mii_client_1, 
        c_mii_tx_0, c_mii_tx_1,
                                       clk_mii_rx_0, p_mii_rxclk_0, p_mii_rxd_0, p_mii_rxdv_0, p_mii_rxer_0,
                                       clk_mii_tx_0, p_mii_txclk_0, p_mii_txd_0, p_mii_txen_0,
                                       clk_mii_rx_1, p_mii_rxclk_1, p_mii_rxd_1, p_mii_rxdv_1, p_mii_rxer_1,
                                       clk_mii_tx_1, p_mii_txclk_1, p_mii_txd_1, p_mii_txen_1,
                                       clk_mii_ref, clk_smi, p_smi_mdc_0, p_smi_mdc_1, p_smi_mdio_0, p_smi_mdio_1, p_mii_resetn
    );
    on stdcore[ETHERNET_CORE]: ethSwitch(c_mii_client_0, c_mii_client_1, c_local_tx,
        c_mii_tx_0, c_mii_tx_1, c_local_rx_in,
                                         cWdogSwitch);
    on stdcore[LED_CORE]: leddrive(c_led_processed,
        p_led_out_r0, p_led_out_g0, p_led_out_b0, p_led_out_r1, p_led_out_g1, p_led_out_b1,
        p_led_out_addr, p_led_out_clk , p_led_out_ltch, p_led_out_oe ,
        b_clk, b_data, b_gsclk, b_ref);

    on stdcore[FLASH_CORE]: spiFlash(cSpiFlash, p_flash_miso, p_flash_ss, p_flash_clk, p_flash_mosi, b_flash_clk, b_flash_data);
    
    // Unconstrained threads
    on stdcore[WDOG_CORE]: watchDog(cWdogSwitch, cWdogServ, cWdogLed);
    on stdcore[3]: pktbuffer(c_local_rx_in, c_local_rx_out);
    on stdcore[3]: ethServer(c_local_rx_out, c_local_tx, c_led_data_in, c_led_cmds_in, cSpiFlash, cWdogServ);
    on stdcore[3]: pktbuffer(c_led_cmds_in, c_led_cmds_out);
    on stdcore[3]: ledbuffer(c_led_data_in, c_led_data_out);
    on stdcore[3]: ledreprocess(c_led_data_out, c_led_cmds_out, c_led_processed, cWdogLed);
    
  }
  return 0;
}
