#include <xs1_reva.h>
int foo(chanend c){
  int i;
  for(i=0;i<100;i++){
     c <: i;
  }
}

void bar(chanend c){
  int value;
  unsigned time;
  timer t;
  t :> time;
  time += 1000;
  select{
    case t when timerafter(time):> time:
      value +=2000;
      break;
    case c :> value:
      value +=1000;
      break;  
  }
  return;
}

int main()
{
  chan c;
  int i;
  par{
    bar(c);
    foo(c);
  }
  return i;
}

