package pipeline;


/**
 * this class is a queue witch works as a pipeline between layers
 *
 * @param <T> the type of the object the queue will hold
 */
public class Queue<T> {
    public Node<T> head;
    public int size;
    private Node<T> tail;

    public Queue() {
        this.head = null;
        this.size = 0;
        this.tail = null;
    }

    /**
     * pops the first record
     * it is synchronized to prevent the objects from being popped several times by threads
     *
     * @return
     */
    public synchronized T pop() {
        if (size > 0) {
            Node<T> node = this.head;
            this.head = head.next;
            size--;

            return node.value;
        }
        return null;
    }

    /**
     * pushes incoming record to the back of the queue
     *
     * @param object the record
     */
    public synchronized void push(T object) {
        if (object != null)
            if (size == 0) {
                head = tail = new Node<T>(object);
                size++;
            } else {
                tail.next = new Node<T>(object);
                tail = tail.next;
                size++;
            }

    }

    /**
     * represents each node of queue
     * holding next record and the value of current record
     *
     * @param <T> the type of object witch we want to hold
     */
    private static class Node<T> {
        public Node<T> next;
        public T value;

        public Node(T value) {
            this.next = null;
            this.value = value;
        }
    }
}
