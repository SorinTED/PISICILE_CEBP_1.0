public class Event_Bot_Thread extends Thread{
    private int wait_ms;

    public void run() {
        int i = 0;
        while (true) {
            try {
                Do_events();
                Thread.sleep(wait_ms);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void Do_events()
    {
        //System.out.println("Event here");
        Topic.delete_posts_after();
    }

    public Event_Bot_Thread(int wait_ms)
    {
        this.wait_ms=wait_ms;
        this.start();
    }
}
