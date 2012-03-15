// This test checks that a loop count can be set on a loop
// that has multiple returns

int test() {
  int res = 0;
  for (int i = 0; i < 5; i++) {
    #pragma xta label "looplabel" 
    res += i;
    if (i == 22) {
      return 0;
    }
  }
  return res;
}

int main() {
  return test();
}

