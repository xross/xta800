int test1(int x) {
  int res = 0;
  for (int i = 0; i < x; i++) {
    #pragma xta label "loop1" 
    res += i;
  }
  return res;
}

int test2(int x) {
  int res = 0;
  for (int i = 0; i < x; i++) {
    #pragma xta label "loop2" 
    res += i;
  }
  return res;
}

int main() {
  return test1(100) + test2(100);
}

