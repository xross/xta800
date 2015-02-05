#include <platform.h>

int a_b0();
int a_b1();
int a_b2();
int a_b3();
int a_c0();
int a_c1();
int a_c2();
int a_c3();

int a_a0()
{
  return 0;
}
int a_a1()
{
  return 0;
}
int a_a2()
{
  return 0;
}
int a_a3()
{
  return 0;
}

void a_test0() {
  a_a0();
  a_b0();
  a_c0();
}

void a_test1() {
  a_a1();
  a_b1();
  a_c1();
}

void a_test2() {
  a_a2();
  a_b2();
  a_c2();
}

void a_test3() {
  a_a3();
  a_b3();
  a_c3();
}

int main()
{
  par
  {
    on tile[0] : a_test0();
    on tile[1] : a_test1();
    on tile[2] : a_test2();
    on tile[3] : a_test3();
  }
  return 0;
}

