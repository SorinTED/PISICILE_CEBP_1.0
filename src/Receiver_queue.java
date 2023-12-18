import java.util.Date;
import java.util.LinkedList;
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
