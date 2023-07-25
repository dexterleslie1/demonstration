package com.future.demo.authing;

import cn.authing.core.graphql.GraphQLException;
import cn.authing.core.mgmt.ManagementClient;
import cn.authing.core.types.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AuthingCNTests {
    @Test
    public void test() throws Exception {
        String userpoolId = "";
        String userpoolSecret = "";
        ManagementClient managementClient = new ManagementClient(userpoolId, userpoolSecret);

        String userGroupId = "group-1";
        String userGroupName = "分组1";
        String userGroupDescription = "单元测试自动创建【" + userGroupName + "】";

        CommonMessage commonMessage;

        // 删除所有应用
        List<Application> applicationList = managementClient.application().list().execute();
        if(applicationList!=null && applicationList.size()>0) {
            for(Application application : applicationList) {
                managementClient.application().delete(application.getId()).execute();
            }
        }

        /*
        用户分组管理
         */
        // 删除用户分组
        try {
            managementClient.group().delete(userGroupId).execute();
        } catch (GraphQLException ex) {
            // 可能出现分组不存在异常，忽略此错误
        }

        // 创建用户分组
        Group userGroup = managementClient.group().create(new CreateGroupParam(userGroupId, userGroupName).withDescription(userGroupDescription)).execute();

        // 查询分组详细信息
        Group userGroupTemporary = managementClient.group().detail(userGroupId).execute();
        Assert.assertEquals(userGroupId, userGroupTemporary.getCode());
        Assert.assertEquals(userGroupName, userGroupTemporary.getName());
        Assert.assertEquals(userGroupDescription, userGroupTemporary.getDescription());
        Assert.assertNull(userGroupTemporary.getAuthorizedResources());
        Assert.assertNull(userGroupTemporary.getUsers());

        // 查询当前开发者所有分组列表
        PaginatedGroups paginatedGroups = managementClient.group().list(new GroupsParam()).execute();
        Assert.assertEquals(1, paginatedGroups.getTotalCount());

//        // TODO 未清楚
//        // 获取分组被授权的所有资源列表
//        userGroupTemporary =
//                managementClient.group()
//                        .listAuthorizedResources(new ListGroupAuthorizedResourcesParam(userGroup.getCode()).withNamespace("100001"))
//                        .execute();
//        Assert.assertEquals(0, userGroupTemporary.getAuthorizedResources().getTotalCount());

        /*
        权限分组管理
         */
        // 创建权限分组
        String namespaceId = "namespace-1";
        String namespaceName = "权限分组1";
        String namespaceDes = "单元测试自动创建【" + namespaceName + "】";
        try {
            // TODO 无法删除namespace
            managementClient.acl().deleteNamespace(namespaceId).execute();
        } catch (IOException ex) {
            //
        }
        ResourceNamespace resourceNamespace = managementClient.acl().createNamespace(namespaceId, namespaceName, namespaceDes).execute();
        if(resourceNamespace==null) {
            Pagination<ResourceNamespace> resourceNamespacePagination = managementClient.acl().listNamespaces(1).execute();
            resourceNamespace = resourceNamespacePagination.getList().get(resourceNamespacePagination.getList().size()-1);
        }

        // 获取当前开发者权限分组列表
        Pagination<ResourceNamespace> pagination = managementClient.acl().listNamespaces().execute();
        Assert.assertEquals(3, pagination.getList().size());
        Assert.assertEquals(resourceNamespace.getCode(), pagination.getList().get(pagination.getList().size()-1).getCode());

        // 在指定权限分组中创建资源
        String resourceId = "resource-1";
        String resourceDes = "单元测试自动创建【" + resourceId + "】";
        List<IAction> iActionList = Arrays.asList(new IAction(resourceId + ":read", "读操作"), new IAction(resourceId + ":write", "写操作"));
        managementClient.acl().deleteResource(resourceId, resourceNamespace.getCode()).execute();
        IResourceResponse resource = managementClient.acl()
                .createResource(new IResourceDto(resourceId, ResourceType.DATA, resourceDes, iActionList, resourceNamespace.getCode()))
                .execute();

        // 获取指定权限分组资源列表
        Pagination<IResourceResponse> iResourceResponsePagination = managementClient.acl().listResources(resourceNamespace.getCode()).execute();
        Assert.assertEquals(1, iResourceResponsePagination.getTotalCount());
        Assert.assertEquals(resourceId, iResourceResponsePagination.getList().get(0).getCode());
        Assert.assertEquals(iActionList.get(0).getName(), iResourceResponsePagination.getList().get(0).getActions().get(0).getName());
        Assert.assertEquals(iActionList.get(1).getName(), iResourceResponsePagination.getList().get(0).getActions().get(1).getName());

        /*
        角色管理
         */
        String roleId = "role-1";
        try {
            managementClient.roles().delete(roleId).execute();
        } catch (GraphQLException ex) {
            // 角色不存在异常
        }
        // 默认在default权限分组中创建角色
        managementClient.roles().create(roleId).execute();
        Role role = managementClient.roles().findByCode(new RoleParam(roleId)).execute();
        Assert.assertEquals(roleId, role.getCode());
        Assert.assertEquals("default", role.getNamespace());

        // 查询当前开发者角色列表
        // TODO namspace过滤没有作用
        PaginatedRoles paginatedRoles = managementClient.roles().list(new RolesParam("default")).execute();
        Assert.assertEquals(1, paginatedRoles.getTotalCount());
        Assert.assertEquals(role.getCode(), paginatedRoles.getList().get(0).getCode());

        // TODO 不清楚policy格式
//        // 批量添加策略
//        commonMessage = managementClient.roles().addPolicies(role.getCode(), Arrays.asList("policy1", "policy2")).execute();
//        if(commonMessage.getCode()!=null && commonMessage.getCode()!=200) {
//            throw new Exception("抛出异常，代号：" + commonMessage.getCode() + "，错误：" + commonMessage.getMessage());
//        }
//
//        // 获取策略列表
//        PaginatedPolicyAssignments paginatedPolicyAssignments = managementClient.roles().listPolicies(role.getCode()).execute();

        /*
        架构管理
         */
        // 创建公司/部门
        String orgIdCompany = "org-1";
        String orgNameCompany = "公司1";
        String orgDesCompany = "单元测试自动创建【" + orgNameCompany + "】";
        String orgIdDept1 = "org-11";
        String orgNameDept1 = "部门1";
        String orgDesDept1 = "单元测试自动创建【" + orgNameDept1 + "】";
        String orgIdDept11 = "org-111";
        String orgNameDept11 = "部门11";
        String orgDesDept11 = "单元测试自动创建【" + orgNameDept11 + "】";
        String orgIdDept2 = "org-12";
        String orgNameDept2 = "部门2";
        String orgDesDept2 = "单元测试自动创建【" + orgNameDept2 + "】";

        // 删除所有公司(authing称之为机构)
        PaginatedOrgs paginatedOrgs = managementClient.org().list(new OrgsParam()).execute();
        if(paginatedOrgs.getTotalCount()>0) {
            for (Org org : paginatedOrgs.getList()) {
                commonMessage = managementClient.org().deleteById(org.getId()).execute();
                if(commonMessage.getCode()!=null && commonMessage.getCode()!=200) {
                    throw new Exception("抛出异常，代号：" + commonMessage.getCode() + "，错误：" + commonMessage.getMessage());
                }
            }
        }

        Org orgCompany = managementClient.org().create(new CreateOrgParam(orgNameCompany, orgIdCompany, orgDesCompany)).execute();
        Node orgDept1 =
                managementClient.org()
                        .addNode(new AddNodeV2Param(orgCompany.getId(), orgCompany.getRootNode().getId(), orgNameDept1).withCode(orgIdDept1).withDescription(orgDesDept1))
                        .execute();
        Node orgDept11 =
                managementClient.org()
                        .addNode(new AddNodeV2Param(orgCompany.getId(), orgDept1.getId(), orgNameDept11).withCode(orgIdDept11).withDescription(orgDesDept11))
                        .execute();
        Node orgDept2 =
                managementClient.org()
                        .addNode(new AddNodeV2Param(orgCompany.getId(), orgCompany.getRootNode().getId(), orgNameDept2).withCode(orgIdDept2).withDescription(orgDesDept2))
                        .execute();

        // TODO 未清楚
        // 根据部门ID获取被授权的所有资源列表
//        Node node = managementClient.org().listAuthorizedResourcesByNodeId(new ListNodeByIdAuthorizedResourcesParam(orgDept11.getId()).withNamespace("default")).execute();
//        Assert.assertNull(node.getAuthorizedResources());

        String phone = "13560189480";
        String password = "123456";

        /*
        用户管理
         */
        // 判断用户是否存在
        boolean isUserExists = managementClient.users().exists(new IsUserExistsParam().withPhone(phone)).execute();

        // 删除用户
        if(isUserExists) {
            // 查找用户
            User user = managementClient.users().find(new FindUserParam().withPhone(phone)).execute();
            commonMessage = managementClient.users().delete(user.getId()).execute();
            if(commonMessage.getCode()!=null && commonMessage.getCode()!=200) {
                throw new Exception("抛出异常，代号：" + commonMessage.getCode() + "，错误：" + commonMessage.getMessage());
            }
        }

        // 创建用户
        User user = managementClient.users().create(new CreateUserInput().withPhone(phone).withPassword(password)).execute();
        Assert.assertNotNull(user);

        // 修改用户邮箱
        user = managementClient.users().update(user.getId(), new UpdateUserInput().withEmail(phone + "@gmail.com")).execute();
        Assert.assertNotNull(user);

        // 通过ID查询用户信息
        User userTemporary = managementClient.users().detail(user.getId()).execute();
        Assert.assertEquals(user.getId(), userTemporary.getId());
        Assert.assertEquals(user.getPhone(), userTemporary.getPhone());
        Assert.assertEquals(user.getEmail(), userTemporary.getEmail());

        // 添加用户到指定部门
        managementClient.org().addMembers(orgDept11.getId(), Arrays.asList(user.getId())).execute();
        // 获取指定部门成员列表
        Node node = managementClient.org().listMembers(new NodeByIdWithMembersParam(orgDept11.getId())).execute();
        Assert.assertEquals(1, node.getUsers().getTotalCount());
        Assert.assertEquals(user.getId(), node.getUsers().getList().get(0).getId());

        // 添加用户到指定分组
        commonMessage = managementClient.group().addUsers(new AddUserToGroupParam(Arrays.asList(user.getId())).withCode(userGroup.getCode())).execute();
        if(commonMessage.getCode()!=null && commonMessage.getCode()!=200) {
            throw new Exception("抛出异常，代号：" + commonMessage.getCode() + "，错误：" + commonMessage.getMessage());
        }

        // 查询指定分组包含用户列表
        PaginatedUsers paginatedUsers = managementClient.group().listUsers(userGroup.getCode()).execute();
        Assert.assertEquals(1, paginatedGroups.getTotalCount());
        Assert.assertEquals(user.getId(), paginatedUsers.getList().get(0).getId());

        // 查询用户所属分组列表
        paginatedGroups = managementClient.users().listGroups(user.getId()).execute();
        Assert.assertEquals(1, paginatedGroups.getTotalCount());
        Assert.assertEquals(userGroup.getCode(), paginatedGroups.getList().get(0).getCode());
        paginatedGroups = managementClient.group().list(new GroupsParam().withUserId(user.getId())).execute();
        Assert.assertEquals(1, paginatedGroups.getTotalCount());
        Assert.assertEquals(userGroup.getCode(), paginatedGroups.getList().get(0).getCode());

        // 获取用户策略列表
        PaginatedPolicyAssignments paginatedPolicyAssignments =
                managementClient.users().listPolicies(user.getId()).execute();
        Assert.assertEquals(0, paginatedPolicyAssignments.getTotalCount());

        // TODO 报错
//        // 给用户添加策略
//        List<String> policyList = Arrays.asList("policy1", "policy2");
//        commonMessage = managementClient.users().addPolicies(user.getId(), policyList).execute();
//        if(commonMessage.getCode()!=null && commonMessage.getCode()!=200) {
//            throw new Exception("抛出异常，代号：" + commonMessage.getCode() + "，错误：" + commonMessage.getMessage());
//        }

        // 获取用户所在组织机构列表
        List<List<Org>> orgListList = managementClient.users().listOrgs(user.getId()).execute();
        Assert.assertEquals(4, orgListList.get(0).size());
        Assert.assertEquals(orgDept11.getId(), orgListList.get(0).get(3).getId());
        Assert.assertEquals(orgDept1.getId(), orgListList.get(0).get(2).getId());
        Assert.assertEquals(orgCompany.getRootNode().getId(), orgListList.get(0).get(1).getId());
        Assert.assertEquals(orgCompany.getId(), orgListList.get(0).get(0).getId());

        // 允许某个用户对某个资源进行某个操作，默认授权到default权限组
        String action = "r1:del";
        commonMessage = managementClient.acl().allow(resource.getCode() + ":*", action, user.getId()).execute();
        if(commonMessage.getCode()!=null && commonMessage.getCode()!=200) {
            throw new Exception("抛出异常，代号：" + commonMessage.getCode() + "，错误：" + commonMessage.getMessage());
        }

        // 判断某个用户是否对某个资源有某个操作权限
        boolean isAllowed = managementClient.acl().isAllowed(user.getId(), resource.getCode() + ":123", action).execute();
        Assert.assertTrue(isAllowed);

        // 获取用户被授权的所有资源列表
        PaginatedAuthorizedResources paginatedAuthorizedResources =
                managementClient.users().listAuthorizedResources(new ListUserAuthorizedResourcesParam(user.getId(), "default")).execute();
        Assert.assertEquals(1, paginatedAuthorizedResources.getTotalCount());
        Assert.assertEquals(resource.getCode() + ":*", paginatedAuthorizedResources.getList().get(0).getCode());
        Assert.assertEquals(1, paginatedAuthorizedResources.getList().get(0).getActions().size());
        Assert.assertEquals(action, paginatedAuthorizedResources.getList().get(0).getActions().get(0));

        // 以角色为责任主体添加、删除用户
        commonMessage = managementClient.roles().addUsers(role.getCode(), Arrays.asList(user.getId())).execute();
        if(commonMessage.getCode()!=null && commonMessage.getCode()!=200) {
            throw new Exception("抛出异常，代号：" + commonMessage.getCode() + "，错误：" + commonMessage.getMessage());
        }
        // 获取指定角色用户列表
        paginatedUsers = managementClient.roles().listUsers(role.getCode()).execute();
        Assert.assertEquals(1, paginatedUsers.getTotalCount());
        Assert.assertEquals(user.getId(), paginatedUsers.getList().get(0).getId());
        // 删除指定角色中用户
        commonMessage = managementClient.roles()
                .removeUsers(new RevokeRoleParam().withNamespace("default").withRoleCode(role.getCode()).withUserIds(Arrays.asList(user.getId())))
                .execute();
        if(commonMessage.getCode()!=null && commonMessage.getCode()!=200) {
            throw new Exception("抛出异常，代号：" + commonMessage.getCode() + "，错误：" + commonMessage.getMessage());
        }

        // 以用户为责任主体添加、删除角色
        commonMessage = managementClient.users().addRoles(user.getId(), Arrays.asList(role.getCode()), "default").execute();
        if(commonMessage!=null && commonMessage.getCode()!=null && commonMessage.getCode()!=200) {
            throw new Exception("抛出异常，代号：" + commonMessage.getCode() + "，错误：" + commonMessage.getMessage());
        }
        paginatedRoles = managementClient.users().listRoles(user.getId(), "default").execute();
        Assert.assertEquals(1, paginatedRoles.getTotalCount());
        Assert.assertEquals(role.getCode(), paginatedRoles.getList().get(0).getCode());
        commonMessage = managementClient.users().removeRoles(user.getId(), Arrays.asList(role.getCode()), "default").execute();
        if(commonMessage!=null && commonMessage.getCode()!=null && commonMessage.getCode()!=200) {
            throw new Exception("抛出异常，代号：" + commonMessage.getCode() + "，错误：" + commonMessage.getMessage());
        }

        // 资源授权
        // 授予用户操作资源的权限
        AuthorizeResourceOptInput authorizeResourceOptInput = new AuthorizeResourceOptInput(PolicyAssignmentTargetType.USER, user.getId(), Arrays.asList("user-op1"));
        commonMessage = managementClient.acl().authorizeResource("default", resource.getCode() + ":*", Arrays.asList(authorizeResourceOptInput)).execute();
        if(commonMessage!=null && commonMessage.getCode()!=null && commonMessage.getCode()!=200) {
            throw new Exception("抛出异常，代号：" + commonMessage.getCode() + "，错误：" + commonMessage.getMessage());
        }
        // 授予角色操作资源的权限
        authorizeResourceOptInput = new AuthorizeResourceOptInput(PolicyAssignmentTargetType.ROLE, role.getCode(), Arrays.asList("role-op1"));
        commonMessage = managementClient.acl().authorizeResource("default", resource.getCode() + ":*", Arrays.asList(authorizeResourceOptInput)).execute();
        if(commonMessage!=null && commonMessage.getCode()!=null && commonMessage.getCode()!=200) {
            throw new Exception("抛出异常，代号：" + commonMessage.getCode() + "，错误：" + commonMessage.getMessage());
        }
        // 授予用户组操作资源的权限
        authorizeResourceOptInput = new AuthorizeResourceOptInput(PolicyAssignmentTargetType.GROUP, userGroup.getCode(), Arrays.asList("userGroup-op1"));
        commonMessage = managementClient.acl().authorizeResource("default", resource.getCode() + ":*", Arrays.asList(authorizeResourceOptInput)).execute();
        if(commonMessage!=null && commonMessage.getCode()!=null && commonMessage.getCode()!=200) {
            throw new Exception("抛出异常，代号：" + commonMessage.getCode() + "，错误：" + commonMessage.getMessage());
        }
        // 授予组织操作资源的权限
        authorizeResourceOptInput = new AuthorizeResourceOptInput(PolicyAssignmentTargetType.ORG, orgDept11.getId(), Arrays.asList("orgDept-op1"));
        commonMessage = managementClient.acl().authorizeResource("default", resource.getCode() + ":*", Arrays.asList(authorizeResourceOptInput)).execute();
        if(commonMessage!=null && commonMessage.getCode()!=null && commonMessage.getCode()!=200) {
            throw new Exception("抛出异常，代号：" + commonMessage.getCode() + "，错误：" + commonMessage.getMessage());
        }

        // 获取用户被授权的所有资源列表
        paginatedAuthorizedResources =
                managementClient.users().listAuthorizedResources(new ListUserAuthorizedResourcesParam(user.getId(), "default")).execute();
        Assert.assertEquals(1, paginatedAuthorizedResources.getTotalCount());
        Assert.assertEquals(resource.getCode() + ":*", paginatedAuthorizedResources.getList().get(0).getCode());
        Assert.assertEquals(ResourceType.valueOf(resource.getType()), paginatedAuthorizedResources.getList().get(0).getType());
        Assert.assertEquals(4, paginatedAuthorizedResources.getList().get(0).getActions().size());
        Assert.assertTrue(paginatedAuthorizedResources.getList().get(0).getActions().contains(action));
        Assert.assertTrue(paginatedAuthorizedResources.getList().get(0).getActions().contains("user-op1"));
        Assert.assertTrue(paginatedAuthorizedResources.getList().get(0).getActions().contains("userGroup-op1"));
        Assert.assertTrue(paginatedAuthorizedResources.getList().get(0).getActions().contains("orgDept-op1"));
        Assert.assertFalse(paginatedAuthorizedResources.getList().get(0).getActions().contains("role-op1"));
        // 获取角色被授权的所有资源列表
        paginatedAuthorizedResources =
                managementClient.roles().listAuthorizedResources(new ListRoleAuthorizedResourcesParam(role.getCode(), "default")).execute();
        Assert.assertEquals(1, paginatedAuthorizedResources.getTotalCount());
        Assert.assertEquals(resource.getCode() + ":*", paginatedAuthorizedResources.getList().get(0).getCode());
        Assert.assertEquals(ResourceType.valueOf(resource.getType()), paginatedAuthorizedResources.getList().get(0).getType());
        Assert.assertEquals("role-op1", paginatedAuthorizedResources.getList().get(0).getActions().get(0));
        // 获取分组被授权的所有资源列表
        userGroupTemporary =
                managementClient.group().listAuthorizedResources(new ListGroupAuthorizedResourcesParam(userGroup.getCode(), "default")).execute();
        paginatedAuthorizedResources = userGroupTemporary.getAuthorizedResources();
        Assert.assertEquals(1, paginatedAuthorizedResources.getTotalCount());
        Assert.assertEquals(resource.getCode() + ":*", paginatedAuthorizedResources.getList().get(0).getCode());
        Assert.assertEquals(ResourceType.valueOf(resource.getType()), paginatedAuthorizedResources.getList().get(0).getType());
        Assert.assertEquals("userGroup-op1", paginatedAuthorizedResources.getList().get(0).getActions().get(0));
        // TODO 没有数据返回
//        // 获取部门被授权的所有资源列表
//        Node nodeTemporary =
//                managementClient.org().listAuthorizedResourcesByNodeId(new ListNodeByIdAuthorizedResourcesParam(orgDept11.getId(), "default")).execute();
//        paginatedAuthorizedResources = nodeTemporary.getAuthorizedResources();
//        Assert.assertEquals(1, paginatedAuthorizedResources.getTotalCount());
//        Assert.assertEquals(resource.getCode() + ":*", paginatedAuthorizedResources.getList().get(0).getCode());
//        Assert.assertEquals(ResourceType.valueOf(resource.getType()), paginatedAuthorizedResources.getList().get(0).getType());
//        Assert.assertEquals("orgDept-op1", paginatedAuthorizedResources.getList().get(0).getActions().get(0));
        // 获取具备某些资源操作权限的主体
        // 用户
        PaginatedAuthorizedTargets paginatedAuthorizedTargets =
                managementClient.acl()
                        .getAuthorizedTargets(
                                new AuthorizedTargetsParam(
                                        "default",
                                        ResourceType.DATA,
                                        resource.getCode())
                                        .withTargetType(PolicyAssignmentTargetType.USER))
                        .execute();
        Assert.assertEquals(1, (int)paginatedAuthorizedTargets.getTotalCount());
        Assert.assertEquals(PolicyAssignmentTargetType.USER, paginatedAuthorizedTargets.getList().get(0).getTargetType());
        Assert.assertEquals(2, paginatedAuthorizedTargets.getList().get(0).getActions().size());
        Assert.assertTrue(paginatedAuthorizedTargets.getList().get(0).getActions().contains("del"));
        Assert.assertTrue(paginatedAuthorizedTargets.getList().get(0).getActions().contains("user-op1"));
        // 角色
        paginatedAuthorizedTargets =
                managementClient.acl()
                        .getAuthorizedTargets(
                                new AuthorizedTargetsParam(
                                        "default",
                                        ResourceType.DATA,
                                        resource.getCode())
                                        .withTargetType(PolicyAssignmentTargetType.ROLE))
                        .execute();
        Assert.assertEquals(1, (int)paginatedAuthorizedTargets.getTotalCount());
        Assert.assertEquals(PolicyAssignmentTargetType.ROLE, paginatedAuthorizedTargets.getList().get(0).getTargetType());
        Assert.assertEquals(1, paginatedAuthorizedTargets.getList().get(0).getActions().size());
        Assert.assertTrue(paginatedAuthorizedTargets.getList().get(0).getActions().contains("role-op1"));
        // 组织架构
        paginatedAuthorizedTargets =
                managementClient.acl()
                        .getAuthorizedTargets(
                                new AuthorizedTargetsParam(
                                        "default",
                                        ResourceType.DATA,
                                        resource.getCode())
                                        .withTargetType(PolicyAssignmentTargetType.ORG))
                        .execute();
        Assert.assertEquals(1, (int)paginatedAuthorizedTargets.getTotalCount());
        Assert.assertEquals(PolicyAssignmentTargetType.ORG, paginatedAuthorizedTargets.getList().get(0).getTargetType());
        Assert.assertEquals(1, paginatedAuthorizedTargets.getList().get(0).getActions().size());
        Assert.assertTrue(paginatedAuthorizedTargets.getList().get(0).getActions().contains("orgDept-op1"));
        // 用户组
        paginatedAuthorizedTargets =
                managementClient.acl()
                        .getAuthorizedTargets(
                                new AuthorizedTargetsParam(
                                        "default",
                                        ResourceType.DATA,
                                        resource.getCode())
                                        .withTargetType(PolicyAssignmentTargetType.GROUP))
                        .execute();
        Assert.assertEquals(1, (int)paginatedAuthorizedTargets.getTotalCount());
        Assert.assertEquals(PolicyAssignmentTargetType.GROUP, paginatedAuthorizedTargets.getList().get(0).getTargetType());
        Assert.assertEquals(1, paginatedAuthorizedTargets.getList().get(0).getActions().size());
        Assert.assertTrue(paginatedAuthorizedTargets.getList().get(0).getActions().contains("userGroup-op1"));

        // 获取用户所在部门
        Pagination<UserDepartment> userDepartmentPagination = managementClient.users().listDepartment(user.getId()).execute();
        Assert.assertEquals(1, userDepartmentPagination.getTotalCount());
        Assert.assertEquals(orgDept11.getId(), userDepartmentPagination.getList().get(0).getDepartment().getId());

        /*
        管理应用
         */
        // 创建应用
        String appId = "app-1";
        String appName = "应用1";
        managementClient.application().delete(appId).execute();
        Application application = managementClient.application().create(new CreateAppParams(appName, appId, Arrays.asList("www.baidu.com"))).execute();

        // 创建资源
        String resourceId2 = "resource-2";
        List<IAction> iActionListResource2 = Arrays.asList(
                new IAction(resourceId2 + ":create", "创建"),
                new IAction(resourceId2 + ":del", "删除"));
        managementClient.application().deleteResource(application.getId(), resourceId2).execute();
        managementClient.application().createResource(application.getId(), new ResourceOptionsParams(resourceId2, ResourceType.DATA, iActionListResource2)).execute();
        // 获取资源列表
        iResourceResponsePagination = managementClient.application().listResources(new ListResourcesParams(application.getId())).execute();
        Assert.assertEquals(1, iResourceResponsePagination.getTotalCount());
        Assert.assertEquals(resourceId2, iResourceResponsePagination.getList().get(0).getCode());
        Assert.assertEquals(ResourceType.DATA, ResourceType.valueOf(iResourceResponsePagination.getList().get(0).getType()));
        Assert.assertEquals(2, iResourceResponsePagination.getList().get(0).getActions().size());
        Assert.assertEquals(resourceId2 + ":create", iResourceResponsePagination.getList().get(0).getActions().get(0).getName());
        Assert.assertEquals(resourceId2 + ":del", iResourceResponsePagination.getList().get(0).getActions().get(1).getName());

        // TODO 没有作用
        // 更改默认应用访问策略
        managementClient.application().updateDefaultAccessPolicy(application.getId(), DefaultStrategy.DENY_ALL).execute();

////        // 配置「拒绝主体（用户、角色、分组、组织机构节点）访问应用」的控制策略(在应用->访问授权->应用访问控制生成一条关于拒绝用户的应用访问控制策略)
////        managementClient.application().denyAccess(application.getId(), new DenyAccessParams(TargetTypeEnum.USER, Arrays.asList(user.getId()))).execute();
        // 配置「允许主体（用户、角色、分组、组织机构节点）访问应用」的控制策略(在应用->访问授权->应用访问控制生成一条关于允许用户的应用访问控制策略)
        managementClient.application().allowAccess(application.getId(), new IAccessPolicyParams(TargetTypeEnum.USER, Arrays.asList(user.getId()))).execute();
////        // 停用应用访问控制策略(应用访问控制策略是否生效)
////        managementClient.application().disableAccessPolicy(application.getId(), new IAccessPolicyParams(TargetTypeEnum.USER, Arrays.asList(user.getId()))).execute();
////        // 启用应用访问控制策略(应用访问控制策略是否生效)
////        managementClient.application().enableAccessPolicy(application.getId(), new IAccessPolicyParams(TargetTypeEnum.USER, Arrays.asList(user.getId()))).execute();
//
        // TODO 下面数据没有主体对象信息
        // 获取应用访问控制策略
        Pagination<IApplicationAccessPolicies> applicationAccessPoliciesPagination =
                managementClient.application().getAccessPolicies(application.getId()).execute();
        Assert.assertEquals(1, applicationAccessPoliciesPagination.getTotalCount());

        // 在应用下创建角色
        String roleId2 = "role-2";
        String roleDescription2 = "单元测试自动创建【" + roleId2 + "】";
        Role role2 = managementClient.application().createRole(application.getId(), new CreateRoleParams(roleId2, roleDescription2)).execute();

        // TODO roleId断言错误
//        // 获取应用下的角色列表
//        paginatedRoles = managementClient.application().getRoles(application.getId()).execute();
//        Assert.assertEquals(1, paginatedRoles.getTotalCount());
//        Assert.assertEquals(role2.getCode(), paginatedRoles.getList().get(0).getCode());

        // 应用下的角色添加用户
        commonMessage = managementClient.application().addUsersToRole(application.getId(), role2.getCode(), Arrays.asList(user.getId())).execute();
        if(commonMessage!=null && commonMessage.getCode()!=null && commonMessage.getCode()!=200) {
            throw new Exception("抛出异常，代号：" + commonMessage.getCode() + "，错误：" + commonMessage.getMessage());
        }

        // 获取应用下角色的用户列表
        paginatedUsers = managementClient.application().getUsersByRoleCode(application.getId(), role2.getCode()).execute();
        Assert.assertEquals(1, paginatedUsers.getTotalCount());
        Assert.assertEquals(user.getId(), paginatedUsers.getList().get(0).getId());

        // 给role2授权
        authorizeResourceOptInput = new AuthorizeResourceOptInput(PolicyAssignmentTargetType.ROLE, role2.getCode(), Arrays.asList("role2-op1"));
        commonMessage = managementClient.acl().authorizeResource(application.getId(), resourceId2 + ":*", Arrays.asList(authorizeResourceOptInput)).execute();
        if(commonMessage!=null && commonMessage.getCode()!=null && commonMessage.getCode()!=200) {
            throw new Exception("抛出异常，代号：" + commonMessage.getCode() + "，错误：" + commonMessage.getMessage());
        }
        // 获取应用下角色被授权的所有资源列表
        paginatedAuthorizedResources = managementClient.application().listAuthorizedResourcesByRole(application.getId(), role2.getCode(), ResourceType.DATA).execute();
        Assert.assertEquals(1, paginatedAuthorizedResources.getTotalCount());
        Assert.assertEquals(resourceId2 + ":*", paginatedAuthorizedResources.getList().get(0).getCode());
        Assert.assertEquals(ResourceType.DATA, paginatedAuthorizedResources.getList().get(0).getType());
        Assert.assertEquals("role2-op1", paginatedAuthorizedResources.getList().get(0).getActions().get(0));
    }
}
