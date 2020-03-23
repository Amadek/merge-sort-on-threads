package com.company;

public class Main {
    public static void main(String[] args) {
        int[] numbers = { 8, 3, 2, 5, 4 };

        printNumbers(new NumbersSorter(numbers).sort());
    }

    public static void printNumbers(int[] numbers) {
        System.out.println("{");
        for (int i = 0; i < numbers.length; i++) {
            System.out.println(numbers[i]);
        }
        System.out.println("}");
    }
}

class NumbersSorter {
    private final int[] _numbers;

    public NumbersSorter(int[] numbers) {
        _numbers = numbers;
    }

    public int[] sort() {
        if (_numbers.length == 1) {
            return _numbers;
        }

        NumbersSlicer numbersSlicer = new NumbersSlicer(_numbers);
        int[] leftNumbers = numbersSlicer.slice(0, _numbers.length / 2);
        int[] rightNumbers = numbersSlicer.slice(_numbers.length / 2, _numbers.length);

        return new SortedNumbersJoiner(new NumbersSorter(leftNumbers).sort(), new NumbersSorter(rightNumbers).sort()).join();
    }
}

class SortedNumbersJoiner {
    private int[] _leftNumbers;
    private int[] _rightNumbers;

    public SortedNumbersJoiner(int[] leftNumbers, int[] rightNumbers) {
        _leftNumbers = leftNumbers;
        _rightNumbers = rightNumbers;
    }

    public int[] join() {
        int[] joinedNumbers = new int[_leftNumbers.length + _rightNumbers.length];

        int index = 0;
        while (_leftNumbers.length != 0 || _rightNumbers.length != 0) {
            if (_leftNumbers.length == 0) {
                joinedNumbers[index] = _rightNumbers[0];
                // Remove added right number to joined numbers.
                _rightNumbers = new NumbersSlicer(_rightNumbers).slice(1, _rightNumbers.length);
            } else if (_rightNumbers.length == 0) {
                joinedNumbers[index] = _leftNumbers[0];
                // Remove added left number to joined numbers.
                _leftNumbers = new NumbersSlicer(_rightNumbers).slice(1, _leftNumbers.length);
            } else if (_leftNumbers[0] < _rightNumbers[0]) {
                joinedNumbers[index] = _leftNumbers[0];
                _leftNumbers = new NumbersSlicer(_leftNumbers).slice(1, _leftNumbers.length);
            } else {
                joinedNumbers[index] = _rightNumbers[0];
                _rightNumbers = new NumbersSlicer(_rightNumbers).slice(1, _rightNumbers.length);
            }
            index++;
        }

        return joinedNumbers;
    }
}

class NumbersSlicer {
    private final int[] _numbers;

    public NumbersSlicer(int[] numbers) {
        _numbers = numbers;
    }

    // Slice numbers in range [begin, end).
    public int[] slice(int begin, int end) {
        int[] slicedNumbers = new int[end - begin];

        for (int i = begin; i < end; i++) {
            slicedNumbers[i - begin] = _numbers[i];
        }

        return slicedNumbers;
    }
}
