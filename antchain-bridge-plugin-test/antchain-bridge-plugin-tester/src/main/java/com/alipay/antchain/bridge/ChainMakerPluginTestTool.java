package com.alipay.antchain.bridge;

import com.alipay.antchain.bridge.abstarct.IPluginTestTool;
import com.alipay.antchain.bridge.core.*;
import com.alipay.antchain.bridge.exception.PluginTestToolException;
import com.alipay.antchain.bridge.testers.ChainMakerTester;
import com.alipay.antchain.bridge.commons.bbc.AbstractBBCContext;
import com.alipay.antchain.bridge.plugins.spi.bbc.AbstractBBCService;

public class ChainMakerPluginTestTool implements IPluginTestTool {

    AbstractBBCContext inContext;
    AbstractBBCService bbcService;

    ChainMakerTester tester;

    public ChainMakerPluginTestTool(AbstractBBCContext _context, AbstractBBCService _service) {
        inContext = _context;
        bbcService = _service;
        tester = new ChainMakerTester(bbcService);
    }


    @Override
    public void startupTest() throws PluginTestToolException {
        StartUpTest.run(inContext, bbcService);
    }

    @Override
    public void shutdownTest() throws PluginTestToolException {
        ShutDownTest.run(inContext, bbcService);
    }

    @Override
    public void getContextTest() throws PluginTestToolException {
        GetContextTest.run(inContext, bbcService);
    }

    @Override
    public void setupAmContractTest() throws PluginTestToolException {
        SetupAuthMessageContractTest.run(inContext, bbcService);
    }

    @Override
    public void setupSdpContractTest() throws PluginTestToolException {
        SetupSDPMessageContractTest.run(inContext, bbcService);
    }

    @Override
    public void setProtocolTest() throws PluginTestToolException {
        StartUpTest.runBefore(inContext, bbcService);
        SetProtocolTest.run(bbcService, tester);
    }

    @Override
    public void querySdpMessageSeqTest() throws PluginTestToolException {
        StartUpTest.runBefore(inContext, bbcService);
        QuerySDPMessageSeqTest.run(bbcService);
    }

    @Override
    public void setAmContractAndLocalDomainTest() throws PluginTestToolException {
        StartUpTest.runBefore(inContext, bbcService);
        SetAMContractAndLocalDomainTest.run(bbcService, tester);
    }

    @Override
    public void readCrossChainMessageReceiptTest() throws PluginTestToolException {
        StartUpTest.runBefore(inContext, bbcService);
        ReadCrossChainMessageReceiptTest.run(bbcService, tester);
    }

    @Override
    public void readCrossChainMessageByHeightTest() throws PluginTestToolException {
        StartUpTest.runBefore(inContext, bbcService);
        ReadCrossChainMessageByHeightTest.run(bbcService, tester);
    }

    @Override
    public void relayAuthMessageTest() throws PluginTestToolException, InterruptedException {
        StartUpTest.runBefore(inContext, bbcService);
        RelayAuthMessageTest.run(bbcService, tester);
    }
}