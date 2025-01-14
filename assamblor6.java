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
 * PSH, POP; 
 */


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
 
 class LinieCurata {
     private String eticheta;
     private String instructiune;
     private String input;
     private String[] tokens; // Vectorul de stringuri in care vom salva fiecare parte a instructiunii.
 
     public int isInstruction(String aux) {
         // Verificam daca instructiunea este valida
         if (aux.equals("ADD")) {    // OK
             return 1;
         } else if (aux.equals("ADDI")) {    //OK
             return 1;
         }  
         else if (aux.equals("AND")) {    //OK
             return 1;
         } 
         
         else if (aux.equals("OR")) {    //OK
             return 1;
         }
         else if (aux.equals("SUB")) {     //OK
             return 1;
         } else if (aux.equals("SUBI")) {    //mai e de LUCRAT
             return 1;
         } else if (aux.equals("MUL")) {     //OK
             return 1;
         } else if (aux.equals("LW")) {      //OK
             return 1;
         } else if (aux.equals("DIV")) {     //OK
             return 1;
         } else if (aux.equals("SW")) {      //OK 
             return 1;
         } else if (aux.equals("MOV")) {     // ne trebuie neaparat ? in manual de risc scrie ca in spate sunt implementate cu ADDI
             return 1;
         } else if (aux.equals("JAL")) {
             return 1;
         } else if (aux.equals("BEQ")) {
             return 1;
         } else if (aux.equals("HLT")) { // OK
             return 1;
         } else {
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
 
     Instructiune(LinieCurata linieCurata) throws UnknownInstruction {
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
                 String immLow = imm.substring(6, 13);
                 System.out.println(immLow); 
                 
                 for(int i=7; i<=11; i++) 
                 {
                     rez[i] = immHigh.charAt(i - 7) - '0';
                 } 
                 
                  for(int i=25; i<=31; i++) 
                 {
                     rez[i] = immLow.charAt(i - 25) - '0';
                 } 
                 
                 
                 
                 setRegBase(tokens[4]);                 
                 setRs2(tokens[2]);                     
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
                 rez[startIndex - i] = (regNum >> i) & 1;
             }
         } catch (Exception e) {
             System.out.println("invalid format for arguments");
         }
     }
 
     private String StringToBin12BitsNegative(String nr)
     {
         int number = Integer.parseInt(nr);
         number = number * -1; 
         String bin = Integer.toBinaryString(number);
         System.out.println(bin);
         return String.format("%12s", bin).replace(' ', '0');
     }
     private String StringToBin12Bits(String nr) {
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
 
             for (LinieCurata linie : Program) {
                 System.out.println(linie);
                 try {
                     if (linie.returnLiniaCurata()[0].equals("invalid")) {
                         throw new UnknownInstruction("Instructiune invalida: " + linie.returnLiniaCurata()[1]);
                     }
                 } catch (UnknownInstruction e) {
                     System.out.println("Instructiune necunoscuta");
                 }
 
                 Instructiune instructiune = new Instructiune(linie);
                 int[] rez = instructiune.returnRez();
 
                 for (int i = 0; i <= 31; i++) {
                     System.out.print(rez[i]);
                 }
                 System.out.println("");
             }
         } catch (FileNotFoundException e) {
             System.out.println("Eroare la citirea fisierului.");
             e.printStackTrace();
         } catch (UnknownInstruction e) {
             System.out.println("Instructiune necunoscuta");
             System.exit(1);
         }
     }
 }
