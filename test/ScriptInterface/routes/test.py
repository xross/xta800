import sys
import java

def printRoutes(xta):
    ids = xta.getRouteIds()
    for id in ids:
        print id,
    print

try:
    xta.analyzeFunction("f")
    xta.analyzeFunction("g")
    xta.analyzeFunction("h")

    printRoutes(xta) 
    for i in range(0, 3):
        xta.removeRoute(i)
        printRoutes(xta) 

except java.lang.Exception, e:
    print e.getMessage()

