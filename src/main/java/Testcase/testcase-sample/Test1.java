package BetterPMD.test;

public class Test1 {
    public int main(String[] args) {
        int x = 10;
        int y = 10;
        int z;
        if (x > y) {
            z = 100;
        } else if (x == y) {
            z = 150;
        } else {
            z = 200; // unreachable branch
            int b = 30; // unreachable branch
            System.out.println("Hello"); // unreachable branch
        }
        return z;
    }
}
