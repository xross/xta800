
int foo(){
  int a=0;
  int b=5+a;
  int i;
  for(i=0;i<b;i++){
    a++;
  }
  b+=5;
  return b;
}

int main()
{
  int a = 2;
  int b = 0;
  for(b=0;b<10;b++)
  {
    foo();
  }
  return b;
}

