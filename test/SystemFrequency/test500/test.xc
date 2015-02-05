#include <xs1.h>
#include <platform.h>

int f(int a);

int main()
{
    
  par {
    on tile[0] : f(1);
    on tile[5] : f(1);
    on tile[10] : f(1);
    on tile[15] : f(1);
    }
  return 0;
}

