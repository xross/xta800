/*
 * @ModuleName Trap handler for XDK which sets LED
 * @Author Ali Dixon
 * @Date 07/04/2008
 * @Version 1.0
 * @Description: Low level assembler support.
 *
 *
 * Copyright XMOS Ltd 2008
 */

#ifndef _LED_TRAP_HANDLER_H_
#define _LED_TRAP_HANDLER_H_ 1
 

/** This register user trap handler, which is implemented inside.
 */
void _userTrapHandleRegister(void);
 
/** Test to invoke trap.
 *  NOTE: Do not return from trap.
 */
void _userTrapInvoke(void);

/** Test to invoke trap.
 *  NOTE: Do not return from trap.
 */
#define ThrowException _userTrapInvoke



#endif  // _LED_TRAP_HANDLER_H_
