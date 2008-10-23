
int delay(int x)
{
  int j = 0;
  
  for (int i = 0; i < x; i++)
    j += i;
    
  return j;
}

void both()
{
  delay(10);
  delay(20);
}

void either(int choice)
{
  if (choice)
    delay(10);
  else
    delay(20);
}

int main()
{
//  both();
  either(0);
  either(1);
  
  return 0;
}

