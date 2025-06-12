package domain.list;

/**
 * Implementación de Lista Circular Enlazada para gestión de usuarios
 */
public class CircularLinkedList {
    private Node head;
    private Node tail;
    private int size;

    public CircularLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public int size() {
        return size;
    }

    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    /**
     * Agrega un elemento al final de la lista
     */
    public void add(Object element) throws ListException {
        if (element == null) {
            throw new ListException("Element cannot be null");
        }

        Node newNode = new Node(element);

        if (isEmpty()) {
            head = newNode;
            tail = newNode;
            newNode.next = head; // Circular: apunta a sí mismo
        } else {
            tail.next = newNode;
            newNode.next = head; // Circular: el último apunta al primero
            tail = newNode;
        }
        size++;
    }

    /**
     * Agrega un elemento en una posición específica
     */
    public void add(Object element, int index) throws ListException {
        if (element == null) {
            throw new ListException("Element cannot be null");
        }
        if (index < 0 || index > size) {
            throw new ListException("Index out of bounds: " + index);
        }

        if (index == size) {
            add(element); // Agregar al final
            return;
        }

        Node newNode = new Node(element);

        if (index == 0) {
            if (isEmpty()) {
                head = tail = newNode;
                newNode.next = head;
            } else {
                newNode.next = head;
                tail.next = newNode;
                head = newNode;
            }
        } else {
            Node current = head;
            for (int i = 0; i < index - 1; i++) {
                current = current.next;
            }
            newNode.next = current.next;
            current.next = newNode;
        }
        size++;
    }

    /**
     * Remueve un elemento por índice
     */
    public Object remove(int index) throws ListException {
        if (isEmpty()) {
            throw new ListException("List is empty");
        }
        if (index < 0 || index >= size) {
            throw new ListException("Index out of bounds: " + index);
        }

        Object removedData;

        if (size == 1) {
            removedData = head.data;
            clear();
            return removedData;
        }

        if (index == 0) {
            removedData = head.data;
            head = head.next;
            tail.next = head;
        } else {
            Node current = head;
            for (int i = 0; i < index - 1; i++) {
                current = current.next;
            }
            removedData = current.next.data;

            if (current.next == tail) {
                tail = current;
            }
            current.next = current.next.next;
        }

        size--;
        return removedData;
    }

    /**
     * Remueve un elemento específico
     */
    public boolean remove(Object element) throws ListException {
        if (isEmpty()) {
            return false;
        }

        int index = indexOf(element);
        if (index != -1) {
            remove(index);
            return true;
        }
        return false;
    }

    /**
     * Obtiene un elemento por índice
     */
    public Object get(int index) throws ListException {
        if (isEmpty()) {
            throw new ListException("List is empty");
        }
        if (index < 0 || index >= size) {
            throw new ListException("Index out of bounds: " + index);
        }

        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }

    /**
     * Actualiza un elemento en una posición específica
     */
    public Object set(int index, Object element) throws ListException {
        if (element == null) {
            throw new ListException("Element cannot be null");
        }
        if (isEmpty()) {
            throw new ListException("List is empty");
        }
        if (index < 0 || index >= size) {
            throw new ListException("Index out of bounds: " + index);
        }

        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        Object oldData = current.data;
        current.data = element;
        return oldData;
    }

    /**
     * Busca la primera ocurrencia de un elemento
     */
    public int indexOf(Object element) throws ListException {
        if (isEmpty()) {
            return -1;
        }

        Node current = head;
        for (int i = 0; i < size; i++) {
            if (util.Utility.compare(current.data, element) == 0) {
                return i;
            }
            current = current.next;
        }
        return -1;
    }

    /**
     * Verifica si la lista contiene un elemento
     */
    public boolean contains(Object element) throws ListException {
        return indexOf(element) != -1;
    }

    /**
     * Busca un elemento que cumpla con un criterio específico
     */
    public Object find(SearchCriteria criteria) throws ListException {
        if (isEmpty()) {
            return null;
        }

        Node current = head;
        for (int i = 0; i < size; i++) {
            if (criteria.matches(current.data)) {
                return current.data;
            }
            current = current.next;
        }
        return null;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "Circular List is empty";
        }

        StringBuilder result = new StringBuilder("Circular List: [");
        Node current = head;
        for (int i = 0; i < size; i++) {
            result.append(current.data);
            if (i < size - 1) {
                result.append(", ");
            }
            current = current.next;
        }
        result.append("]");
        return result.toString();
    }

    /**
     * Interfaz para criterios de búsqueda
     */
    public interface SearchCriteria {
        boolean matches(Object element);
    }
}