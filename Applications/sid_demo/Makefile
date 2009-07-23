ifndef $(TARGETDIR)
TARGETDIR = .
endif

INCLUDES = -I.

VPATH = .

OBJECTS = \
$(TARGETDIR)/sid_store_core_1.o \
$(TARGETDIR)/sid_store_core_2.o \
$(TARGETDIR)/sid_store_core_3.o \
$(TARGETDIR)/siddata.o \
$(TARGETDIR)/sid_player.o \
$(TARGETDIR)/sid.o \
$(TARGETDIR)/led_controller.o \
$(TARGETDIR)/fft.o \
$(TARGETDIR)/buffer.o \
$(TARGETDIR)/main.o

$(TARGETDIR)/test.xe: $(OBJECTS)
	xcc -o $(TARGETDIR)/test.xe $(OBJECTS)

$(TARGETDIR)/%.o: %.c
	xcc $(INCLUDES) -g -O3 -Wall -Werror -c -o "$@" "$<"

$(TARGETDIR)/%.o: %.xc
	xcc $(INCLUDES) -fxta-info -g -O3 -Wall -Werror -c -o "$@" "$<"

clean:
	rm -rf $(OBJECTS)
	rm -rf $(TARGETDIR)/test.xe
