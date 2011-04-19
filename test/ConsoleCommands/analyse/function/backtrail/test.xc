int f() {
    return 0;
}

int a() { 
    f();
    return 0; 
}

int b() { 
    f();
    return 0; 
}

int main()
{
  #pragma xta call "call_a"
  a();
  #pragma xta call "call_b"
  b();
  return 0;
}
