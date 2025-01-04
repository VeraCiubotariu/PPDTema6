public class WorkerThread extends Thread {
    private final Queue queue;
    private final LinkedList list;
    private final LinkedList bannedContestants;

    public WorkerThread(Queue queue, LinkedList list, LinkedList bannedContestants) {
        this.queue = queue;
        this.list = list;
        this.bannedContestants = bannedContestants;
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try{
                ContestEntry entry = queue.dequeue();
                // System.out.println(Thread.currentThread().getName() + ": " + entry);

                if(entry.score() == -1){
                    bannedContestants.add(entry.contestantID(), 0, "");
                    list.remove(entry.contestantID());
                }

                if(entry.contestantID() == -1 && entry.score() == -1){
                    Thread.currentThread().interrupt();
                }

                if(!bannedContestants.contains(entry.contestantID())){
                    list.add(entry.contestantID(), entry.score(), entry.country());
                }

                // list.sort();
            } catch (Exception ex){
                System.out.println(Thread.currentThread().getName() + ": " + ex);
            }
        }
    }
}


