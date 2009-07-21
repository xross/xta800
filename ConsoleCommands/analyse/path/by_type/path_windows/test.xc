int add(int a, int b);

int sub(int a, int b)
{
  return a - b;
}

int main()
{
  timer t;
  unsigned int x, y, z;
  
  #pragma xta endpoint "main"
  t :> x;
  
  x = add(x, 1); 
   
  #pragma xta endpoint "add"
  t :> y;

  y = sub(y, x);

  t :> z;

  return y - z;
}

