(BEGIN)
	// Initialize product to 0.
	@R2
	M=0
(LOOP)
	
	@R1
	D=M
	@END
	D;JLE
	
	@R0
	D=M
	@R2
	M=M+D
	
	@R1
	M=M-1
	
	@LOOP
	0;JMP
(END)
	// Terminate.
	@END
	0;JMP



//i = RAM[1]
// @1
// D = M
// @counter
// M=D 

// //Create new variable
// @0
// D=A 
// @toReturn
// M=D

// (LOOP)
// //if i <= 0, break
// @counter
// D=M
// @END
// D;JLE

// D=D-1
// @counter
// M=D

// //else load R0 to toReturn
// @toReturn
// D=M
// D=D+M
// @toReturn
// M=D 

// @LOOP
// 0;JMP
// (END)

// @toReturn
// D=M
// @2
// M=D 
