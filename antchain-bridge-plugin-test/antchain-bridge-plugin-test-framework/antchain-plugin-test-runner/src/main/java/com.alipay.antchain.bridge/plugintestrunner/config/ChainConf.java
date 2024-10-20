package com.alipay.antchain.bridge.plugintestrunner.config;

import com.alipay.antchain.bridge.plugintestrunner.chainmanager.IChainManager;
import com.alipay.antchain.bridge.plugintestrunner.chainmanager.chainmaker.ChainMakerChainManager;
import com.alipay.antchain.bridge.plugintestrunner.chainmanager.eos.EosChainManager;
import com.alipay.antchain.bridge.plugintestrunner.chainmanager.eth.EthChainManager;
import com.alipay.antchain.bridge.plugintestrunner.chainmanager.fabric.FabricChainManager;
import com.alipay.antchain.bridge.plugintestrunner.chainmanager.fiscobcos.FiscoBcosChainManager;
import com.alipay.antchain.bridge.plugintestrunner.chainmanager.hyperchain.HyperchainChainManager;
import com.alipay.antchain.bridge.plugintestrunner.exception.TestCaseException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.alipay.antchain.bridge.plugintestrunner.exception.TestCaseException.*;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public abstract class ChainConf {

    public abstract boolean isValid();

    public static ChainConf readFromProperties(String product) throws Exception {
        ChainProductEnum cp = ChainProductEnum.fromValue(product);
        ChainConfigManager config = ChainConfigManager.getInstance();
        switch (cp) {
            case ETH:
                String eth_http_addr = config.getProperty("ethereum.http_addr");
                String eth_http_port = config.getProperty("ethereum.http_port");
                String eth_url = "http://" + eth_http_addr + ":" + eth_http_port;
                String eth_private_key_file = config.getProperty("ethereum.private_key_file");
                String eth_gas_price = config.getProperty("ethereum.gas_price");
                String eth_gas_limit = config.getProperty("ethereum.gas_limit");
                return new EthChainConf(eth_url, eth_private_key_file, eth_gas_price, eth_gas_limit);
            case EOS:
                String eos_http_server_address = config.getProperty("eos.http_server_address");
                String eos_url = "http://" + "127.0.0.1" + ":" + eos_http_server_address;
                String eos_private_key_file = config.getProperty("eos.private_key_file");
                return new EosChainConf(eos_url, eos_private_key_file);
            case BCOS:
                String bcos_conf_dir = config.getProperty("fiscobcos.conf_dir");
                return new BcosChainConf(bcos_conf_dir);
            case FABRIC:
                String fabric_conf_file = config.getProperty("fabric.conf_file");
                return new FabricChainConf(fabric_conf_file);
            case CHAINMAKER:
                String chainmaker_conf_file = config.getProperty("chainmaker.conf_file");
                return new ChainMakerChainConf(chainmaker_conf_file);
            case HYPERCHAIN:
                String hyperchain_http_port = config.getProperty("hyperchain.http_port");
                String hyperchain_url = "127.0.0.1" + ":" + hyperchain_http_port;
                return new HyperChainChainConf(hyperchain_url);
            default:
                throw new TestCaseChainConfReadFromPropertiesException("Unknown product type: " + product);
        }
    }

    // 将 chainConf 转换为 chainManager
    public IChainManager toChainManager() throws Exception {
        ChainProductEnum cp = ChainProductEnum.fromValue(product);
        switch (cp) {
            case ETH:
                return new EthChainManager(((EthChainConf) this).getHttpUrl(), ((EthChainConf) this).getPrivateKeyFile(), ((EthChainConf) this).getGasPrice(), ((EthChainConf) this).getGasLimit());
            case EOS:
                return new EosChainManager(((EosChainConf) this).getHttpUrl(), ((EosChainConf) this).getPrivateKeyFile());
            case BCOS:
                return new FiscoBcosChainManager(((BcosChainConf) this).getConfDir());
            case FABRIC:
                return new FabricChainManager(((FabricChainConf) this).getConfFile());
            case CHAINMAKER:
                return new ChainMakerChainManager(((ChainMakerChainConf) this).getConfFile());
            case HYPERCHAIN:
                return new HyperchainChainManager(((HyperChainChainConf) this).getHttpUrl());
            default:
                throw new TestCaseChainConfToChainManagerException("Unknown product type: " + product);
        }
    }

    // 读取 json 字符串，转换为 ChainConf
    public static ChainConf fromJson(String jsonString, String product) throws JsonProcessingException, TestCaseException {
        ObjectMapper mapper = new ObjectMapper();
        ChainProductEnum cp = ChainProductEnum.fromValue(product);
        switch (cp) {
            case ETH:
                return mapper.readValue(jsonString, EthChainConf.class);
            case EOS:
                return mapper.readValue(jsonString, EosChainConf.class);
            case BCOS:
                return mapper.readValue(jsonString, BcosChainConf.class);
            case FABRIC:
                return mapper.readValue(jsonString, FabricChainConf.class);
            case CHAINMAKER:
                return mapper.readValue(jsonString, ChainMakerChainConf.class);
            case HYPERCHAIN:
                return mapper.readValue(jsonString, HyperChainChainConf.class);
            default:
                throw new TestCaseChainConfToClassException("Unknown product type: " + product);
        }
    }

    // 基类的 product 字段
    @JsonIgnore
    private String product;

    protected void setProduct(String product) {
        this.product = product;
    }


    @Getter
    @Setter
    public static class EthChainConf extends ChainConf {
        private String httpUrl;
        private String privateKeyFile;
        private String gasPrice;
        private String gasLimit;

        public EthChainConf() {
            setProduct(ChainProductEnum.ETH.getValue());
        }

        public EthChainConf(String httpUrl, String privateKeyFile, String gasPrice, String gasLimit) {
            this.httpUrl = httpUrl;
            this.privateKeyFile = privateKeyFile;
            this.gasPrice = gasPrice;
            this.gasLimit = gasLimit;
            setProduct(ChainProductEnum.ETH.getValue());
        }

        @Override
        public boolean isValid() {
            return httpUrl != null && privateKeyFile != null && gasPrice != null && gasLimit != null;
        }
    }

    @Setter
    @Getter
    public static class EosChainConf extends ChainConf {
        private String httpUrl;
        private String privateKeyFile;

        public EosChainConf() {
            setProduct(ChainProductEnum.EOS.getValue());
        }

        public EosChainConf(String httpUrl, String privateKeyFile) {
            this.httpUrl = httpUrl;
            this.privateKeyFile = privateKeyFile;
            setProduct(ChainProductEnum.EOS.getValue());
        }

        @Override
        public boolean isValid() {
            return httpUrl != null && privateKeyFile != null;
        }
    }

    @Setter
    @Getter
    public static class BcosChainConf extends ChainConf {
        private String confDir;

        public BcosChainConf() {
            setProduct(ChainProductEnum.BCOS.getValue());
        }

        public BcosChainConf(String confDir) {
            this.confDir = confDir;
            setProduct(ChainProductEnum.BCOS.getValue());
        }

        @Override
        public boolean isValid() {
            return confDir != null;
        }
    }

    @Setter
    @Getter
    public static class FabricChainConf extends ChainConf {
        private String confFile;

        public FabricChainConf() {
            setProduct(ChainProductEnum.FABRIC.getValue());
        }

        public FabricChainConf(String confFile) {
            this.confFile = confFile;
            setProduct(ChainProductEnum.FABRIC.getValue());
        }

        @Override
        public boolean isValid() {
            return confFile != null;
        }

    }

    @Setter
    @Getter
    public static class ChainMakerChainConf extends ChainConf {
        private String confFile;

        public ChainMakerChainConf() {
            setProduct(ChainProductEnum.CHAINMAKER.getValue());
        }

        public ChainMakerChainConf(String confFile) {
            this.confFile = confFile;
            setProduct(ChainProductEnum.CHAINMAKER.getValue());
        }

        @Override
        public boolean isValid() {
            return confFile != null;
        }
    }

    @Setter
    @Getter
    public static class HyperChainChainConf extends ChainConf {
        private String httpUrl;

        public HyperChainChainConf() {
            setProduct(ChainProductEnum.HYPERCHAIN.getValue());
        }

        public HyperChainChainConf(String httpUrl) {
            this.httpUrl = httpUrl;
            setProduct(ChainProductEnum.HYPERCHAIN.getValue());
        }

        @Override
        public boolean isValid() {
            return httpUrl != null;
        }
    }
}