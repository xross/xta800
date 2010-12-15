#include "header.h"

#include "add.xc"
#include "sub.xc"

int main()
{
  int result1 = add(2, 4);
  int result2 = sub(5, 1);
  
  return result1 + result2;
}

