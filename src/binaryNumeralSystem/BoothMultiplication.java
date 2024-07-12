package binaryNumeralSystem;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class BoothMultiplication {
    public static void boothMultiplication (BinaryLiteral firstNum, BinaryLiteral secondNum) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("out.txt", true))) {
            BinaryLiteral m = BinaryLiteral.decimalToBinaryLiteral(0, firstNum.getLength() + 1, true);
            BinaryLiteral a = BinaryLiteral.decimalToBinaryLiteral(firstNum.toDecimal(), firstNum.getLength() + 1, true);;
            BinaryLiteral b = secondNum.shiftLeft(1);
//            BinaryLiteral b = BinaryLiteral.decimalToBinaryLiteral(secondNum.toDecimal(), secondNum.getLength() + 1, true).shiftLeft(1);

            writer.write("booth multiplication\n");
            writer.write(String.format("A=%d=%s, B=%d=%s\n", firstNum.toDecimal(), firstNum.toString(), secondNum.toDecimal(), secondNum.toString()));
            writer.write("~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            writer.write(String.format("(0,   ) M=0║%s|%s║0\n", "0".repeat(firstNum.getLength()), secondNum.toString()));
            writer.write("~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");

            int stepCounter = secondNum.getLength() / 2;
            BinaryLiteral operand = a;
            boolean sameSign = false;   //indicates that both a & m are negative when adding them together
            for (int i = 1; i <= stepCounter; i++) {
                char mOperandMSB = m.getMSB();
                switch (getBoothOperation(b)) {
                    case 0 -> writer.write(String.format("(%d,   ) M=%s║%s|%s║%s",
                            i, m.getMSB(), m.toString().substring(1,m.getLength()), b.toString().substring(0,b.getLength() - 1), b.getLSB()));
                    case 1,2 -> {
                        operand = a.shiftLeft((i - 1) * 2);
                        m = BinaryLiteral.add(m, operand);
                        writer.write(String.format("(%d, +A) M=%s║%s|%s║%s",
                            i, m.getMSB(), m.toString().substring(1,m.getLength()), b.toString().substring(0,b.getLength() - 1), b.getLSB()));
                    }
                    case 3 -> {
                        operand = firstNum.shiftLeft(((i - 1) * 2) + 1);
                        m = BinaryLiteral.add(m, operand);
                        writer.write(String.format("(%d,+2A) M=%s║%s|%s║%s",
                            i, m.getMSB(), m.toString().substring(1,m.getLength()), b.toString().substring(0,b.getLength() - 1), b.getLSB()));
                    }
                    case 4 -> {
                        operand = firstNum.twosComplement().shiftLeft(((i - 1) * 2) + 1);
                        m = BinaryLiteral.add(m, operand);
                        writer.write(String.format("(%d,-2A) M=%s║%s|%s║%s",
                            i, m.getMSB(), m.toString().substring(1,m.getLength()), b.toString().substring(0,b.getLength() - 1), b.getLSB()));
                    }
                    case 5,6 -> {
                        operand = a.twosComplement().shiftLeft((i - 1) * 2);
                        m = BinaryLiteral.add(m, operand);
                        writer.write(String.format("(%d, -A) M=%s║%s|%s║%s",
                            i, m.getMSB(), m.toString().substring(1,m.getLength()), b.toString().substring(0,b.getLength() - 1), b.getLSB()));
                    }
                }
                sameSign = operand.getMSB() == '1' && mOperandMSB == '1';

                BinaryLiteral newM = BinaryLiteral.decimalToBinaryLiteral(m.toDecimal(), m.getLength() + 2, true);
                if (m.getCarry()) {
                    if (sameSign && m.getMSB() == '0') {
                        newM.setMSB('1');
                        writer.write(" (C)");   //Carry Sign
                    } else
                        writer.write(" (×)");   //throwing away carry bit
                }
                m = newM;
                b = b.shiftRightLogical().shiftRightLogical();
                writer.write('\n');
                writer.write(String.format("(%d,>>>) M=%s║%s|%s║%s\n",
                        i, m.getMSB(), m.toString().substring(1,m.getLength()), b.toString().substring(0,b.getLength() - 1), b.getLSB()));
                writer.write("~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            }
            writer.write(String.format("M=A×B=%d\n", firstNum.toDecimal() * secondNum.toDecimal()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
    }
    
    private static int getBoothOperation (BinaryLiteral num) {
        int op = 0; // op 0 = op 7 = NOP
        String LSB3 = num.toString().substring(num.getLength() - 3);
        switch (LSB3) {
            case "001" -> op = 1; // +A
            case "010" -> op = 2; // +A
            case "011" -> op = 3; // +2A
            case "100" -> op = 4; // -2A
            case "101" -> op = 5; // -A
            case "110" -> op = 6; // -A
        }
        return op;
    }
}
