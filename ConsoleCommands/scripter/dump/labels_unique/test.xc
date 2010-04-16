
int endpoint_0() {
  timer t;
  int x,y;
  
  t :> x;
  t :> y;
  
  return y - x ;
}

int main() {
  return endpoint_0();
}
