package com.alipay.antchain.bridge;

import com.alipay.antchain.bridge.plugintestrunner.PluginTestRunner;
import com.alipay.antchain.bridge.plugintestrunner.util.LogLevel;
import com.alipay.antchain.bridge.plugintestrunner.util.PTRLogger;
import picocli.CommandLine;
import picocli.CommandLine.ParentCommand;

import java.util.List;

@CommandLine.Command(name = "run-case", mixinStandardHelpOptions = true,description = "Run TestCase.")
public class TestCaseCmd implements Runnable {

    @ParentCommand
    private App parentCommand;

    // 指定链的类型
    @CommandLine.Option(names = {"-p", "--path"}, description = "Path to testcase.json", required = false)
    private String path;


    @Override
    public void run() {
        PluginTestRunner runner = parentCommand.pluginTestRunner;
        PTRLogger logger = runner.getLogger();
        try{
            if (path == null ) {
                runner.run();
            } else {
                runner.run(path);
            }
        } catch (Exception e) {
            logger.rlog(LogLevel.ERROR, "Run TestCase failed: " + e.getMessage());
            if (e.getCause() != null) {
                logger.rlog(LogLevel.ERROR, e.getCause().getMessage());
            }
        }
    }
}
