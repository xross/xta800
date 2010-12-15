int f(int i)
{
  if(i==0){
    #pragma xta label "then"
    i = i + 2;
    i = i * i + 8;
  } else {
    #pragma xta label "else"
    i = i + 2;
  }
  return i;
}

int g(int i)
{
  return f(i);
}

int h(int i)
{
  return f(i);
}

int main()
{
  g(6);
  h(33);
  return 0;
}
