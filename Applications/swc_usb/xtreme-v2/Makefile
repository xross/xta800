ifndef TARGETDIR
$(error TARGETDIR must be defined)
endif

OBJECTS = \
    $(TARGETDIR)/audio_xtreme.o \
    $(TARGETDIR)/physical_xtreme.o \
    $(TARGETDIR)/spdif_audio_if.o \
    $(TARGETDIR)/midiuart.o \
    $(TARGETDIR)/midi2usbmidi.o \
    $(TARGETDIR)/codec.o \
    $(TARGETDIR)/hid.o \
    $(TARGETDIR)/sof_clock.o \
    $(TARGETDIR)/endpoint0.o \
    $(TARGETDIR)/XUD_DoEnumReqs.o \
    $(TARGETDIR)/audio.o \
    $(TARGETDIR)/codec_akm.o \
    $(TARGETDIR)/mixerCall.o \
    $(TARGETDIR)/deliverCall.o \
    $(TARGETDIR)/adat_tx.o \
    $(TARGETDIR)/spdif_tx_single.o \
    $(TARGETDIR)/param.o \
    $(TARGETDIR)/deliverNew.o \
    $(TARGETDIR)/spdif_rx.o \
    $(TARGETDIR)/adat48000.o \
    $(TARGETDIR)/adat44100.o \
    $(TARGETDIR)/usbRecord.o \
    $(TARGETDIR)/XUD_Manager.o \
    $(TARGETDIR)/XUD_LLD_Line.o \
    $(TARGETDIR)/xud_buff_manager.o \
    $(TARGETDIR)/xud_ep_manager.o \
    $(TARGETDIR)/XUD_DeviceAttach.o \
    $(TARGETDIR)/XUD_LLD_Io.o \
    $(TARGETDIR)/XUD_EpFunctions.o \
    $(TARGETDIR)/XUD_LLD_IoLoop.o \
    $(TARGETDIR)/xud_control.o \
    $(TARGETDIR)/ep_man_select.o \
    $(TARGETDIR)/ledTrapHandler.o \
    $(TARGETDIR)/line_select.o \
    $(TARGETDIR)/ssctrl.o \
    $(TARGETDIR)/XUD_BuffMan.o \
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
    $(TARGETDIR)/uifm_reg_read.o

INCLUDES = \
    -I ./xud/include \
    -I ./xud/src \
    -I ./xud/src/uifm \
    -I ./xud/src/support \
    -I ./apps/audio/xtreme-v2/src \
    -I ./apps/audio/shared

FLAGS = -g -Wall  -O3 -D UIFM_MODE=2  -fasm-linenum -fcomment-asm  -fsubword-select

DEFINES = -DSPDIF -DASYNC -D UIFM_MODE=2 -D VENDOR_LSB=0x92 -D VENDOR_MSB=0x0A -D PRODUCT_LSB=0x11 -D PRODUCT_MSB=0x70 -g  

VPATH = ./xud/src:./xud/src/support:./xud/src/uifm:./apps/audio/xtreme-v2/src:./apps/audio/shared

$(TARGETDIR)/test.xe : $(OBJECTS)
	xcc -target=XC-1 $(OBJECTS) -o $(TARGETDIR)/test.xe
	
$(TARGETDIR)/%.o : %.xc
	xcc $(FLAGS) $(DEFINES) $(INCLUDES) -c $< -o $@
	
$(TARGETDIR)/%.o : %.S
	xcc $(FLAGS) $(DEFINES) $(INCLUDES) -c $< -o $@

