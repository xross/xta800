int func1() {
  return 1;
}

int func2() {
  int x = 0;
  x *= 2;
  x *= 2;
  x *= 2;
  x *= 2;
  x *= 2;
  x *= 2;
  x *= 2;
  x *= 2;
  x *= 2;
  return x;
}

int test(int x) {
  int res = 0;
  int i = 0;

  do {
    #pragma xta label "loop" 
    if (i % 2) {           
      #pragma xta label "loop_func1"
      func1(); 
    } else {
      #pragma xta label "loop_func2"
      func2(); 
    }

    res += i;
    i++;
  } while (i < x);
  return res;
}

int main() {
  return test(100);
}

