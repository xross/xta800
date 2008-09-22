#include <stdio.h>

void swap(int *a, int *b)
{
  int t=*a;
  *a = *b;
  *b = t;
}

void sort(int arr[], int beg, int end)
{
  if (end > beg + 1)
  {
    int piv = arr[beg], l = beg + 1, r = end;
    while (l < r)
    {
      if (arr[l] <= piv)
        l++;
      else
        swap(&arr[l], &arr[--r]);
    }
    swap(&arr[--l], &arr[beg]);
    sort(arr, beg, l);
    sort(arr, r, end);
  }
}

int data[] = { 40, 23, 51, 252, 2, 5, 0, 16, 91, 72 };

int main()
{
  int begin = 0;
  int end = sizeof(data) / sizeof(int);
  
  sort(data, begin, end);
 
  for (int i = begin + 1; i < end; i++)
  {
    if (data[i - 1] > data[i])
      return 1;
  }
 
  return 0; 
}
