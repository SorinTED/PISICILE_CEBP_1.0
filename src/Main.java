import java.util.Scanner;

/*
 *                                                  IMPORTANT
 *
 *  send message syntax: "send <username> <content>"
 *      ex: send Sorin Hello my dear friend
 *
 *  add post to topic: "add to <topic_name> for <post_timeout>h <content>"
 *                                          or
 *                     "add to <topic_name> for <post_timeout>H <content>"
 *              <post_timeout> - is a value (double or integer) which represents
 *                                  after how many hours will be the post deleted (number+h/H)
 *      ex: add to Moto for 3h I am very excited about my new motorcycle
 *
 */

/*
    TO DO:
        - topic header?

        -add events
            + maximum number of topics/messages in queues
 */

//read from Radu

public class Main {

    public static void main(String[] args) {
        boolean bots_on = true;
            boolean message_on = false;
            boolean topic_on = true;
        boolean my_terminal_on = false;

	    Receiver_queue Sorin_messages = new Receiver_queue("Sorin");
        Receiver_queue Radu_messages = new Receiver_queue("Radu");

        Topic moto = new Topic("Moto");
        Topic university = new Topic("University");
        Topic job = new Topic("Job");

        if(bots_on)
        {
            if(topic_on)
            {
                int time_between_topics_messages = 3000;

                Bot_Thread_Send bot1 = new Bot_Thread_Send("Jack","add to Moto for 0.0013h Hello",time_between_topics_messages);
                Bot_Thread_Send bot2 = new Bot_Thread_Send("John","add to Moto for 0.0007h Yello",time_between_topics_messages);
                Bot_Thread_Send bot3 = new Bot_Thread_Send("Jim","add to Moto for 24H Hi",time_between_topics_messages);

                bot1.start();
                bot2.start();
                bot3.start();
            }

            if(message_on)
            {
                int time_between_messages = 1000;

                Bot_Thread_Send bot4 = new Bot_Thread_Send("Jack","send Sorin Hello",time_between_messages);
                Bot_Thread_Send bot5 = new Bot_Thread_Send("John","send Sorin Yello",time_between_messages);
                Bot_Thread_Send bot6 = new Bot_Thread_Send("Jim","send Sorin Hi",time_between_messages);

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
