package org.example.plugintestrunner.service;

import lombok.Getter;
import org.example.plugintestrunner.testcase.TestCase;
import org.example.plugintestrunner.util.PTRLogger;

import java.io.IOException;

@Getter
public abstract class AbstractService {
    protected final PTRLogger logger;

    public AbstractService(PTRLogger logger) {
        this.logger = logger;
    }

    public abstract void run(TestCase testCase) throws IOException;

    public abstract void close();
}

