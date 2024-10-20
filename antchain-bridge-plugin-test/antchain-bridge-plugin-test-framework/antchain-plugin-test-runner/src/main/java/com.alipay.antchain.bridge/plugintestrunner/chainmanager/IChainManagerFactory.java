package com.alipay.antchain.bridge.plugintestrunner.chainmanager;


import com.alipay.antchain.bridge.plugintestrunner.chainmanager.chainmaker.ChainMakerChainManager;
import com.alipay.antchain.bridge.plugintestrunner.chainmanager.eos.EosChainManager;
import com.alipay.antchain.bridge.plugintestrunner.chainmanager.eth.EthChainManager;
import com.alipay.antchain.bridge.plugintestrunner.chainmanager.fabric.FabricChainManager;
import com.alipay.antchain.bridge.plugintestrunner.chainmanager.fiscobcos.FiscoBcosChainManager;
import com.alipay.antchain.bridge.plugintestrunner.chainmanager.hyperchain.HyperchainChainManager;
import com.alipay.antchain.bridge.plugintestrunner.config.ChainConf;
import com.alipay.antchain.bridge.plugintestrunner.config.ChainProductEnum;
import com.alipay.antchain.bridge.plugintestrunner.exception.ChainManagerException.*;

public class IChainManagerFactory {
    // 根据 product 创建 IChainManager
    public static IChainManager createIChainManager(String chainProduct) throws Exception {
        ChainProductEnum cp = ChainProductEnum.fromValue(chainProduct);
        ChainConf conf = ChainConf.readFromProperties(chainProduct);
        switch (cp) {
            case ETH:
                return new EthChainManager(((ChainConf.EthChainConf) conf).getHttpUrl(), ((ChainConf.EthChainConf) conf).getPrivateKeyFile(), ((ChainConf.EthChainConf) conf).getGasPrice(), ((ChainConf.EthChainConf) conf).getGasLimit());
            case EOS:
                return new EosChainManager(((ChainConf.EosChainConf) conf).getHttpUrl(), ((ChainConf.EosChainConf) conf).getPrivateKeyFile());
            case BCOS:
                return new FiscoBcosChainManager(((ChainConf.BcosChainConf) conf).getConfDir());
            case FABRIC:
                  return new FabricChainManager(((ChainConf.FabricChainConf) conf).getConfFile());
            case CHAINMAKER:
                return new ChainMakerChainManager(((ChainConf.ChainMakerChainConf) conf).getConfFile());
            case HYPERCHAIN:
                return new HyperchainChainManager(((ChainConf.HyperChainChainConf) conf).getHttpUrl());
            default:
                throw new ChainNotSupportedException("Unsupported chain product: " + chainProduct);
        }
    }
}
