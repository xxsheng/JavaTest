var AdminUserMenu = function() {
	
	var AdminUserMenuTable = function() {
		var tableList = $('#table-admin-user-menu-list');
		
		var isLoading = false;
		var loadData = function() {
			if(isLoading) return;
			var url = './admin-user-menu/list';
			isLoading = true;
			$.ajax({
				type : 'post',
				url : url,
				data : {},
				dataType : 'json',
				success : function(data) {
					isLoading = false;
					buildData(data);
				}
			});
		}
		
		var buildData = function(data) {
			var table = tableList.find('table > tbody').empty();
			var innerHtml = buildChild(data);
			table.html(innerHtml);
			
			table.find('[data-command="status"]').unbind('click').click(function() {
				var id = $(this).parents('tr').attr('data-id');
				var status = $(this).attr('data-status');
				var msg = '确定禁用该菜单？（禁用后该菜单以及其子菜单将不可用）';
				if(status == -1) {
					msg = '确定启用该菜单？';
				}
				bootbox.dialog({
					message: msg,
					title: '提示消息',
					buttons: {
						success: {
							label: '<i class="fa fa-check"></i> 确定',
							className: 'green-meadow',
							callback: function() {
								if(status == 0) {
									updateStatus(id, -1);
								}
								if(status == -1) {
									updateStatus(id, 0);
								}
							}
						},
						danger: {
							label: '<i class="fa fa-undo"></i> 取消',
							className: 'btn-danger',
							callback: function() {}
						}
					}
				});
			});
			table.find('[data-command="moveUp"]').unbind('click').click(function(e) {
				var $tr = $(e.target).parents("tr");
				var id = $(this).parents('tr').attr('data-id');
				var upid = $(this).parents('tr').attr('data-upid');
				moveUp($tr, id,upid);
			});

			table.find('[data-command="moveDown"]').unbind('click').click(function(e) {
				var $tr = $(e.target).parents("tr");
				var id = $(this).parents('tr').attr('data-id');
				var upid = $(this).parents('tr').attr('data-upid');
				
				moveDown($tr, id,upid);
			});
			// tree效果
			table.find('tr').each(function() {
				var id = $(this).attr('data-id');
				var upid = $(this).attr('data-upid');
				var level = $(this).attr('data-level');
				if(upid == 0) {
					$(this).show();
				} else {
					$(this).hide();
				}
				$(this).find('td').eq(0).css('padding-left', level * 26 + 8);
				$(this).find('[data-command="child"]').unbind('click').click(function() {
					if($(this).hasClass('fa-caret-right')) {
						open(this, id);
					} else if($(this).hasClass('fa-caret-down')) {
						close(this, id);
					}
				});
			});
			var moveSending = false;
			var moveUp = function($tr, id,upid) {
				if (moveSending) {
					toastr['error']('正在操作，请稍候！', '操作提示');
					return;
				}
				var $prevs= $tr.prevAll('[data-upid="'+upid+'"]');//获取之前所用的兄弟节点
				var $subprevs= $tr.nextAll('[data-upid="'+id+'"]');//获取当前节点的所有子节点
				var $prev=$($prevs[0]);//获取上一个兄弟节点
				if (typeof($prev) == "undefined")  
				{  
				    return; 
				}
				var prevId=$prev.attr("data-id");
				if(typeof(prevId) == "undefined"){
					return;
				}
				var $nextsubprevs= $tr.nextAll('[data-upid="'+prevId+'"]');//获取上一个兄弟节点所有子节点
				var params = {id: id};
				var url = './lottery-payment-thrid/move-down';
				var nextSortId= $prev.find("[name='sort']").html();
				var trSortId= $tr.find("[name='sort']").html();
				var params = {id: id};
				var url = './admin-user-menu/moveup';
				$.ajax({
					type : 'post',
					url : url,
					data : params,
					dataType : 'json',
					success : function(data) {
						if(data.error == 0) {
							toastr['success']('操作成功！', '操作提示');
							$tr.insertBefore($prev);
							$subprevs.insertBefore($prev);
							$nextsubprevs.insertBefore($prev);
							$tr.find("[name='sort']").html(nextSortId);
							$prev.find("[name='sort']").html(trSortId);
							$tr.fadeIn(600);
						}
						else if(data.error == 1 || data.error == 2) {
							toastr['error']('操作失败！' + data.message, '操作提示');
						}
					},
					complete: function(){
						moveSending = false;
					}
				});
			}

			var moveDown = function($tr, id,upid) {
				if (moveSending) {
					toastr['error']('正在操作，请稍候！', '操作提示');
					return;
				}
				var $prevs= $tr.nextAll('[data-upid="'+upid+'"]');
				var $subprevs= $tr.nextAll('[data-upid="'+id+'"]');
				var $nextTr=$($prevs[0]);
				if (typeof($nextTr) == "undefined")  
				{  
				    return; 
				}
				var nextId=$($nextTr).attr("data-id");
				if(typeof(nextId) == "undefined"){
					return;
				}
				var $nextsubprevs= $tr.nextAll('[data-upid="'+nextId+'"]');
				var nextSortId= $nextTr.find("[name='sort']").html();
				var trSortId= $tr.find("[name='sort']").html();
				var params = {id: id};
				var url = './admin-user-menu/movedown';
				$.ajax({
					type : 'post',
					url : url,
					data : params,
					dataType : 'json',
					success : function(data) {
						if(data.error == 0) {
							toastr['success']('操作成功！', '操作提示');
							$tr.insertAfter($nextTr);
							$subprevs.insertAfter($tr);
							$nextsubprevs.insertBefore($tr);
							$tr.find("[name='sort']").html(nextSortId);
							$nextTr.find("[name='sort']").html(trSortId);
							$tr.fadeIn(600);
						}
						else if(data.error == 1 || data.error == 2) {
							toastr['error']('操作失败！' + data.message, '操作提示');
						}
					},
					complete: function(){
						moveSending = false;
					}
				});
			}
			
			var open = function(els, id) {
				table.find('tr[data-upid="' + id + '"]').show();
				$(els).removeClass('fa-caret-right').addClass('fa-caret-down');
			}
			
			var close = function(els, id) {
				table.find('tr[data-upid="' + id + '"]').each(function() {
					var command = $(this).find('[data-command="child"]');
					$(this).hide();
					if(command && command.hasClass('fa-caret-down')) {
						command.trigger('click');
					}
				});
				$(els).removeClass('fa-caret-down').addClass('fa-caret-right');
			}
		}
		
		var buildChild = function(data, level) {
			var innerHtml = '';
			level = level == undefined ? 0 : level + 1;
			$.each(data, function(idx, val) {
				var statusAction = '<a data-command="moveUp" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-arrow-up"></i></a>'
					statusAction +='<a data-command="moveDown" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-arrow-down"></i></a>'
				if(val.status == -1) {
					statusAction += '<a data-command="status" data-status="' + val.status + '" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-check"></i> 启用 </a>';
				}else{
					statusAction += '<a data-command="status" data-status="' + val.status + '" href="javascript:;" class="btn default btn-xs black"><i class="fa fa-ban"></i> 禁用 </a>';
				}
				var formatCommand = '<i class="fa"></i>';
				if(val.items.length > 0) {
					formatCommand = '<i data-command="child" class="tree fa fa-caret-right"></i>';
				}
				var formatLink = val.link == 'javascript:;' ? '' : val.link;
				innerHtml +=
				'<tr class="align-center" data-id="' + val.id + '" data-upid="' + val.upid + '" data-level="'+level+'">'+
					'<td class="align-left">' + formatCommand + '<i class="' + val.icon + '"></i>' + val.name + '</td>'+
					//'<td class="align-left">' + formatLink + '</td>'+
					'<td name="sort">' + val.sort + '</td>'+
					'<td>' + DataFormat.formatAdminUserMenuStatus(val.status) + '</td>'+
					'<td>' + statusAction + '</td>'+
				'</tr>';
				if(val.items.length > 0) {
					innerHtml += buildChild(val.items, level);
				}
			});
			return innerHtml;
		}
		
	    var updateStatus = function(id, status) {
			var params = {id: id, status: status};
			var url = './admin-user-menu/update-status';
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					isSending = false;
					if(data.error == 0) {
						loadData();
						if(status == 0) {
							toastr['success']('该菜单已启用！', '操作提示');
						}
						if(status == -1) {
							toastr['success']('该菜单已禁用！', '操作提示');
						}
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('操作失败！' + data.message, '操作提示');
					}
				}
			});
		}
		
		var init = function() {
			loadData();
		}
		
		return {
			init: init
		}
	}();
	
	return {
		init: function() {
			AdminUserMenuTable.init();
		}
	}
}();