package com.alipay.antchain.bridge;

import com.alipay.antchain.bridge.plugintestrunner.PluginTestRunner;
import com.alipay.antchain.bridge.plugintestrunner.util.LogLevel;
import com.alipay.antchain.bridge.plugintestrunner.util.PTRLogger;
import picocli.CommandLine;
import picocli.CommandLine.ParentCommand;

@CommandLine.Command(name = "run-case", mixinStandardHelpOptions = true,description = "Run TestCase.")
public class TestCaseCmd implements Runnable {

    @ParentCommand
    private App parentCommand;

    @Override
    public void run() {
        PluginTestRunner runner = parentCommand.pluginTestRunner;
        PTRLogger logger = runner.getLogger();
        try{
            runner.run();
        } catch (Exception e) {
            logger.rlog(LogLevel.ERROR, "Run TestCase failed: " + e.getMessage());
            if (e.getCause() != null) {
                logger.rlog(LogLevel.ERROR, e.getCause().getMessage());
            }
        }
    }
}
