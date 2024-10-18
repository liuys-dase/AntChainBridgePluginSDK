package com.alipay.antchain.bridge.plugintestrunner.testcase;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.List;

public class TestCaseDeserializer extends StdDeserializer<TestCase> {

    public TestCaseDeserializer() {
        this(null);
    }

    public TestCaseDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public TestCase deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode node = mapper.readTree(jp);

        TestCase testCase = new TestCase();

        // 解析简单字段
        JsonNode nameNode = node.get("name");
        if (nameNode != null && !nameNode.isNull()) {
            testCase.setName(nameNode.asText());
        }

        JsonNode jarPathNode = node.get("jarPath");
        if (jarPathNode != null && !jarPathNode.isNull()) {
            testCase.setJarPath(jarPathNode.asText());
        }

        JsonNode productNode = node.get("product");
        if (productNode != null && !productNode.isNull()) {
            testCase.setProduct(productNode.asText());
        }

        JsonNode domainNode = node.get("domain");
        if (domainNode != null && !domainNode.isNull()) {
            testCase.setDomain(domainNode.asText());
        }

        // 解析 pluginLoadAndStartTestList
        JsonNode pluginLoadAndStartTestListNode = node.get("pluginLoadAndStartTestList");
        if (pluginLoadAndStartTestListNode != null && pluginLoadAndStartTestListNode.isArray()) {
            List<String> pluginLoadAndStartTestList = mapper.convertValue(
                    pluginLoadAndStartTestListNode,
                    mapper.getTypeFactory().constructCollectionType(List.class, String.class)
            );
            testCase.setPluginLoadAndStartTestList(pluginLoadAndStartTestList);
        }

        // 解析 pluginInterfaceTestList
        JsonNode pluginInterfaceTestListNode = node.get("pluginInterfaceTestList");
        if (pluginInterfaceTestListNode != null && pluginInterfaceTestListNode.isArray()) {
            List<String> pluginInterfaceTestList = mapper.convertValue(
                    pluginInterfaceTestListNode,
                    mapper.getTypeFactory().constructCollectionType(List.class, String.class)
            );
            testCase.setPluginInterfaceTestList(pluginInterfaceTestList);
        }

        // 解析 chainConf
        JsonNode chainConfNode = node.get("chainConf");
        if (chainConfNode != null) {
            String product = testCase.getProduct();
            if (product == null || product.isEmpty()) {
                throw new JsonMappingException(jp, "Product field is missing or empty.");
            }
            String chainConfJson = new ObjectMapper().writeValueAsString(chainConfNode);
            TestCaseChainConf chainConf;
            try {
                // 尝试将 JSON 字符串转换为 TestCaseChainConf 对象
                // 根据 product 字段的值，选择不同的解析方式
                chainConf = TestCaseChainConf.fromJson(chainConfJson, product);
            } catch (Exception e) {
                throw new JsonMappingException(jp, "Failed to parse chainConf: " + e.getMessage(), e);
            }
            testCase.setChainConf(chainConf);
        }
        return testCase;
    }
}