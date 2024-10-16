package org.example.plugintestrunner.chainmanager.chainmaker;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import org.chainmaker.sdk.ChainClient;
import org.chainmaker.sdk.ChainManager;
import org.chainmaker.sdk.config.AuthType;
import org.chainmaker.sdk.config.ChainClientConfig;
import org.chainmaker.sdk.config.NodeConfig;
import org.chainmaker.sdk.config.SdkConfig;
import org.chainmaker.sdk.crypto.ChainMakerCryptoSuiteException;
import org.chainmaker.sdk.utils.CryptoUtils;
import org.chainmaker.sdk.utils.FileUtils;
import org.chainmaker.sdk.utils.UtilsException;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

public class ChainMakerConfigUtil {


    private static final String ADMIN1_TLS_KEY_PATH = "/tmp/chainmaker/chainmaker-go/build/release/chainmaker-v3.0.0-wx-org1.chainmaker.org/config/wx-org1.chainmaker.org/certs/user/admin1/admin1.tls.key";
    private static final String ADMIN1_TLS_CERT_PATH = "/tmp/chainmaker/chainmaker-go/build/release/chainmaker-v3.0.0-wx-org1.chainmaker.org/config/wx-org1.chainmaker.org/certs/user/admin1/admin1.tls.crt";
    private static final String ADMIN2_TLS_KEY_PATH = "/tmp/chainmaker/chainmaker-go/build/release/chainmaker-v3.0.0-wx-org2.chainmaker.org/config/wx-org2.chainmaker.org/certs/user/admin1/admin1.tls.key";
    private static final String ADMIN2_TLS_CERT_PATH = "/tmp/chainmaker/chainmaker-go/build/release/chainmaker-v3.0.0-wx-org2.chainmaker.org/config/wx-org2.chainmaker.org/certs/user/admin1/admin1.tls.crt";
    private static final String ADMIN3_TLS_KEY_PATH = "/tmp/chainmaker/chainmaker-go/build/release/chainmaker-v3.0.0-wx-org3.chainmaker.org/config/wx-org3.chainmaker.org/certs/user/admin1/admin1.tls.key";
    private static final String ADMIN3_TLS_CERT_PATH = "/tmp/chainmaker/chainmaker-go/build/release/chainmaker-v3.0.0-wx-org3.chainmaker.org/config/wx-org3.chainmaker.org/certs/user/admin1/admin1.tls.crt";

    private static final String ADMIN1_KEY_PATH = "/tmp/chainmaker/chainmaker-go/build/release/chainmaker-v3.0.0-wx-org1.chainmaker.org/config/wx-org1.chainmaker.org/certs/user/admin1/admin1.sign.key";
    private static final String ADMIN1_CERT_PATH = "/tmp/chainmaker/chainmaker-go/build/release/chainmaker-v3.0.0-wx-org1.chainmaker.org/config/wx-org1.chainmaker.org/certs/user/admin1/admin1.sign.crt";
    private static final String ADMIN2_KEY_PATH = "/tmp/chainmaker/chainmaker-go/build/release/chainmaker-v3.0.0-wx-org2.chainmaker.org/config/wx-org2.chainmaker.org/certs/user/admin1/admin1.sign.key";
    private static final String ADMIN2_CERT_PATH = "/tmp/chainmaker/chainmaker-go/build/release/chainmaker-v3.0.0-wx-org2.chainmaker.org/config/wx-org2.chainmaker.org/certs/user/admin1/admin1.sign.crt";
    private static final String ADMIN3_KEY_PATH = "/tmp/chainmaker/chainmaker-go/build/release/chainmaker-v3.0.0-wx-org3.chainmaker.org/config/wx-org3.chainmaker.org/certs/user/admin1/admin1.sign.key";
    private static final String ADMIN3_CERT_PATH = "/tmp/chainmaker/chainmaker-go/build/release/chainmaker-v3.0.0-wx-org3.chainmaker.org/config/wx-org3.chainmaker.org/certs/user/admin1/admin1.sign.crt";

    private static final String ORG_ID1 = "wx-org1.chainmaker.org";
    private static final String ORG_ID2 = "wx-org2.chainmaker.org";
    private static final String ORG_ID3 = "wx-org3.chainmaker.org";

    static ChainClient chainClient;
    static ChainManager chainManager;

    public static String parseChainConfig(String sdk_config_file) throws Exception {
        Yaml yaml = new Yaml();
        InputStream in = ChainMakerConfigUtil.class.getClassLoader().getResourceAsStream(sdk_config_file);
        if (in == null) {
            System.out.println("in is null");
        }
        SdkConfig sdkConfig;
        sdkConfig = yaml.loadAs(in, SdkConfig.class);
        in.close();

        ChainClientConfig chainClientConfig = sdkConfig.getChainClient();
        dealChainClientConfig(chainClientConfig);

        NodeConfig[] nodes = chainClientConfig.getNodes();
        NodeConfig[] newNodes = new NodeConfig[nodes.length];

        int nodesCount = nodes.length;
        for (int i = 0; i < nodesCount; i++) {
            NodeConfig nodeConfig = nodes[i];
            List<byte[]> tlsCaCertList = new ArrayList<>();

            if (nodeConfig.getTrustRootBytes() == null && nodeConfig.getTrustRootPaths() != null) {
                String[] trustRootPaths = nodeConfig.getTrustRootPaths();
                int trustRootPathsLen = trustRootPaths.length;
                int j = 0;
                while (true) {
                    if (j >= trustRootPathsLen) {
                        byte[][] tlsCaCerts = new byte[tlsCaCertList.size()][];
                        tlsCaCertList.toArray(tlsCaCerts);
                        nodeConfig.setTrustRootBytes(tlsCaCerts);
                        break;
                    }

                    String rootPath = trustRootPaths[j];
                    List<String> filePathList = FileUtils.getFilesByPath(rootPath);

                    for (String fp : filePathList) {
                        tlsCaCertList.add(FileUtils.getFileBytes(fp));
                    }
                    ++j;
                }
            }
            newNodes[i] = nodeConfig;
        }
        chainClientConfig.setNodes(newNodes);

        sdkConfig.setChainClient(chainClientConfig);
        Gson gson = new Gson();
        String sdkConfigString = gson.toJson(sdkConfig);

        // 创建fastjson的JSONObject
        JSONObject upperConfigJson = new JSONObject();
        upperConfigJson.put("sdkConfig", sdkConfigString);

        // 创建adminTlsKeyPaths数组
        JSONArray adminTlsKeyPaths = new JSONArray();
        adminTlsKeyPaths.add(FileUtils.getFileBytes(ADMIN1_TLS_KEY_PATH));
        adminTlsKeyPaths.add(FileUtils.getFileBytes(ADMIN2_TLS_KEY_PATH));
        adminTlsKeyPaths.add(FileUtils.getFileBytes(ADMIN3_TLS_KEY_PATH));
        upperConfigJson.put("adminTlsKeyPaths", adminTlsKeyPaths);

        // 创建adminTlsCertPaths数组
        JSONArray adminTlsCertPaths = new JSONArray();
        adminTlsCertPaths.add(FileUtils.getFileBytes(ADMIN1_TLS_CERT_PATH));
        adminTlsCertPaths.add(FileUtils.getFileBytes(ADMIN2_TLS_CERT_PATH));
        adminTlsCertPaths.add(FileUtils.getFileBytes(ADMIN3_TLS_CERT_PATH));
        upperConfigJson.put("adminTlsCertPaths", adminTlsCertPaths);

        // 创建adminKeyPaths数组
        JSONArray adminKeyPaths = new JSONArray();
        adminKeyPaths.add((FileUtils.getFileBytes(ADMIN1_KEY_PATH)));
        adminKeyPaths.add((FileUtils.getFileBytes(ADMIN2_KEY_PATH)));
        adminKeyPaths.add((FileUtils.getFileBytes(ADMIN3_KEY_PATH)));
        upperConfigJson.put("adminKeyPaths", adminKeyPaths);

        // 创建adminCertPaths数组
        JSONArray adminCertPaths = new JSONArray();
        adminCertPaths.add(FileUtils.getFileBytes(ADMIN1_CERT_PATH));
        adminCertPaths.add(FileUtils.getFileBytes(ADMIN2_CERT_PATH));
        adminCertPaths.add(FileUtils.getFileBytes(ADMIN3_CERT_PATH));
        upperConfigJson.put("adminCertPaths", adminCertPaths);

        // 创建orgIds数组
        JSONArray orgIds = new JSONArray();
        orgIds.add(ORG_ID1);
        orgIds.add(ORG_ID2);
        orgIds.add(ORG_ID3);
        upperConfigJson.put("orgIds", orgIds);

        return JSON.toJSONString(upperConfigJson);
    }

    private static void dealChainClientConfig(ChainClientConfig chainClientConfig) throws UtilsException, ChainMakerCryptoSuiteException {
        String authType = chainClientConfig.getAuthType();
        byte[] userKeyBytes;
        if (!authType.equals(AuthType.PermissionedWithKey.getMsg()) && !authType.equals(AuthType.Public.getMsg())) {
            chainClientConfig.setAuthType(AuthType.PermissionedWithCert.getMsg());
            userKeyBytes = chainClientConfig.getUserKeyBytes();
            if (userKeyBytes == null && chainClientConfig.getUserKeyFilePath() != null) {
                chainClientConfig.setUserKeyBytes(FileUtils.getFileBytes(chainClientConfig.getUserKeyFilePath()));
            }

            byte[] userCrtBytes = chainClientConfig.getUserCrtBytes();
            if (userCrtBytes == null && chainClientConfig.getUserCrtFilePath() != null) {
                chainClientConfig.setUserCrtBytes(FileUtils.getFileBytes(chainClientConfig.getUserCrtFilePath()));
            }

            byte[] userSignKeyBytes = chainClientConfig.getUserSignKeyBytes();
            if (userSignKeyBytes == null && chainClientConfig.getUserSignKeyFilePath() != null) {
                chainClientConfig.setUserSignKeyBytes(FileUtils.getFileBytes(chainClientConfig.getUserSignKeyFilePath()));
            }

            byte[] userSignCrtBytes = chainClientConfig.getUserSignCrtBytes();
            if (userSignCrtBytes == null && chainClientConfig.getUserSignCrtFilePath() != null) {
                chainClientConfig.setUserSignCrtBytes(FileUtils.getFileBytes(chainClientConfig.getUserSignCrtFilePath()));
            }
        } else {
            userKeyBytes = FileUtils.getFileBytes(chainClientConfig.getUserSignKeyFilePath());
            PrivateKey privateKey = CryptoUtils.getPrivateKeyFromBytes(userKeyBytes);

            PublicKey publicKey;
            try {
                publicKey = CryptoUtils.getPublicKeyFromPrivateKey(privateKey);
            } catch (NoSuchProviderException | InvalidKeySpecException | NoSuchAlgorithmException nodesCount) {
                throw new ChainMakerCryptoSuiteException("Get publicKey from privateKey Error: " + nodesCount.getMessage());
            }
            chainClientConfig.setPublicKey(publicKey);
        }
    }
}
