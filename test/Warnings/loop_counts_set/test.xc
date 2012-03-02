extern int test();

int main()
{
  int sum = 0;
  
  for (int i = 0; i < 5*7*11*13*17; i++) {
    #pragma xta label "loop"
    sum += test();
  }
  
  return (sum == 90) ? 0 : 1;
}
