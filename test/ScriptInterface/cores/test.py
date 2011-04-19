import sys
import java

def displayCores():
    for core in xta.getCores():
         print core,
    print

try:
    displayCores()

    xta.removeCore("stdcore[0]")
    displayCores()

    xta.addCore("stdcore[0]")
    displayCores()

except java.lang.Exception, e:
    print e.getMessage()

