<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>">
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

	<link rel="stylesheet" type="text/css" href="jquery/bs_pagination/jquery.bs_pagination.min.css">
	<script type="text/javascript" src="jquery/bs_pagination/jquery.bs_pagination.min.js"></script>
	<script type="text/javascript" src="jquery/bs_pagination/en.js"></script>

	<script type="text/javascript">
	$(function(){
		$("#addBtn").click(function (){
			//时间插件
			$(".time").datetimepicker({
				minView: "month",
				language:  'zh-CN',
				format: 'yyyy-mm-dd',
				autoclose: true,
				todayBtn: true,
				pickerPosition: "bottom-left"
			});
			/*
			操作模态窗口的方式
			需要操作模态窗口的jquery对象，调用modal方法，为该方法传递参数 show：打开模态窗口， hide：关闭模态窗口
			 */
			//走后台，目的是为了取得用户信息列表，ajax请求
			$.ajax({
				url:"workbench/activity/getUserList.do",
				data:{},
				type:"get",
				dataType:"json",
				success:function (data){
					var html="<option></option>";
					$.each(data,function (i,n){
						html+="<option value='"+n.id+"'>"+n.name+"</option>"
					})
					$("#create-owner").html(html);
					//将当前登陆的用户，设置为下拉框默认的选项
					//js中使用el表达式，el表达式必须用""
					var id ="${sessionScope.user.id}"
					$("#create-owner").val(id);
					//所有者下拉框处理完毕后，展现模态窗口
					$("#createActivityModal").modal("show");
				}
			})

		})
		//为保存按钮绑定时间，执行添加操作
		$("#saveBtn").click(function (){
			$.ajax({
				url:"workbench/activity/save.do",
				data:{
					owner:$.trim($("#create-owner").val()),
					name:$.trim($("#create-name").val()),
					startDate:$.trim($("#create-startDate").val()),
					endDate:$.trim($("#create-endDate").val()),
					cost:$.trim($("#create-cost").val()),
					description:$.trim($("#create-description").val()),
				},
				type:"post",
				dataType:"json",
				success:function (data){
					//返回 data{"success":true/false}
					if(data.success){
						//添加成功后
						//刷新市场活动信息列表（局部刷新）
						/*
						$("#activityPage").bs_pagination('getOption', 'currentPage')
						表示操作后停留在当前页
						$("#activityPage").bs_pagination('getOption', 'rowsPerPage')
						表示操作后维持已经蛇者好的每页展现的记录
						*/
						pageList(1
								,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
						//清空已经填入的数据
						//reset方法不能用，需要将jquery对象转换为原生js dom对象；
						//jquery对象与dom对象的互相转换   jquery对象遍历数组后就是dom对象，dom对象加上$(dom)就是jquery对象
						$("#activityAddForm")[0].reset();
						//关闭添加操作的模态窗口


						$("#createActivityModal").modal("hide");
					}
					else {
						alert("添加市场活动失败")
					}
				}
			})
		})


		//页面加载完毕后，需要触发一个方法 pageList
		pageList(1,5);
		//查询的日期插件
		$("#search-startDate").click(function (){
			$(".time").datetimepicker({
				minView: "month",
				language:  'zh-CN',
				format: 'yyyy-mm-dd',
				autoclose: true,
				todayBtn: true,
				pickerPosition: "bottom-left"
			});
		})
		$("#search-endDate").click(function (){
			$(".time").datetimepicker({
				minView: "month",
				language:  'zh-CN',
				format: 'yyyy-mm-dd',
				autoclose: true,
				todayBtn: true,
				pickerPosition: "bottom-left"
			});
		})


		//为查询事件按钮绑定时间，触发pageList方法
		$("#searchBtn").click(function (){
			/*
			每次在点击搜索的时候应该讲搜索栏里的文字保存起来,保存到隐藏域中
			*/
			$("#hidden-name").val($.trim($("#search-name").val()));
			$("#hidden-owner").val($.trim($("#search-owner").val()));
			$("#hidden-startDate").val($.trim($("#search-startDate").val()));
			$("#hidden-endDate").val($.trim($("#search-endDate").val()));
			pageList(1,2);
		})


		//全选框的处理
		$("#qx").click(function (){
			$("input[name=xz]").prop("checked",this.checked);
		})
		//动态生成的元素不能使用普通的选择器选择需要使用on来触发事件
		//语法：$(需要绑定事件的有效外层标签).on(绑定事件的方式，需要绑定的元素的jquery对象，回调函数)
		$("#activityBody").on("click",$("input[name=xz]"),function (){
			//判断一下子选中状态与子数量的大小，相等即触发全选框选中事件
			$("#qx").prop("checked",$("input[name=xz]").length==$("input[name=xz]:checked").length)
		})

		//删除市场活动
		$("#deleteBtn").click(function (){
			var $xz = $("input[name=xz]:checked");
			if($xz.length==0){
				alert("请选择要删除的记录")
			}else {
				if(confirm("确定删除所选记录吗")){
					//采用传统的id=xxx&&id=xxx
					//拼接参数
					var param="";
					for (var i=0;i<$xz.length;i++){
						param+="id="+$($xz[i]).val();
						if(i<$xz.length-1) {
							param += "&";
						}
					}
					$.ajax({
						url:"workbench/activity/delete.do",
						data:param,
						type:"post",
						dataType:"json",
						success:function (data){
							/*
                            data:success
                            */
							if(data.success){
								alert("成功删除所选记录")
								//删除成功后刷新页面
								pageList(1
										,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
							}
							else {
								alert("删除市场活动失败")
							}
						}
					})

				}

			}

		})

		//修改市场活动
		$("#editBtn").click(function (){
			var $xz = $("input[name=xz]:checked");
			if($xz.length==0){
				alert("请选择要修改的记录")
			} else if($xz.length>1){
				alert("请选择一条记录修改")
			}else{
				var id = $xz.val();
				$.ajax({
					url:"workbench/activity/getUserListAndActivity.do",
					data:{"id":id},
					type:"get",
					dataType:"json",
					success:function (data){
						/*
						data 用户列表 市场活动对象
						*/
						//处理所有者的下拉框
						var html="<option></option>"
						$.each(data.userList,function (i,n){
							html+="<option value='"+n.id+"'>"+n.name+"</option>";
						})
						$("#edit-owner").html(html);
						//处理单条的activity
						$("#edit-id").val(data.activity.id)
						$("#edit-name").val(data.activity.name)
						$("#edit-owner").val(data.activity.owner)
						$("#edit-startDate").val(data.activity.startDate)
						$("#edit-endDate").val(data.activity.endDate)
						$("#edit-cost").val(data.activity.cost)
						$("#edit-description").val(data.activity.description)
						//展示已经获取现有信息并准备更新的模态窗口，
						$("#editActivityModal").modal("show");
					}
				})
			}
		})
		//为更新按钮绑定时间，执行更新操作
		/*
		  在实际项目开发中，一定是按照先做添加再做修改的顺序，为了节省开发时间，修改操作一般copy添加操作

		*/
		$("#updateBtn").click(function (){
			$.ajax({
				url:"workbench/activity/update.do",
				data:{
					id:$.trim($("#edit-id").val()),
					owner:$.trim($("#edit-owner").val()),
					name:$.trim($("#edit-name").val()),
					startDate:$.trim($("#edit-startDate").val()),
					endDate:$.trim($("#edit-endDate").val()),
					cost:$.trim($("#edit-cost").val()),
					description:$.trim($("#edit-description").val()),
				},
				type:"post",
				dataType:"json",
				success:function (data){
					//返回 data{"success":true/false}
					if(data.success){
						pageList($("#activityPage").bs_pagination('getOption', 'currentPage')
								,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
						//添加成功后
						//刷新市场活动信息列表（局部刷新）
						//清空已经填入的数据
						//reset方法不能用，需要将jquery对象转换为原生js dom对象；
						//jquery对象与dom对象的互相转换   jquery对象遍历数组后就是dom对象，dom对象加上$(dom)就是jquery对象
						//$("#activityAddForm")[0].reset();
						//关闭添加操作的模态窗口
						$("#editActivityModal").modal("hide");
					}
					else {
						alert("修改市场活动失败")
					}
				}
			})


		})



	});
	/*
	   对于所有的关系型数据库，做前端的分页相关操作的基础组件就是pageNum和pageSize
	   pageNo:页码
	   pageSize：每页展示的记录库
       pageList方法：就是发出ajax请求，从后台取得最新的市场活动信息列表数据，
       通过相应回来的数据，局部刷新市场活动信息列表
       我们都在哪些情况下，需要调用pageList方法
       1）点击左侧菜单中的“市场活动”超链接
       2）添加，修改，删除市场活动后，需要刷新
       3）查询时候
       4）点击分页组件的时候
       以上为pageList制定了6个入口
	*/
	   function pageList(pageNum,pageSize){
	   	//将全选框的勾干掉
		   $("#qx").prop("checked",false)
	   	//在查询前，将隐藏域中保存的信息取出来，重新赋予到搜索框中
		   $("#search-name").val($.trim($("#hidden-name").val()));
		   $("#search-owner").val($.trim($("#hidden-owner").val()));
		   $("#search-startDate").val($.trim($("#hidden-startDate").val()));
		   $("#search-endDate").val($.trim($("#hidden-endDate").val()));
		   $.ajax({
			   url:"workbench/activity/pageList.do",
			   data:{
				   "pageNum":pageNum,
				   "pageSize":pageSize,
				   "name":$.trim($("#search-name").val()),
				   "owner":$.trim($("#search-owner").val()),
				   "startDate":$.trim($("#search-startDate").val()),
				   "endDate":$.trim($("#search-endDate").val()),
			   },
			   type:"get",
			   dataType:"json",
			   success:function (data){
			   	/*
			   	          int total       List<Activity> aList
			   	  data{"total:100,"dataList":[市场活动1],{2},{3}}}
			   	 */
				   var html="";
				   $.each(data.dataList,function (i,n){
					    html+= '<tr class="active">';
                        html+= '<td><input type="checkbox" name="xz" value="'+n.id+'" /></td>';
                        html+= '<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'workbench/activity/detail.do?id='+n.id+'\';">'+n.name+'</a></td>';
                        html+= '<td>'+n.owner+'</td>';
                        html+= '<td>'+n.startDate+'</td>';
                        html+= '<td>'+n.endDate+'</td>';
                        html+= '</tr>';
				   })
				   $("#activityBody").html(html);
				   //计算总页数
				   var totalPages=data.total%pageSize==0?data.total/pageSize:parseInt(data.total/pageSize+1)
				   //数据处理完毕后，结合分页 查询，对前端展现分页信息
				   $("#activityPage").bs_pagination({
					   currentPage: pageNum, // 页码
					   rowsPerPage: pageSize, // 每页显示的记录条数
					   maxRowsPerPage: 20, // 每页最多显示的记录条数
					   totalPages: totalPages, // 总页数
					   totalRows: data.total, // 总记录条数
					   visiblePageLinks: 3, // 显示几个卡片
					   showGoToPage: true,
					   showRowsPerPage: true,
					   showRowsInfo: true,
					   showRowsDefaultInfo: true,
					   //该函数触发时间是点击分页组件的时候触发
					   onChangePage : function(event, data){
						   pageList(data.currentPage , data.rowsPerPage);
					   }
				   });

			   }
		   })

	   }
	
</script>
</head>
<body>
    <input type="hidden" id="hidden-name"/>
	<input type="hidden" id="hidden-owner"/>
	<input type="hidden" id="hidden-startDate"/>
	<input type="hidden" id="hidden-endDate"/>
	<!-- 创建市场活动的模态窗口 -->
	<div class="modal fade" id="createActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form id="activityAddForm" class="form-horizontal" role="form">
					
						<div class="form-group">
							<label for="create-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-owner">

								</select>
							</div>
                            <label for="create-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-name">
                            </div>
						</div>
						
						<div class="form-group">
							<label for="create-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-startDate">
							</div>
							<label for="create-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-endDate">
							</div>
						</div>
                        <div class="form-group">

                            <label for="create-cost" class="col-sm-2 control-label">成本</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-cost">
                            </div>
                        </div>
						<div class="form-group">
							<label for="create-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<%--
					data-dismiss表示关闭模态窗口
					--%>
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="saveBtn">保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 修改市场活动的模态窗口 -->
	<div class="modal fade" id="editActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form">
						<input type="hidden" id="edit-id"/>
						<div class="form-group">
							<label for="edit-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-owner">

								</select>
							</div>
                            <label for="edit-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-name" value="发传单">
                            </div>
						</div>

						<div class="form-group">
							<label for="edit-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-startDate" value="2020-10-10">
							</div>
							<label for="edit-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-endDate" value="2020-10-20">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-cost" class="col-sm-2 control-label">成本</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-cost" value="5,000">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<%--
								关于文本域textarea，一定是标签对状态
								（1）一定是要以标签对的形式呈现，正常状态要紧紧挨着，
								（2）textarea虽然是以标签对的形式来呈现的，但是它也是属于表单元素范畴，取值和赋值不能用.html
								    要用val（）
								--%>
								<textarea class="form-control" rows="3" id="edit-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="updateBtn">更新</button>
				</div>
			</div>
		</div>
	</div>
	
	
	
	
	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>市场活动列表123</h3>
			</div>
		</div>
	</div>
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
		
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" type="text" id="search-name">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="search-owner">
				    </div>
				  </div>


				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">开始日期</div>
					  <input class="form-control time" type="text" id="search-startDate" />
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">结束日期</div>
					  <input class="form-control time" type="text" id="search-endDate">
				    </div>
				  </div>
				  
				  <button type="button" id="searchBtn" class="btn btn-default">查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
				<div class="btn-group" style="position: relative; top: 18%;">
					<%--点击创建按钮，观察两个属性和属性值
					data-toggle="modal"
					表示触发该按钮，将要打开一个模态窗口
					data-target="#createActivityModal"
					表示要打开哪个模态窗口，通过#id的形式找到该窗口
					现在以属性和属性值的方式写在了button元素中，用来打开模糊窗口，但是这样无法对按钮进行功能扩充
					所以触发模态窗口的操作，不要写死在元素中要采用js代码去控制
					--%>
				  <button type="button" class="btn btn-primary" id="addBtn"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="editBtn"><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="deleteBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				
			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="qx"/></td>
							<td>名称</td>
                            <td>所有者</td>
							<td>开始日期</td>
							<td>结束日期</td>
						</tr>
					</thead>
					<tbody id="activityBody">
<%--						<tr class="active">
							<td><input type="checkbox" /></td>
							<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/activity/detail.jsp';">发传单</a></td>
                            <td>zhangsan</td>
							<td>2020-10-10</td>
							<td>2020-10-20</td>
						</tr>
                        <tr class="active">
                            <td><input type="checkbox" /></td>
                            <td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/activity/detail.jsp';">发传单</a></td>
                            <td>zhangsan</td>
                            <td>2020-10-10</td>
                            <td>2020-10-20</td>--%>
                        </tr>
					</tbody>
				</table>
			</div>
			
			<div style="height: 50px; position: relative;top: 30px;">
				<div id="activityPage"></div>
<%--				<div>
					<button type="button" class="btn btn-default" style="cursor: default;">共<b>50</b>条记录</button>
				</div>--%>
				<%--<div class="btn-group" style="position: relative;top: -34px; left: 110px;">
					<button type="button" class="btn btn-default" style="cursor: default;">显示</button>
					<div class="btn-group">
						<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">
							10
							<span class="caret"></span>
						</button>
						<ul class="dropdown-menu" role="menu">
							<li><a href="#">20</a></li>
							<li><a href="#">30</a></li>
						</ul>
					</div>
					<button type="button" class="btn btn-default" style="cursor: default;">条/页</button>
&lt;%&ndash;				</div>
&lt;%&ndash;				<div style="position: relative;top: -88px; left: 285px;">
&lt;%&ndash;					<nav>
						<ul class="pagination">
							<li class="disabled"><a href="#">首页</a></li>
							<li class="disabled"><a href="#">上一页</a></li>
							<li class="active"><a href="#">1</a></li>
							<li><a href="#">2</a></li>
							<li><a href="#">3</a></li>
							<li><a href="#">4</a></li>
							<li><a href="#">5</a></li>
							<li><a href="#">下一页</a></li>
							<li class="disabled"><a href="#">末页</a></li>
						</ul>
					</nav>&ndash;%&gt;
				</div>&ndash;%&gt;
			</div>&ndash;%&gt;
			
		</div>--%>
		
	</div>
</body>
</html>