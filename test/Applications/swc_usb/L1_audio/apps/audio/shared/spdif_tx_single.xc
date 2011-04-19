///////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2009 XMOS Ltd
//
// spdif_tx.xc
// S/PDIF line transmitter
//

#include <xs1.h>
#include <xclib.h>
#include <print.h>

//#include "interface.h"

static int inline parity32(unsigned x)
{
  crc32(x, 0, 1);
  return (x & 1)^1;
}


unsigned data_words_192_nibble[16] = {
  0x0F0F,
  0xF0F3,
  0xF0CF,
  0x0F33,
  0xF30F,
  0x0CF3,
  0x0CCF,
  0xF333,
  0xCF0F,
  0x30F3,
  0x30CF,
  0xCF33,
  0x330F,
  0xCCF3,
  0xCCCF,
  0x3333
};

unsigned data_words_192[256] = {
  0x0F0F0F0F,
  0xF0F0F0F3,
  0xF0F0F0CF,
  0x0F0F0F33,
  0xF0F0F30F,
  0x0F0F0CF3,
  0x0F0F0CCF,
  0xF0F0F333,
  0xF0F0CF0F,
  0x0F0F30F3,
  0x0F0F30CF,
  0xF0F0CF33,
  0x0F0F330F,
  0xF0F0CCF3,
  0xF0F0CCCF,
  0x0F0F3333,
  0xF0F30F0F,
  0x0F0CF0F3,
  0x0F0CF0CF,
  0xF0F30F33,
  0x0F0CF30F,
  0xF0F30CF3,
  0xF0F30CCF,
  0x0F0CF333,
  0x0F0CCF0F,
  0xF0F330F3,
  0xF0F330CF,
  0x0F0CCF33,
  0xF0F3330F,
  0x0F0CCCF3,
  0x0F0CCCCF,
  0xF0F33333,
  0xF0CF0F0F,
  0x0F30F0F3,
  0x0F30F0CF,
  0xF0CF0F33,
  0x0F30F30F,
  0xF0CF0CF3,
  0xF0CF0CCF,
  0x0F30F333,
  0x0F30CF0F,
  0xF0CF30F3,
  0xF0CF30CF,
  0x0F30CF33,
  0xF0CF330F,
  0x0F30CCF3,
  0x0F30CCCF,
  0xF0CF3333,
  0x0F330F0F,
  0xF0CCF0F3,
  0xF0CCF0CF,
  0x0F330F33,
  0xF0CCF30F,
  0x0F330CF3,
  0x0F330CCF,
  0xF0CCF333,
  0xF0CCCF0F,
  0x0F3330F3,
  0x0F3330CF,
  0xF0CCCF33,
  0x0F33330F,
  0xF0CCCCF3,
  0xF0CCCCCF,
  0x0F333333,
  0xF30F0F0F,
  0x0CF0F0F3,
  0x0CF0F0CF,
  0xF30F0F33,
  0x0CF0F30F,
  0xF30F0CF3,
  0xF30F0CCF,
  0x0CF0F333,
  0x0CF0CF0F,
  0xF30F30F3,
  0xF30F30CF,
  0x0CF0CF33,
  0xF30F330F,
  0x0CF0CCF3,
  0x0CF0CCCF,
  0xF30F3333,
  0x0CF30F0F,
  0xF30CF0F3,
  0xF30CF0CF,
  0x0CF30F33,
  0xF30CF30F,
  0x0CF30CF3,
  0x0CF30CCF,
  0xF30CF333,
  0xF30CCF0F,
  0x0CF330F3,
  0x0CF330CF,
  0xF30CCF33,
  0x0CF3330F,
  0xF30CCCF3,
  0xF30CCCCF,
  0x0CF33333,
  0x0CCF0F0F,
  0xF330F0F3,
  0xF330F0CF,
  0x0CCF0F33,
  0xF330F30F,
  0x0CCF0CF3,
  0x0CCF0CCF,
  0xF330F333,
  0xF330CF0F,
  0x0CCF30F3,
  0x0CCF30CF,
  0xF330CF33,
  0x0CCF330F,
  0xF330CCF3,
  0xF330CCCF,
  0x0CCF3333,
  0xF3330F0F,
  0x0CCCF0F3,
  0x0CCCF0CF,
  0xF3330F33,
  0x0CCCF30F,
  0xF3330CF3,
  0xF3330CCF,
  0x0CCCF333,
  0x0CCCCF0F,
  0xF33330F3,
  0xF33330CF,
  0x0CCCCF33,
  0xF333330F,
  0x0CCCCCF3,
  0x0CCCCCCF,
  0xF3333333,
  0xCF0F0F0F,
  0x30F0F0F3,
  0x30F0F0CF,
  0xCF0F0F33,
  0x30F0F30F,
  0xCF0F0CF3,
  0xCF0F0CCF,
  0x30F0F333,
  0x30F0CF0F,
  0xCF0F30F3,
  0xCF0F30CF,
  0x30F0CF33,
  0xCF0F330F,
  0x30F0CCF3,
  0x30F0CCCF,
  0xCF0F3333,
  0x30F30F0F,
  0xCF0CF0F3,
  0xCF0CF0CF,
  0x30F30F33,
  0xCF0CF30F,
  0x30F30CF3,
  0x30F30CCF,
  0xCF0CF333,
  0xCF0CCF0F,
  0x30F330F3,
  0x30F330CF,
  0xCF0CCF33,
  0x30F3330F,
  0xCF0CCCF3,
  0xCF0CCCCF,
  0x30F33333,
  0x30CF0F0F,
  0xCF30F0F3,
  0xCF30F0CF,
  0x30CF0F33,
  0xCF30F30F,
  0x30CF0CF3,
  0x30CF0CCF,
  0xCF30F333,
  0xCF30CF0F,
  0x30CF30F3,
  0x30CF30CF,
  0xCF30CF33,
  0x30CF330F,
  0xCF30CCF3,
  0xCF30CCCF,
  0x30CF3333,
  0xCF330F0F,
  0x30CCF0F3,
  0x30CCF0CF,
  0xCF330F33,
  0x30CCF30F,
  0xCF330CF3,
  0xCF330CCF,
  0x30CCF333,
  0x30CCCF0F,
  0xCF3330F3,
  0xCF3330CF,
  0x30CCCF33,
  0xCF33330F,
  0x30CCCCF3,
  0x30CCCCCF,
  0xCF333333,
  0x330F0F0F,
  0xCCF0F0F3,
  0xCCF0F0CF,
  0x330F0F33,
  0xCCF0F30F,
  0x330F0CF3,
  0x330F0CCF,
  0xCCF0F333,
  0xCCF0CF0F,
  0x330F30F3,
  0x330F30CF,
  0xCCF0CF33,
  0x330F330F,
  0xCCF0CCF3,
  0xCCF0CCCF,
  0x330F3333,
  0xCCF30F0F,
  0x330CF0F3,
  0x330CF0CF,
  0xCCF30F33,
  0x330CF30F,
  0xCCF30CF3,
  0xCCF30CCF,
  0x330CF333,
  0x330CCF0F,
  0xCCF330F3,
  0xCCF330CF,
  0x330CCF33,
  0xCCF3330F,
  0x330CCCF3,
  0x330CCCCF,
  0xCCF33333,
  0xCCCF0F0F,
  0x3330F0F3,
  0x3330F0CF,
  0xCCCF0F33,
  0x3330F30F,
  0xCCCF0CF3,
  0xCCCF0CCF,
  0x3330F333,
  0x3330CF0F,
  0xCCCF30F3,
  0xCCCF30CF,
  0x3330CF33,
  0xCCCF330F,
  0x3330CCF3,
  0x3330CCCF,
  0xCCCF3333,
  0x33330F0F,
  0xCCCCF0F3,
  0xCCCCF0CF,
  0x33330F33,
  0xCCCCF30F,
  0x33330CF3,
  0x33330CCF,
  0xCCCCF333,
  0xCCCCCF0F,
  0x333330F3,
  0x333330CF,
  0xCCCCCF33,
  0x3333330F,
  0xCCCCCCF3,
  0xCCCCCCCF,
  0x33333333,
};

unsigned preamble_words_192[3] = {
    0x303F, 0x0C3F, 0x033F
};

unsigned data_words_96[16] = {
  0x00FF00FF,
  0xFF00FF0F,
  0xFF00F0FF,
  0x00FF0F0F,
  0xFF0F00FF,
  0x00F0FF0F,
  0x00F0F0FF,
  0xFF0F0F0F,
  0xF0FF00FF,
  0x0F00FF0F,
  0x0F00F0FF,
  0xF0FF0F0F,
  0x0F0F00FF,
  0xF0F0FF0F,
  0xF0F0F0FF,
  0x0F0F0F0F
};

unsigned preamble_words_96[3] = {
    0x0F000FFF, 0x00F00FFF, 0x000F0FFF
};

unsigned data_words_48[32] = {
    0x0000FFFF, 0x0000FFFF,
    0xFFFF0000, 0xFFFF00FF,
    0xFFFF0000, 0xFF00FFFF,
    0x0000FFFF, 0x00FF00FF,
    0xFFFF00FF, 0x0000FFFF,
    0x0000FF00, 0xFFFF00FF,
    0x0000FF00, 0xFF00FFFF,
    0xFFFF00FF, 0x00FF00FF,
    0xFF00FFFF, 0x0000FFFF,
    0x00FF0000, 0xFFFF00FF,
    0x00FF0000, 0xFF00FFFF,
    0xFF00FFFF, 0x00FF00FF,
    0x00FF00FF, 0x0000FFFF,
    0xFF00FF00, 0xFFFF00FF,
    0xFF00FF00, 0xFF00FFFF,
    0x00FF00FF, 0x00FF00FF
};

unsigned preamble_words_48[6] = {
    0x00FF0000, 0x00FFFFFF,
    0x0000FF00, 0x00FFFFFF,
    0x000000FF, 0x00FFFFFF
};




#pragma unsafe arrays
void spdif_tx_192_single(out buffered port:32 p, chanend c_tx0)
{
  unsigned word;
  unsigned xor;
  unsigned encoded_preamble, encoded_byte, encoded_word;

  unsigned sample, control, preamble, parity;
  
  xor = 0x0;
  
  inuint(c_tx0);
  

  sample = inuint(c_tx0) << 4 & 0x0FFFFFF0 ;

#pragma unsafe arrays
  while (1) {
      int controlLeft  = 0x1000204;
      int controlRight = 0x2000204;
      int newblock = 2;
      for (int i = 0 ; i<192; i++) {
        
          // left sample
          /*
          if (testct(c_tx0)) {              // new frequency
              inct(c_tx0);
              inuint(c_tx0);
          }
          */
          control = (controlLeft & 1)<<30;
          preamble = newblock ;
          parity = parity32(sample | control) << 31;
          word = preamble | sample | control | parity | (1<<28);

          //outuint(c_tx, word); // send left sample

          // Preamble
          encoded_preamble = preamble_words_192[word & 0xF];
          encoded_preamble ^= xor;
          p:16 <: encoded_preamble;
          xor = 0xFFFFFFFF * ((encoded_preamble & 0x8000) == 0x8000);  // Replace with sext(encoded_word,1)
          word = word >> 4;

          // Lookup remaining 28 bits, 8/4 bits at a time

          encoded_word = data_words_192[word & 0xFF];
          encoded_word ^= xor;  // Xor to invert data if lsab of last data was a 1
          p <: encoded_word;
          xor = 0xFFFFFFFF * (encoded_word < 0);  // Replace with sext(encoded_word,1)
          word = word >> 8;

          chkct(c_tx0, 1);
          newblock = 0;
          controlLeft >>=1;

          encoded_word = data_words_192[word & 0xFF];
          encoded_word ^= xor;  // Xor to invert data if lsab of last data was a 1
          p <: encoded_word;
          xor = 0xFFFFFFFF * (encoded_word < 0);  // Replace with sext(encoded_word,1)
          word = word >> 8;

          encoded_word = data_words_192_nibble[word & 0xF];
          encoded_word ^= xor;  // Xor to invert data if lsab of last data was a 1
          p:16 <: encoded_word;
          xor = 0xFFFFFFFF * ((encoded_word & 0x8000) != 0);  // Replace with sext(encoded_word,1)
          word = word >> 4;

          sample = inuint(c_tx0) << 4 & 0x0FFFFFF0 ;
          
          encoded_word = data_words_192[word & 0xFF];
          encoded_word ^= xor;  // Xor to invert data if lsab of last data was a 1
          p <: encoded_word;
          xor = 0xFFFFFFFF * (encoded_word < 0);  // Replace with sext(encoded_word,1)

          // Right sample
          /* 
          if (testct(c_tx0)) {
              inct(c_tx0);
              inuint(c_tx0);
              continue;
          }
          */
          control = (controlRight & 1)<<30;
          preamble = (1);
          parity = parity32(sample | control) << 31;
          word = preamble | sample | control | parity | (1<<28);

          //outuint(c_tx0, word); // send right sample

          // Preamble
          encoded_preamble = preamble_words_192[word & 0xF];
          encoded_preamble ^= xor;
          p:16 <: encoded_preamble;
          xor = 0xFFFFFFFF * ((encoded_preamble & 0x8000) == 0x8000);  // Replace with sext(encoded_word,1)
          word = word >> 4;

          // Lookup remaining 28 bits, 8/4 bits at a time

          encoded_word = data_words_192[word & 0xFF];
          encoded_word ^= xor;  // Xor to invert data if lsab of last data was a 1
          p <: encoded_word;
          xor = 0xFFFFFFFF * (encoded_word < 0);  // Replace with sext(encoded_word,1)
          word = word >> 8;

          encoded_word = data_words_192[word & 0xFF];
          encoded_word ^= xor;  // Xor to invert data if lsab of last data was a 1
          p <: encoded_word;
          xor = 0xFFFFFFFF * (encoded_word < 0);  // Replace with sext(encoded_word,1)
          word = word >> 8;

          chkct(c_tx0, 1);
          controlRight >>=1;
          
          encoded_word = data_words_192_nibble[word & 0xF];
          encoded_word ^= xor;  // Xor to invert data if lsab of last data was a 1
          p:16 <: encoded_word;
          xor = 0xFFFFFFFF * ((encoded_word & 0x8000) != 0);  // Replace with sext(encoded_word,1)
          word = word >> 4;

          sample = inuint(c_tx0) << 4 & 0x0FFFFFF0 ;
          
          encoded_word = data_words_192[word & 0xFF];
          encoded_word ^= xor;  // Xor to invert data if lsab of last data was a 1
          p <: encoded_word;
          xor = 0xFFFFFFFF * (encoded_word < 0);  // Replace with sext(encoded_word,1)
      }
  }
}


#pragma unsafe arrays
void spdif_tx_96_single(out buffered port:32 p, chanend c_tx0)
{
  unsigned word;
  unsigned xor;
  unsigned encoded_preamble, encoded_byte;

  unsigned sample, control, preamble, parity;
  
  xor = 0x0;
  
  inuint(c_tx0);
  
#pragma unsafe arrays
  while (1) {
      int controlLeft  = 0x1000204;
      int controlRight = 0x2000204;
      int newblock = 2;
      for (int i = 0 ; i<192; i++) {
        
          // left sample
          if (testct(c_tx0)) {              // new frequency
              inct(c_tx0);
              inuint(c_tx0);
          }
          sample = inuint(c_tx0) << 4 & 0x0FFFFFF0 ;
//          chkct(c_tx0, 1);
          
          control = (controlLeft & 1)<<30;
          preamble = newblock ;
          parity = parity32(sample | control) << 31;
          word = preamble | sample | control | parity | (1<<28);

          // output left sample
          
          // Preamble
          encoded_preamble = preamble_words_96[word & 0xF];
          encoded_preamble ^= xor;
          p <: encoded_preamble;
          xor = 0xFFFFFFFF * ((encoded_preamble & 0x80000000) != 0);  // Replace with sext(encoded_byte,1)
          word = word >> 4;

          newblock = 0;
          controlLeft >>=1;
          
          // Lookup remaining 28 bits, 4 bits at a time
#pragma unsafe arrays
#pragma loop unroll(7)
          for (int i = 0; i < 7; i++)
          {
            encoded_byte = data_words_96[word & 0xF];
            encoded_byte ^= xor;  // Xor to invert data if lsab of last data was a 1
            p <: encoded_byte;
            xor = 0xFFFFFFFF * ((encoded_byte & 0x80000000) != 0);  // Replace with sext(encoded_byte,1)
            word = word >> 4;
          }       
           
          if (testct(c_tx0)) {
              inct(c_tx0);
              inuint(c_tx0);
              continue;
          }
          sample = inuint(c_tx0) << 4 & 0x0FFFFFF0 ;
//          chkct(c_tx0, 1);
          
          control = (controlRight & 1)<<30;
          preamble = (1);
          parity = parity32(sample | control) << 31;
          word = preamble | sample | control | parity | (1<<28);

          // output  right sample

          // Preamble
          encoded_preamble = preamble_words_96[word & 0xF];
          encoded_preamble ^= xor;
          p <: encoded_preamble;
          xor = 0xFFFFFFFF * ((encoded_preamble & 0x80000000) != 0);  // Replace with sext(encoded_byte,1)
          word = word >> 4;

          controlRight >>=1;
          
          // Lookup remaining 28 bits, 4 bits at a time
#pragma unsafe arrays
#pragma loop unroll(7)
          for (int i = 0; i < 7; i++)
          {
            encoded_byte = data_words_96[word & 0xF];
            encoded_byte ^= xor;  // Xor to invert data if lsab of last data was a 1
            p <: encoded_byte;
            xor = 0xFFFFFFFF * ((encoded_byte & 0x80000000) != 0);  // Replace with sext(encoded_byte,1)
            word = word >> 4;
          }          
      }
  }
}




void spdif_tx_48_single(chanend p, chanend c_tx0)
{
  unsigned word;
  unsigned xor;
  unsigned encoded_preamble, encoded_byte;

  unsigned sample, control, preamble, parity, sample2;
  
  xor = 0x0;
  
  inuint(c_tx0);
  outuint(p, 0);
  
#pragma unsafe arrays
  while (1) {
      int controlLeft  = 0x1000204;
      int controlRight = 0x2000204;
      int newblock = 2;
      for (int i = 0 ; i<192; i++) {
        
          if (testct(c_tx0)) {              // new frequency
              chkct(c_tx0, 1);
              outct(p, 1);
              inuint(c_tx0);
              outuint(p, 0);
          }
          sample = inuint(c_tx0) >> 4 & 0x0FFFFFF0 ;
          sample2 = inuint(c_tx0);
          
          control = (controlLeft & 1)<<30;
          preamble = newblock ;
          parity = parity32(sample | control) << 31;
          word = preamble | sample | control | parity | (1<<28);

          // output left sample

          // Preamble
          encoded_preamble = preamble_words_48[(word & 0xF)*2+1];
          encoded_preamble ^= xor;
          outuint(p, encoded_preamble);
          encoded_preamble = preamble_words_48[(word & 0xF)*2];
          encoded_preamble ^= xor;
          outuint(p, encoded_preamble);
          xor = 0xFFFFFFFF * ((encoded_preamble & 0x80000000) != 0);  // Replace with sext(encoded_byte,1)
          word = word >> 4;

          newblock = 0;
          controlLeft >>=1;
          
          // Lookup remaining 28 bits, 4 bits at a time
#pragma unsafe arrays
#pragma loop unroll(7)    
          for (int i = 0; i < 7; i++)
          {
              encoded_byte = data_words_48[(word & 0xF)*2+1];
              encoded_byte ^= xor;  // Xor to invert data if lsab of last data was a 1
              outuint(p, encoded_byte);
              encoded_byte = data_words_48[(word & 0xF) * 2];  
              encoded_byte ^= xor;  // Xor to invert data if lsab of last data was a 1
              outuint(p, encoded_byte);
              xor = 0xFFFFFFFF * ((encoded_byte & 0x80000000) != 0);  // Replace with sext(encoded_byte,1)
              word = word >> 4;
          }

          sample = sample2 >> 4 & 0x0FFFFFF0 ;

          // output right sample
          
          control = (controlRight & 1)<<30;
          preamble = (1);
          parity = parity32(sample | control) << 31;
          word = preamble | sample | control | parity | (1<<28);

          // Preamble
          encoded_preamble = preamble_words_48[(word & 0xF)*2+1];
          encoded_preamble ^= xor;
          outuint(p, encoded_preamble);
          encoded_preamble = preamble_words_48[(word & 0xF)*2];
          encoded_preamble ^= xor;
          outuint(p, encoded_preamble);
          xor = 0xFFFFFFFF * ((encoded_preamble & 0x80000000) != 0);  // Replace with sext(encoded_byte,1)
          word = word >> 4;

          controlRight >>=1;
          
          
          // Lookup remaining 28 bits, 4 bits at a time
#pragma unsafe arrays
#pragma loop unroll(7)    
          for (int i = 0; i < 7; i++)
          {
              encoded_byte = data_words_48[(word & 0xF)*2+1];
              encoded_byte ^= xor;  // Xor to invert data if lsab of last data was a 1
              outuint(p, encoded_byte);
              encoded_byte = data_words_48[(word & 0xF) * 2];
              encoded_byte ^= xor;  // Xor to invert data if lsab of last data was a 1
              xor = 0xFFFFFFFF * ((encoded_byte & 0x80000000) != 0);  // Replace with sext(encoded_byte,1)
              word = word >> 4;
              outuint(p, encoded_byte);
          }
      }
  }
}



void spdif_transmitter(chanend c_in, chanend c_tx)
{
    spdif_tx_48_single(c_tx, c_in);
}


void spdif_transmitterNOT(chanend c_in, chanend c_tx)
{
    while (1) {

        int speed = inuint(c_in);
        int len = speed <= 48000 ? 16 : speed <= 96000 ? 8 : 4;
        outuint(c_tx, 0);
        while (!testct(c_in)) {
            inuint(c_in);
            inuint(c_in);
            for (int i = 0; i < len; i++) {
                outuint(c_tx, 0);
                outuint(c_tx, -1);
            }
        }
        chkct(c_in, 1);
        outct(c_tx, 1);
    }
}
