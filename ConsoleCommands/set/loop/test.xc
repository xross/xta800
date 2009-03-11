int test(int a)
{
  int res = 0;
  int i = 0;
  for (int j = 0; j < 10; j++) {
    for (i = 0; i < a; ++i)
    {
      res += 1;
    }
    for (i = 0; i < (a - 1); ++i)
    {
      res += 1;
    }
    for (i = 0; i < (a - 2); ++i)
    {
      res += 1;
    }
  }
  for (i = 0; i < (a - 3); ++i)
  {
    res += 1;
  }
  for (i = 0; i < (a - 4); ++i)
  {
    res += 1;
  }
  return res;
}

int main()
{
  return test(5);
}
