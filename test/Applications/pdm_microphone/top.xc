#include <xs1.h>
#include <platform.h>
#include <print.h>
#include <xclib.h>
#include <stdlib.h>

out port p_bitclk = on stdcore[0] : XS1_PORT_1H;
out buffered port:32 p_data = on stdcore[0] : XS1_PORT_1I;
out buffered port:32 p_lrclk = on stdcore[0] : XS1_PORT_1F;
clock b_i2s = on stdcore[0] : XS1_CLKBLK_1;
clock b_lpf = on stdcore[0] : XS1_CLKBLK_2;
out port p_lpfclk = on stdcore[0] : XS1_PORT_1L;

// These values are such that F*D*N*(R+1) = 25000000
// Or F*D*(R+1) = 781250 if resolution is fixed to 32
// Warning: partial outputs work in simulation, but not on hardware
#define N 32     // Resolution
#define F 39062  // Sampling frequency
#define R 1      // Reference clock divider
#define D 10     // Clock block divider
#if F > 50000
#error "SYSCLK/PLL1 is not brought out, cannot go beyond 50kHz"
#endif

void lcdac_init()
{
  char message[] = "WARNING! CORE # REF CLK WILL BE MODIFIED!";
  message[14] = get_local_tile_id() + '0';
  printstrln(message);

  // Set reference clock divider and clock block divider
  write_pswitch_reg(get_local_tile_id(), 6, R);
  set_clock_div(b_i2s, D);

  // Setup ports
  set_port_clock(p_bitclk, b_i2s);
  set_port_mode_clock(p_bitclk);
  set_port_clock(p_data, b_i2s);
  set_port_clock(p_lrclk, b_i2s);
  start_clock(b_i2s);

  set_thread_fast_mode_on();

  // DAC needs about 1us to initialize with clock running
}

void lcdac(streaming chanend c_lcdac)
{
  unsigned l, r;
  unsigned t;
  int init = 10000;

  // Wait for other side and then synchronize ports
  c_lcdac :> l;
  t = partout_timestamped(p_data, 1, 0);
  t += (2 * N);
  p_data @ (t + 1) <: 0;
  p_lrclk @ t <: 0;
  c_lcdac :> l;
  p_data <: 0;
  p_lrclk <: 0;
  p_data <: 0;
  p_lrclk <: 0;

  while (1)
  {
    // Input signed left aligned data (as unsigned)
    // Then bit reverse and drive 32 MSb bits using port serialisation
    c_lcdac :> l;
    l &= 0xFFFF0000;
    r = l;

    // Left sample - drive 32 0s as word clock
    p_data <: (init == 0) * bitrev(l);
    p_lrclk <: 0;

    // Right sample - drive 32 1s as word clock
    p_data <: (init == 0) * bitrev(r);
    p_lrclk <: ~0;

    init -= (init > 0);
  }
}

void pdm(streaming chanend c_pdm);

void lpfclk(int freq)
{
  int ff = freq * 100;
  int fr = 100000000;
  const int a = fr / ff;
  const int b = fr % ff;
  int e = 0;
  unsigned t;
  int x;

  printstr("LPF frequency is ");
  printint(freq);
  printstr("Hz (filter clk is ");
  printint(ff / 1000);
  printstrln("kHz)");

  p_lpfclk <: 0 @ t;

  while (1)
  {
    t += a;
    e += b;
    if (e >= ff)
    {
      t++;
      e -= ff;
    }
    p_lpfclk @ t <: x;
    x = !x;
  }
}

extern unsigned char sinewave[100 * 2];

void sine_timed(streaming chanend c_lcdac, int mul, int shf)
{
  unsigned t, T = (100000000 / F);
  timer tmr;
  for (unsigned n = 0; 1; n++)
  {
    int i = (((n * mul) >> shf) % (200 >> 1)) << 1;
    unsigned x = (sinewave[i] << 8) | sinewave[i + 1];
    tmr when timerafter(t) :> void;
    t += T;
    c_lcdac <: (x << 0) << 16;
  }
}

void sine(streaming chanend c_lcdac, int mul, int shf)
{
  timer tmr;
  unsigned t0, t1;
  c_lcdac <: 0;
  c_lcdac <: 0;
  tmr :> t0;
  for (int i = 0; i < 100; i++)
  {
    c_lcdac <: 0;
  }
  tmr :> t1;
  {
    int fsin = mul * 200000000 / (1 << (shf + 1)) / (t1 - t0);
    printstr("Sine frequency is ");
    printint(fsin);
    printstrln("Hz");
  }
  for (unsigned n = 0; 1; n++)
  {
    int i = (((n * mul) >> shf) % (200 >> 1)) << 1;
    unsigned x = (sinewave[i] << 8) | sinewave[i + 1];
    c_lcdac <: (x << 0) << 16;
  }
}

int main()
{
  streaming chan c_lcdac;
  streaming chan c_pdm;
  par
  {
    on stdcore[0] :
    {
      lcdac_init();
      par
      {
        lpfclk(2000);
        lcdac(c_lcdac);
      }
    }
    on stdcore[0] : pdm(c_lcdac);


#if 0
    on stdcore[1] :
    {
      timer tmr;
      unsigned t, T = (100000000 / F);
      unsigned counter = 0;
      int s = 0;
      int n = 0;
      int sample = 0;
      tmr :> t;
      t += T;
      while (1)
      {
        select
        {
          case c_pdm :> int y:
            if (n == 32)
            {
              int gain = 1;
              sample = (s << 19) << gain;
              s = 0;
              n = 0;
              counter++;
            }
            else
            {
              s += y;
              n++;
            }
            break;

          case tmr when timerafter(t) :> void:
            c_lcdac <: sample;
            t += T;
            break;
        }
      }
    }
#endif
    //on stdcore[1] : sine(c_lcdac, 10, 0);
    //on stdcore[1] : sine_timed(c_lcdac, 4, 0);
  }
  return 0;
}

/* Samples are stored MSB first, 2 bytes per sample */
unsigned char sinewave[100 * 2] =
{
  0x00, 0x00, 
  0x00, 0xCD, 
  0x01, 0x9A, 
  0x02, 0x66, 
  0x03, 0x2E, 
  0x03, 0xF4, 
  0x04, 0xB6, 
  0x05, 0x73, 
  0x06, 0x2A, 
  0x06, 0xDB, 
  0x07, 0x86, 
  0x08, 0x28, 
  0x08, 0xC3, 
  0x09, 0x54, 
  0x09, 0xDC, 
  0x0A, 0x5A, 
  0x0A, 0xCE, 
  0x0B, 0x37, 
  0x0B, 0x94, 
  0x0B, 0xE6, 
  0x0C, 0x2C, 
  0x0C, 0x65, 
  0x0C, 0x92, 
  0x0C, 0xB2, 
  0x0C, 0xC6, 
  0x0C, 0xCC, 
  0x0C, 0xC6, 
  0x0C, 0xB2, 
  0x0C, 0x92, 
  0x0C, 0x65, 
  0x0C, 0x2C, 
  0x0B, 0xE6, 
  0x0B, 0x94, 
  0x0B, 0x37, 
  0x0A, 0xCE, 
  0x0A, 0x5A, 
  0x09, 0xDC, 
  0x09, 0x54, 
  0x08, 0xC3, 
  0x08, 0x28, 
  0x07, 0x86, 
  0x06, 0xDB, 
  0x06, 0x2A, 
  0x05, 0x73, 
  0x04, 0xB6, 
  0x03, 0xF4, 
  0x03, 0x2E, 
  0x02, 0x66, 
  0x01, 0x9A, 
  0x00, 0xCD, 
  0x00, 0x00, 
  0xFF, 0x32, 
  0xFE, 0x65, 
  0xFD, 0x99, 
  0xFC, 0xD1, 
  0xFC, 0x0B, 
  0xFB, 0x49, 
  0xFA, 0x8C, 
  0xF9, 0xD5, 
  0xF9, 0x24, 
  0xF8, 0x79, 
  0xF7, 0xD7, 
  0xF7, 0x3C, 
  0xF6, 0xAB, 
  0xF6, 0x23, 
  0xF5, 0xA5, 
  0xF5, 0x31, 
  0xF4, 0xC8, 
  0xF4, 0x6B, 
  0xF4, 0x19, 
  0xF3, 0xD3, 
  0xF3, 0x9A, 
  0xF3, 0x6D, 
  0xF3, 0x4D, 
  0xF3, 0x39, 
  0xF3, 0x33, 
  0xF3, 0x39, 
  0xF3, 0x4D, 
  0xF3, 0x6D, 
  0xF3, 0x9A, 
  0xF3, 0xD3, 
  0xF4, 0x19, 
  0xF4, 0x6B, 
  0xF4, 0xC8, 
  0xF5, 0x31, 
  0xF5, 0xA5, 
  0xF6, 0x23, 
  0xF6, 0xAB, 
  0xF7, 0x3C, 
  0xF7, 0xD7, 
  0xF8, 0x79, 
  0xF9, 0x24, 
  0xF9, 0xD5, 
  0xFA, 0x8C, 
  0xFB, 0x49, 
  0xFC, 0x0B, 
  0xFC, 0xD1, 
  0xFD, 0x99, 
  0xFE, 0x65, 
  0xFF, 0x32, 
};
