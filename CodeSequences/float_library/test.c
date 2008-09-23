#include <stdlib.h>

double mul(double one, double two)
{
  double result = one * two;
  return result;
}

int main()
{
  double result = mul(1.0, 3.5);
  return floor(result); 
}
