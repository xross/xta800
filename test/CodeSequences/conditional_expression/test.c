
int half(int x)
{
  return x / 2;
}

int times2(int x)
{
  return x * 2;
}

int test(int res)
{ 
  for (int i = 0; i < 10; i++)
  {
    res = (res > 5) ? half(res) : times2(res);
  }
  return res;
}

int main(){
  return test(5);
}