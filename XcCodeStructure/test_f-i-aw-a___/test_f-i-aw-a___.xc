void test(int x) {
  for (int i1 = 0; i1 < 10; i1++) {
    if (x == 3) {
      x = 5;
    } else if (x == 3) {
      while (x < 8) {
        x = 10;
        x--;
      }
    }
  }
}

int main() {
  test(1);
  return 0;
}
