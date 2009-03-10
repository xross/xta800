void test(int x) {
  if (x == 1) {
    if (x == 3) {
      if (x == 5) {
        if (x == 7) {
          if (x == 9) {
            if (x == 11) {
              if (x == 13) {
                if (x == 15) {
                  x = 17;
                }
              }
            }
          }
        }
      }
    }
  }
}

int main() {
  test(1);
  return 0;
}
