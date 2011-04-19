
/** XUD_Manager.xc
  * @brief     XMOS USB Driver (XUD) Lite
  * @author    Ross Owen
  * @version   0.1
  **/

#include <xs1.h>
#include <print.h>

#include "xud.h"
#include "XUD_UIFM_Defines.h"
#include "XUD_USB_Defines.h"

#include "XUD_Support.h"
#include "XUD_UIFM_Functions.h"

#include "XUD_DeviceAttach.h"

#ifndef XUD_DEBUG_PRINTS
#define printstrln(x) /**/
#define printstr(x) /**/
#define printhexln(x) /**/
#endif

int XUD_USB_Done = 0;

extern void SetupChannelVectors(XUD_chan chans[], int count);

extern int XUD_LLD_IoLoop(in port rxd_port, in port rxa_port, out port txd_port, in port rxe_port, in port flag0_port, 
    in port, out port, int x,
    XUD_EpType epTypeTableOut[], XUD_EpType epTypeTableIn[], XUD_chan epChans[], int  epCount) ;


// Primary Endpoint status tables (Where OUT IO code writes data to..)
unsigned ep_stat_table_OUT [] = {EP_OK, EP_OK, EP_OK, EP_OK, EP_OK, EP_OK};
unsigned ep_stat_table_IN []  = {EP_OK, EP_OK, EP_OK, EP_OK, EP_OK, EP_OK};

// Secondary endpoint stats tables. (Where endpoint code reads data from...)
unsigned ep_stat_table2_OUT [] = {EP_OK, EP_OK, EP_OK, EP_OK, EP_OK, EP_OK};
unsigned ep_stat_table2_IN [] = {EP_OK, EP_OK, EP_OK, EP_OK, EP_OK, EP_OK};

// Pid sequencing tables.. note currently only supports DATA0/DATA1 sequencing
unsigned char ep_pid_sequence_table_OUT[] = {PID_DATA1, PID_DATA0, PID_DATA0, PID_DATA0, PID_DATA0, PID_DATA0, PID_DATA0, PID_DATA0, PID_DATA0, PID_DATA0, PID_DATA0, PID_DATA0, PID_DATA0, PID_DATA0, PID_DATA0, PID_DATA0};

unsigned char ep_pid_sequence_table_IN_A[] = {PIDn_DATA0, PIDn_DATA0, PIDn_DATA1, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0};
unsigned char ep_pid_sequence_table_IN_B[] = {PIDn_DATA0, PIDn_DATA0, PIDn_DATA1, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0, PIDn_DATA0};

/* Ports: */
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

unsigned chanArray;

// Main XUD loop
static int XUD_Manager_loop(chanend c_ep0_data, XUD_chan epChans[],  chanend c_sof, XUD_EpType epTypeTableOut[], XUD_EpType epTypeTableIn[])
{
    unsigned tmp;
    int reset = 1;            /* Flag for if device is returning from a reset */
    int suspend = 0;          /* Flag if device is returning from suspend */

    /* Enable fast mode on thread */
    set_thread_fast_mode_on();

    /* Setup channel event vectors */
    SetupChannelVectors(epChans, XUD_EP_COUNT);
    
    /* Main IO loop */ 
    while(!XUD_USB_Done)
    {
        /* Test if comming back from reset or suspend */
        if(reset)
        {
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

            /* Initialise PID sequence tables etc */ 
            for (int i = 0; i < XUD_EP_COUNT; i++)
            {
                ep_pid_sequence_table_IN_A[i] = PIDn_DATA0;
                ep_pid_sequence_table_IN_B[i] = PIDn_DATA0;
            }

            /* Reset transceiver */
            XUD_PhyReset();

            /* Wait for USB clock (typically 1ms after reset) */
            p_usb_clk when pinseq(1) :> int _;
            p_usb_clk when pinseq(0) :> int _;
            p_usb_clk when pinseq(1) :> int _;
            p_usb_clk when pinseq(0) :> int _;
   
            /* Configure ports and clock blocks for use with UIFM.  TODO: Is this really required every reset? */
             XUD_UIFM_PortConfig(p_usb_clk, reg_write_port, reg_read_port, flag0_port, flag1_port, flag2_port, p_usb_txd, p_usb_rxd) ;

 
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
    
            /* Set UIFM to CHECK TOKENS mode and enable LINESTATE_DECODE
               NOTE: Need to do this every iteration since CHKTOK would break power signaling */
            XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_CTRL, UIFM_CTRL_CHKTOK | UIFM_CTRL_DECODE_LS);

            /* Set flag0_port to NEW_TOKEN (bit 6 of ifm flags) */
            XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_FLAG_MASK0, 0x40);   // bit 6

            // Set flag1_port to RX_ACTIVE (bit 1)
            XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_FLAG_MASK1, 0x02);   // bit 1

            // Set flag2_port to RX_ERROR (bit 0)
            XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_FLAG_MASK2, 0x01);   // bit 0 
 
            /* Set linestate bits in IFM_FLAGS as sticky (FLAGS_STICKY reg) */
            XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_STICKY, 0x38);       // Bits   [5:3]
   								                                            //        [3] J
 								                                            //        [4] K	
                                                                            //        [5] SE0
    
            /* Allow SOF tokens through */
            XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_MISC, UIFM_MISC_SOFISTOKEN);      

        }
    
        /* Run main asm IO loop 
           flag0: Valid token flag
           flag1: Rx Active
           flag2: Rx Error */
        suspend = XUD_LLD_IoLoop(p_usb_rxd,  flag1_port, p_usb_txd, flag2_port,  flag0_port, reg_read_port, 
                reg_write_port, 0, epTypeTableOut, epTypeTableIn, epChans, XUD_EP_COUNT) ;
    
        /* When the function above exits suspend/reset condition has been detected */
        if(suspend)
        {
            /* TODO Clock down */
        
            /* Whilst in suspend there is a FS J (HS K) state on bus , we should look for HS J (resume) or SE0 (reset) */
            /* TODO currently we poll flags reg here, but to save power lets setup the flag ports and use events...*/
                  
            /* Clear flags reg just to make sure... */          
            XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_FLAGS, 0);  // TODO: should read and only update correct bit
                  
            while(1)
            {
                /* Read flags reg... */
                tmp = XUD_UIFM_RegRead(reg_write_port, reg_read_port, UIFM_REG_FLAGS);

                /* Look for HS J */
                if (tmp & UIFM_FLAGS_J)
                {
                    /* Resume detected from suspend: switch back to HS (suspendm high) and continue...*/
                    //XUD_UIFM_RegWrite(reg_write_port, UIFM_REG_PHYCON, 0x1);
                    reset = 0;
                }
                else if(tmp & UIFM_FLAGS_SE0)
                {
                     /* Detected reset from suspend, just fall through and do normal reset */
                    reset = 1;
                    break;
                }
            }
        }
        else 
        {
            /* Got reset... */
            reset = 1;

            /* Disable UIFM */
            tmp = XUD_UIFM_Enable(0);

            /* Turn ports off */
            set_port_use_off(p_usb_txd);
            set_port_use_off(p_usb_rxd);
            set_port_use_off(flag0_port);
            set_port_use_off(flag1_port);
            set_port_use_off(flag2_port);
            set_port_use_off(reg_read_port);
            set_port_use_off(reg_write_port);
            set_port_use_off(p_usb_clk);
        }
    }
    return 0;
}

void _userTrapHandleRegister(void);

int XUD_Manager(chanend c_ep0_out, chanend c_ep0_in, 
                    chanend c_ep1_out, chanend c_ep1_in, 
                    chanend c_ep2_out, chanend c_ep2_in, 
                    chanend c_ep3_out, chanend c_ep3_in, 
                    //chanend c_ep4_out, chanend c_ep4_in,  
                    chanend c_sof, 
                    XUD_EpType epTypeTableOut[], XUD_EpType epTypeTableIn[])
{
    /* Arrays for channels... */ 
    XUD_chan epChans[XUD_EP_COUNT * 2];

    //_userTrapHandleRegister();

    XUD_USB_Done = 0;

    /* Populate arrays of channels TODO Can we automate this */
    /* TODO Use many to one channel for ep manager */
    epChans[0] = XUD_Sup_GetResourceId(c_ep0_out);
    epChans[1] = XUD_Sup_GetResourceId(c_ep1_out);
    epChans[2] = XUD_Sup_GetResourceId(c_ep2_out);
    epChans[3] = XUD_Sup_GetResourceId(c_ep3_out);
    //epChans[4] = XUD_Sup_GetResourceId(c_ep4_out);
    //epChans[5] = XUD_Sup_GetResourceId(c_ep5_out);
    epChans[XUD_EP_COUNT ] = XUD_Sup_GetResourceId(c_ep0_in);
    epChans[1 + XUD_EP_COUNT ] = XUD_Sup_GetResourceId(c_ep1_in);
    epChans[2 + XUD_EP_COUNT ] = XUD_Sup_GetResourceId(c_ep2_in);
    epChans[3 + XUD_EP_COUNT ] = XUD_Sup_GetResourceId(c_ep3_in);
    //epChans[4 + XUD_EP_COUNT ] = XUD_Sup_GetResourceId(c_ep4_in);
    //epChans[5 + XUD_EP_COUNT ] = XUD_Sup_GetResourceId(c_ep5_in);


    /* EpTypeTable Checks.  Note, currently this is not too crucial since we only really care if the EP is ISO or not */
    
    /* Check for control on IN/OUT 0 */
    if(epTypeTableOut[0] != XUD_EPTYPE_CTL || epTypeTableIn[0] != XUD_EPTYPE_CTL)
        XUD_Error("XUD_Manager: Ep 0 must be control for IN and OUT");
    
    /* Check that if the required channel has a destination if the EP is marked as in use */
    for( int i = 0; i < XUD_EP_COUNT; i++ )
    {
        if( XUD_Sup_getd( epChans[i] )  == 0 && epTypeTableOut[i] != XUD_EPTYPE_DIS )
            XUD_Error_hex("XUD_Manager: OUT Ep marked as in use but chanend has no dest: ", i); 
    }
    
    for( int i = 0; i < XUD_EP_COUNT; i++ )
    {
        if( XUD_Sup_getd( epChans[i + XUD_EP_COUNT ] )  == 0 && epTypeTableIn[i] != XUD_EPTYPE_DIS )
            XUD_Error_hex("XUD_Manager: IN Ep marked as in use but chanend has no dest: ", i); 
    }

    /* Clock reset port from reference clock (required as clkblk 0 running from USB clock) */ 
    set_clock_on(clk);
    set_clock_ref(clk);
    set_port_clock(p_usb_rst, clk);
    start_clock(clk);
 
    /* Run the main XUD loop */
    XUD_Manager_loop(c_ep0_out, epChans, c_sof, epTypeTableOut, epTypeTableIn);

    // TODO --- Could do with a cleaner mechanism for this cleaning up all endpoints
    // If the global variable XUD_USB_Done is set the manager loop will exit and return us to main()
    // This is pretty nasty, required to clean up the endpoint channels so they can be free'd by the normal exit from main
    outuint(c_ep0_out, -1);
    outuint(c_ep0_out, -1);

    outct(c_ep0_out, XS1_CT_END);
    outct(c_ep1_out, XS1_CT_END);
    outct(c_ep2_in, XS1_CT_END);
    outct(c_ep0_in, XS1_CT_END);

    while (!testct(c_ep0_out)) 
      inuchar(c_ep0_out);
    while (!testct(c_ep0_out))
      inuchar(c_ep0_out);
    while (!testct(c_ep1_out))
      inuchar(c_ep1_out);
    while (!testct(c_ep2_in))
      inuchar(c_ep2_in);
    while (!testct(c_ep2_in))
      inuchar(c_ep0_in);

    chkct(c_ep0_out, XS1_CT_END);
    chkct(c_ep1_out, XS1_CT_END);
    chkct(c_ep2_in, XS1_CT_END);
    chkct(c_ep0_in, XS1_CT_END);
    
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

void ERR_EndIn5(int x)
{
  printhex(x);
  //printstrln(" ERR: Endin5");
  while(1);
}

void ResetDetected(int x)
{
    printint(x);
    printstr(" rrrreeeset\n");
    
}

void SuspendDetected()
{
    printstr("Suspend!\n");
}
