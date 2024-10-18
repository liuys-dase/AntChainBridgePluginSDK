package com.alipay.antchain.bridge.plugintestrunner;

import com.alipay.antchain.bridge.plugintestrunner.config.ChainConfigManager;
import com.alipay.antchain.bridge.plugintestrunner.exception.TestCaseException;
import com.alipay.antchain.bridge.plugintestrunner.service.ChainManagerService;
import com.alipay.antchain.bridge.plugintestrunner.service.PluginManagerService;
import com.alipay.antchain.bridge.plugintestrunner.service.PluginTestService;
import com.alipay.antchain.bridge.plugintestrunner.testcase.TestCaseContainer;
import com.alipay.antchain.bridge.plugintestrunner.testcase.TestCaseLoader;
import com.alipay.antchain.bridge.plugintestrunner.util.PTRLogger;
import com.alipay.antchain.bridge.plugintestrunner.util.ShellScriptRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class PluginTestRunnerTest {

    PluginTestRunner runner;

    @BeforeEach
    public void init() throws IOException, TestCaseException {
        ChainConfigManager configManager = ChainConfigManager.getInstance();
        PTRLogger logger = PTRLogger.getInstance();
        ShellScriptRunner shellScriptRunner = new ShellScriptRunner(configManager.getProperty("log.directory"),
                configManager.getProperty("script.directory"));
        ChainManagerService chainManagerService = new ChainManagerService(logger, shellScriptRunner);
        PluginManagerService pluginManagerService = new PluginManagerService(logger, configManager.getProperty("plugin.directory"));
        PluginTestService pluginTestService = new PluginTestService(logger, pluginManagerService, chainManagerService);
        TestCaseContainer testCaseContainer = TestCaseLoader.loadTestCasesFromFile(configManager.getProperty("testcase.path"));
        runner = new PluginTestRunner(logger, pluginManagerService, pluginTestService, chainManagerService, testCaseContainer);
    }

    @Test
    public void testRunByTestCase() throws IOException {
        runner.run();
    }
}
