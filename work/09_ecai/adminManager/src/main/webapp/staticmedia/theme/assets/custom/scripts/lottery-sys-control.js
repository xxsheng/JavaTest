var LotterySysControl = function() {
	
	var LotterySysControlTable = function() {
		
		var thisTable = $('#table-lottery-sys-control');
		
		/**
		 * doSomething
		 * action: start stop restart stop and ...
		 */
		var doSomething = function(action) {
			var url = './lottery-sys-control/do';
			var params = {action: action};
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.result == true) {
						toastr['success']('操作成功！', '操作提示');
					}
					if(data.result == false) {
						toastr['error']('操作失败！', '操作提示');
					}
				}
			});
		};
		
		var loadConfig = function() {
				var url = './lottery-sys-config/list';
				$.ajax({
					type : 'post',
					url : url,
					data : {},
					dataType : 'json',
					success : function(data) {
						if(data.error == 0) {
							buildConfig(data.data);
						}
					}
				});
			};
			var buildConfig = function(data) {
				$.each(data, function(idx, val) {
					thisTable.find('input[name="' + val.key + '"]').val(val.value);
				});
				loadServerStatus();
			};
			
		var serverStatus = function(server){
			var host = thisTable.find('input[name="LotteryControlHost"]').val();
			var url = './lottery-sys-control/status';
			var params = {host:host,server: server};
			Metronic.blockUI(thisTable);
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					Metronic.unblockUI();
					thisTable.find('[data-server="'+server+'"]').find('label[data-field="status"]').html(data.message);
					if(data.code == 0) {
						toastr['success'](data.message, '操作提示');
					}else{
						toastr['error'](data.message, '操作提示');
					}
				}
			});
			
		};
		
		var actionSubmit = function(action,server){
			var host = thisTable.find('input[name="LotteryControlHost"]').val();
			var url = './lottery-sys-control/do';
			var params = {server: server,action:action};
			Metronic.blockUI(thisTable);
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					Metronic.unblockUI();
					thisTable.find('[data-server="'+server+'"]').find('label[data-field="status"]').html(data.message);
					if(data.code == 0) {
						toastr['success'](data.message, '操作提示');
					}else{
						toastr['error'](data.message, '操作提示');
					}
				}
			});
			
		};
		
		/**
		 * example
		 * [data-command="example"] 这里是操作按钮
		 * thisTable.find('input[name="example"]') 根据name获取表单值
		 */
		thisTable.find('[data-command="start"]').unbind('click').click(function() {
			var server = $(this).parents('.form-group').attr('data-server');
			actionSubmit("start",server);
		 
		});
		thisTable.find('[data-command="status"]').unbind('click').click(function() {
			var server = $(this).parents('.form-group').attr('data-server');
			serverStatus(server);
		 
		});
		thisTable.find('[data-command="stop"]').unbind('click').click(function() {
			var server = $(this).parents('.form-group').attr('data-server');
			actionSubmit("stop",server);
		});
		
		thisTable.find('[data-command="restart"]').unbind('click').click(function() {
			var server = $(this).parents('.form-group').attr('data-server');
			actionSubmit("restart",server);
		});
		
		var loadServerStatus = function(){
			serverStatus("LotteryServer");
			serverStatus("AdminManager");
			serverStatus("LotteryPrize");
			serverStatus("Payment");
			serverStatus("HostNameVerify");
		};
		var init = function() {
			// 这里初始化
			loadConfig();
		};
		
		
		return {
			init: init
		};
		
	}();
	
	return {
		init: function() {
			LotterySysControlTable.init();
		}
	};
	
}();