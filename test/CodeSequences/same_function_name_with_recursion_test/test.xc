extern int g(int);

static void f (){
  g(8);
  return;
}

int main()
{
  f();
  return 0;
}

