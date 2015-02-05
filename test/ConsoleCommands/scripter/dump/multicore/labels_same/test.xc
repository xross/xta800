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
    #pragma xta label "then"
    i = i + 10;
  } else {
    #pragma xta label "else"
    i = i + 2;

    // To be used to test the add (define) functionality 
    #pragma xta endpoint "timer1"
    t :> time; 

    for (j = 0; j < i; ++j) {
      
      int l;
      for (l = 0; l < 10; ++l) {
        #pragma xta label "loop1_inner" 
        k++;
      }

      #pragma xta label "loop1"
      if (j == 23) {
        #pragma xta label "loop1_if"
        k = 2;
      } else {
        #pragma xta label "loop1_else"
        k = 3;
      }
      g();
    }

    // To be used to test the set (define) functionality
    #pragma xta endpoint "timer2"
    t :> time; 

    for (j = 0; j < i; ++j) {

      int l;
      for (l = 0; l < 10; ++l) {
        #pragma xta label "loop2_inner" 
        k++;
      }

      #pragma xta label "loop2"
      if (j == 23) {
        #pragma xta label "loop2_if"
        k = 2;
      } else {
        #pragma xta label "loop2_else"
        k = 3;
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

