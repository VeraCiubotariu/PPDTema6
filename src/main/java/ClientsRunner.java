import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientsRunner {
    public static void main( String[] args ) {
        ExecutorService executor = Executors.newFixedThreadPool( Constants.NUMBER_OF_CLIENTS );

        for ( int i = 1; i <= Constants.NUMBER_OF_CLIENTS; i++ ) {
            Client client = new Client( "C" + i );
            executor.submit( client::runClient );
        }

        executor.shutdown( );
    }

}

