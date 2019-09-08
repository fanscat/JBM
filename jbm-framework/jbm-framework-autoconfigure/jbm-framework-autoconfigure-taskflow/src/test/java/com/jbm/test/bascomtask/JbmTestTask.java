package com.jbm.test.bascomtask;

import com.ebay.bascomtask.annotations.Work;
import com.google.common.collect.Lists;
import com.jbm.test.model.ClassRoom;
import com.jbm.test.model.Student;
import jbm.framework.boot.autoconfigure.taskflow.spring.JbmTaskFlow;
import jbm.framework.boot.autoconfigure.taskflow.useage.JbmEndProcessor;
import jbm.framework.boot.autoconfigure.taskflow.useage.JbmStartProcessor;
import jbm.framework.boot.autoconfigure.taskflow.useage.JbmStepProcessor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class JbmTestTask extends JbmTaskFlow {


    @Override
    public List<Object> sources() {
        List<Object> list = Lists.newArrayList();
        Student student = new Student();
        student.setAge(10);
        student.setName("小明");
        list.add(student);
        Student student2 = new Student();
        student2.setAge(18);
        student2.setName("小王");
        list.add(student2);

        ClassRoom classRoom = new ClassRoom();
        classRoom.setClassName("一班");
        list.add(classRoom);
        ClassRoom classRoom1 = new ClassRoom();
        classRoom1.setClassName("二班");
        list.add(classRoom1);
        return list;
    }


    /**
     * 学习
     */
    @Data
    public class Learn extends JbmStartProcessor<Student> {
        private Student student;

        @Work
        public void execute(Student student) {
            this.student = student;
            log.info("学生[{}]开始学习", student.getName());
        }

    }

    /**
     * 进入考场
     */
    @Data
    class Examination extends JbmStepProcessor<Learn> {

        private Student student;

        @Work
        public void execute(Learn learn) {
            this.student = learn.getStudent();
            if (student.getAge() > 10) {
                log.info("学生[{}]进入考场", student.getName());
            } else {
                log.info("学生[{}]未满18岁不能考试", student.getName());
                this.setStop(true);
            }
        }

    }


    /**
     * 考试
     */
    @Data
    class Test extends JbmStepProcessor<Examination> {

        @Work
        public void execute(Examination examination) {
            log.info("学生[{}]开始考试", examination.getStudent().getName());
        }

    }


    /**
     * 公布考试结果
     */
    @Data
    class End extends JbmEndProcessor<Test> {

        @Work
        public void execute(Test test, Student student) {
            log.info("学生[{}]考试结果", student.getName());
        }

        @Override
        public void execute(Test test) {

        }
    }

}
