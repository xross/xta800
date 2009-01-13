int main() {
  int x;
  int before, after;
  
  timer t;
  
  #pragma xta label "l"
  t :> before;
  for (int i = 0; i < 1000; i++) {
    x += i;
  }
  #pragma xta label "l"
  t :> after;
  
  return x;
}
