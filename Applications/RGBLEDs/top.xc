#include <xs1.h>
#include <platform.h>

extern void getButtonData(chanend ch);
extern void rgbLeds(chanend ch);

int main()
{
  chan ch;
  par
  {
    on stdcore[0] : getButtonData(ch);
    on stdcore[2] : rgbLeds(ch);
  }
  return 0;
}
