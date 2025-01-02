// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen
// by writing 'black' in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen by writing
// 'white' in every pixel;
// the screen should remain fully clear as long as no key is pressed.

//// Replace this comment with your code.

//approach: Fill while key held.
@8192 // Initialize max = 8192
D=A
@max
M=D

@0 // Initialize min = 0
D=A
@min
M=D

@i // Initialize i = 0
M=D

(LOOP)
    @KBD // Check if KBD is 0
    D=M
    @CLEAR
    D;JEQ

    @FILL
    0;JMP

(CLEAR)
    @min
    D=M
    @SCREEN
    A=A+D
    M=0

    @min
    D=M
    @max
    D=D-M
    @LOOP
    D;JEQ

    @min
    D=M
    D=D+1 // D++
    @min
    M=D

    @LOOP
    0;JMP

(FILL)
    @min
    D=M
    @SCREEN
    A=A+D
    M=-1

    @min
    D=M
    @max
    D=D-M
    @LOOP
    D;JEQ

    @min
    D=M
    D=D+1 // D++
    @min
    M=D

    @LOOP
    0;JMP


