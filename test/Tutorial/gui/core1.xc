/*
 * A simple program to explain some of the basic concepts of the XTA tool
 *
 */
#include <xs1.h>
#include <print.h>
#include <platform.h>

#define STATE_IDLE   0x1
#define STATE_ACTIVE 0x2
#define STATE_DONE   0x4
#define STATE_ERROR  0x8

#define DATA_MASK    0x00ffffff
#define TOKEN_START  0xff000000
#define TOKEN_ACK    0xfe000000
#define TOKEN_RESET  0xfd000000

#define BITS_PER_WORD 24
#define MAX_ERRORS 10

on tile[1] : out port statusP = XS1_PORT_4A;

unsigned int checkWord(unsigned int data)
{
  unsigned int check = 0;
  for (int bit = 0; bit < BITS_PER_WORD; bit++) {
    if (data & 1)
      check /= 13;
    else
      check += 50;
    data >>= 1;
  }
  return (check > 5);
}

int stateMachine(int state, unsigned int data, unsigned int &errorCount) {
  int nextState = state;
  switch (state) {
    case STATE_IDLE:
      if (data == TOKEN_START) {
        nextState = STATE_ACTIVE;
        errorCount = 0;
      }
      break;

    case STATE_ACTIVE:
      if (data & ~DATA_MASK) {
        nextState = STATE_ERROR;
      } else {
        errorCount += checkWord(data);
        if (errorCount > MAX_ERRORS)
          nextState = STATE_ERROR;
      }
      break;

    case STATE_DONE:
      if (data == TOKEN_ACK)
        nextState = STATE_IDLE;
      break;

    case STATE_ERROR:
      if (data == TOKEN_RESET)
        nextState = STATE_IDLE;
      else
        nextState = STATE_ERROR;
      break;

    default:
      nextState = STATE_ERROR;
      printstrln("ERROR: Unknown state found");
      break;
  }
    
  return nextState;
}

int processData(streaming chanend c) {
  int state = STATE_IDLE;


  while(1) {
    unsigned int errorCount;
    unsigned int data;
    c :> data;
    state = stateMachine(state, data, errorCount);
    statusP <: state;
  }
  return 0;
}
