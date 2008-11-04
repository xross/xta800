#include <xs1.h>

core cores[8];

__regular __network double_xdk[2] =
{
  {XS1_G4A_FB512, {cores[0], cores[1], cores[2], cores[3]}},
  {XS1_G4A_FB512, {cores[4], cores[5], cores[6], cores[7]}} };

extern void kickOff( int coreId, chanend c ); extern void passOn( int coreId, chanend inC, chanend outC ); extern void collect( int coreId, chanend c, int testVal );

int main() on double_xdk
{
  chan c,d,e;
  par
  {
    on cores[0]: kickOff( 0, c );
    on cores[1]: passOn(  1, c, d );
    on cores[5]: passOn(  2, d, e );
    on cores[3]: collect( 3, e, 90 );
  }

  return(0);
}


