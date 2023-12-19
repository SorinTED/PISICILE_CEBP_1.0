import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Receiver_queue {
    public String receiver_name;
    private int max_messages = 20;
    public static LinkedList all_receivers = new LinkedList<>();
    private static LinkedList all_queues = new LinkedList<>();
    public LinkedList message_queue = new LinkedList();

    public static boolean concurrency_enabled_for_write = true;
    public static boolean verbose = false;

    public Semaphore sem_message_queue_wr = new Semaphore(1);
    public static Semaphore sem_linked_lists_wr = new Semaphore(1);

    public Semaphore sem_message_queue_rd = new Semaphore(3);
    public static Semaphore sem_linked_lists_rd = new Semaphore(3);

    public Lock lock_max_messages = new ReentrantLock();

    public static Event_Bot_Thread event_bot = new Event_Bot_Thread("Messages",1000);

    public Receiver_queue(String receiver_name)
    {
        try {
            sem_linked_lists_wr.acquire();

            this.receiver_name = receiver_name;
            all_receivers.add(receiver_name);
            all_queues.add(this);

            sem_linked_lists_wr.release();
        }catch (Exception exc)
        {
            System.out.println(exc);
        }
    }

    public void setMax_messages(int max_messages)
    {
        this.lock_max_messages.lock();
        this.max_messages = max_messages;
        this.lock_max_messages.unlock();
    }

    public static Receiver_queue find_queue_for(String user)
    {
        try {
            sem_linked_lists_rd.acquire();

            if(all_receivers.contains(user))
            {
                int index = all_receivers.indexOf(user);
                sem_linked_lists_rd.release();
                return (Receiver_queue) all_queues.get(index);
            }
            else
            {
                System.out.println("User not found!");
                sem_linked_lists_rd.release();
                return null;
            }
        }catch (Exception exc)
        {
            System.out.println(exc);
        }
        return null;
    }

    public static String list_of_users()
    {
        try {
            sem_linked_lists_rd.acquire();

            String to_return = all_receivers.toString();

            sem_linked_lists_rd.release();

            return to_return;
        }catch (Exception exc)
        {
            System.out.println(exc);
        }
        return null;
    }

    public boolean space_left_in_queue()
    {

        lock_max_messages.lock();
        if(this.max_messages - this.message_queue.size()>0) {
            lock_max_messages.unlock();

            return true;
        }
        else {
            lock_max_messages.unlock();
            return false;
        }
    }
    public void write(Receiver_queue user_queue, String sender ,String[] args)
    {
        try {
            if (concurrency_enabled_for_write)
                user_queue.sem_message_queue_wr.acquire();

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

            //here we have also a NullPointerException in case of concurrent modifications
            if(verbose)
                System.out.println(user_queue.receiver_name + ": " + user_queue.message_queue);
            System.out.println("Message sent successfully");

            if (concurrency_enabled_for_write)
                user_queue.sem_message_queue_wr.release();
        }catch (Exception exc)
        {
            System.out.println(exc);
        }
    }

    public void read_messages_from_queue(Receiver_queue user_queue,String user,String sender,boolean is_admin)
    {

        if(user_queue != null)
        {
            if(user_queue.message_queue.size()>0)
            {
                try {
                    user_queue.sem_message_queue_wr.acquire();
                    user_queue.header(sender);

                        LinkedList<LinkedList> messageQueueCopy = new LinkedList<>(this.message_queue);
                        for (LinkedList content : messageQueueCopy)
                        {
                            if(sender.equals("all"))
                                display_all_messages(content);
                            else
                            if(sender.equals((String)content.get(0)))
                            {
                                display_message(content);
                                if(!is_admin)
                                    user_queue.message_queue.remove(content);
                            }
                        }
                        if(!is_admin && sender.equals("all"))
                            user_queue.empty_queue();

                    System.out.println("------------------------------------------------------------");
                    user_queue.sem_message_queue_wr.release();
                } catch (Exception exc)
                {
                    System.out.println(exc);
                }
            }
                else
                    System.out.print("There are no messages in queue for user "+ user+".\n");

        }
        else
        {
            System.out.print("There is no queue for user "+ user+".\n");
        }
    }
    private void header(String sender){

        System.out.println("------------------------------------------------------------");
        if(sender.equals("all"))
            System.out.println("|All messages for " + this.receiver_name + ":");
        else
            System.out.println("|Messages for " + this.receiver_name +" from "+sender+ ":");
        System.out.println("------------------------------------------------------------");
    }
    private void display_all_messages(LinkedList content) {
        String sender = (String) content.get(0);
        String message = (String) content.get(1);
        Date timestamp = (Date) content.get(2);

        System.out.println("Sender: " + sender);
        System.out.println("Message: " + message);
        System.out.println("Timestamp: " + timestamp);
    }
    private void display_message(LinkedList content) {
        String message = (String) content.get(1);
        Date timestamp = (Date) content.get(2);

        System.out.println("Message: " + message);
        System.out.println("Timestamp: " + timestamp);
    }

    public static void notifyUserUponLogin(String loggedInUser) {
        try {
            sem_linked_lists_rd.acquire();
            Receiver_queue loggedInQueue = find_queue_for(loggedInUser);

            if (loggedInQueue != null) {
                loggedInQueue.sem_message_queue_rd.acquire();
                // loggedInQueue.header("all");
                System.out.println("======================================================================");
                if (loggedInQueue.message_queue.size() > 0) {
                    System.out.println("Welcome! You have " + loggedInQueue.message_queue.size() + " new messages!");

                    // Keep track of processed senders
                    List<String> processedSenders = new ArrayList<>();

                    for (Object element : loggedInQueue.message_queue) {
                        LinkedList content = (LinkedList) element;
                        String sender = (String) content.get(0);

                        // Check if sender is already processed
                        if (!((ArrayList<?>) processedSenders).contains(sender)) {
                            int messagesFromSender = countMessagesFromSender(loggedInQueue.message_queue, sender);

                            System.out.println("Sender: " + sender + " | Messages: " + messagesFromSender);

                            // Add sender to the list of processed senders
                            processedSenders.add(sender);
                        }
                    }
                } else
                    System.out.println("Welcome! You are up to date");
                System.out.println("======================================================================");
                loggedInQueue.sem_message_queue_rd.release();
                System.out.println("------------------------------------------------------------");
            } else {
                System.out.println("User not found!");
            }

            sem_linked_lists_rd.release();
        } catch (Exception exc) {
            System.out.println(exc);
        }
    }


    private static int countMessagesFromSender(LinkedList messageQueue, String sender) {
        int count = 0;
        for (Object element : messageQueue) {
            LinkedList content = (LinkedList) element;
            if (sender.equals(content.get(0))) {
                count++;
            }
        }
        return count;
    }
    //Event 1
    public static void verify_number_of_messages()
    {
        try {
            sem_linked_lists_rd.acquire();
            for(Object element : Receiver_queue.all_queues)
            {
                Receiver_queue receiver_queue = (Receiver_queue)(element);
                receiver_queue.sem_message_queue_rd.acquire();

                if(!receiver_queue.space_left_in_queue())
                    System.out.println("The inbox for " + receiver_queue.receiver_name + " is full");

                receiver_queue.sem_message_queue_rd.release();
            }
            sem_linked_lists_rd.release();
        }catch (Exception exc){
            System.out.println(exc);
        }
    }
    public void empty_queue()
    {
        try {
            this.sem_message_queue_wr.acquire();

            this.message_queue.clear();

            this.sem_message_queue_wr.release();
        } catch (Exception exc)
        {
            System.out.println(exc);
        }
    }
    public void delete_user()
    {
        try {
            sem_linked_lists_wr.acquire();

            all_receivers.remove(receiver_name);
            all_queues.remove(this);
            empty_queue();

            sem_linked_lists_wr.release();
        }catch (Exception exc)
        {
            System.out.println(exc);
        }
    }
}
