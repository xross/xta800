/*
 * The copyrights, all other intellectual and industrial
 * property rights are retained by XMOS and/or its licensors.
 * Terms and conditions covering the use of this code can
 * be found in the Xmos End User License Agreement.
 *
 * Copyright XMOS Ltd 2008
 */

#include <xs1.h>
#include <xclib.h>
#include "led.h"
// Gives us FRAME_HEIGHT and FRAME_WIDTH

#define FRAME_SIZE  (FRAME_HEIGHT * FRAME_WIDTH)
#define BUFFER_SIZE (2 * FRAME_SIZE)
#define SWAP(a,b)   {a -= b; b += a; a = b - a;}
#define BUFFER_TYPE unsigned

//----Possible test patterns-----
//#define SINGLELINETEST
//#define SHADETESTX
//#define SHADETESTY
//#define GAMMATEST
extern unsigned char xmossmall_raw[];
#define SHIFT 0x00000100
#define DIV 1
#define LOGO_HEIGHT 64
#define LOGO_WIDTH  32
#define LOGO_YSCALE (LOGO_HEIGHT/FRAME_HEIGHT)
#define LOGO_XSCALE (LOGO_WIDTH/FRAME_WIDTH)

// ------------------------------
#pragma unsafe arrays
void ledbuffer(chanend cIn, chanend cOut)
{
  // Double buffer -- two frames
  // Frame is stored with in columns (original bitmap xy swapped)
  // This allows outputting one column at a time in a simple loop
  BUFFER_TYPE buffer[BUFFER_SIZE];

  unsigned bufswaptriggerN=1, inbufptr=0, outbufptr=FRAME_SIZE;
  
  start_streaming_master(cOut);
  
  // Initialise the buffer to the specified test pattern
  // ---------------------------------------------------
  {
    int ptr = 0;
    for (int buf=0; buf < 2 * FRAME_WIDTH * FRAME_HEIGHT; buf++)
      buffer[buf]=0;
    
#if defined SINGLELINETEST
    for (int buf=0; buf < 2; buf ++)
    {
      for (int x=0; x < FRAME_WIDTH; x++)
      {
        for (int y=0; y < FRAME_HEIGHT; y++)
        {
          buffer[ptr] = 0xFF * (x==0); 
          ptr++;
        }
      }
    }   
#elif defined SHADETESTX
    for (int buf=0; buf < 2; buf ++)
    {
      for (int x=0; x < FRAME_WIDTH; x++)
      {
        for (int y=0; y < FRAME_HEIGHT; y++)
        {
          buffer[ptr] = ( 0xFF * x ) / (FRAME_WIDTH - 1); 
          ptr++;
        }
      }
    }
#elif defined SHADETESTY
    for (int buf=0; buf < 2; buf ++)
    {
      for (int y=0; y < FRAME_HEIGHT; y++)
      {
        for (int x=0; x < FRAME_WIDTH; x++)
        {
          buffer[ptr] = ((0xFF * y) / ((FRAME_HEIGHT - 1))) * SHIFT; 
          ptr++;
        }
      }
    }
#elif defined GAMMATEST
    for (int buf=0; buf < 2; buf ++)
    {
      for (int y=0; y < FRAME_HEIGHT; y++)
      {
        for (int x=0; x < FRAME_WIDTH; x++)
        {
          if (y < FRAME_HEIGHT >> 1)
          {
            buffer[ptr] = ( (GAMMATEST * 255) / 100 ) * SHIFT; 
          }
          else if ((y+x) & 1)
          {
            buffer[ptr] = 0;
          }
          else
          {
            buffer[ptr] = (255/ DIV) * SHIFT;
          }
            
          ptr++;
        }
      }
    }  
#else
    {
      int ptr=0;
      // Load from logo.c
      for (int y=0; y<FRAME_HEIGHT; y++)
      {
        for (int x=0; x<FRAME_WIDTH; x++)
        {
          int ptr2 = ((x * FRAME_HEIGHT * LOGO_XSCALE) + (FRAME_HEIGHT - 1 - y)) * 3 * LOGO_YSCALE;
          
          buffer[ptr] = (xmossmall_raw[ptr2 + 0] <<  8) |
                        (xmossmall_raw[ptr2 + 1] << 16) |
                        (xmossmall_raw[ptr2 + 2] << 24);
          buffer[FRAME_SIZE + ptr] = buffer[ptr];
          ptr++;
        }
      }
    }
#endif
  }
  
  // ---------------------------------------------------
  // Buffer init complete
    
  while (1)
  {
    unsigned pixelptr;
#pragma ordered
    select
    {
      // Sink request
      case inuint_byref(cOut, pixelptr):
        if (pixelptr == -1)
        {
          // End of frame signal from display driver
          // If the source wants us to swap, do so
          if (bufswaptriggerN == 0)
          {
            SWAP(inbufptr, outbufptr);
            bufswaptriggerN=1;
          }
        }
        else
        {
          // Request for data received by display driver
          // Entire column sent in response
          // Move ptr to required frame
#ifdef NOROTATE
          pixelptr *= FRAME_HEIGHT;
          pixelptr += outbufptr;
          for (int i=0; i<FRAME_HEIGHT; i++)
          {
            outuint(cOut, buffer[pixelptr]);
            pixelptr++;
          }
#else
          pixelptr += outbufptr;
          for (int i=0; i<FRAME_HEIGHT; i++)
          {
            outuint(cOut, buffer[pixelptr]); 
            pixelptr += FRAME_WIDTH;
          }
#endif
        }
        break;
      
      // Source dump
      // Guard exists to prevent more data pushed in after frame switch
      case (bufswaptriggerN) => slave
      {
        cIn :> pixelptr;
        if (pixelptr == -1)
        {
          // End of frame signal from source
          // Frame will not be swapped until sink completes also
          bufswaptriggerN = 0;
        }
        else
        {
          // New data from source
          int len;
          cIn :> len;
          
          pixelptr += inbufptr;
          while (len > 0)
          {
            cIn :> buffer[pixelptr];
            pixelptr++;
            len-=3;
          }
        }
      }:
      break;
    }
  }
}
