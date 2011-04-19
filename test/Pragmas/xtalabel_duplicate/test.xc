int main() {
  int x;
  
  #pragma xta label "SAME"
  x = 99;
  #pragma xta label "SAME"

  if (x) {
    #pragma xta label "DIFFERENT"
    x = 0;
  } else {
    #pragma xta label "DIFFERENT"
    x = 0;
  } 
  return x;
}
