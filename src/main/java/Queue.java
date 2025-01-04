import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Queue {
    private Node firstNode;
    private Node lastNode;
    private final int MAX_CAPACITY = 50;
    private int size;

    private final Lock lock = new ReentrantLock();
    private final Condition isEmpty = lock.newCondition();
    private final Condition isFull = lock.newCondition();

    public Queue() {
        firstNode = lastNode = null;
        this.size = 0;
    }

    public void enqueue(int contestantID, int score, String countryName) throws InterruptedException {
        lock.lock();
        try {
            while (size == MAX_CAPACITY) {
                isEmpty.await();
            }

            Node newNode = new Node(contestantID, score, countryName);

            if (firstNode == null) {
                firstNode = lastNode = newNode;
            } else {
                lastNode.setNext(newNode);
                lastNode = newNode;
            }

            size++;
            isFull.signal();
        } finally {
            lock.unlock();
        }
    }

    public ContestEntry dequeue() throws Exception {
        lock.lock();
        try {
            while (size == 0) {
                isFull.await();
            }

            ContestEntry result = new ContestEntry(firstNode.getContestantID(), firstNode.getScore(), firstNode.getCountryName());
            firstNode = firstNode.getNext();

            if (firstNode == null) {
                lastNode = null;
            }

            isEmpty.signal();
            size--;
            return result;
        } finally {
            lock.unlock();
        }
    }

    public void printQueue() {
        lock.lock();
        try{
            Node currentNode = firstNode;
            while (currentNode != null) {
                System.out.print(currentNode.getContestantID() + " ");
                currentNode = currentNode.getNext();
            }
            System.out.println();
        } finally {
            lock.unlock();
        }
    }
}
