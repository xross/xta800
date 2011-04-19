
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
    on stdcore[0] : f(1);
    on stdcore[1] : f(1);
    on stdcore[2] : f(1);
    on stdcore[3] : f(1);
    on stdcore[4] : f(1);
    on stdcore[5] : f(1);
    on stdcore[6] : f(1);
    on stdcore[7] : f(1);
    on stdcore[8] : f(1);
    on stdcore[9] : f(1);
    on stdcore[10] : f(1);
    on stdcore[11] : f(1);
    on stdcore[12] : f(1);
    on stdcore[13] : f(1);
    on stdcore[14] : f(1);
    on stdcore[15] : f(1);
    }
  return 0;
}

