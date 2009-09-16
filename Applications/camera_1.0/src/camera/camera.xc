/**
 * Module:  camera
 * Version: 1.0
 * Build:   N/A
 * File:    src/camera/camera.xc
 *
 * The copyrights, all other intellectual and industrial 
 * property rights are retained by XMOS and/or its licensors. 
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2009
 **/                                   

#include "camera/camera.h"
#include "camera/sccb.h"
 

#define COLOUR_GBR422 1

void initCamInterface(clock clk, clock clk2, in port Y_UV_port, out port EXCLK_port, in port PCLK_port, in port HREF_port, in port VSYNC_port)
{
  set_thread_fast_mode_on();
  
  /* Clock clock block from camera clock */
  set_clock_src(clk, PCLK_port);

  /* Clock camera data ports from clock block */
  set_port_clock(HREF_port, clk);      
  set_port_clock(VSYNC_port, clk);
  set_port_clock(Y_UV_port, clk);
  
  /* Setup external clocking (osciallator removed)*/
  configure_clock_rate(clk2, 100, 4); // 100/6 = 16.6Mhz (LCD * 2)
  configure_port_clock_output(EXCLK_port, clk2);
  //start_clock(clk2);
  
  /* Start the clock block */
  start_clock(clk);
}

unsigned configCamera()
{
  unsigned tmp = 0xffffffff;

  // Reg 0x11: Clock rate control
  // 16 bit mode: pclk = (input freq) mhz / (divide + 1)*2  => [ input freq = EXCLK or 27Mhz (onboard crystal) ]
  tmp = sccbRegWrite(0x11, 0xf); //TODO First write doesn't seem to work
  tmp = sccbRegWrite(0x11, 0x0); 
  tmp = sccbRegWrite(0x11, 0x0);   
  tmp = sccbRegWrite(0x11, 0x1);

  // Reg 0x14: Common Control C
  //     bit5: QVGA: 1, VGA 0
  //     bit2: Gamma on
  tmp += sccbRegWrite(0x14, 0x20);

#ifdef COLOUR_GBR422
  // Reg 0x28: Common Control H
  //     bit6: Black/White mode
  //     bit5: 1: Progressive scan, 0: interlaced
  //     bit4: 1: Freeze AEC/AGC value
  //     bit3: 1: AGC disable
  //     bit2: 1: GRB422
  //     bit1: 1: 2x gain boost
  //     bit0: Reserved
  tmp += sccbRegWrite(0x28, 0b00100100);
#endif

  // Reg 0x12: Common Control A
  //     bit6: Mirror
  tmp = sccbRegWrite(0x12, 0x6C ); // 0x6C mirror: 0x64

  // Should return 0 if all commands acked ok
  return tmp;

}

void camLoop(streaming chanend c_lcd_pro, streaming chanend c_lcd_cntrl, in port Y_UV_port, in port PCLK_port, in port HREF_port, in port VSYNC_port )
{
  int i, j;
  unsigned lastBlue;
  unsigned lastRed;
  int blue = 1;
  int tmp;

  set_thread_fast_mode_on();

  VSYNC_port when pinseq(0) :> tmp;
  while(1)
  {
    #pragma xta endpoint "a"
    VSYNC_port when pinseq(1) :> tmp;    // Wait for vsync
    for ( i= 0; i < 240; i+=1)
    {
      lastRed = 0;
      HREF_port when pinseq(1) :> tmp;
      c_lcd_cntrl <: 0xff;
      for(j = 0; j < 320; j +=1)          // Read 320 pixels
      {

#ifdef COLOUR_GBR422
//        HREF_port when pinseq(1) :> tmp;
        PCLK_port when pinseq(1) :> int tm;
        PCLK_port when pinseq(1) :> int tm;
        PCLK_port when pinseq(1) :> int tm;
        Y_UV_port :> tmp;
         //Y_UV_port :> tmp;


        
        if(blue)
          //lastBlue = tmp >> 10; // RGB666
          lastBlue = tmp >> 11; // RGB565
        else
          lastRed = tmp >> 11; // RGB565
          //lastRed = tmp >> 10; // RGB666
        
        tmp &= 0xff;
        tmp >>= 2;
        tmp = (lastBlue << 11) | (tmp << 5) | lastRed; // RGB565
        //tmp = (lastBlue << 12) | (tmp << 6) | lastRed; // RGB666
        blue = !blue;
#endif

//        if ((j < 160) & (i < 120)) c_lcd_pro <: 0xFF;
//        else c_lcd_pro <: 0xFF00;
        c_lcd_pro <: tmp;
        //fb_data( pixNo, tmp,  c_lcd_pro);
      }
      HREF_port when pinseq(0) :> tmp; // Wait for href low
      //fb_done( c_lcd_pro );
      
    }
    VSYNC_port when pinseq(0) :> tmp;    
    
  }
}


void doCamera(streaming chanend c_lcd_pro, streaming chanend c_lcd_cntrl, clock clk, clock clk2, in port Y_UV_port, in port PCLK_port, out port EXCLK_port, in port HREF_port, in port VSYNC_port, out port p_cam_rst)
{
  timer t;
  unsigned time;
  int tmp;

  printstrln( "XMOS Camera..." );
  printstr( "Initialising interface...");
  initCamInterface(clk, clk2, Y_UV_port, EXCLK_port, PCLK_port, HREF_port, VSYNC_port);
  printstrln("DONE");

  /* Reset Camera */
  p_cam_rst <: 1;
  t :> time;
  t when timerafter(time+10000000) :> int _;
  p_cam_rst <: 0;

  tmp  = configCamera();
  printstr("Config Returned: ");
  printhexln(tmp);
  

 
  printstr("Checking clock...");
  PCLK_port when pinseq(0) :> int tmp;
  PCLK_port when pinseq(1) :> int tmp;
  PCLK_port when pinseq(0) :> int tmp;
  PCLK_port when pinseq(1) :> int tmp;
  PCLK_port when pinseq(0) :> int tmp;
  printstrln("OK");

  printstr("Checking HREF...");
  HREF_port when pinseq(0) :> int tmp;
  HREF_port when pinseq(1) :> int tmp;
  HREF_port when pinseq(0) :> int tmp;
  HREF_port when pinseq(1) :> int tmp;
  HREF_port when pinseq(0) :> int tmp;
  printstrln("OK");

  printstr("Checking clock...");
  VSYNC_port when pinseq(0) :> int tmp;
  VSYNC_port when pinseq(1) :> int tmp;
  VSYNC_port when pinseq(0) :> int tmp;
  VSYNC_port when pinseq(1) :> int tmp;
  VSYNC_port when pinseq(0) :> int tmp;
  printstrln("OK");
  
  printstrln("Camera running...OK");
  
  camLoop(c_lcd_pro, c_lcd_cntrl, Y_UV_port, PCLK_port, HREF_port, VSYNC_port );

}
