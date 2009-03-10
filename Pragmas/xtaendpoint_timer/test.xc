#include <xs1.h>

int main() {
  timer tmr1;
  timer tmr2;
  
  int x = 0;
    
  #pragma xta endpoint "input1" 
  tmr1 :> x;
    
  tmr2 :> x;

  tmr1 :> x;

  #pragma xta endpoint "input2" 
  tmr2 :> x;

  return x;
}

