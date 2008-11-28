#define ITERATIONS 10

int data[] = {
    0, 1, 2, 3, 4, 5, 6, 7, 8, 9
  };

int result[ITERATIONS];

void  producer(chanend c)
{
  for (int i; i < ITERATIONS; i++)
  {
    c <: data[i];
  }
}

int process(int x)
{
  static int sum = 0;
  
  sum += x;
  
  return (x * x) + sum;
}

void consumer(chanend c)
{
  for (int i; i < ITERATIONS; i++)
  {
    int x;
    c :> x;
    result[i] = process(x);
  }
}

int main()
{
  chan c;
  
  par
  {
    producer(c);
    consumer(c);
  }
  
  return 0;
}

