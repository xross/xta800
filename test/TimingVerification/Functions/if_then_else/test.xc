int test(int l){
    int i=0;
    int j=0;
    l++;
    if(l == l*l){
        i++;
        j=j*i;
        j++;
    } else {
        i--;
    }
    return j;
}


int main(){
    return test(0);
}
