#include <xs1.h>

port a = XS1_PORT_4A;

int x(port a){
  #pragma xta endpoint "ep"
  int i;  
  a:> i;
  return i;
}

int g(port a){
  #pragma xta call "cx1"
  x(a);

  #pragma xta call "cx2"
  return x(a);
}

int f(port a) {
  int i = 1;
  
  #pragma xta call "cg"
  i = g(a);

  i += x(a);
  i += x(a);
  #pragma xta call "cg1"
  #pragma xta call "cg2"
  i = g(a);

  i += x(a);
  i += x(a);
  return i;
}

int main(){
  f(a);
  return 0;
} 
