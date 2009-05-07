int test(int x) {
  int res = 0;
  int i = 0;
  while (i < x) {
    #pragma xta label "loop" 
    res += i;

    if (res == 22)
      i += 2;
    else
      i += 1;
  }
  return res;
}

int main() {
  test(100);
  test(100);
  return 0;
}

