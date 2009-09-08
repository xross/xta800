/* Generated __mcmain() for core 3. */

out port p_lcd_dtm = 0x10000;

out port p_lcd_clk = 0x10200;

buffered out port:32 p_lcd_rgb = 0x200000;

clock clk_lcd = 0x106;

extern void __main_xm_2( streaming chanend, streaming chanend );

__mcmain( streaming chanend chan0_e2, streaming chanend chan2_e2 )
{
	__main_xm_2( chan0_e2, chan2_e2 );
}

