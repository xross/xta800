import sys
import java

def loadExe(fileName):
    try:
        xta.load(fileName);
        print "Success"

    except java.lang.Exception, e:
        print e.getMessage()

loadExe(sys.argv[1] + "/test.xe");
loadExe(sys.argv[1] + "/missing.xe");
