package jbm.framework.boot.autoconfigure.taskflow.useage;

/************************************************************************
 Copyright 2018 eBay Inc.
 Author/Developer: Brendan McCarthy

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 https://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 **************************************************************************/


import cn.hutool.core.collection.CollectionUtil;
import com.ebay.bascomtask.main.ITask;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Provides test tasks the ability to verify actual invocations against expected
 * invocations.
 *
 * @author bremccarthy
 */
@Slf4j
@Data
public abstract class PathTask {
    private final List<List<PathTask.Arg>> exp = new ArrayList<>();
    private final List<List<PathTask.Arg>> got = new ArrayList<>();
    protected ITask taskInstance;
    private int sleepFor = 0;
    private long timestamp = 0;

    static List<PathTask.Arg> toArgs(PathTask... tasks) {
        List<PathTask.Arg> args = new ArrayList<>();
        for (PathTask next : Arrays.asList(tasks)) {
            args.add(next.asArg());
        }
        return args;
    }

    static <T> String css(List<T> os, Class<T> cls) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < os.size(); i++) {
            Object next = os.get(i);
            if (i > 0) {
                sb.append(',');
            }
            sb.append(next.toString());
        }
        return sb.toString();
    }

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ;
    }

    @Override
    public String toString() {
        return getName() + ':' + exp.size() + ':' + got.size();
    }

    String getName() {
        // taskInstance might not be set in nested @Work cases
        if (taskInstance == null) {
            return "???";
        }
        return taskInstance.getName();
    }

    PathTask name(String name) {
        taskInstance.name(name);
        return this;
    }

    PathTask noWait() {
        taskInstance.noWait();
        return this;
    }

    PathTask multiMethodOk() {
        taskInstance.multiMethodOk();
        return this;
    }

    PathTask fork() {
        taskInstance.fork();
        return this;
    }

    PathTask before(Object x) {
        taskInstance.before(x);
        return this;
    }

    PathTask after(Object x) {
        taskInstance.after(x);
        return this;
    }

    PathTask provides(Class<?> taskClass) {
        taskInstance.provides(taskClass);
        return this;
    }

    /**
     * Sleeps for given number of ms when {@link #got} is called, in order to
     * simulate taskInstance-specific delay.
     *
     * @param sleepFor ms
     * @return
     */
    PathTask sleepFor(int sleepFor) {
        this.sleepFor = sleepFor;
        return this;
    }

    PathTask.Arg asArg() {
        return new SingleArg(this);
    }

    /**
     * Sets an expectation of the given actual parameter list, which should
     * match the formal parameter list of the method signature. This method
     * should be invoked once for each call expected.
     *
     * @param tasks
     * @return
     */
    public PathTask exp(PathTask... tasks) {
        if (tasks.length > 0) {
            List<PathTask.Arg> args = toArgs(tasks);
            // List<PathTask.Arg> args = Arrays.asList(tasks).stream().map(t ->
            // t.asArg()).collect(Collectors.toList());
            exp.add(args);
        }
        return this;
    }

    /**
     * Did both tasks execute, and did this one execute after the given one?
     *
     * @param other
     * @return
     */
    public boolean followed(PathTask other) {
        if (this.timestamp == 0 || other.timestamp == 0) {
            throw new RuntimeException("Timestamps unset this=" + this.timestamp + ", other=" + other.timestamp);
        }
        return this.timestamp > other.timestamp;
    }

    public synchronized PathTask got(PathTask... tasks) {
        timestamp = System.nanoTime();

        if (tasks.length > 0) {
            List<PathTask.Arg> args = toArgs(tasks);
            log.info("tasks runing ", CollectionUtil.getFirst(args).getClass());
            got.add(args);
        }
        if (sleepFor > 0) {
            sleep(sleepFor);
        }

        return this;
    }

    public String fmt(List<PathTask.Arg> args) {
        return "(" + css(args, PathTask.Arg.class) + ")";
        // return "(" +
        // args.stream().map(Object::toString).collect(Collectors.joining(","))
        // + ")";
    }

    /**
     * Raises JUnit failures if actual results did not match expected results.
     */
    public synchronized void check() {
        List<String> bad = new ArrayList<>();
        for (List<PathTask.Arg> next : exp) {
            if (!got.contains(next)) {
                bad.add("missing" + next);
            }
        }
        for (List<PathTask.Arg> next : got) {
            if (!exp.contains(next)) {
                bad.add("unexp " + next);
            }
        }
        if (bad.size() > 0) {
            log.error(taskInstance.getName() + " " + css(bad, String.class));
        }
        // This might happen when task is called extra times with same
        // argument(s)
        int expSize = exp.size();
        int gotSize = got.size();
        if (expSize != gotSize) {
            log.error(taskInstance.getName() + " contents matched but exp.length " + expSize + " != got.length " + gotSize);
        }
    }

    static class Arg {

    }

    static class SingleArg extends PathTask.Arg {
        final PathTask arg;

        SingleArg(PathTask arg) {
            this.arg = arg;
        }

        @Override
        public int hashCode() {
            return Objects.hash(arg);
        }

        @Override
        public boolean equals(Object x) {
            if (this == x)
                return true;
            if (x instanceof PathTask.SingleArg) {
                PathTask.SingleArg that = (PathTask.SingleArg) x;
                return this.arg == that.arg;
            }
            return false;
        }

        @Override
        public String toString() {
            return arg.toString();
        }
    }

    public static class ListArg extends PathTask.Arg {
        final List<PathTask> args;

        ListArg(List<PathTask> args) {
            this.args = args;
        }

        @Override
        public int hashCode() {
            return Objects.hash(args);
        }

        @Override
        public boolean equals(Object x) {
            if (this == x)
                return true;
            if (x instanceof PathTask.ListArg) {
                PathTask.ListArg that = (PathTask.ListArg) x;
                if (this.args.size() != that.args.size())
                    return false;
                if (!this.args.containsAll(that.args))
                    return false;
                if (!that.args.containsAll(this.args))
                    return false;
                return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return css(args, PathTask.class);
            // return "{" +
            // args.stream().map(Object::toString).collect(Collectors.joining(","))
            // + "}";
        }
    }

    /**
     * Temporary holder of tasklist so we can create a ListArg. Only extends
     * PathTask for convenience -- it would be probably be cleaner to have this
     * and PathTask share a common base but this works well enough for test
     * purposes.
     */
    public static class ListPath extends PathTask {
        final List<PathTask> tasks;

        public ListPath(PathTask... args) {
            this.tasks = Arrays.asList(args);
        }

        PathTask.Arg asArg() {
            return new ListArg(tasks);
        }
    }
}