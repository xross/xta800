#include <xs1.h>
#include <platform.h>

int g() {
  return 0;
}

int f(int i) {
  int j = 0;
  int k = 0;

  if (i==0) {
    #pragma xta label "then"
    i = i + 10;
  } else {
    #pragma xta label "else"
    i = i + 2;
 
    for (j = 0; j < i; ++j) {
        #pragma xta label "loop"
        g();
    }
  }
  return i + k;
}

int main() {
  par {
    on stdcore[0] : f(10);
    on stdcore[1] : f(10);
    on stdcore[2] : f(10);
    on stdcore[3] : f(10);
  }
  return 0;
}

