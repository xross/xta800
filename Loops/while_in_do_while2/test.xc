int test(int x) {
  int res = 0;
  int i = 0;
  int j = 0;
  do {
    while (i < x) {
      #pragma xta label "while" 
      res += i;
      i++;
    }
    i = 0;
    #pragma xta label "do" 
    j++;
  } while (j < x);
  return res;
}

int main() {
  return test(100);
}

