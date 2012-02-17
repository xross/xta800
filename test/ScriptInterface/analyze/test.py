import sys
import java

try:
    xta.load(sys.argv[1] + "/test.xe");
except java.lang.Exception, e:
    print e.getMessage()

try:
    ids = xta.analyzeFunction("main");
    for id in ids:
        print xta.getRouteDescription(id),
        print xta.getWorstCase(id, "ns"),
        print xta.getBestCase(id, "ns")

    ids = xta.analyzeLoop("loop");
    for id in ids:
        print xta.getRouteDescription(id),
        print xta.getWorstCase(id, "ns"),
        print xta.getBestCase(id, "ns")

except java.lang.Exception, e:
    print e.getMessage()

try:
    xta.analyzeFunction("Invalid");
except java.lang.Exception, e:
    print e.getMessage()

try:
    xta.analyzeLoop("Invalid");
except java.lang.Exception, e:
    print e.getMessage()
