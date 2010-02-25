int f(int i){
 timer t;
  unsigned int x, y;

  #pragma xta endpoint "ep1"
  t :> x;

  #pragma xta endpoint "ep2"
  #pragma xta endpoint "ep2"
  #pragma xta endpoint "ep2"
  #pragma xta endpoint "ep2"
  #pragma xta endpoint "ep2"
  #pragma xta endpoint "ep2"
  #pragma xta endpoint "ep2"
  #pragma xta endpoint "ep2"
  #pragma xta endpoint "ep2"
  t :> y;
  i = i + 32;
  i = i + 32;
	return 43 + i;
}

int g (int i){
timer t;
  unsigned int x, y;

  #pragma xta endpoint "ep3"
  t :> x;
#pragma xta call "callf"
i = f(i) + 32;
i = i + 32;
	return 54;
}

int h(int i){
timer t;
 #pragma xta endpoint "ep4"
  t :> i;
  #pragma xta call "callg"
	i = i + 32 + g(i);

  
	#pragma xta call "label_0"
	return 43 + i;
#pragma xta call "label_0"
}

int main(){


	#pragma xta call "test.xc:42"
	f(5);
	#pragma xta call "test.xc:42"
	g(6);
#pragma xta call "callh"
	h(8);
	return f(8) + g(423) + h(2);
}
