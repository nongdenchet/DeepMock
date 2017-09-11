package com.deepmock;

public class DeepMock {
    public static void inject(Object testObject) throws Exception {
        new DeepMockInjector(testObject).execute();
    }
}
