int function1()
{
  int a = 22;
  int b[12];
  return a;
}

int function2()
{
  int a = 23;
  int b[5];
  return a;
}

int test()
{
  int a;
  if (a < 21) 
    function1();
  else
    function2();
}

int main()
{
  test();
  return 0;
}

