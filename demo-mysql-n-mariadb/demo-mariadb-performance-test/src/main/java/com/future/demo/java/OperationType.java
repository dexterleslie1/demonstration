package com.future.demo.java;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 操作日志类型
 */
public enum OperationType {
    // todo i18n
    CreateUser("创建用户"),
    UpdatePassword("修改密码"),
    UserAuthorization("用户授权"),
    CreateRole("创建角色"),
    UpdateRole("修改角色"),
    DeleteRole("删除角色"),

    CreateLine("新增线路"),
    UpdateLine("修改线路"),
    DeleteLine("删除线路"),
    BindingLine("绑定线路"),
    UnbindingLine("解绑线路"),

    CreateNotice("新增公告"),
    CreateNoticeDetail("新增公告明细"),
    UpdateNotice("修改公告"),
    DeleteNotice("删除公告"),
    DeleteNoticeDetail("删除公告明细"),
    ResetNotice("弹窗置为已读"),

    UpdateOrder("修改订单"),
    ConfirmOrder("确认订单"),
    SearchConfirmOrder("搜索已确认订单"),
    SearchUnConfirmOrder("搜索未确认订单"),
    SearchOrderDetail("搜索订单商品"),
    QueryConfirmOrder("查询已确认订单"),
    QueryUnConfirmOrder("查询未确认订单"),
    AddGoods("新增商品"),
    QueryGoodsList("查询商品列表"),
    EditGoods("修改商品"),
    DeleteGoods("删除商品"),
    SearchGoods("搜索商品"),
    AddType("新增分类"),
    QueryType("分类查询"),
    EditType("修改分类"),
    DeleteType("删除分类"),
    SortType("分类排序"),
    QueryOrder("查询订单"),
    QueryOrderDetail("查询订单详情"),
    BatchUploadImages("批量上传商品图片"),
    EditImage("修改商品图片"),
    DeleteImages("删除商品图片"),
    ListImages("查询商品图片"),
    SearchImages("搜索商品图片"),
    BatchUploadCommonImages("批量上传公共图片"),
    DeleteCommonImages("删除公共图片"),
    ListCommonImages("查询公共图片"),
    ShopManageBatchAddGoods("批量新增店铺商品"),
    ShopManageListGoods("查询店铺商品"),
    ShopManageEditGoods("店铺修改商品"),
    ShopManageDelGoods("删除店铺商品"),
    ShopManageSortGoods("店铺商品排序"),
    AddShoppingCart("添加购物车"),
    DelShoppingCart("删除购物车"),
    ListShoppingCart("查询购物车"),
    SubmitShoppingCart("购物车结算"),

    CreateWarehouse("创建仓库"),
    UpdateWarehouse("修改仓库"),
    UpdateWarehosueStauts("停启用仓库"),
    DeleteWarehouse("删除仓库"),

    DeleterEnterWarehouseUndeterminedReceipt("删除未确认入库单"),
    EnterWarehouse("商品入库"),
    UpdateEnterWarehouse("修改入库商品"),
    DeleteGoodsInEnterWarehouse("删除入库单商品"),

    DeliverWarehouse("商品出库"),
    StubDeliverWarehouse("商品出库存根"),
    DeleteStubDeliverWarehouse("删除商品出库存根"),

    UpdateStore("修改库存"),
    InventoryStore("盘点库存"),
    AddInventoryStore("盘点新增"),
    SynInventoryStore("同步盘点库存"),

    LoginAsOther("冒充登录");

    private String description;

    OperationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    @ApiModel(value = "返回给前端的操作类型vo")
    @Data
    public static class OperationTypeVo {
        @ApiModelProperty(value = "操作类型代号，NOTE: 调用操作日志流水接口时提供此操作类型代号列表即可")
        private String code;
        @ApiModelProperty(value = "操作类型名称")
        private String name;
    }
}
