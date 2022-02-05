<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<%@include file="/WEB-INF/include-head.jsp" %>
<%--引入pagination的css--%>
<link href="css/pagination.css" rel="stylesheet"/>
<%--引入基于jquery的paginationjs--%>
<script type="text/javascript" src="jquery/jquery.pagination.js"></script>
<link rel="stylesheet" href="ztree/zTreeStyle.css"/>
<script type="text/javascript" src="ztree/jquery.ztree.all-3.5.min.js"></script>
<%--引入自定义的js代码--%>
<script type="text/javascript" src="crowd/my-role.js" charset="UTF-8"></script>
<script type="text/javascript">
    $(function () {
        // 设置各个全局变量，方便外部js文件中使用
        window.pageNum = 1;
        window.pageSize = 5;
        window.keyword = "";
        // 调用外部的生成分页的函数
        generatePage();

        // 给查询按钮绑定单击事件
        $("#searchBtn").click(function () {
            // 查询后的页面从第一页显示
            window.pageNum = 1;
            // 获取关键字数据给对应的全局变量
            window.keyword = $("#inputKeyword").val();
            // 调用分页函数刷新
            generatePage();
        });

        // 点击新增按钮打开模态框
        $("#showAddModalBtn").click(function () {
            $("#addRoleModal").modal("show");
        });

        // 给新增模态框的保存按钮绑定单击事件
        $("#saveRoleBtn").click(function () {
            // 获取用户在文本框中输入角色的名称
            // #addModal表示找到整个模态框
            // 空格表示后代元素中继续查找
            // [name=roleName] 表示匹配name属性roleName的元素
            var roleName = $.trim($("#addRoleModal [name=roleName]").val());

            // 发送Ajax请求
            $.ajax({
                url: "role/save.json",
                type: "post",
                data: {
                    name: roleName
                },
                success: function (response) {
                    var result = response.result;
                    // 成功则弹框输出
                    if (result == "SUCCESS") {
                        layer.msg("操作成功！");

                        // 进入最后一页 方便显示添加的内容
                        window.pageNum = 999;
                        // 重新生成分页
                        generatePage();
                    }

                    // 失败弹出原因
                    if (result == "FAILED") {
                        layer.msg("操作失败！" + response.message);
                    }
                },
                error: function () {
                    layer.msg(response.status + " " + response.statusText);
                }
            });

            // 关闭模态框
            $("#addRoleModal").modal("hide");

            // 清理模态框
            $("#addRoleModal [name=roleName]").val("");
        });

        // 传统的事件绑定方式只有那个在第一个页面有效，在翻页后失效，使用jQuery对象on（）函数解决
        //首先找到“动态生成”的元素附着的“静态”元素
        // on：第一个参数：事件类型
        // on：第二个参数：找到真正的绑定元素的选择器
        // on：第一个参数：事件的响应函数
        $("#rolePageTBody").on("click", ".pencilBtn", function () {
            // 打开模态框
            $("#editModal").modal("show");

            // 获取表格中当前行的角色名称
            var roleName = $(this).parent().prev().text();

            // 获取当前角色的id,为了发送Ajax请求，将它设置为全局变量
            window.roleId = this.id;

            // 使用roleName设置模态框中的文本框
            $("#editModal [name=roleName]").val(roleName);

            // 绑定更新，发送Ajax请求
            $("#updateRoleBtn").click(function () {
                // 从模态框的文本框中获得修改后的roleName
                var roleName = $("#editModal [name=roleName]").val();
                $.ajax({
                    url: "role/update.json",
                    type: "post",
                    data: {
                        id: window.roleId,	// 从全局遍历取得当前角色的id
                        name: roleName
                    },
                    dataType: "json",
                    success: function (response) {
                        if (response.result == "SUCCESS") {
                            layer.msg("操作成功！");
                            generatePage();
                        }
                        if (response.result == "FAILED")
                            layer.msg("操作失败" + response.message)
                    },
                    error: function (response) {
                        layer.msg("statusCode=" + response.status + " message=" + response.statusText);
                    }
                });

                // 关闭模态框
                $("#editModal").modal("hide");
            });
        });

        // 给确认删除按钮绑定单击事件
        $("#confirmRoleBtn").click(function () {
            // 将id信息封装到请求体
            var requestBody = JSON.stringify(window.roleIdArray);
            $.ajax({
                url: "role/remove.json",
                type: "post",
                data: requestBody,									// 将转换后的数据传给后端
                dataType: "json",
                contentType: "application/json;charset=UTF-8",	// 表明发送json格式数据
                success: function (response) {
                    if (response.result == "SUCCESS") {
                        layer.msg("操作成功！");
                        generatePage();
                    }
                    if (response.result == "FAILED")
                        layer.msg("操作失败" + response.message)
                },
                error: function (response) {
                    layer.msg("statusCode=" + response.status + " message=" + response.statusText);
                }
            });

            // 关闭模态框
            $("#confirmRoleModal").modal("hide");
        });

        // 给单击删除绑定响应函数
        $("#rolePageTBody").on("click", ".removeBtn", function () {
            // 通过x按钮删除时，只有一个角色，因此只需要建一个特殊的数组，存放单个对象即可
            var roleArray = [{
                id: this.id,
                name: $(this).parent().prev().text()
            }];
            // 调用删除静态框函数，传入roleArray
            showConfirmModal(roleArray);
        });

        // 给总的checked绑定单击响应函数
        $("#summaryBox").click(function () {
            // 获取当前多选框的自身状态
            var currentStatus = this.checked;

            // 用当前多选框的状态设置其他多选框
            $(".itemBox").prop("checked", currentStatus);
        });

        // 全选，全不选的反向操作
        $("#rolePageTBody").on("click", ".itemBox", function () {
            // 获取当前已选中的多选框的数量
            var checkedBoxCount = $(".itemBox:checked").length;
            // 获取全部checkBox的数量
            var checkBoxAll = $(".itemBox").length;
            // 两者比较设置总的多选框状态
            $("#summaryBox").prop("checked", checkedBoxCount == checkBoxAll);
        });

        // 给多选删除按钮绑定单击事件
        $("#batchRemoveBtn").click(function () {

            // 创建一个数组对象，用来存放后面获得的角色对象
            var roleArray = [];

            // 遍历被勾选的内容
            $(".itemBox:checked").each(function () {
                // 通过this引用当前遍历得到的多选框的id
                var roleId = this.id;

                // 通过DOM操作获取角色名称
                var roleName = $(this).parent().next().text();

                roleArray.push({
                    id: roleId,
                    name: roleName
                });
            });

            // 判断roleArray的长度是否为0
            if (roleArray.length == 0) {
                layer.msg("请至少选择一个来删除");
                return;
            }

            // 显示确认框
            showConfirmModal(roleArray);
        });

        // 弹出权限分配静态框
        $("#rolePageTBody").on("click", ".checkBtn", function () {
            window.roleId = this.id;
            // 打开模态框
            $("#assignModal").modal("show");
            // 生成树形结构
            fullAuthTree();
        });

        // 给提交权限修改绑定单击事件
        $("#assignBtn").click(function () {
            // 声明一个数组，用来存放被勾选的auth的id
            var authIdArray = [];

            // 拿到zTreeObj
            var zTreeObj = $.fn.zTree.getZTreeObj("authTreeDemo");

            // 通过getCheckedNodes方法拿到被选中的option信息
            var authArray = zTreeObj.getCheckedNodes();

            for (var i = 0; i < authArray.length; i++) {
                // 从被选中的auth中遍历得到每一个auth的id
                var authId = authArray[i].id;
                // 通过push方法将得到的id存入authIdArray
                authIdArray.push(authId);
            }
            var requestBody = {
                // 为了后端取值方便，两个数据都用数组格式存放，后端统一用List<Integer>获取
                roleId: [window.roleId],
                authIdList: authIdArray
            }
            requestBody = JSON.stringify(requestBody);

            // 发送Ajax请求保存
            $.ajax({
                url: "assign/do/role/assign/auth.json",
                type: "post",
                data: requestBody,
                contentType: "application/json;charset=UTF-8",
                dataType: "json",
                success: function (response) {
                    if (response.result == "SUCCESS"){
                        layer.msg("操作成功！");
                    }
                    if (response.result == "FAILED"){
                        layer.msg("操作失败！提示信息："+ response.message);
                    }
                },
                error: function (response) {
                    layer.msg(response.status + "  " + response.statusText);
                }
            });

            // 关闭模态框
            $("#assignModal").modal("hide");
        });
    });
</script>
<body>
<%@include file="/WEB-INF/include-nav.jsp" %>
<div class="container-fluid">
    <div class="row">
        <%@include file="/WEB-INF/include-sidebar.jsp" %>
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title"><i class="glyphicon glyphicon-th"></i> 数据列表</h3>
                </div>
                <div class="panel-body">
                    <form class="form-inline" role="form" style="float:left;">
                        <div class="form-group has-feedback">
                            <div class="input-group">
                                <div class="input-group-addon">查询条件</div>
                                <input class="form-control has-success" id="inputKeyword" type="text"
                                       placeholder="请输入查询条件">
                            </div>
                        </div>
                        <button id="searchBtn" type="button" class="btn btn-warning"><i
                                class="glyphicon glyphicon-search"></i> 查询
                        </button>
                    </form>
                    <button type="button" id="batchRemoveBtn" class="btn btn-danger"
                            style="float:right;margin-left:10px;"><i
                            class=" glyphicon glyphicon-remove"></i> 删除
                    </button>
                    <button type="button" class="btn btn-primary"
                            style="float:right;" id="showAddModalBtn">
                        <i class="glyphicon glyphicon-plus"></i> 新增
                    </button>
                    <br>
                    <hr style="clear:both;">
                    <div class="table-responsive">
                        <table class="table  table-bordered">
                            <thead>
                            <tr>
                                <th width="30">#</th>
                                <th width="30"><input id="summaryBox" type="checkbox"></th>
                                <th>名称</th>
                                <th width="100">操作</th>
                            </tr>
                            </thead>
                            <%--  tbody的id=rolePageTBody,用于绑定on()函数 --%>
                            <tbody id="rolePageTBody">
                            </tbody>
                            <tfoot>
                            <tr>
                                <td colspan="6" align="center">
                                    <div id="Pagination" class="pagination"><!-- 这里显示分页 --></div>
                                </td>
                            </tr>
                            </tfoot>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<%@include file="/WEB-INF/modal-role-add.jsp" %>
<%@include file="/WEB-INF/modal-role-update.jsp" %>
<%@include file="/WEB-INF/modal-role-confirm.jsp" %>
<%@include file="/WEB-INF/modal-role-assign-auth.jsp" %>
</body>
</html>
