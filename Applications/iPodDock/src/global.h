#ifndef GLOBAL_H_
#define GLOBAL_H_

// Global settings and configuration variables

// ---- Board type ----
#include "ipod_dock_1v0.h"
//#include "ipod_dock_1v1.h"

//#define ENFORCE_FAST_MODE

// ---- Release mode: ----
// Debug statements removed, streaming channels used, etc.
//#define RELEASE

// ---- Audio Output mode: ----
// Choose audio output options
//#define I2SOUTPUT
//#define SPDIFOUTPUT
#define I2SOUTPUT_SPDIFOUTPUT

// ---- Wolfson Sonaptic Virtual Surround support ----
// Note: This forces ON streaming channels
//#define WOLFSON_SONAPTIC

// ---- SRS Virtual Surround support ----
// Note: This forces ON streaming channels
//#define SRS

// Number of USB clients for the USB stack
#define NUM_CTRL_CHANENDS 3

// ----------------------------------------------------------------------


#ifdef RELEASE
#define USE_STREAMING_CHANNELS
#undef VERBOSE
#else
#undef USE_STREAMING_CHANNELS
#define VERBOSE
#endif

#if defined WOLFSON_SONAPTIC || defined SRS
#define USE_STREAMING_CHANNELS
#define DSP_ENABLED
#endif

#include <xs1.h>

// ------------------------------------------------------------------------------------------------------------
#include "print.h"
#ifdef VERBOSE
#define vprintstr(a)   printstr(a)
#define vprinthexln(a) printhexln(a)
#define vprintstrln(a) printstrln(a)
#define vprintintln(a) printintln(a)
#else
#define vprintstr(a)
#define vprinthexln(a)
#define vprintstrln(a)
#define vprintintln(a)
#endif

#define vprint(a) { vprintstrln(a); return 1;}
#define vprinti(a) { vprintintln(a); return 1; }

#ifdef __XC__
#ifdef USE_STREAMING_CHANNELS
#define streaming_chanend streaming chanend
#else
#define streaming_chanend chanend
#endif
#else
#define streaming_chanend unsigned
#endif

#endif /*GLOBAL_H_*/
