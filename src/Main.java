import java.util.Scanner;

/*
    TO DO:
        - topic header?
        - remove topic after X amount of time (read specs)

        -add events
            + maximum number of topics/messages in queues
 */

//read from Radu

public class Main {

    public static void main(String[] args) {
        boolean bots_on = false;
            boolean message_on = true;
            boolean topic_on = false;
        boolean my_terminal_on = true;

	    Receiver_queue Sorin_messages = new Receiver_queue("Sorin");
        Receiver_queue Radu_messages = new Receiver_queue("Radu");

        Topic moto = new Topic("Moto");

        if(bots_on)
        {
            if(topic_on)
            {
                Bot_Thread_Send bot1 = new Bot_Thread_Send("Jack","add Moto Hello",1000);
                Bot_Thread_Send bot2 = new Bot_Thread_Send("John","add Moto Yello",1000);
                Bot_Thread_Send bot3 = new Bot_Thread_Send("Jim","add Moto Hi",1000);

                bot1.start();
                bot2.start();
                bot3.start();
            }

            if(message_on)
            {
                Bot_Thread_Send bot4 = new Bot_Thread_Send("Jack","send Sorin Hello",1000);
                Bot_Thread_Send bot5 = new Bot_Thread_Send("John","send Sorin Yello",1000);
                Bot_Thread_Send bot6 = new Bot_Thread_Send("Jim","send Sorin Hi",1000);

                bot4.start();
                bot5.start();
                bot6.start();
            }
        }

        //for me in terminal
        if(my_terminal_on)
        {
            Scanner scanner = new Scanner(System.in);
            String input;
            String[] input_args;

            do {
                System.out.print("Enter text (press Enter to exit): ");
                input = scanner.nextLine();
                System.out.println("You entered: " + input);
                input_args = input.split(" ");
                Send.send_to("Vivaldo Pasquale Constantin Tiberius Marius al 3-lea", input_args);
            } while (!input.isEmpty());

            scanner.close();
        }


    }
}
