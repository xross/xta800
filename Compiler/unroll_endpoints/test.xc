void producer(chanend c);
void consumer(chanend c);

int main()
{
  chan c;
  par {
    while(1) {
      producer(c);
    }
    while(1) {
      consumer(c);
    }
  }
  return 0;
}

