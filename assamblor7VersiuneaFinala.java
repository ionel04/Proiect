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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
                String aux = StringToBin12Bits(tokens[3]); // valoarea de offset, formula pentru adresa = imm + valoare reg baza
                setImmediate(aux);
                setRegBase(tokens[4]);
                break;

            case "SW": // store din registru in memorie
                setOpcode("0100011");              // Correct: opcode for store instructions (SW)
                setFunct3("010");                  // Correct: funct3 for SW is 010
                String imm = StringToBin12Bits(tokens[3]);  // Convert immediate (offset) to 12-bit binary string
                System.out.println(imm);
                String immHigh = imm.substring(0, 5);   
                System.out.println(immHigh);
                String immLow = imm.substring(5, 12);
                System.out.println(immLow); 
                
                for(int i=7; i<=11; i++) 
                {
                    rez[i] = immHigh.charAt(i - 7) - '0';
                } 
                
                 for(int i=25; i<32; i++) 
                {
                    rez[i] = immLow.charAt(i - 25) - '0';
                } 
                
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
                setOpcode("1110111");
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
        for (int i = 0; i < bin.length(); i++) {
            rez[31 - i] = bin.charAt(bin.length() - 1 - i) - '0';
        }
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

class Main5 {
    public static void main(String[] args) {
        try {
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

                for (int i = 31; i >= 0; i--) {
                    System.out.print(rez[i]);
                }
                System.out.println("");
                curent++;
            }
        } catch (FileNotFoundException e) {
            System.out.println("Eroare la citirea fisierului.");
            e.printStackTrace();
        } catch (UnknownInstruction e) {
            System.out.println("Instructiune necunoscuta");
            System.exit(1);
        }
        catch (LabelNotExisting e) {
            System.out.println("Nu exista labe-ul");
            System.exit(1);
        }
    }
}
