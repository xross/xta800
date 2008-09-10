int function1()
{
  int a = 22;
  int b[12] = {0};
  return a;
}

int function2()
{
  int a = 23;
  int b[5] = {0};
  return a;
}

int main()
{
  int a = 22;
  if (a < 21) 
    function1();
  else
    function2();

  return 0;
}

