<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@taglib prefix="s" uri="/struts-tags" %>
	<%@ taglib prefix="sb" uri="/struts-bootstrap-tags" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

    <sb:head includeScripts="true" includeStyles="false"/>
    <link rel="stylesheet" href="<s:url value="/styles/bootstrap-superhero.css" />" type="text/css"/>
    <style type="text/css">
        body {
            padding-top: 60px; /* 60px to make the container go all the way to the bottom of the topbar */
        }
    </style>

    <script type="text/javascript">
            function greeting(){
                alert("Comfirm your aircraft purchase!")
            }
    </script>
        
</head>

<body>
<%@include file="adminheader.jsp"%>


<div class="container">
    <div class="row">
        <div class="col-md-9">
        <font face="Arial" size="5" color="orange">
        
        Latest simulate run time : <s:property value="simulateRunTime" />
 <table class="table table-striped">
 <tr>
    <!-- <th>Purchase</th> -->
    <th>Rank</th>
    <th>User</th>
    <th>LastSimulationProfit</th>
    <th>Balance</th>
  </tr>
 <s:iterator value="profits" var="p">
 <tr>
   <!--<td><input type="radio" name="planeId" value="#p.id"></td>  -->
   <td> <s:property value="#p.rank" /></td>
   <td> <s:property value="#p.userName" /></td>
   <td> <s:property value="#p.profitString" /></td>
   <td> <s:property value="#p.balanceString" /></td>
    </tr>
 </s:iterator>
</table>
       </font>
       
<s:actionerror theme="bootstrap"/>
<s:actionmessage theme="bootstrap"/>
<s:fielderror theme="bootstrap"/>
  <s:form action="profitDetails" namespace="/route" theme="bootstrap" cssClass="form-horizontal">
   <div class="form-group">
            <div class="col-sm-offset-11 col-md-9">
                   <s:submit value="Profit Details" name="Profit Details" cssClass="btn btn-primary"/>
              </div>
 </div>
 </s:form>
 <s:form action="runSimulate" method="post" namespace="/login" theme="bootstrap" cssClass="form-horizontal">
    <font face="Verdana" size="4" color="#ccccff">
 <br>
 <br>
 <br>
 
  Run a new simulation:
  <br>
  1. input fuel price(dollar/gallon) <input type="text" name="fuelPrice" value="0"/>
  <br>
  <br>
  2. input basic profit of economy class(cent/seat,mile) <input type="text" name="seatPrice" value="0"/>
  
  <br>
      <br>
       (business class price will be 1.25 * base profit; <br>
       first class price will be 1.5 * base profit)</font>
  <br>
  <br>
                        
   <div class="form-group">
          <div class="col-sm-offset-3 col-md-9">
                 <s:submit value="RUN SIMULATION NOW" name="RUN" cssClass="btn btn-primary"/>
          </div>
  </div>
  
  </s:form>  
     


        </div>    
    </div>
    
</div>
</body>
</html>