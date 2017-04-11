package com.codeest.geeknews;

/**
 * Created on 2017/4/10.
 *
 * @author WangJian
 */

public class Test {
    @org.junit.Test
    public void Test() {
        Father father = new Children();
        father.run();
        Children children = new Children();
        children.run();
    }
}

abstract class Father implements Run {

}

class Children extends Father implements Run {
    @Override
    public void run() {
        System.out.print("Children");
    }
}

interface Run {
    void run();
}
