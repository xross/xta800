#ifndef BUFFERMANAGER_H_
#define BUFFERMANAGER_H_

// Guess?
#define MAX_DATA_LEN 64

// ------------------- USB STRUCTURES ---------------------
#include "usb_host.h"

// Word aligned transfer structure
#define TRANS_STRUC_SIZE ((sizeof(UsbTransfer) + 3) & (~0x3))

// TODO: Validate NUM_BUFFERS should always be >= num endpoints
#define NUM_BUFFERS 8

// ------------------- ROUTINES FOR ENDPOINT MANAGER ---------------------

// ep_add: Add a transfer request structure to the tx buffer
#if defined __XC__
int ep_add(char ptr[]);
#elif defined __STDC__
int ep_add(char *ptr);
#endif

// ep_remove: Remove a completed transfer structure from the rx buffer
#if defined __XC__
int ep_remove(char ptr[]);
#elif defined __STDC__
int ep_remove(char *ptr);
#endif

// ------------------- ROUTINES FOR LLD ---------------------

// lld_add: Add a completed transfer structure to the rx buffer
#if defined __XC__
int lld_add(char ptr[]);
#elif defined __STDC__
int lld_add(char *ptr);
#endif

// lld_remove: Remove a transfer request structure from the tx buffer
#if defined __XC__
int lld_remove(char ptr[]);
#elif defined __STDC__
int lld_remove(char *ptr);
#endif

#endif /*BUFFERMANAGER_H_*/



