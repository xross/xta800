#include <platform.h>

int b0();
int b1();
int b2();
int b3();
int c0();
int c1();
int c2();
int c3();

int a0()
{
  return 0;
}
int a1()
{
  return 0;
}
int a2()
{
  return 0;
}
int a3()
{
  return 0;
}

void test0() {
  a0();
  b0();
  c0();
}

void test1() {
  a1();
  b1();
  c1();
}

void test2() {
  a2();
  b2();
  c2();
}

void test3() {
  a3();
  b3();
  c3();
}

int main()
{
  par
  {
    on stdcore[0] : test0();
    on stdcore[1] : test1();
    on stdcore[2] : test2();
    on stdcore[3] : test3();
  }
  return 0;
}

