package org.easyit.learn.asm.example2.target;

@Deprecated
public sealed class MyClass<@NonNull T> extends AnotherClass.MyClass.NestClass permits MyPermitSubClass {

    private @NonNull String hello = "Hello world";

    public void print(String a) {
        System.out.println(hello);
        class C {
            public void print() {
                System.out.println("local A");
            }
        }
        new C().print();
    }

    public static final class MyPermitSubClass extends AnotherClass.MyClass {
        private String hello = "Hello world";

    }

    class A {
        public void print() {
            System.out.println(hello);
        }

        class B {
            public void print() {
                System.out.println(hello);
            }

        }
    }

}
