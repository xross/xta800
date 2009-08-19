
//XUD_Defines_Internal.h

#define XUD_CMD_NULL		4
#define XUD_CMD_SETUP 		0	
#define XUD_CMD_IN    		1  		// Endpoint wants to send some data
#define XUD_CMD_IN_RST_PID	2		// End point wants to send data and reset PID toggle 
						// to specified value
#define XUD_CMD_OUT		3               // Endpoint wants some data
#define XUD_CMD_SETADR		7		// Set device address

// Control tokens
#define XUD_CT_RESET 		0x99
#define XUD_CT_ACK      0x98
//#define XUD_CT_SETADR           0x98
