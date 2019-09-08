//Server Test Class

import javax.swing.JFrame;

public class ServerTest
{
   public static void main(String[] args)
   {
      Server mayank = new Server();
      mayank.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      mayank.startRunning();
   }
}
