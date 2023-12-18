public class Send_Bot {
    private String name;
    private String[] args;
    public Send_Bot(String name, String commands)
    {
        this.name=name;
        this.args = commands.split(" ");
        behave(args);
    }
    private void behave(String[] args)
    {
        Do.execute(name, args);
    }
}
