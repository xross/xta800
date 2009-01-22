/*
 * A simple program to explain some of the basic concepts of the XTA tool
 *
 */
#define MAX_SIZE 100

void resetTable(int table[]) {
  table[0] = 0;
  table[1] = 0;
  table[2] = 0;
  table[3] = 0;
  table[4] = 0;
  table[5] = 0;
  table[6] = 0;
  table[7] = 0;
  table[8] = 0;
  table[9] = 0;
}

void initTable(int table[], int size) {
  for (int i = 0; i < size; i++) {
    table[i] = i;
  }
}

void squareTable(int table[], int size) {
  for (int i = 0; i < size; i++) {
    table[i] = table[i] * table[i];
  }
}

int sumTable(int table[], int size) {
  int sum = 0;
  for (int i = 0; i < size; i++) {
    sum += table[i];
  }
  return sum;
}

int functionCalls(int table[], int size) {
   int result = 0;
   
   initTable(table, size);
   squareTable(table, size);
   result = sumTable(table, size);
}

int main() {
  int table[MAX_SIZE];
  int x = 0;
  
  resetTable(table);
  x = functionCalls(table, 10);
  
  if (x > 100) {
    x = x / 10;
  } else {
    x = x * 10;
  }

  return x;
}
