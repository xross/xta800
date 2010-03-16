extern int g(int);

static void f (){
  return;
}

int main()
{
  f();
  return g(8);
}

