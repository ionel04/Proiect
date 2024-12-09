import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

class LinieCurata 
{
    private String eticheta; 
    private String instructione;  
    private String input; 
    private String [] tokens; // vectoul de stringuri care in care vom salva fiecare parte a instrucntiuni. 
    
     public int isInstrunction(String aux) // MAI TREBUIE LUCRAT LA ACESTA FUNCTIE
     {   
        if(aux.equals("ADD"))
        {
            return 1; 
        } 
        
        else 
        {
            return 0; 
        }
    }

    String removeConsecutiveSpaces(String input) 
    {
        if (input == null) 
        {
            return null;
        }

        return input.replaceAll("\\s+", " ").trim();
    }
    
    LinieCurata(String input) 
    {   
        this.input = input; 
        this.input = removeConsecutiveSpaces(this.input); // stergem spatiile consocuteive pentru a nu avea intrari initulile in vectorul de stringuri 
        this.input = this.input.toUpperCase(); // pentru a traduce mai simplu instructiunile in cod masina vom tramsforma inputul in literea mari. 
        String delims = "[ ,]"; // aici stergem si virgula nu stiu cat de corect este, sau daca o sa ne afecteze pe parcurs 
        this.tokens = this.input.split(delims); 
        
        if(isInstrunction(tokens[0]) == 1) 
        {
            this.input = "- " + this.input; 
            this.tokens = this.input.split(delims);
        } 

        this.instructione = tokens[1]; 
        this.eticheta = tokens[0];

        if(isInstrunction(this.instructione) == 0) // MAI E DE LUCRAT daca in vector pe pozitia 1 nu gasim o intreuction afisam la ecran un mesaj de eroare
        {   

            tokens[0] = "invalid "; 
        }

    } 
    
    public String [] retunrnezaLiniaCurata() // NU STIU DACA ESTE CEA MAI BUNA INPLEMENTARE, dar altefel nu putem face, decat cu functia asta care iti returneaza vectorul de stringuri.  
    {
        return this.tokens; 
    }
}

class Main 
{ 
    public static void main(String [] args) 
    {
        String s = "ADD R0 R2";
        String d = "loop ADD R0, R3"; 
        
        LinieCurata rez = new LinieCurata(s); 
        LinieCurata rez2 = new LinieCurata(d); 
        
        String [] aux; 
        
        aux = rez.retunrnezaLiniaCurata(); 
        
        //System.out.println(rez.primulToken()); 
        //System.out.println(rez2.primulToken());  
        
        // for(int i=0; i < aux.length; i++) 
        // {
        //     System.out.print(aux[i]);
        // } 

        try 
        {
            File myObj = new File("1.txt"); 
            Scanner myReader = new Scanner(myObj);

            while (myReader.hasNextLine()) 
            {
                String data = myReader.nextLine();
                LinieCurata stringCuratat = new LinieCurata(data);
                String tokens [] = stringCuratat.retunrnezaLiniaCurata(); 

                for(int i=0; i < tokens.length; i++) 
                {
                    System.out.print(tokens[i]); 
                    System.out.print(" "); 
                } 

                System.out.println(" ");
            } 
        } 

        catch (FileNotFoundException e) 
        {
            System.out.println("An error occurred while reading the file.");
            e.printStackTrace();
        }
    } 
    
    
}