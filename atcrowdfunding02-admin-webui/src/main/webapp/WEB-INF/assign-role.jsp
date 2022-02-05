<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="zh-CN">
<%@include file="/WEB-INF/include-head.jsp" %>
<script type="text/javascript">
    $(function () {
        // 点击右箭头，将左边角色追加在右边
        $("#toRightBtn").click(function () {
            $("select:eq(0)>option:selected").appendTo("select:eq(1)");
        });

        // 点击左箭头，将右边角色追加在右边
        $("#toLeftBtn").click(function () {
            $("select:eq(1)>option:selected").appendTo("select:eq(0)");
        });

        // 让右边框中元素全部为选中，提交表单则全部提交
        $("#submitBtn").click(function () {
            $("select:eq(1)>option").prop("selected", "selected");
        });
    });
</script>
<body>
<%@include file="/WEB-INF/include-nav.jsp" %>
<div class="container-fluid">
    <div class="row">
        <%@include file="/WEB-INF/include-sidebar.jsp" %>
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
            <ol class="breadcrumb">
                <li><a href="#">首页</a></li>
                <li><a href="#">数据列表</a></li>
                <li class="active">分配角色</li>
            </ol>
            <div class="panel panel-default">
                <div class="panel-body">
                    <form action="assign/do/role/assign.html" method="post" role="form" class="form-inline">
                        <!--隐藏域保存不会改变的adminId、pageNum、keyword，在提交时一起传给后端-->
                        <input type="hidden" value="${param.adminId}" name="adminId"/>
                        <input type="hidden" value="${param.pageNum}" name="pageNum"/>
                        <input type="hidden" value="${param.keyword}" name="keyword"/>
                        <div class="form-group">
                            <label for="exampleInputPassword1">未分配角色列表</label><br>
                            <select class="form-control" multiple="" size="10" style="width:100px;overflow-y:auto;">
                                <c:forEach items="${requestScope.unAssignRoleList}" var="role">
                                    <option value="${role.id}">${role.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="form-group">
                            <ul>
                                <li id="toRightBtn" class="btn btn-default glyphicon glyphicon-chevron-right"></li>
                                <br>
                                <li id="toLeftBtn" class="btn btn-default glyphicon glyphicon-chevron-left"
                                    style="margin-top:20px;"></li>
                            </ul>
                        </div>
                        <div class="form-group" style="margin-left:40px;">
                            <label for="exampleInputPassword1">已分配角色列表</label><br>
                            <!-- 被选中要分配的部分，name设置为roleIdList -->
                            <select name="roleIdList" class="form-control" multiple="" size="10"
                                    style="width:100px;overflow-y:auto;">
                                <c:forEach items="${requestScope.assignRoleList}" var="role">
                                    <option value="${role.id}">${role.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <button id="submitBtn" type="submit" style="width:100px;margin-top: 20px;margin-left: 230px;"
                                class="btn btn-sm btn-success btn-block">提交
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
