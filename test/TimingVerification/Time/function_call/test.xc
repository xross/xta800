int foo(){
    return 6; 
}

int bar(){
    int i=0;
    for(int i=0;i<10;i++){
    }
    return i;
}

int test(){
    return foo() + bar();
}


int main(){
    return test();
}
