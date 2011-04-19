
// Received SOF token: Reset suspend/reset detection timer 
Pid_Sof:

    ldw        r10, sp[14]                      // Load timer from stack
    clrsr       0x2
    setc       res[r10], XS1_SETC_COND_NONE    // Read current time
    in         r11, res[r10]                     
    ldw        r9, cp[suspendTimeout]           // Add suspend timeout to current time
    add        r11, r11, r9           
    setd       res[r10], r11                    

    setc       res[r10], XS1_SETC_COND_AFTER    // Re-enable thread interrupts
    setsr 0x2
    bu NextToken
