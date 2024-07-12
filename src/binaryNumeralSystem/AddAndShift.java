package binaryNumeralSystem;

import java.io.*;

public class AddAndShift {
    public static void addAndShiftUnsigned (BinaryLiteral firstNum, BinaryLiteral secondNum) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("out.txt",true))) {
            BinaryLiteral m = BinaryLiteral.decimalToBinaryLiteral(0, firstNum.getLength(), false);
            BinaryLiteral a = firstNum;
            BinaryLiteral b = secondNum;

            writer.write("unsigned add & shift multiplication\n");
            writer.write(String.format("A=%d=%s, B=%d=%s\n",a.toDecimal(), a.toString(), b.toDecimal(), b.toString()));
            writer.write("~~~~~~~~~~~~~~~~~~~~~\n");
            writer.write(String.format("(0, ) M=%s|%s\n", m.toString(), b.toString()));
            writer.write("~~~~~~~~~~~~~~~~~~~~~\n");

            int stepCounter = secondNum.getLength();
            for (int i = 1; i <= stepCounter; i++) {
                if (b.getLSB() == '1') {
                    m = BinaryLiteral.add(m, a.shiftLeft(i - 1));
                    writer.write(String.format("(%d,+) M=%s|%s", i, m.toString(), b.toString()));
                    if (m.getCarry())
                        writer.write(" (C)");   //Carry Sign
                    writer.write('\n');
                } else
                    writer.write(String.format("(%d, ) M=%s|%s\n", i, m.toString(), b.toString()));

                BinaryLiteral newM = BinaryLiteral.decimalToBinaryLiteral(m.toDecimal(), m.getLength() + 1, false);
                if (m.getCarry())
                    newM.setMSB('1');
                m = newM;
                b = b.shiftRightLogical();
                writer.write(String.format("(%d,>) M=%s|%s\n", i, m.toString(), b.toString()));
                writer.write("~~~~~~~~~~~~~~~~~~~~~\n");
            }
            writer.write(String.format("M=A×B=%d\n",m.toDecimal()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void addAndShiftSigned (BinaryLiteral firstNum, BinaryLiteral secondNum) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("out.txt",true))) {
            BinaryLiteral m = BinaryLiteral.decimalToBinaryLiteral(0, firstNum.getLength(), true);
            BinaryLiteral a = firstNum;
            BinaryLiteral b = secondNum;

            writer.write("signed add & shift multiplication\n");
            writer.write(String.format("A=%d=%s, B=%d=%s\n",a.toDecimal(), a.toString(), b.toDecimal(), b.toString()));
            writer.write("~~~~~~~~~~~~~~~~~~~~~\n");
            writer.write(String.format("(0, ) M=%s|%s\n", m.toString(), b.toString()));
            writer.write("~~~~~~~~~~~~~~~~~~~~~\n");

            int stepCounter = secondNum.getLength();
            boolean sameSign = false;   //indicates that both a & m are negative when adding them together
            for (int i = 1; i <= stepCounter; i++) {
                if (b.getLSB() == '1') {
                    sameSign = a.getMSB() == '1' && m.getMSB() == '1';
                    if (i == stepCounter) {
                        sameSign = a.twosComplement().getMSB() == '1' && m.getMSB() == '1';
                        m = BinaryLiteral.add(m, a.shiftLeft(i - 1).twosComplement());
                        writer.write(String.format("(%d,-) M=%s|%s", i, m.toString(), b.toString()));
                    } else {
                        m = BinaryLiteral.add(m, a.shiftLeft(i - 1));
                        writer.write(String.format("(%d,+) M=%s|%s", i, m.toString(), b.toString()));
                    }
                } else
                    writer.write(String.format("(%d, ) M=%s|%s", i, m.toString(), b.toString()));

                BinaryLiteral newM = BinaryLiteral.decimalToBinaryLiteral(m.toDecimal(), m.getLength() + 1, true);
                if (m.getCarry()) {
                    if (sameSign && m.getMSB() == '0') {
                        newM.setMSB('1');
                        writer.write(" (C)");   //Carry Sign
                     } else
                        writer.write(" (×)");   //throwing away carry bit
                }
                m = newM;
                b = b.shiftRightLogical();
                writer.write('\n');
                writer.write(String.format("(%d,>) M=%s|%s\n", i, m.toString(), b.toString()));
                writer.write("~~~~~~~~~~~~~~~~~~~~~\n");
            }
            writer.write(String.format("M=A×B=%d\n",m.toDecimal()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
