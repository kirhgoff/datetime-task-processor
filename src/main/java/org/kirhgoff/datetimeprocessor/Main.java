package org.kirhgoff.datetimeprocessor;

public class Main {
  public static void main(String[] args) throws InterruptedException {
    //Feeder properties
    int taskIncomingDelay = 100;
    int tasksCount = 10;

    //Processor properties
    int resourcesSize = 4;

    //Main program properties
    int waitBeforeQuit = 1000;

    System.out.println("Starting simulation...");
    System.out.println("======================");
    System.out.println("Starting processor...");
    Processor<String> processor = new ProcessorImpl<>();
    processor.start(resourcesSize);

    System.out.println("Starting feeder...");
    //TODO make generic
    Feeder stringFeeder = new Feeder(processor, taskIncomingDelay);
    //TODO join methods and extract Generator
    stringFeeder.generateTasks(tasksCount);
    stringFeeder.run();

    System.out.println("Jobs generated, waiting for simulation to end...");
    //TODO make sure processor is busy until task is finished
    while (processor.isBusy()) {
      Thread.sleep(waitBeforeQuit);
    }

    processor.stop();
    System.out.println("Finished simulation.");
  }
}
