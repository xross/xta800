/*
 * This is a test for the
 * #pragma xta label
 *
 */
int src1(int x);
int src2(int x);

int main() {
  int x = 0;
  
  #pragma xta label "mainA"
  x = src1(x);
  
  #pragma xta label "mainB"
  x = src2(x);
  
  return x;
}
