package com.company;

public class Main {
    public static void main(String[] args) {
        int[] numbers = { 8, 3, 2, 5, 4 };

        printNumbers(sortNumbers(numbers));
    }

    public static int[] sortNumbers(int[] numbers) {
        if (numbers.length == 1) {
            return numbers;
        }

        int[] leftNumbers = sliceNumbers(numbers, 0, numbers.length / 2);
        int[] rightNumbers = sliceNumbers(numbers, numbers.length / 2, numbers.length);

        return joinSortedNumbers(sortNumbers(leftNumbers), sortNumbers(rightNumbers));
    }

    public static void printNumbers(int[] numbers) {
        System.out.println("{");
        for (int i = 0; i < numbers.length; i++) {
            System.out.println(numbers[i]);
        }
        System.out.println("}");
    }

    // Slice numbers in range [begin, end).
    public static int[] sliceNumbers(int[] numbers, int begin, int end) {
        int[] result = new int[end - begin];

        for (int i = begin; i < end; i++) {
            result[i - begin] = numbers[i];
        }

        return result;
    }

    public static int[] joinSortedNumbers(int[] leftNumbers, int[] rightNumbers) {
        return joinSortedNumbers(leftNumbers, rightNumbers, new int[leftNumbers.length + rightNumbers.length], 0);
    }

    public static int[] joinSortedNumbers(int[] leftNumbers, int[] rightNumbers, int[] joinedNumbers, int index) {
        if (leftNumbers.length == 0 && rightNumbers.length == 0) {
            return joinedNumbers;
        }

        if (leftNumbers.length == 0) {
            joinedNumbers[index] = rightNumbers[0];
            // Remove added right number to joined numbers.
            rightNumbers = sliceNumbers(rightNumbers, 1, rightNumbers.length);
        } else if (rightNumbers.length == 0) {
            joinedNumbers[index] = leftNumbers[0];
            // Remove added left number to joined numbers.
            leftNumbers = sliceNumbers(leftNumbers, 1, leftNumbers.length);
        } else if (leftNumbers[0] < rightNumbers[0]) {
            joinedNumbers[index] = leftNumbers[0];
            leftNumbers = sliceNumbers(leftNumbers, 1, leftNumbers.length);
        } else {
            joinedNumbers[index] = rightNumbers[0];
            rightNumbers = sliceNumbers(rightNumbers, 1, rightNumbers.length);
        }

        return joinSortedNumbers(leftNumbers, rightNumbers, joinedNumbers, ++index);
    }
}
