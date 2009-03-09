void test(int x) {
  if (x == 1) {
    x = 3;
  }
  if (x == 5) {
    if (x == 7) {
      x = 9;
    }
  }
}

int main() {
  test(1);
  return 0;
}
