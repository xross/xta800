#include <platform.h>

int test();

int main()
{
  par {
    on tile[0]: test();
    on tile[1]: test();
  }
  
  return 0;
}
