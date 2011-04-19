int test(int x) {
  int i = 0;
  while (i < x) {
    #pragma xta label "loop" 
     ++i;
  }
  return i;
}

int main() {
  #pragma xta call "test1"
  test(100);
  #pragma xta call "test2"
  test(100);
  return 0;
}

