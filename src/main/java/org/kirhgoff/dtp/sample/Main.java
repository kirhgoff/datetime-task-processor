package org.kirhgoff.dtp.sample;

import org.apache.commons.lang3.tuple.Pair;
import org.kirhgoff.dtp.impl.ProcessorImpl;
import org.kirhgoff.dtp.api.Processor;

import java.time.LocalDateTime;
import java.util.Queue;
import java.util.concurrent.Callable;

public class Main {
  public static void main(String[] args) throws InterruptedException {

    //Feeder properties
    long fakeFeedPeriod = 100;
    int tasksCount = 3;

    //Processor properties
    int resourcesSize = 4;

    //Main program properties
    int waitBeforeQuit = 1000;

    System.out.println("Starting simulation...");
    System.out.println("======================");
    System.out.println("Starting processor...");
    Processor<String> processor = new ProcessorImpl<>(resourcesSize);
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
