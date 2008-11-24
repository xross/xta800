#include <xs1_reva.h>

int foo(chanend c){
  int i=1;
  c <: i;
}

void bar(chanend c){
  int i;
  c :> i;
}

int main()
{
  chan c;
  par{
    bar(c);
    foo(c);
  }
  return 0;
}

