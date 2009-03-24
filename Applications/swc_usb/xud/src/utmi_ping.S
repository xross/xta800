// r10: EP number
// PING -----------------------------------------------------------------------------

.align 4
.skip 0
Pid_Ping:

LoadStatTablePing:
  //bl           pinger 
  //ldaw         r6, dp[ep_datasize_table_OUT_A]
  
LoadEPStatus_PING:
  //ldw          r4, r6[r10]
  //eq         r4, r4, EP_OUT_IDLE
  //bt           r4, Err_EndpointProblem_PING

CheckSpaceAvail_PING:
  ldw          r5, r5[r10]
  //mov          r0, r5
  //bl printintln
  bt           r5, PrimaryBufferFull_PING
  
PrimaryBufferEmpty_PING:               // Send ACK
  ldc          r11, PIDn_ACK
  outpw        res[TXD], r11, 8
  bu           Return_PING 


PrimaryBufferFull_PING:
  ldc          r11, PIDn_NAK
  outpw        res[TXD], r11, 8
  bu           Return_PING


Err_EndpointProblem_PING:
  // TODO: Send stall



Return_PING:
  bu NextToken
 
