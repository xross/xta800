void test(int x) {
  switch (x) {
  case 1:
    {
      timer tmr0;
      timer tmr1;
      select {
      case tmr0 :> int tmp0:
        if (x == 5) {
          x = 7;
        }
        break;
      case tmr1 :> int tmp1:
        while (x < 11) {
          x = 13;
          x--;
        }
        break;
      }
    }
    break;
  }
  if (x == 17) {
    x = 19;
  }
  while (x < 21) {
    if (x == 23) {
      x = 25;
    }
    x--;
  }
  for (int i28 = 0; i28 < 10; i28++) {
    x = 30;
  }
}

int main() {
  test(1);
  return 0;
}
