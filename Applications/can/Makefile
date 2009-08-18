
SOURCE = source
INCLUDES = -I include
FILES = $(SOURCE)/CanFunctions.xc $(SOURCE)/CanExtra.c $(SOURCE)/CanAsm.S $(SOURCE)/CanPhyRx.S

all:
	xcc -Wall -O2  -g $(FILES) $(INCLUDES) -target=$(TARGET) -o $(TARGETDIR)/can.xe

