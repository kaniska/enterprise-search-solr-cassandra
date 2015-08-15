<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<c:set var="req" value="${pageContext.request}" />
	<c:set var="url">${req.requestURL}</c:set>
	<c:set var="base" value="${fn:substring(url, 0, fn:length(url) - fn:length(req.requestURI))}${req.contextPath}/" />

	<title>MediScene Web Application</title>
	<link type="text/css" rel="stylesheet" href="resources/javascript/dijit/themes/tundra/tundra.css"/>
	<link rel="stylesheet" href="resources/styles/blueprint/screen.css" type="text/css"/>
	<link rel="stylesheet" href="resources/styles/blueprint/print.css" type="text/css"/>
	<!--[if lt IE 8]>
	        <link rel="stylesheet" href="<c:url value="/resources/blueprint/ie.css" />" type="text/css" media="screen, projection" />
	<![endif]-->
	<link rel="stylesheet" href="resources/styles/travel.css" type="text/css"/>
    <script type="text/javascript" src="resources/javascript/dojo/dojo.js"/></script>
    <script type="text/javascript" src="resources/javascript/spring/Spring.js"/></script>
    <script type="text/javascript" src="resources/javascript/spring/Spring-Dojo.js"/></script>
</head>
<body class="tundra">
<div id="page" class="container">
<!-- 
	<div id="header">
		<div id="logo">
			<p>
				<a href="<c:url value="/" />">
					<img src="<c:url value="resources/images/form_title.jpg"/>" alt="MediScene Search" />
				</a>
			</p>
		</div>
	</div>
	-->
	<div id="content">
		<div id="main" class="span-12 last">
			<tiles:insertAttribute name="body" />
		</div>
	</div>
	<hr />
	<div id="footer">
		<%-- <a href="http://www.springframework.org">
			<img src="<c:url value="/resources/images/powered-by-spring.png"/>" alt="Powered by Spring" />
		</a> --%>
	</div>
</div>
</body>
</html>