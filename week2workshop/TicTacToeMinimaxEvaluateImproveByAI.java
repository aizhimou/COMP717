import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * this is the evaluation improvement by DeepSeek, it runs very well.
 * However, due to the small size of the board, the difference before and after the improvement is not significantly noticeable.
 */
public class TicTacToeMinimaxEvaluateImproveByAI {

    // 1. Game State Representation: 2D char array
    private static char[][] state;
    private static final char MAX_PLAYER = 'X';
    private static final char MIN_PLAYER = 'O';
    private static final char EMPTY_CELL = ' ';

    public static void main(String[] args) {
        initializeState();
        Scanner scanner = new Scanner(System.in);
        char currentPlayer; // Declare currentPlayer outside if-else

        System.out.println("Welcome to Tic-Tac-Toe vs Computer (Minimax)! ");
        System.out.print("Enter the difficulty level (depth for minimax, higher means harder, e.g., 3): ");
        int depth = 3; // Default depth if input is invalid
        try {
            depth = scanner.nextInt();
            if (depth < 1) {
                System.out.println("Depth should be at least 1. Using default depth 3.");
                depth = 3;
            }
        } catch (java.util.InputMismatchException e) {
            System.out.println("Invalid input. Using default depth 3.");
            scanner.next(); // consume the invalid input
            depth = 3;
        }
        System.out.println("Difficulty level set to depth: " + depth);

        System.out.print("Who should make the first move? (1 - Computer (X), 2 - You (O)): ");
        int firstMoveChooser = 1; // Default to computer first
        try {
            firstMoveChooser = scanner.nextInt();
            if (firstMoveChooser != 1 && firstMoveChooser != 2) {
                System.out.println("Invalid choice. Computer (X) will go first by default.");
                firstMoveChooser = 1;
            }
        } catch (java.util.InputMismatchException e) {
            System.out.println("Invalid input. Computer (X) will go first by default.");
            scanner.next(); // consume invalid input
            firstMoveChooser = 1;
        }

        if (firstMoveChooser == 1) {
            System.out.println("Computer (X) will make the first move.");
            currentPlayer = MAX_PLAYER;
        } else {
            System.out.println("You (O) will make the first move.");
            currentPlayer = MIN_PLAYER;
        }


        while (true) {
            printState();
            if (currentPlayer == MAX_PLAYER) {
                System.out.println("Computer (MAX - X) is thinking...");
                int[] bestMove = findBestMove(state, depth);
                makeMove(state, bestMove[0], bestMove[1], MAX_PLAYER);
                currentPlayer = MIN_PLAYER;
            } else {
                System.out.println("Your turn (MIN - O). Enter row and column (e.g., 0 0):");
                int row = -1, col = -1;
                boolean validInput = false;
                while (!validInput) {
                    try {
                        row = scanner.nextInt();
                        col = scanner.nextInt();
                        if (isValidMove(state, row, col)) {
                            validInput = true;
                        } else {
                            System.out.println("Invalid move. Cell is not empty or out of bounds. Try again:");
                        }
                    } catch (java.util.InputMismatchException e) {
                        System.out.println("Invalid input format. Enter row and column as numbers (e.g., 0 0). Try again:");
                        scanner.next(); // consume the invalid input
                    }
                }
                makeMove(state, row, col, MIN_PLAYER);
                currentPlayer = MAX_PLAYER;
            }

            if (isGameOver(state)) {
                printState();
                int score = evaluate(state);
                if (score == 10) {
                    System.out.println("Computer (MAX - X) wins!");
                } else if (score == -10) {
                    System.out.println("You (MIN - O) win!");
                } else {
                    System.out.println("It's a draw!");
                }
                break;
            }
        }
        scanner.close();
    }

    public static void initializeState() {
        state = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                state[i][j] = EMPTY_CELL;
            }
        }
    }

    public static void printState() {
        System.out.println("--------------");
        for (int i = 0; i < 3; i++) {
            System.out.print("| ");
            for (int j = 0; j < 3; j++) {
                System.out.print(state[i][j] + " | ");
            }
            System.out.println();
            System.out.println("--------------");
        }
    }

    // 2. Move Generation Function
    public static List<int[]> getValidMoves(char[][] state) {
        List<int[]> validMoves = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (state[i][j] == EMPTY_CELL) {
                    validMoves.add(new int[]{i, j});
                }
            }
        }
        return validMoves;
    }

    public static boolean isValidMove(char[][] state, int row, int col) {
        return row >= 0 && row < 3 && col >= 0 && col < 3 && state[row][col] == EMPTY_CELL;
    }

    public static void makeMove(char[][] state, int row, int col, char player) {
        if (isValidMove(state, row, col)) {
            state[row][col] = player;
        }
    }

    // 3. Evaluation Function
    public static int evaluate(char[][] state) {
        // 1. 检查是否已有玩家获胜（原逻辑保持不变）
        int terminalScore = checkTerminalStateScore(state);
        if (terminalScore != 0) return terminalScore;

        // 2. 启发式评分：评估潜在威胁和棋盘控制
        return calculateHeuristicScore(state);
    }

    private static int checkTerminalStateScore(char[][] state) {
        // 原胜负检查逻辑
        for (int i = 0; i < 3; i++) {
            if (state[i][0] == state[i][1] && state[i][1] == state[i][2]) {
                if (state[i][0] == MAX_PLAYER) return 10;
                if (state[i][0] == MIN_PLAYER) return -10;
            }
            if (state[0][i] == state[1][i] && state[1][i] == state[2][i]) {
                if (state[0][i] == MAX_PLAYER) return 10;
                if (state[0][i] == MIN_PLAYER) return -10;
            }
        }
        if (state[0][0] == state[1][1] && state[1][1] == state[2][2]) {
            if (state[0][0] == MAX_PLAYER) return 10;
            if (state[0][0] == MIN_PLAYER) return -10;
        }
        if (state[0][2] == state[1][1] && state[1][1] == state[2][0]) {
            if (state[0][2] == MAX_PLAYER) return 10;
            if (state[0][2] == MIN_PLAYER) return -10;
        }
        return 0;
    }

    private static int calculateHeuristicScore(char[][] state) {
        int score = 0;

        // 评分规则权重
        final int TWO_IN_ROW_SCORE = 3;  // 二连威胁
        final int SINGLE_IN_ROW_SCORE = 1; // 单子潜力
        final int CENTER_BONUS = 2;     // 中心控制

        // 评估所有行、列、对角线
        score += evaluateLines(state, TWO_IN_ROW_SCORE, SINGLE_IN_ROW_SCORE);

        // 中心位置奖励
        if (state[1][1] == MAX_PLAYER) score += CENTER_BONUS;
        else if (state[1][1] == MIN_PLAYER) score -= CENTER_BONUS;

        return score;
    }

    private static int evaluateLines(char[][] state, int twoInRowScore, int singleScore) {
        int total = 0;

        // 检查所有行、列、对角线
        total += evaluateLine(state, 0, 0, 0, 1, 0, 2, twoInRowScore, singleScore); // 第一行
        total += evaluateLine(state, 1, 0, 1, 1, 1, 2, twoInRowScore, singleScore); // 第二行
        total += evaluateLine(state, 2, 0, 2, 1, 2, 2, twoInRowScore, singleScore); // 第三行

        total += evaluateLine(state, 0, 0, 1, 0, 2, 0, twoInRowScore, singleScore); // 第一列
        total += evaluateLine(state, 0, 1, 1, 1, 2, 1, twoInRowScore, singleScore); // 第二列
        total += evaluateLine(state, 0, 2, 1, 2, 2, 2, twoInRowScore, singleScore); // 第三列

        total += evaluateLine(state, 0, 0, 1, 1, 2, 2, twoInRowScore, singleScore); // 主对角线
        total += evaluateLine(state, 0, 2, 1, 1, 2, 0, twoInRowScore, singleScore); // 副对角线

        return total;
    }

    private static int evaluateLine(char[][] state,
                                    int r1, int c1,
                                    int r2, int c2,
                                    int r3, int c3,
                                    int twoInRowScore,
                                    int singleScore) {
        int maxCount = 0, minCount = 0, empty = 0;

        // 统计当前线上的棋子分布
        if (state[r1][c1] == MAX_PLAYER) maxCount++;
        else if (state[r1][c1] == MIN_PLAYER) minCount++;
        else empty++;

        if (state[r2][c2] == MAX_PLAYER) maxCount++;
        else if (state[r2][c2] == MIN_PLAYER) minCount++;
        else empty++;

        if (state[r3][c3] == MAX_PLAYER) maxCount++;
        else if (state[r3][c3] == MIN_PLAYER) minCount++;
        else empty++;

        // 计算该线的威胁值
        int lineScore = 0;

        // MAX玩家潜在威胁
        if (minCount == 0) { // 该线没有MIN玩家的棋子
            if (maxCount == 2 && empty == 1) lineScore += twoInRowScore; // 二连威胁
            else if (maxCount == 1 && empty == 2) lineScore += singleScore; // 单子潜力
        }

        // MIN玩家潜在威胁
        if (maxCount == 0) { // 该线没有MAX玩家的棋子
            if (minCount == 2 && empty == 1) lineScore -= twoInRowScore; // 对手二连威胁
            else if (minCount == 1 && empty == 2) lineScore -= singleScore; // 对手单子潜力
        }

        return lineScore;
    }

    public static boolean isGameOver(char[][] state) {
        return getValidMoves(state).isEmpty() || Math.abs(evaluate(state)) == 10;
    }

    // 4. max_value(state, depth) Function
    public static int maxValue(char[][] state, int depth) {
        if (isGameOver(state)) {
            return evaluate(state);
        }
        if (depth == 0) {
            return evaluate(state); // 如果深度耗尽，返回当前评估值
        }

        int maxEval = Integer.MIN_VALUE;
        List<int[]> moves = getValidMoves(state);
        for (int[] move : moves) {
            char[][] nextState = copyState(state);
            makeMove(nextState, move[0], move[1], MAX_PLAYER);
            int eval = minValue(nextState, depth - 1);
            maxEval = Math.max(maxEval, eval);
        }
        return maxEval;
    }

    // 5. min_value(state, depth) Function
    public static int minValue(char[][] state, int depth) {
        if (isGameOver(state)) {
            return evaluate(state);
        }
        if (depth == 0) {
            return evaluate(state); // 如果深度耗尽，返回当前评估值
        }

        int minEval = Integer.MAX_VALUE;
        List<int[]> moves = getValidMoves(state);
        for (int[] move : moves) {
            char[][] nextState = copyState(state);
            makeMove(nextState, move[0], move[1], MIN_PLAYER);
            int eval = maxValue(nextState, depth - 1);
            minEval = Math.min(minEval, eval);
        }
        return minEval;
    }

    // 6. Get Best Move Function
    public static int[] findBestMove(char[][] state, int depth) {
        int bestMoveRow = -1;
        int bestMoveCol = -1;
        int maxEval = Integer.MIN_VALUE;

        for (int[] move : getValidMoves(state)) {
            char[][] nextState = copyState(state);
            makeMove(nextState, move[0], move[1], MAX_PLAYER);
            int eval = minValue(nextState, depth - 1); // MIN's response to MAX's move

            if (eval > maxEval) {
                maxEval = eval;
                bestMoveRow = move[0];
                bestMoveCol = move[1];
            }
        }
        return new int[]{bestMoveRow, bestMoveCol};
    }

    // Helper function to copy the state (for minimax simulation)
    public static char[][] copyState(char[][] state) {
        char[][] copy = new char[3][3];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(state[i], 0, copy[i], 0, 3);
        }
        return copy;
    }
}