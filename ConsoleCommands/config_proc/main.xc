#include <xs1.h>

core xdk[8];

__node xdk_node0 = {XS1_G4000A, {xdk[0], xdk[1], xdk[2], xdk[3]}};
__node xdk_node1 = {XS1_G4000A, {xdk[4], xdk[5], xdk[6], xdk[7]}};

__regular network single_xdk[2] = {xdk_node0, xdk_node1};

extern void kickOff( int coreId, chanend c );
extern void passOn( int coreId, chanend inC, chanend outC );
extern void collect( int coreId, chanend c, int testVal );

#define COREUSED_0 0
#define COREUSED_1 1
#define COREUSED_2 2
#define COREUSED_3 3

int main() on single_xdk 
{
  chan c,d,e;
  par
  {
    on xdk[COREUSED_0]: kickOff( COREUSED_0, c );
    on single_xdk[1].c[1]: passOn(  COREUSED_1, c, d );
    on xdk[COREUSED_2]: passOn(  COREUSED_2, d, e );
    on xdk[COREUSED_3]: collect( COREUSED_3, e, 90 );
  }

  return(0);
}

