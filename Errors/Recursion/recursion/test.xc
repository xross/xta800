int g(int x);

int f(int x)
{
  if (x < 0)
    return g(-x);
  else
    return 0;
}

int g(int x)
{
  if (x < 0)
    return f(-x);
  else
    return 0;
}

int main()
{
  f(123);
  return 0;
}
