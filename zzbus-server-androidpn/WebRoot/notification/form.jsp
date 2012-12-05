<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8"%>
<%@ include file="/includes/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Admin Console</title>
	<meta name="menu" content="notification" />   
	<script type="text/javascript" src="<c:url value='/scripts/jquery.js'/>"></script> 
</head>
<body>

<h1>Send Notifications</h1>

<div style="margin:20px 0px;">
<form action="<c:url value='/servlet/SendMessageServlet'/>" method="post" style="margin: 0px;">
<table width="600" cellpadding="4" cellspacing="0" border="0">
<tr>
	<td width="20%">To:</td>
	<td width="80%">
		<input type="radio" name="broadcast" value="Y" checked="checked" />  在线用户 
        <input type="radio" name="broadcast" value="N" /> 指定用户(多个用户之间用英文;分隔)
        <input type="radio" name="broadcast" value="A" /> 所有用户  
	</td>
</tr>
<tr id="trUsername" style="display:none;">
	<td>用户名:</td>
	<td><input type="text" id="username" name="username" value="" style="width:380px;" /></td>
</tr>
<tr>
	<td>标题:</td>
	<td><input type="text" id="title" name="title" value="你有新消息" style="width:380px;" /></td>
</tr>
<tr>
	<td>信息:</td>
	<td><textarea id="message" name="message" style="width:380px; height:80px;" >测试服务器推信息!</textarea></td>
</tr>
<%--
<tr>
	<td>Ticker:</td>
	<td><input type="text" id="ticker" name="ticker" value="" style="width:380px;" /></td>
</tr>
--%>
<tr>
	<td>URI:</td>
	<td><input type="text" id="uri" name="uri" value="" style="width:380px;" />
	    <br/><span style="font-size:0.8em">ex) http://222.143.37.7:8082/mobileinfo/m_2.1.1.apk, geo:37.24,131.86, tel:111-222-3333</span>
	</td>
</tr>
<tr>
	<td>&nbsp;</td>
	<td><input type="submit" value="Submit" /></td>
</tr>
</table> 
</form>
</div>

<script type="text/javascript"> 
//<![CDATA[
 
$(function() {
	$('input[name=broadcast]').click(function() {
		if ($('input[name=broadcast]')[1].checked) {
			$('#trUsername').show();
		} else {
			$('#trUsername').hide();
		}
	});
	
	if ($('input[name=broadcast]')[1].checked) {
		$('#trUsername').show();
	} else {
		$('#trUsername').hide();
	}	
});
 
//]]>
</script>

</body>
</html>
