int test(int x, int i);

void producer(chanend c) {
  int x = 1;
  #pragma loop unroll(10)
  for (int i = 0; i < 10; i++) {
    #pragma xta endpoint "producer"
    c <: i;
  }
}

void consumer(chanend c) {
  int x = 1;
  #pragma loop unroll(10)
  for (int i = 0; i < 10; i++) {
    #pragma xta endpoint "consumer"
    c :> x;
  }
}

