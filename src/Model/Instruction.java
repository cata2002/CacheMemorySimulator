package Model;

import java.util.HexFormat;
import java.util.Random;

public class Instruction {
    private String instruction;
    private Cache cacheType;

    private MainMemory mainMemory;

    public Instruction() {
    }

    public Instruction(String instruction, Cache cacheType) {
        this.instruction = instruction;
        this.cacheType = cacheType;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public Cache getCacheType() {
        return cacheType;
    }

    public void setCacheType(Cache cacheType) {
        this.cacheType = cacheType;
    }


    public static String generateBinaryInstruction(String instruction, int instructionLength) {
        StringBuilder binaryString = new StringBuilder();

        for (int i = 0; i < instruction.length(); i++) {
            char hexChar = instruction.charAt(i);
            int decimalValue = Character.digit(hexChar, 16);
            String binaryValue = Integer.toBinaryString(decimalValue);

            while (binaryValue.length() < 4) {
                binaryValue = "0" + binaryValue;
            }

            binaryString.append(binaryValue);
        }

        // Check if the binary string is shorter than the instruction length
        if (binaryString.length() < instructionLength) {
            // Calculate the number of zeros to prepend
            int zerosToPrepend = instructionLength - binaryString.length();

            // Prepend the binary string with zeros
            for (int i = 0; i < zerosToPrepend; i++) {
                binaryString.insert(0, "0");
            }
        } else {
            // Trim the binary string to the instruction length from the right
            int trimLength = binaryString.length() - instructionLength;
            binaryString = new StringBuilder(binaryString.substring(trimLength));
        }

        return binaryString.toString();
    }

    public int getInstructionLength(MainMemory m){
        return (int) (Math.log(m.getSize())/ Math.log(2.0));
    }

    //generate a hex instruction of length 4
    public static String generateHexInstruction(){
        String hexString = "";
        for (int i = 0; i < 4; i++) {
            int random = (int) (Math.random() * 16);
            hexString += Integer.toHexString(random);
        }
        return hexString;
    }

    public static String[][] directMappedTableContent(int size, int offsetBits){
        int colNr=(int)Math.pow(2,offsetBits);
        int rowNr=size/(int)Math.pow(2,offsetBits);
        String [][]content=new String[rowNr][colNr];
        for(int i=0;i<rowNr;i++){
            for(int j=0;j<colNr;j++){
                content[i][j]="B"+i+" W"+j;
            }
        }
        return content;
    }

    public static String generateHexSmallerThan(long maxDecimalValue) {
        // Generate a random decimal value smaller than the maximum
        Random random = new Random();
        long generatedDecimal = random.nextLong() % maxDecimalValue;

        // Ensure the generated decimal value is positive
        generatedDecimal = Math.abs(generatedDecimal);

        // Convert the decimal value to hexadecimal
        String generatedHex = Long.toHexString(generatedDecimal).toUpperCase();

        return generatedHex;
    }

    public static void main(String args[]){

        Cache cache=new Cache(2048,2,WritePolicy.WRITE_BACK);
        MainMemory m=new MainMemory(2048, 2);
        Instruction a=new Instruction("123",cache);
        System.out.println(m.getSize());
        System.out.println(a.getInstructionLength(m));
        System.out.println(generateBinaryInstruction("abc",11));
        //System.out.println(Math.pow(2,2));
    }
}
