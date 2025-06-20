// Console-based Chess Game with Capture Tracking (OOP)
// Simplified: no castling, en passant, or promotion

import java.util.*;

public class ChessGame {
    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }
}

class Game {
    private Board board;
    private boolean whiteTurn = true;
    private Scanner scanner = new Scanner(System.in);
    private List<Piece> whiteCaptured = new ArrayList<>();
    private List<Piece> blackCaptured = new ArrayList<>();

    public Game() {
        board = new Board();
        board.initialize();
    }

    public void start() {
        while (true) {
            board.print();
            printScore();
            System.out.println((whiteTurn ? "White" : "Black") + " to move. Format: e2 e4");
            String from = scanner.next();
            if (from.equalsIgnoreCase("exit")) break;
            String to = scanner.next();
            if (to.equalsIgnoreCase("exit")) break;

            int fromX = 8 - Character.getNumericValue(from.charAt(1));
            int fromY = from.charAt(0) - 'a';
            int toX = 8 - Character.getNumericValue(to.charAt(1));
            int toY = to.charAt(0) - 'a';

            Piece piece = board.getPiece(fromX, fromY);
            if (piece == null || piece.isWhite() != whiteTurn) {
                System.out.println("Invalid move. Try again.");
                continue;
            }

            if (piece.isValidMove(board, fromX, fromY, toX, toY)) {
                Piece target = board.getPiece(toX, toY);
                if (target != null) {
                    if (whiteTurn) {
                        whiteCaptured.add(target);
                    } else {
                        blackCaptured.add(target);
                    }
                    System.out.println((whiteTurn ? "White" : "Black") + " captured " + target.getSymbol());
                }

                board.setPiece(toX, toY, piece);
                board.setPiece(fromX, fromY, null);
                whiteTurn = !whiteTurn;
            } else {
                System.out.println("Illegal move. Try again.");
            }
        }
    }

    private void printScore() {
        System.out.print("White captured: ");
        for (Piece p : whiteCaptured) System.out.print(p.getSymbol() + " ");
        System.out.println();

        System.out.print("Black captured: ");
        for (Piece p : blackCaptured) System.out.print(p.getSymbol() + " ");
        System.out.println();
    }
}

class Board {
    private Piece[][] board = new Piece[8][8];

    public void initialize() {
        for (int i = 0; i < 8; i++) {
            board[1][i] = new Pawn(false);
            board[6][i] = new Pawn(true);
        }
        board[0][0] = new Rook(false);
        board[0][7] = new Rook(false);
        board[7][0] = new Rook(true);
        board[7][7] = new Rook(true);

        board[0][1] = new Knight(false);
        board[0][6] = new Knight(false);
        board[7][1] = new Knight(true);
        board[7][6] = new Knight(true);

        board[0][2] = new Bishop(false);
        board[0][5] = new Bishop(false);
        board[7][2] = new Bishop(true);
        board[7][5] = new Bishop(true);

        board[0][3] = new Queen(false);
        board[7][3] = new Queen(true);

        board[0][4] = new King(false);
        board[7][4] = new King(true);
    }

    public Piece getPiece(int x, int y) {
        return board[x][y];
    }

    public void setPiece(int x, int y, Piece piece) {
        board[x][y] = piece;
    }

    public void print() {
        for (int i = 0; i < 8; i++) {
            System.out.print((8 - i) + " ");
            for (int j = 0; j < 8; j++) {
                Piece p = board[i][j];
                System.out.print((p == null ? "-" : p.getSymbol()) + " ");
            }
            System.out.println();
        }
        System.out.println("  a b c d e f g h");
    }
}

abstract class Piece {
    private boolean white;

    public Piece(boolean white) {
        this.white = white;
    }

    public boolean isWhite() {
        return white;
    }

    public abstract boolean isValidMove(Board board, int fromX, int fromY, int toX, int toY);

    public abstract char getSymbol();
}

class Pawn extends Piece {
    public Pawn(boolean white) {
        super(white);
    }

    @Override
    public boolean isValidMove(Board board, int fromX, int fromY, int toX, int toY) {
        int dir = isWhite() ? -1 : 1;
        if (fromY == toY && board.getPiece(toX, toY) == null) {
            if (toX - fromX == dir) return true;
            if ((isWhite() && fromX == 6 || !isWhite() && fromX == 1) && toX - fromX == 2 * dir)
                return board.getPiece(fromX + dir, fromY) == null;
        }
        if (Math.abs(fromY - toY) == 1 && toX - fromX == dir && board.getPiece(toX, toY) != null) {
            return board.getPiece(toX, toY).isWhite() != isWhite();
        }
        return false;
    }

    public char getSymbol() {
        return isWhite() ? 'P' : 'p';
    }
}

class Rook extends Piece {
    public Rook(boolean white) {
        super(white);
    }

    public boolean isValidMove(Board board, int fromX, int fromY, int toX, int toY) {
        if (fromX != toX && fromY != toY) return false;
        int stepX = Integer.compare(toX, fromX);
        int stepY = Integer.compare(toY, fromY);
        int x = fromX + stepX, y = fromY + stepY;
        while (x != toX || y != toY) {
            if (board.getPiece(x, y) != null) return false;
            x += stepX;
            y += stepY;
        }
        return board.getPiece(toX, toY) == null || board.getPiece(toX, toY).isWhite() != isWhite();
    }

    public char getSymbol() {
        return isWhite() ? 'R' : 'r';
    }
}

class Knight extends Piece {
    public Knight(boolean white) {
        super(white);
    }

    public boolean isValidMove(Board board, int fromX, int fromY, int toX, int toY) {
        int dx = Math.abs(fromX - toX);
        int dy = Math.abs(fromY - toY);
        if ((dx == 2 && dy == 1) || (dx == 1 && dy == 2)) {
            return board.getPiece(toX, toY) == null || board.getPiece(toX, toY).isWhite() != isWhite();
        }
        return false;
    }

    public char getSymbol() {
        return isWhite() ? 'N' : 'n';
    }
}

class Bishop extends Piece {
    public Bishop(boolean white) {
        super(white);
    }

    public boolean isValidMove(Board board, int fromX, int fromY, int toX, int toY) {
        if (Math.abs(fromX - toX) != Math.abs(fromY - toY)) return false;
        int stepX = Integer.compare(toX, fromX);
        int stepY = Integer.compare(toY, fromY);
        int x = fromX + stepX, y = fromY + stepY;
        while (x != toX && y != toY) {
            if (board.getPiece(x, y) != null) return false;
            x += stepX;
            y += stepY;
        }
        return board.getPiece(toX, toY) == null || board.getPiece(toX, toY).isWhite() != isWhite();
    }

    public char getSymbol() {
        return isWhite() ? 'B' : 'b';
    }
}

class Queen extends Piece {
    public Queen(boolean white) {
        super(white);
    }

    public boolean isValidMove(Board board, int fromX, int fromY, int toX, int toY) {
        Rook rook = new Rook(isWhite());
        Bishop bishop = new Bishop(isWhite());
        return rook.isValidMove(board, fromX, fromY, toX, toY) || bishop.isValidMove(board, fromX, fromY, toX, toY);
    }

    public char getSymbol() {
        return isWhite() ? 'Q' : 'q';
    }
}

class King extends Piece {
    public King(boolean white) {
        super(white);
    }

    public boolean isValidMove(Board board, int fromX, int fromY, int toX, int toY) {
        int dx = Math.abs(fromX - toX);
        int dy = Math.abs(fromY - toY);
        return (dx <= 1 && dy <= 1) && (board.getPiece(toX, toY) == null || board.getPiece(toX, toY).isWhite() != isWhite());
    }

    public char getSymbol() {
        return isWhite() ? 'K' : 'k';
    }
}
