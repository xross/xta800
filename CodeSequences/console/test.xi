# 1 "mii.xc"
# 12 "mii.xc"
# 1 "xs1.h" 1 3
# 31 "xs1.h" 3
# 1 "xs1_g4000b-512.h" 1 3
# 32 "xs1.h" 2 3
# 37 "xs1.h" 3
# 1 "xs1_user.h" 1 3
# 20 "xs1_user.h" 3
# 1 "xs1b_user.h" 1 3
# 21 "xs1_user.h" 2 3
# 38 "xs1.h" 2 3
# 1 "xs1_kernel.h" 1 3
# 20 "xs1_kernel.h" 3
# 1 "xs1b_kernel.h" 1 3
# 21 "xs1_kernel.h" 2 3
# 39 "xs1.h" 2 3
# 1 "xs1_registers.h" 1 3
# 20 "xs1_registers.h" 3
# 1 "xs1b_registers.h" 1 3
# 28 "xs1b_registers.h" 3
# 1 "xs1_g_registers.h" 1 3
# 29 "xs1b_registers.h" 2 3
# 1 "xs1_l_registers.h" 1 3
# 30 "xs1b_registers.h" 2 3
# 21 "xs1_registers.h" 2 3
# 40 "xs1.h" 2 3
# 74 "xs1.h" 3
void configure_in_port_handshake(void port p, in port readyin,
                                 out port readyout,  __clock_t  clk);
# 103 "xs1.h" 3
void configure_out_port_handshake(void port p, in port readyin,
                                 out port readyout,  __clock_t  clk,
                                 unsigned initial);
# 129 "xs1.h" 3
void configure_in_port_strobed_master(void port p, out port readyout,
                                      const  __clock_t  clk);
# 152 "xs1.h" 3
void configure_out_port_strobed_master(void port p, out port readyout,
                                      const  __clock_t  clk, unsigned initial);
# 174 "xs1.h" 3
void configure_in_port_strobed_slave(void port p, in port readyin,  __clock_t  clk);
# 199 "xs1.h" 3
void configure_out_port_strobed_slave(void port p, in port readyin,  __clock_t  clk,
                                      unsigned initial);
# 223 "xs1.h" 3
void configure_in_port(void port p, const  __clock_t  clk);
# 251 "xs1.h" 3
void configure_out_port(void port p, const  __clock_t  clk, unsigned initial);
# 266 "xs1.h" 3
void configure_port_clock_output(void port p, const  __clock_t  clk);
# 275 "xs1.h" 3
void start_port(void port p);
# 282 "xs1.h" 3
void stop_port(void port p);
# 295 "xs1.h" 3
void configure_clock_src( __clock_t  clk, void port p);
# 309 "xs1.h" 3
void configure_clock_ref( __clock_t  clk, unsigned char divide);
# 325 "xs1.h" 3
void configure_clock_rate( __clock_t  clk, unsigned a, unsigned b);
# 339 "xs1.h" 3
void configure_clock_rate_at_least( __clock_t  clk, unsigned a, unsigned b);
# 354 "xs1.h" 3
void configure_clock_rate_at_most( __clock_t  clk, unsigned a, unsigned b);
# 367 "xs1.h" 3
void set_clock_src( __clock_t  clk, void port p);
# 380 "xs1.h" 3
void set_clock_ref( __clock_t  clk);
# 396 "xs1.h" 3
void set_clock_div( __clock_t  clk, unsigned char div);
# 411 "xs1.h" 3
void set_clock_rise_delay( __clock_t  clk, unsigned n);
# 426 "xs1.h" 3
void set_clock_fall_delay( __clock_t  clk, unsigned n);
# 446 "xs1.h" 3
void set_port_clock(void port p, const  __clock_t  clk);
# 464 "xs1.h" 3
void set_port_ready_src(void port p, void port ready);
# 482 "xs1.h" 3
void set_clock_ready_src( __clock_t  clk, void port ready);
# 492 "xs1.h" 3
void set_clock_on( __clock_t  clk);
# 502 "xs1.h" 3
void set_clock_off( __clock_t  clk);
# 512 "xs1.h" 3
void start_clock( __clock_t  clk);
# 520 "xs1.h" 3
void stop_clock( __clock_t  clk);
# 530 "xs1.h" 3
void set_port_use_on(void port p);
# 540 "xs1.h" 3
void set_port_use_off(void port p);
# 550 "xs1.h" 3
void set_port_mode_data(void port p);
# 562 "xs1.h" 3
void set_port_mode_clock(void port p);
# 583 "xs1.h" 3
void set_port_mode_ready(void port p);
# 594 "xs1.h" 3
void set_port_drive(void port p);
# 610 "xs1.h" 3
void set_port_drive_low(void port p);
# 625 "xs1.h" 3
void set_port_pull_up(void port p);
# 640 "xs1.h" 3
void set_port_pull_down(void port p);
# 650 "xs1.h" 3
void set_port_pull_none(void port p);
# 664 "xs1.h" 3
void set_port_master(void port p);
# 678 "xs1.h" 3
void set_port_slave(void port p);
# 692 "xs1.h" 3
void set_port_no_ready(void port p);
# 707 "xs1.h" 3
void set_port_strobed(void port p);
# 720 "xs1.h" 3
void set_port_handshake(void port p);
# 729 "xs1.h" 3
void set_port_no_sample_delay(void port p);
# 738 "xs1.h" 3
void set_port_sample_delay(void port p);
# 746 "xs1.h" 3
void set_port_no_inv(void port p);
# 757 "xs1.h" 3
void set_port_inv(void port p);
# 780 "xs1.h" 3
void set_port_shift_count( void port p, unsigned n);
# 795 "xs1.h" 3
void set_pad_delay(void port p, unsigned n);
# 810 "xs1.h" 3
void set_thread_fast_mode_on(void);
# 818 "xs1.h" 3
void set_thread_fast_mode_off(void);
# 844 "xs1.h" 3
void start_streaming_slave(chanend c);
# 863 "xs1.h" 3
void start_streaming_master(chanend c);
# 876 "xs1.h" 3
void stop_streaming_slave(chanend c);
# 889 "xs1.h" 3
void stop_streaming_master(chanend c);
# 904 "xs1.h" 3
void outuchar(chanend c, unsigned char val);
# 919 "xs1.h" 3
void outuint(chanend c, unsigned val);
# 935 "xs1.h" 3
unsigned char inuchar(chanend c);
# 951 "xs1.h" 3
unsigned inuint(chanend c);
# 968 "xs1.h" 3
void inuchar_byref(chanend c, unsigned char &val);
# 986 "xs1.h" 3
void inuint_byref(chanend c, unsigned &val);
# 996 "xs1.h" 3
void sync(void port p);
# 1007 "xs1.h" 3
unsigned peek(void port p);
# 1021 "xs1.h" 3
void clearbuf(void port p);
# 1037 "xs1.h" 3
unsigned endin( void port p);
# 1054 "xs1.h" 3
unsigned partin( void port p, unsigned n);
# 1070 "xs1.h" 3
void partout( void port p, unsigned n, unsigned val);
# 1088 "xs1.h" 3
unsigned partout_timed( void port p, unsigned n, unsigned val, unsigned t);
# 1106 "xs1.h" 3
{unsigned , unsigned } partin_timestamped( void port p, unsigned n);
# 1124 "xs1.h" 3
unsigned partout_timestamped( void port p, unsigned n, unsigned val);
# 1138 "xs1.h" 3
void outct(chanend c, unsigned char val);
# 1153 "xs1.h" 3
void chkct(chanend c, unsigned char val);
# 1168 "xs1.h" 3
unsigned char inct(chanend c);
# 1183 "xs1.h" 3
void inct_byref(chanend c, unsigned &val);
# 1197 "xs1.h" 3
int testct(chanend c);
# 1210 "xs1.h" 3
int testwct(chanend c);
# 1224 "xs1.h" 3
void soutct(streaming chanend c, unsigned char val);
# 1240 "xs1.h" 3
void schkct(streaming chanend c, unsigned char val);
# 1256 "xs1.h" 3
unsigned char sinct(streaming chanend c);
# 1272 "xs1.h" 3
void sinct_byref(streaming chanend c, unsigned &val);
# 1286 "xs1.h" 3
int stestct(streaming chanend c);
# 1300 "xs1.h" 3
int stestwct(streaming chanend c);
# 1315 "xs1.h" 3
transaction out_char_array(chanend c, const char src[], unsigned size);
# 1328 "xs1.h" 3
transaction in_char_array(chanend c, char src[], unsigned size);
# 1351 "xs1.h" 3
void crc32(unsigned &checksum, unsigned data, unsigned poly);
# 1375 "xs1.h" 3
unsigned crc8shr(unsigned &checksum, unsigned data, unsigned poly);
# 1389 "xs1.h" 3
{unsigned, unsigned} lmul(unsigned a, unsigned b, unsigned c, unsigned d);
# 1403 "xs1.h" 3
{unsigned, unsigned} mac(unsigned a, unsigned b, unsigned c, unsigned d);
# 1417 "xs1.h" 3
{signed, unsigned} macs(signed a, signed b, signed c, unsigned d);
# 1431 "xs1.h" 3
signed sext(unsigned a, unsigned b);
# 1445 "xs1.h" 3
unsigned zext(unsigned a, unsigned b);
# 1458 "xs1.h" 3
void pinseq(unsigned val);
# 1471 "xs1.h" 3
void pinsneq(unsigned val);
# 1486 "xs1.h" 3
void pinseq_at(unsigned val, unsigned time);
# 1501 "xs1.h" 3
void pinsneq_at(unsigned val, unsigned time);
# 1514 "xs1.h" 3
void timerafter(unsigned val);
# 1548 "xs1.h" 3
unsigned getps(unsigned reg);
# 1559 "xs1.h" 3
void setps(unsigned reg, unsigned value);
# 1583 "xs1.h" 3
int read_pswitch_reg(unsigned coreid, unsigned reg, unsigned &data);
# 1605 "xs1.h" 3
int read_sswitch_reg(unsigned coreid, unsigned reg, unsigned &data);
# 1625 "xs1.h" 3
int write_pswitch_reg(unsigned coreid, unsigned reg, unsigned data);
# 1643 "xs1.h" 3
int write_sswitch_reg(unsigned coreid, unsigned reg, unsigned data);
# 1651 "xs1.h" 3
unsigned get_core_id(void);
# 1659 "xs1.h" 3
unsigned get_thread_id(void);
# 1664 "xs1.h" 3
extern int __builtin_getid(void);
# 13 "mii.xc" 2
# 1 "syscall.h" 1 3
# 48 "syscall.h" 3
typedef unsigned ___size_t;

typedef unsigned ___mode_t;

typedef long ___off_t;

void _exit(int status);
void _done();
int _open(const char path[], int flags, ___mode_t mode);
int _close(int d);




int _read(int fd, char buf[], unsigned count);
int _write(int fd, const char buf[], ___size_t count);

int _lseek(int fd, ___off_t offset, int origin);
int _remove(const char filename[]);
int _rename(const char oldname[], const char newname[]);
int _system(const char s[]);
void _exception(unsigned type, unsigned data);
# 77 "syscall.h" 3
void _plugins(int type, unsigned arg1, unsigned arg2);
# 14 "mii.xc" 2
# 1 "xclib.h" 1 3
# 35 "xclib.h" 3
unsigned bitrev(unsigned x);
# 46 "xclib.h" 3
unsigned byterev(unsigned x);
# 59 "xclib.h" 3
int clz(unsigned x);
# 15 "mii.xc" 2




buffered in port:32 p_mii_rxd =  0x40000 ;
in port p_mii_rxdv =  0x10a00 ;

void consumer(chanend);
void mii_rx_pins(chanend);

int mii_counter_rxgood = 0;
int mii_counter_rxeroverflow = 0;
int mii_counter_rxercrc = 0;
int mii_counter_rxerlen = 0;
int mii_counter_rxeralgn = 0;

enum mii_ctl {
  FRAME_DATA = 0,
  END_FRAME = 1,
} ;

int main()
{
    chan channel;
    par {
        consumer(channel);
        mii_rx_pins(channel);
    }
}

void consumer(chanend input)
{
  int done = 0;
  while(!done) {
    int x;
    input:> x;

    if (x) {
      done = 1;
    }
  }
}
# 59 "mii.xc"
#pragma unsafe arrays
void mii_rx_pins(chanend c_mii_rx)
{
    const register unsigned poly = 0xEDB88320;
    timer tmr;

    p_mii_rxdv when  __builtin_pins_eq(0)  :> int lo;
    do
    {
        int eop = 0;
        int length = 0;
        register unsigned crc = 0x9226F562;
        unsigned time;
        unsigned word;
# 74 "mii.xc"
#pragma xta endpoint "packet_start"
        p_mii_rxdv when  __builtin_pins_eq(1)  :> int hi;
        p_mii_rxd when  __builtin_pins_eq(0xD)  :> int sof;

        tmr :> time;

        p_mii_rxd :> word;
        __builtin_out_uint(c_mii_rx, FRAME_DATA) ;
        __builtin_out_uint(c_mii_rx, word) ;
        length+=4;
        __builtin_crc32(crc, word, poly) ;
        p_mii_rxd :> word;
        __builtin_out_uint(c_mii_rx, FRAME_DATA) ;
        __builtin_out_uint(c_mii_rx, word) ;
        length+=4;
        __builtin_crc32(crc, word, poly) ;

        do
        {
# 93 "mii.xc"
#pragma xta label "do_loop"
            select
            {
# 96 "mii.xc"
#pragma xta endpoint "receive_loop"
                case p_mii_rxd :> word:
                {
                    __builtin_out_uint(c_mii_rx, FRAME_DATA) ;
                    __builtin_out_uint(c_mii_rx, word) ;
                    length += 4;
                    __builtin_crc32(crc, word, poly) ;
                    break;
                }
# 105 "mii.xc"
#pragma xta endpoint "eop_detected"
                case p_mii_rxdv when  __builtin_pins_eq(0)  :> int lo:
                {
                    unsigned tail;
                    int taillen;
                    int endbytes;

                    eop = 1;
                    taillen =  __builtin_endin(p_mii_rxd) ;
                    p_mii_rxd :> tail;

                    tail = tail >> (32 - taillen);
                    endbytes = (taillen >> 3);

                    __builtin_out_uint(c_mii_rx, 0) ;
                    __builtin_out_uint(c_mii_rx, tail) ;

                    __builtin_out_uint(c_mii_rx, FRAME_DATA) ;
                    __builtin_out_uint(c_mii_rx, time) ;

                    switch (endbytes) {
                    case 0:
                        length += -4 + 0;
                        break;
                    case 1:
                        length += -4 + 1;
                        tail =  __builtin_crc8shr(crc, tail, poly) ;
                        break;
                    case 2:
                        length += -4 + 2;
                        tail =  __builtin_crc8shr(crc, tail, poly) ;
                        tail =  __builtin_crc8shr(crc, tail, poly) ;
                        break;
                    case 3:
                        length += -4 + 3;
                        tail =  __builtin_crc8shr(crc, tail, poly) ;
                        tail =  __builtin_crc8shr(crc, tail, poly) ;
                        tail =  __builtin_crc8shr(crc, tail, poly) ;
                        break;
                    default:
# 145 "mii.xc"
#pragma xta label "error_overflow"
                        mii_counter_rxeroverflow++;
                        __builtin_clear_buff(p_mii_rxd) ;
                        break;
                    }
# 151 "mii.xc"
#pragma xta endpoint "end_frame"
                    __builtin_out_uint(c_mii_rx, END_FRAME) ;
                    __builtin_out_uint(c_mii_rx, length) ;

                    if (~crc) {
# 156 "mii.xc"
#pragma xta label "error_crc"
                        mii_counter_rxercrc++;
                        __builtin_clear_buff(p_mii_rxd) ;
                    } else if (length & ~63) {
# 160 "mii.xc"
#pragma xta label "error_len"
                        mii_counter_rxerlen++;
                        __builtin_clear_buff(p_mii_rxd) ;
                    } else if (taillen & 7) {
# 164 "mii.xc"
#pragma xta label "error_nibble"
                        mii_counter_rxeralgn++;
                        __builtin_clear_buff(p_mii_rxd) ;
                    } else {
                        mii_counter_rxgood++;
                    }

                    break;
                }
            }
        } while (!eop);
    } while (1);

}
