int test(int x) {
  int res = 0;
  int i = 0;
  while (i < x) {
    #pragma xta label "loop" 
    res += i;
    i++;
  }
  return res;
}

int main() {
  return test(100);
}

