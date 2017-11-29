<%--
  Created by IntelliJ IDEA.
  User: liuyandong
  Date: 2017-11-29
  Time: 17:55
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <title></title>
    <%--<link rel="stylesheet" type="text/css" href="../libraries/artDialog/css/ui-dialog.css">--%>
    <%--<script type="text/javascript" src="../libraries/jquery/jquery-3.2.1.min.js"></script>--%>
    <%--<script type="text/javascript" src="../libraries/artDialog/dist/dialog.js"></script>--%>
    <link rel="stylesheet" type="text/css" href="${ui_dialog_css_url}">
    <script type="text/javascript" src="${jquery_3_2_1_min_js_url}"></script>
    <script type="text/javascript" src="${dialog_js_url}"></script>
    <style type="text/css">
        body {margin: 0px;padding: 0px}
        .header {background: #20A0FF;padding: 15px 0;color: #FFFFFF;text-align: center;}
        #ok-button {-webkit-appearance:none;-webkit-border-radius:0;background: #20A0FF;margin-top: 20px;width: 80%;height: 40px;border-radius: 3px;font-size: 16px;color: #FFFFFF;border: none;margin-right: auto;cursor: pointer;}
        #shopId {width: 80%;height: 40px;font-size: 14px;border: 1px solid #DFDFDF;border-radius: 3px;-webkit-appearance: none;padding-left: 10px;padding-right: 10px;box-sizing: border-box;}
        .ui-dialog-footer {border-top: 1px solid #E5E5E5;vertical-align: middle;padding: 10px;text-align: right !important;}
        .ui-dialog-button button.ui-dialog-autofocus {display: inline-block !important;width: 100% !important;background-color: #20A0FF !important;color: #FFFFFF !important;border: none !important;}
    </style>
    <script type="text/javascript">
        var height = $(window).height();
        var width = $(window).width();
        function handleOkButtonOnClick() {
            $("#ok-button").attr("disabled", true);
            var shopId = $("#shopId").val();
            if (!shopId) {
                alertMessage("提示", "请输入饿了么门店号！", "确定", function () {
                    alert(111)
                });
                $("#ok-button").attr("disabled", false);
                return;
            }
            var tenantId = $("#tenantId").val();
            var branchId = $("#branchId").val();
            $.post("../eleme/doBindingRestaurant", {"tenantId": tenantId, "branchId": branchId, "shopId": shopId}, function(data) {
                if (data["successful"]) {
                    alertMessage("提示", data["message"], "确定", undefined);
                    $("#ok-button").attr("disabled", false);
                } else {
                    alertMessage("提示", data["error"], "确定", undefined);
                    $("#ok-button").attr("disabled", false);
                }
            }, "json");
        }

        function alertMessage(title, content, okValue, okCallback) {
            var alertDialog = dialog({
                title: title,
                content: content,
                width: (width * 2 / 3) + "px",
                height: (height / 10) + "px",
                okValue: okValue,
                ok: okCallback || function () {

                }
            });
            alertDialog.showModal();
        }
    </script>
</head>
<body>
<div class="header">饿了么店铺绑定</div>
<div style="text-align: center;margin-top: 60px;">
    <input type="hidden" name="tenantId" value="${tenantId}" id="tenantId">
    <input type="hidden" name="branchId" value="${branchId}" id="branchId">
    <input type="hidden" name="branchId" value="${userId}" id="userId">
    <input type="text" name="shopId" id="shopId" value="" placeholder="请输入饿了么店铺号">
    <input type="button" value="确定" onclick="handleOkButtonOnClick();" id="ok-button">
</div>
</body>
</html>
