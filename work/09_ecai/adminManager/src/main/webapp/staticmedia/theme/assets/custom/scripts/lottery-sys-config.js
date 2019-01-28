var LotterySysConfig = function() {
	
	var LotterySysConfigTable = function() {
		var table = $('#table-lottery-sys-config');
		var loadConfig = function() {
			var url = './lottery-sys-config/list';
			$.ajax({
				type : 'post',
				url : url,
				data : {},
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						buildConfig(data.slist);
						buildSection(data.alist);
					}
				}
			});
		}
		
		var buildConfig = function(data) {
			
			var setSwitch = function(input, value) {
				if(value == 1) {
					if(input.is(':checked') == false) {
						input.trigger('click');
					}
				} else {
					if(input.is(':checked') == true) {
						input.trigger('click');
					}
				}
			}
			
			$.each(data, function(idx, val) {
				var thisGroup = $('[data-group="' + val.group + '"]');
				var thisInput = thisGroup.find('input[name="' + val.key + '"]');
				
				switch (val.key) {
				case 'STATUS':
				case 'REGIST_STATUS':
				case 'AUTO_HIT_RANKING':
				case 'MMC_CONTROL':
				case 'FGFFC_CONTROL':
				case 'TXFFC_CONTROL':
				case 'QQFFC_CONTROL':
				case 'XXL1D5FC_CONTROL':
				case 'NY2FC_CONTROL':
				case 'XDL3D5FC_CONTROL':
				case 'AZ5FC_CONTROL':
				case 'JSMMC_CONTROL':
					setSwitch(thisInput, val.value);
					break;
				default:
					thisInput.val(val.value);
					break;
				}
			});
			
			table.find('input[type="checkbox"]').on('switchChange.bootstrapSwitch', function () {
				var group = $(this).parents('section').attr('data-group');
				var key = $(this).attr('name');
				var value = $(this).is(':checked') ? 1 : -1;
				doUpdateConfig(group, key, value);
	        });
		}
		
		var initActions = function() {
			table.find('button[data-command="update"]').unbind('click').click(function() {
				var group = $(this).parents('section').attr('data-group');
				var input = $(this).parents('.form-group').find('input[type="text"]');
				var key = input.attr('name');
				var value = input.val();
				doUpdateConfig(group, key, value);
			});
		}
		
		var buildSection = function(data) {
			$.each(data, function(idx, val) {
				table.find('section[data-group="' + val + '"]').show();
			});
		}
		
		var doUpdateConfig = function(group, key, value) {
			var url = './lottery-sys-config/update';
			var params = {group: group, key: key, value: value};
			$.ajax({
				type : 'post',
				url : url,
				data : params,
				dataType : 'json',
				success : function(data) {
					if(data.error == 0) {
						toastr['success']('配置修改成功！', '操作提示');
					}
					if(data.error == 1 || data.error == 2) {
						toastr['error']('配置修改失败！', '操作提示');
					}
				}
			});
		}
		
		initActions();
		
		var initSection = function() {
			table.find('section').hide();
		}
		
		var init = function() {
			initSection();
			loadConfig();
		}
		
		return {
			init: init
		}
		
	}();
	
	return {
		init: function() {
			LotterySysConfigTable.init();
		}
	}
	
}();