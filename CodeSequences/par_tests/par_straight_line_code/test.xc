int f1(int x)
{
  int a = x * 2;
  
  return a - x;
}

int f2(int x)
{
  int a = x / 2;
  
  return a - x;
}

int main()
{
  int a, b;
  
  par
  {
    a = f1(10);
    b = f2(20);
  }
  
  return (a + b);
}

