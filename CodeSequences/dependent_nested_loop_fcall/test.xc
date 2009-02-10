
int loop(int i)
{
  int res = 0;
  for (int j = 0; j < i; j++)
  {
    for (int k = 0; k < 3; k++)
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
int j;
  for (int i = 0; i < 3; i++)
  {
    res += loop(j);
    res += loop(j);
  }
  return res;
}

