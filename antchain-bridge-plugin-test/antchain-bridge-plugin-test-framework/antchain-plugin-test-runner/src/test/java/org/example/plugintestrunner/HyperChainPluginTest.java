package org.example.plugintestrunner;


import com.alipay.antchain.bridge.plugins.spi.bbc.IBBCService;
import org.example.plugintestrunner.chainmanager.hyperchain.HyperchainChainManager;
import org.example.plugintestrunner.exception.ChainManagerException;
import org.example.plugintestrunner.exception.PluginManagerException;
import org.example.plugintestrunner.service.ChainManagerService;
import org.example.plugintestrunner.service.PluginManagerService;
import org.example.plugintestrunner.util.PTRLogger;
import org.example.plugintestrunner.util.ShellScriptRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class HyperChainPluginTest {

    // 插件服务
    private PluginManagerService pluginManagerService;
    private ChainManagerService chainManagerService;

    // 插件配置
    private final String PLUGIN_DIRECTORY = "src/test/resources/plugins";
    private final String JAR_PATH = "hyperchain2-bbc-0.1.0-plugin.jar";
    private final String PLUGIN_PRODUCT = "hyperchain2";
    private final String DOMAIN_NAME = "hyperchain2-domain";
    private final String SCRIPT_DIR = "scripts";
    private final String LOG_DIR = "logs";


    private HyperchainChainManager chainManager;
    private IBBCService bbcService;


    @BeforeEach
    public void init() throws PluginManagerException, IOException, ChainManagerException, InterruptedException {
        PTRLogger logger = PTRLogger.getInstance();
        // 加载启动插件
        pluginManagerService = new PluginManagerService(logger, PLUGIN_DIRECTORY);
        pluginManagerService.testLoadPlugin(JAR_PATH);
        pluginManagerService.testStartPlugin(JAR_PATH);
        pluginManagerService.testCreateBBCService(PLUGIN_PRODUCT, DOMAIN_NAME);
        // 创建测试链
        ShellScriptRunner shellScriptRunner = new ShellScriptRunner(LOG_DIR, SCRIPT_DIR);
        chainManagerService = new ChainManagerService(logger, shellScriptRunner);
        chainManagerService.startup(PLUGIN_PRODUCT);
        chainManager = (HyperchainChainManager) chainManagerService.getChainManager(PLUGIN_PRODUCT);
        // 配置 context、bbcService
        bbcService = pluginManagerService.getBBCService(PLUGIN_PRODUCT, DOMAIN_NAME);
    }

    @Test
    public void testHyperChain() {
        System.out.println(chainManager.getConfig());
        bbcService.startup(chainManager.getBBCContext());
        bbcService.setupAuthMessageContract();
        bbcService.setupSDPMessageContract();
        System.out.println("authMessageContract address: " + bbcService.getContext().getAuthMessageContract().getContractAddress());
        System.out.println("sdpMessageContract address: " + bbcService.getContext().getSdpContract().getContractAddress());
    }

    @AfterEach
    public void close() {
        if (chainManagerService != null) {
            chainManagerService.close();
        }
        if (pluginManagerService != null) {
            pluginManagerService.close();
        }
    }
}
