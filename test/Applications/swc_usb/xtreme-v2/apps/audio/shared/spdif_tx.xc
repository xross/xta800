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

#include "interface.h"

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
void spdif_tx_192(out buffered port:32 p, chanend c_tx)
{
  unsigned word;
  unsigned xor;
  unsigned encoded_preamble;
  int encoded_word;

  xor = 0x0;

#pragma unsafe arrays
  while (1)
  {
      if (testct(c_tx)) {
          inct(c_tx);
          return;
      }
    word = inuint(c_tx);

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

    encoded_word = data_words_192_nibble[word & 0xF];
    encoded_word ^= xor;  // Xor to invert data if lsab of last data was a 1
  p:16 <: encoded_word;
    xor = 0xFFFFFFFF * ((encoded_word & 0x8000) != 0);  // Replace with sext(encoded_word,1)
    word = word >> 4;

    encoded_word = data_words_192[word & 0xFF];
    encoded_word ^= xor;  // Xor to invert data if lsab of last data was a 1
    p <: encoded_word;
    xor = 0xFFFFFFFF * (encoded_word < 0);  // Replace with sext(encoded_word,1)
  }
}

#pragma unsafe arrays
void spdif_tx_96(out buffered port:32 p, chanend c_tx)
{
  unsigned word;
  unsigned xor;
  unsigned encoded_preamble, encoded_byte;

  xor = 0x0;

#pragma unsafe arrays
  while (1)
  {
      if (testct(c_tx)) {
          inct(c_tx);
          return;
      }
    word = inuint(c_tx);

    // Preamble
    encoded_preamble = preamble_words_96[word & 0xF];
    encoded_preamble ^= xor;
    p <: encoded_preamble;
    xor = 0xFFFFFFFF * ((encoded_preamble & 0x80000000) != 0);  // Replace with sext(encoded_byte,1)
    word = word >> 4;

    // Lookup remaining 28 bits, 4 bits at a time
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


// Uses 50Mhz clock - this results in 4x the data of 12.5Mhz.
// This is achieved by performing the data lookup to get 10 bits, then
// splitting that in two and doing a further lookup to get 20bits for each 
// of the 5 bit halves.
void adat_tx_50(out buffered port:32 p_adat_tx, chanend c_dataIn)
{ 
  unsigned int word;
  unsigned int value;
  
  // lookup table from 5 bits -> 20 bits
  unsigned int dataLookupLong[32] = {
      0b00000000000000000000, 0b00000000000000001111, 0b00000000000011110000, 0b00000000000011111111, 
      0b00000000111100000000, 0b00000000111100001111, 0b00000000111111110000, 0b00000000111111111111, 
      0b00001111000000000000, 0b00001111000000001111, 0b00001111000011110000, 0b00001111000011111111, 
      0b00001111111100000000, 0b00001111111100001111, 0b00001111111111110000, 0b00001111111111111111,
      0b11110000000000000000, 0b11110000000000001111, 0b11110000000011110000, 0b11110000000011111111, 
      0b11110000111100000000, 0b11110000111100001111, 0b11110000111111110000, 0b11110000111111111111, 
      0b11111111000000000000, 0b11111111000000001111, 0b11111111000011110000, 0b11111111000011111111, 
      0b11111111111100000000, 0b11111111111100001111, 0b11111111111111110000, 0b11111111111111111111,
  };
  /*
  unsigned int dataLookupLong[32] = {
      0b00000, 0b00001, 0b00010, 0b00011, 0b00100, 0b00101, 0b00110, 0b00111, 
      0b01000, 0b01001, 0b01010, 0b01011, 0b01100, 0b01101, 0b01110, 0b01111,
      0b10000, 0b10001, 0b10010, 0b10011, 0b10100, 0b10101, 0b10110, 0b10111, 
      0b11000, 0b11001, 0b11010, 0b11011, 0b11100, 0b11101, 0b11110, 0b11111,
  };
  */
  
  // lookup table for 8 data bits -> 10 bits
  unsigned int dataLookup[256] = {
      0x1F0, 0x2F0, 0x270, 0x170, 0x230, 0x130, 0x1B0, 0x2B0, 0x210, 0x110, 0x190, 0x290, 0x1D0, 0x2D0, 
      0x250, 0x150, 0x208, 0x108, 0x188, 0x288, 0x1C8, 0x2C8, 0x248, 0x148, 0x1E8, 0x2E8, 0x268, 0x168, 
      0x228, 0x128, 0x1A8, 0x2A8, 0x20C, 0x10C, 0x18C, 0x28C, 0x1CC, 0x2CC, 0x24C, 0x14C, 0x1EC, 0x2EC, 
      0x26C, 0x16C, 0x22C, 0x12C, 0x1AC, 0x2AC, 0x1F4, 0x2F4, 0x274, 0x174, 0x234, 0x134, 0x1B4, 0x2B4,
      0x214, 0x114, 0x194, 0x294, 0x1D4, 0x2D4, 0x254, 0x154, 0x20E, 0x10E, 0x18E, 0x28E, 0x1CE, 0x2CE,
      0x24E, 0x14E, 0x1EE, 0x2EE, 0x26E, 0x16E, 0x22E, 0x12E, 0x1AE, 0x2AE, 0x1F6, 0x2F6, 0x276, 0x176,
      0x236, 0x136, 0x1B6, 0x2B6, 0x216, 0x116, 0x196, 0x296, 0x1D6, 0x2D6, 0x256, 0x156, 0x1F2, 0x2F2,
      0x272, 0x172, 0x232, 0x132, 0x1B2, 0x2B2, 0x212, 0x112, 0x192, 0x292, 0x1D2, 0x2D2, 0x252, 0x152,
      0x20A, 0x10A, 0x18A, 0x28A, 0x1CA, 0x2CA, 0x24A, 0x14A, 0x1EA, 0x2EA, 0x26A, 0x16A, 0x22A, 0x12A,
      0x1AA, 0x2AA, 0x20F, 0x10F, 0x18F, 0x28F, 0x1CF, 0x2CF, 0x24F, 0x14F, 0x1EF, 0x2EF, 0x26F, 0x16F,
      0x22F, 0x12F, 0x1AF, 0x2AF, 0x1F7, 0x2F7, 0x277, 0x177, 0x237, 0x137, 0x1B7, 0x2B7, 0x217, 0x117,
      0x197, 0x297, 0x1D7, 0x2D7, 0x257, 0x157, 0x1F3, 0x2F3, 0x273, 0x173, 0x233, 0x133, 0x1B3, 0x2B3,
      0x213, 0x113, 0x193, 0x293, 0x1D3, 0x2D3, 0x253, 0x153, 0x20B, 0x10B, 0x18B, 0x28B, 0x1CB, 0x2CB,
      0x24B, 0x14B, 0x1EB, 0x2EB, 0x26B, 0x16B, 0x22B, 0x12B, 0x1AB, 0x2AB, 0x1F1, 0x2F1, 0x271, 0x171,
      0x231, 0x131, 0x1B1, 0x2B1, 0x211, 0x111, 0x191, 0x291, 0x1D1, 0x2D1, 0x251, 0x151, 0x209, 0x109,
      0x189, 0x289, 0x1C9, 0x2C9, 0x249, 0x149, 0x1E9, 0x2E9, 0x269, 0x169, 0x229, 0x129, 0x1A9, 0x2A9,
      0x20D, 0x10D, 0x18D, 0x28D, 0x1CD, 0x2CD, 0x24D, 0x14D, 0x1ED, 0x2ED, 0x26D, 0x16D, 0x22D, 0x12D,
      0x1AD, 0x2AD, 0x1F5, 0x2F5, 0x275, 0x175, 0x235, 0x135, 0x1B5, 0x2B5, 0x215, 0x115, 0x195, 0x295,
      0x1D5, 0x2D5, 0x255, 0x155
   };
       
  
/*
  // set clock to 50Mhz 
  stop_clock(spdif_tx_clock);
  set_clock_div(spdif_tx_clock, 1);  // 4=12.5Mhz. 2=25Mhz, 1=50Mhz  
  start_clock(spdif_tx_clock);

  set_port_clock(p_adat_tx, spdif_tx_clock);
  set_port_clock(p_clk, spdif_tx_clock);
  set_port_mode_clock(p_clk);
*/    
/*    
  // data lookup for NRZI. 8 bit lookup produces 10 bits
#pragma unsafe arrays
  for (int i=0; i<256; i++)
  {
    // need to bitrev i here as ADAT data is sent MSB first and we send values  
    // from the table LSB first
    unsigned int bitrev_i = bitrev(i) >> 24;
      
    // add the SYNC bits
    dataLookup[i] = ((bitrev_i & 0xF0) << 1) | (bitrev_i & 0xF) | 0x210;
      
#ifdef _PRINT_TABLE
    printint(i);
    printstr("  ");
    printhex(dataLookup[i]);
#endif
      
    // generate NRZI code by xor-ing each bit with the previous bit
    // last bit of previous value is assumed to be 0 - hence will need to
    // bitwise-not the value if the last bit of the previous word is a 1
      
    lastBit = 0;
    word = 0;
#pragma unsafe arrays
    for (int j=0; j<10; j++)
    {
      lastBit = (dataLookup[i] & 0x1) ^ lastBit;
      dataLookup[i] = dataLookup[i] >> 1;
      word = (word>>1) | (lastBit << 9);
    }
    dataLookup[i] = word;
#ifdef _PRINT_TABLE
    printstr("  ");
    printhex(dataLookup[i]);
    printstr("  ");
    printPortBinary(dataLookup[i], 10);
    printstr("\n");
#endif      
  }
*/  

  // sync with data handling thread
  //c_dataIn :> int tmp;

  //printstr("adat_tx\n");

  value = 0;   
  p_adat_tx <: 0xFFFFFFFF;

#pragma unsafe arrays
  while (1)
  { 
    unsigned int temp;
    
    // sync bits and user bits (assumed user bits all 0)

    if (value & 0x80000) 
    {
      value = 0b11111111111111111111111111111111;
      set_port_shift_count(p_adat_tx, 32);
      p_adat_tx <: value;

#ifdef _PRINT_ADAT_OUTPUT 
      printstr("\n");     
      printPortBinary(value, 32);
#endif            
    
      value = 0b11110000000000000000000011111111;    
      set_port_shift_count(p_adat_tx, 32);
      p_adat_tx <: value;

#ifdef _PRINT_ADAT_OUTPUT 
      printstr(" ");     
      printPortBinary(value, 32);      
      printstr("\n");
#endif      
    }
    else
    {
      value = 0b00000000000000000000000000000000;
      set_port_shift_count(p_adat_tx, 32);
      p_adat_tx <: value;

#ifdef _PRINT_ADAT_OUTPUT 
      printstr("\n");     
      printPortBinary(value, 32);
#endif            
      value = 0b00001111111111111111111100000000;
      set_port_shift_count(p_adat_tx, 32);
      p_adat_tx <: value;

#ifdef _PRINT_ADAT_OUTPUT 
      printstr(" ");     
      printPortBinary(value, 32);      
      printstr("\n");
#endif      
    }   
    
    // shift down 12 so last bit is at bitpos 12
    value = value >> 12;        
 
#pragma unsafe arrays
#pragma loop unroll(8)    
    // output channels
    for (int chanNum=0; chanNum<8; chanNum++)
    {  
      // receive data, then send each of the three bytes 
      if ((chanNum == 0) || (chanNum == 1))
      {
        if (testct(c_dataIn)) {
            inct(c_dataIn);
            return;
        }
        word = inuint(c_dataIn) >> 4;
        
      }
      else
      {
      	word = 0;
      }
      		
        
      // invert lookup value if last bit of previous value was '1'
      if (value & 0x80000)
      {
        value = ~dataLookup[(word>>16) & 0xFF];
      }
      else
      {
        value = dataLookup[(word>>16) & 0xFF];
      }
      
      temp = dataLookupLong[value & 0x1F];
      // output the 10 bits (8 data and 2 SYNC bits)
      set_port_shift_count(p_adat_tx, 20);
      p_adat_tx <: temp;
  
#ifdef _PRINT_ADAT_OUTPUT        
      printPortBinary(temp, 20);
      printstr(" ");
#endif
      temp = dataLookupLong[(value>>5) & 0x1F];
      // output the 10 bits (8 data and 2 SYNC bits)
      set_port_shift_count(p_adat_tx, 20);
      p_adat_tx <: temp;
  
#ifdef _PRINT_ADAT_OUTPUT        
      printPortBinary(temp, 20);
      printstr("\n");
#endif      
      value = temp;
  
      
      if (value & 0x80000)
      {
        value = ~dataLookup[(word>>8) & 0xFF];
      }
      else
      {
        value = dataLookup[(word>>8) & 0xFF];
      }       
      temp = dataLookupLong[value & 0x1F];
      // output the 10 bits (8 data and 2 SYNC bits)
      set_port_shift_count(p_adat_tx, 20);
      p_adat_tx <: temp;
  
#ifdef _PRINT_ADAT_OUTPUT        
      printPortBinary(temp, 20);
      printstr(" ");
#endif
      temp = dataLookupLong[(value>>5) & 0x1F];
      // output the 10 bits (8 data and 2 SYNC bits)
      set_port_shift_count(p_adat_tx, 20);
      p_adat_tx <: temp;
  
#ifdef _PRINT_ADAT_OUTPUT        
      printPortBinary(temp, 20);
      printstr("\n");
#endif      
      value = temp;
      
      if (value & 0x80000)
      {
        value = ~dataLookup[word & 0xFF];
      }
      else
      {
        value = dataLookup[word & 0xFF];
      }
      temp = dataLookupLong[value & 0x1F];
      // output the 10 bits (8 data and 2 SYNC bits)
      set_port_shift_count(p_adat_tx, 20);
      p_adat_tx <: temp;
  
#ifdef _PRINT_ADAT_OUTPUT        
      printPortBinary(temp, 20);
      printstr(" ");
#endif
      temp = dataLookupLong[(value>>5) & 0x1F];
      // output the 10 bits (8 data and 2 SYNC bits)
      set_port_shift_count(p_adat_tx, 20);
      p_adat_tx <: temp;
  
#ifdef _PRINT_ADAT_OUTPUT        
      printPortBinary(temp, 20);
      printstr("\n");
#endif      
      value = temp;      

    }          
  }

}

#ifndef ADAT
void spdif_tx_48(out buffered port:32 p, chanend c_tx)
{
  unsigned word;
  unsigned xor;
  unsigned encoded_preamble, encoded_byte;

  xor = 0x0;

#pragma unsafe arrays
  while (1)
  {
      if (testct(c_tx)) {
          inct(c_tx);
          return;
      }
    word = inuint(c_tx);

    // Preamble
    encoded_preamble = preamble_words_48[(word & 0xF)*2+1];
    encoded_preamble ^= xor;
    p <: encoded_preamble;
    encoded_preamble = preamble_words_48[(word & 0xF)*2];
    encoded_preamble ^= xor;
    p <: encoded_preamble;
    xor = 0xFFFFFFFF * ((encoded_preamble & 0x80000000) != 0);  // Replace with sext(encoded_byte,1)
    word = word >> 4;

    // Lookup remaining 28 bits, 4 bits at a time
    for (int i = 0; i < 7; i++)
    {
        encoded_byte = data_words_48[(word & 0xF)*2+1];
        encoded_byte ^= xor;  // Xor to invert data if lsab of last data was a 1
        p <: encoded_byte;
        encoded_byte = data_words_48[(word & 0xF) * 2];
        encoded_byte ^= xor;  // Xor to invert data if lsab of last data was a 1
        p <: encoded_byte;
        xor = 0xFFFFFFFF * ((encoded_byte & 0x80000000) != 0);  // Replace with sext(encoded_byte,1)
        word = word >> 4;
    }
  }
}
#else

// Call ADAT function
void spdif_tx_48(out buffered port:32 p, chanend c_tx)
{
	adat_tx_50(p, c_tx);
}
#endif


void spdif_transmitter(chanend c_in, chanend c_tx)
{
    while (1) {

#define printstr(X)  ;
        int speed = inuint(c_in);
        int len = speed <= 48000 ? 16 : speed <= 96000 ? 8 : 4
        printstr("SPDIF in\n");
        outuint(c_tx, 0);
        while (!testct(c_in)) {
            inuint(c_in);
            printstr("SPDif got 1\n");
            inuint(c_in);
            printstr("SPDif got 1\n");
            for (int i = 0; i < len; i++) {
                outuint(c_tx, 0);
                outuint(c_tx, -1);
            }
            printstr("SPDif delivered 16\n");
        }
        chkct(c_in, 1);
        printstr("SPDIF Out\n");
        outct(c_tx, 1);
    }
}

void spdif_transmitterNOT(out buffered port:32 p, chanend c_tx)
{
#ifdef FASTMODE
    set_thread_fast_mode_on();
#endif
    while (1) 
    {
        int speed = inuint(c_tx);
        switch(speed) {
        case 44100: 
        case 48000: 
            spdif_tx_48(p, c_tx);
            break;
        case 96000: 
            spdif_tx_96(p, c_tx);
            break;
        case 192000: 
            spdif_tx_192(p, c_tx);
            break;
        }
     //   printintln(speed);
    }
}
