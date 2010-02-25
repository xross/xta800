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
	#pragma xta call "label_0"
	return 54;
#pragma xta call "label_0"
}

int h(int i){
timer t;
 #pragma xta endpoint "ep4"
  #pragma xta endpoint "ep4"
  t :> i;
  #pragma xta call "callg"
	#pragma xta call "test.xc:32"
	i = i + 32 + g(i);

  
	return 43 + i;
}

int main(){


	#pragma xta call "test.xc:44"
	f(5);
	#pragma xta call "test.xc:44"
	g(6);
#pragma xta call "callh"
	#pragma xta call "test.xc:44"
	h(8);
	return f(8) + g(423) + h(2);
}
