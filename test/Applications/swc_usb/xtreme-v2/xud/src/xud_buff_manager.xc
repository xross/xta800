
#include <xs1.h>
#include <print.h>

#include "XUD_Defines_Internal.h"
#include "XUD_Support.h"

extern unsigned ep_datasize_table_IN_A[];
extern unsigned ep_datasize_table_IN_B[];
extern unsigned ep_datasize_table_OUT_A[];
extern unsigned ep_datasize_table_OUT_B[];

extern unsigned char buffer_ep1_OUT_A[];

extern unsigned bufferSelectTable_IN[];
extern unsigned bufferSelectTable2_IN[];

extern unsigned char ep_pid_sequence_table_IN_A[];
extern unsigned char ep_pid_sequence_table_IN_B[];

extern void swapBufferPointers_IN(unsigned epNum);
extern void swapBufferPointers_OUT(unsigned epNum);

extern void setDatasizeAtoB_IN(unsigned epNum);
extern void SetDatasizeBtoA_OUT(unsigned epNum);

extern unsigned getUint(unsigned address, int a);
extern void     setUint(unsigned address, int a, int b);

extern unsigned GetDatasize_IN_B(unsigned epNum);
extern void setPidAtoB_IN(unsigned epNum);
unsigned getPidSeq_IN_B(unsigned epNum);
unsigned GetDatasize_IN_B(unsigned epNum);
unsigned GetDatasize_IN_A(unsigned epNum);
unsigned GetDatasize_OUT_B(unsigned epNum);
void SetDatasize_OUT_B(unsigned epNum, unsigned x);


void KillBuff(chanend c_empty, chanend c_full, chanend c_ep)
{
    while(!testct(c_full))
        inuchar(c_full);

    chkct(c_empty, XS1_CT_END);

    chkct(c_full, XS1_CT_END);

  
  /* Pass on kill to Endpoint manager */
  outct(c_ep, XS1_CT_END);
  chkct(c_ep, XS1_CT_END); 
}

{int , int } XUD_LLD_BuffMan(chanend c_out, chanend c_in, chanend c_ep, chanend c_rate_report);

void XUD_LLD_BuffManager(chanend c_out, chanend c_in, chanend c_ep, chanend c_ctl_buf, chanend c_rate_report)
{

    //while(1)
    {

        XUD_LLD_BuffMan(c_out, c_in, c_ep, c_rate_report);



    }
}


#if 0
#pragma ordered //(give priority to io)
        switch(sel)
        {
            // IO has received data into buffer A (and sent EP number and datalength
            case 0: 
            {
            inuint_byref(c_out, datalength);
           
            
                //datalength = datalength >> 8;

                /* Check for kill */
                if(datalength == -1 ) 
                {
                    KillBuff(c_out, c_in, c_ep);
                    return;
                }
            
                epNum = inuint(c_out);

                if(epNum == -1 ) 
                {
                    KillBuff(c_out, c_in, c_ep);
                    return;
                }
         
	            /* Set datasize */
	            ep_datasize_table_OUT_A[epNum] = datalength ; 
	 
                /* Check if buffer B is free */
	            if(GetDatasize_OUT_B((unsigned) epNum) == 0)
	            {

                    /* Swap buffer pointers */ 
	                swapBufferPointers_OUT((unsigned) epNum);

	                /* Set datasize of buffer B to buffer A */
	                /* Reset buffer A datasize to 0 */
	                SetDatasizeBtoA_OUT((unsigned)epNum); 

	            }
	            break;
            }
       
            // IO has sent data from buffer A
            case 1:
            {
            #if 0
                inuint_byref(c_in, epNum);
            
                if(epNum == -1 ) 
                {
                    KillBuff(c_in, c_out, c_ep);
                    return;
                }

                // Reset datasize NOTE: Do this in IO thread?
                ep_datasize_table_IN_A[epNum] = 0;
         
                // Check if data ready to go in buffer B, if so then do the buffer swap
                if(GetDatasize_IN_B((unsigned) epNum) != 0)
                {
 
                    // Swap buffer pointers
                    swapBufferPointers_IN((unsigned) epNum);

                    // Set PidA[epNum] to PidB[epNum]
                    setPidAtoB_IN((unsigned)epNum);

                    // Set buffer A datasize to buffer B datasize
                    // Reset buffer B data size to 0
                    setDatasizeAtoB_IN((unsigned)epNum);	
          
                }
                break;
            }
   
            // EP Manager has emptied or filled buffer B
            case 2:
            {


                    //case (XUD_CMD_IN):
	                //{

                        // Check if A buffer is free
                        if(ep_datasize_table_IN_A[epNum] == 0)
	                    {

                            // Swap buffer pointers (handcoded to avoid disjointness checks)
                            swapBufferPointers_IN(epNum);

                            // Swap the PIDs (handcode for disjoint checks...) 
                            //ep_pid_sequence_table_IN_A[epNum] = ep_pid_sequence_table_IN_B[epNum];
                            setPidAtoB_IN(epNum);
 
                            // Set data length of data A (handcode for disjoint...)
                            //ep_datasize_table_IN_A[epNum]  = ep_datasize_table_IN_B[epNum];
                            //ep_datasize_table_IN_B[epNum] = 0;
                            setDatasizeAtoB_IN(epNum);	// Note, this also resets B datasize to 0
                        }
                        //else
                        //{
                        // There is data already in buffer A, leave the data buffer B, 
                        // when the IO thread empties A, this thread will check to for data in B
                        //}
                    //}
	                    break;
                }
                case 3:
                {
	                  //case(XUD_CMD_OUT):
	                  //{
	                      // Check if data in buffer A
                        if(ep_datasize_table_OUT_A[(unsigned)epNum] != 0)
	                      {
                            // Swap buffer pointers 
                            swapBufferPointers_OUT((unsigned)epNum);

                            // Set B's datasize (and reset A);
                            SetDatasizeBtoA_OUT((unsigned)epNum);
                        }
                        else
                        {
                            //if(epNum == 1)
                            //printstrln("no data in a");
                            // No data read in A, so just reset B datasize
                            SetDatasize_OUT_B((unsigned)epNum, 0);
                        }
	                  //}

	                      break;
          
	              }
            //break;
        }
  
       /* Kill command from IO thread */
       //case inct_byref(c_ctl_buf, ccmd):
	       //   threadActive = 0;          
	         // break; 
  }

#endif
    //printstr("buffer man dead\n");

 }
#endif
