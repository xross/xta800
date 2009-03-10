void test(int x) {
  if (x == 1) {
    x = 3;
  }
  if (x == 5) {
    x = 7;
  }
  if (x == 9) {
    if (x == 11) {
      x = 13;
    }
    if (x == 15) {
      x = 17;
    }
  }
  while (x < 20) {
    if (x == 22) {
      x = 24;
    }
    if (x == 26) {
      x = 28;
    }
    while (x < 30) {
      if (x == 32) {
        x = 34;
      }
      x--;
    }
    x--;
  }
}

int main() {
  test(1);
  return 0;
}
