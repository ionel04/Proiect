
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.lang.Math; 
class Reader {
    
    public static  Memory createInstructionMemory() {
        Memory instructionMemory = new Memory(1024);
        try {
            int indexAdresare = 0; 
            File myObj = new File("in.txt");
            Scanner myReader = new Scanner(myObj);
            List<LinieCurata> Program = new ArrayList<>();
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                LinieCurata instructiune = new LinieCurata(data);
                Program.add(instructiune);
            }
            myReader.close();
            int curent=0;
            for (LinieCurata linie : Program) {
                System.out.println(linie);
                try {
                    if (linie.returnLiniaCurata()[0].equals("invalid")) {
                        throw new UnknownInstruction("Instructiune invalida: " + linie.returnLiniaCurata()[1]);
                    }
                } catch (UnknownInstruction e) {
                    System.out.println("Instructiune necunoscuta");
                }

                Instructiune instructiune = new Instructiune(linie, curent, Program);
                int[] rez = instructiune.returnRez();
                
                instructionMemory.storeInstruction(indexAdresare, rez);
                indexAdresare += 4; 

                for (int i = 31; i >= 0; i--) {
                    System.out.print(rez[i]);
                }
                System.out.println("");
                curent++;
                
                
            } 
             //byte [] memoriePentruAfisare = instructionMemory.getMemory(); 
                // for(int i=0; i<8; i++) 
                // { 
                //     System.out.println(memoriePentruAfisare[i]);
                //     String binaryString = String.format("%8s", Integer.toBinaryString(memoriePentruAfisare[i]& 0xFF)).replace(' ', '0');

                //     System.out.println(binaryString);
                // } 
                
               
            
        } catch (FileNotFoundException e) {
            System.out.println("Eroare la citirea fisierului.");
            e.printStackTrace();
        } catch (UnknownInstruction e) {
            System.out.println("Instructiune necunoscuta");
            System.exit(1);
        } catch (LabelNotExisting e) {
            System.out.println("Nu exista labe-ul");
            System.exit(1);
        }
        return instructionMemory;
    }
    
}
// ADRESELE IN RISC V SUNT PE 5 BITI.
// adresa R0 este = 00000
// adresa R1 este = 00001
// adresa R2 este = 00010
// adresa R3 este = 00011
// adresa R4 este = 00100
// adresa R5 este = 00101
// adresa R6 este = 00110
// adresa R7 este = 00111

// adresa R8 este SP(stack pointer); 

/*
 * DE IMPLEMENTAT:
 * LW, SW;
 * JAL, JMS;
 * BNE, BEQ, BNZ;
 * HTL;
 * PSH, POP; 
 */

// IMPORTANT valorile pe care le vom putea folsi vor fi [-2048; +2047]. 
// bitul 25 este bitul de semn di instructiune.  

class UnknownInstruction extends Exception {
    public UnknownInstruction(String msg) {
        super(msg);
    }
}

class LabelNotExisting extends Exception {
    public LabelNotExisting(String msg) {
        super(msg);
    }
}
class LinieCurata {
    private String eticheta;
    private String instructiune;
    private String input;
    private String[] tokens; // Vectorul de stringuri in care vom salva fiecare parte a instructiunii.

    public int isInstruction(String aux){
        int instr = 0;
        // Verificam daca instructiunea este valida
        switch (aux){
            case "ADD":    // OK
                instr = 1;
                break;
            
            case "ADDI":   //OK
                instr = 1;
                break;  
            
            case "AND":    //OK
                instr = 1;
                break;       
            
            case "OR":     //OK
                instr = 1;
                break;

            case "SUB":     //OK
                instr = 1;
                break;
         
            case "SUBI":    //OK 
                instr = 1;
                break;

            case "MUL":     //OK
                instr = 1;
                break;

            case "LW":      //OK
                instr = 1;
                break;
            
            case "DIV":     //OK
                instr = 1;
                break;
            
            case "SW":      //OK 
                instr = 1;
                break;

            case "MOV":     // ne trebuie neaparat ? in manual de risc scrie ca in spate sunt implementate cu ADDI
                instr = 1;
                break;

            case "JAL":
                instr = 1;
                break;

            case "JALR":
                instr = 1;
                break;

            case "BEQ":
                instr = 1; 
                break;

            case "BNE":
                instr = 1; 
                break;

            case "BLT":
                instr = 1; 
                break;

            // case "BGT":
            //     instr = 1; 
            //     break;

            case "BLTU":
                instr = 1; 
                break;

            case "BGEU":
                instr = 1; 
                break;

            case "BGE":
                instr = 1; 
                break;
            
            case "HLT":     // OK
                instr = 1;
                break;

            case "REM":     //OK
                instr = 1;
                break; 
     
            case "POP":    //ok
                instr = 1; 
                break;

            case "PSH":
                instr = 1; 
                break;
            default:
                break;
        }
        return instr;
    }

    String removeConsecutiveSpaces(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("\\s+", " ").trim();
    }

    LinieCurata(String input) {
        this.input = input;
        this.input = removeConsecutiveSpaces(this.input); // Eliminam spatiile consecutive
        this.input = this.input.toUpperCase(); // Transformam in litere mari
        String delims = "[ ,()]"; // Separator: spatiu, virgula, paranteze
        this.tokens = this.input.split(delims);

        if (isInstruction(tokens[0]) == 1) {
            this.input = "- " + this.input;
            this.tokens = this.input.split(delims);
        }

        this.instructiune = tokens[1];
        this.eticheta = tokens[0];

        if (isInstruction(this.instructiune) == 0) {
            tokens[0] = "invalid ";
        }
    }

    public String[] returnLiniaCurata() {
        return this.tokens;
    }

    public String toString() {
        String rez = "";
     
        for (String i : tokens) {
            rez = rez + i;
          
        }
        return rez;
    }
}

class Instructiune {
    private int[] rez;

    Instructiune(LinieCurata linieCurata, int curent , List<LinieCurata> program ) throws UnknownInstruction,  LabelNotExisting {
        rez = new int[32];
        String[] tokens = linieCurata.returnLiniaCurata();

        switch (tokens[1]) {
            case "ADD":
                setOpcode("0110011");
                setFunct3("000");
                setFunct7("0000000");
                setRegisters(tokens[2], tokens[3], tokens[4]);
                break;
                
            case "OR":
                setOpcode("0110011");
                setFunct3("110");
                setFunct7("0000000");
                setRegisters(tokens[2], tokens[3], tokens[4]);
                break;

            case "AND":
                setOpcode("0110011");
                setFunct3("111");
                setFunct7("0000000");
                setRegisters(tokens[2], tokens[3], tokens[4]);
                break;

            case "ADDI":
                setOpcode("0010011");
                setFunct3("000");
                setRegistersImmediate(tokens[2], tokens[3], tokens[4]);
                break;
                
            case "SUBI": // MAI E DE LUCRAT AICI 
                setOpcode("0010011");
                setFunct3("000");
                setRegistersImmediateSubi(tokens[2], tokens[3], tokens[4]);
                
                break;

            case "SUB":
                setOpcode("0110011");
                setFunct3("000");
                setFunct7("0100000");
                setRegisters(tokens[2], tokens[3], tokens[4]);
                break;

            case "MUL":
                setOpcode("0110011");
                setFunct3("000");
                setFunct7("0000001");
                setRegisters(tokens[2], tokens[3], tokens[4]);
                break;

            case "DIV":
                setOpcode("0110011");
                setFunct3("100");
                setFunct7("0000001");
                setRegisters(tokens[2], tokens[3], tokens[4]);
                break;

            case "REM":
                setOpcode("0110011");
                setFunct3("110");
                setFunct7("0000001");
                setRegisters(tokens[2], tokens[3], tokens[4]);
                break;

            case "LW": // load din memorie in registru
                setOpcode("0000011");
                setFunct3("010");
                setRegDest(tokens[2]);
                int aux = Integer.parseInt(tokens[3]); // valoarea de offset, formula pentru adresa = imm + valoare reg baza
                setImmediateBit(aux,20,12);
                setRegBase(tokens[4]);
                break;

            case "SW": // store din registru in memorie
                setOpcode("0100011");              // Correct: opcode for store instructions (SW)
                setFunct3("010");                  // Correct: funct3 for SW is 010
                //token[3] val imediata
                setImmediateStore(tokens[3]);
                setRegBase(tokens[4]);                 
                setRs2(tokens[2]);                     
                break; 

            case "POP": 
                setOpcode("0000011");
                setRegDest(tokens[2]);  
                setFunct3("010");     
                setRegBase("R8");    //daca baza este 8 va trebui sa incremantam sau sa scadem valoare din R8  
                String immediatePOP = "111111111100";   // 4, negativ in complement de 2 
                setImmediate(immediatePOP); 
                break;

            case "PSH": 
                setOpcode("0100011"); 
                setFunct3("010");     
                setRegBase("R8");     
                setRs2(tokens[2]);
                String immediatePSH = "111111111100"; 
                String immHighPSH = immediatePSH.substring(0, 5);   
                //System.out.println(immHighPSH);
                String immLowPSH = immediatePSH.substring(5, 12);
                //System.out.println(immLowPSH); 
                 
                for(int i=7; i<=11; i++) {
                    rez[i] = immHighPSH.charAt(i - 7) - '0';
                } 
                 
                for(int i=25; i<32; i++) {
                    rez[i] = immLowPSH.charAt(i - 25) - '0';
                } 
                break; 
                
                
            case "JAL":
                setOpcode("1101111");
                setRegisterBits(11, tokens[2]);
                int offset = getOffset(tokens[3], curent, program);
                setOffsetJAL(offset);
                break;
                
            case "JALR":
                setOpcode("1100111");
                setRegisterBits(11, tokens[2]);
                setRegisterBits(19, tokens[3]);
                setFunct3("000");
                setImmediateBit(Integer.parseInt(tokens[4]),20,12);
                break;

            case "BEQ":
                setOpcode("1100011");
                setFunct3("000");
                setRegisterBits(19, tokens[2]);
                setRegisterBits(24,tokens[3]);
                offset = getOffset(tokens[4], curent, program);
                setImmediateBranch(offset);
                break;
            case "BNE":
                setOpcode("1100011");
                setFunct3("001");
                setRegisterBits(19, tokens[2]);
                setRegisterBits(24,tokens[3]);
                offset = getOffset(tokens[4], curent, program);
                setImmediateBranch(offset);
                break;
            case "BLT":
                setOpcode("1100011");
                setFunct3("100");
                setRegisterBits(19, tokens[2]);
                setRegisterBits(24,tokens[3]);
                offset = getOffset(tokens[4], curent, program);
                setImmediateBranch(offset);
                break;  
            case "BGE":
                setOpcode("1100011");
                setFunct3("101");
                setRegisterBits(19, tokens[2]);
                setRegisterBits(24,tokens[3]);
                offset = getOffset(tokens[4], curent, program);
                setImmediateBranch(offset);
                break;
            case "BLTU":
                setOpcode("1100011");
                setFunct3("110");
                setRegisterBits(19, tokens[2]);
                setRegisterBits(24,tokens[3]);
                offset = getOffset(tokens[4], curent, program);
                setImmediateBranch(offset);
                break;
            case "BGEU":
                setOpcode("1100011");
                setFunct3("111");
                setRegisterBits(19, tokens[2]);
                setRegisterBits(24,tokens[3]);
                offset = getOffset(tokens[4], curent, program);
                setImmediateBranch(offset);
                break;
            case "HLT": 
                
                break; 
                

            default:
                
                throw new UnknownInstruction("Instructiune necunoscuta: ");
        }
    }

    private void setOpcode(String opcode) {
        for (int i = 0; i < 7; i++) {
            rez[6 - i] = opcode.charAt(i) - '0';
        }
    }

    private int getOffset(String destLabel, int poz_curenta, List<LinieCurata> program ) throws LabelNotExisting
    {
        int i=0;
        for(LinieCurata line : program)
        {
            if(destLabel.equals(line.returnLiniaCurata()[0])==true)
            {
                return i-poz_curenta;
            }
            i++;
        }
        throw new LabelNotExisting("nu exista destinatia dorita");
    }

    private void setFunct3(String funct3) {
        for (int i = 0; i < 3; i++) {
            rez[14 - i] = funct3.charAt(i) - '0';
        }
    }

    private void setFunct7(String funct7) {
        for (int i = 0; i < 7; i++) {
            rez[31 - i] = funct7.charAt(i) - '0';
        }
    }

    private void setRegisters(String rd, String rs1, String rs2) {
        setRegisterBits(11, rd);
        setRegisterBits(19, rs1);
        setRegisterBits(24, rs2);
    }

    private void setRs2(String rs2) {
        setRegisterBits(24, rs2);
    }

    private void setRegDest(String rd) {
        setRegisterBits(11, rd);
    }

    private void setRegBase(String rd) {
       
        setRegisterBits(19, rd);
    }

    private void setRegistersImmediate(String rd, String rs1, String imm) {
        setRegisterBits(11, rd);
        setRegisterBits(19, rs1);
        setImmediate(StringToBin12Bits(imm));
    } 
    
    private void setRegistersImmediateSubi(String rd, String rs1, String imm) {
        setRegisterBits(11, rd);
        setRegisterBits(19, rs1);
        setImmediate(StringToBin12BitsNegative(imm));
    }



    private void setRegisterBits(int startIndex, String reg) {
        try {
            int regNum = Integer.parseInt(reg.substring(1));
            if (regNum > 8)
                throw new Exception();
            for (int i = 0; i < 5; i++) {
                rez[startIndex -5 + 1 +i] = (regNum >> i) & 1;
            }
        } catch (Exception e) {
            System.out.println("invalid format for arguments");
        }
    }

    private String StringToBin12BitsNegative(String nr)
    {  
       int number = Integer.parseInt(nr);
       
       if(number > 0) 
       {
           number = number * -1; 
           number = (1 << 12) + number;  
       } 

       else 
       {
           number = number * -1;
       }
       String bin = Integer.toBinaryString(number);
       return String.format("%12s", bin).replace(' ', '0');
    }
    private String StringToBin12Bits(String nr) {
        int number = Integer.parseInt(nr); 
        if (number < 0) {
           number = (1 << 12) + number; // (prin incantatia asta, optinem complementul de 2, aparent adaugad 4096, la numarul nostru negativ, nu pusca codul instructiuni, si obtine numarul in complement de 2, GG MAH, GG)
       }
        String bin = Integer.toBinaryString(number);
        return String.format("%12s", bin).replace(' ', '0');
    }

    private void setImmediate(String bin) {
         for (int i = 0; i < 12; i++) {
        rez[20 + i] = bin.charAt(11 - i) - '0'; // Map immediate to bits 31:20
    }   
    }

    private void setImmediateStore(String imm)
    {
        int val = Integer.parseInt(imm);
        // 7 11 - 0 4
        //25 31 - 5 11
        for(int i=7;i<=11;i++)
            rez[i] = (val>>(i-7)) & 1;
        for(int i=25;i<=31;i++)
            rez[i] = (val >> (i-20)) & 1;
    }

    private void setImmediateBit(int val, int start, int numberOFBits)
    {
        int stop = start + numberOFBits - 1;
        for(int i=start; i<=stop; i++)
        {
            rez[i]= (val>>(i-start)) & 1;
        }
    }

    private void setOffsetJAL(int offset)
    {
        if(offset>=0)
            rez[31] = 0;
        else
        {
            rez[31] = 1;
            offset = offset * (-1);
        }

        for(int i=19; i >= 12 ; i--)
        {
            rez[i] = (offset>>i-2) & 1;
        }
        rez[20] = (offset>>9) & 1;
        for(int i=30; i>21; i--)
        {
            rez[i] = (offset>>(i-20-2)) & 1;
        }
        rez[21] = 0;
    }
    private void  setImmediateBranch(int val)
    {
        if(val>=0)
        {
            rez[31] = 0;
        }
        else
        {
            rez[31] = 1;
            val=val * (-1);
        }
        rez[7] = (val>>9) & 1;
        rez[8] =0;
        rez[9] = val & 1;
        rez[10] = (val>>1) & 1;
        rez[11] = (val>>2) & 1;
        
        for(int i=30;i>=25;i--)
        {
            rez[i] = (val>>(i-20-2));
        }
    }
    public int[] returnRez() {
        return rez;
    }
} 



 class Memory {
    private byte[] memory;

    
    Memory(int MEMORY_SIZE_IN_BYTES){
        memory = new byte[MEMORY_SIZE_IN_BYTES];
    }

    int getMemorySize()
    {
        return memory.length;
    }
    // Stores a single byte in the memory array
    void storeByte (int addr, int data) {
        memory[addr] = (byte) (data & 0xFF);
    }

    // Stores a half word in the memory array
    void storeHalfWord(int addr, short data) {
        memory[addr]    = (byte) ((data & 0x00FF));
        memory[addr+1]  = (byte) ((data & 0xFF00) >>> 8);
    }

    // Stores a word in the memory array
    void storeWord(int addr, int data) {
        memory[addr]    = (byte) ((data & 0x000000FF));
        memory[addr+1]  = (byte) ((data & 0x0000FF00) >>> 8);
        memory[addr+2]  = (byte) ((data & 0x00FF0000) >>> 16);
        memory[addr+3]  = (byte) ((data & 0xFF000000) >>> 24);
    }


    void storeInstruction(int addr, int [] data) 
    {   
        int rez = 0; 
        
        for(int i=0; i<32; i++) 
        { 
            rez = rez + (int)(Math.pow(2,i) * data[i]);
        } 
        
        storeWord(addr, rez); 
    }

    // Returns the byte in the memory given by the address.
    byte getByte (int addr) {
        return memory[addr];
    }

    // Returns half word from memory given by address
    int getHalfWord(int addr){
        return (getByte(addr+1) << 8) | (getByte(addr) & 0xFF);
    }

    // Returns word from memory given by address
    int getWord(int addr){
        return (getHalfWord(addr+2) << 16) | (getHalfWord(addr) & 0xFFFF);
    }

    // Returns string starting at the address given and ends when next memory address is zero.
    String getString(int addr){
        String returnValue = "";
        int i = 0;
        while(memory[addr+i] != 0){
	    returnValue += (char) (memory[addr+i]);
	    i++;
        }
        return returnValue;
    }

    byte[] getMemory() {
        return memory;
    }
	
    void setMemory(byte[] mem){
        this.memory = mem;
    }
}


class ExecutionFinish extends Exception
    {
    public ExecutionFinish(String mesage)
    {
        super(mesage);
    }
       }

 class Cpu { 

    private int pc = 0; 
    private int[] reg = new int[8];
    private Memory InstructionMemory;
    private Memory DataMemory; 

    public Cpu(Memory InstructionMemory, Memory DataMemory)
    {
        this.InstructionMemory = InstructionMemory; 
        this.DataMemory = DataMemory; 
        reg[2] = DataMemory.getMemorySize() - 1;
    } 

    public void executeInstruction()throws ExecutionFinish
    {
        int opcode = 0b1111111; 
        int operatieCurenta = InstructionMemory.getWord(pc); 
        opcode = opcode & operatieCurenta; 

        switch (opcode) {
            case 0b0000000: // Halt instruction
                System.out.println("Halt instruction encountered. Stopping execution.");
                throw new ExecutionFinish("S-a terminat executia programului"); // Terminate program

            case 0b0110011: // R-type instruction (e.g., ADD, SUB)
                executeRType(operatieCurenta);
                break;

            case 0b0010011: // I-type instruction (e.g., ADDI, ORI)
                executeIType(operatieCurenta);
                break;

            case 0b0000011: // Load instruction
                executeLoad(operatieCurenta);
                break;
            //S Type
            case 0b0100011: // Store instruction
                executeStore(operatieCurenta);
                break;
            case 0b1101111: //JAL
                executeJAL(operatieCurenta);
                break;
            case 0b1100111: // JALR
                executeJALR(operatieCurenta);
                break;
            //B type
            case 0b1100011: // BEQ / BNE / BLT / BGE / BLTU / BGEU
                executeBType(operatieCurenta);
                break; 
            default: // Unknown opcode
                System.err.println("Unknown opcode: " + Integer.toBinaryString(opcode));
                break;
        }
    }
    private void executeRType(int operatieCurenta)
    {
        int funct3 = (operatieCurenta>>12) & (0b111);
        int funct7 = (operatieCurenta>>25) & (0b1111111);
        int rs1 = (operatieCurenta>>15) & (0b11111);
        int rs2 = (operatieCurenta>>20) & (0b11111);
        int rd = (operatieCurenta>>7) & (0b11111);
        switch(funct3){
            case 0b000: // ADD // SUB
               switch(funct7){
                    case 0b0000000: // ADD
                        reg[rd] = reg[rs1] + reg[rs2];
                        break;
                    case 0b0100000: // SUB
                        reg[rd] = reg[rs1] - reg[rs2];
                        break;
                }
                break;
        }
        
        pc = pc+4;

    }
    private void executeIType(int operatieCurenta)
    {

    }
    private void executeBType(int operatieCurenta)
    {
        int rs1 = (operatieCurenta>>15) & (0b11111);
        int rs2 = (operatieCurenta>>20) & (0b11111);
        int funct3 = (operatieCurenta>>12) & (0b111);
        int immediate = (((operatieCurenta>>31) & (0b1))<<12) | (((operatieCurenta>>7) & (0b1))<<11) | (((operatieCurenta>>25) & (0b111111))<<5) | (((operatieCurenta>>8) & (0b1111))<<1);
        switch(funct3){
            case 0b000: // BEQ
                pc += (reg[rs1] == reg[rs2]) ? immediate : 1;
                break;
            case 0b001: // BNE
                pc += (reg[rs1] != reg[rs2]) ? immediate : 1;
                break;
            case 0b100: // BLT
                pc += (reg[rs1] < reg[rs2]) ? immediate : 1;
                break;
            case 0b101: // BGE
                pc += (reg[rs1] >= reg[rs2]) ? immediate : 1;
                break;
            case 0b110: //BLTU
                pc += (Integer.toUnsignedLong(reg[rs1]) < Integer.toUnsignedLong(reg[rs2])) ? immediate : 1;
                break;
            case 0b111: //BLGEU
                pc += (Integer.toUnsignedLong(reg[rs1]) >= Integer.toUnsignedLong(reg[rs2])) ? immediate : 1;
                break;
        }
    }
    private void executeLoad(int operatieCurenta)
    {

    }
    private void executeStore(int operatieCurenta)
    {

    }
    private void executeJAL(int operatieCurenta)
    {

    }
    private void executeJALR(int operatieCurenta)
    {

    }
}

public class Main {
    public static void main(String[] args) {
        Memory InstructionMemory = Reader.createInstructionMemory();
        Memory DataMemory = new Memory(2048);
        Cpu core = new Cpu(InstructionMemory,DataMemory);
        try{
            while(true)
            core.executeInstruction();
        }
        catch(ExecutionFinish e)
        {
            System.out.println("Executia programului s-a terminat");
        }
    }
}