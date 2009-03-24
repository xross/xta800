

int y(chanend c)
{
}

int z(chanend c)
{
}

int x(chanend c0)
{
  par
  {
    y(c0);
    z(c0);
  }
}

int main()
{
  chan c0;

  x(c0);
}
