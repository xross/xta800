
int mul(int x, int y) {
  return (x * y) / 10;
}

int test() {
  timer t;
  int x, y;
  
  #pragma xta endpoint "start"
  t :> x;
  for (int i = 0; i < 100; i++) {
    x = mul(x, i);
  }
  #pragma xta endpoint "stop"
  t :> y;
    
  return y - x;
}

int main() {
    return test();
}

