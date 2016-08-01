package org.kirhgoff.datetimeprocessor;

import java.util.concurrent.CountDownLatch;

public class Main {
  public static void main(String[] args) throws InterruptedException {
    int taskIncomingDelay = 100;
    int tasksCount = 10;
    int waitBeforeQuit = 1000;

    System.out.println("Starting simulation...");
    System.out.println("======================");
    System.out.println("Starting processor...");
    Processor<String> processor = new ProcessorImpl<>();
    processor.start();

    System.out.println("Starting feeder...");
    Feeder<String> stringFeeder = new Feeder<>(processor, taskIncomingDelay);
    stringFeeder.generateTasks(tasksCount);
    CountDownLatch latch = stringFeeder.start();

    System.out.println("Waiting for simulation end...");
    latch.await();
    //TODO make sure processor is busy until task is finished
    while (processor.isBusy()) {
      Thread.sleep(waitBeforeQuit);
    }

    processor.stop();
    System.out.println("Finished simulation.");
  }
}
