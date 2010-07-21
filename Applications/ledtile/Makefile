ifndef $(TARGETDIR)
TARGETDIR = .
endif

INCLUDES = \
-Ibuffers/ledbuffer \
-Ibuffers/pktbuffer \
-Iethernet/dualmii \
-Iethernet/localServer \
-Iethernet/switch \
-Iled/driver \
-Iled/processing \
-Isupport/misc \
-Isupport/retrieveMAC \
-Isupport/spiflash \
-Isupport/watchdog \
-I.

VPATH = buffers/ledbuffer:\
buffers/pktbuffer:\
ethernet/dualmii:\
ethernet/localServer:\
ethernet/switch:\
led/driver:\
led/processing:\
support/misc:\
support/retrieveMAC:\
support/spiflash:\
support/watchdog:\
.

OBJECTS = \
$(TARGETDIR)/logo.o \
$(TARGETDIR)/main.o \
$(TARGETDIR)/watchdog.o \
$(TARGETDIR)/flashManager.o \
$(TARGETDIR)/flashPhy.o \
$(TARGETDIR)/xbhandler.o \
$(TARGETDIR)/getmac.o \
$(TARGETDIR)/ctrl.o \
$(TARGETDIR)/math.o \
$(TARGETDIR)/reset.o \
$(TARGETDIR)/sleep.o \
$(TARGETDIR)/ledprocess.o \
$(TARGETDIR)/leddriver.o \
$(TARGETDIR)/mbi5026.o \
$(TARGETDIR)/mbi5026_support.o \
$(TARGETDIR)/mbi5030.o \
$(TARGETDIR)/resetres.o \
$(TARGETDIR)/ethSwitch.o \
$(TARGETDIR)/ethIp.o \
$(TARGETDIR)/ethLed.o \
$(TARGETDIR)/ethServer.o \
$(TARGETDIR)/ethServerProcess.o \
$(TARGETDIR)/ethTftp.o \
$(TARGETDIR)/mii.o \
$(TARGETDIR)/miimain.o \
$(TARGETDIR)/smi.o \
$(TARGETDIR)/pktbuffer.o \
$(TARGETDIR)/ledbuffer.o

$(TARGETDIR)/test.xe: $(OBJECTS)
	xcc -target=$(TARGET) -g -o $(TARGETDIR)/test.xe $(OBJECTS)

$(TARGETDIR)/%.o: %.c
	xcc -target=$(TARGET) $(INCLUDES) -g -O3 -Wall -c -o "$@" "$<"

$(TARGETDIR)/%.o: %.xc
	xcc -target=$(TARGET) $(INCLUDES) -fxta-info -g -O3 -Wall -c -o "$@" "$<"

clean:
	rm -rf $(OBJECTS)
	rm -rf $(TARGETDIR)/test.xe
