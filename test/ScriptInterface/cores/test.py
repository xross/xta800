import sys
import java

def displayCores():
    for core in xta.getCores():
         print core,
    print

try:
    displayCores()

    xta.removeCore("tile[0]")
    displayCores()

    xta.addCore("tile[0]")
    displayCores()

except java.lang.Exception, e:
    print e.getMessage()

