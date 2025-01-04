public class ClientsRunner {
    public static void main(String[] args) {
        for(int i=1;i<=Constants.P_R;i++){
            Client client = new Client("C" + i);
            client.runClient();
        }

    }
}
