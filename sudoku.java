package sudoku;

import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class sudoku {
    static int size = 9, cell = 3, sqrt = (int) Math.sqrt(size);
    static int[][] resultBoard, screenBoard;

    static boolean isSolved(int filledCount, int mistakes, int row, int col, int number, boolean[][] isWronglyEntered) {
        if (resultBoard[row][col] != number) {
            mistakes++;
            isWronglyEntered[row][col] = true;
            filledCount++;
        } else if (isWronglyEntered[row][col] && resultBoard[row][col] == number) {
            mistakes--;
        }
        return filledCount == size * size && mistakes == 0;
    }

    static int getRandomIndex() {
        Random getRandom = new Random();
        return getRandom.nextInt(size);
    }

    static void displayBoard(boolean[][] isWronglyEntered) {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int temp = screenBoard[row][col] > 9 ? (char) ('A' + (screenBoard[row][col] % 10))
                        : screenBoard[row][col] + '0';
                if (isWronglyEntered[row][col]) {
                    System.out.print(
                            "\u001B[31m" + (screenBoard[row][col] == 0 ? " "
                                    : (char) temp)
                                    + " \u001B[0m");
                    continue;
                }
                System.out.print((screenBoard[row][col] == 0 ? " "
                        : (char) temp)
                        + " ");
            }
            System.out.println();
        }
    }

    static boolean solveBoard(int[][] resultBoard) {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (resultBoard[row][col] == 0) {
                    for (int num = 1; num <= size; num++) {
                        if (isValidPosition(resultBoard, row, col, num)) {
                            resultBoard[row][col] = num;
                            if (solveBoard(resultBoard) == true) {
                                return true;
                            } else {
                                resultBoard[row][col] = 0;
                            }
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    static boolean isValidPosition(int[][] resultBoard, int row, int col, int number) {
        for (int i = 0; i < size; i++) {
            if (resultBoard[i][col] == number) {
                return false;
            }
            if (resultBoard[row][i] == number) {
                return false;
            }
            if (resultBoard[cell * (row / cell) + i / cell][cell * (col / cell) + i % cell] == number) {
                return false;
            }
        }
        return true;
    }

    static void fillBox(int row, int col) {
        for (int i = 0; i < sqrt; i++) {
            for (int j = 0; j < sqrt; j++) {
                resultBoard[row + i][col + j] = getRandomIndex();
            }
        }
    }

    static void fillCell() {
        for (int i = 0; i < size; i += sqrt) {
            fillBox(i, i);
        }
    }

    static void copyBoard(boolean[][] isQuestionNumber) {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                screenBoard[row][col] = resultBoard[row][col];
                isQuestionNumber[row][col] = true;
            }
        }
    }

    static void removeDigits(int k, boolean[][] isQuestionNumber) {
        for (int removes = 0; removes < k; removes++) {
            int row = getRandomIndex(), col = getRandomIndex();
            if (screenBoard[row][col] == 0) {
                for (int i = 0; i < size; i++) {
                    if (screenBoard[row][i] != 0) {
                        screenBoard[row][i] = 0;
                        isQuestionNumber[row][i] = false;
                        break;
                    } else if (screenBoard[i][col] != 0) {
                        screenBoard[i][col] = 0;
                        isQuestionNumber[i][col] = false;
                        break;
                    }
                }
            }
            screenBoard[row][col] = 0;
            isQuestionNumber[row][col] = false;
        }

    }

    static void boardInitialize(int difficultLevel, boolean[][] isQuestionNumber) {
        fillCell();
        Set<Integer> diagSet = new HashSet<>();
        for (int rows = 0; rows < size; rows += cell) {
            for (int row = rows; row < rows + cell; row++) {
                for (int col = rows; col < rows + cell; col++) {
                    if (diagSet.contains(resultBoard[row][col])) {
                        resultBoard[row][col] = 0;
                        continue;
                    }
                    diagSet.add(resultBoard[row][col]);
                }
            }
        }
        solveBoard(resultBoard);
        copyBoard(isQuestionNumber);
        removeDigits(difficultLevel, isQuestionNumber);
    }

    static int chooseDifficulty(int choice) {
        int returnValue = size * 4;
        switch (choice) {
            case (1):
                returnValue = size * 3;
                break;
            case (2):
                returnValue = size * 5;
                break;
            case (3):
                returnValue = size * 7;
                break;
        }
        return returnValue;
    }

    public static void main(String[] args) {
        Scanner getInput = new Scanner(System.in);
        int filledCount = size * size, mistakes = 0, inputRow = 0, inputCol = 0, number = 0;
        System.out.println("Enter the size of board cell you want range (2 - 4)");
        cell = getInput.nextInt();
        if (cell > 2 && cell <= 4) {
            size = cell * cell;
            filledCount = size * size;
            sqrt = cell;
        }
        boolean[][] isQuestionNumber = new boolean[size][size], isWronglyEntered = new boolean[size][size];
        resultBoard = new int[size][size];
        screenBoard = new int[size][size];
        System.out.println("Enter the difficulty level \n\t 1 - Easy \n\t 2 - Medium \n\t 3 - Hard");
        int difficultLevel = chooseDifficulty(getInput.nextInt());
        boardInitialize(difficultLevel, isQuestionNumber);
        filledCount -= difficultLevel;
        displayBoard(isWronglyEntered);
        do {
            System.out.println("\t 1 - to solve by yourself \n\t 2 - solved solution");
            int choice = getInput.nextInt();
            switch (choice) {
                case (1):
                    System.out.println("Enter row and column input : ");
                    inputRow = getInput.nextInt();
                    inputCol = getInput.nextInt();
                    if (!isQuestionNumber[inputRow][inputCol]) {
                        System.out.println("Enter the correct number");
                        number = getInput.nextInt();
                        if (isValidPosition(screenBoard, inputRow, inputCol, number)) {
                            screenBoard[inputRow][inputCol] = number;

                        } else {
                            System.out.println(
                                    "The number cannot be entered in this position, because it is getting interfering");
                        }
                    }
                    displayBoard(isWronglyEntered);
                    break;
                case (2):
                    copyBoard(isQuestionNumber);
                    displayBoard(isWronglyEntered);
                    return;
            }

        } while (!isSolved(filledCount, mistakes, inputRow, inputCol, number, isWronglyEntered));
    }
}