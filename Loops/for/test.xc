int test(int x) {
  int res = 0;
  for (int i = 0; i < x; i++) {
    #pragma xta label "label1" 
    res += i;
  }
  return res;
}

int main() {
  return test(100);
}

