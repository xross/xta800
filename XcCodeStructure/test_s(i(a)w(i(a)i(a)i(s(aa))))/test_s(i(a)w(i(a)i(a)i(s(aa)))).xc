void test(int x) {
  switch (x) {
  case 1:
    if (x == 3) {
      x = 5;
    }
    while (x < 7) {
      if (x == 9) {
        x = 11;
      }
      if (x == 13) {
        x = 15;
      }
      if (x == 17) {
        switch (x) {
        case 19:
          x = 21;
          break;
        case 20:
          x = 24;
          break;
        }
      }
      x--;
    }
    break;
  }
}

int main() {
  test(1);
  return 0;
}
