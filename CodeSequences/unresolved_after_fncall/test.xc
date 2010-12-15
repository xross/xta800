#include <xs1.h>

in port p = XS1_PORT_1A;
out port q = XS1_PORT_1B;

select selectFunction(in port p, out port q)
{
  case p :> int t:
    q <: t;
    break;
}

int foo(){
  return 0;
}

int main(){
  timer t;
  unsigned s;
  int counter = 0;
  
  t :> s;
  foo();
  while (1) {
    select {
      case selectFunction(p, q);

      case t when timerafter(s) :> void:
        counter++;
        s += 10000;
      break;
    }
  }
  
  return counter == 0;
}
