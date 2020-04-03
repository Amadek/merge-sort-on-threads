package com.company;

import java.util.Random;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        RandomNumbersGenerator generator = new RandomNumbersGenerator(100, 1, 100);
        NumbersJoinSorter sorter = new NumbersJoinSorter(generator.generate(), true);

        printNumbers(sorter.sort());
    }

    public static void printNumbers(int[] numbers) {
        System.out.println("{");
        for (int i = 0; i < numbers.length; i++) {
            System.out.println(numbers[i]);
        }
        System.out.println("}");
    }
}

// Generator zbioru liczb pseudolosowych.
// Losuje zbiór z podanego zakresu.
class RandomNumbersGenerator {
    private final int _numbersCount;
    private final int _minNumber;
    private final int _maxNumber;
    private final Random _random = new Random();

    public RandomNumbersGenerator(int numbersCount, int minNumber, int maxNumber) {
        _numbersCount = numbersCount;
        _minNumber = minNumber;
        _maxNumber = maxNumber;
    }

    public int[] generate() {
        int[] numbers = new int[_numbersCount];

        for (int i = 0; i < _numbersCount; i++) {
            numbers[i] = _minNumber + _random.nextInt(_maxNumber - _minNumber + 1);
        }

        return numbers;
    }
}

// Interfejs obiektu sortującego liczby.
interface INumbersSorter {
    int[] sort() throws InterruptedException;
}

// Klasa sortowania liczb metodą sortowania przez scalanie.
// Tworzy i wywołuje samego siebie do momentu aż osiągnie mniej niż 5 elementów.
// Potem przełącza się na sortowanie przez wstawianie.
class NumbersJoinSorter implements INumbersSorter {
    private final int[] _numbers;
    private final boolean _concurrentMode;

    public NumbersJoinSorter(int[] numbers, boolean concurrentMode) {
        _numbers = numbers;
        _concurrentMode = concurrentMode;
    }

    @Override
    public int[] sort() throws InterruptedException {
        // Dla zbioru co najwyżej 5-elementowego stosujemy sortowanie przez wstawianie.
        if (_numbers.length <= 5) {
            return new NumbersInsertionSorter(_numbers).sort();
        }

        NumbersSlicer numbersSlicer = new NumbersSlicer(_numbers);
        int[] leftNumbers = numbersSlicer.slice(0, _numbers.length / 2);
        int[] rightNumbers = numbersSlicer.slice(_numbers.length / 2, _numbers.length);

        INumbersSorter leftNumbersSorter = new NumbersJoinSorter(leftNumbers, _concurrentMode);
        INumbersSorter rightNumbersSorter = new NumbersJoinSorter(rightNumbers, _concurrentMode);

        if (_concurrentMode) {
            NumbersSorterThread leftNumbersSorterThread = new NumbersSorterThread(leftNumbersSorter);
            NumbersSorterThread rightNumbersSorterThread = new NumbersSorterThread(rightNumbersSorter);
            leftNumbersSorterThread.start();
            rightNumbersSorterThread.start();
            leftNumbersSorterThread.join();
            rightNumbersSorterThread.join();

            leftNumbers = leftNumbersSorterThread.getSortedNumbers();
            rightNumbers = rightNumbersSorterThread.getSortedNumbers();
        } else {
            leftNumbers = leftNumbersSorter.sort();
            rightNumbers = rightNumbersSorter.sort();
        }

        return new SortedNumbersJoiner(leftNumbers, rightNumbers).join();
    }
}

// Klasa sortowania liczb metodą sortowania przez wstawianie.
class NumbersInsertionSorter implements INumbersSorter {
    private final int[] _numbers;

    public NumbersInsertionSorter(int[] numbers) {
        _numbers = this.copyNumbers(numbers);
    }

    @Override
    public int[] sort() {
        for (int i = 1; i < _numbers.length; i++) {
            int j = i;
            while (j > 0 && _numbers[j - 1] > _numbers[j]) {
                int buffer =  _numbers[j - 1];
                _numbers[j - 1] = _numbers[j];
                _numbers[j] = buffer;
                j--;
            }
        }

        return _numbers;
    }

    private int[] copyNumbers(int[] numbers) {
        int[] copiedNumbers = new int[numbers.length];

        for (int i = 0; i < numbers.length; i++) {
            copiedNumbers[i] = numbers[i];
        }

        return copiedNumbers;
    }
}

// Klasa łączenia już posortowanych dwóch zbiorów liczb.
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
                _leftNumbers = new NumbersSlicer(_leftNumbers).slice(1, _leftNumbers.length);
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

// Klasa dzielenia zbioru liczb na dwa zbiory.
class NumbersSlicer {
    private final int[] _numbers;

    public NumbersSlicer(int[] numbers) {
        _numbers = numbers;
    }

    // Dzieli w zakresie [begin, end).
    public int[] slice(int begin, int end) {
        int[] slicedNumbers = new int[end - begin];

        for (int i = begin; i < end; i++) {
            slicedNumbers[i - begin] = _numbers[i];
        }

        return slicedNumbers;
    }
}

// Wątek przyjmujące obiekt sortowania liczb.
// Wywołuje sortującego liczby.
class NumbersSorterThread extends Thread {
    private final INumbersSorter _numbersSorter;
    private int[] _sortedNumbers;

    public int[] getSortedNumbers() { return _sortedNumbers; }

    public NumbersSorterThread(INumbersSorter numbersSorter){
        _numbersSorter = numbersSorter;
    }

    @Override
    public void run() {
        try {
            _sortedNumbers = _numbersSorter.sort();
        } catch (InterruptedException ex) { }
    }
}
