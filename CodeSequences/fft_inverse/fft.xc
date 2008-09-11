extern int Sinewave[];

int fix_fft_64(int fr[], int fi[])
{
        int mr,nn,i,j,l,k,m;
        int qr,qi,tr,ti,wr,wi,t;
        mr = 0;
        nn = 63;

#pragma unsafearrays
        for(m=1; m<=nn; m += 1) {
                l = 64;
                do {
                        l >>= 1;
                } while(mr+l > nn);
                mr = (mr & (l-1)) + l;

                if(mr > m) {
                    tr = fr[m];
                    fr[m] = fr[mr];
                    fr[mr] = tr;
                    ti = fi[m];
                    fi[m] = fi[mr];
                    fi[mr] = ti;
                }
        }

        l = 1;
        k = 5;
#pragma unsafearrays
        while(l < 64) {
                {int m; for(m=0; m < l; m+=1) {
                        int j = m << k;
                        int wr =  Sinewave[j+16];
                        int wi = -Sinewave[j];
                        wr >>= 1;
                        wi >>= 1;
                       {int i; for(i=m; i< 64; i+=l+l) {
                                int j = i + l;
                                int tr = (wr*fr[j] - wi*fi[j])>>15;
                                int ti = (wr*fi[j] + wi*fr[j])>>15;
                                int qr = fr[i];
                                int qi = fi[i];
                                qr >>= 1;
                                qi >>= 1;
                                fr[j] = qr - tr;
                                fi[j] = qi - ti;
                                fr[i] = qr + tr;
                                fi[i] = qi + ti;
                        }};
                }};
                k-=1;
                l = l << 1;
        }
        return 6;
}
int fix_fft_64_inverse(int fr[], int fi[])
{
        int mr,nn,i,j,l,k,m;
        int qr,qi,tr,ti,wr,wi,t;
        int scale=0, shift;
        mr = 0;
        nn = 63;

#pragma unsafearrays
        for(m=1; m<=nn; m += 1) {
                l = 64;
                do {
                        l >>= 1;
                } while(mr+l > nn);
                mr = (mr & (l-1)) + l;

                if(mr > m) {
                    tr = fr[m];
                    fr[m] = fr[mr];
                    fr[mr] = tr;
                    ti = fi[m];
                    fi[m] = fi[mr];
                    fi[mr] = ti;
                }
        }

        l = 1;
        k = 5;
#pragma unsafearrays
        while(l < 64) {
                shift = 0;
                for(i=0; i<64 && !shift; i+=1) {
                        j = fr[i];
                        if(j < 0)
                                j = -j;
                        m = fi[i];
                        if(m < 0)
                                m = -m;
                        if(j > 16383 || m > 16383) {
                                shift = 1;
                                scale+=1;
                        }
                }
               {int m; for(m=0; m < l; m+=1) {
                        int j = m << k;
                        int wr =  Sinewave[j+16];
                        int wi =  Sinewave[j];
                        if(shift) {
                                wr >>= 1;
                                wi >>= 1;
                        }
                       {int i; for(i=m; i< 64; i+=l+l) {
                                int j = i + l;
                                int tr = (wr*fr[j] - wi*fi[j])>>15;
                                int ti = (wr*fi[j] + wi*fr[j])>>15;
                                int qr = fr[i];
                                int qi = fi[i];
                                if(shift) {
                                        qr >>= 1;
                                        qi >>= 1;
                                }
                                fr[j] = qr - tr;
                                fi[j] = qi - ti;
                                fr[i] = qr + tr;
                                fi[i] = qi + ti;
                        }};
                }};
                k-=1;
                l = l << 1;
        }
 
        return scale;
}
int Sinewave[64] = {
 0, 3212, 6393, 9512, 12539, 15446, 18204, 20787, 23170, 25329, 27245, 28898, 30273, 31356, 32137, 32609,
 32767, 32609, 32137, 31356, 30273, 28898, 27245, 25329, 23170, 20787, 18204, 15446, 12539, 9512, 6393, 3212,
 0, -3212, -6393, -9512, -12539, -15446, -18204, -20787, -23170, -25329, -27245, -28898, -30273, -31356, -32137, -32609,
 -32767, -32609, -32137, -31356, -30273, -28898, -27245, -25329, -23170, -20787, -18204, -15446, -12539, -9512, -6393, -3212,};
#include "print.h"
int main() {
  int fi[64], fr[64], i, undefined=~0, sum = 0, sumi = 0,t3,t2,t1,t0; timer t;
  int t4; int k;
  for (k = 0; k <1000; k++) {
      fr[0]=-32767; fi[0] = 0;
      for(i=1; i<64; i+=1) {
          fr[i] = ((fr[i-1]+32768)*17)%65535-32767;
          fi[i] = ((fi[i-1]+32768)*17)%65535-32767;
      }
      sum = 0; sumi =0;
  t :> t0; fix_fft_64(fi,fr); t:> t1;
      for(i=0; i<64; i+=1) sum = (sum*13 + fr[i] + 17*fi[i])%65536;
  t :> t2; fix_fft_64_inverse(fi,fr); t :> t3;
      for(i=0; i<64; i+=1) sumi = (sumi*13 + fr[i] + 17*fi[i])%65536;
      if (sum != -48418 || sumi != -17350) { printstr("WRONG values "); printintln(sum); printintln(sumi);}
  }
}
