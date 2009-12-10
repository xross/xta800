void producer(chanend c);
void consumer(chanend c);

int main()
{
  chan c;
  par {
      producer(c);
      consumer(c);
  }
  return 0;
}

