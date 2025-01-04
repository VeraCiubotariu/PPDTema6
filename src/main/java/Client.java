import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Client {
    private final String countryName;

    public Client(String countryName) {
        this.countryName = countryName;
    }

    public void runClient() {
        ClientSocket client = new ClientSocket();

        try {
            client.startConnection("127.0.0.1", 6666);

            File folder = new File(Constants.PATH);
            File[] listOfFiles = folder.listFiles();

            if(listOfFiles != null) {
                List<ContestEntry> contestEntries;

                for (File file : listOfFiles) {
                    if(file.getName().contains(countryName)) {
                        contestEntries = Utils.readEntries(file, countryName);

                        for(int i=0; i<contestEntries.size(); i+=20) {
                            System.out.println(i);
                            int max = Math.min(contestEntries.size(), i + 20);
                            List<ContestEntry> buffer = new ArrayList<>(contestEntries.subList(i, max));
                            System.out.println("Sending buffer with size: " + buffer.size());
                            System.out.println("Sending: " + buffer);

                            client.sendEntries(buffer);
                        }

                        // Information request
                        client.sendInformationRequest();

                        // TODO: Maybe the results of the information requests can be received from the server in a separate thread in the client
                   /*     String finalRanking = client.getFinalContestantsRanking();
                        System.out.println("Country partial ranking: " + finalRanking);*/
                    }
                }
            }

            System.out.println("Ending stream");
            client.markEndOfStream();

            String finalRanking = client.getFinalContestantsRanking();
            System.out.println("Final ranking: " + finalRanking);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
