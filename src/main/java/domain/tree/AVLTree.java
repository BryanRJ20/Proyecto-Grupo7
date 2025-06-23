package domain.tree;

import domain.Passenger;

public class AVLTree {
    private AVLNode root;

    public AVLTree() {
        this.root = null;
    }

    // Obtener altura de un nodo
    private int getHeight(AVLNode node) {
        if (node == null) return 0;
        return node.height;
    }

    // Obtener factor de balance de un nodo
    private int getBalance(AVLNode node) {
        if (node == null) return 0;
        return getHeight(node.left) - getHeight(node.right);
    }

    // Actualizar altura de un nodo
    private void updateHeight(AVLNode node) {
        if (node != null) {
            node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));
        }
    }

    // Rotación a la derecha
    private AVLNode rotateRight(AVLNode y) {
        AVLNode x = y.left;
        AVLNode T2 = x.right;

        // Realizar rotación
        x.right = y;
        y.left = T2;

        // Actualizar alturas
        updateHeight(y);
        updateHeight(x);

        // Retornar nueva raíz
        return x;
    }

    // Rotación a la izquierda
    private AVLNode rotateLeft(AVLNode x) {
        AVLNode y = x.right;
        AVLNode T2 = y.left;

        // Realizar rotación
        y.left = x;
        x.right = T2;

        // Actualizar alturas
        updateHeight(x);
        updateHeight(y);

        // Retornar nueva raíz
        return y;
    }

    // Insertar un pasajero
    public void insert(Passenger passenger) {
        root = insertRec(root, passenger);
    }

    private AVLNode insertRec(AVLNode node, Passenger passenger) {
        // 1. Inserción normal del BST
        if (node == null) {
            return new AVLNode(passenger);
        }

        if (passenger.getId() < node.data.getId()) {
            node.left = insertRec(node.left, passenger);
        } else if (passenger.getId() > node.data.getId()) {
            node.right = insertRec(node.right, passenger);
        } else {
            // IDs iguales, actualizar datos
            node.data = passenger;
            return node;
        }

        // 2. Actualizar altura del nodo ancestro
        updateHeight(node);

        // 3. Obtener factor de balance
        int balance = getBalance(node);

        // 4. Rotaciones para mantener balance

        // Caso Izquierda-Izquierda
        if (balance > 1 && passenger.getId() < node.left.data.getId()) {
            return rotateRight(node);
        }

        // Caso Derecha-Derecha
        if (balance < -1 && passenger.getId() > node.right.data.getId()) {
            return rotateLeft(node);
        }

        // Caso Izquierda-Derecha
        if (balance > 1 && passenger.getId() > node.left.data.getId()) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // Caso Derecha-Izquierda
        if (balance < -1 && passenger.getId() < node.right.data.getId()) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        // Retornar el nodo sin cambios
        return node;
    }

    // Buscar un pasajero por ID
    public Passenger search(int id) {
        AVLNode result = searchRec(root, id);
        return result != null ? result.data : null;
    }

    private AVLNode searchRec(AVLNode node, int id) {
        if (node == null || node.data.getId() == id) {
            return node;
        }

        if (id < node.data.getId()) {
            return searchRec(node.left, id);
        }

        return searchRec(node.right, id);
    }

    // Encontrar el nodo con valor mínimo
    private AVLNode findMinNode(AVLNode node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    // Eliminar un pasajero
    public void delete(int id) {
        root = deleteRec(root, id);
    }

    private AVLNode deleteRec(AVLNode node, int id) {
        // 1. Eliminación normal del BST
        if (node == null) {
            return node;
        }

        if (id < node.data.getId()) {
            node.left = deleteRec(node.left, id);
        } else if (id > node.data.getId()) {
            node.right = deleteRec(node.right, id);
        } else {
            // Nodo a eliminar encontrado
            if (node.left == null || node.right == null) {
                AVLNode temp = (node.left != null) ? node.left : node.right;

                if (temp == null) {
                    // Caso sin hijos
                    temp = node;
                    node = null;
                } else {
                    // Caso con un hijo
                    node = temp;
                }
            } else {
                // Caso con dos hijos
                AVLNode temp = findMinNode(node.right);
                node.data = temp.data;
                node.right = deleteRec(node.right, temp.data.getId());
            }
        }

        if (node == null) {
            return node;
        }

        // 2. Actualizar altura
        updateHeight(node);

        // 3. Obtener factor de balance
        int balance = getBalance(node);

        // 4. Rotaciones para mantener balance

        // Caso Izquierda-Izquierda
        if (balance > 1 && getBalance(node.left) >= 0) {
            return rotateRight(node);
        }

        // Caso Izquierda-Derecha
        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // Caso Derecha-Derecha
        if (balance < -1 && getBalance(node.right) <= 0) {
            return rotateLeft(node);
        }

        // Caso Derecha-Izquierda
        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    // Recorrido inorder
    public String inOrder() {
        StringBuilder sb = new StringBuilder();
        inOrderRec(root, sb);
        return sb.toString();
    }

    private void inOrderRec(AVLNode node, StringBuilder sb) {
        if (node != null) {
            inOrderRec(node.left, sb);
            sb.append(node.data.toString()).append("\n");
            inOrderRec(node.right, sb);
        }
    }

    // Verificar si está vacío
    public boolean isEmpty() {
        return root == null;
    }

    // Obtener tamaño del árbol
    public int size() {
        return sizeRec(root);
    }

    private int sizeRec(AVLNode node) {
        if (node == null) {
            return 0;
        }
        return 1 + sizeRec(node.left) + sizeRec(node.right);
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "AVL Tree is empty";
        }
        return "AVL Tree Content:\n" + inOrder();
    }


    public AVLNode getRoot() {
        return root;
    }
}

