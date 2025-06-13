package domain.tree;

import domain.Passenger;

public class AVLNode {
    public Passenger data;
    public AVLNode left;
    public AVLNode right;
    public int height;

    public AVLNode(Passenger data) {
        this.data = data;
        this.left = null;
        this.right = null;
        this.height = 1; // Altura inicial es 1
    }

    public Passenger getData() {
        return data;
    }

    public void setData(Passenger data) {
        this.data = data;
    }

    public AVLNode getLeft() {
        return left;
    }

    public void setLeft(AVLNode left) {
        this.left = left;
    }

    public AVLNode getRight() {
        return right;
    }

    public void setRight(AVLNode right) {
        this.right = right;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "AVLNode{" +
                "data=" + data +
                ", height=" + height +
                '}';
    }
}