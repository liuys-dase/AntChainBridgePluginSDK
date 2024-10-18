package com.alipay.antchain.bridge.plugintestrunner.service;

import com.alipay.antchain.bridge.commons.core.base.CrossChainDomain;
import com.alipay.antchain.bridge.plugins.manager.exception.AntChainBridgePluginManagerException;

import com.alipay.antchain.bridge.plugins.spi.bbc.IBBCService;
import com.alipay.antchain.bridge.plugintestrunner.exception.PluginManagerException;
import com.alipay.antchain.bridge.plugintestrunner.testcase.TestCase;
import com.alipay.antchain.bridge.plugintestrunner.util.LogLevel;
import com.alipay.antchain.bridge.plugintestrunner.util.PTRLogger;
import lombok.Getter;
import com.alipay.antchain.bridge.plugins.manager.pf4j.Pf4jAntChainBridgePluginManager;
import com.alipay.antchain.bridge.plugintestrunner.exception.PluginManagerException.*;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PluginManagerService extends AbstractService{

    @Getter
    private final String pluginDirectory;
    private final Pf4jAntChainBridgePluginManager manager;

    private TestCase testCase;

    public PluginManagerService(PTRLogger logger, String pluginDirectory) {
        super(logger);
        this.pluginDirectory = pluginDirectory;
        this.manager = new Pf4jAntChainBridgePluginManager(pluginDirectory);
    }

    @Override
    public void run(TestCase testCase_) {
        // 设置 MDC
        logger.putMDC(testCase_.getName());
        try {
            this.testCase = testCase_;
            runTest();
            testCase.setPluginLoadAndStartTestSuccess(summarizeTestResult());
        } catch (PluginManagerException e) {
            logger.rlog(LogLevel.ERROR, "Plugin test for " + testCase.getName() + ": FAILED");
            logger.rlog(LogLevel.ERROR, e.getMessage());
            if (e.getCause() != null) {
                logger.rlog(LogLevel.ERROR, e.getCause().getMessage());
            }
        }

        // 清除 MDC
        logger.clearMDC();
    }

    private boolean summarizeTestResult() {
        int totalTests = 0;
        int successfulTests = 0;
        int failedTests = 0;
        StringBuilder failedTestNames = new StringBuilder();
        boolean isLoadPlugin = testCase.isLoadPlugin();
        boolean isStartPlugin = testCase.isStartPlugin();
        boolean isStartPluginFromStop = testCase.isStartPluginFromStop();
        boolean isStopPlugin = testCase.isStopPlugin();
        boolean isCreateBBCService = testCase.isCreateBBCService();
        if (isLoadPlugin) {
            totalTests++;
            if (testCase.isLoadPluginSuccess()) {
                successfulTests++;
            } else {
                failedTests++;
                failedTestNames.append("LoadPlugin").append(", ");
            }
        }
        if (isStartPlugin) {
            totalTests++;
            if (testCase.isStartPluginSuccess()) {
                successfulTests++;
            } else {
                failedTests++;
                failedTestNames.append("StartPlugin").append(", ");
            }
        }
        if (isStopPlugin) {
            totalTests++;
            if (testCase.isStopPluginSuccess()) {
                successfulTests++;
            } else {
                failedTests++;
                failedTestNames.append("StopPlugin").append(", ");
            }
        }
        if (isStartPluginFromStop) {
            totalTests++;
            if (testCase.isStartPluginFromStopSuccess()) {
                successfulTests++;
            } else {
                failedTests++;
                failedTestNames.append("StartPluginFromStop").append(", ");
            }
        }
        if (isCreateBBCService) {
            totalTests++;
            if (testCase.isCreateBBCServiceSuccess()) {
                successfulTests++;
            } else {
                failedTests++;
                failedTestNames.append("CreateBBCService").append(", ");
            }
        }

        // 去掉最后一个多余的逗号和空格
        if (failedTestNames.length() > 0) {
            failedTestNames.setLength(failedTestNames.length() - 2);
        }

        // 打印测试结果
        logger.rlog(LogLevel.INFO, "Total number of plugin tests: " + totalTests + ", Successful tests: " + successfulTests + ", Failed tests: " + failedTests);

        if (failedTests > 0) {
            logger.rlog(LogLevel.INFO, "Failed plugin tests: " + failedTestNames.toString());
            return false;  // There are failed tests, return false
        }

        return true;  // 所有测试成功，返回 true
    }


    private void runTest() throws PluginManagerException{
        // 获取测试用例的参数
        boolean isLoadPlugin = testCase.isLoadPlugin();
        boolean isStartPlugin = testCase.isStartPlugin();
        boolean isStartPluginFromStop = testCase.isStartPluginFromStop();
        boolean isStopPlugin = testCase.isStopPlugin();
        boolean isCreateBBCService = testCase.isCreateBBCService();
        String productName = testCase.getProduct();
        String jarPath = Paths.get(testCase.getJarPath()).toString();
        String domain = testCase.getDomain();

        // loadPlugin -> startPlugin -> stopPlugin -> startPluginFromStop
        // loadPlugin -> startPlugin -> createBBCService
        if (isLoadPlugin) {
            testLoadPlugin(jarPath);
        }

        if (isStartPlugin) {
            if (!isLoadPlugin) {
                testLoadPlugin(jarPath);
            }
            testStartPlugin(jarPath);
        }

        if (isStopPlugin) {
            if (!isLoadPlugin) {
                testLoadPlugin(jarPath);
            }
            if (!isStartPlugin) {
                testStartPlugin(jarPath);
            }
            testStopPlugin(productName);
        }

        if (isStartPluginFromStop) {
            if (!isLoadPlugin) {
                testLoadPlugin(jarPath);
            }
            if (!isStartPlugin) {
                testStartPlugin(jarPath);
            }
            if (!isStopPlugin) {
                testStopPlugin(productName);
            }
            testStartPluginFromStop(productName);
        }

        if (isCreateBBCService) {
            if (isStartPluginFromStop) {
                testCreateBBCService(productName, domain);
            } else {
                if (isStopPlugin) {
                    testStartPlugin(jarPath);
                    testCreateBBCService(productName, domain);
                } else {
                    if (!isLoadPlugin) {
                        testLoadPlugin(jarPath);
                    }
                    if (!isStartPlugin) {
                        testStartPlugin(jarPath);
                    }
                    testCreateBBCService(productName, domain);
                }
            }
        }
    }

    @Override
    public void close() {

    }

    // 加载特定路径下的插件
    public void testLoadPlugin(String jarPath) throws PluginManagerException {
        Path path = resolveJarPath(jarPath);
        logger.plog(LogLevel.INFO, "Loading plugin from " + path);
        try {
            manager.loadPlugin(path);
            if (testCase != null) {
                testCase.setLoadPluginSuccess(true);
            }
        } catch (AntChainBridgePluginManagerException e){
            if (testCase != null) {
                testCase.setLoadPluginSuccess(false);
            }
            throw new PluginLoadException("Failed to load plugin " + path, e);
        }
        logger.plog(LogLevel.INFO, "Plugin " + path + " has been successfully loaded");
    }

    // 启动特定路径下的插件
    public void testStartPlugin(String jarPath) throws PluginManagerException {
        Path path = resolveJarPath(jarPath);
        logger.plog(LogLevel.INFO, "Starting plugin from " + path);
        try {
            manager.startPlugin(path);
            if (testCase != null) {
                testCase.setStartPluginSuccess(true);
            }
            logger.plog(LogLevel.INFO, "Plugin " + path + " has been successfully started");
        } catch (AntChainBridgePluginManagerException e) {
            if (testCase != null) {
                testCase.setStartPluginSuccess(false);
            }
            throw new PluginStartException("Failed to start plugin: " + path, e);
        }
    }
    
    // 关闭特定路径下的插件
    public void testStopPlugin(String pluginProduct) throws PluginManagerException {
        try {
            manager.stopPlugin(pluginProduct);
            if (testCase != null) {
                testCase.setStopPluginSuccess(true);
            }
            logger.plog(LogLevel.INFO, "Plugin " + pluginProduct + " has been successfully stopped");
        } catch (Exception e) {
            if (testCase != null) {
                testCase.setStopPluginSuccess(false);
            }
            throw new PluginStopException("Failed to stop plugin " + pluginProduct, e);
        }
    }

    // 启动已经关闭的插件
    public void testStartPluginFromStop(String pluginProduct) throws PluginManagerException {
        try {
            manager.startPluginFromStop(pluginProduct);
            if (testCase != null) {
                testCase.setStartPluginFromStopSuccess(true);
            }
            logger.plog(LogLevel.INFO, "Plugin " + pluginProduct + " has been successfully started from stop");
        } catch (Exception e) {
            if (testCase != null) {
                testCase.setStartPluginFromStopSuccess(false);
            }
            throw new PluginStartException("Failed to start plugin " + pluginProduct + " from stop", e);
        }
    }

    // 重新加载插件
    public void testReloadPlugin(String jarPath, String pluginProduct) throws PluginManagerException {
        Path path = resolveJarPath(jarPath);
        try {
            manager.reloadPlugin(pluginProduct, path);
        } catch (Exception e) {
            throw new PluginLoadException("Failed to reload plugin " + path, e);
        }
        logger.plog(LogLevel.INFO, "Plugin " + path + " has been successfully reloaded");
    }

    // 检查插件是否存在
    public boolean hasPlugin(String pluginProduct){
        return manager.hasPlugin(pluginProduct);
    }

    // 创建 BBC 服务
    public void testCreateBBCService(String pluginProduct, String domainName) throws PluginManagerException {
        try {
            CrossChainDomain domain = new CrossChainDomain(domainName);
            manager.createBBCService(pluginProduct, domain, logger.getProcessLogger());
            if (testCase != null) {
                testCase.setCreateBBCServiceSuccess(true);
            }
            logger.plog(LogLevel.INFO, "BBC service for " + pluginProduct + " has been successfully created");
        } catch (Exception e) {
            if (testCase != null) {
                testCase.setCreateBBCServiceSuccess(false);
            }
            throw new BBCServiceCreateException("Failed to create BBC service", e);
        }
    }

    public boolean hasDomain(String domainName) {
        return manager.hasDomain(new CrossChainDomain(domainName));
    }

    public boolean hasBBCService(String pluginProduct, String domainName) {
        return manager.getBBCService(pluginProduct, new CrossChainDomain(domainName)) != null;
    }

    public IBBCService getBBCService(String pluginProduct, String domainName) {
        return manager.getBBCService(pluginProduct, new CrossChainDomain(domainName));
    }

    private Path resolveJarPath(String jarPath) {
        Path path = Paths.get(jarPath);
        if (path.getNameCount() == 1) {
            path = Paths.get(pluginDirectory, jarPath).toAbsolutePath();
        } else {
            path = path.toAbsolutePath();
        }
        return path;
    }
}