import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class ClientSocket {
    private Socket clientSocket;
    private ObjectOutputStream oos;

    public void startConnection( String ip, int port ) throws IOException {
        clientSocket = new Socket( ip, port );

        try {
            oos = new ObjectOutputStream( clientSocket.getOutputStream( ) );
        } catch ( IOException e ) {
            e.printStackTrace( );
        }
    }

    public void sendEntries( List<ContestEntry> entries ) throws IOException {
        oos.writeInt( 1 );
        oos.writeObject( entries );
        oos.flush( );

        try {
            Thread.sleep( Constants.DELTA_X );
        } catch ( InterruptedException e ) {
        }
    }

    public void sendInformationRequest( ) throws IOException {
        oos.writeInt( 2 );
        oos.flush( );
        Logging.log( "sent request for partial ranking" );
    }

    public void markEndOfStream( ) throws IOException {
        oos.writeInt( -1 );
        oos.flush( );
    }

    public void stopConnection( ) throws IOException {
        oos.close( );
        clientSocket.close( );
    }
    public String getFinalContestantsRanking( ) throws IOException {
        try {
            ObjectInputStream ois = new ObjectInputStream( clientSocket.getInputStream( ) );
            byte[] ranking = ( byte[] ) ois.readObject( );
            String rankingString = new String( ranking, StandardCharsets.UTF_8 );

            System.out.println( rankingString );
            ois.close();
            return rankingString;
        } catch ( ClassNotFoundException e ) {
            System.out.println( e.getMessage( ) );
        }

        return "";
    }
}
