.file "XDK.s"
  .section .netinfo, "", @netinfo
.globl __xnc_version
.set __xnc_version,8
.globl __main_default_network, "g:nw(1:nd)"
  .align 4
__main_default_network:
  .long __network_node0
  .long __network_links
  .long __boot_order

  .align 4
__network_node0:
.locl __network_node0, "nd(4:cr)"
  .long __network_node_config_name0
  .long __network_node_user_name0
  .long __network_node_configure_block0
  .long stdcore+(0*4)
  .long 0
  .long stdcore+(1*4)
  .long 0
  .long stdcore+(2*4)
  .long 0
  .long stdcore+(3*4)
  .long 0
__network_node_config_name0:
  .asciiz "XS1-G4B-FB512"
__network_node_user_name0:
  .asciiz "0"
  .align 4
__network_node_configure_block0:
  .long 0

.globl stdcore, "a(4:cr)"
.globl stdcore.globound
.set stdcore.globound, 4
  .align 4
stdcore:
  .space 16

  .align 4
.locl __network_links, "a(0:nl)"
__network_links:

  .align 4
__boot_order:
  .long 1
  .long __network_node0
  .long __network_node0

