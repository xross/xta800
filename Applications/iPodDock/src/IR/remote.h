/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2009
 *
 * @ModuleName 	remote
 * @Author 	Corin Rathbone
 * @Date 	20/07/2009
 * @Version 	1.0
 * @Description Apple IR Remote Receiver
 */

#ifndef _REMOTE_H__
#define _REMOTE_H__

#include <remote_cmds.h>
	// Prototype functions
int ir_receive(streaming chanend cOut, in port pIR, int bitoffset, timer t);

#endif  /* _REMOTE_H__ */

