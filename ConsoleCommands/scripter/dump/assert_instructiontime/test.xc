#include <xs1.h>

void f() {
    int time;
    timer t;
    #pragma xta endpoint "t_f"
    #pragma xta endpoint "t_f"
    #pragma xta endpoint "t_f"
    #pragma xta endpoint "t_f"
    #pragma xta endpoint "t_f"
    #pragma xta endpoint "t_f"
    #pragma xta endpoint "t_f"
    #pragma xta endpoint "t_f"
    #pragma xta endpoint "t_f"
    #pragma xta endpoint "t_f"
    #pragma xta endpoint "t_f"
    t :> time;
}

void g() {
    int time;
    timer t;
    #pragma xta endpoint "t_g"
    #pragma xta endpoint "t_g"
    #pragma xta endpoint "t_g"
    #pragma xta endpoint "t_g"
    #pragma xta endpoint "t_g"
    #pragma xta endpoint "t_g"
    #pragma xta endpoint "t_g"
    #pragma xta endpoint "t_g"
    #pragma xta endpoint "t_g"
    #pragma xta endpoint "t_g"
    #pragma xta endpoint "t_g"
    t :> time;
}

void h() {
    int time;
    timer t;
    #pragma xta endpoint "t_h"
    #pragma xta endpoint "t_h"
    #pragma xta endpoint "t_h"
    #pragma xta endpoint "t_h"
    #pragma xta endpoint "t_h"
    #pragma xta endpoint "t_h"
    #pragma xta endpoint "t_h"
    #pragma xta endpoint "t_h"
    #pragma xta endpoint "t_h"
    #pragma xta endpoint "t_h"
    #pragma xta endpoint "t_h"
    t :> time;
}

void a() {
  f();
  g(); 
}

void b() {
  f();
  h(); 
}

int main(void) {
    a();
    b();
    return 0;
}
