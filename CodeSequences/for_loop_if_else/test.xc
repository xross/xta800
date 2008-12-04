int main()
{
  int res = 0;
  for (int i = 0; i < 5; ++i)
  {
    if (i == 0)
      res += 1;
    else
      res += 2;
  }
  return res;
}

