
int src1Term(int x);

int src2(int x)
{
  x = src1Term(x);
  
  #pragma xta label "src2"
  return x;
}

int src2Term(int x)
{
  #pragma xta label "src2Term"
  return 1;
}
