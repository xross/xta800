int f(int i){
 timer t;
  unsigned int x, y;

  #pragma xta endpoint "ep1"
  #pragma xta endpoint "ep1"
  #pragma xta endpoint "ep1"
  #pragma xta endpoint "ep1"
  #pragma xta endpoint "ep1"
  #pragma xta endpoint "ep1"
  #pragma xta endpoint "ep1"
  #pragma xta endpoint "ep1"
  #pragma xta endpoint "ep1"
  #pragma xta endpoint "ep1"
  #pragma xta endpoint "ep1"
  #pragma xta endpoint "ep1"
  t :> x;

  #pragma xta endpoint "ep2"
  #pragma xta endpoint "ep2"
  t :> y;
  #pragma xta call "label_0"
  i = i + 32;
  #pragma xta call "label_0"
  i = i + 32;
	#pragma xta call "label_0"
	return 43 + i;
#pragma xta call "label_0"
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
  #pragma xta endpoint "ep4"
  t :> i;
  #pragma xta call "callg"
	#pragma xta call "test.xc:46"
	i = i + 32 + g(i);

  
	return 43 + i;
#pragma xta call "label_0"
}

int main(){


	f(5);
	#pragma xta call "test.xc:46"
	g(6);
#pragma xta call "callh"
	#pragma xta call "test.xc:46"
	h(8);
	#pragma xta call "test.xc:46"
	return f(8) + g(423) + h(2);
#pragma xta call "label_0"
}
