//Client Test Class

import javax.swing.JFrame;

public class ClientTest
{
   public static void main(String[] args)
   {
      Client jason = new Client("127.0.0.1"); // 127.0.0.1 means local host (the computer that you're at)
      jason.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      jason.startRunning();
   }
}
