extern int bar();

int test() {
  int res = 0;
  int i = 0;
  while (i < 500) {
    res += 7 + i*(i+1) + bar();
    i++;
  }
  return res;
}

int main() {
  return test();
}

