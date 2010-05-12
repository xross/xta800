#include "CanIncludes.h"

on stdcore[0] : clock canClk = XS1_CLKBLK_1;

on stdcore[0] : buffered in port:32 canRx = XS1_PORT_1A;
on stdcore[0] :             port    canTx = XS1_PORT_1B;

int main() {
	chan rxChan, txChan;

	par {
		on stdcore[0]: canPhy(rxChan, txChan, canClk, canRx, canTx);
		on stdcore[0]: canNetworkRx(rxChan);
		on stdcore[0]: canNetworkTx(txChan);
	}
	return 0;
}
