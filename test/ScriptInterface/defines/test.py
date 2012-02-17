import sys
import java

def display(defines):
    for define in defines:
        print define    

try:
    xta.addBranch("callf", ["f"]);
    for branch in xta.getBranches():
        for target in xta.getBranchTargets(branch):
            print branch, target    
    xta.removeBranch("callf", ["f"]);
    for branch in xta.getBranches():
        for target in xta.getBranchTargets(branch):
            print branch, target    

    xta.addExclusion("g");
    display(xta.getExclusions());
    xta.removeExclusion("g");
    display(xta.getExclusions());
   
    xta.addLoop("h", 10);
    display(xta.getLoops())
    xta.removeLoop("h");
    display(xta.getLoops())

    xta.addLoopPath("i", 10);
    display(xta.getLoopPaths())
    xta.removeLoopPath("i")
    display(xta.getLoopPaths())

    xta.addLoopScope("j", True)
    display(xta.getLoopScopes())
    xta.removeLoopScope("j");
    display(xta.getLoopScopes())

    xta.addInstructionTime("k", 10.0, "us")
    display(xta.getInstructionTimes())
    xta.removeInstructionTime("k");
    display(xta.getInstructionTimes())

    xta.addFunctionTime("l", 10.0, "us")
    display(xta.getFunctionTimes())
    xta.removeFunctionTime("l");
    display(xta.getFunctionTimes())

except java.lang.Exception, e:
    print e.getMessage()

