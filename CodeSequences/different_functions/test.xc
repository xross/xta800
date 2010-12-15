int f(int a) {
  if (a == 0)
    #pragma xta label "if"
    return 2;
  else
    #pragma xta label "else"
    return 5;
}

int main()
{
  #pragma xta call "call1"
  f(0);
  #pragma xta call "call2"
  f(1);
  return 0;
}
