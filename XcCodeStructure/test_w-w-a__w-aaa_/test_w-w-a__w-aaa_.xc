void test(int x) {
  while (x < 1) {
    while (x < 3) {
      x = 5;
      x--;
    }
    x--;
  }
  while (x < 8) {
    x = 10;
    x = 11;
    x = 12;
    x--;
  }
}

int main() {
  test(1);
  return 0;
}
