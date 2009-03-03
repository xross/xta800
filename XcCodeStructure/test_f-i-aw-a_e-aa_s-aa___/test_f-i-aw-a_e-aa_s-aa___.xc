void test(int x) {
  for (int i1 = 0; i1 < 10; i1++) {
    if (x == 3) {
      x = 5;
    } else if (x == 3) {
      while (x < 8) {
        x = 10;
        x--;
      }
    } else if (x == 3) {
      {
        timer tmr0;
        timer tmr1;
        select {
        case tmr0 :> int tmp0:
          x = 16;
          break;
        case tmr1 :> int tmp1:
          x = 19;
          break;
        }
      }
    } else if (x == 3) {
      switch (x) {
      case 23:
        x = 25;
        break;
      case 24:
        x = 28;
        break;
      }
    }
  }
}

int main() {
  test(1);
  return 0;
}
