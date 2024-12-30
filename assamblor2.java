import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

class LinieCurata {
    private String eticheta;
    private String instructiune;
    private String input;
    private String[] tokens; // Vectorul de stringuri în care vom salva fiecare parte a instrucțiunii.

    public int isInstruction(String aux) {
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
            return 0; 
        }
    }

    String removeConsecutiveSpaces(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("\\s+", " ").trim();
    }

    LinieCurata(String input) {
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
}

class Instructiune {
    private String input;
    private int[] rez; // Vector pentru codul binar

    Instructiune(String s) {
        input = s;
        rez = new int[32]; // Inițializăm vectorul de 32 de biți
        LinieCurata inputCurat = new LinieCurata(input);
        String[] linieInput = inputCurat.returnLiniaCurata();

        if (linieInput[1].equals("ADD")) {
            setOpcode("0110011"); // Opcode pentru ADD (R-Type)
            setFunct3("000");
            setFunct7("0000000");
            setRegisters(linieInput[2], linieInput[3], linieInput[4]);
        }
    }

    private void setOpcode(String opcode) {
        for (int i = 0; i < 7; i++) {
            rez[i] = opcode.charAt(i) - '0';
        }
    }

    private void setFunct3(String funct3) {
        for (int i = 0; i < 3; i++) {
            rez[12 + i] = funct3.charAt(i) - '0';
        }
    }

    private void setFunct7(String funct7) {
        for (int i = 0; i < 7; i++) {
            rez[25 + i] = funct7.charAt(i) - '0';
        }
    }

    private void setRegisters(String rd, String rs1, String rs2) {
        setRegisterBits(7, rd);
        setRegisterBits(15, rs1);
        setRegisterBits(20, rs2);
    }

    private void setRegisterBits(int startIndex, String reg) {
        int regNum = Integer.parseInt(reg.substring(1)); // Eliminăm "R" din "R0", "R1", etc.
        for (int i = 0; i < 5; i++) {
            rez[startIndex + i] = (regNum >> i) & 1;
        }
    }

    void littleEndian() // ATATA S-A PUTUT
    {
        int aux [] = new int[32]; 
        int j=0; 
        for(int i=31; i>0; i--) 
        {
            aux[j] = rez[i]; 
            j++; 
        } 

        rez = aux; 

    }

    public int[] returnRez() { 
        
        littleEndian(); 
        return rez;
    }
}

class Main {
    public static void main(String[] args) {
        try {
            File myObj = new File("in.txt");
            Scanner myReader = new Scanner(myObj);

            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                Instructiune instructiune = new Instructiune(data);
                int[] rez = instructiune.returnRez();

                // Afișăm codul binar pe o linie
                for (int bit : rez) {
                    System.out.print(bit);
                }
                System.out.println();
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading the file.");
            e.printStackTrace();
        }
    }
}
