
int delay(int x)
{
  int j = 0;
  
  for (int i = 0; i < x; i++)
    j += i;
    
  return j;
}

int main()
{
  delay(10);
  delay(20);
  return 0;
}

