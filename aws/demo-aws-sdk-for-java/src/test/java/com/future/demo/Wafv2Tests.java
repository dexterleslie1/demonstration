package com.future.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.wafv2.Wafv2Client;
import software.amazon.awssdk.services.wafv2.model.*;
import software.amazon.awssdk.utils.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class Wafv2Tests {
    final static String Prefix = "demo-test-";

    @Test
    public void test() {
        String accessKeyId = System.getenv("accessKeyId");
        String accessKeySecret = System.getenv("accessKeySecret");
        Region region = Region.AP_NORTHEAST_1;

        Wafv2Client wafv2Client = null;
        try {
            AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKeyId, accessKeySecret);
            wafv2Client = Wafv2Client.builder()
                    .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                    .region(region).build();
            final Wafv2Client finalWafv2Client = wafv2Client;

            //region 创建ipset
            int ruleTotalCount = 2;
            List<String> ipsetNameList = new ArrayList<>();
            for (int i = 1; i <= ruleTotalCount; i++) {
                String ipsetName = Prefix + "ipset" + i;
                ipsetNameList.add(ipsetName);
            }
            Map<String, String> ipsetNameToArnMapper = new HashMap<>();
            ListIpSetsResponse listIpSetsResponse = wafv2Client.listIPSets(
                    ListIpSetsRequest.builder()
                            .limit(100)
                            .scope(Scope.REGIONAL)
                            .build());
            Map<String, IPSetSummary> nameToIpsetSummaryMapper = listIpSetsResponse.ipSets().stream().filter(o -> ipsetNameList.contains(o.name())).collect(Collectors.toMap(o -> o.name(), o -> o));
            ipsetNameList.forEach(o -> {
                if (!nameToIpsetSummaryMapper.containsKey(o)) {
                    // 创建ipset
                    CreateIpSetResponse createIpSetResponse = finalWafv2Client.createIPSet(
                            CreateIpSetRequest.builder()
                                    .scope(Scope.REGIONAL)
                                    .ipAddressVersion(IPAddressVersion.IPV4)
                                    .addresses(Collections.emptyList())
                                    .name(o)
                                    .build());
                    Assert.assertFalse(StringUtils.isBlank(createIpSetResponse.summary().id()));
                    ipsetNameToArnMapper.put(o, createIpSetResponse.summary().arn());
                } else {
                    ipsetNameToArnMapper.put(o, nameToIpsetSummaryMapper.get(o).arn());
                }
            });

            //endregion

            //region 创建webacl
            ListWebAcLsResponse webAclListResponse = wafv2Client.listWebACLs(ListWebAcLsRequest.builder().limit(100).scope(Scope.REGIONAL).build());
            String webAclName = Prefix + "web-acl";
            String webAclDescription = UUID.randomUUID().toString();
            String webAclId;
            String webAclLockToken;
            List<WebACLSummary> webACLSummaryList = webAclListResponse.webACLs().stream().filter(o -> o.name().equals(webAclName)).collect(Collectors.toList());
            if (webACLSummaryList.size() <= 0) {
                List<Rule> ruleList = new ArrayList<>();
                for (int i = 1; i <= ruleTotalCount; i++) {
                    String ruleName = Prefix + "rule" + i;
                    ruleList.add(Rule.builder()
                            .name(ruleName)
                            .priority(i)
                            .visibilityConfig(VisibilityConfig.builder()
                                    .cloudWatchMetricsEnabled(true)
                                    .sampledRequestsEnabled(false)
                                    .metricName(ruleName)
                                    .build())
                            .statement(Statement.builder()
                                    .ipSetReferenceStatement(IPSetReferenceStatement.builder()
                                            // rule和ipset一一对应绑定
                                            .arn(ipsetNameToArnMapper.get(Prefix + "ipset" + i))
                                            // 注意：如果注释ipSetForwardedIPConfig表示使用sourceIp
                                            .ipSetForwardedIPConfig(IPSetForwardedIPConfig.builder()
                                                    .fallbackBehavior(FallbackBehavior.MATCH)
                                                    .headerName("X-Forwarded-For")
                                                    .position(ForwardedIPPosition.FIRST)
                                                    .build())
                                            .build())
                                    .build())
                            .action(
                                    RuleAction.builder()
                                            .block(BlockAction.builder()
                                                    .build())
                                            .build())
                            .build());
                }

                // 创建频率控制rule
                String ruleName = Prefix + "rule-ratelimit";
                ruleList.add(Rule.builder()
                        .name(ruleName)
                        .priority(ruleTotalCount + 1)
                        .visibilityConfig(VisibilityConfig.builder()
                                .cloudWatchMetricsEnabled(true)
                                .sampledRequestsEnabled(false)
                                .metricName(ruleName)
                                .build())
                        .statement(Statement.builder().rateBasedStatement(
                                RateBasedStatement.builder()
                                        // 注意：RateBasedStatementAggregateKeyType.IP表示使用sourceIp
                                        //.aggregateKeyType(RateBasedStatementAggregateKeyType.IP)
                                        .aggregateKeyType(RateBasedStatementAggregateKeyType.FORWARDED_IP)
                                        .forwardedIPConfig(ForwardedIPConfig.builder()
                                                .fallbackBehavior(FallbackBehavior.MATCH)
                                                .headerName("X-Forwarded-For")
                                                .build())
                                        .limit(2000L)
                                        .build()).build())
                        .action(
                                RuleAction.builder()
                                        .block(BlockAction.builder()
                                                .build())
                                        .build())
                        .build());

                // webacl不存在则创建
                CreateWebAclResponse createWebAclResponse = wafv2Client.createWebACL(
                        CreateWebAclRequest.builder()
                                .name(webAclName)
                                .description(webAclDescription)
                                .scope(Scope.REGIONAL)
                                .visibilityConfig(VisibilityConfig.builder()
                                        .metricName(webAclName)
                                        .cloudWatchMetricsEnabled(true)
                                        .sampledRequestsEnabled(false)
                                        .build())
                                .rules(ruleList)
                                .defaultAction(DefaultAction.builder()
                                        .allow(AllowAction.builder().build())
                                        .build())
                                .build());
                Assert.assertEquals(webAclName, createWebAclResponse.summary().name());
                Assert.assertEquals(webAclDescription, createWebAclResponse.summary().description());
                webAclId = createWebAclResponse.summary().id();
                webAclLockToken = createWebAclResponse.summary().lockToken();
            } else {
                webAclId = webACLSummaryList.get(0).id();
                webAclLockToken = webACLSummaryList.get(0).lockToken();
            }

            Assert.assertFalse(StringUtils.isBlank(webAclId));

            //endregion

            //region 添加ip地址到ipset中
            listIpSetsResponse = wafv2Client.listIPSets(ListIpSetsRequest.builder().scope(Scope.REGIONAL).limit(100).build());
            List<IPSetSummary> ipSetSummaryList = listIpSetsResponse.ipSets().stream().filter(o -> o.name().startsWith(Prefix + "ipset")).collect(Collectors.toList());
            String ipsetId = ipSetSummaryList.get(0).id();
            String ipsetName = ipSetSummaryList.get(0).name();
            String lockToken = ipSetSummaryList.get(0).lockToken();
            wafv2Client.updateIPSet(
                    UpdateIpSetRequest.builder()
                            .name(ipsetName)
                            .id(ipsetId)
                            .addresses("192.168.1.2/32", "192.168.1.5/32")
                            .scope(Scope.REGIONAL)
                            .lockToken(lockToken)
                            .build());

            //endregion

            //region 测试更新rule
            List<Rule> ruleList = new ArrayList<>();
            for (int i = 1; i <= ruleTotalCount; i++) {
                String ruleName = Prefix + "rule" + i;
                ruleList.add(
                        Rule.builder()
                                .name(ruleName)
                                .priority(i)
                                .visibilityConfig(VisibilityConfig.builder()
                                        .cloudWatchMetricsEnabled(true)
                                        .sampledRequestsEnabled(false)
                                        .metricName(ruleName)
                                        .build())
                                .statement(
                                        Statement.builder()
                                                .ipSetReferenceStatement(
                                                        IPSetReferenceStatement.builder()
                                                                .arn(ipsetNameToArnMapper.get(Prefix + "ipset" + i))
                                                                .build()
                                                )
                                                .build()
                                )
                                .action(
                                        RuleAction.builder()
                                                .block(BlockAction.builder()
                                                        .build())
                                                .build()
                                )
                                .build()
                );
            }

            String ruleName = Prefix + "rule-ratelimit";
            ruleList.add(Rule.builder()
                    .name(ruleName)
                    .priority(ruleTotalCount + 1)
                    .visibilityConfig(VisibilityConfig.builder()
                            .cloudWatchMetricsEnabled(true)
                            .sampledRequestsEnabled(false)
                            .metricName(ruleName)
                            .build())
                    .statement(Statement.builder().rateBasedStatement(
                            RateBasedStatement.builder()
                                    // 注意：RateBasedStatementAggregateKeyType.IP表示使用sourceIp
                                    .aggregateKeyType(RateBasedStatementAggregateKeyType.IP)
                                    .limit(2000L)
                                    .build()).build())
                    .action(
                            RuleAction.builder()
                                    .block(BlockAction.builder()
                                            .build())
                                    .build())
                    .build());

            wafv2Client.updateWebACL(
                    UpdateWebAclRequest.builder()
                            .id(webAclId)
                            .name(webAclName)
                            .description(webAclDescription)
                            .scope(Scope.REGIONAL)
                            .visibilityConfig(VisibilityConfig.builder()
                                    .metricName(webAclName)
                                    .cloudWatchMetricsEnabled(true)
                                    .sampledRequestsEnabled(false)
                                    .build())
                            .rules(ruleList)
                            .defaultAction(DefaultAction.builder()
                                    .allow(AllowAction.builder().build())
                                    .build())
                            .lockToken(webAclLockToken)
                            .build()
            );

            //endregion
        } finally {
            if (wafv2Client != null) {
                wafv2Client.close();
            }
        }
    }
}
