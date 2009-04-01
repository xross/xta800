
int loop(int k)
{
  int res = 0;
  int i = 0;
  while (i < k) 
  {
    #pragma xta label "outer_loop"
    int j = i;
    while (j < 10) 
    {
      #pragma xta label "inner_loop"
      res += j;
      ++j;
    }
    ++i;
  }
  return res;
}


int main()
{
  int res = 0;
  int j = 10;

  for (int i = 0; i < 3; i++)
  {
    #pragma xta label "main_loop"
    res += loop(j);
    res += loop(j);
  }
  return res;
}

