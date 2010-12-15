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
#include "ledprocess.h"


// Central reprocessing algorithm, using LUT and intensity values
#define process(output, lut, input, intensity) { output = lut[((input & 0xFF) * (intensity + 1)) >> 8]; }
#define pixcorrect(val, x, y) { unsigned r = (val >> 8) & 0xFF;                \
                                unsigned g = (val >> 16) & 0xFF;         \
                                unsigned b = (val >> 24) & 0xFF;        \
                                r = (pixintensity[y][x][0] * r) >> 8;     \
                                g = (pixintensity[y][x][1] * g) >> 8;     \
                                b = (pixintensity[y][x][2] * b) >> 8;     \
                                val = (b << 24) | (g << 16) | (r << 8);       \
                              }

// ledreprocess
// Load pixel data from buffer and apply gamma LUT and intensity colour correction
#pragma unsafe arrays
void ledreprocess(chanend cLedData, chanend cLedCmd, chanend cOut, chanend cWdog)
{
  
  unsigned short buffers[SCAN_RATE][BUFFER_SIZE][MODULE_WIDTH/SCAN_RATE][3];  
  unsigned char pixintensity[FRAME_HEIGHT][FRAME_WIDTH][3];
  unsigned char intensityadjust[3] = {0xFF, 0xFF, 0xFF};
  unsigned short gammaLUT[3][256];
  
  start_streaming_slave(cLedData);
  start_streaming_slave(cLedCmd);
  start_streaming_master(cOut);

  // Init pixintensity
  for (int y=0; y<FRAME_HEIGHT; y++)
    for (int x=0; x<FRAME_WIDTH; x++)
      for (int c=0; c<3; c++)
        pixintensity[y][x][c] = 0xFF;
  
  while (1)
  {
    
    cWdog <: (int)1;
    
    outuint(cLedData, -1);
    for (int scanx = 0; scanx < SCAN_RATE; scanx++)
    {

      // Get new data
      for (int i=0; i < NUM_MODULES_X; i++)
      {
        for (int j=0; j < MODULE_WIDTH/SCAN_RATE; j++)
        {
          // Retrieve pixel data from the buffer
          unsigned x = MODULE_WIDTH*NUM_MODULES_X - (scanx + (i * MODULE_WIDTH) + (j * SCAN_RATE) + 1);
          unsigned tbuf[FRAME_HEIGHT];
          
          {
            unsigned ptr = FRAME_HEIGHT;
            outuint(cLedData, x);
            while (ptr)
            {
              ptr--;
              tbuf[ptr] = inuint(cLedData);
            }
          }
          
          for (int y=0; y<FRAME_HEIGHT; y++)
          {
            for (int channel=0; channel<3; channel++)
            {
              unsigned val;
              unsigned lval;
              
              // Separate r,g,b
              val = tbuf[y];
              val >>= 8;
              lval = val & 0xFF;
              tbuf[y] = val;
              
              // Adjust intensity by fixed offset
              lval *= (intensityadjust[2 - channel] + 1) * (pixintensity[y][x][2 - channel] + 1);
              lval >>= 16;
              
              // Gamma LUT
              lval = gammaLUT[2 - channel][lval];
              // Bitreversed
              lval = bitrev(lval) >> 16;

              // Other reprocessing if required
              
              // Finished processing
              buffers[scanx][y + (i * FRAME_HEIGHT)][j][channel] = lval;
            }
          }
        }
      }
    }

    // Give to driving thread
    outuint(cOut, 1);
    for (int scanx = 0; scanx < SCAN_RATE; scanx++)
    {
      for (int j=0; j < MODULE_WIDTH/SCAN_RATE; j++)
      {
        for (int col=2; col >= 0; col--)
        {
          for (int i=BUFFER_SIZE-1; i>=0; i--)
          {
#if 1
            outuint(cOut, buffers[scanx][i][j][col]);
#elif 0
            outuint(cOut, 1<<6);
#else
            outuint(cOut, 0x100 * (i * 0x20) / BUFFER_SIZE);
#endif
          }
        }
      }
    }
      
    // Check for commands
#ifdef PROCESSCOMMANDS
    outuint(cLedCmd, 0);
    if (inuint(cLedCmd) == 0)
    {
      // New command received
      unsigned int cmdlen, cmdtyp;
      cmdlen = inuint(cLedCmd);
      cmdtyp = inuint(cLedCmd);
      switch (cmdtyp)
      {
        case (XMOS_GAMMAADJ):
        {
          char colchan = (char)inuint(cLedCmd);
          cmdlen -= 2;
          if (cmdlen >= 256)
            cmdlen = 256;
          switch(colchan)
          {
            case ('R'):
              for (int i=0; i < cmdlen; i++)
                gammaLUT[0][i] = (unsigned short)inuint(cLedCmd);
              break;
            case ('G'):
              for (int i=0; i < cmdlen; i++)
                gammaLUT[1][i] = (unsigned short)inuint(cLedCmd);
              break;
            case ('B'):
              for (int i=0; i < cmdlen; i++)
                gammaLUT[2][i] = (unsigned short)inuint(cLedCmd);
              break;
            default:
              for (int i=0; i < cmdlen; i++)
              {
                unsigned short temp = (unsigned short)inuint(cLedCmd); 
                gammaLUT[0][i] = temp; gammaLUT[1][i] = temp; gammaLUT[2][i] = temp;
              }
              break;
          }
        }
        break;
        case (XMOS_CHANGEDRIVER):
        {
          outuint(cOut, 0);
          outuint(cOut, XMOS_CHANGEDRIVER);
          outuint(cOut, inuint(cLedCmd));
        }
        break;
        case (XMOS_INTENSITYADJ):
        {
          char colchan = (char)inuint(cLedCmd);
          unsigned mask;
          outuint(cOut, 0);
          outuint(cOut, XMOS_INTENSITYADJ);
          switch(colchan)
          {
            case ('R'):
              outuint(cOut, 0x09090909);
              break;
            case ('G'):
              outuint(cOut, 0x12121212);
              break;
            case ('B'):
              outuint(cOut, 0x24242424);
              break;
            default:
              outuint(cOut, 0xFFFFFFFF);
              break;
          }
          outuint(cOut, inuint(cLedCmd));
        }
        break;
        case (XMOS_SINTENSITYADJ):
        {
          char colchan = (char)inuint(cLedCmd);
          switch(colchan)
          {
            case ('R'):
              intensityadjust[0] = inuint(cLedCmd);
              break;
            case ('G'):
              intensityadjust[1] = inuint(cLedCmd);
              break;
            case ('B'):
              intensityadjust[2] = inuint(cLedCmd);
              break;
            default:
              intensityadjust[0] = inuint(cLedCmd);
              intensityadjust[1] = intensityadjust[0];
              intensityadjust[2] = intensityadjust[0];
              break;
          }
        }
        break;
        case (XMOS_SINTENSITYADJ_PIX):
        {
          char colchan = (char)inuint(cLedCmd);
          int x,y;
          
          y = inuint(cLedCmd);
          x = inuint(cLedCmd);
          switch(colchan)
          {
            case ('R'):
              pixintensity[y][x][0] = inuint(cLedCmd);
              break;
            case ('G'):
              pixintensity[y][x][1] = inuint(cLedCmd);
              break;
            case ('B'):
              pixintensity[y][x][2] = inuint(cLedCmd);
              break;
            default:
              pixintensity[y][x][0] = inuint(cLedCmd);
              pixintensity[y][x][1] = pixintensity[y][x][0];
              pixintensity[y][x][2] = pixintensity[y][x][0];
              break;
          }
        }
        break;
        default:
          for (int i=1; i < cmdlen; i++)
            inuint(cLedCmd);
          break;
      }
    }
#endif
  }
}