package dev.mrflyn.securelogin.scheduler;

import java.util.LinkedList;
import java.util.List;

public class SchedulerTaskHandler {

    private long ticks;
    private List<ScheduledTask> tasks;
    private List<ScheduledTask> cancelledTasks;

    public SchedulerTaskHandler() {
        tasks = new LinkedList<>();
        cancelledTasks = new LinkedList<>();
    }

    public void tick() {
        for (ScheduledTask task : tasks) {
            if (task.cancelled) continue;
            if (task.ranTimes == -1) {
                task.runTick = ticks + task.delayTicks;
                task.ranTimes = 0;
            }
            if (ticks == task.runTick) {
                task.run();
                task.runTick = task.repeatTicks + ticks;
                task.ranTimes++;
            }
            if (task.ranTimes == task.maxRunTimes) {
                task.cancelled = true;
                task.onEnd();
            }
            if (task.cancelled) cancelledTasks.add(task);
        }
        if (!cancelledTasks.isEmpty()) {
            tasks.removeAll(cancelledTasks);
            cancelledTasks.clear();
        }
        ticks++;
    }

    public void scheduleTask(ScheduledTask task) {
        tasks.add(task);
    }

}
