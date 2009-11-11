ifndef $(TARGETDIR)
TARGETDIR = .
endif

INCLUDES = -I.

VPATH = .

OBJECTS = \
$(TARGETDIR)/pwmMain.o \
$(TARGETDIR)/pwmThread.o \
$(TARGETDIR)/rawSampleThread.o \
$(TARGETDIR)/sine.o \
$(TARGETDIR)/interS1Thread.o \
$(TARGETDIR)/interS2Thread.o

$(TARGETDIR)/test.xe: $(OBJECTS)
	xcc -target=$(TARGET) -o $(TARGETDIR)/test.xe $(OBJECTS)

$(TARGETDIR)/%.o: %.c
	xcc $(INCLUDES) -g -O3 -Wall -Werror -c -o "$@" "$<"

$(TARGETDIR)/%.o: %.xc
	xcc $(INCLUDES) -fxta-info -g -O3 -Wall -Werror -c -o "$@" "$<"

clean:
	rm -rf $(OBJECTS)
	rm -rf $(TARGETDIR)/test.xe
