public class Event_Bot_Thread extends Thread{
    private int wait_ms;
    public String type;

    public void run() {
        int i = 0;
        while (true) {
            try {
                if (type.equals("Topic"))
                    Do_events_topic();
                else if(type.equals("Database"))
                    Do_events_database();
                else
                    Do_events_messages();
                Thread.sleep(wait_ms);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void Do_events_messages() {
        Receiver_queue.verify_number_of_messages();
    }

    private void Do_events_topic()
    {
        //System.out.println("Event here");
        Topic.delete_posts_after();
        Topic.verify_number_of_posts();
    }

    private void Do_events_database()
    {
        //System.out.println("Event here");
        if(false)
        {
            System.out.println("New user login!");
        }
    }

    public Event_Bot_Thread(String type, int wait_ms)
    {
        this.wait_ms = wait_ms;
        this.type = type;
        this.start();
    }
}
