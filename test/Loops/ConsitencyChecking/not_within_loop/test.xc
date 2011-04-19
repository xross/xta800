int main() 
{
  int res = 0;
  if (res == 0) {
    #pragma xta label "if"
    res = 2;
  } else {
    #pragma xta label "else"
    res = 3;
  }
  return res;
}

