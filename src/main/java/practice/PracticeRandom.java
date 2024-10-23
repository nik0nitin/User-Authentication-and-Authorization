package practice;

import java.util.Random;

public class PracticeRandom {
    public static void main(String[] args) {
        Random random = new Random();
        int x = random.nextInt(9999)+1;
        System.out.println(x);

        double y = random.nextDouble();
        System.out.println(y * 1000);
    }
}
