package com.garv.foodApp.foodApp.Scheduler;

import com.garv.foodApp.foodApp.Repository.ScheduleConfigRepository;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import reactor.core.scheduler.Scheduler;

public class DynamicSchedulerService {

    private final ScheduleConfigRepository scheduleConfigRepository;

    private final OrderScheduler orderScheduler;

    private final ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();

//    private SchedulerFuture<?> schedulerFuture;


    public DynamicSchedulerService(ScheduleConfigRepository scheduleConfigRepository, OrderScheduler orderScheduler) {
        this.scheduleConfigRepository = scheduleConfigRepository;
        this.orderScheduler = orderScheduler;
//        this.schedulerFuture = schedulerFuture;
    }




}
