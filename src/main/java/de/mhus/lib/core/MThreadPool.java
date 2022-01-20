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
package de.mhus.lib.core;

import java.util.function.Consumer;

import org.apache.shiro.subject.Subject;

import de.mhus.lib.core.aaa.Aaa;
import de.mhus.lib.core.aaa.SubjectEnvironment;
import de.mhus.lib.core.logging.ITracer;
import de.mhus.lib.core.logging.Log;
import de.mhus.lib.core.util.MObject;
import io.opentracing.Scope;
import io.opentracing.Span;

/**
 * @author hummel
 *     <p>To change the template for this generated type comment go to
 *     Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MThreadPool extends MObject implements Runnable {

    protected static Log log = Log.getLog(MThreadPool.class);

    protected Runnable task = this;
    protected String name = "";
    protected ThreadContainer tc = null;

    protected final Subject subject = Aaa.getSubject();

    public MThreadPool() {}

    public MThreadPool(String _name) {
        name = _name;
    }

    public MThreadPool(Runnable _task) {
        task = _task;
    }

    public MThreadPool(Runnable _task, String _name) {
        task = _task;
        name = _name;
    }

    protected Runnable getTask() {
        return task;
    }

    @Override
    public void run() {}

    public MThreadPool start() {
        tc = M.l(MThreadPoolManager.class).start(this, name);
        return this;
    }

    public void setName(String _name) {
        if (tc != null) tc.setName(_name);
    }

    public String getName() {
        if (tc != null) return tc.getName();
        return "";
    }

    public void setPriority(int _p) {
        if (tc != null) tc.setPriority(_p);
    }

    public int getPriority() {
        if (tc != null) return tc.getPriority();
        return 0;
    }

    @SuppressWarnings("deprecation")
    public void stop() {
        if (tc == null) return;
        tc.stop();
    }

    //	@SuppressWarnings("deprecation")
    //	public void throwException(Throwable throwable) {
    //		if (tc == null)
    //			return;
    //		tc.stop(throwable);
    //	}

    public void interupt() {
        if (tc == null) return;
        tc.interrupt();
    }

    /**
     * Sleeps _millisec milliseconds. On Error (e.g. a break), it prints a stacktrace dump.
     *
     * @param _millisec
     */
    public static void sleep(long _millisec) {
        try {
            Thread.sleep(_millisec);
        } catch (InterruptedException e) {
            log.i(e);
        }
    }

    private void taskBegin() {
        setName(
                name
                        + '['
                        + Thread.currentThread().getId()
                        + "] "
                        + getTask().getClass().getName());

        MThread.cleanup();
    }

    private void taskFinish() {
        setName(name + " sleeping");
        tc = null;
        MThread.cleanup();
    }

    public boolean isAlive() {
        return tc != null;
    }

    public void taskError(Throwable t) {}

    protected static class ThreadContainer extends Thread {

        private volatile boolean running = true;
        private volatile MThreadPool task = null;
        private volatile String name;
        private volatile long sleepStart;
        private volatile Span span;

        public ThreadContainer(ThreadGroup group, String pName) {
            super(group, pName);
            name = pName;
            setName(name + " sleeping");
        }

        public synchronized boolean newWork(MThreadPool _task) {
            synchronized (this) {
                if (task != null || !running) return false;
                // remember next task
                task = _task;
                // remember trail log
                span = ITracer.get().current();
                notify();
            }
            return true;
        }

        public boolean isWorking() {
            synchronized (this) {
                return task != null;
            }
        }

        public boolean isRunning() {
            return running;
        }

        public boolean stopRunning() {
            synchronized (this) {
                running = false;
                if (task != null) return false;
                notifyAll();
            }
            return true;
        }

        public long getSleepTime() {
            if (task != null) return 0;
            return System.currentTimeMillis() - sleepStart;
        }

        @Override
        public void run() {

            while (running) {

                sleepStart = System.currentTimeMillis();
                while (task == null && running) {
                    // AThread.sleep( 100 );
                    try {
                        synchronized (this) {
                            this.wait();
                        }
                    } catch (InterruptedException e) {
                    }
                }

                MThreadPool currentTask = task;
                if (currentTask != null) {

                    // run ....
                    currentTask.taskBegin();
                    try (SubjectEnvironment env =
                            Aaa.asSubjectWithoutTracing(currentTask.subject)) {
                        // set trail log if set
                        try (Scope scope = ITracer.get().enter(span, name)) {
                            try {
                                log.t("Enter Thread Task");
                                currentTask.getTask().run();
                                log.t("Leave Thread Task");
                            } catch (Throwable t) {
                                try {
                                    log.i("Thread Task Error", getName(), t);
                                    currentTask.taskError(t);
                                } catch (Throwable t2) {
                                    log.i("Thread Task Finish Error", getName(), t2);
                                }
                            }
                            log.t("###: LEAVE THREAD");
                        }
                    }
                    currentTask.taskFinish();
                    currentTask = null;
                }
                task = null; // don't need a sync
            }
        }
    }

    public static void asynchron(Runnable task) {
        new MThreadPool(task).start();
    }

    public static void run(Runnable task) {
        new MThreadPool(
                        new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    task.run();
                                } catch (Throwable t) {
                                    t.printStackTrace();
                                }
                            }
                        })
                .start();
    }

    public static void run(Consumer<Thread> consumer) {
        new MThreadPool(
                        new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    consumer.accept(Thread.currentThread());
                                } catch (Throwable t) {
                                    t.printStackTrace();
                                }
                            }
                        })
                .start();
    }
}
