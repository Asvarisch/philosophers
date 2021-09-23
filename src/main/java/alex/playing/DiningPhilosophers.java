package alex.playing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophers {
    private class Philosopher {
        public boolean lFork = true;
        public boolean rFork = true;
    }

    private java.util.concurrent.locks.Lock lock = new ReentrantLock();

    // create fixed thread pool
    private static ExecutorService executor = Executors.newFixedThreadPool(5);

    private final Philosopher[] philosophers = new Philosopher[5];

    public DiningPhilosophers() {
        for (int i = 0; i < 5; i++) {
            philosophers[i] = new Philosopher();
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

            if (philosophers[philosopher].lFork && philosophers[philosopher].rFork) {
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
        philosophers[user].lFork = status;
        philosophers[user].rFork = status;

        int[] neighbors = getNeighbors(user);

        philosophers[neighbors[0]].lFork = status;
        philosophers[neighbors[1]].rFork = status;
    }

    private int[] getNeighbors(int id) {
        return new int[]{
                id == 0 ? 4 : id - 1,
                id == 4 ? 0 : id + 1
        };
    }

    public static void main(String[] args) {
        // create runnable task for each action
        Runnable pickLeftFork = () -> System.out.println("Philosopher-" + (Thread.currentThread().getId() - 10) + " pickLeftFork");
        Runnable pickRightFork = () -> System.out.println("Philosopher-" + (Thread.currentThread().getId() - 10) + " pickRightFork");
        Runnable eat = () -> System.out.println("Philosopher-" + (Thread.currentThread().getId() - 10) + " eat");
        Runnable putLeftFork = () -> System.out.println("Philosopher-" + (Thread.currentThread().getId() - 10) + " putLeftFork");
        Runnable putRightFork = () -> System.out.println("Philosopher-" + (Thread.currentThread().getId() - 10) + " putRightFork");

        // create philosophers
        DiningPhilosophers diningPhilosophers = new DiningPhilosophers();

        List<Runnable> philosopherTasks = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            int finalI = i;
            Runnable task = () -> {
                try {
                    diningPhilosophers.wantsToEat(finalI, pickLeftFork, pickRightFork, eat, putLeftFork, putRightFork);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };
            philosopherTasks.add(task);
        }

        // how many times each philosopher will eat
        int n = 2;

        // run all philosopher Threads
        startEating(n, philosopherTasks);
    }

    private static void startEating(int n, List<Runnable> philosopherTasks) {
        for (int i = 0; i < n; i++) {
            //philosopherTasks.forEach(ph -> new Thread(ph).start());
            // do the same thing as new Thread, but better, since we control number of Threads created
            philosopherTasks.forEach(ph -> executor.execute(ph));
        }
        executor.shutdown();
    }

}
