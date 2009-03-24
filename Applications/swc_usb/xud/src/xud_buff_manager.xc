
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




void XUD_LLD_BuffManager(chanend c_out, chanend c_in, chanend c_ep, chanend c_con_buff)
{
  unsigned epNum, datalength;
  unsigned cmd;
  unsigned char ccmd;
  int threadActive = 1;

  start_streaming_master(c_ep);

  while(threadActive)
  {
     // TODO use tokens (fall-stream-tokens)
     select
     {
       // IO has received data into buffer A (and sent EP number and datalength
       case inuint_byref(c_out, datalength):
       {
         epNum = inuint(c_out);
	
         /*if(epNum == 1)
       {tmpz[counttt] = datalength;
      counttt++;}
      if(counttt==100)
      {
        for (int i = 1; i< 100; i++)
          printintln(tmpz[i]);
      }*/



	 // Set datasize 
	 ep_datasize_table_OUT_A[epNum] = datalength ; // TODO there is no need for this +1 since datalength is alwats >=2

	 // Check if buffer B is free
	 if(GetDatasize_OUT_B((unsigned) epNum) == 0)
	 {
           //printstrln("buffer b is free");

           // Swap buffer pointers 
	   swapBufferPointers_OUT((unsigned) epNum);

	   // Set datasize of buffer B to buffer A
	   // Reset buffer A datasize to 0
	   SetDatasizeBtoA_OUT((unsigned)epNum); 

 
	 }
	 //else printstrln("buffer b not free\n");
	 break;
       }
       
       // IO has sent data from buffer A
       case inuint_byref(c_in, epNum):
       {
         // Reset datasize NOTE: Do this in IO thread?
         ep_datasize_table_IN_A[epNum] = 0;
         
         // Check if data ready to go in buffer B, if so then do the buffer swap
         if(GetDatasize_IN_B((unsigned) epNum) != 0)
         {
           //if(GetDatasize_IN_B(epNum) == 3 + 1)
             //printintln(GetDatasize_IN_B(epNum));
 
           // Swap buffer pointers
           swapBufferPointers_IN((unsigned) epNum);

           // Set PidA[epNum] to PidB[epNum]
           setPidAtoB_IN((unsigned)epNum);

           // Set buffer A datasize to buffer B datasize
           // Reset buffer B data size to 0
           setDatasizeAtoB_IN((unsigned)epNum);	// Note, this also resets B datasize to 0
           //XUD_Sup_Delay(1000000);
          
           //printstr("reset b");
         }      
         break;
       }
   
       // EP Manager has emptied or filled buffer B
       case inuint_byref(c_ep, cmd): 
       {
         inuint_byref(c_ep, epNum);

	 switch(cmd)
	 {
           case (XUD_CMD_IN):
	   {
             // Check if A buffer is free
             if(ep_datasize_table_IN_A[epNum] == 0)
	     {
               //if(GetDatasize_IN_B(epNum) == 3 + 1)
                 //printintln(GetDatasize_IN_A(epNum));

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
             // There is data already in buffer A, leave the data buffer B, when the IO thread empties A, this 
             // thread will check to for data in B
             //}
	     break;
	   }
	   case(XUD_CMD_OUT):
	   {
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

	     break;
	   }
          
	 }
         break;
       }
  
       // Linestate issues a cmd:
       case inct_byref(c_con_buff, ccmd):
	 threadActive = 0;          
	 break; 
	 // {
//         switch(ccmd)
//         {
           // Kill Thread
           //case XUD_CT_RESET:
         //}
         //break;
       //}
     }
  }

  // Channel shutdown
  inct(c_con_buff);
  inct(c_out);
  inct(c_in);
  
  //outuint(c_hack, 0x80005678);
  //stop_streaming_master(c_hack);

  //utct(c_ep, XS1_CT_END); 
  stop_streaming_master(c_ep);
}
