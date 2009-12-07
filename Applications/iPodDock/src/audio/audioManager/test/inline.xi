# 1 "i2sOutput_spdifOutput.xc"
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
# 245 "xs1.h" 3
void configure_out_port(void port p, const clock clk, unsigned initial);
# 260 "xs1.h" 3
void configure_port_clock_output(void port p, const clock clk);
# 269 "xs1.h" 3
void start_port(void port p);
# 276 "xs1.h" 3
void stop_port(void port p);
# 289 "xs1.h" 3
void configure_clock_src(clock clk, void port p);
# 303 "xs1.h" 3
void configure_clock_ref(clock clk, unsigned char divide);
# 319 "xs1.h" 3
void configure_clock_rate(clock clk, unsigned a, unsigned b);
# 333 "xs1.h" 3
void configure_clock_rate_at_least(clock clk, unsigned a, unsigned b);
# 348 "xs1.h" 3
void configure_clock_rate_at_most(clock clk, unsigned a, unsigned b);
# 361 "xs1.h" 3
void set_clock_src(clock clk, void port p);
# 374 "xs1.h" 3
void set_clock_ref(clock clk);
# 390 "xs1.h" 3
void set_clock_div(clock clk, unsigned char div);
# 405 "xs1.h" 3
void set_clock_rise_delay(clock clk, unsigned n);
# 420 "xs1.h" 3
void set_clock_fall_delay(clock clk, unsigned n);
# 440 "xs1.h" 3
void set_port_clock(void port p, const clock clk);
# 458 "xs1.h" 3
void set_port_ready_src(void port p, void port ready);
# 476 "xs1.h" 3
void set_clock_ready_src(clock clk, void port ready);
# 486 "xs1.h" 3
void set_clock_on(clock clk);
# 496 "xs1.h" 3
void set_clock_off(clock clk);
# 506 "xs1.h" 3
void start_clock(clock clk);
# 514 "xs1.h" 3
void stop_clock(clock clk);
# 524 "xs1.h" 3
void set_port_use_on(void port p);
# 534 "xs1.h" 3
void set_port_use_off(void port p);
# 544 "xs1.h" 3
void set_port_mode_data(void port p);
# 556 "xs1.h" 3
void set_port_mode_clock(void port p);
# 577 "xs1.h" 3
void set_port_mode_ready(void port p);
# 588 "xs1.h" 3
void set_port_drive(void port p);
# 604 "xs1.h" 3
void set_port_drive_low(void port p);
# 619 "xs1.h" 3
void set_port_pull_up(void port p);
# 634 "xs1.h" 3
void set_port_pull_down(void port p);
# 644 "xs1.h" 3
void set_port_pull_none(void port p);
# 658 "xs1.h" 3
void set_port_master(void port p);
# 672 "xs1.h" 3
void set_port_slave(void port p);
# 686 "xs1.h" 3
void set_port_no_ready(void port p);
# 701 "xs1.h" 3
void set_port_strobed(void port p);
# 714 "xs1.h" 3
void set_port_handshake(void port p);
# 723 "xs1.h" 3
void set_port_no_sample_delay(void port p);
# 732 "xs1.h" 3
void set_port_sample_delay(void port p);
# 740 "xs1.h" 3
void set_port_no_inv(void port p);
# 751 "xs1.h" 3
void set_port_inv(void port p);
# 774 "xs1.h" 3
void set_port_shift_count( void port p, unsigned n);
# 789 "xs1.h" 3
void set_pad_delay(void port p, unsigned n);
# 804 "xs1.h" 3
void set_thread_fast_mode_on(void);
# 812 "xs1.h" 3
void set_thread_fast_mode_off(void);
# 838 "xs1.h" 3
void start_streaming_slave(chanend c);
# 857 "xs1.h" 3
void start_streaming_master(chanend c);
# 870 "xs1.h" 3
void stop_streaming_slave(chanend c);
# 883 "xs1.h" 3
void stop_streaming_master(chanend c);
# 891 "xs1.h" 3
void outuchar(chanend c, unsigned char val);
# 899 "xs1.h" 3
void outuint(chanend c, unsigned val);
# 908 "xs1.h" 3
unsigned char inuchar(chanend c);
# 917 "xs1.h" 3
unsigned inuint(chanend c);
# 927 "xs1.h" 3
void inuchar_byref(chanend c, unsigned char &val);
# 938 "xs1.h" 3
void inuint_byref(chanend c, unsigned &val);
# 948 "xs1.h" 3
void sync(void port p);
# 959 "xs1.h" 3
unsigned peek(void port p);
# 973 "xs1.h" 3
void clearbuf(void port p);
# 989 "xs1.h" 3
unsigned endin( void port p);
# 1006 "xs1.h" 3
unsigned partin( void port p, unsigned n);
# 1022 "xs1.h" 3
void partout( void port p, unsigned n, unsigned val);
# 1040 "xs1.h" 3
unsigned partout_timed( void port p, unsigned n, unsigned val, unsigned t);
# 1058 "xs1.h" 3
{unsigned , unsigned } partin_timestamped( void port p, unsigned n);
# 1076 "xs1.h" 3
unsigned partout_timestamped( void port p, unsigned n, unsigned val);
# 1090 "xs1.h" 3
void outct(chanend c, unsigned char val);
# 1105 "xs1.h" 3
void chkct(chanend c, unsigned char val);
# 1120 "xs1.h" 3
unsigned char inct(chanend c);
# 1135 "xs1.h" 3
void inct_byref(chanend c, unsigned &val);
# 1149 "xs1.h" 3
int testct(chanend c);
# 1162 "xs1.h" 3
int testwct(chanend c);
# 1176 "xs1.h" 3
transaction out_char_array(chanend c, const char src[], unsigned size);
# 1189 "xs1.h" 3
transaction in_char_array(chanend c, char src[], unsigned size);
# 1212 "xs1.h" 3
void crc32(unsigned &checksum, unsigned data, unsigned poly);
# 1236 "xs1.h" 3
unsigned crc8shr(unsigned &checksum, unsigned data, unsigned poly);
# 1250 "xs1.h" 3
{unsigned, unsigned} lmul(unsigned a, unsigned b, unsigned c, unsigned d);
# 1264 "xs1.h" 3
{unsigned, unsigned} mac(unsigned a, unsigned b, unsigned c, unsigned d);
# 1278 "xs1.h" 3
{signed, unsigned} macs(signed a, signed b, signed c, unsigned d);
# 1292 "xs1.h" 3
signed sext(unsigned a, unsigned b);
# 1306 "xs1.h" 3
unsigned zext(unsigned a, unsigned b);
# 1319 "xs1.h" 3
void pinseq(unsigned val);
# 1332 "xs1.h" 3
void pinsneq(unsigned val);
# 1347 "xs1.h" 3
void pinseq_at(unsigned val, unsigned time);
# 1362 "xs1.h" 3
void pinsneq_at(unsigned val, unsigned time);
# 1375 "xs1.h" 3
void timerafter(unsigned val);
# 1409 "xs1.h" 3
unsigned getps(unsigned reg);
# 1420 "xs1.h" 3
void setps(unsigned reg, unsigned value);
# 1444 "xs1.h" 3
int read_pswitch_reg(unsigned coreid, unsigned reg, unsigned &data);
# 1466 "xs1.h" 3
int read_sswitch_reg(unsigned coreid, unsigned reg, unsigned &data);
# 1486 "xs1.h" 3
int write_pswitch_reg(unsigned coreid, unsigned reg, unsigned data);
# 1504 "xs1.h" 3
int write_sswitch_reg(unsigned coreid, unsigned reg, unsigned data);
# 1512 "xs1.h" 3
unsigned get_core_id(void);
# 1520 "xs1.h" 3
unsigned get_thread_id(void);
# 1525 "xs1.h" 3
extern int __builtin_getid(void);
# 2 "i2sOutput_spdifOutput.xc" 2
# 1 "xclib.h" 1 3
# 35 "xclib.h" 3
unsigned bitrev(unsigned x);
# 46 "xclib.h" 3
unsigned byterev(unsigned x);
# 59 "xclib.h" 3
int clz(unsigned x);
# 3 "i2sOutput_spdifOutput.xc" 2
# 1 "global.h" 1
# 7 "global.h"
# 1 "ipod_dock_1v0.h" 1
# 8 "global.h" 2
# 45 "global.h"
# 1 "xs1.h" 1 3
# 46 "global.h" 2
# 48 "global.h"
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
# 49 "global.h" 2
# 4 "i2sOutput_spdifOutput.xc" 2
# 1 "audioManager.h" 1
# 5 "audioManager.h"
# 1 "global.h" 1
# 6 "audioManager.h" 2









typedef struct
{
  buffered out port:32 pSPDIF;
  buffered out port:32 p_sdout;
  buffered out port:32 p_bclk;
  port p_mclk;

  buffered out port:32 p_lrclk;

  clock clk_audio_mclk;
  clock clk_audio_bclk;
} s_audio;

void audioWaitForInput(streaming chanend cSOF, streaming chanend cAudioMixer);
# 35 "audioManager.h"
void audioManager(
    streaming chanend  cAudioStreaming, streaming chanend cSOFGen, streaming chanend cAudioMixer,
    s_audio &audioports);
# 43 "audioManager.h"
void audioPlayerInit(out port p32, s_audio &audioports);
# 5 "i2sOutput_spdifOutput.xc" 2
# 17 "i2sOutput_spdifOutput.xc"
#pragma unsafe arrays
static inline void checkForData( streaming chanend  cAudioStreaming, signed short audiodata[], int &wptr, int &audiorunning, int nelements, int audiovolume)
{
  unsigned val;
  select
  {

    case cAudioStreaming :> val:
# 28 "i2sOutput_spdifOutput.xc"
      audiodata[wptr] = ((int)val * audiovolume) >> 8;
      cAudioStreaming :> val;
      audiodata[wptr + 1] = ((int)val * audiovolume) >> 8;
      audiorunning |= (nelements > ( 512  >> 1));
      if (nelements >  512  - 2 )
      {
# 37 "i2sOutput_spdifOutput.xc"
      }
      else
      {
        { { wptr = (wptr+2) & 0x1FF ; } ; nelements+=2; } ;
      }

      break;
    default:
      break;
  }
}

static inline int mysext(int x, int n)
{
  __asm__ ("sext %0, %1": "=r" (x): "r" (n));
  return x;
}


static inline int parity32(unsigned x)
{
  __builtin_crc32(x, 0, 1) ;
  return (x & 1);
}

unsigned char data_words_48[32] = {
    0x0F, 0x0F,
    0xF0, 0xF3,
    0xF0, 0xCF,
    0x0F, 0x33,
    0xF3, 0x0F,
    0x0C, 0xF3,
    0x0C, 0xCF,
    0xF3, 0x33,
    0xCF, 0x0F,
    0x30, 0xF3,
    0x30, 0xCF,
    0xCF, 0x33,
    0x33, 0x0F,
    0xCC, 0xF3,
    0xCC, 0xCF,
    0x33, 0x33

};

unsigned char preamble_words_48[6] = {
    0x30, 0x3F,
    0x0C, 0x3F,
    0x03, 0x3F
};





signed short audiodata[ 512 ];
static int audiovolume = 0;
static int count = 0;
# 96 "i2sOutput_spdifOutput.xc"
#pragma unsafe arrays
void audioManager_48kHz(
    streaming chanend  cAudioStreaming, streaming chanend cSOFGen, streaming chanend cAudioMixer,
    s_audio &audioports, int freqcount)
{
  int rptr = 0;
  int wptr = 0;
  unsigned word;
  unsigned xor;
  unsigned encoded_preamble, encoded_byte;
  unsigned sample=0, control, parity;

  int controlLeft =  (0x00100004 << 1) ;
  int controlRight =  (0x00200004 << 1) ;
  int nelements = 0;
  int newblock = 2;

  int running = 1;
  int audiorunning = 0;

  xor = 0x0;

  sample = (audiodata[rptr] << 12) & 0x0FFFFFF0 ;
  control = (controlLeft & 2);
  parity = parity32(sample | control |  (0x00000000 << 24) );
  control =  __builtin_bitrev(control | parity)  |  (0x00000000 << 24)  | newblock | sample;

  while (running)
  {
    int loopcount = 192;

    while (loopcount)
    {


      word = control;
      audioports.p_sdout <:  __builtin_bitrev(audiodata[rptr] << 16) ;

      audioports.p_lrclk <: 0x80000000;

      encoded_preamble = preamble_words_48[(word & 0xF)*2+1] ^ xor;
# 139 "i2sOutput_spdifOutput.xc"
#pragma xta endpoint "audio_48_left_o0"
      audioports.p_bclk <: 0xCCCCCCCC;
      audioports.pSPDIF :8 <: encoded_preamble;
      encoded_preamble = preamble_words_48[(word & 0xF)*2] ^ xor;
# 143 "i2sOutput_spdifOutput.xc"
#pragma xta endpoint "audio_48_left_o1"
      audioports.pSPDIF :8 <: encoded_preamble;
      xor = mysext(encoded_preamble, 8) >> 8;
      word = word >> 4;
      controlLeft >>=1;
      control = (controlRight & 2);
      sample = (audiodata[rptr+1] << 12) & 0x0FFFFFF0 ;
      encoded_byte = data_words_48[(word & 0xF) * 2] ^ xor;
# 151 "i2sOutput_spdifOutput.xc"
#pragma xta endpoint "audio_48_left_o2"
      audioports.pSPDIF :8 <: data_words_48[(word & 0xF)*2+1] ^ xor;
      parity = parity32(sample | control |  (0x00000000 << 24) );
      control =  __builtin_bitrev(parity | control)  |  (0x00000000 << 24) ;
# 155 "i2sOutput_spdifOutput.xc"
#pragma xta endpoint "audio_48_left_o3"
      audioports.pSPDIF :8 <: encoded_byte;

      count += 2560;
      if (count > freqcount)
      {
        select
        {
        case cSOFGen :> int _:
          cSOFGen <: 1;
          break;
        default:
          break;
        }
        count -= freqcount;
      }
      xor = mysext(encoded_byte, 8) >> 8;

      for (int i=0; i<3; i++)
      {
        word = word >> 4;
        encoded_byte = data_words_48[(word & 0xF)*2+1] ^ xor;
# 177 "i2sOutput_spdifOutput.xc"
#pragma xta endpoint "audio_48_left_o4"
        audioports.p_bclk <: 0xCCCCCCCC;
        audioports.pSPDIF :8 <: encoded_byte;
        encoded_byte = data_words_48[(word & 0xF) * 2] ^ xor;
        xor = mysext(encoded_byte, 8) >> 8;
        word = word >> 4;
# 183 "i2sOutput_spdifOutput.xc"
#pragma xta endpoint "audio_48_left_o5"
        audioports.pSPDIF :8 <: encoded_byte;

        checkForData(cAudioStreaming, audiodata, wptr, audiorunning, nelements, audiovolume);
# 218 "i2sOutput_spdifOutput.xc"
        sample = sample | control | 1;
# 219 "i2sOutput_spdifOutput.xc"
#pragma xta endpoint "audio_48_left_o6"
        audioports.pSPDIF :8 <: data_words_48[(word & 0xF)*2+1] ^ xor;
        encoded_byte = data_words_48[(word & 0xF) * 2] ^ xor;
# 222 "i2sOutput_spdifOutput.xc"
#pragma xta endpoint "audio_48_left_o7"
        audioports.pSPDIF :8 <: encoded_byte;
        xor = mysext(encoded_byte, 8) >> 8;
      }



      word = sample;
      audioports.p_sdout <:  __builtin_bitrev(audiodata[rptr+1] << 16) ;

      audioports.p_lrclk <: 0x7FFFFFFF;



      encoded_preamble = preamble_words_48[(word & 0xF)*2+1] ^ xor;
# 237 "i2sOutput_spdifOutput.xc"
#pragma xta endpoint "audio_48_right_o0"
      audioports.p_bclk <: 0xCCCCCCCC;
      audioports.pSPDIF :8 <: encoded_preamble;
      encoded_preamble = preamble_words_48[(word & 0xF)*2] ^ xor;
      xor = mysext(encoded_preamble, 8) >> 8;
      word = word >> 4;
      if (audiorunning && nelements > 0)
      {
        { { rptr = (rptr+2) & 0x1FF ; } ; nelements-=2; } ;
      }
# 254 "i2sOutput_spdifOutput.xc"
      else
      {
        audiorunning = 0;
      }
# 258 "i2sOutput_spdifOutput.xc"
#pragma xta endpoint "audio_48_right_o1"
      audioports.pSPDIF :8 <: encoded_preamble;
      loopcount--;
      if (loopcount == 0)
      {
        controlLeft =  (0x00100004 << 1) ;
        controlRight =  (0x00200004 << 1) ;
        newblock = 2;
      }
      else
      {
        controlRight >>= 1;
        newblock = 0;
      }
      sample = (audiodata[rptr] << 12) & 0x0FFFFFF0 ;
# 274 "i2sOutput_spdifOutput.xc"
#pragma xta endpoint "audio_48_right_o2"
      audioports.pSPDIF :8 <: data_words_48[(word & 0xF)*2+1] ^ xor;
      control = (controlLeft & 2);
      encoded_byte = data_words_48[(word & 0xF) * 2] ^ xor;
# 278 "i2sOutput_spdifOutput.xc"
#pragma xta endpoint "audio_48_right_o3"
      audioports.pSPDIF :8 <: encoded_byte;
      parity = parity32(sample | control |  (0x00000000 << 24) );
      control =  __builtin_bitrev(control | parity)  |  (0x00000000 << 24)  | newblock | sample;
      xor = mysext(encoded_byte, 8) >> 8;

      for (int i=0; i<3; i++)
      {
        word = word >> 4;
        encoded_byte = data_words_48[(word & 0xF)*2+1] ^ xor;
        if (running)
        {
          select
          {
            case cAudioMixer :> int cmd:
# 293 "i2sOutput_spdifOutput.xc"
#pragma xta label "audio_48_freqadjust"
              switch (cmd)
              {
              case 0:
              {
                int val;
                cAudioMixer :> val;
                switch (val)
                {
                case 0:
                  freqcount = 112896;
                  break;
                case 1:
                  freqcount = 122880;
                  break;
                }
              }
              break;
              case 1:
              {
# 313 "i2sOutput_spdifOutput.xc"
#pragma xta label "audio_48_wipebuf"
                for (int i=0; i< 512 ; i++)
                  audiodata[i] = 0;
              }
              break;
              case 2:
              {
                running = 0;
              }
              break;
              case 3:
# 324 "i2sOutput_spdifOutput.xc"
#pragma xta label "audio_48_waitforinput"
                audioWaitForInput(cSOFGen, cAudioMixer);
                break;
              case 4:
# 328 "i2sOutput_spdifOutput.xc"
#pragma xta label "audio_48_voladjust"
                cAudioMixer :> audiovolume;
                if (audiovolume == 255)
                  audiovolume = 256;
                break;
              }
            break;
          default:
            break;
          }
        }
        audioports.p_bclk <: 0xCCCCCCCC;
# 340 "i2sOutput_spdifOutput.xc"
#pragma xta endpoint "audio_48_right_o4"
        audioports.pSPDIF :8 <: encoded_byte;
        encoded_byte = data_words_48[(word & 0xF) * 2] ^ xor;
        xor = mysext(encoded_byte, 8) >> 8;
        word = word >> 4;
# 345 "i2sOutput_spdifOutput.xc"
#pragma xta endpoint "audio_48_right_o5"
        audioports.pSPDIF :8 <: encoded_byte;

        checkForData(cAudioStreaming, audiodata, wptr, audiorunning, nelements, audiovolume);
# 380 "i2sOutput_spdifOutput.xc"
#pragma xta endpoint "audio_48_right_o6"
        audioports.pSPDIF :8 <: data_words_48[(word & 0xF)*2+1] ^ xor;
        encoded_byte = data_words_48[(word & 0xF) * 2] ^ xor;
# 383 "i2sOutput_spdifOutput.xc"
#pragma xta endpoint "audio_48_right_o7"
        audioports.pSPDIF :8 <: encoded_byte;
        xor = mysext(encoded_byte, 8) >> 8;
      }
    }
  }
# 389 "i2sOutput_spdifOutput.xc"
#pragma xta label "audio_48_exit"
}

unsigned short data_words_32[32] = {
    0x003F, 0x003F,
    0x0FC0, 0x0FC7,
    0x0FC0, 0x0E3F,
    0x003F, 0x01C7,
    0x0FC7, 0x003F,
    0x0038, 0x0FC7,
    0x0038, 0x0E3F,
    0x0FC7, 0x01C7,
    0x0E3F, 0x003F,
    0x01C0, 0x0FC7,
    0x01C0, 0x0E3F,
    0x0E3F, 0x01C7,
    0x01C7, 0x003F,
    0x0E38, 0x0FC7,
    0x0E38, 0x0E3F,
    0x01C7, 0x01C7
};

unsigned short preamble_words_32[6] = {
    0x01C0, 0x01FF,
    0x0038, 0x01FF,
    0x0007, 0x01FF
};
# 417 "i2sOutput_spdifOutput.xc"
#pragma unsafe arrays
void audioManager_32kHz(
    streaming chanend  cAudioStreaming, streaming chanend cSOFGen, streaming chanend cAudioMixer,
    s_audio &audioports, int freqcount)
{
  int rptr = 0;
  int wptr = 0;
  unsigned word;
  unsigned xor;
  unsigned encoded_preamble, encoded_byte;
  unsigned sample=0, control, parity;

  int controlLeft =  (0x00100004 << 1) ;
  int controlRight =  (0x00200004 << 1) ;
  int newblock = 2;
  int nelements = 0;

  int running = 1;
  int audiorunning = 0;

  xor = 0x0;

  sample = (audiodata[rptr] << 12) & 0x0FFFFFF0 ;
  control = (controlLeft & 2);
  parity = parity32(sample | control |  (0x00000000 << 24) );
  control =  __builtin_bitrev(control | parity)  |  (0x00000000 << 24)  | newblock | sample;

  while (running)
  {
    int loopcount = 192;

    while (loopcount)
    {


      word = control;
      audioports.p_sdout <:  __builtin_bitrev(audiodata[rptr] << 16) ;

      audioports.p_lrclk <: 0x80000000;

      encoded_preamble = preamble_words_32[(word & 0xF)*2+1] ^ xor;
# 460 "i2sOutput_spdifOutput.xc"
#pragma xta endpoint "audio_32_left_o0"
      audioports.p_bclk <: 0xC71C71C7;
      audioports.p_bclk :16 <: 0x1C71;

      audioports.pSPDIF :12 <: encoded_preamble;
      encoded_preamble = preamble_words_32[(word & 0xF)*2] ^ xor;
# 466 "i2sOutput_spdifOutput.xc"
#pragma xta endpoint "audio_32_left_o1"
      audioports.pSPDIF :12 <: encoded_preamble;
      xor = mysext(encoded_preamble, 8) >> 8;
      word = word >> 4;
      controlLeft >>=1;
      control = (controlRight & 2);
      sample = (audiodata[rptr+1] << 12) & 0x0FFFFFF0 ;
      encoded_byte = data_words_32[(word & 0xF) * 2] ^ xor;
# 474 "i2sOutput_spdifOutput.xc"
#pragma xta endpoint "audio_32_left_o2"
      audioports.pSPDIF :12 <: data_words_32[(word & 0xF)*2+1] ^ xor;
      parity = parity32(sample | control |  (0x00000000 << 24) );
      control =  __builtin_bitrev(parity | control)  |  (0x00000000 << 24) ;
# 478 "i2sOutput_spdifOutput.xc"
#pragma xta endpoint "audio_32_left_o3"
      audioports.pSPDIF :12 <: encoded_byte;

      count += 3840;
      if (count > freqcount)
      {
        select
        {
        case cSOFGen :> int _:
          cSOFGen <: 1;
          break;
        default:
          break;
        }
        count -= freqcount;
      }
      xor = mysext(encoded_byte, 8) >> 8;

      for (int i=0; i<3; i++)
      {
        word = word >> 4;
        encoded_byte = data_words_32[(word & 0xF)*2+1] ^ xor;
# 500 "i2sOutput_spdifOutput.xc"
#pragma xta endpoint "audio_32_left_o4"
        audioports.p_bclk <: 0xC71C71C7;
        audioports.p_bclk :16 <: 0x1C71;
        audioports.pSPDIF :12 <: encoded_byte;
        encoded_byte = data_words_32[(word & 0xF) * 2] ^ xor;
        xor = mysext(encoded_byte, 8) >> 8;
        word = word >> 4;
# 507 "i2sOutput_spdifOutput.xc"
#pragma xta endpoint "audio_32_left_o5"
        audioports.pSPDIF :12 <: encoded_byte;

        checkForData(cAudioStreaming, audiodata, wptr, audiorunning, nelements, audiovolume);
# 542 "i2sOutput_spdifOutput.xc"
        sample = sample | control | 1;
# 543 "i2sOutput_spdifOutput.xc"
#pragma xta endpoint "audio_32_left_o6"
        audioports.pSPDIF :12 <: data_words_32[(word & 0xF)*2+1] ^ xor;
        encoded_byte = data_words_32[(word & 0xF) * 2] ^ xor;
# 546 "i2sOutput_spdifOutput.xc"
#pragma xta endpoint "audio_32_left_o7"
        audioports.pSPDIF :12 <: encoded_byte;
        xor = mysext(encoded_byte, 8) >> 8;
      }



      word = sample;
      audioports.p_sdout <:  __builtin_bitrev(audiodata[rptr+1] << 16) ;

      audioports.p_lrclk <: 0x7FFFFFFF;



      encoded_preamble = preamble_words_32[(word & 0xF)*2+1] ^ xor;
# 561 "i2sOutput_spdifOutput.xc"
#pragma xta endpoint "audio_32_right_o0"
      audioports.p_bclk <: 0xC71C71C7;
      audioports.p_bclk :16 <: 0x1C71;
      audioports.pSPDIF :12 <: encoded_preamble;
      encoded_preamble = preamble_words_32[(word & 0xF)*2] ^ xor;
      xor = mysext(encoded_preamble, 8) >> 8;
      if (audiorunning && nelements > 0)
      {
        { { rptr = (rptr+2) & 0x1FF ; } ; nelements-=2; } ;
      }
# 577 "i2sOutput_spdifOutput.xc"
      else
      {
        audiorunning = 0;
      }
# 581 "i2sOutput_spdifOutput.xc"
#pragma xta endpoint "audio_32_right_o1"
      audioports.pSPDIF :12 <: encoded_preamble;
      loopcount--;
      if (loopcount == 0)
      {
        controlLeft =  (0x00100004 << 1) ;
        controlRight =  (0x00200004 << 1) ;
        newblock = 2;
      }
      else
      {
        controlRight >>= 1;
        newblock = 0;
      }
      sample = (audiodata[rptr] << 12) & 0x0FFFFFF0 ;
# 597 "i2sOutput_spdifOutput.xc"
#pragma xta endpoint "audio_32_right_o2"
      audioports.pSPDIF :12 <: data_words_32[(word & 0xF)*2+1] ^ xor;
      control = (controlLeft & 2);
      encoded_byte = data_words_32[(word & 0xF) * 2] ^ xor;
# 601 "i2sOutput_spdifOutput.xc"
#pragma xta endpoint "audio_32_right_o3"
      audioports.pSPDIF :12 <: encoded_byte;
      parity = parity32(sample | control |  (0x00000000 << 24) );
      control =  __builtin_bitrev(control | parity)  |  (0x00000000 << 24)  | newblock | sample;
      xor = mysext(encoded_byte, 8) >> 8;

      for (int i=0; i<3; i++)
      {
        word = word >> 4;
        encoded_byte = data_words_32[(word & 0xF)*2+1] ^ xor;
        if (running)
        {
          select
          {
            case cAudioMixer :> int cmd:
# 616 "i2sOutput_spdifOutput.xc"
#pragma xta label "audio_32_freqadjust"
              switch (cmd)
              {
              case 0:
              {
                int val;
                cAudioMixer :> val;
                switch (val)
                {
                case 0:
                  freqcount = 112896;
                  break;
                case 1:
                  freqcount = 122880;
                  break;
                }
              }
              break;
              case 1:
              {
# 636 "i2sOutput_spdifOutput.xc"
#pragma xta label "audio_32_wipebuf"
                for (int i=0; i< 512 ; i++)
                  audiodata[i] = 0;
              }
              break;
              case 2:
              {
                running = 0;
              }
              break;
              case 3:
                audioWaitForInput(cSOFGen, cAudioMixer);
                break;
              case 4:
# 650 "i2sOutput_spdifOutput.xc"
#pragma xta label "audio_32_voladjust"
                cAudioMixer :> audiovolume;
                if (audiovolume == 255)
                  audiovolume = 256;
                break;
              }
            break;
          default:
            break;
          }
        }
        audioports.p_bclk <: 0xC71C71C7;
        audioports.p_bclk :16 <: 0x1C71;
# 663 "i2sOutput_spdifOutput.xc"
#pragma xta endpoint "audio_32_right_o4"
        audioports.pSPDIF :12 <: encoded_byte;
        encoded_byte = data_words_32[(word & 0xF) * 2] ^ xor;
        xor = mysext(encoded_byte, 8) >> 8;
        word = word >> 4;
# 668 "i2sOutput_spdifOutput.xc"
#pragma xta endpoint "audio_32_right_o5"
        audioports.pSPDIF :12 <: encoded_byte;

        checkForData(cAudioStreaming, audiodata, wptr, audiorunning, nelements, audiovolume);
# 703 "i2sOutput_spdifOutput.xc"
#pragma xta endpoint "audio_32_right_o6"
        audioports.pSPDIF :12 <: data_words_32[(word & 0xF)*2+1] ^ xor;
        encoded_byte = data_words_32[(word & 0xF) * 2] ^ xor;
# 706 "i2sOutput_spdifOutput.xc"
#pragma xta endpoint "audio_32_right_o7"
        audioports.pSPDIF :12 <: encoded_byte;
        xor = mysext(encoded_byte, 8) >> 8;
      }
    }
  }

}
# 716 "i2sOutput_spdifOutput.xc"
#pragma unsafe arrays
void audioManager(
    streaming chanend  cAudioStreaming, streaming chanend cSOFGen, streaming chanend cAudioMixer,
    s_audio &audioports)
{
  int mode = 48000;
  timer audiotimer;
  int time;

  __builtin_set_thread_fast() ;


  cAudioStreaming <: 0;


  for (int i=0; i< 512 ; i++)
    audiodata[i] = 0;

  cSOFGen <: 1;

  while (1)
  {
    switch (mode)
    {
    case (44100):
      audioManager_48kHz(cAudioStreaming, cSOFGen, cAudioMixer, audioports, 112896);
      break;
    case (48000):
      audioManager_48kHz(cAudioStreaming, cSOFGen, cAudioMixer, audioports, 122880);
      break;
    case (32000):
      audioManager_32kHz(cAudioStreaming, cSOFGen, cAudioMixer, audioports, 122880);
      break;
    default:
      ;
      ;
      while (1);
      break;
    }
    cAudioMixer :> mode;


    {
      int lrval = 0x80000000;
      for (int i=0; i<256; i++)
      {
        audioports.p_sdout <: 0;

        audioports.p_lrclk <: lrval;

        lrval = ~lrval;
        audioports.p_bclk <: 0xCCCCCCCC;
        audioports.p_bclk <: 0xCCCCCCCC;
        audioports.p_bclk <: 0xCCCCCCCC;
        audioports.p_bclk <: 0xCCCCCCCC;

        count += 2560;
        if (count > 122880)
        {
          select
          {
          case cSOFGen :> int _:
            cSOFGen <: 1;
            break;
          default:
            break;
          }
          count -= 122880;
        }
      }
    }


    cAudioMixer <: 0;


    audioports.p_bclk :1 <: 0x0;
    audiotimer :> time;
    for (int i=0; i<1; i++)
    {
      audiotimer when  __builtin_timer_after(time + 1000* 0x64 )  :> time;
      select
      {
      case cSOFGen :> int _:
        cSOFGen <: 1;
        break;
      default:
        break;
      }
    }


  }
}
# 813 "i2sOutput_spdifOutput.xc"
#pragma xta command "add exclusion audio_48_exit"
# 814 "i2sOutput_spdifOutput.xc"
#pragma xta command "add exclusion audio_48_waitforinput"
# 815 "i2sOutput_spdifOutput.xc"
#pragma xta command "add exclusion audio_48_wipebuf"
# 816 "i2sOutput_spdifOutput.xc"
#pragma xta command "add exclusion audio_48_freqadjust"
# 817 "i2sOutput_spdifOutput.xc"
#pragma xta command "config freq 500"
# 818 "i2sOutput_spdifOutput.xc"
#pragma xta command "analyse path audio_48_left_o0 audio_48_left_o2"
# 819 "i2sOutput_spdifOutput.xc"
#pragma xta command "set required - 1300 ns"
# 820 "i2sOutput_spdifOutput.xc"
#pragma xta command "analyse path audio_48_left_o1 audio_48_left_o3"
# 821 "i2sOutput_spdifOutput.xc"
#pragma xta command "set required - 1300 ns"
# 822 "i2sOutput_spdifOutput.xc"
#pragma xta command "analyse path audio_48_left_o2 audio_48_left_o4"
# 823 "i2sOutput_spdifOutput.xc"
#pragma xta command "set required - 1300 ns"
# 824 "i2sOutput_spdifOutput.xc"
#pragma xta command "analyse path audio_48_left_o3 audio_48_left_o5"
# 825 "i2sOutput_spdifOutput.xc"
#pragma xta command "set required - 1300 ns"
# 826 "i2sOutput_spdifOutput.xc"
#pragma xta command "analyse path audio_48_left_o4 audio_48_left_o6"
# 827 "i2sOutput_spdifOutput.xc"
#pragma xta command "set required - 1300 ns"
# 828 "i2sOutput_spdifOutput.xc"
#pragma xta command "analyse path audio_48_left_o5 audio_48_left_o7"
# 829 "i2sOutput_spdifOutput.xc"
#pragma xta command "set required - 1300 ns"
# 830 "i2sOutput_spdifOutput.xc"
#pragma xta command "add exclusion audio_48_right_o0"
# 831 "i2sOutput_spdifOutput.xc"
#pragma xta command "analyse path audio_48_left_o6 audio_48_left_o4  "
# 832 "i2sOutput_spdifOutput.xc"
#pragma xta command "set required - 1300 ns"
# 833 "i2sOutput_spdifOutput.xc"
#pragma xta command "remove exclusion audio_48_right_o0"
# 834 "i2sOutput_spdifOutput.xc"
#pragma xta command "add exclusion audio_48_left_o4"
# 835 "i2sOutput_spdifOutput.xc"
#pragma xta command "analyse path audio_48_left_o6 audio_48_right_o0"
# 836 "i2sOutput_spdifOutput.xc"
#pragma xta command "set required - 1300 ns"
# 837 "i2sOutput_spdifOutput.xc"
#pragma xta command "analyse path audio_48_left_o7 audio_48_right_o1"
# 838 "i2sOutput_spdifOutput.xc"
#pragma xta command "set required - 1300 ns"
# 839 "i2sOutput_spdifOutput.xc"
#pragma xta command "remove exclusion audio_48_left_o4"
# 840 "i2sOutput_spdifOutput.xc"
#pragma xta command "analyse path audio_48_right_o0 audio_48_right_o2"
# 841 "i2sOutput_spdifOutput.xc"
#pragma xta command "set required - 1300 ns"
# 842 "i2sOutput_spdifOutput.xc"
#pragma xta command "analyse path audio_48_right_o1 audio_48_right_o3"
# 843 "i2sOutput_spdifOutput.xc"
#pragma xta command "set required - 1300 ns"
# 844 "i2sOutput_spdifOutput.xc"
#pragma xta command "analyse path audio_48_right_o2 audio_48_right_o4"
# 845 "i2sOutput_spdifOutput.xc"
#pragma xta command "set required - 1300 ns"
# 846 "i2sOutput_spdifOutput.xc"
#pragma xta command "analyse path audio_48_right_o3 audio_48_right_o5"
# 847 "i2sOutput_spdifOutput.xc"
#pragma xta command "set required - 1300 ns"
# 848 "i2sOutput_spdifOutput.xc"
#pragma xta command "analyse path audio_48_right_o4 audio_48_right_o6"
# 849 "i2sOutput_spdifOutput.xc"
#pragma xta command "set required - 1300 ns"
# 850 "i2sOutput_spdifOutput.xc"
#pragma xta command "analyse path audio_48_right_o5 audio_48_right_o7"
# 851 "i2sOutput_spdifOutput.xc"
#pragma xta command "set required - 1300 ns"
# 852 "i2sOutput_spdifOutput.xc"
#pragma xta command "add exclusion audio_48_left_o0"
# 853 "i2sOutput_spdifOutput.xc"
#pragma xta command "analyse path audio_48_right_o6 audio_48_right_o4"
# 854 "i2sOutput_spdifOutput.xc"
#pragma xta command "set required - 1300 ns"
# 855 "i2sOutput_spdifOutput.xc"
#pragma xta command "analyse path audio_48_right_o7 audio_48_right_o5"
# 856 "i2sOutput_spdifOutput.xc"
#pragma xta command "set required - 1300 ns"
# 857 "i2sOutput_spdifOutput.xc"
#pragma xta command "remove exclusion audio_48_left_o0"
# 858 "i2sOutput_spdifOutput.xc"
#pragma xta command "add exclusion audio_48_right_o4"
# 859 "i2sOutput_spdifOutput.xc"
#pragma xta command "analyse path audio_48_right_o6 audio_48_left_o0"
# 860 "i2sOutput_spdifOutput.xc"
#pragma xta command "set required - 1300 ns"
# 861 "i2sOutput_spdifOutput.xc"
#pragma xta command "analyse path audio_48_right_o7 audio_48_left_o1"
# 862 "i2sOutput_spdifOutput.xc"
#pragma xta command "set required - 1300 ns"
# 863 "i2sOutput_spdifOutput.xc"
#pragma xta command "remove exclusion audio_48_right_o4"
# 864 "i2sOutput_spdifOutput.xc"
#pragma xta command "remove exclusion audio_48_wipebuf"
# 865 "i2sOutput_spdifOutput.xc"
#pragma xta command "remove exclusion audio_48_freqadjust"
# 866 "i2sOutput_spdifOutput.xc"
#pragma xta command "remove exclusion audio_48_exit"
# 867 "i2sOutput_spdifOutput.xc"
#pragma xta command "remove exclusion audio_48_waitforinput"
# 868 "i2sOutput_spdifOutput.xc"
#pragma xta command "print summary"
# 869 "i2sOutput_spdifOutput.xc"
#pragma xta command "exit"
