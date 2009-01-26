/*
 * This is a test for the
 * #pragma xta label
 *
 */

int table[64] = {
   0,  2,  4,  8,  1,  3,  5,  9, 
  10, 12, 14, 18, 11, 13, 15, 19, 
  20, 22, 24, 28, 21, 23, 25, 29, 
  30, 32, 34, 38, 31, 33, 35, 39, 
  40, 42, 44, 48, 41, 43, 45, 49, 
  50, 52, 54, 58, 51, 53, 55, 59, 
  60, 62, 64, 68, 61, 63, 65, 69
};

int send(chanend c)
{
  for(int i = 0; i < 64; i++) {
    c <: table[i];
  }
  return 0;
}

int receive(chanend c)
{
  for(int i = 0; i < 64; i++) {
    int x;
    c :> x;
    if (x != table[i])
      return 1;
  }
  return 0;
}

int main() {
  int a[64], b[64], i;
  int before, after;
  
  timer t;
  chan c;
  int result = 0;
  
  #pragma xta label "pointA"
  t :> before;
  for (i = 0; i < 1000; i++) {
    result += i;
  }
  #pragma xta label "pointB"
  t :> after;
  
  return (after - before) < 1000;
}
