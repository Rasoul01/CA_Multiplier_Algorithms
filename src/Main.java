import binaryNumeralSystem.BinaryLiteral;

import java.io.*;
import static binaryNumeralSystem.AddAndShift.*;
import static binaryNumeralSystem.BinaryLiteral.decimalToBinaryLiteral;
import static binaryNumeralSystem.BoothMultiplication.boothMultiplication;

public class Main {

    public static void main(String[] args) {
        int i = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("in.txt"));
             BufferedWriter writer = new BufferedWriter(new FileWriter("out.txt", true))) {
            String input = reader.readLine();
            int operationCount = Integer.parseInt(input);
            for (; i < operationCount; i++) {
                // opCode       0 : add&shift | 1 : booth
                // numLength max value : 32
                // isSigned     1 : signed | 0 : unsigned
                int opCode = Integer.parseInt(reader.readLine());
                int numLength = Integer.parseInt(reader.readLine());
                boolean isSigned = Integer.parseInt(reader.readLine()) == 1;
                int firstNum = Integer.parseInt(reader.readLine());
                int secondNum = Integer.parseInt(reader.readLine());


                writer.write("------------------------------------------\n");
                writer.write("out-" + i + "\n");
                writer.flush();
                BinaryLiteral a, b;
                switch (opCode) {
                    case 0 -> {
                        a = decimalToBinaryLiteral(firstNum, numLength, isSigned);
                        b = decimalToBinaryLiteral(secondNum, numLength, isSigned);
                        if (isSigned)
                            addAndShiftSigned(a, b);
                        else
                            addAndShiftUnsigned(a, b);
                    }
                    case 1 -> {
                        // Numbers are always signed in Booth algorithm
                        a = decimalToBinaryLiteral(firstNum, numLength, true);
                        b = decimalToBinaryLiteral(secondNum, numLength, true);
                        boothMultiplication(a, b);
                    }
//                    default ->
                }
                System.out.println("Operation " + i + " Done!");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NumberFormatException e ) {
            throw new RuntimeException("Invalid input in operation " + i);
        }
    }
}