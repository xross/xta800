
int test(int i) {
  return i*2;
}

int main()
{
  int sum = 0;
  
  for (int i = 0; i < 10; i++) {
    #pragma xta label "loop"
    sum += test(i);
  }
  
  return (sum == 90) ? 0 : 1;
}
