int function1();
int function2();
int function3();
int function4();
int function5();

int test(int a)
{
  int b = 0;
  if (a == 0)
    b = 1;

  function1();
  function2();
  function3();
  function4();
  function5();
  return 0;
}

int main()
{
  test(0);
}
