int test(int x) {
  int i = 0;
  while (i < x) {
    #pragma xta label "loop" 
     ++i;
  }
  return i;
}

int main() {
  test(100);
  test(100);
  return 0;
}

