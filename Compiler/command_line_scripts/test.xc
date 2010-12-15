#pragma xta command "analyse function main"
#pragma xta command "print structure -"

int f(int a) {
  if (a == 0)
    return 2;
  else
    return 5;
}

int main()
{
  f(0);
  f(1);
  return 0;
}

