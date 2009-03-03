void test(int x) {
  switch (x) {
  case 1:
    while (x < 3) {
      if (x == 5) {
        x = 7;
      }
      if (x == 9) {
        x = 11;
      }
      if (x == 13) {
        switch (x) {
        case 15:
          x = 17;
          break;
        case 16:
          x = 20;
          break;
        }
      }
      x--;
    }
    break;
  case 2:
    if (x == 26) {
      if (x == 28) {
        x = 30;
      } else if (x == 28) {
        x = 33;
      }
    }
    if (x == 36) {
      x = 38;
    }
    break;
  case 3:
    x = 42;
    break;
  case 4:
    if (x == 45) {
      x = 47;
    }
    break;
  }
}

int main() {
  test(1);
  return 0;
}
