int a = 2;
int main()
{
  int b = 0;

  if (a == 2) {
    #pragma xta label "case_1"
    b = 2;
  } else if (a == 1) {
    #pragma xta label "case_2"
    b = 3;
  } else {
    #pragma xta label "case_3"
    b = 4;
  }

  #pragma xta label "done"
  return b;
}

