
/** XUD_Manager.xc
  * @brief     XMOS USB Driver (XUD)
  * @author    Ross Owen
  * @version   0.1
  **/

//#include <AutoDefines.h>   // TODO move to xs1.h
#include <xs1.h>
#include <print.h>

#include "xud.h"
#include "XUD_UIFM_Defines.h"
#include "XUD_USB_Defines.h"

#include "XUD_Support.h"
#include "XUD_UIFM_Functions.h"

#include "XUD_DeviceAttach.h"

// Prototypes for main XUD threads:
extern void XUD_LLD_Io(in port p_usb_rxd, in port rxa_port, out port p_usb_txd, in port rxe_port, in port flag0_port, chanend c_out, chanend c_in, chanend c_ctl_io, chanend c_ctl_buf, XUD_EpType epTypeTableOut[], XUD_EpType epTypeTableIn[]) ;

extern void XUD_LLD_BuffManager(chanend c_out, chanend c_in, chanend c_ep, chanend c_con_buff, chanend c_rate_report);

extern void XUD_LLD_EpManager(chanend c_ep, XUD_chan epChans[],  chanend c_ctl_ep, XUD_EpType epTypeTableOut[], XUD_EpType epTypeTableIn[]);

extern unsigned  XUD_LLD_LineMonitor(in port p_ifm_rd, out port p_ifm_wr, chanend c_ctl_io, chanend c_ctl_ep);

// TODO Automate the declaration of enpoint/setup buffers based on EP_COUNT and types

//////////////////////////////////////////////////////////////////////////////////////
// Setup Data buffering
// Setup data buffers for control endpoints, note always 8 byte payload
unsigned char sBuffer_ep0[20];   // 8 byte payload, 2 byte crc

// Datasize table for setup data buffers
// TODO Dont really need this for all EPs
unsigned char sBuffer_Datasize[] = {0,0,0,0,0,0,0,0};

/////////////////////////////////////////////////////////////////////////////////////
// Endpoint buffers (IN and OUT) (double buffer for each):
// Endpoint 0:
unsigned char buffer_ep0_OUT_A[EP_BUFFER_SIZE];
unsigned char buffer_ep0_OUT_B[EP_BUFFER_SIZE];

unsigned char buffer_ep0_IN_A[EP_BUFFER_SIZE];
unsigned char buffer_ep0_IN_B[EP_BUFFER_SIZE];

// Endpoint 1:
unsigned char buffer_ep1_OUT_A[EP_BUFFER_SIZE];
unsigned char buffer_ep1_OUT_B[EP_BUFFER_SIZE];

unsigned char buffer_ep1_IN_A[EP_BUFFER_SIZE];
unsigned char buffer_ep1_IN_B[EP_BUFFER_SIZE];

// Endpoint 2:
unsigned char buffer_ep2_OUT_A[EP_BUFFER_SIZE];
unsigned char buffer_ep2_OUT_B[EP_BUFFER_SIZE];

unsigned char buffer_ep2_IN_A[EP_BUFFER_SIZE];
unsigned char buffer_ep2_IN_B[EP_BUFFER_SIZE];

// Endpoint 3:
unsigned char buffer_ep3_OUT_A[EP_BUFFER_SIZE];
unsigned char buffer_ep3_OUT_B[EP_BUFFER_SIZE];

unsigned char buffer_ep3_IN_A[EP_BUFFER_SIZE];
unsigned char buffer_ep3_IN_B[EP_BUFFER_SIZE];

// Endpoint 4:
unsigned char buffer_ep4_OUT_A[EP_BUFFER_SIZE-500];
unsigned char buffer_ep4_OUT_B[EP_BUFFER_SIZE-500];

unsigned char buffer_ep4_IN_A[EP_BUFFER_SIZE-500];
unsigned char buffer_ep4_IN_B[EP_BUFFER_SIZE-500];

// Endpoints 4:
unsigned char buffer_ep5_OUT_A[EP_BUFFER_SIZE-500];
unsigned char buffer_ep5_OUT_B[EP_BUFFER_SIZE-500];

unsigned char buffer_ep5_IN_A[EP_BUFFER_SIZE-500];
unsigned char buffer_ep5_IN_B[EP_BUFFER_SIZE-500];

// Primary Endpoint status tables (Where OUT IO code writes data to..)
unsigned ep_stat_table_OUT [] = {EP_OK, EP_OK, EP_OK, EP_OK, EP_OK, EP_OK};
unsigned ep_stat_table_IN []  = {EP_OK, EP_OK, EP_OK, EP_OK, EP_OK, EP_OK};

// Secondary endpoint stats tables. (Where endpoint code reads data from...)
unsigned ep_stat_table2_OUT [] = {EP_OK, EP_OK, EP_OK, EP_OK, EP_OK, EP_OK};
unsigned ep_stat_table2_IN [] = {EP_OK, EP_OK, EP_OK, EP_OK, EP_OK, EP_OK};

// Endpoint datasize tables
// Note these could be shorts
unsigned  ep_datasize_table_IN_A[]  = {0, 0, 0, 0, 0, 0, 0};
unsigned  ep_datasize_table_OUT_A[] = {0, 0, 0, 0, 0, 0, 0};
unsigned  ep_datasize_table_IN_B[]  = {0, 0, 0, 0, 0, 0, 0};
unsigned  ep_datasize_table_OUT_B[] = {0, 0, 0, 0, 0, 0, 0};

// Pid sequencing tables.. note currently only supports DATA0/DATA1 sequencing
unsigned char ep_pid_sequence_table_OUT[] = {PID_DATA1, PID_DATA0, PID_DATA0, PID_DATA0, PID_DATA0, PID_DATA0, PID_DATA0, PID_DATA0, PID_DATA0, PID_DATA0, PID_DATA0, PID_DATA0, PID_DATA0, PID_DATA0, PID_DATA0, PID_DATA0};

unsigned char ep_pid_sequence_table_IN_A[] = {PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0};

unsigned char ep_pid_sequence_table_IN_B[] = {PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0};

extern unsigned bufferSelectTable_OUT[16];
extern unsigned bufferSelectTable2_OUT[16];
extern unsigned bufferSelectTable_IN[16];
extern unsigned bufferSelectTable2_IN[16];

// Ports:
extern in  port p_usb_clk;      
extern out port reg_write_port ;
extern in  port reg_read_port  ;
extern in  port flag0_port     ;
extern in  port flag1_port     ;
extern in  port flag2_port     ;
extern out port p_usb_txd       ;
extern   port p_usb_rxd       ;
extern out port p_usb_rst      ; 

extern clock clk;

/** XUD_PhyReset
  * @brief Resets transceiver 
  **/
void XUD_PhyReset(void)
{
  //stp_sus_port <: 0;      // TODO Required?
  clearbuf(p_usb_rxd);
  //clearbuf(stp_sus_port); // TODO Required?

  p_usb_rxd <: 0;            // While in reset, drive 0 on data bus and clear port buffers
  clearbuf(p_usb_rxd);
  p_usb_rst <: 0;

  XUD_Sup_Delay(RESET_TIME);
  p_usb_rst <: 1;
}


// Main XUD loop
static int XUD_Manager_loop(chanend c_ep0_data, XUD_chan epChans[], XUD_EpType epTypeTableOut[], XUD_EpType epTypeTableIn[], chanend c_rate_report)
{
    unsigned tmp;
    chan c_lld_out, c_lld_in, c_lld_ep;
    XUD_EpType epTypeTableIn2[XUD_EP_COUNT], epTypeTableOut2[XUD_EP_COUNT];
    /* Internal control channels (mainly for kill) */
    chan c_ctl_buf, c_ctl_ep, c_ctl_io;

    for(int i = 0; i < XUD_EP_COUNT; i++) {           // TODO: fix this by fixing compiler to accept read-only array
        epTypeTableIn2[i]= epTypeTableIn[i];
        epTypeTableOut2[i]= epTypeTableOut[i];
    }

    /* Enable fast mode on thread */
    set_thread_fast_mode_on();
   
    /* Reset port states */
    set_port_use_on(p_usb_clk);
    set_port_clock(p_usb_clk, clk);
    set_port_use_on(p_usb_txd);
    set_port_use_on(p_usb_rxd);
    set_port_use_on(flag0_port);
    set_port_use_on(flag1_port);
    set_port_use_on(flag2_port);
    set_port_use_on(reg_read_port);
    set_port_use_on(reg_write_port);

    /* Initialise datasize/PID sequence tables etc */ 
    for (int i = 0; i < XUD_EP_COUNT; i++)
    {
        sBuffer_Datasize[i] = 0;
        ep_datasize_table_IN_A[i] = 0;
        ep_datasize_table_OUT_A[i] = 0;
        ep_datasize_table_IN_B[i] =  0;
        ep_datasize_table_OUT_B[i] = 0;
        ep_pid_sequence_table_IN_A[i] = PIDn_DATA1;
        ep_pid_sequence_table_IN_B[i] = PIDn_DATA0;
    }

    /* Reset buffer select/pointer tables */ 
    bufferSelectTable_OUT[0] = GetArrayAddress(buffer_ep0_OUT_A);
    bufferSelectTable2_OUT[0] = GetArrayAddress(buffer_ep0_OUT_B);
    bufferSelectTable_IN[0] = GetArrayAddress(buffer_ep0_IN_A);
    bufferSelectTable2_IN[0] = GetArrayAddress(buffer_ep0_IN_B);
  
     bufferSelectTable_OUT[1] = GetArrayAddress(buffer_ep1_OUT_A);
    bufferSelectTable2_OUT[1] = GetArrayAddress(buffer_ep1_OUT_B);
    bufferSelectTable_IN[1] = GetArrayAddress(buffer_ep1_IN_A);
    bufferSelectTable2_IN[1] = GetArrayAddress(buffer_ep1_IN_B);
  
    bufferSelectTable_OUT[2] = GetArrayAddress(buffer_ep2_OUT_A);
    bufferSelectTable2_OUT[2] = GetArrayAddress(buffer_ep2_OUT_B);
    bufferSelectTable_IN[2] = GetArrayAddress(buffer_ep2_IN_A);
    bufferSelectTable2_IN[2] = GetArrayAddress(buffer_ep2_IN_B);
  
    bufferSelectTable_OUT[3] = GetArrayAddress(buffer_ep3_OUT_A);
    bufferSelectTable2_OUT[3] = GetArrayAddress(buffer_ep3_OUT_B);
    bufferSelectTable_IN[3] = GetArrayAddress(buffer_ep3_IN_A);
    bufferSelectTable2_IN[3] = GetArrayAddress(buffer_ep3_IN_B);
  
    bufferSelectTable_OUT[4] = GetArrayAddress(buffer_ep4_OUT_A);
    bufferSelectTable2_OUT[4] = GetArrayAddress(buffer_ep4_OUT_B);
    bufferSelectTable_IN[4] = GetArrayAddress(buffer_ep4_IN_A);
    bufferSelectTable2_IN[4] = GetArrayAddress(buffer_ep4_IN_B);

    bufferSelectTable_OUT[5] = GetArrayAddress(buffer_ep5_OUT_A);
    bufferSelectTable2_OUT[5] = GetArrayAddress(buffer_ep5_OUT_B);
    bufferSelectTable_IN[5] = GetArrayAddress(buffer_ep5_IN_A);
    bufferSelectTable2_IN[5] = GetArrayAddress(buffer_ep5_IN_B);
  
    /* Reset transceiver */
    XUD_PhyReset();

    /* Wait for USB clock (typically 1ms after reset) */
    p_usb_clk when pinseq(1) :> int _;
    p_usb_clk when pinseq(0) :> int _;
    p_usb_clk when pinseq(1) :> int _;
    p_usb_clk when pinseq(0) :> int _;
   
    /* Configure ports and clock blocks for use with UIFM.  */
    XUD_UIFM_PortConfig(p_usb_clk, reg_write_port, reg_read_port, flag0_port, flag1_port, flag2_port, p_usb_txd, p_usb_rxd) ;

    /*
    Removed 26/04/09 
    clearbuf(reg_write_port);
    clearbuf(reg_read_port);
    clearbuf(flag0_port);
    clearbuf(flag1_port);
    clearbuf(flag2_port);
    clearbuf(p_usb_rxd);
    clearbuf(p_usb_txd);*/
 
  /* Enable UIFM in appropriate mode. Note: Returns value read back */
  XUD_UIFM_Enable(UIFM_MODE);

  /* Set UIFM to DECODE LINESTATE mode (CHKTOKENS disabled since would breaks power signaling) */
  XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_CTRL, 0x4);

  /* Set default device address */
  XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_ADDRESS, 0x0);
 
  /* Go into full speed mode: XcvrSelect and Term Select (and suspend) high */ 
  XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_PHYCON, 0x7);
   
  /* Ensure none of the flags are sticky */   
  XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_STICKY, 0x0); 
   
  /* Do HS device attach and handshaking.  Note: this function modifies flag ports */
  XUD_DeviceAttachHS();
    
  // Set UIFM to CHECK TOKENS mode and enable LINESTATE_DECODE
  // NOTE: Need to do this every iteration since CHKTOK would break power signaling
  XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_CTRL, UIFM_CTRL_CHKTOK | UIFM_CTRL_DECODE_LS);

  // Set flag0_port to NEW_TOKEN (bit 6 of ifm flags)
  XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_FLAG_MASK0, 0x40);   // bit 6

  // Set flag1_port to RX_ACTIVE (bit 1)
  XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_FLAG_MASK1, 0x02);   // bit 1

  // Set flag2_port to RX_ERROR (bit 0)
  XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_FLAG_MASK2, 0x01);   // bit 0 
 
  // Set linestate bits in IFM_FLAGS as sticky (FLAGS_STICKY reg)
  XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_STICKY, 0x38);         // Bits   [5:3]
   								                                    //  [3] J
 								                                    //  [4] K	
                                                                    //  [5] SE0
  // Spawn I/O and buffering threads
  par
  {
    // flag0: Valid token flag
    // flag1: Rx Active
    // flag2: Rx Error
    XUD_LLD_Io(p_usb_rxd, flag1_port, p_usb_txd, flag2_port, flag0_port, c_lld_out, c_lld_in, c_ctl_io, c_ctl_buf,
        epTypeTableOut, epTypeTableIn);

    XUD_LLD_BuffManager(c_lld_out, c_lld_in, c_lld_ep, c_ctl_buf, c_rate_report);

    XUD_LLD_EpManager(c_lld_ep, epChans, c_ctl_ep, epTypeTableOut2, epTypeTableIn2);

    XUD_LLD_LineMonitor(reg_read_port, reg_write_port, c_ctl_io, c_ctl_ep);    
  }

  //printstrln("XUD Dead");

  // Disable UIFM
  tmp = XUD_UIFM_Enable(0);

  // Turn ports off
  set_port_use_off(p_usb_txd);
  set_port_use_off(p_usb_rxd);
  set_port_use_off(flag0_port);
  set_port_use_off(flag1_port);
  set_port_use_off(flag2_port);
  set_port_use_off(reg_read_port);
  set_port_use_off(reg_write_port);
  set_port_use_off(p_usb_clk);
  
 
  return 0;
}

void _userTrapHandleRegister(void);

int XUD_Manager(chanend c_ep0, 
                chanend c_ep1_out, chanend c_ep2_out,  chanend c_ep3_out, chanend c_ep4_out, chanend c_ep5_out, 
                chanend c_ep1_in, chanend c_ep2_in,  chanend c_ep3_in, chanend c_ep4_in, chanend c_ep5_in, 

                out port p, out port p1,
                XUD_EpType epTypeTableOut[], XUD_EpType epTypeTableIn[], chanend c_rate_report)
{
    /* Arrays for channels... */ 
    XUD_chan epChans[XUD_EP_COUNT * 2];

    //_userTrapHandleRegister();

    /* Populate arrays of channels TODO Can we automate this */
    /* TODO Use many to one channel for ep manager */
    epChans[0] = XUD_Sup_GetResourceId(c_ep0);
    epChans[1] = XUD_Sup_GetResourceId(c_ep1_out);
    epChans[2] = XUD_Sup_GetResourceId(c_ep2_out);
    epChans[3] = XUD_Sup_GetResourceId(c_ep3_out);
    epChans[4] = XUD_Sup_GetResourceId(c_ep4_out);
    epChans[5] = XUD_Sup_GetResourceId(c_ep5_out);
    epChans[0 + XUD_EP_COUNT ] = XUD_Sup_GetResourceId(c_ep0);
    epChans[1 + XUD_EP_COUNT ] = XUD_Sup_GetResourceId(c_ep1_in);
    epChans[2 + XUD_EP_COUNT ] = XUD_Sup_GetResourceId(c_ep2_in);
    epChans[3 + XUD_EP_COUNT ] = XUD_Sup_GetResourceId(c_ep3_in);
    epChans[4 + XUD_EP_COUNT ] = XUD_Sup_GetResourceId(c_ep4_in);
    epChans[5 + XUD_EP_COUNT ] = XUD_Sup_GetResourceId(c_ep5_in);


    /* EpTypeTable Checks.  Note, currently this is not too crucial since we only really care if the EP is ISO or not */
    
    /* Check for control on IN/OUT 0 */
    //if(epTypeTableOut[0] != XUD_EPTYPE_CTL || epTypeTableIn[0] != XUD_EPTYPE_CTL)
    //    XUD_Error("XUD_Manager: Ep 0 must be control for IN and OUT");
    
    /* Check that if the required channel has a destination if the EP is marked as in use */
    for( int i = 0; i < XUD_EP_COUNT; i++ )
    {
   //     if( XUD_Sup_getd( epChans[i] )  == 0 && epTypeTableOut[i] != XUD_EPTYPE_DIS )
     //       XUD_Error_hex("XUD_Manager: OUT Ep marked as in use but chanend has no dest: ", i); 
    }
    for( int i = 0; i < XUD_EP_COUNT; i++ )
    {
       // if( XUD_Sup_getd( epChans[i + XUD_EP_COUNT ] )  == 0 && epTypeTableIn[i] != XUD_EPTYPE_DIS )
         //   XUD_Error_hex("XUD_Manager: IN Ep marked as in use but chanend has no dest: ", i); 
    }

    /* Clock reset port from reference clock (required as clkblk 0 running from USB clock) */ 
    set_clock_on(clk);
    set_clock_ref(clk);
    set_port_clock(p_usb_rst, clk);
    start_clock(clk);
 
    /* Run the main XUD loop */
    while(1)
    {
        XUD_Manager_loop(c_ep0, epChans, epTypeTableOut, epTypeTableIn, c_rate_report);
    }

    /* Don't hit */
    return 0;
}


/* Various messages for error cases */
void ERR_BadToken()
{
  printstrln("BAD TOKEN RECEVED");
}

void ERR_BadCrc()
{
  printstrln("BAD DATA CRC");
  while(1);
}

void ERR_SetupBuffFull()
{
  printstrln("SETUP BUFFER FULL");
}

void ERR_UnsupportedToken(unsigned x)
{
  printstr("UNSUPPORTED TOKEN: ");
  printhexln(x);
}

void ERR_BadTxHandshake(unsigned x)
{
  printstr("BAD TX HANDSHAKE: ");
  printhexln(x);
}

void ERR_GotSplit()
{
  printstrln("ERR: Got a split");
}

void ERR_TxHandshakeTimeout()
{
  printstrln("WARNING: TX HANDSHAKE TIMEOUT");
  while(1);
}

void ERR_OutDataTimeout()
{
  printstrln("ERR: Out data timeout");
}

void ERR_EndIn4()
{
  printstrln("ERR: Endin4");
  while(1);
}

void ERR_EndIn5()
{
  printstrln("ERR: Endin5");
  while(1);
}

void ERR_EndIn6()
{
  printstrln("ERR: Endin6");
  while(1);
}

void ERR_EndIn7()
{
  printstrln("ERR: Endin7");
  while(1);
}


void ep4()
{
  printstrln("ep4");
  while(1);
}

void IODead()
{
    printstr("io dead\n");
}
