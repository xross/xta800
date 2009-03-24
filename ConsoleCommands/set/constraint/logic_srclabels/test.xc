
int a() {
  #pragma xta label "label_a"
  return 1;
}

int b() {
  #pragma xta label "label_b"
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
  // Prevent functions from being stripped
  testA();
  testB();
  testAandB();
  testAorB(0);
  return 0;
}
