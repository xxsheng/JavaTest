var LotteryPlatformBill = function() {
	
	var handelDatePicker = function() {
		$('.date-picker').datepicker({
			language: 'zh-CN',
			format: 'yyyy-mm-dd',
            autoclose: true
        });
	}
	
	var els = function() {
		return $('#lottery-platform-bill');
	}
	
	var download = function() {
		var action = els().find('select[name="action"]').val();
		var sDate = els().find('input[name="from"]').val();
		var eDate = els().find('input[name="to"]').val();
		if(sDate == '' || eDate == '') {
			toastr['error']('请选择要下载的账单日期！', '操作提示');
			return false;
		}

		var diffDays = $.dateDiff(sDate, eDate);
		if (diffDays <= 0) {
			toastr['error']('请至少选择间隔一天！', '操作提示');
			return false;
		}
		if (diffDays > 7) {
			toastr['error']('时间间隔最大7天！', '操作提示');
			return false;
		}

		var downloadUrl = '/lottery-platform-bill/download?action=' + action + '&sDate=' + sDate + '&eDate=' + eDate;
		$('#download').remove();
		$('body').append('<iframe id="download" src="' + downloadUrl + '"></iframe>');
	}
	
	var init = function() {
		var today = moment().format('YYYY-MM-DD');
		var tomorrow = moment().add(1, 'days').format('YYYY-MM-DD');
		var tableList = els();
		tableList.find('[data-field="time"] > input[name="from"]').val(today);
		tableList.find('[data-field="time"] > input[name="to"]').val(tomorrow);
		
		els().find('[data-command="download"]').unbind('click').click(function() {
			download();
		});
	}
	
	return {
		init: function() {
			init();
			handelDatePicker();
		}
	}
}();