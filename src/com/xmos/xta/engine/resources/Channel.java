package com.xmos.xta.engine.resources;

import com.xmos.xta.engine.Expression;

public class Channel implements Resource {

    private Channel() {

    }

    public void FREER(int resId) {

    }

    public void SETV(int resId, int value) {

    }

    public void SETEV(int resId, int value) {

    }

    public void SETD(int dataPointer) {

    }

    public void GETD(int dest, int scr) {

    }

    public void SETC(int resId, int controlBits) {

    }

    public void EDU(int resId) {

    }

    public void EEF(int dest, int scr) {

    }

    public void EET(int dest, int scr) {

    }

    public void EEU(int resId) {

    }

    public void IN(int dest, int scr) {

    }

    public void OUT(int scr, int scr1) {

    }

    public void INT(int dest, int scr) {

    }

    public void INCT(int dest, int scr) {

    }

    public void OUTT(int resId, int dest) {

    }

    public void OUTCT(int resId, int dest) {

    }

    public void TESTCT(int dest, int scr) {

    }

    public void TESTLCL(int dest, int scr) {

    }

    public void TESTWCT(int dest, int scr) {

    }

    public void CHKCT(int scr, int scr1) {

    }

    public void CLRPT(int resId) {
        System.err.println("ILLEGAL_RESOURCE exception");

    }

    public void ENDIN(int dest, int scr) {
        System.err.println("ILLEGAL_RESOURCE exception");

    }

    public void GETR(int dest, int scr) {
        System.err.println("ILLEGAL_RESOURCE exception");

    }

    public void GETST(int dest, int scr) {
        System.err.println("ILLEGAL_RESOURCE exception");

    }

    public void GETTS(int dest, int scr) {
        System.err.println("ILLEGAL_RESOURCE exception");

    }

    public void INSHR(int dest, int scr) {
        System.err.println("ILLEGAL_RESOURCE exception");

    }

    public void MJOIN(int resId) {
        System.err.println("ILLEGAL_RESOURCE exception");

    }

    public void MSYNC(int resId) {
        System.err.println("ILLEGAL_RESOURCE exception");

    }

    public void OUTSHR(int dest, int scr) {
        System.err.println("ILLEGAL_RESOURCE exception");

    }

    public void PEEK(int dest, int scr) {
        System.err.println("ILLEGAL_RESOURCE exception");

    }

    public void SETCLK(int scr, int dest) {
        System.err.println("ILLEGAL_RESOURCE exception");

    }

    public void SETPSC(int scr, int scr1) {
        System.err.println("ILLEGAL_RESOURCE exception");

    }

    public void SETPT(int scr, int scr1) {
        System.err.println("ILLEGAL_RESOURCE exception");

    }

    public void SETRDY(int scr, int dest) {
        System.err.println("ILLEGAL_RESOURCE exception");

    }

    public void SETTW(int resId, int dest) {
        System.err.println("ILLEGAL_RESOURCE exception");

    }

    public void TINITCP(int dest, int scr) {
        System.err.println("ILLEGAL_RESOURCE exception");

    }

    public void TINITDP(int dest, int scr) {
        System.err.println("ILLEGAL_RESOURCE exception");

    }

    public void TINITLR(int dest, int scr) {
        System.err.println("ILLEGAL_RESOURCE exception");

    }

    public void TINITPC(int dest, int scr) {
        System.err.println("ILLEGAL_RESOURCE exception");

    }

    public void TINITSP(int dest, int scr) {
        System.err.println("ILLEGAL_RESOURCE exception");

    }

    public void TSETR(int dest, int scr1, int scr2) {
        System.err.println("ILLEGAL_RESOURCE exception");

    }

    public void TSTART(int threadId) {
        System.err.println("ILLEGAL_RESOURCE exception");

    }

    public void CLRPT(int resId, int resType, Expression expression) {
        // TODO Auto-generated method stub
        
    }

    public void EDU(int resId, int resType) {
        // TODO Auto-generated method stub
        
    }

    public void EEF(int resId, int resType, Expression expression) {
        // TODO Auto-generated method stub
        
    }

    public void EET(int resId, int resType, Expression expression) {
        // TODO Auto-generated method stub
        
    }

    public void EEU(int resId, int resType) {
        // TODO Auto-generated method stub
        
    }

    public void ENDIN(int resId, int resType, Expression expression) {
        // TODO Auto-generated method stub
        
    }

    public void FREER(int resId, int resType) {
        // TODO Auto-generated method stub
        
    }

    public void GETD(int resId, int resType, Expression expression) {
        // TODO Auto-generated method stub
        
    }

    public void GETST(int resId, int resType, Expression dest) {
        // TODO Auto-generated method stub
        
    }

    public void GETTS(int resId, int resType, Expression expression) {
        // TODO Auto-generated method stub
        
    }

    public void IN(int dest, int scr, Expression expression) {
        // TODO Auto-generated method stub
        
    }

    public void INCT(int resId, int resType, Expression expression) {
        // TODO Auto-generated method stub
        
    }

    public void INSHR(int resId, int resType, Expression expression) {
        // TODO Auto-generated method stub
        
    }

    public void INT(int resId, int resType, Expression expression) {
        // TODO Auto-generated method stub
        
    }

    public void MJOIN(int resId, int resType) {
        // TODO Auto-generated method stub
        
    }

    public void MSYNC(int resId, int resType) {
        // TODO Auto-generated method stub
        
    }

    public void OUT(int scr, int scr1, Expression expression) {
        // TODO Auto-generated method stub
        
    }

    public void OUTCT(int resId, int resType, Expression expression) {
        // TODO Auto-generated method stub
        
    }

    public void OUTSHR(int resId, int resType, Expression expression) {
        // TODO Auto-generated method stub
        
    }

    public void OUTT(int resId, int resType, Expression expression) {
        // TODO Auto-generated method stub
        
    }

    public void PEEK(int resId, int resType, Expression expression) {
        // TODO Auto-generated method stub
        
    }

    public void SETC(int resId, int resType, Expression expression) {
        // TODO Auto-generated method stub
        
    }

    public void SETCLK(int resId, int resType, Expression expression) {
        // TODO Auto-generated method stub
        
    }

    public void SETD(int resId, int resType, Expression expression) {
        // TODO Auto-generated method stub
        
    }

    public void SETPSC(int resId, int resType, Expression expression) {
        // TODO Auto-generated method stub
        
    }

    public void SETPT(int resId, int resType, Expression expression) {
        // TODO Auto-generated method stub
        
    }

    public void SETRDY(int resId, int resType, Expression expression) {
        // TODO Auto-generated method stub
        
    }

    public void SETTW(int resId, int resType, Expression expression) {
        // TODO Auto-generated method stub
        
    }

}
