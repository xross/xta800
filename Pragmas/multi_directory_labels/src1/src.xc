
int src2Term(int x);

int src1(int x)
{
  x = src2Term(x);
  
  #pragma xta label "src1"
  return x;
}

int src1Term(int x)
{
  #pragma xta label "src1Term"
  return 1;
}
