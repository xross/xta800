#include <xs1.h>

int main()
{
  long time;
  timer t1;
  timer t2;
  timer t3;
  timer t4;
  t1 :> time;
  select{
    case t1 when timerafter(time+100) :> time:
      time +=1000;
      break;
    case t2 when timerafter(time+200) :> time:
      time +=2000;
      select {
        case t3 when timerafter(time+300) :> time:
          time +=2000;
          break;
        case t4 when timerafter(time+400) :> time:
          time +=2000;
          break;
      }
      break;
  }
  return 0;
}

