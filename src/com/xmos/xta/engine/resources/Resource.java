package com.xmos.xta.engine.resources;

import com.xmos.xta.engine.Expression;

public interface Resource {
    public void GETR(int dest, int scr);
    
    public void FREER(int resId, int resType);
    public void GETST(int resId, int resType, Expression dest);
    
    public void MSYNC(int resId, int resType);
    public void MJOIN(int resId, int resType);
    
    public void TINITCP(int dest, int scr);
    public void TINITDP(int dest, int scr);
    public void TINITSP(int dest, int scr);
    public void TINITPC(int dest, int scr);
    public void TINITLR(int dest, int scr);
    public void TSTART(int threadId);
    public void TSETR(int dest, int scr1, int scr2);
    
    public void SETV(int resId, int resType);
    public void SETEV(int resId, int resType);
    
    public void SETD(int resId, int resType, Expression expression);
    public void GETD(int resId, int resType, Expression expression);
    public void SETC(int resId, int resType, Expression expression);
    
    public void SETCLK(int resId, int resType, Expression expression);
    public void SETRDY(int resId, int resType, Expression expression);
    
    public void SETPT(int resId, int resType, Expression expression);
    public void GETTS(int resId, int resType, Expression expression);
    public void INSHR(int resId, int resType, Expression expression);
    public void OUTSHR(int resId, int resType, Expression expression);
    public void PEEK(int resId, int resType, Expression expression);
    public void SETTW(int resId, int resType, Expression expression);
    public void SETPSC(int resId, int resType, Expression expression);
    public void ENDIN(int resId, int resType, Expression expression);
    public void CLRPT(int resId, int resType, Expression expression);
    
    public void EEU(int resId, int resType);
    public void EDU(int resId, int resType);
    public void EET(int resId, int resType, Expression expression);
    public void EEF(int resId, int resType, Expression expression);
    
    public void IN(int dest, int scr, Expression expression);
    
    public void OUT(int scr, int scr1, Expression expression);
    
    public void INT(int resId, int resType, Expression expression);
    public void INCT(int resId, int resType, Expression expression);
    public void OUTT(int resId, int resType, Expression expression);
    public void OUTCT(int resId, int resType, Expression expression);
    public void TESTCT(int dest, int scr);
    public void TESTWCT(int dest, int scr);
    public void CHKCT(int scr, int scr1);
    public void TESTLCL(int dest, int scr);
    
}
