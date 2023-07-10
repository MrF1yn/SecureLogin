package dev.mrflyn.securelogin.scheduler;

public abstract class ScheduledTask {

    public int repeatTicks;
    public int delayTicks;
    public long ranTimes=-1;
    public long maxRunTimes=-1;
    public boolean cancelled;
    public long runTick;

    public ScheduledTask(int repeatTicks, int delayTicks) {
        this.repeatTicks = repeatTicks;
        this.delayTicks = delayTicks;
    }

    public ScheduledTask(int repeatTicks, int delayTicks, long maxRunTimes) {
        this.repeatTicks = repeatTicks;
        this.delayTicks = delayTicks;
        this.maxRunTimes = maxRunTimes;
    }

    //called every given interval
    public abstract void run();
    //called only on its last run when maxRunTimes is specified
    public abstract void onEnd();

    public void cancel(){
        this.cancelled=true;
    }

}
