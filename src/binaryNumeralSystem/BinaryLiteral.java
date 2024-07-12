package binaryNumeralSystem;

import java.util.Arrays;

/**
 * BinaryLiteral is a data structure for binary numbers
 * that stores bits in a fixed size int array; which
 * LSB (The Least Significant Bit) is at index 0 (binaryArray[0]).
 */
public class BinaryLiteral {
    private int length;
    private boolean isSigned;
    private boolean hasCarry = false;
    private char[] binaryArray;

    public BinaryLiteral(boolean isSigned) {
        this.isSigned = isSigned;
    }
    public BinaryLiteral() {
    }

    private void setBinaryArray(char[] binaryArray) {
        this.binaryArray = binaryArray;
    }
    private void setLength(int length) {
        this.length = length;
    }

    public void setSigned(boolean signed) {
        this.isSigned = signed;
    }

    public void setHasCarry(boolean hasCarry) {
        this.hasCarry = hasCarry;
    }

    public boolean getCarry() {
        return hasCarry;
    }

    public int getLength() {
        return length;
    }

    public static BinaryLiteral decimalToBinaryLiteral(int decimalNumber, int length, boolean isSigned) {
        if (length <= 0)
            throw new IllegalArgumentException("Binary number length should greater than zero.");
        if (decimalNumber < 0 && !isSigned)
            throw new IllegalArgumentException("Can't convert Negative number to unsigned binary.");

        if (isSigned) {
            if (decimalNumber < 0) {
                if ((-1 * (Math.pow(2, length - 1))) > decimalNumber)
                    throw new ArithmeticException("Desired length is not enough to convert decimal number.");
            } else {
                if (Math.pow(2, length - 1) <= decimalNumber)
                    throw new ArithmeticException("Desired length is not enough to convert decimal number.");
            }
        } else {
            if (Math.pow(2, length) <= decimalNumber)
                throw new ArithmeticException("Desired length is not enough to convert decimal number.");
        }

        BinaryLiteral binaryLiteral = new BinaryLiteral(isSigned);
        binaryLiteral.setLength(length);

        char[] tempArray = new char[length];
        String binaryString = Integer.toBinaryString(decimalNumber);
        // Integer.toBinaryString function returns a 32-char long String if the passed integer is negative.
        // There wouldn't be any issue if the number is negative as a result of max binary number length is 32.
        // So we only need to zero-fill if the number is non-negative to get the desired length.


        // Sign-extend the array to desired length
        if (decimalNumber >= 0)
            Arrays.fill(tempArray, '0');
        else
            Arrays.fill(tempArray, '1');
        // Extended sign bits would be cut to desired length
        for (int i = 0; i < Math.min(length, binaryString.length()); i++)
            tempArray[i] = binaryString.charAt(binaryString.length() - i - 1);

        binaryLiteral.setBinaryArray(tempArray);
        return binaryLiteral;
    }

    public int toDecimal() {
        if (!isSigned || this.getMSB() == '0')
            return Integer.parseInt(this.toString(), 2);
        else
            return Integer.parseInt(this.twosComplement().toString(), 2) * -1;
    }

    public char getMSB() {
        return binaryArray [this.length -1];
    }

    public void setMSB(char bit) {
        if (bit == '0' || bit == '1')
            this.binaryArray [this.length -1] = bit;
        else
            throw new IllegalArgumentException("bit should be either 1 or 0");
    }

    public char getLSB() {
        return binaryArray[0];
    }

    public BinaryLiteral twosComplement() {
        return add(this.onesComplement(), decimalToBinaryLiteral(1, this.length, true));
    }
    public BinaryLiteral onesComplement() {
        BinaryLiteral complement = new BinaryLiteral(true);
        complement.length = this.length;
        char[] complementArray = new char[this.length];
        for (int i = 0; i < this.length; i++) {
            complementArray[i] = (this.binaryArray[i] == '1') ? '0' : '1';
        }
        complement.setBinaryArray(complementArray);
        return complement;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (char n : binaryArray)
            str.append(n);
        return str.reverse().toString();
    }

    /**
     * @return A new Binary Literal with the length reduced by one.
     */
    public BinaryLiteral shiftRightLogical() {
        BinaryLiteral returnBin = new BinaryLiteral();
        returnBin.setLength(this.length - 1);
        returnBin.setSigned(this.isSigned);
        returnBin.setBinaryArray(Arrays.copyOfRange(this.binaryArray, 1, this.length));
        return returnBin;
    }

    /**
     * @return A new Binary Literal with the same length and the sign bit extended.
     */
    public BinaryLiteral shiftRightArithmetic() {
        BinaryLiteral returnBin = new BinaryLiteral();
        returnBin.setLength(this.length);
        returnBin.setSigned(this.isSigned);
        char[] newArray = new char[this.length];
        System.arraycopy(this.binaryArray, 1, newArray, 0, this.length - 1);
        newArray[this.length - 1] = newArray[this.length - 2];
        returnBin.setBinaryArray(newArray);
        return returnBin;
    }

    /**
     * @param shiftAmount the amount to be shifted
     * @return A new Binary Literal with the length added by one.
     */
    public BinaryLiteral shiftLeft(int shiftAmount) {
        if (shiftAmount < 0)
            throw new IllegalArgumentException("Shift amount can't be negative");
        if (shiftAmount == 0)
                return this;

        BinaryLiteral returnBin = new BinaryLiteral();
        int newLength = this.length + shiftAmount;
        returnBin.setLength(newLength);
        returnBin.setSigned(this.isSigned);
        char[] newArray = new char[newLength];
        Arrays.fill(newArray, '0');
        System.arraycopy(this.binaryArray, 0, newArray, shiftAmount, newLength - shiftAmount);
        returnBin.setBinaryArray(newArray);
        return returnBin;
    }

    public static BinaryLiteral add(BinaryLiteral a, BinaryLiteral b) {
        int maxLength = Math.max(a.length, b.length);
        if (a.getLength() > b.getLength()) {
            b = decimalToBinaryLiteral(b.toDecimal(), maxLength, b.isSigned);
        } else if (a.getLength() < b.getLength()) {
            a = decimalToBinaryLiteral(a.toDecimal(), maxLength, a.isSigned);
        }
        int carry = 0;
        char[] result = new char[maxLength];

        for (int i = 0; i < maxLength; i++){
            int bitA = a.binaryArray[i] - '0';
            int bitB = b.binaryArray[i] - '0';
            int sum = bitA + bitB + carry;
            result[i] = (char) ((sum % 2) + '0');
            carry = sum / 2;
        }

        BinaryLiteral sumBin = new BinaryLiteral();
        sumBin.setLength(maxLength);
        sumBin.setSigned(a.isSigned || b.isSigned);
        sumBin.setBinaryArray(result);
        if (carry > 0) {
            sumBin.setHasCarry(true);
        }

        return sumBin;
    }
}
