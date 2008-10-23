
int multiPath(int x)
{
  for (int i = 0; i < 10; i++) {
    x += 1;
  }
  return x;
}

int main()
{
  int i = 0;
  do
  {    
    multiPath(10);
  } while (i < 10);
  
  return 0;
}

