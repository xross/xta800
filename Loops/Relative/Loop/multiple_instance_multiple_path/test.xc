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
  int c = 0;
  int b = 0;
  if (c == 10)
    b = 1;
  else
    b = 2;

  test(100);
  test(100);
  return b;
}

