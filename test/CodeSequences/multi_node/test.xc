
#include <xs1.h>
#include <platform.h>

int f(int a) {
  if (a == 0)
    return 2;
  else
    return 5;
}

int main()
{
    
  par {
    on tile[0] : f(1);
    on tile[1] : f(1);
    on tile[2] : f(1);
    on tile[3] : f(1);
    on tile[4] : f(1);
    on tile[5] : f(1);
    on tile[6] : f(1);
    on tile[7] : f(1);
    on tile[8] : f(1);
    on tile[9] : f(1);
    on tile[10] : f(1);
    on tile[11] : f(1);
    on tile[12] : f(1);
    on tile[13] : f(1);
    on tile[14] : f(1);
    on tile[15] : f(1);
    }
  return 0;
}

