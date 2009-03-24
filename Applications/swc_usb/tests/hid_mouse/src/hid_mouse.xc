               
/**
 * @file device_attach.xc
 * @brief Tests HS device attach via UIFM
 * @author Ross Owen, XMOS Semiconductor Ltd 
 * @version 1.00
 **/                               
                                       
#include <syscall.h>
#include <platform.h>
#include <xs1.h>
#include <xclib.h>
#include <print.h>

#include "XUD_USB_Defines.h"
#include "XUD_Defines_Internal.h"
#include "XUD_UIFM_Defines.h"

#include "xud.h"

unsigned samp;

//#define HID 1

static int GetParam(int);
int XUD_Manager(chanend, chanend, chanend, chanend, chanend, chanend, chanend, chanend, chanend);
int XUD_Endpoints0(chanend, chanend);
void ep1_thread();
int XUD_GetBuffer(chanend c, chanend c_con, unsigned epNum, unsigned char
buffer[]);
int XUD_SetBuffer_ResetPid(chanend c, chanend c_con, unsigned epNum, unsigned
char buffer[], unsigned datalength, unsigned char pid);
int XUD_SetBuffer(chanend c, chanend c_con, unsigned epNum, unsigned char
buffer[], unsigned datalength);
void XUD_Sup_Delay(unsigned);

int XUD_Endpoints1(chanend c_ep1_dat, chanend c_ep1_ctl, chanend c_hid, in port p_key)
{
  int loop = 1;
  char buffer[] = {0x0, 8, 0, 0x0, 4, 5, 66, 77, 88, 99};
  int counter = 0, counter2 = 0;
  unsigned char pid = PIDn_DATA0;
  int posx0 = 0;
  int posy0 = 0;
  int posx1 = 0;
  int posy1 = 0;
  int tmp;
  
  int state = 0;

#ifdef CIRCLES
  while(1)
  {
    loop = 1;
    counter = 0;
    //buffer[0] = 0;
    while(loop)
    {
      counter++;
      counter2++;
      if(counter2 == 3)
      {
        //buffer[0] = !buffer[0];
        counter2 = 0;
      }
      if(counter == 50)
      {
               state++; 
        if(state == 4)
          state = 0;

        if(state == 0)
        {
          buffer[1] = 4;
          buffer[2] = 0; 
        }
        else if(state == 1)
        {
          buffer[1] = 0;
          buffer[2] = 4; 
        }
         else if(state == 2)
        {
          buffer[1] = -4;
          buffer[2] = 0; 
        }
        else if(state == 3)
        {
          buffer[1] = 0;
          buffer[2] = -4; 
        }

        counter = 0;
      }
      //loop = XUD_SetBuffer_ResetPid(c_ep1_dat, c_ep1_ctl, 1, buffer, 4, pid);
      loop = XUD_SetBuffer(c_ep1_dat, c_ep1_ctl, 1, buffer, 4);
      pid = pid ^ 0x88;
      
    }
#endif
 
while(1)
 { 
    tmp = inuint(c_hid);
    if(tmp == 0)
    {
      posx0 = inuint(c_hid);
      posy0 = inuint(c_hid);

      buffer[1] = (posx0 - posx1) * 2;
      buffer[2] = (posy1 - posy0) * 2; 
 
      p_key :> tmp;
      buffer[0] = (~tmp) & 7;

      posx1 = posx0;
      posy1 = posy0;

      
      loop = XUD_SetBuffer(c_ep1_dat, c_ep1_ctl, 1, buffer, 4);
      pid = pid ^ 0x88;
  }
  else
   {  
      tmp = inuint(c_hid);
      posx1 = inuint(c_hid);
      posy1 = inuint(c_hid);

   }
}
    



    stop_streaming_slave(c_ep1_dat); 
    stop_streaming_slave(c_ep1_ctl);
  
}

int sof(chanend c_sof)
{
  while(1)
    inuint(c_sof);
}

int dummyEP(chanend c_dat, chanend c_ctl)
{
  while(1)
  {
    inct(c_ctl);
    stop_streaming_slave(c_dat); 
    stop_streaming_slave(c_ctl);
  }
  return 0;
}

void doClock(chanend c_sof, out port p) 
{
  int sofcount = 0;
  int frameNo;
  int val = 0;
  timer t;
  int time;

  p <: val;

  t :> time;
  time += 500000;

  while(1)
  {
    //val = !val;

    select
    {
      case c_sof :> frameNo:
       // break;

      //case t when timerafter(time) :> int _:
      //time += 100000;

      val = !val;
      p <: val;
      break;

   }
  }

}

#define CROSS_RADIUS 4
/** doADCTransaction
  * @brief Sends commands to ADC and returns relevant data (using SPI)
  */
unsigned doADCTransaction(out port p_tou_cs, out port p_tou_dclk, out port p_tou_din, in port p_tou_dout, unsigned controlReg)
{
  unsigned returnVal = 0;
  int i;

  p_tou_cs <: 0;

  controlReg=bitrev(controlReg);
  controlReg=controlReg >> 25;

  p_tou_dclk <: 0;

  // Start bit
  p_tou_din <: 1;
  p_tou_dclk <: 1;

  for(i = 0; i< 7; i+=1)
  {
    p_tou_dclk <: 0;
    p_tou_din <: >> controlReg;
    p_tou_dclk <: 1;
  }

  //Busy clock.  .
  p_tou_dclk <:0;
  p_tou_dclk <:1;

  // TODO: check control reg val to see how many bits to clock in
  // Currently only using 8-bit mode...
  for(i = 0; i < 8; i+=1)
  {
    p_tou_dclk <: 0;
    p_tou_dclk <: 1;
    sync(p_tou_dclk);
    p_tou_dout :> >> returnVal;
  }

  p_tou_cs <: 1;
  p_tou_dclk <: 0;

  return bitrev(returnVal) ;
}


/** getTouchScreenPos
  * @brief Uses doADCTransaction function to get X and Y positions
  * Note the nice use of XC multiple return values for X and Y data
  * Also note: this is probably the main resuable interface this demo app provides
  */
{unsigned, unsigned} getTouchScreenPos(out port p_tou_cs, out port p_tou_dclk, out port p_tou_din, in port p_tou_dout)
{
  unsigned returnValX;
  unsigned returnValY;

  // 8 bit/single ended/power up  Y:0x1e  X:0x5e
  // 8 bit/differential/power up  Y:0x1a  X:0x5a
  returnValY = doADCTransaction(p_tou_cs, p_tou_dclk, p_tou_din, p_tou_dout, 0x1a);
  returnValX = doADCTransaction(p_tou_cs, p_tou_dclk, p_tou_din, p_tou_dout, 0x5a);

  // Physical screen resolution is 240 X 320, ADC resolution (8 bit) is 255 * 255.  Do simple scaling and bounds check
  returnValY = (returnValY * 320) >> 8;
  if (returnValX > 240 - CROSS_RADIUS)
    returnValX = 240 - CROSS_RADIUS;

  return {returnValX, returnValY};
}

/* Generates lcd data */
void lcd_gendata(chanend c_lcd, chanend c_tou)
{
  int x_pos = 120;
  int y_pos = 120;
  int radius = CROSS_RADIUS;
  int tmp; 

  while(1)
  {
    select
    {
      case inuint_byref(c_lcd, tmp):
      for (int i = 0; i < 320; i++)
        for (int j = 0; j < 240; j++)
        {
          if(j == y_pos)
            outuint(c_lcd, 0xffff);
          else if (j == 0)
            outuint(c_lcd, 0xff);
          else if (j == 239)
            outuint(c_lcd, 0xffff00);
          else if(i == 0) 
            outuint(c_lcd, 0xff00);
          else if (i == 319)
            outuint(c_lcd, 0xff0000);  
          else if(i > (x_pos - radius) && i < (x_pos + radius))
            outuint(c_lcd, 0xffff);
          else if(j > (y_pos - radius) && j < (y_pos + radius))
            outuint(c_lcd, 0xffff);
          else 
            outuint(c_lcd, 0x0);
        }
        break;
       case inuint_byref(c_tou, x_pos):
         inuint_byref(c_tou, y_pos);
         break;
     }
  }
}


#define CROSS_RADIUS 4
/** LCD Driver thread
  * @brief Sets up the LCD ports/clock and calls the main lcd loop
  * @param c_lcd channel to position keeper thread
  */
void LcdDriver(chanend c_lcd, out port p_lcd_hsy, out port p_lcd_dtm, out port p_lcd_clk, out port p_lcd_rgb, clock clk_lcd) 
{
  unsigned porttime;
  unsigned tmp;

  // Setup LCD ports and clock...
  set_clock_div(clk_lcd, 19);                // Clock clock block from divided ref clock

  set_port_inv(p_lcd_clk);                  // Invert clock (LCD samples on falling edge)

  set_port_clock(p_lcd_clk, clk_lcd);       // Clock all LCD ports from the clock block
  set_port_clock(p_lcd_dtm, clk_lcd);
  set_port_clock(p_lcd_rgb, clk_lcd);
  set_port_clock(p_lcd_hsy, clk_lcd);

  set_port_mode_clock(p_lcd_clk);           // Generate clock using clock mode of port

  start_clock(clk_lcd);                      // Finally start the clock block

  // Get current time
  p_lcd_hsy <: 0 @ porttime;                 //
  
  while(1)
  {
    outuint(c_lcd, 1);

    // Get position and colour data
    porttime += 700;
    p_lcd_hsy @ porttime <: 0;

    // For each line....
    for(int i = 0; i < 320; i++)
    {
      porttime += 5;
      p_lcd_hsy @ porttime <: 1;

      porttime += 17;
      tmp = inuint(c_lcd);
      
      p_lcd_rgb @ porttime <: tmp;
      p_lcd_dtm @ porttime <: 1;
      
      // Drive out one line ...
      for(int j = 0; j < 239; j++)
      {
        tmp = inuint(c_lcd);
        p_lcd_rgb <: tmp;
      }
      p_lcd_dtm <: 1;

      p_lcd_dtm <: 0;

      porttime += 251;
      p_lcd_hsy @ porttime  <:0;
    }
  }
  set_port_mode_data(p_lcd_clk);
}

int touchDriver(in port p_tou_pen, out port p_tou_cs, out port p_tou_dclk, out port p_tou_din, in port p_tou_dout, out port p_led_30, clock clk_tou, chanend c_tou, chanend c_hid)
{
  int tmp;
  unsigned posx, posy;
  int tmp2=100;
  int pen;

  // Setup touchscreen/adc ports
  set_clock_div(clk_tou,50);
  set_port_clock(p_tou_dclk, clk_tou);
  set_port_clock(p_tou_din, clk_tou);
  set_port_clock(p_tou_dout, clk_tou);
  start_clock(clk_tou);
  while(1)
  {
    /* Wait for user press */
    p_tou_pen when pinseq(0) :> pen;
   
    while(1) 
    { 
      p_tou_pen :> pen;
      if(pen)
        break;
      /* LED on */
      p_led_30 <: 0;

      /* get Position */
      {posy, posx} = getTouchScreenPos(p_tou_cs, p_tou_dclk, p_tou_din, p_tou_dout);
  
      outuint(c_tou, posx);
      outuint(c_tou, posy);

      outuint(c_hid, 0);
      outuint(c_hid, posx);
      outuint(c_hid, posy);
    }
    
    outuint(c_hid, 1);

      p_led_30 <: 1;

  }
}

int  Endpoint0Wrapper(chanend, chanend);

#define USB_CORE 1
#define LCD_CORE 3



// USB Port declarations
on stdcore[USB_CORE] : in port  p_usb_clk      = UIFM_USB_CLK_PORT;
on stdcore[USB_CORE] : out port reg_write_port = UIFM_REG_WRITE_PORT;
on stdcore[USB_CORE] : in  port reg_read_port  = UIFM_REG_READ_PORT;
on stdcore[USB_CORE] : in  port flag0_port     = UIFM_FLAG_0_PORT;
on stdcore[USB_CORE] : in  port flag1_port     = UIFM_FLAG_1_PORT;
on stdcore[USB_CORE] : in  port flag2_port     = UIFM_FLAG_2_PORT;
on stdcore[USB_CORE] : out port p_usb_txd      = UIFM_TX_DATA_PORT;
on stdcore[USB_CORE] :     port p_usb_rxd      = UIFM_RX_DATA_PORT;
on stdcore[USB_CORE] : out port p_usb_rst      = XS1_PORT_1B; // was PORT_1A, 1D for XVB

/* LCD port declarations */
on stdcore[LCD_CORE] : out port p_lcd_hsy  = XS1_PORT_4F;
on stdcore[LCD_CORE] : out port p_lcd_dtm  = XS1_PORT_1B;
on stdcore[LCD_CORE] : out port p_lcd_clk  = XS1_PORT_1A;
on stdcore[LCD_CORE] : out port p_lcd_rgb  = XS1_PORT_32A;
on stdcore[LCD_CORE] : clock clk_lcd       = XS1_CLKBLK_1;

// Touchscreen ADC ports/ clock block
on stdcore[LCD_CORE] : in port p_tou_pen       = XS1_PORT_4C;         // Pen Interupt
on stdcore[LCD_CORE] : in port p_tou_dout      = XS1_PORT_1O;        // DOUT (ADC -> Xcore)
on stdcore[LCD_CORE] : out port p_tou_cs       = XS1_PORT_4E;
on stdcore[LCD_CORE] : out port p_tou_dclk     = XS1_PORT_4D;
on stdcore[LCD_CORE] : out port p_tou_din      = XS1_PORT_1P;  
on stdcore[LCD_CORE] : clock clk_tou           = XS1_CLKBLK_2; 

// LED ports 
on stdcore[3]        : out port p_led_30        = XS1_PORT_1E;

// Button ports
on stdcore[0]        : in port p_key        = XS1_PORT_4F;

on stdcore[1]  : out port p_test0 = XS1_PORT_1A;
on stdcore[1]  : out port p_test1 = XS1_PORT_1C;
void test(out port p0, out port p1)
{
  p0 <: 0;
  set_port_mode_clock(p1);
  while(1);
}


int main()
{
  chan c_ep0_dat, c_ep0_ctl;
  chan c_ep1_dat, c_ep1_ctl;
  chan c_ep2_dat, c_ep2_ctl;
  chan c_ep3_dat, c_ep3_ctl;

  chan c_sof;
  
  chan c_lcd;
  chan c_tou;
  chan c_hid;

  par
  {
    on stdcore[USB_CORE] : XUD_Manager(c_ep0_dat, c_ep0_ctl, c_ep1_dat, c_ep1_ctl, c_ep2_dat, c_ep2_ctl, c_ep3_dat, c_ep3_ctl, c_sof);  
    on stdcore[2] : Endpoint0Wrapper(c_ep0_dat, c_ep0_ctl);
    on stdcore[0] : XUD_Endpoints1(c_ep1_dat, c_ep1_ctl, c_hid, p_key);
    on stdcore[0] : dummyEP(c_ep2_dat, c_ep2_ctl);
    on stdcore[0] : sof(c_sof);
    on stdcore[3] : LcdDriver(c_lcd, p_lcd_hsy, p_lcd_dtm, p_lcd_clk, p_lcd_rgb, clk_lcd); 
    on stdcore[3] : lcd_gendata(c_lcd, c_tou);
    on stdcore[LCD_CORE] : touchDriver(p_tou_pen, p_tou_cs, p_tou_dclk, p_tou_din,  p_tou_dout, p_led_30, clk_tou, c_tou, c_hid);
    on stdcore[1] : test(p_test0, p_test1);
  }

  return 0;
}








