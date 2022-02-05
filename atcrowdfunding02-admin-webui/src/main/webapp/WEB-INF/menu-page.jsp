<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<%@include file="/WEB-INF/include-head.jsp" %>
<link rel="stylesheet" href="ztree/zTreeStyle.css"/>
<script type="text/javascript" src="ztree/jquery.ztree.all-3.5.min.js"></script>
<script type="text/javascript" src="crowd/my-menu.js"></script>
<script type="text/javascript">
    $(function () {
        generateTree();

        // 给“+”按钮，添加单击响应函数，打开添加节点的模态框
        $("#treeDemo").on("click", ".addBtn", function () {
            // 将当前按钮的id保存为全局变量pid，方便后面调用
            window.pid = this.id;
            // 打开模态框
            $("#menuAddModal").modal("show");
            // 关闭默认跳转行为
            return false;
        });

        // 添加节点模态框中保存按钮的单击事件
        $("#menuSaveBtn").click(function () {
            // 从输入框中获得name，并去掉前后空格
            var name = $.trim($("#menuAddModal [name=name]").val());
            // 从输入框中获得url，并去掉前后空格
            var url = $.trim($("#menuAddModal [name=url]").val());
            // 下面的选项中获得被选中的icon的值
            var icon = $("#menuAddModal [name=icon]:checked").val();

            $.ajax({
                url: "menu/save.json",
                type: "post",
                "data": {
                    name: name,
                    url: url,
                    icon: icon,
                    // 从全局变量获得该节点的父节点id
                    pid: window.pid
                },
                dataType: "json",
                success: function (response) {
                    if (response.result == "SUCCESS") {
                        layer.msg("操作成功！");

                        // 重新生成树形结构
                        generateTree();
                    }
                    if (response.result == "FAILED") {
                        layer.msg("操作失败！");
                    }
                },
                error: function (response) {
                    layer.msg(response.status + " " + response.statusText);
                }
            });

            // 关闭模态框
            $("#menuAddModal").modal("hide");

            // 清空模态框内的数据(通过模拟用户单击“重置”按钮)
            $("#menuResetBtn").click();
        });

        // 动态生成的修改按钮，单击打开修改的模态框
        $("#treeDemo").on("click", ".editBtn", function () {

            // 保存此按钮的id
            window.id = this.id;

            $("#menuEditModal").modal("show");

            // 要实现通过id拿到整个节点的信息，需要拿到zTreeObj
            var zTreeObj = $.fn.zTree.getZTreeObj("treeDemo");

            var key = "id";
            var value = window.id;

            // getNodeByParam，通过id得到当前的整个节点
            // 注意：id为treeNode的id，返回的就是那个treeNode
            var currentNode = zTreeObj.getNodeByParam(key, value);

            $("#menuEditModal [name=name]").val(currentNode.name);

            $("#menuEditModal [name=url]").val(currentNode.url);

            // 这里currentNode.icon其实是数组形式，利用这个值，放在[]中，传回val，就可以使相匹配的值回显在模态框中
            $("#menuEditModal [name=icon]").val([currentNode.icon]);

            return false;
        });

        // 更新节点模态框中保存按钮的单击事件
        $("#menuEditBtn").click(function () {

            var name = $("#menuEditModal [name=name]").val();

            var url = $("#menuEditModal [name=url]").val();

            var icon = $("#menuEditModal [name=icon]:checked").val();
            $.ajax({
                url: "menu/update.json",
                type: "post",
                "data": {
                    name: name,
                    url: url,
                    icon: icon,
                    // 从全局变量获得该节点的节点id
                    id: window.id
                },
                dataType: "json",
                success: function (response) {
                    if (response.result == "SUCCESS") {
                        layer.msg("操作成功！");

                        // 重新生成树形结构
                        generateTree();
                    }
                    if (response.result == "FAILED") {
                        layer.msg("操作失败！");
                    }
                },
                error: function (response) {
                    layer.msg(response.status + " " + response.statusText);
                }
            });
            // 关闭模态框
            $("#menuEditModal").modal("hide");
        });

        // 动态生成的删除按钮，单击打开删除的模态框
        $("#treeDemo").on("click", ".removeBtn", function () {

            // 保存此按钮的id
            window.id = this.id;

            $("#menuConfirmModal").modal("show");

            // 要实现通过id拿到整个节点的信息，需要拿到zTreeObj
            var zTreeObj = $.fn.zTree.getZTreeObj("treeDemo");

            var key = "id";
            var value = window.id;

            // getNodeByParam，通过id得到当前的整个节点
            // 注意：id为treeNode的id，返回的就是那个treeNode
            var currentNode = zTreeObj.getNodeByParam(key, value);

            // 获取当前节点的icon和name来作为提示信息
            var icon = currentNode.icon;
            var name = currentNode.name;

            // 回显-向id=removeNodeSpan的span标签添加html语句（显示图标与节点名）
            $("#removeNodeSpan").html("【<i class='"+icon+"'>"+name+"】</i>");

            return false;
        });

        // 删除节点模态框中确定按钮的单击事件
        $("#confirmBtn").click(function () {

            var name = $("#menuEditModal [name=name]").val();

            var url = $("#menuEditModal [name=url]").val();

            var icon = $("#menuEditModal [name=icon]:checked").val();
            $.ajax({
                url: "menu/remove.json",
                type: "post",
                "data": {
                    // 从全局变量获得该节点的节点id
                    id: window.id
                },
                dataType: "json",
                success: function (response) {
                    if (response.result == "SUCCESS") {
                        layer.msg("操作成功！");

                        // 重新生成树形结构
                        generateTree();
                    }
                    if (response.result == "FAILED") {
                        layer.msg("操作失败！");
                    }
                },
                error: function (response) {
                    layer.msg(response.status + " " + response.statusText);
                }
            });
            // 关闭模态框
            $("#menuConfirmModal").modal("hide");
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
                <div class="panel-heading"><i class="glyphicon glyphicon-th-list"></i> 权限菜单列表
                    <div style="float:right;cursor:pointer;" data-toggle="modal" data-target="#myModal">
                        <i class="glyphicon glyphicon-question-sign">
                        </i>
                    </div>
                </div>
                <div class="panel-body">
                    <ul id="treeDemo" class="ztree">
                        <%-- 显示树形结构依附于上面的ul --%>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>
<%@include file="/WEB-INF/modal-menu-add.jsp" %>
<%@include file="/WEB-INF/modal-menu-confirm.jsp" %>
<%@include file="/WEB-INF/modal-menu-edit.jsp" %>
</body>
</html>
