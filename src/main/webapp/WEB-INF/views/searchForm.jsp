<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core"%>

<h3>MediScene Search</h3>

<c:url var="Url" value="/view" />
<form:form modelAttribute="searchCriteria" action="${Url}" method="get"
	cssClass="inline" name="form1">
	<fieldset>
					
			<div class="span-8">
			<label for="fieldName">Field Name:</label>
			<form:select id="fieldName" path="fieldName">
				<form:option label="Generic Category" value="generic_name"/>
				<form:option label="Indications" value="indication"/>
				<form:option label="Contra-Indications" value="contra_indication"/>
				<form:option label="Side Effects" value="side_effect"/>
				<form:option label="Caution" value="caution"/>
				<form:option label="Symptoms" value="symptom"/>
			</form:select>
			</div>
			
			<div class="span-8">
			<label for="searchFilter">Query Text:</label>
			<form:input id="searchFilter" path="searchFilter" />
			<script type="text/javascript">
				Spring.addDecoration(new Spring.ElementDecoration({
					elementId : "searchFilter",
					widgetType : "dijit.form.ValidationTextBox",
					widgetAttrs : { promptMessage : "Specify a Search Term." }}));
			</script>

		<%--  <div class="span-8">
				<label for="pageSize">Maximum Records:</label>
				<form:select id="pageSize" path="pageSize">
					<form:option label="50" value="50" />
					<form:option label="100" value="100" />
					<form:option label="200" value="200" />
					<form:option label="500" value="500" />
				</form:select>
		</div>  --%>
		
		<!-- <div class="span-4">
			<button type="submit" >Search</button>
		</div> -->
		
		<!-- 	<button type="submit" name="search"  onClick="javascript:loadImage()">Search</button>
		-->
		<input class="input" type="hidden" name="apiToken" value="@!Qg$34dr" />
		 <input class="input" type="submit" name="search" value="Search"
		 onclick="javascript:loadSubmit()">
		 </div>
		
		<p style="visibility:hidden;" id="progress"/>
		<img id="progress_image" style="padding-left:5px;padding-top:5px;" 
		src="resources/images/loading.gif" alt=""> <b>Search in progress</b><p> 
		<span id="loading"></span>
		
	</fieldset>
</form:form>
		
		
		
<script type="text/javascript">
function loadImage() {
	document.getElementById('loading').innerHTML = "Refreshing the UI...<img src=\"/resources/images/loading.gif\"/>";location=document.form1.search;
	return true;
}

function loadSubmit() {

    ProgressImage = document.getElementById('progress_image');
    document.getElementById("progress").style.visibility = "visible";
    setTimeout("ProgressImage.src = ProgressImage.src",100);
    return true;

    } 
</script>
