int div(int a, int b)
{
  return a / b;
}

int test(int a)
{
  int b = 100;
  int c = 200;
  
  return div(div(a, b), c);
}

int pausable()
{
  int a;
  timer t;
  t :> a;
  return a;
}

int main() {
  int a = pausable();
  return test(a);
}
