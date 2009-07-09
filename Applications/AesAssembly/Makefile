ifndef $(TARGETDIR)
TARGETDIR = .
endif

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

all: $(TARGETDIR)/test.xe

$(TARGETDIR)/test.xe:
	@xcc $(SOURCES) -target=$(TARGET) -o $(TARGETDIR)/test.xe

