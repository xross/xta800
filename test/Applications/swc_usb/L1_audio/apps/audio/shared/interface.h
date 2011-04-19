void playEndpoint(chanend c_ep1_out, chanend c_usbPlayData, chanend c_time, chanend c_errorCounters);

void recordEndpoint(chanend c_ep1_in, chanend c_usbRecordData);

void feedbackEndpoint(unsigned epNum, chanend c_ep, chanend c_time);

void audioPhysical(
    out port p_aud_rst, buffered out port:32 AUD_BCLK, port AUD_SCLK, port AUD_SDIN, buffered out port:32 AUD_LRCLK,
    in port p_master_clock,
    buffered out port:32 AUD_DOUT1, buffered out port:32 AUD_DOUT2, buffered out port:32 AUD_DOUT3, buffered out port:32 AUD_DOUT4,
    buffered in port:32 AUD_DIN1,  buffered in port:32 AUD_DIN2,  buffered in port:32 AUD_DIN3,  buffered in port:32 AUD_DIN4,
    out buffered port:32 p_adatTx, out buffered port:32 p_spDifTx, 
    clock bitClockBlock,  clock masterClockBlock,
    chanend c_adatPhysical, chanend c_spDifPhysical, chanend c_i2s_In, chanend c_i2s_Out);

void mixer(chanend c_clock_in, chanend c_usb_in, chanend c_usb_buffer_out,
           chanend c_adat_out, chanend c_spdif_out, chanend c_i2s_in, chanend c_i2s_out);

void spdif_receiver(clock clk, buffered in port:4 p, chanend);
void adat_receiver(clock clk, buffered in port:32 p, chanend spDifReceivedData);

void adat_transmitter(chanend c_adatOutputData, chanend c_adatPhysicalData);
void spdif_transmitter(chanend c_spdifOutputData, chanend c_spdifPhysicalData);






void physical(buffered out port:32 ADAT_TX, buffered out port:32 SPDIF_TX, out port p_aud_rst,
              buffered out port:32 BCLK, port SCLK, port SDIN, buffered out port:32 LRCLK,
              buffered out port:32 DOUT1,  buffered out port:32 DOUT2,  buffered out port:32 DOUT3, buffered out port:32 DOUT4,  buffered in port:32 DIN1,  buffered in port:32 DIN2,  buffered in port:32 DIN3,  buffered in port:32 DIN4, clock clk_aud_bit, chanend c_adat, chanend c_spdif, chanend c_i2s_in, chanend c_i2s_out);




void deliver(buffered out port:32 ADAT_TX, buffered out port:32 LRCLK, buffered out port:32 BCLK, buffered out port:32 SPDIF_TX,
             chanend ADATin, chanend SPDIFin, chanend I2SIn, chanend I2sOut,
             buffered out port:32 DOUT1, buffered out port:32 DOUT2, buffered out port:32 DOUT3, buffered out port:32 DOUT4, 
             buffered in port:32 DIN1, buffered in port:32 DIN2, buffered in port:32 DIN3, buffered in port:32 DIN4,
             int curSampFreq);
