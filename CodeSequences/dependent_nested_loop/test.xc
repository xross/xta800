int main()
{
  int res = 0;
  for (int i = 0; i < 10; i++)
  {
    for (int j = i; j < 10; j++)
      res += j;
  }
  return res;
}

