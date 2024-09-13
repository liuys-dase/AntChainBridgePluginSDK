package org.example.plugintestrunner.config;

public class ChainConfig {

    public static class EthChainConfig extends ChainConfig {
        public static final String dataDir;
        public static final String httpAddr;
        public static final int httpPort;
        public static final String httpApi;
        public static final String privateKeyFile;

        static {
            ChainConfigManager config = ChainConfigManager.getInstance();
            dataDir = config.getProperty("ethereum.data_dir");
            httpAddr = config.getProperty("ethereum.http_addr");
            httpPort = Integer.parseInt(config.getProperty("ethereum.http_port"));
            httpApi = config.getProperty("ethereum.http_api");
            privateKeyFile = config.getProperty("ethereum.private_key_file");
        }

        public static String getHttpUrl() {
            return "http://" + httpAddr + ":" + httpPort;
        }
    }

    public static class EosChainConfig extends ChainConfig {
        public static final String dataDir;
        public static final String httpServerAddress;

        static {
            ChainConfigManager config = ChainConfigManager.getInstance();
            dataDir = config.getProperty("eos.data_dir");
            httpServerAddress = config.getProperty("eos.http_server_address");
        }

        public static String getHttpUrl() {
            return "http://" + "127.0.0.1" + ":" + httpServerAddress;
        }
    }

    public static class FiscoBcosChainConfig extends ChainConfig {
        public static final String dataDir;
        public static final String confDir;
        public static final String confFile;

        static {
            ChainConfigManager config = ChainConfigManager.getInstance();
            dataDir = config.getProperty("fisco-bcos.data_dir");
            confDir = config.getProperty("fisco-bcos.conf_dir");
            confFile = config.getProperty("fisco-bcos.conf_file");
        }
    }

    public static class ChainMakerChainConfig extends ChainConfig {
        public static final String confFile;

        static {
            ChainConfigManager config = ChainConfigManager.getInstance();
            confFile = config.getProperty("chainmaker.conf_file");
        }
    }


    public static class HyperChainChainConfig extends ChainConfig {
        public static final String httpPort;

        static {
            ChainConfigManager config = ChainConfigManager.getInstance();
            httpPort = config.getProperty("hyperchain.http_port");
        }

        public static String getHttpUrl() {
            return "127.0.0.1" + ":" + httpPort;
        }
    }


    public static class FabricChainConfig extends ChainConfig {
        public static final String privateKeyFile;
        public static final String certFile;
        public static final String peerTlsCertFile;
        public static final String ordererTlsCertFile;

        static {
            ChainConfigManager config = ChainConfigManager.getInstance();
            privateKeyFile = config.getProperty("fabric.private_key_file");
            certFile = config.getProperty("fabric.cert_file");
            peerTlsCertFile = config.getProperty("fabric.peer_tls_cert_file");
            ordererTlsCertFile = config.getProperty("fabric.orderer_tls_cert_file");
        }
    }
    // TODO add more chains
}