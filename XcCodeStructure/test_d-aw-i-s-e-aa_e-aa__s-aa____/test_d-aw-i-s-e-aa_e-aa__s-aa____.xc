void test(int x) {
  do {
    x = 3;
    while (x < 4) {
      if (x == 6) {
        switch (x) {
        case 8:
          {
            timer tmr0;
            timer tmr1;
            select {
            case tmr0 :> int tmp0:
              x = 12;
              break;
            case tmr1 :> int tmp1:
              x = 15;
              break;
            }
          }
          break;
        case 9:
          {
            timer tmr0;
            timer tmr1;
            select {
            case tmr0 :> int tmp0:
              x = 21;
              break;
            case tmr1 :> int tmp1:
              x = 24;
              break;
            }
          }
          break;
        }
      } else if (x == 6) {
        switch (x) {
        case 29:
          x = 31;
          break;
        case 30:
          x = 34;
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
