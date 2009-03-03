void test(int x) {
  {
    timer tmr0;
    timer tmr1;
    select {
    case tmr0 :> int tmp0:
      {
        timer tmr0;
        timer tmr1;
        select {
        case tmr0 :> int tmp0:
          switch (x) {
          case 5:
            if (x == 7) {
              x = 9;
            }
            if (x == 11) {
              x = 13;
            }
            if (x == 15) {
              while (x < 17) {
                x = 19;
                x--;
              }
            }
            break;
          case 6:
            do {
              x = 26;
              x--;
            } while (x < 24);
            break;
          }
          break;
        case tmr1 :> int tmp1:
          x = 31;
          break;
        }
      }
      break;
    case tmr1 :> int tmp1:
      for (int i35 = 0; i35 < 10; i35++) {
        x = 37;
      }
      break;
    }
  }
}

int main() {
  test(1);
  return 0;
}
