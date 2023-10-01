package com.future.demo.test.feign;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yyd.common.exception.BusinessException;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        contextId = "testSupportDemoFeignClient",
        value = "demo-spring-boot-test",
        path = "/api/v2")
public interface TestSupportDemoFeignClient {

    @GetMapping("account/details")
    ObjectNode accountDetails(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String token) throws BusinessException;

    @GetMapping("/organizations/{organizationName}/projects")
    ObjectNode listProjects(@PathVariable("organizationName") String organizationName,
                            @RequestHeader(value = HttpHeaders.AUTHORIZATION) String token) throws BusinessException;

    @RequestMapping(method = RequestMethod.POST, value = "/organizations/{organizationName}/projects",consumes = "application/vnd.api+json")
    ObjectNode createProject(@RequestBody CreateProjectVo vo,
                             @PathVariable("organizationName") String organizationName,
                             @RequestHeader(value = HttpHeaders.AUTHORIZATION) String token) throws BusinessException;

//    @GetMapping("/organizations/{organizationName}/workspaces")
//    ObjectNode listWorkspaces(@PathVariable("organizationName") String organizationName,
//                              @RequestHeader(value = HttpHeaders.AUTHORIZATION) String token) throws BusinessException;

    @RequestMapping(method = RequestMethod.POST, value = "/organizations/{organizationName}/workspaces", consumes = "application/vnd.api+json")
    ObjectNode createWorkspace(@PathVariable("organizationName") String organizationName,
                               @RequestBody CreateWorkspaceVo vo,
                               @RequestHeader(value = HttpHeaders.AUTHORIZATION) String token) throws BusinessException;

    @GetMapping("/organizations/{organizationName}/workspaces/{workspaceName}")
    ObjectNode getWorkspace(@PathVariable("organizationName") String organizationName,
                            @PathVariable("workspaceName") String workspaceName,
                            @RequestHeader(value = HttpHeaders.AUTHORIZATION) String token) throws BusinessException;

    @RequestMapping(method = RequestMethod.POST, value = "/workspaces/{workspaceId}/configuration-versions", consumes = "application/vnd.api+json")
    ObjectNode createConfigurationVersion(@PathVariable("workspaceId") String workspaceId,
                                          @RequestBody CreateConfigurationVersionVo vo,
                                          @RequestHeader(value = HttpHeaders.AUTHORIZATION) String token) throws BusinessException;

    @GetMapping("/workspaces/{workspaceId}/runs")
    ObjectNode listRuns(@RequestParam("workspaceId") String workspaceId,
                        @RequestHeader(value = HttpHeaders.AUTHORIZATION) String token) throws BusinessException;

    @RequestMapping(method = RequestMethod.POST, value = "/runs", consumes = "application/vnd.api+json")
    ObjectNode createRun(@RequestBody CreateRunVo vo,
                         @RequestHeader(value = HttpHeaders.AUTHORIZATION) String token) throws BusinessException;

    @RequestMapping(method = RequestMethod.POST, value = "/runs/{runId}/actions/apply", consumes = "application/vnd.api+json")
    void applyRun(@PathVariable("runId") String runId,
                        @RequestHeader(value = HttpHeaders.AUTHORIZATION) String token) throws BusinessException;

    @Data
    public static class CreateRunVo {

        private CreateRunDataVo data = new CreateRunDataVo();

        @Data
        public static class CreateRunDataVo {
            private CreateRunAttributesVo attributes = new CreateRunAttributesVo();
            private String type = "runs";
            @JsonProperty("relationships")
            private CreateRunRelationshipsVo relationships = new CreateRunRelationshipsVo();

            @Data
            public static class CreateRunAttributesVo {
                @JsonProperty("auto-apply")
                private boolean autoApply = false;
                private String message;
            }

            @Data
            public static class CreateRunRelationshipsVo {

                private CreateRunWorkspaceVo workspace = new CreateRunWorkspaceVo();
                @JsonProperty("configuration-version")
                private CreateRunConfigurationVersionVo configurationVersion = new CreateRunConfigurationVersionVo();

                @Data
                public static class CreateRunWorkspaceVo {

                    private CreateRunWorkspaceDataVo data = new CreateRunWorkspaceDataVo();

                    @Data
                    public static class CreateRunWorkspaceDataVo {
                        private String type = "workspaces";
                        private String id;
                    }
                }

                @Data
                public static class CreateRunConfigurationVersionVo {

                    private CreateRunConfigurationVersionDataVo data = new CreateRunConfigurationVersionDataVo();

                    @Data
                    public static class CreateRunConfigurationVersionDataVo {
                        private String type = "configuration-versions";
                        private String id;
                    }

                }
            }
        }

    }

    @Data
    public static class CreateConfigurationVersionVo {

        private CreateConfigurationVersionDataVo data = new CreateConfigurationVersionDataVo();

        @Data
        public static class CreateConfigurationVersionDataVo {

            private String type = "configuration-versions";
            private CreateConfigurationVersionAttributesVo attributes = new CreateConfigurationVersionAttributesVo();

            @Data
            public static class CreateConfigurationVersionAttributesVo {
                @JsonProperty("auto-queue-runs")
                private boolean autoQueueRuns = false;
            }
        }
    }

    @Data
    public static class CreateWorkspaceVo {

        private CreateWorkspaceDataVo data = new CreateWorkspaceDataVo();

        @Data
        public static class CreateWorkspaceDataVo {
            private String type = "workspaces";
            private CreateWorkspaceAttributesVo attributes = new CreateWorkspaceAttributesVo();

            @Data
            public static class CreateWorkspaceAttributesVo {
                private String name;
            }
        }
    }

    @Data
    public static class CreateProjectVo {

        private CreateProjectDataVo data = new CreateProjectDataVo();

        @Data
        public static class CreateProjectDataVo {

            private String type= "projects";
            private CreateProjectAttributesVo attributes = new CreateProjectAttributesVo();

            @Data
            public static class CreateProjectAttributesVo {
                private String name;
            }
        }
    }
}

