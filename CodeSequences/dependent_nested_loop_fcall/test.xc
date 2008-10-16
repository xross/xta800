
int loop(int i)
{
  int res = 0;
  for (int j = 0; j < i; j++)
  {
    for (int k = 0; k < 20; k++)
    {
      res += k * i;
    }
    res += j * i;
  }
  return res;
}


int main()
{
  int res = 0;
  for (int i = 0; i < 10; i++)
  {
    res += loop(i);
    res += loop(1);
  }
  return res;
}

