int test (int x){
    int y = 0;
    switch (x){
    case 0:
        y=00;
        break;
    case 1:
        y=11;
        break;
    case 2:
        y=22;
        break;
    case 3:
        y=33;
        break;
    case 4:
        y=44;
        break;
    case 5:
        y=55;
        break;
    case 6:
        y=66;
        break;
    case 7:
        y=77;
        break;
    }
    return y;
}


int main() {
    int count=0;
    int i;
    for(i=0;i<100;i++){
        count += test(i);
    }
    return count;
}
