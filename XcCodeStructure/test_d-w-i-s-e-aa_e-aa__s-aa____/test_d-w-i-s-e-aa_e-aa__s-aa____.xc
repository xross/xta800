void test(int x) {
  do {
    while (x < 3) {
      if (x == 5) {
        switch (x) {
        case 7:
          {
            timer tmr0;
            timer tmr1;
            select {
            case tmr0 :> int tmp0:
              x = 11;
              break;
            case tmr1 :> int tmp1:
              x = 14;
              break;
            }
          }
          break;
        case 8:
          {
            timer tmr0;
            timer tmr1;
            select {
            case tmr0 :> int tmp0:
              x = 20;
              break;
            case tmr1 :> int tmp1:
              x = 23;
              break;
            }
          }
          break;
        }
      } else if (x == 5) {
        switch (x) {
        case 28:
          x = 30;
          break;
        case 29:
          x = 33;
          break;
        }
      }
      x--;
    }
    x--;
  } while (x < 1);
}

int main() {
  test(1);
  return 0;
}
