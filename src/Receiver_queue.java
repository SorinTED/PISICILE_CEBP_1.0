import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.List;
public class Receiver_queue {
    public String receiver_name;
    public static LinkedList all_receivers = new LinkedList<>();
    private static LinkedList all_queues = new LinkedList<>();
    public LinkedList message_queue = new LinkedList();
    public Semaphore sem_receiver_queue = new Semaphore(1);

    public Receiver_queue(String receiver_name)
    {
        this.receiver_name = receiver_name;
        all_receivers.add(receiver_name);
        all_queues.add(this);
    }
    public static Receiver_queue find_queue_for(String user)
    {
        if(all_receivers.contains(user))
        {
            int index = all_receivers.indexOf(user);
            return (Receiver_queue) all_queues.get(index);
        }
        else
        {
            System.out.println("User not found!");
            return null;
        }
    }
    public void displayMessages(String user) {
        Receiver_queue userQueue = find_queue_for(user);

        if (userQueue != null) {
            try {
                // Acquire a lock to ensure synchronized access
                userQueue.sem_receiver_queue.acquire();

                // Display messages in the user's queue
                System.out.println("Messages for " + userQueue.receiver_name + ":");
                while (!userQueue.message_queue.isEmpty()) {
                    LinkedList content = (LinkedList) userQueue.message_queue.poll();
                    displayMessage(content);
                }

                // Release the lock
                userQueue.sem_receiver_queue.release();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }

    // Method to display a single message
    private void displayMessage(LinkedList content) {
        String sender = (String) content.get(0);
        String message = (String) content.get(1);
        Date timestamp = (Date) content.get(2);

        System.out.println("Sender: " + sender);
        System.out.println("Message: " + message);
        System.out.println("Timestamp: " + timestamp);
        System.out.println("---------------");
    }
    public void write(Receiver_queue user_queue, String sender ,String[] args)
    {
        LinkedList content = new LinkedList();
        content.add(sender);
        String message = "";
        for(int i=2; i<args.length; i += 1)
        {
            message+= args[i] + " ";
        }
        content.add(message);
        Date timestamp = new Date();
        content.add(timestamp);
        user_queue.message_queue.add(content);
    }
}
