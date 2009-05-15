ifndef $(TARGETDIR)
TARGETDIR = .
endif

TARGET = $(TARGETDIR)/test.xe

SOURCEDIR = source

SOURCES = \
$(SOURCEDIR)/AES.xc \
$(SOURCEDIR)/DecryptTables.xc \
$(SOURCEDIR)/EncryptTables.xc \
$(SOURCEDIR)/InvKeyExpansionTables.xc \
$(SOURCEDIR)/sBoxInv.xc \
$(SOURCEDIR)/sBox.xc \
$(SOURCEDIR)/Decrypt.S \
$(SOURCEDIR)/Encrypt.S \
$(SOURCEDIR)/InvKeyExpansion.S \
$(SOURCEDIR)/KeyExpansion.S \

all: $(TARGET)

$(TARGET):
	xcc $(SOURCES) -o $(TARGET)

