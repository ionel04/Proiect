// ADRESELE IN RISC V SUNT PE 5 BITI. 
// adresa R0 este = 00000
// adresa R1 este = 00001 
// adresa R2 este = 00010
// adresa R3 este = 00011 
// adresa R4 este = 00100
// adresa R5 este = 00101
// adresa R6 este = 00110 
// adresa R7 este = 00111  

/*
 * DE IMPLEMENTAT:
 * LW, SW; 
 * JAL, JMS;
 * BNE, BEQ, BNZ; 
 * HTL; 
 */


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
class UnknownInstruction extends Exception
{
    public UnknownInstruction(String msg)
    {
        super(msg);
    }
}
class LinieCurata {
    private String eticheta;
    private String instructiune;
    private String input;
    private String[] tokens; // Vectorul de stringuri în care vom salva fiecare parte a instrucțiunii.

    public int isInstruction(String aux)throws UnknownInstruction {
        // Verificăm dacă instrucțiunea este validă
        if(aux.equals("ADD")) 
        {   
            return 1; 
        } 

        else if(aux.equals("ADDI")) 
        {   
            aux = aux + " I";
            return 1; 
        } 

        else if (aux.equals("SUB")) 
        {    
            aux = aux + " R";
            return 1; 
        }

        else if (aux.equals("SUBI")) 
        {   
            aux = aux + " I";
            return 1; 
        }

        else if (aux.equals("MUL")) 
        {
            aux = aux + " R";
            return 1; 
        } 

        else if (aux.equals("MULI")) 
        {
            aux = aux + " I";
            return 1; 
        } 

        else if (aux.equals("DIV")) 
        {
            aux = aux + " R";
            return 1; 
        } 

        else if (aux.equals("DIVI")) 
        {
            return 1; 
        }
        
        else if (aux.equals("MOV")) 
        {
            return 1; 
        } 

        else if (aux.equals("STR")) 
        {
            return 1; 
        } 

        else if (aux.equals("JAL")) 
        {
            return 1; 
        } 

        else if (aux.equals("BEQ")) 
        {
            return 1; 
        }

        else 
        {
            throw new UnknownInstruction("unknown instr");
        }
    }

    String removeConsecutiveSpaces(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("\\s+", " ").trim();
    }

    LinieCurata(String input) throws UnknownInstruction{
        this.input = input;
        this.input = removeConsecutiveSpaces(this.input); // Eliminăm spațiile consecutive
        this.input = this.input.toUpperCase(); // Transformăm în litere mari
        String delims = "[ ,]"; // Separator: spațiu sau virgulă
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
    public String toString()
    {
        String rez="";
        for(String i : tokens)
        {
                rez = rez + i +" ";
        }
        return rez;
    }
}

class Instructiune {
    private int[] rez;

    Instructiune(LinieCurata linieCurata) {
        rez = new int[32];
        String[] tokens = linieCurata.returnLiniaCurata();

        switch (tokens[1]) {
            case "ADD":
                setOpcode("0110011");
                setFunct3("000");
                setFunct7("0000000");
                setRegisters(tokens[2], tokens[3], tokens[4]);
                break;
            case "ADDI":
                setOpcode("0010011");
                setFunct3("000");
                setRegistersImmediate(tokens[2], tokens[3], tokens[4]);
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
            case "LW":
                setOpcode("0110011");
                setFunct3("010");


            default:
                System.out.println("Instructiune necunoscuta: " + tokens[1]);
                break;
        }
    }

    private void setOpcode(String opcode) {
        for (int i = 0; i < 7; i++) {
            rez[6 - i] = opcode.charAt(i) - '0';
        }
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

    private void setRegistersImmediate(String rd, String rs1, String imm) {
        setRegisterBits(11, rd);
        setRegisterBits(19, rs1);
        setImmediate(StringToBin(imm));
    }

    private void setRegisterBits(int startIndex, String reg) {
        try{
         int regNum = Integer.parseInt(reg.substring(1));
         if(regNum>8)
            throw new Exception();
         for (int i = 0; i < 5; i++) {
            rez[startIndex - i] = (regNum >> i) & 1;
        }
        }
        catch(Exception e)
        {
            System.out.println("invalid format for arguments");
        }
        
    }

    private String StringToBin(String nr) {
        int number = Integer.parseInt(nr);
        String bin = Integer.toBinaryString(number);
        return String.format("%12s", bin).replace(' ', '0');
    }

    private void setImmediate(String bin) {
        for (int i = 0; i < bin.length(); i++) {
            rez[31 - i] = bin.charAt(bin.length() - 1 - i) - '0';
        }
    }

    public int[] returnRez() {
        return rez;
    }
}

class Main4 {
    public static void main(String[] args) {
        try {
            File myObj = new File("in.txt");
            Scanner myReader = new Scanner(myObj);
            List<LinieCurata> Program = new ArrayList<LinieCurata>();
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();

                LinieCurata instructiune = new LinieCurata(data);
                Program.add(instructiune);

            }
            myReader.close();


            for(LinieCurata linie : Program)
            {
                Instructiune instructiune = new Instructiune(linie);
                int[] rez = instructiune.returnRez();

                for (int i = 0; i <= 31; i++) {
                    System.out.print(rez[i]);
                }
                System.out.println("");
                //System.out.println(linie);

            }
        } catch (FileNotFoundException e) {
            System.out.println("Eroare la citirea fișierului.");
            e.printStackTrace();
        }
        catch(UnknownInstruction e)
        {
            System.out.println("Instructiune necunoscuta");
        }
    }
} 
/*
 * int[] rez = instructiune.returnRez();

                for (int i = 0; i <= 31; i++) {
                    System.out.print(rez[i]);
                }
 */
