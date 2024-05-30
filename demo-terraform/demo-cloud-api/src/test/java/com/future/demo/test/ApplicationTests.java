package com.future.demo.test;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.future.demo.Application;
import com.future.demo.test.config.TestSupportConfiguration;
import com.future.demo.test.feign.TestSupportDemoFeignClient;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class, TestSupportConfiguration.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class ApplicationTests {

    @Resource
    TestSupportDemoFeignClient testSupportDemoFeignClient;

    // 配置terraform cloud google credentials环境变量
    // https://support.hashicorp.com/hc/en-us/articles/4406586874387-How-to-set-up-Google-Cloud-GCP-credentials-in-Terraform-Cloud
    //
    // terraform cloud api工作流程
    // https://developer.hashicorp.com/terraform/cloud-docs/run/api
    @Test
    public void test() throws Exception {
        // 登录terraform cloud获取token
        String token = "Bearer xxx";
        String organizationName = "future-demo-my-org";
        ObjectNode response = this.testSupportDemoFeignClient.accountDetails(token);
        Assert.assertEquals("dexterleslie", response.get("data").get("attributes").get("username").asText());

        response = this.testSupportDemoFeignClient.listProjects(organizationName, token);
        Assert.assertEquals("Default Project", response.withArray("data").get(0).get("attributes").get("name").asText());

        List<String> projectNameList = StreamSupport.stream(response.withArray("data").spliterator(), false).map(o -> o.get("attributes").get("name").asText()).collect(Collectors.toList());

        String projectName = "demo";
        // 如果不存在项目则创建
        if (!projectNameList.contains(projectName)) {
            this.testSupportDemoFeignClient.createProject(new TestSupportDemoFeignClient.CreateProjectVo() {{
                getData().getAttributes().setName(projectName);
            }}, organizationName, token);
        }

        String workspaceName = "demo-workspace";
        try {
            response = this.testSupportDemoFeignClient.getWorkspace(organizationName, workspaceName, token);
        } catch (Exception ex) {
            response = this.testSupportDemoFeignClient.createWorkspace(organizationName, new TestSupportDemoFeignClient.CreateWorkspaceVo() {{
                getData().getAttributes().setName(workspaceName);
            }}, token);
        }
        String workspaceId = response.get("data").get("id").asText();

        response = this.testSupportDemoFeignClient.createConfigurationVersion(workspaceId, new TestSupportDemoFeignClient.CreateConfigurationVersionVo() {{
            // 不自动plan和apply
            getData().getAttributes().setAutoQueueRuns(false);
        }}, token);
        String uploadUrl = response.get("data").get("attributes").get("upload-url").asText();
        String configurationVersionId = response.get("data").get("id").asText();

        String tarGzPath = "/tmp/demo.tar.gz";

        FileOutputStream fOut = null;
        BufferedOutputStream bOut = null;
        GzipCompressorOutputStream gzOut = null;
        TarArchiveOutputStream tOut = null;
        try {
            fOut = new FileOutputStream(new File(tarGzPath));
            bOut = new BufferedOutputStream(fOut);
            gzOut = new GzipCompressorOutputStream(bOut);
            tOut = new TarArchiveOutputStream(gzOut);

            File f = new File("main.tf");
            String entryName = f.getName();
            TarArchiveEntry tarEntry = new TarArchiveEntry(f, entryName);
            tOut.putArchiveEntry(tarEntry);
            IOUtils.copy(new FileInputStream(f), tOut);
            tOut.closeArchiveEntry();

            f = new File("private.key");
            entryName = f.getName();
            tarEntry = new TarArchiveEntry(f, entryName);
            tOut.putArchiveEntry(tarEntry);
            IOUtils.copy(new FileInputStream(f), tOut);
            tOut.closeArchiveEntry();

            f = new File("public.key");
            entryName = f.getName();
            tarEntry = new TarArchiveEntry(f, entryName);
            tOut.putArchiveEntry(tarEntry);
            IOUtils.copy(new FileInputStream(f), tOut);
            tOut.closeArchiveEntry();
        } finally {
            if (tOut != null) {
                tOut.finish();
                tOut.close();
            }
            if (gzOut != null)
                gzOut.close();
            if (bOut != null)
                bOut.close();
            if (fOut != null)
                fOut.close();
        }

        URL urlObject = new URL(uploadUrl);
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/octet-stream");
        IOUtils.copy(new FileInputStream(tarGzPath), connection.getOutputStream());
        IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);

        response = this.testSupportDemoFeignClient.createRun(new TestSupportDemoFeignClient.CreateRunVo() {{
            getData().getAttributes().setMessage("Customize message");
            // 自动确认apply
            getData().getAttributes().setAutoApply(true);
            getData().getRelationships().getWorkspace().getData().setId(workspaceId);
            getData().getRelationships().getConfigurationVersion().getData().setId(configurationVersionId);
        }}, token);
        String runId = response.get("data").get("id").asText();

//        Thread.sleep(30000);
//        // 自动确认apply
//        this.testSupportDemoFeignClient.applyRun(runId, token);
    }

}
