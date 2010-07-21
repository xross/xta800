int test (int x){
    int y = 0;
    switch (x){
    case 0:
        y=0;
        break;
    case 1:
        y=1;
        break;
    case 2:
        y=2;
        break;
    case 3:
        y=3;
        break;
    case 4:
        y=4;
        break;
    case 5:
        y=5;
        break;
    case 6:
        y=6;
        break;
    case 7:
        y=7;
        break;
    case 8:
        y=8;
        break;
    case 9:
        y=9;
        break;
    case 10:
        y=10;
        break;
    case 11:
        y=11;
        break;
    case 12:
        y=12;
        break;
    case 13:
        y=13;
        break;
    case 14:
        y=14;
        break;
    case 15:
        y=15;
        break;
    case 16:
        y=16;
        break;
    case 17:
        y=17;
        break;
    case 18:
        y=18;
        break;
    case 19:
        y=19;
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
