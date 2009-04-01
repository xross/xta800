int main() 
{
  int res = 0;

  int i = 0;
  while (i < 10) 
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

