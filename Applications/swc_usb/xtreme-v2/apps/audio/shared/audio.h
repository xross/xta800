
void XUD_Endpoint1(chanend c, chanend c1, out port p_aud_rst,
                   buffered out port:32 AUD_BCLK, port AUD_SCLK, port AUD_SDIN, buffered out port:32 AUD_LRCLK,
                   buffered out port:32 AUD_DOUT1,  buffered out port:32 AUD_DOUT2,  buffered out port:32 AUD_DOUT3, buffered out port:32 AUD_DOUT4, buffered in port:32 AUD_DIN1,  buffered in port:32 AUD_DIN2,  buffered in port:32 AUD_DIN3,  buffered in port:32 AUD_DIN4, clock clk_aud_bit, out buffered port:32 spDifTx, chanend, chanend, in port masterClock, clock masterClockBlock, chanend sampleChannel, int dofft, chanend fft); 
