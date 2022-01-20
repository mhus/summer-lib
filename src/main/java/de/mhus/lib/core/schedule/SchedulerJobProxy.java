/**
 * Copyright (C) 2002 Mike Hummel (mh@mhus.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.lib.core.schedule;

import de.mhus.lib.core.ITimerTask;
import de.mhus.lib.core.operation.OperationDescription;
import de.mhus.lib.core.operation.OperationResult;
import de.mhus.lib.core.operation.TaskContext;

public class SchedulerJobProxy extends SchedulerJob implements MutableSchedulerJob {

    private SchedulerJob instance;

    public SchedulerJobProxy(SchedulerJob instance) {
        super(instance.getName(), instance.getTask());
        this.instance = instance;
    }

    @Override
    public void doTick(boolean forced) {
        instance.doTick(forced);
    }

    @Override
    public boolean cancel() {
        super.cancel();
        return instance.cancel();
    }

    @Override
    public boolean isCanceled() {
        return super.isCanceled() || instance.isCanceled();
    }

    @Override
    public boolean isBusy() {
        return instance.isBusy();
    }

    @Override
    public boolean setBusy(Object owner) {
        return instance.setBusy(owner);
    }

    @Override
    public boolean releaseBusy(Object owner) {
        return instance.releaseBusy(owner);
    }

    @Override
    public long scheduledExecutionTime() {
        return instance.scheduledExecutionTime();
    }

    @Override
    public Object getOwner() {
        return instance.getOwner();
    }

    // Enabling this will cause the Scheduler not be able to remove the job out of the running list
    //	@Override
    //	public boolean equals(Object obj) {
    //		return instance.equals(obj);
    //	}

    @Override
    public boolean isDone() {
        return instance.isDone();
    }

    @Override
    public boolean hasAccess(TaskContext context) {
        return instance.hasAccess(context);
    }

    @Override
    public boolean canExecute(TaskContext context) {
        return instance.canExecute(context);
    }

    @Override
    public OperationDescription getDescription() {
        return instance.getDescription();
    }

    @Override
    public ITimerTask getTask() {
        return instance.getTask();
    }

    @Override
    public void doit() throws Exception {
        instance.doit();
    }

    @Override
    public long getNextExecutionTime() {
        return instance.getNextExecutionTime();
    }

    @Override
    public String getName() {
        return instance.getName();
    }

    @Override
    public long getLastExecutionStart() {
        return instance.getLastExecutionStart();
    }

    @Override
    public long getLastExecutionStop() {
        return instance.getLastExecutionStop();
    }

    @Override
    public long getScheduledTime() {
        return instance.getScheduledTime();
    }

    @Override
    public String toString() {
        return instance.toString();
    }

    @Override
    public long getTimeoutInMinutes() {
        return instance.getTimeoutInMinutes();
    }

    @Override
    public void setTimeoutInMinutes(long timeoutInMinutes) {
        instance.setTimeoutInMinutes(timeoutInMinutes);
    }

    @Override
    public void doTimeoutReached() {
        instance.doTimeoutReached();
    }

    @Override
    protected void doCaclulateNextExecution() {
        instance.doCaclulateNextExecution();
    }

    @Override
    public void setCanceled(boolean canceled) {
        instance.setCanceled(canceled);
    }

    //	@Override
    //	protected void doSchedule(Scheduler scheduler) {
    //		super.doSchedule(scheduler);
    //	}

    @Override
    public void doReschedule(Scheduler scheduler, long time) {
        super.doReschedule(scheduler, time);
        //		instance.doReschedule(scheduler, time);
    }

    @Override
    protected void doError(Throwable t) {
        instance.doError(t);
    }

    @Override
    public void setDone(boolean done) {
        instance.setDone(done);
    }

    @Override
    protected boolean isExecutionTimeReached() {
        return instance.isExecutionTimeReached();
    }

    @Override
    protected OperationResult doExecute2(TaskContext context) throws Exception {
        return instance.doExecute2(context);
    }

    @Override
    public void setNextExecutionTime(long nextExecutionTime) {
        instance.setNextExecutionTime(nextExecutionTime);
    }

    @Override
    public void setScheduledTime(long scheduledTime) {
        instance.setScheduledTime(scheduledTime);
    }

    @Override
    public boolean doReconfigure(String config) {
        if (instance instanceof MutableSchedulerJob)
            return ((MutableSchedulerJob) instance).doReconfigure(config);
        return false;
    }

    @Override
    public void setUsername(String username) {
        instance.setUsername(username);
    }
}
