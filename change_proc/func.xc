#include <stdlib.h>

extern unsigned int getCoreId(void);

void kickOff( int coreId, chanend c )
{
  int a = 10;
  if( getCoreId() != coreId )
  {
    exit(2);
  }
  c <: a;
}

void passOn( int coreId, chanend inC, chanend outC )
{
  int a;
  if( getCoreId() != coreId )
  {
    exit(2);
  }
  inC :> a;
  a *= 3;
  outC <: a;
}

void collect( int coreId, chanend c, int testVal )
{
  int gotVal;
  if( getCoreId() != coreId )
  {
    exit(2);
  }
  c :> gotVal;
  if( testVal != gotVal )
  {
    exit(1);
  }
}


