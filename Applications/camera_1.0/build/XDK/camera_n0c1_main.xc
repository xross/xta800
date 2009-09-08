/* Generated __mcmain() for core 1. */

out port p_cam_rst = 0x10000;

in port HREF_port = 0x10400;

in port VSYNC_port = 0x10700;

in port PCLK_port = 0x10500;

out port EXCLK_port = 0x10a00;

in port Y_UV_port = 0x100000;

port p_iic_sda = 0x10100;

port p_iic_scl = 0x10600;

clock clk = 0x306;

clock clk2 = 0x206;

extern void __main_xm_0( streaming chanend, streaming chanend );
extern void __main_xm_1( streaming chanend, streaming chanend );

__mcmain( streaming chanend chan0_e1, streaming chanend chan1_e1, streaming chanend chan2_e1, streaming chanend chan1_e2 )
{
	par
	{
		__main_xm_0( chan0_e1, chan1_e1 );
		__main_xm_1( chan2_e1, chan1_e2 );
	}
}

