package com.alipay.antchain.bridge.plugintestrunner.config;

import lombok.Getter;

@Getter
public enum ChainProductEnum {
    TESTCHAIN("testchain"),
    ETH("simple-ethereum"),
    CHAINMAKER("chainmaker"),
    FABRIC("fabric"),
    BCOS("fiscobcos"),
    EOS("eos"),
    HYPERCHAIN("hyperchain2");

    private final String value;

    ChainProductEnum(String value) {
        this.value = value;
    }

    // 静态方法，将字符串转换为 ChainType 枚举
    public static ChainProductEnum fromValue(String value) {
        for (ChainProductEnum type : ChainProductEnum.values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown ChainProductEnum value: " + value);
    }

    public static boolean isInvalid(String value) {
        for (ChainProductEnum type : ChainProductEnum.values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return false;
            }
        }
        return true;
    }
}