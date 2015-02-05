#include <xs1.h>
#include <platform.h>

void asmfunc();

int g() {
  return 0;
}

int h() {
  return 0;
}

int f(int i) {
  int j = 0;
  int k = 0;
  timer t;
  int time;

  // Used to test the branches
  asmfunc();

  // Used to test the exclusions
  if (i==0) {
    i = i + 10; // then

  } else {
    i = i + 2; //else

    // To be used to test the add (define) functionality 
    t :> time; // timer1 

    for (j = 0; j < i; ++j) {
      
      int l;
      for (l = 0; l < 10; ++l) {
        k++; // loop1_inner
      }

      if (j == 23) { // loop1
        k = 2;       // loop1_if
      } else {
        k = 3;       // loop1_else
      }
      g();
    }

    // To be used to test the set (define) functionality
    t :> time; // timer2

    for (j = 0; j < i; ++j) {

      int l;
      for (l = 0; l < 10; ++l) {
        k++; // loop2_inner
      }

      if (j == 23) { // loop2
        k = 2;       // loop2_if
      } else {
        k = 3;       // loop2_else
      }
      h();
    }
  }
  return i + k;
}

int main() {
  par {
    on tile[0] : f(10);
    on tile[1] : f(10);
    on tile[2] : f(10);
    on tile[3] : f(10);
  }
  return 0;
}

