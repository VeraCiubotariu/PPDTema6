import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private ServerSocket serverSocket;
    private final Queue queue = new Queue();
    private static final ExecutorService listenerExecutor = Executors.newFixedThreadPool(Constants.P_R);
    private final List<Thread> workerThreads = new ArrayList<>();
    private static final LinkedList list = new LinkedList();
    private static final LinkedList bannedContestants = new LinkedList();
    private final List<Socket> clientSockets = new ArrayList<>();

    public void startListenerThreads( ) {
        for ( int i = 0; i < Constants.P_R; i++ ) {
            listenerExecutor.execute( new ListenerTask( serverSocket, queue, clientSockets, list ) );
        }
    }

    public void stopListenerThreads( ) {
        listenerExecutor.shutdown( );
    }

    private void startWorkerThreads( ) {
        for ( int i = 0; i < Constants.P_W; i++ ) {
            WorkerThread worker = new WorkerThread( queue, list, bannedContestants );
            workerThreads.add( worker );
            worker.start( );
        }
    }

    private void stopWorkerThreads( ) {
        for ( Thread t : workerThreads ) {
            try {
                t.join( );
            } catch ( InterruptedException e ) {
                e.printStackTrace( );
            }
        }
    }

    public void start( int port ) throws IOException {
        serverSocket = new ServerSocket( port );

        startWorkerThreads( );
        startListenerThreads( );
        stopWorkerThreads( );
        stopListenerThreads( );
    }

    public void stop( ) throws IOException {
        for ( Socket clientSocket : clientSockets ) {
            clientSocket.close( );
        }
        serverSocket.close( );
    }

    public static void main( String[] args ) {
        Logging.clearLog( );
        Server server = new Server( );
        try {
            long programStart = System.currentTimeMillis();
            server.start( 6666 );
            long programEnd = System.currentTimeMillis();
            System.out.println(programEnd - programStart);
        } catch ( IOException e ) {
            e.printStackTrace( );
        }
    }
}
