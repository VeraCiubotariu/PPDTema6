import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LinkedList {
    private Node head;
    private final Lock lock = new ReentrantLock( );
    private long lastTimeCalculated = 0;

    private List<CountryScore> countryRanking;

    public LinkedList( ) {
        head = null;
    }

    public void add( int contestantID, int score, String countryName ) {
        Node newNode = new Node( contestantID, score, countryName );
        lock.lock( );

        try {
            Node current = head;

            while ( current != null ) {
                Node currentNode = current;
                current.getLock( ).lock( );

                try {
                    if ( current.getContestantID( ) == contestantID ) {
                        current.addScore( score );
                        return;
                    }
                    current = current.getNext( );
                } finally {
                    currentNode.getLock( ).unlock( );
                }
            }

            Node headNode = head;

            if ( headNode != null ) {
                headNode.getLock( ).lock( );
            }

            try {
                newNode.setNext( head );
                head = newNode;

            } finally {
                if ( headNode != null ) {
                    headNode.getLock( ).unlock( );
                }
            }
        } finally {
            lock.unlock( );
        }
    }

    public void remove( int contestantID ) {
        if ( head == null ) {
            return;
        }

        Node headNode = head;
        headNode.getLock( ).lock( );

        Node nextNode = headNode.getNext( );
        headNode.getNext( ).getLock( ).lock( );

        try {
            if ( head.getContestantID( ) == contestantID ) {
                head = head.getNext( );
                return;
            }
        } finally {
            nextNode.getLock( ).unlock( );
            headNode.getLock( ).unlock( );
        }

        Node current = head;

        while ( current.getNext( ) != null ) {
            Node currentNode = current;
            current.getLock( ).lock( );

            Node next = current.getNext( );
            next.getLock( ).lock( );

            try {
                if ( next.getContestantID( ) == contestantID ) {
                    current.setNext( next.getNext( ) );
                    return;
                }
                current = next;
            } finally {
                next.getLock( ).unlock( );
                currentNode.getLock( ).unlock( );
            }
        }
    }

    public boolean contains( int contestantID ) {
        if ( head == null ) {
            return false;
        }

        Node current = head;

        while ( current != null ) {
            Node currentNode = current;
            currentNode.getLock( ).lock( );

            try {
                if ( current.getContestantID( ) == contestantID ) {
                    return true;
                }
                current = current.getNext( );
            } finally {
                currentNode.getLock( ).unlock( );
            }
        }

        return false;
    }

    public void sort( ) {
        lock.lock( );
        if ( head == null || head.getNext( ) == null ) {
            return;
        }

        boolean swapped;
        do {
            swapped = false;
            Node current = head;
            Node prev = null;

            while ( current != null && current.getNext( ) != null ) {
                Node next = current.getNext( );

                if ( current.getScore( ) < next.getScore( ) || ( ( current.getScore( ) == next.getScore( ) ) && ( current.getContestantID( ) > next.getContestantID( ) ) ) ) {
                    if ( prev == null ) {
                        head = next;
                    } else {
                        prev.setNext( next );
                    }
                    current.setNext( next.getNext( ) );
                    next.setNext( current );

                    swapped = true;
                    prev = next;
                } else {
                    prev = current;
                    current = current.getNext( );
                }
            }
        } while ( swapped );
        lock.unlock( );
    }


    public void printList( ) {
        Node current = head;
        while ( current != null ) {
            System.out.println( current.getContestantID( ) + " " + current.getScore( ) );
            current = current.getNext( );
        }
    }

    public void printListToFile( String filePath ) {
        try ( BufferedWriter writer = new BufferedWriter( new FileWriter( filePath ) ) ) {
            Node current = head;
            while ( current != null ) {
                writer.write( current.getContestantID( ) + "," + current.getScore( ) + "," + current.getCountryName( ) );
                writer.newLine( );
                current = current.getNext( );
            }
        } catch ( IOException e ) {
            System.err.println( "An error occurred while writing to the file: " + e.getMessage( ) );
        }
    }

    public void printCountryClasament( String filePath ) {
        try ( BufferedWriter writer = new BufferedWriter( new FileWriter( filePath ) ) ) {
            List<CountryScore> countryScoreList = getCountryRanking( );
            for ( CountryScore countryScore : countryScoreList ) {
                writer.write( String.valueOf( countryScore ) );
                writer.newLine( );
            }
        } catch ( IOException e ) {
            System.err.println( "An error occurred while writing to the file: " + e.getMessage( ) );
        }
    }

    public final List<CountryScore> getCountryRanking( ) {
        if ( System.currentTimeMillis( ) - lastTimeCalculated > Constants.DELTA_T ) {
            Map<String, CountryScore> countryScoreMap = new HashMap<>( );
            var current = head;
            lock.lock( );
            while ( current != null ) {
                var country = current.getCountryName( );
                countryScoreMap.computeIfAbsent( country, k -> new CountryScore( country, 0 ) )
                        .addScore( current.getScore( ) );
                current = current.getNext( );
            }
            lock.unlock( );
            countryRanking = countryScoreMap.values( )
                    .stream( )
                    .sorted( Comparator.comparing( CountryScore::getScore ).reversed( ) )
                    .toList( );
            lastTimeCalculated = System.currentTimeMillis( );
        }
        return countryRanking;
    }
}
