package ua.oleksiienko;

public class Main {
    public static void main(String[] args) {
        int arraySize = 10_000_000;
        int threadCount = 10;
        if (args.length >= 1) {
            threadCount = validate(args[0], 32, 10);
        }
        if (args.length >= 2) {
            arraySize = validate(args[1], 100_000_000, 10_000_000);
        }
        int min = new ArrayMinFinder(threadCount, arraySize).findMin();
        System.out.println("Execution completed, array minimum: " + min);
    }

    private static int validate(String arg, int max, int def) {
        try {
            int res = Integer.parseInt(arg);
            if (res <= 0 || res > max) {
                throw new NumberFormatException();
            }
            return res;
        } catch (NumberFormatException e) {
            System.err.printf("Unable to parse argument. Integer [1-%d] expected\n", max);
            return def;
        }
    }
}