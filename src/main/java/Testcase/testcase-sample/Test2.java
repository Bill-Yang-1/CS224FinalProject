package BetterPMD.test;

import java.io.*;

public class Test2 {
    private byte[] b;
    private int length;

    Test2() {
        length = 40;
        b = new byte[length];
    }

    public void bar() {
        try {
            FileInputStream x = new FileInputStream("z");
            x.read(b, 0, length);
            x.close();
        } catch (Exception e) {
            System.out.println("Oopsie");
        }
        for (int i = 1; i <= length; i++) {
            if (Integer.toString(50) == Byte.toString(b[i]))
                System.out.print(b[i] + " ");
            if (Integer.toString(50).equals(Byte.toString(b[i])))
                System.out.print(b[i] + " ");
        }
    }
}
