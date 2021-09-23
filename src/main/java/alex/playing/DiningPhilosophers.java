package alex.playing;

import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophers {
    private class Seat {
        public boolean lFork = true;
        public boolean rFork = true;
    }

    private java.util.concurrent.locks.Lock lock = new ReentrantLock();

    private final Seat[] seats = new Seat[5];

    public DiningPhilosophers() {
        for (int i = 0; i < 5; i++) {
            seats[i] = new Seat();
        }
    }

    // call the run() method of any runnable to execute its code
    public void wantsToEat(int philosopher,
                           Runnable pickLeftFork,
                           Runnable pickRightFork,
                           Runnable eat,
                           Runnable putLeftFork,
                           Runnable putRightFork) throws InterruptedException {
        boolean done = false;

        while (!done) {
            this.lock.lock();

            if (seats[philosopher].lFork && seats[philosopher].rFork) {
                locker(philosopher, false);

                this.lock.unlock();

                pickLeftFork.run();
                pickRightFork.run();
            } else {
                this.lock.unlock();

                Thread.sleep(500);
                continue;
            }

            eat.run();
            Thread.sleep(200);

            putLeftFork.run();
            putRightFork.run();

            this.lock.lock();

            locker(philosopher, true);

            this.lock.unlock();

            done = true;
        }
    }


    private void locker(int user, boolean status) {
        seats[user].lFork = status;
        seats[user].rFork = status;

        int[] neighbors = getNeighbors(user);

        seats[neighbors[0]].lFork = status;
        seats[neighbors[1]].rFork = status;
    }

    private int[] getNeighbors(int id) {
        return new int[]{
                id == 0 ? 4 : id - 1,
                id == 4 ? 0 : id + 1
        };
    }

    public static void main(String[] args) {
        Runnable pickLeftFork = () -> System.out.println(Thread.currentThread().getName() + " pickLeftFork is running");
        Runnable pickRightFork = () -> System.out.println(Thread.currentThread().getName() + " pickRightFork is running");
        Runnable eat = () -> System.out.println(Thread.currentThread().getName() + " eat is running");
        Runnable putLeftFork = () -> System.out.println(Thread.currentThread().getName() + " putLeftFork is running");
        Runnable putRightFork = () -> System.out.println(Thread.currentThread().getName() + " putRightFork is running");


        DiningPhilosophers diningPhilosophers = new DiningPhilosophers();

        Runnable phil1 = () -> {
            try {
                diningPhilosophers.wantsToEat(0, pickLeftFork, pickRightFork, eat, putLeftFork, putRightFork);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Runnable phil2 = () -> {
            try {
                diningPhilosophers.wantsToEat(1, pickLeftFork, pickRightFork, eat, putLeftFork, putRightFork);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Runnable phil3 = () -> {
            try {
                diningPhilosophers.wantsToEat(2, pickLeftFork, pickRightFork, eat, putLeftFork, putRightFork);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Runnable phil4 = () -> {
            try {
                diningPhilosophers.wantsToEat(3, pickLeftFork, pickRightFork, eat, putLeftFork, putRightFork);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Runnable phil5 = () -> {
            try {
                diningPhilosophers.wantsToEat(4, pickLeftFork, pickRightFork, eat, putLeftFork, putRightFork);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };


        for (int i = 0; i < 1; i++) {
            new Thread(phil1).start();
            new Thread(phil2).start();
            new Thread(phil3).start();
            new Thread(phil4).start();
            new Thread(phil5).start();
        }

    }

}
