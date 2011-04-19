#include <platform.h>
int main(){
  streaming chan chan0, chan1;
  par{
     on stdcore[0] :{ 
			int i=0;
			i++;
			chan0 <: i; }
     on stdcore[1] :{ 
			int i=0;
			i++;
			chan0 :> int; }
     on stdcore[2] :{ 
			int i=0;
			i++;
			chan1 <: i; }
     on stdcore[3] :{ 
			int i=0;
			i++;
			chan1 :> int; }
  }
  return 0;
}
