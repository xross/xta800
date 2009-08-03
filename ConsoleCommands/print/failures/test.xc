int div(int a, int b)
{
  return a / b;
}

int main() {
  int a = 50;
  int b = 100;
  int c = 200;
  
  return div(div(a, b), c);
}
