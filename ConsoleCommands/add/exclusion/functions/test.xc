
int a() {
  return 1;
}

int b() {
  return 2;
}

int testA() {
  a();
}

int testB() {
  b();
}

int testAandB() {
  a();
  b();
}

int testAorB(int x) {
  if (x)
    a();
  else
    b();
}

int main()
{
  testA();
  testB();
  testAandB();
  testAorB(0);
  return 0;
}
