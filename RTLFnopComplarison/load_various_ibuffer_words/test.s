.text

data:
.word 0
 
.align 2
.globl _start
_start:
  bl main
  
.type main, @function
.globl main
main:
  entsp 0
  # initialise data pointer
  ldap r11, data
  bu test0

.align 4
test0:
  add r0, r0, r0
  add r0, r0, r0
  add r0, r0, r0
  ldw r0, r11[0]
  ldw r0, r11[0]
  ldw r0, r11[0]
  ldw r0, r11[0]
  ldw r0, r11[0]
  bu test1

.align 4
test1:
  add r0, r0, r0
  add r0, r0, r0
  ldw r0, r11[0]
  ldw r0, r11[0]
  ldw r0, r11[0]
  ldw r0, r11[0]
  ldw r0, r11[0]
  bu test2

.align 4
test2:
  add r0, r0, r0
  ldw r0, r11[0]
  ldw r0, r11[0]
  ldw r0, r11[0]
  ldw r0, r11[0]
  ldw r0, r11[0]
  bu test3

.align 4
test3:
  ldw r0, r11[0]
  ldw r0, r11[0]
  ldw r0, r11[0]
  ldw r0, r11[0]
  ldw r0, r11[0]
  bu test4

.align 4
test4:
  ldw r0, r11[0]
  add r0, r0, r0
  add r0, r0, r0
  add r0, r0, r0
  ldw r0, r11[0]
  ldw r0, r11[0]
  ldw r0, r11[0]
  ldw r0, r11[0]
  bu test5

.align 4
test5:
  ldw r0, r11[0]
  add r0, r0, r0
  add r0, r0, r0
  ldw r0, r11[0]
  ldw r0, r11[0]
  ldw r0, r11[0]
  ldw r0, r11[0]
  bu test6

.align 4
test6:
  ldw r0, r11[0]
  add r0, r0, r0
  ldw r0, r11[0]
  ldw r0, r11[0]
  ldw r0, r11[0]
  ldw r0, r11[0]

  retsp 0
