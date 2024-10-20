package com.alipay.antchain.bridge.plugintestrunner.operation;

public enum PluginInterfaceEnum {
    STARTUP("startup"),
    SHUTDOWN("shutdown"),
    GET_CONTEXT("getContext"),
    QUERY_LATEST_HEIGHT("queryLatestHeight"),
    SETUP_AUTH_MESSAGE_CONTRACT("setupAuthMessageContract"),
    SETUP_SDP_MESSAGE_CONTRACT("setupSDPMessageContract"),
    SET_LOCAL_DOMAIN("setLocalDomain"),
    QUERY_SDP_MESSAGE_SEQ("querySDPMessageSeq"),
    SET_PROTOCOL("setProtocol"),
    SET_AM_CONTRACT("setAmContract"),
    READ_CROSS_CHAIN_MESSAGES_BY_HEIGHT("readCrossChainMessagesByHeight"),
    RELAY_AUTH_MESSAGE("relayAuthMessage"),
    READ_CROSS_CHAIN_MESSAGE_RECEIPT("readCrossChainMessageReceipt");

    private final String operationName;

    PluginInterfaceEnum(String operationName) {
        this.operationName = operationName;
    }

    public String getOperationName() {
        return operationName;
    }

    public static PluginInterfaceEnum fromString(String operationName) {
        for (PluginInterfaceEnum operation : PluginInterfaceEnum.values()) {
            if (operation.getOperationName().equalsIgnoreCase(operationName)) {
                return operation;
            }
        }
        // 如果没有匹配项，可以抛出异常或返回 null
        throw new IllegalArgumentException("No enum constant for operation: " + operationName);
    }

    public static String getAllOperationNames() {
        StringBuilder operationNames = new StringBuilder();
        for (PluginInterfaceEnum operation : PluginInterfaceEnum.values()) {
            if (operationNames.length() > 0) {
                operationNames.append(", ");
            }
            operationNames.append(operation.getOperationName());
        }
        return operationNames.toString();
    }
}