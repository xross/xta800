import sys
import java

DEBUG = False

def checkNumErrors(xta, id, required):
    if DEBUG:
        for error in xta.getErrors(id):
            print "ERROR: %s" % error
    got = len(xta.getErrors(id))
    if got != required:
        print "Incorrect number of errors detected: required: %d, got: %d" % (required, got)

def checkNumWarnings(xta, id, required):
    if DEBUG:
        for warning in xta.getWarnings(id):
            print "WARNING: %s" % warning
    got = len(xta.getWarnings(id))
    if got != required:
        print "Incorrect number of warnings detected: required: %d, got: %d" % (required, got)

def checkLessThan(t1, t2):
    if DEBUG:
        print "checkLessThan: %f <= %f" % (t1, t2)
    if t2 <= t1:
        print "Time not incremented"

def doAnalyze(xta, functionName, numErrors, numWarnings, callback):
    print "+" + functionName
    ids = xta.analyzeFunction(functionName)
    for id in ids:
        checkNumErrors(xta, id, numErrors)
        checkNumWarnings(xta, id, numWarnings)
        callback(xta, id);

def doLoop(xta, id):
    t1 = xta.getWorstCase(id, "ns") 
    xta.setLoop(id, "_loop", 5)
    t2 = xta.getWorstCase(id, "ns") 
    checkLessThan(t1, t2)

def doLoopPath(xta, id):
    t1 = xta.getWorstCase(id, "ns") 
    xta.setLoop(id, "_looppath", 5)
    t2 = xta.getWorstCase(id, "ns") 
    checkLessThan(t1, t2)

    xta.setLoopPath(id, "if", 1)
    t3 = xta.getWorstCase(id, "ns") 
    checkLessThan(t3, t2)

def doLoopScope(xta, id):
    t1 = xta.getWorstCase(id, "ns") 
    xta.setLoop(id, "outer", 5)
    t2 = xta.getWorstCase(id, "ns") 
    checkLessThan(t1, t2)

    xta.setLoop(id, "inner", 5)
    t3 = xta.getWorstCase(id, "ns") 
    checkLessThan(t2, t3)

    xta.setLoopScope(id, "inner", True)
    t4 = xta.getWorstCase(id, "ns") 
    checkLessThan(t4, t3)

def doFunctionTime(xta, id):
    t1 = xta.getWorstCase(id, "ns") 
    xta.setFunctionTime(id, "f", 999.0, "ns")
    t2 = xta.getWorstCase(id, "ns") 
    checkLessThan(t1, t2)

def doPathTime(xta, id):
    t1 = xta.getWorstCase(id, "ns") 
    xta.setPathTime(id, "start", "stop", 999.0, "ns")
    t2 = xta.getWorstCase(id, "ns") 
    checkLessThan(t1, t2)

def doInstructionTime(xta, id):
    t1 = xta.getWorstCase(id, "ns") 
    xta.setInstructionTime(id, "inst", 999.0, "ns")
    t2 = xta.getWorstCase(id, "ns") 
    checkLessThan(t1, t2)

def doExclusion(xta, id):
    t1 = xta.getWorstCase(id, "ns") 
    xta.setExclusion(id, "_exclusion")
    t2 = xta.getWorstCase(id, "ns") 
    checkLessThan(t2, t1)

try:
    doAnalyze(xta, "loop", 1, 0, doLoop)
    doAnalyze(xta, "looppath", 1, 0, doLoopPath)
    doAnalyze(xta, "loopscope", 2, 0, doLoopScope)
    doAnalyze(xta, "functiontime", 0, 0, doFunctionTime)
    doAnalyze(xta, "pathtime", 0, 3, doPathTime)
    doAnalyze(xta, "instructiontime", 0, 1, doInstructionTime)
    doAnalyze(xta, "exclusion", 0, 0, doExclusion)

except java.lang.Exception, e:
    print e.getMessage()

