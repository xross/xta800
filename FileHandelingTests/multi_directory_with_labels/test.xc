int add(int a, int b);

int main()
{
  #pragma xta label "label1"
  int a = add(2, 4);
  return a;
}

