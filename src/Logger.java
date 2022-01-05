public class Logger {
    void log(String logString) {
        System.out.println("@ " + this.getClass().getName() + ": " + logString);
    }
}
