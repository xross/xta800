#include "print.h"

void softwatch()
{
  volatile int *address = 0x1F124;
  volatile int val;
  
  val = *address;
  while (1)
  {
    while (*address != 0x403);
    
    while (*address == 0x403);
    
    printhexln(*address);
  }
}

void testchan(unsigned c)
{
  if (c & 0xF != 0x2)
  {
    printhexln(c);
  }
}
