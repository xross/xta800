#include <xs1.h>

int main() {
  chan c;
  int x = 0;
  int y = 1;
    
  par
  {
    {
      #pragma xta endpoint "input1"
      c :> x;
      x+= 1;
      
      #pragma xta endpoint "input2"
      c :> x;
      x -= 1;
    }
    {
      c <: y;
      c <: y;
    }
  }

  return x;
}

