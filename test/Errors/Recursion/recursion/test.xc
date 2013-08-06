int g(int x);

int f(int x)
{
  if (x < 0) {
    int a = 78;
    int b = 344;
    return g(-x + a + b);

  } else {
    return 0;
  }
}

int g(int x)
{
  if (x < 0) {
    int a = 78;
    int b = 344;
    return f(-x + a + b);

  } else {
    return 0;
  }
}

int main()
{
  f(123);
  return 0;
}
