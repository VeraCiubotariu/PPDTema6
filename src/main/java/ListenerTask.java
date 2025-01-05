import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ListenerTask implements Runnable {
    private final ServerSocket serverSocket;
    private final Queue queue;
    private final List<Socket> clientsSockets;
    private final LinkedList list;
    private String country = "";
    private final ExecutorService executor = Executors.newSingleThreadExecutor( );

    public ListenerTask( ServerSocket serverSocket, Queue queue, List<Socket> clientsSockets, LinkedList list ) {
        this.serverSocket = serverSocket;
        this.queue = queue;
        this.clientsSockets = clientsSockets;
        this.list = list;
    }

    @Override
    public void run( ) {
        Socket clientSocket;

        try {
            clientSocket = serverSocket.accept( );
            clientsSockets.add( clientSocket );

            try ( ObjectInputStream ois = new ObjectInputStream( clientSocket.getInputStream( ) );
                  ObjectOutputStream oos = new ObjectOutputStream( clientSocket.getOutputStream( ) ) ) {
                int recvAction = ois.readInt( );
                while ( recvAction != -1 ) {
                    // Data chunk
                    if ( recvAction == 1 ) {
                        Object buffer = ois.readObject( );

                        if ( buffer != null ) {
                            List<ContestEntry> list = ( List<ContestEntry> ) buffer;
                            this.country = list.get( 0 ).country( );
                            System.out.println( "Received buffer size: " + list.size( ) );
                            System.out.println( "Received: " + list );
                            for ( ContestEntry c : list ) {
                                queue.enqueue( c.contestantID( ), c.score( ), c.country( ) );
                            }
                        }

                        Logging.log( "Receiving chunk from country " + this.country );
                    }

                    // Information request
                    else if ( recvAction == 2 ) {
                        Logging.log( "Handling information request from country " + this.country );
                        Future<List<CountryScore>> future = executor.submit( ( ) -> {
                            Logging.log( "Recalculating country ranking..." );
                            return list.getCountryRanking( );
                        } );
                        oos.writeObject( future.get( ) );
                        oos.flush( );
                    }

                    recvAction = ois.readInt( );
                }

                // Sending the final rankings
                Logging.log( "Sending final rankings to country " + this.country );

                list.sort( );
                list.printListToFile( Constants.PATH + "ClasamentFinalConcurenti.txt" );
                list.printCountryClasament( Constants.PATH + "ClasamentFinalTari.txt" );
                executor.shutdown( );
                byte[] rankingParticipants = Files.readAllBytes( Path.of( Constants.PATH + "ClasamentFinalConcurenti.txt" ) );
                byte[] rankingCountries = Files.readAllBytes( Path.of( Constants.PATH + "ClasamentFinalTari.txt" ) );
                oos.writeObject( rankingParticipants );
                oos.flush( );
                oos.writeObject( rankingCountries );
                oos.flush( );
            } catch ( Exception ex ) {
                System.out.println( ex.getMessage( ) );
            }

            // Poison pill
            queue.enqueue( -1, -1, "" );
        } catch ( Exception e ) {
            e.printStackTrace( );
        }
    }
}
