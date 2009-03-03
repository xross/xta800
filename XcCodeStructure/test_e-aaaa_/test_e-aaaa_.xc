void test(int x) {
  {
    timer tmr0;
    timer tmr1;
    timer tmr2;
    timer tmr3;
    select {
    case tmr0 :> int tmp0:
      x = 3;
      break;
    case tmr1 :> int tmp1:
      x = 6;
      break;
    case tmr2 :> int tmp2:
      x = 9;
      break;
    case tmr3 :> int tmp3:
      x = 12;
      break;
    }
  }
}

int main() {
  test(1);
  return 0;
}
