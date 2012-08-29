ifndef TARGETDIR
$(error TARGETDIR must be defined)
endif

OBJECTS = \
    $(TARGETDIR)/XUD_Manager.o \
    $(TARGETDIR)/XUD_DeviceAttach.o \
    $(TARGETDIR)/XUD_LLD_Io.o \
    $(TARGETDIR)/XUD_EpFunctions.o  \
    $(TARGETDIR)/XUD_LLD_IoLoop.o \
    $(TARGETDIR)/ledTrapHandler.o \
    $(TARGETDIR)/ssctrl.o \
    $(TARGETDIR)/XUD_EpFuncs.o \
    $(TARGETDIR)/delay.o \
    $(TARGETDIR)/outpw.o \
    $(TARGETDIR)/sr.o \
    $(TARGETDIR)/inpw.o \
    $(TARGETDIR)/lock.o \
    $(TARGETDIR)/channels.o \
    $(TARGETDIR)/XUD_UIFM_PConfig.o \
    $(TARGETDIR)/uifm_enable.o \
    $(TARGETDIR)/uifm_reg_write.o \
    $(TARGETDIR)/setAddress.o \
    $(TARGETDIR)/uifm_reg_read.o \
    $(TARGETDIR)/main.o \
    $(TARGETDIR)/endpoint0.o \
    $(TARGETDIR)/XUD_DoEnumReqs.o \
    $(TARGETDIR)/mixerCall.o

INCLUDES = \
    -I ./xud_lite/include \
    -I ./xud_lite/src \
    -I ./xud_lite/src/uifm \
    -I ./xud_lite/src/support \
    -I ./apps/L1_audio/src \
    -I ./apps/audio/shared

FLAGS = -Wall -g -O3 -fasm-linenum -fcomment-asm -fsubword-select -target=XS1-L1A-TQ128

DEFINES = -DASYNC -DSPDIF -D UIFM_MODE=2 -D VENDOR_LSB=0x92 -D VENDOR_MSB=0x0A -D PRODUCT_LSB=0x11 -D PRODUCT_MSB=0x70 -D G1

VPATH = ./xud_lite/src:./xud_lite/src/support:./xud_lite/src/uifm:./apps/L1_audio/src

$(TARGETDIR)/test.xe : $(OBJECTS)
	xcc -target=XS1-L1A-TQ128 $(OBJECTS) -o $(TARGETDIR)/test.xe
	
$(TARGETDIR)/%.o : %.xc
	xcc $(FLAGS) $(DEFINES) $(INCLUDES) -c $< -o $@
	
$(TARGETDIR)/%.o : %.S
	xcc $(FLAGS) $(DEFINES) $(INCLUDES) -c $< -o $@

