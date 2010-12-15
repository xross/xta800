/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */

//#define VERBOSE


#ifdef VERBOSE
#include <print.h>
#define   dbgprintstr(a) printstr(a)
#define   dbgprinthex(a) printhex(a)
#define   dbgprinthexln(a) printhexln(a)
#define   dbgprintint(a) printint(a)
#define   dbgprintintln(a) printintln(a)
#else
#define   dbgprinthex(a)
#define   dbgprinthexln(a)
#define   dbgprintstr(a)
#define   dbgprintint(a)
#define   dbgprintintln(a)
#endif
