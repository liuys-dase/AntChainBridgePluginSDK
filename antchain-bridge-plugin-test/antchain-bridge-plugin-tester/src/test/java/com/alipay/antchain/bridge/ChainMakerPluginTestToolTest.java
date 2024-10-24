package com.alipay.antchain.bridge;

import com.alipay.antchain.bridge.commons.bbc.AbstractBBCContext;
import com.alipay.antchain.bridge.commons.bbc.DefaultBBCContext;
import com.alipay.antchain.bridge.exception.PluginTestToolException;
import com.alipay.antchain.bridge.plugins.chainmaker.ChainMakerBBCService;
import com.alipay.antchain.bridge.plugins.chainmaker.ChainMakerConfig;
import com.alipay.antchain.bridge.plugins.spi.bbc.AbstractBBCService;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ChainMakerPluginTestToolTest {

    // 测试入参
    AbstractBBCContext inContext;
    AbstractBBCService bbcService;

    // 测试主体结构
    ChainMakerPluginTestTool chainMakerTestTool;
    private static final String SDK_CONFIG_JSON = "chainmaker.json";

    @Before
    public void setUp() throws Exception {
        inContext = new DefaultBBCContext();
        String jsonString = readFileJson(SDK_CONFIG_JSON);
        ChainMakerConfig chainMakerConfig = ChainMakerConfig.fromJsonString(jsonString);
        inContext.setConfForBlockchainClient(chainMakerConfig.toJsonString().getBytes());

        bbcService = new ChainMakerBBCService();  // 使用 ChainMaker 实现的 BBC 服务

        chainMakerTestTool = new ChainMakerPluginTestTool(inContext, bbcService);
    }

    @Test
    public void testStartupTest() throws PluginTestToolException {
        chainMakerTestTool.startupTest();
    }

    @Test
    public void testShutdownTest() throws PluginTestToolException {
        chainMakerTestTool.shutdownTest();
    }

    @Test
    public void testGetContextTest() throws PluginTestToolException {
        chainMakerTestTool.getContextTest();
    }

    @Test
    public void testSetupAmContractTest() throws PluginTestToolException {
        chainMakerTestTool.setupAmContractTest();
    }

    @Test
    public void testSetupSdpContractTest() throws PluginTestToolException {
        chainMakerTestTool.setupSdpContractTest();
    }

    @Test
    public void testSetProtocolTest() throws Exception {
        chainMakerTestTool.setProtocolTest();
    }

    @Test
    public void testQuerySdpMessageSeqTest() throws PluginTestToolException {
        chainMakerTestTool.querySdpMessageSeqTest();
    }

    @Test
    public void testSetAmContractAndLocalDomainTest() throws PluginTestToolException {
        chainMakerTestTool.setAmContractAndLocalDomainTest();
    }

    @Test
    public void testReadCrossChainMessageReceiptTest() throws PluginTestToolException {
        chainMakerTestTool.readCrossChainMessageReceiptTest();
    }

    @Test
    public void testReadCrossChainMessageByHeightTest() throws PluginTestToolException {
        chainMakerTestTool.readCrossChainMessageByHeightTest();
    }

    @Test
    public void testRelayAuthMessageTest() throws PluginTestToolException, InterruptedException {
        chainMakerTestTool.relayAuthMessageTest();
    }

    public static String readFileJson(String fileName) {
        StringBuilder jsonStringBuilder = new StringBuilder();

        try {
            // 使用ClassLoader获取资源文件的输入流
            InputStream inputStream = ChainMakerConfig.class.getClassLoader().getResourceAsStream(fileName);

            // 使用BufferedReader逐行读取文件内容
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                jsonStringBuilder.append(line);
            }

            // 关闭资源
            reader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonStringBuilder.toString();
    }
}