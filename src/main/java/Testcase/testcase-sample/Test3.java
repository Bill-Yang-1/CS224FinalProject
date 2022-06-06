package BetterPMD.test;

class Test3 {
    private int counter;
    int id;

    public class InnerClass {
        InnerClass() {
            Test3.this.counter++;
        }

        public int getOuterClassId() {
            return Test3.this.id; 
        }
    }
}
