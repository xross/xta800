#include <platform.h>

int test();

int main()
{
  par {
    on stdcore[0]: test();
    on stdcore[1]: test();
  }
  
  return 0;
}
