SOURCES = source/AES.xc \
          source/DecryptTables.xc \
          source/EncryptTables.xc \
          source/InvKeyExpansionTables.xc \
          source/sBoxInv.xc \
          source/sBox.xc \
          source/Decrypt.xc \
          source/Encrypt.xc \
          source/InvKeyExpansion.xc \
          source/KeyExpansion.xc

all:
	@xcc -target=$(TARGET) -O2 $(SOURCES) -o $(GENDIR)/test.xe
