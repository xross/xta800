# 1 "../../src/camera/camera.xc"
# 15 "../../src/camera/camera.xc"
# 1 "camera/camera.h" 1
# 16 "camera/camera.h"
# 1 "xs1.h" 1 3
# 31 "xs1.h" 3
# 1 "xs1_g4000b-512.h" 1 3
# 32 "xs1.h" 2 3
# 36 "xs1.h" 3
# 1 "xs1_user.h" 1 3
# 20 "xs1_user.h" 3
# 1 "xs1b_user.h" 1 3
# 21 "xs1_user.h" 2 3
# 37 "xs1.h" 2 3
# 1 "xs1_kernel.h" 1 3
# 20 "xs1_kernel.h" 3
# 1 "xs1b_kernel.h" 1 3
# 21 "xs1_kernel.h" 2 3
# 38 "xs1.h" 2 3
# 1 "xs1_registers.h" 1 3
# 20 "xs1_registers.h" 3
# 1 "xs1b_registers.h" 1 3
# 28 "xs1b_registers.h" 3
# 1 "xs1_g_registers.h" 1 3
# 29 "xs1b_registers.h" 2 3
# 1 "xs1_l_registers.h" 1 3
# 30 "xs1b_registers.h" 2 3
# 21 "xs1_registers.h" 2 3
# 39 "xs1.h" 2 3
# 68 "xs1.h" 3
void configure_in_port_handshake(void port p, in port readyin,
                                 out port readyout, clock clk);
# 97 "xs1.h" 3
void configure_out_port_handshake(void port p, in port readyin,
                                 out port readyout, clock clk,
                                 unsigned initial);
# 123 "xs1.h" 3
void configure_in_port_strobed_master(void port p, out port readyout,
                                      const clock clk);
# 146 "xs1.h" 3
void configure_out_port_strobed_master(void port p, out port readyout,
                                      const clock clk, unsigned initial);
# 168 "xs1.h" 3
void configure_in_port_strobed_slave(void port p, in port readyin, clock clk);
# 193 "xs1.h" 3
void configure_out_port_strobed_slave(void port p, in port readyin, clock clk,
                                      unsigned initial);
# 217 "xs1.h" 3
void configure_in_port(void port p, const clock clk);
# 246 "xs1.h" 3
void configure_out_port(void port p, const clock clk, unsigned initial);
# 262 "xs1.h" 3
void configure_port_clock_output(void port p, const clock clk);
# 271 "xs1.h" 3
void start_port(void port p);
# 279 "xs1.h" 3
void stop_port(void port p);
# 292 "xs1.h" 3
void configure_clock_src(clock clk, void port p);
# 306 "xs1.h" 3
void configure_clock_ref(clock clk, unsigned char divide);
# 322 "xs1.h" 3
void configure_clock_rate(clock clk, unsigned a, unsigned b);
# 336 "xs1.h" 3
void configure_clock_rate_at_least(clock clk, unsigned a, unsigned b);
# 351 "xs1.h" 3
void configure_clock_rate_at_most(clock clk, unsigned a, unsigned b);
# 365 "xs1.h" 3
void set_clock_src(clock clk, void port p);
# 380 "xs1.h" 3
void set_clock_ref(clock clk);
# 397 "xs1.h" 3
void set_clock_div(clock clk, unsigned char div);
# 412 "xs1.h" 3
void set_clock_rise_delay(clock clk, unsigned n);
# 427 "xs1.h" 3
void set_clock_fall_delay(clock clk, unsigned n);
# 448 "xs1.h" 3
void set_port_clock(void port p, const clock clk);
# 467 "xs1.h" 3
void set_port_ready_src(void port p, void port ready);
# 486 "xs1.h" 3
void set_clock_ready_src(clock clk, void port ready);
# 498 "xs1.h" 3
void set_clock_on(clock clk);
# 510 "xs1.h" 3
void set_clock_off(clock clk);
# 520 "xs1.h" 3
void start_clock(clock clk);
# 528 "xs1.h" 3
void stop_clock(clock clk);
# 540 "xs1.h" 3
void set_port_use_on(void port p);
# 552 "xs1.h" 3
void set_port_use_off(void port p);
# 564 "xs1.h" 3
void set_port_mode_data(void port p);
# 578 "xs1.h" 3
void set_port_mode_clock(void port p);
# 601 "xs1.h" 3
void set_port_mode_ready(void port p);
# 612 "xs1.h" 3
void set_port_drive(void port p);
# 628 "xs1.h" 3
void set_port_drive_low(void port p);
# 643 "xs1.h" 3
void set_port_pull_up(void port p);
# 658 "xs1.h" 3
void set_port_pull_down(void port p);
# 668 "xs1.h" 3
void set_port_pull_none(void port p);
# 684 "xs1.h" 3
void set_port_master(void port p);
# 700 "xs1.h" 3
void set_port_slave(void port p);
# 716 "xs1.h" 3
void set_port_no_ready(void port p);
# 733 "xs1.h" 3
void set_port_strobed(void port p);
# 748 "xs1.h" 3
void set_port_handshake(void port p);
# 757 "xs1.h" 3
void set_port_no_sample_delay(void port p);
# 766 "xs1.h" 3
void set_port_sample_delay(void port p);
# 774 "xs1.h" 3
void set_port_no_inv(void port p);
# 785 "xs1.h" 3
void set_port_inv(void port p);
# 810 "xs1.h" 3
void set_port_shift_count( void port p, unsigned n);
# 825 "xs1.h" 3
void set_pad_delay(void port p, unsigned n);
# 842 "xs1.h" 3
void set_thread_fast_mode_on(void);
# 852 "xs1.h" 3
void set_thread_fast_mode_off(void);
# 880 "xs1.h" 3
void start_streaming_slave(chanend c);
# 901 "xs1.h" 3
void start_streaming_master(chanend c);
# 916 "xs1.h" 3
void stop_streaming_slave(chanend c);
# 931 "xs1.h" 3
void stop_streaming_master(chanend c);
# 941 "xs1.h" 3
void outuchar(chanend c, unsigned char val);
# 951 "xs1.h" 3
void outuint(chanend c, unsigned val);
# 962 "xs1.h" 3
unsigned char inuchar(chanend c);
# 973 "xs1.h" 3
unsigned inuint(chanend c);
# 985 "xs1.h" 3
void inuchar_byref(chanend c, unsigned char &val);
# 998 "xs1.h" 3
void inuint_byref(chanend c, unsigned &val);
# 1008 "xs1.h" 3
void sync(void port p);
# 1019 "xs1.h" 3
unsigned peek(void port p);
# 1033 "xs1.h" 3
void clearbuf(void port p);
# 1049 "xs1.h" 3
unsigned endin( void port p);
# 1066 "xs1.h" 3
unsigned partin( void port p, unsigned n);
# 1082 "xs1.h" 3
void partout( void port p, unsigned n, unsigned val);
# 1100 "xs1.h" 3
unsigned partout_timed( void port p, unsigned n, unsigned val, unsigned t);
# 1118 "xs1.h" 3
{unsigned , unsigned } partin_timestamped( void port p, unsigned n);
# 1136 "xs1.h" 3
unsigned partout_timestamped( void port p, unsigned n, unsigned val);
# 1152 "xs1.h" 3
void outct(chanend c, unsigned char val);
# 1169 "xs1.h" 3
void chkct(chanend c, unsigned char val);
# 1186 "xs1.h" 3
unsigned char inct(chanend c);
# 1203 "xs1.h" 3
void inct_byref(chanend c, unsigned &val);
# 1219 "xs1.h" 3
int testct(chanend c);
# 1234 "xs1.h" 3
int testwct(chanend c);
# 1250 "xs1.h" 3
transaction out_char_array(chanend c, const char src[], unsigned size);
# 1265 "xs1.h" 3
transaction in_char_array(chanend c, char src[], unsigned size);
# 1290 "xs1.h" 3
void crc32(unsigned &checksum, unsigned data, unsigned poly);
# 1316 "xs1.h" 3
unsigned crc8shr(unsigned &checksum, unsigned data, unsigned poly);
# 1332 "xs1.h" 3
{unsigned, unsigned} lmul(unsigned a, unsigned b, unsigned c, unsigned d);
# 1348 "xs1.h" 3
{unsigned, unsigned} mac(unsigned a, unsigned b, unsigned c, unsigned d);
# 1364 "xs1.h" 3
{signed, unsigned} macs(signed a, signed b, signed c, unsigned d);
# 1377 "xs1.h" 3
void pinseq(unsigned val);
# 1390 "xs1.h" 3
void pinsneq(unsigned val);
# 1405 "xs1.h" 3
void pinseq_at(unsigned val, unsigned time);
# 1420 "xs1.h" 3
void pinsneq_at(unsigned val, unsigned time);
# 1433 "xs1.h" 3
void timerafter(unsigned val);
# 1469 "xs1.h" 3
unsigned getps(unsigned reg);
# 1482 "xs1.h" 3
void setps(unsigned reg, unsigned value);
# 1508 "xs1.h" 3
int read_pswitch_reg(unsigned coreid, unsigned reg, unsigned &data);
# 1532 "xs1.h" 3
int read_sswitch_reg(unsigned coreid, unsigned reg, unsigned &data);
# 1554 "xs1.h" 3
int write_pswitch_reg(unsigned coreid, unsigned reg, unsigned data);
# 1574 "xs1.h" 3
int write_sswitch_reg(unsigned coreid, unsigned reg, unsigned data);
# 1584 "xs1.h" 3
unsigned get_core_id(void);
# 1594 "xs1.h" 3
unsigned get_thread_id(void);
# 1599 "xs1.h" 3
extern int __builtin_getid(void);
# 17 "camera/camera.h" 2
# 1 "print.h" 1 3
# 34 "print.h" 3
int printchar(char value);
# 40 "print.h" 3
int printcharln(char value);
# 46 "print.h" 3
int printint(int value);
# 52 "print.h" 3
int printintln(int value);
# 58 "print.h" 3
int printuint(unsigned value);
# 64 "print.h" 3
int printuintln(unsigned value);
# 71 "print.h" 3
int printhex(unsigned value);
# 78 "print.h" 3
int printhexln(unsigned value);
# 84 "print.h" 3
int printstr(const char s[]);
# 90 "print.h" 3
int printstrln(const char s[]);
# 18 "camera/camera.h" 2
# 1 "buffer/line_buf.h" 1
# 16 "buffer/line_buf.h"
# 1 "xs1.h" 1 3
# 17 "buffer/line_buf.h" 2




void lb_lineDone(chanend c);

unsigned lb_getPix(chanend c);

void line_buffer( streaming chanend c_lcd_pro, streaming chanend c_lcd_con );
# 19 "camera/camera.h" 2


void doCamera(streaming chanend c_lcd_pro, streaming chanend c_lcd_cntrl, clock clk, clock clk2, in port Y_UV_port, in port PCLK_port, out port EXCLK_port, in port HREF_port, in port VSYNC_port, out port p_cam_rst);
# 16 "../../src/camera/camera.xc" 2
# 1 "camera/sccb.h" 1
# 17 "camera/sccb.h"
# 1 "xs1.h" 1 3
# 18 "camera/sccb.h" 2

unsigned sccbRegWrite(unsigned address, unsigned value);
# 17 "../../src/camera/camera.xc" 2




void initCamInterface(clock clk, clock clk2, in port Y_UV_port, out port EXCLK_port, in port PCLK_port, in port HREF_port, in port VSYNC_port)
{
  __builtin_set_thread_fast() ;


  __builtin_set_clk_src(clk, PCLK_port) ;


  __builtin_set_port_clk(HREF_port, clk) ;
  __builtin_set_port_clk(VSYNC_port, clk) ;
  __builtin_set_port_clk(Y_UV_port, clk) ;


  configure_clock_rate(clk2, 100, 4);
  configure_port_clock_output(EXCLK_port, clk2);



  __builtin_start_clock(clk) ;
}

unsigned configCamera()
{
  unsigned tmp = 0xffffffff;



  tmp = sccbRegWrite(0x11, 0xf);
  tmp = sccbRegWrite(0x11, 0x0);
  tmp = sccbRegWrite(0x11, 0x0);
  tmp = sccbRegWrite(0x11, 0x1);




  tmp += sccbRegWrite(0x14, 0x20);










  tmp += sccbRegWrite(0x28, 0b00100100);




  tmp = sccbRegWrite(0x12, 0x6C );


  return tmp;

}

void camLoop(streaming chanend c_lcd_pro, streaming chanend c_lcd_cntrl, in port Y_UV_port, in port PCLK_port, in port HREF_port, in port VSYNC_port )
{
  int i, j;
  unsigned lastBlue;
  unsigned lastRed;
  int blue = 1;
  int tmp;

  __builtin_set_thread_fast() ;

  VSYNC_port when  __builtin_pins_eq(0)  :> tmp;
  while(1)
  {

    VSYNC_port when  __builtin_pins_eq(1)  :> tmp;
    for ( i= 0; i < 240; i+=1)
    {
      lastRed = 0;
      HREF_port when  __builtin_pins_eq(1)  :> tmp;
      c_lcd_cntrl <: 0xff;
      for(j = 0; j < 320; j +=1)
      {



        PCLK_port when  __builtin_pins_eq(1)  :> int tm;
        PCLK_port when  __builtin_pins_eq(1)  :> int tm;
        PCLK_port when  __builtin_pins_eq(1)  :> int tm;
        Y_UV_port :> tmp;




        if(blue)

          lastBlue = tmp >> 11;
        else
          lastRed = tmp >> 11;


        tmp &= 0xff;
        tmp >>= 2;
        tmp = (lastBlue << 11) | (tmp << 5) | lastRed;

        blue = !blue;




        c_lcd_pro <: tmp;

      }
      HREF_port when  __builtin_pins_eq(0)  :> tmp;


    }
    VSYNC_port when  __builtin_pins_eq(0)  :> tmp;

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


  p_cam_rst <: 1;
  t :> time;
  t when  __builtin_timer_after(time+10000000)  :> int _;
  p_cam_rst <: 0;

  tmp = configCamera();
  printstr("Config Returned: ");
  printhexln(tmp);



  printstr("Checking clock...");
  PCLK_port when  __builtin_pins_eq(0)  :> int tmp;
  PCLK_port when  __builtin_pins_eq(1)  :> int tmp;
  PCLK_port when  __builtin_pins_eq(0)  :> int tmp;
  PCLK_port when  __builtin_pins_eq(1)  :> int tmp;
  PCLK_port when  __builtin_pins_eq(0)  :> int tmp;
  printstrln("OK");

  printstr("Checking HREF...");
  HREF_port when  __builtin_pins_eq(0)  :> int tmp;
  HREF_port when  __builtin_pins_eq(1)  :> int tmp;
  HREF_port when  __builtin_pins_eq(0)  :> int tmp;
  HREF_port when  __builtin_pins_eq(1)  :> int tmp;
  HREF_port when  __builtin_pins_eq(0)  :> int tmp;
  printstrln("OK");

  printstr("Checking clock...");
  VSYNC_port when  __builtin_pins_eq(0)  :> int tmp;
  VSYNC_port when  __builtin_pins_eq(1)  :> int tmp;
  VSYNC_port when  __builtin_pins_eq(0)  :> int tmp;
  VSYNC_port when  __builtin_pins_eq(1)  :> int tmp;
  VSYNC_port when  __builtin_pins_eq(0)  :> int tmp;
  printstrln("OK");

  printstrln("Camera running...OK");

  camLoop(c_lcd_pro, c_lcd_cntrl, Y_UV_port, PCLK_port, HREF_port, VSYNC_port );

}
