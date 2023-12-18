public class Bot_Thread_Send extends Thread {

    private String name,commands;
    private int wait_ms;

    public void run() {
        int i = 0;
        while (true) {
            try {
                Send_Bot Bot = new Send_Bot(name,commands);
//                System.out.println(this.getName() + ": " + name + " is running - " + i++);
                Thread.sleep(wait_ms);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Bot_Thread_Send(String name, String commands, int wait_ms)
    {
        this.name=name;
        this.commands=commands;
        this.wait_ms=wait_ms;
    }

    public Bot_Thread_Send(String commands, int wait_ms)
    {
        this.commands=commands;
        this.wait_ms=wait_ms;
    }
}