
//XUD_Defines_Internal.h

#define XUD_CMD_NULL		0x123110F4
#define XUD_CMD_SETUP 		0x123110F0	
#define XUD_CMD_IN    		0x123110F1  		// Endpoint wants to send some data
#define XUD_CMD_IN_RST_PID	0x123110F2		// End point wants to send data and reset PID toggle 
						// to specified value
#define XUD_CMD_OUT		0x123110F3               // Endpoint wants some data
#define XUD_CMD_SETADR		0x123110F7		// Set device address

// Control tokens
#define XUD_CT_RESET 		0x99
//#define XUD_CT_SETADR           0x98
