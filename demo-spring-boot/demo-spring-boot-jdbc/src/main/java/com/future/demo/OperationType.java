package com.future.demo;

/**
 * 操作日志类型
 */
public enum OperationType {
    // todo i18n
    CreateUser(1, "创建用户"),
    UpdatePassword(2, "修改密码"),
    UserAuthorization(3, "用户授权"),
    CreateRole(4, "创建角色"),
    UpdateRole(5, "修改角色"),
    DeleteRole(6, "删除角色"),

    CreateLine(7, "新增线路"),
    UpdateLine(8, "修改线路"),
    DeleteLine(9, "删除线路"),
    BindingLine(10, "绑定线路"),
    UnbindingLine(11, "解绑线路"),

    CreateNotice(12, "新增公告"),
    CreateNoticeDetail(13, "新增公告明细"),
    UpdateNotice(14, "修改公告"),
    DeleteNotice(15, "删除公告"),
    DeleteNoticeDetail(16, "删除公告明细"),
    ResetNotice(17, "弹窗置为已读"),

    UpdateOrder(18, "修改订单"),
    ConfirmOrder(19, "确认订单"),
    SearchConfirmOrder(20, "搜索已确认订单"),
    SearchUnConfirmOrder(21, "搜索未确认订单"),
    SearchOrderDetail(22, "搜索订单商品"),
    QueryConfirmOrder(23, "查询已确认订单"),
    QueryUnConfirmOrder(24, "查询未确认订单"),
    AddGoods(25, "新增商品"),
    QueryGoodsList(26, "查询商品列表"),
    EditGoods(27, "修改商品"),
    DeleteGoods(28, "删除商品"),
    SearchGoods(29, "搜索商品"),
    AddType(30, "新增分类"),
    QueryType(31, "分类查询"),
    EditType(32, "修改分类"),
    DeleteType(33, "删除分类"),
    SortType(34, "分类排序"),
    QueryOrder(35, "查询订单"),
    QueryOrderDetail(36, "查询订单详情"),
    BatchUploadImages(37, "批量上传商品图片"),
    EditImage(38, "修改商品图片"),
    DeleteImages(39, "删除商品图片"),
    ListImages(40, "查询商品图片"),
    SearchImages(41, "搜索商品图片"),
    BatchUploadCommonImages(42, "批量上传公共图片"),
    DeleteCommonImages(43, "删除公共图片"),
    ListCommonImages(44, "查询公共图片"),
    ShopManageBatchAddGoods(45, "批量新增店铺商品"),
    ShopManageListGoods(46, "查询店铺商品"),
    ShopManageEditGoods(47, "店铺修改商品"),
    ShopManageDelGoods(48, "删除店铺商品"),
    ShopManageSortGoods(49, "店铺商品排序"),
    AddShoppingCart(50, "添加购物车"),
    DelShoppingCart(51, "删除购物车"),
    ListShoppingCart(52, "查询购物车"),
    SubmitShoppingCart(53, "购物车结算"),

    CreateWarehouse(54, "创建仓库"),
    UpdateWarehouse(55, "修改仓库"),
    UpdateWarehosueStauts(56, "停启用仓库"),
    DeleteWarehouse(57, "删除仓库"),

    DeleterEnterWarehouseUndeterminedReceipt(58, "删除未确认入库单"),
    EnterWarehouse(59, "商品入库"),
    UpdateEnterWarehouse(60, "修改入库商品"),
    DeleteGoodsInEnterWarehouse(61, "删除入库单商品"),

    DeliverWarehouse(62, "商品出库"),
    StubDeliverWarehouse(63, "商品出库存根"),
    DeleteStubDeliverWarehouse(64, "删除商品出库存根"),

    UpdateStore(65, "修改库存"),
    InventoryStore(66, "盘点库存"),
    AddInventoryStore(67, "盘点新增"),
    SynInventoryStore(68, "同步盘点库存"),

    LoginAsOther(69, "冒充登录");

    private Integer value;
    private String description;

    OperationType(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getDescription() {
        return this.description;
    }

    /**
     * @param value
     * @return
     */
    public static OperationType fromValue(Integer value) {
        if (value == null) {
            return null;
        }

        for (OperationType operationType : OperationType.values()) {
            if (value.equals(operationType.value)) {
                return operationType;
            }
        }

        return null;
    }
}
