void label1();
void label2();

int add(int a, int b)
{
  return a + b;
}

int sub(int a, int b)
{
  return a - b;
}

int main()
{
  timer t;
  unsigned int x, y, z;
  
  #pragma xta endpoint "label1"
  t :> x;
  
  x = add(x, 1); 
   
  #pragma xta endpoint "label2"
  t :> y;

  #pragma xta call "label2"
  y = sub(y, x);

  t :> z;
    
  #pragma xta call "label1"
  label1();
  label2();

  return y - z;
}

