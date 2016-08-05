package org.kirhgoff.dtp.sample;

import org.kirhgoff.dtp.api.Processor;
import org.kirhgoff.dtp.impl.ProcessorImpl;

public class Main {
  public static void main(String[] args) throws InterruptedException {

    //Feeder properties
    long fakeFeedPeriod = 100;
    int tasksCount = 10;

    //Processor properties
    int resourcesSize = 4;
    int precisionMillis = 100;

    //Main program properties
    int waitBeforeQuit = 500;

    System.out.println("Starting simulation...");
    System.out.println("======================");
    System.out.println("Starting processor...");
    Processor<String> processor = new ProcessorImpl<>(resourcesSize, precisionMillis);
    processor.start();

    Thread.sleep(1000); //TODO just to check
    
    System.out.println("Starting feeder...");
    Feeder stringFeeder = new Feeder(fakeFeedPeriod);
    stringFeeder.feed(processor,
        new FakeTasksGenerator().newTasks(tasksCount, fakeFeedPeriod)
    );

    System.out.println("Jobs generated, waiting for simulation to end...");
    //TODO make sure processor is busy until task is finished
    while (processor.isBusy()) {
      Thread.sleep(waitBeforeQuit);
    }

    processor.stop();
    System.out.println("Finished simulation.");
  }
}
