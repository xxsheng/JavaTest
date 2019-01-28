var Login = function() {
	var loginContent = $('#loginContent');
	var form = $('#loginForm', loginContent);
	var loginBtn = $('[data-command=submit]', loginContent);
	var init = function() {
		form.validate({
			errorElement: 'span', //default input error message container
            errorClass: 'help-block', // default input error message class
            focusInvalid: false, // do not focus the last invalid input
			rules: {
				username: {
					required: true,
					minlength: 4,
					maxlength: 20
				},
				password: {
					required: true,
					minlength: 2,
					maxlength: 30
				}
			},
			messages: {
                username: {
                    required: '用户名不能为空！',
					minlength: '至少输入{0}个字符',
					maxlength: '最多输入{0}个字符'
                },
				password: {
					required: '密码不能为空！',
					minlength: '至少输入{0}个字符',
					maxlength: '最多输入{0}个字符'
				}
            },
			invalidHandler: function (event, validator) {
                //$('.alert-danger', $('.login-form')).show();
            },
            highlight: function (element) {
                $(element).closest('.form-group').addClass('has-error');
            },
            success: function (label) {
                label.closest('.form-group').removeClass('has-error');
                label.remove();
            },
            errorPlacement: function (error, element) {
            	error.insertAfter(element.closest('.input-icon'));
            }
		});
		
		form.find('[data-command="submit"]').unbind('click').click(function() {
			if(form.validate().form()) {
				doLogin();
	    	}
		});
		
		form.find('input[name="username"]').unbind('blur').bind('blur', function(){
			isBindGoogleAuth($(this).val());
		});
		
		form.find('[data-command="submit"]').unbind('mouseover').bind('mouseover', function(){
			var username = form.find('input[name="username"]').val();
			if(username != '')
				isBindGoogleAuth(username);
		});
		
		var errorMessage = function(name, message) {
			var element = form.find('input[name="' + name + '"]');
			element.closest('.form-group').addClass('has-error');
			element.closest('.form-group').append('<span id="username-error" class="help-block">' + message + '</span>');
		}
		
		var alertMessage = function(message) {
			var alert = $('.alert-danger', $('.login-form'));
			alert.find('span').html(message);
			alert.show();
		}
		
		var isLogining = false;
		var doLogin = function() {
			if(isLogining) return;
			loginBtn.attr("disabled", "disabled");
			var username = form.find('input[name="username"]').val();
			var password = form.find('input[name="password"]').val();
			var googlecode = form.find('input[name="googlecode"]').val();
			isLogining = true;

			$.ajax({
				type : 'post',
				url : './DisposableToken',
				data : {},
				dataType : 'json',
				success : function(tokenData) {
					if(tokenData.error == 0) {
						password = encryptPasswordWithToken(password, tokenData.token);
						var params = {username: username, password: password, googlecode:googlecode};
						var url = './login';

						$.ajax({
							type : 'post',
							url : url,
							data : params,
							dataType : 'json',
							success : function(data) {
								isLogining = false;
								if(data.error == 0) {
									window.location.href = './';
								}
								else if(data.error == 1 || data.error == 2) {
									if(data.code == '2-27') {
										AdminModGooglePwdModal.show();
									} else {
										alertMessage(data.message);
									}
									loginBtn.removeAttr("disabled");
								}
							}
						});
					}
					else {
						isLogining = false;
						alertMessage("请求超时，请重试！");
						loginBtn.removeAttr("disabled");
					}
				},
				error: function(){
					isLogining = false;
					alertMessage("请求失败，请重试！");
					loginBtn.removeAttr("disabled");
				}
			});
		}
		
		$(document).keydown(function(evt) {
			if(evt.keyCode == 13) {
				doLogin();
			}
		});

		// initParticles();
		initBackgroundAnimation();
	}

	var initParticles = function() {
		particlesJS.load('particles', 'theme/assets/global/plugins/particles/particlesjs-config.json', function() {
			setTimeout(function(){
				loginContent.show();
				loginContent.animateCss("bounceInDown", function () {
				});
			}, 500);
			AdminModGooglePwdModal.init();
		});
	}

	var initBackgroundAnimation = function() {
		// playAnimation1;
		playAnimation2();

		setTimeout(function(){
			loginContent.show();
			loginContent.animateCss("bounceInDown", function () {

			});

			$('input[name="username"]', '#loginForm').focus();
		}, 500);
		AdminModGooglePwdModal.init();
	}

	var playAnimation1 = function() {
		var canvas = document.getElementById('background-canvas'),
			ctx = canvas.getContext('2d'),
			w = canvas.width = window.innerWidth,
			h = canvas.height = window.innerHeight,

			hue = 217,
			stars = [],
			count = 0,
			maxStars = 1400;

// Thanks @jackrugile for the performance tip! http://codepen.io/jackrugile/pen/BjBGoM
// Cache gradient
		var canvas2 = document.createElement('canvas'),
			ctx2 = canvas2.getContext('2d');
		canvas2.width = 100;
		canvas2.height = 100;
		var half = canvas2.width/2,
			gradient2 = ctx2.createRadialGradient(half, half, 0, half, half, half);
		gradient2.addColorStop(0.025, '#fff');
		gradient2.addColorStop(0.1, 'hsl(' + hue + ', 61%, 33%)');
		gradient2.addColorStop(0.25, 'hsl(' + hue + ', 64%, 6%)');
		gradient2.addColorStop(1, 'transparent');

		ctx2.fillStyle = gradient2;
		ctx2.beginPath();
		ctx2.arc(half, half, half, 0, Math.PI * 2);
		ctx2.fill();

// End cache

		function random(min, max) {
			if (arguments.length < 2) {
				max = min;
				min = 0;
			}

			if (min > max) {
				var hold = max;
				max = min;
				min = hold;
			}

			return Math.floor(Math.random() * (max - min + 1)) + min;
		}

		var Star = function() {

			this.orbitRadius = random(w / 2 - 50);
			this.radius = random(100, this.orbitRadius) / 10;
			this.orbitX = w / 2;
			this.orbitY = h / 2;
			this.timePassed = random(0, maxStars);
			this.speed = random(this.orbitRadius) / 900000;
			this.alpha = random(2, 10) / 10;

			count++;
			stars[count] = this;
		}

		Star.prototype.draw = function() {
			var x = Math.sin(this.timePassed + 1) * this.orbitRadius + this.orbitX,
				y = Math.cos(this.timePassed) * this.orbitRadius/2 + this.orbitY,
				twinkle = random(10);

			if (twinkle === 1 && this.alpha > 0) {
				this.alpha -= 0.05;
			} else if (twinkle === 2 && this.alpha < 1) {
				this.alpha += 0.05;
			}

			ctx.globalAlpha = this.alpha;
			ctx.drawImage(canvas2, x - this.radius / 2, y - this.radius / 2, this.radius, this.radius);
			this.timePassed += this.speed;
		}

		for (var i = 0; i < maxStars; i++) {
			new Star();
		}

		function animation() {
			ctx.globalCompositeOperation = 'source-over';
			ctx.globalAlpha = 0.8;
			ctx.fillStyle = 'hsla(' + hue + ', 64%, 6%, 1)';
			ctx.fillRect(0, 0, w, h)

			ctx.globalCompositeOperation = 'lighter';
			for (var i = 1, l = stars.length; i < l; i++) {
				stars[i].draw();
			};

			window.requestAnimationFrame(animation);


		}

		animation();
	}

	var playAnimation2 = function() {
		var canvas = document.getElementById('background-canvas'),
		ctx = canvas.getContext('2d'),
			w = canvas.width = window.innerWidth,
			h = canvas.height = window.innerHeight,

			hue = 217,
			stars = [],
			count = 0,
			maxStars = 1200;

		var canvas2 = document.createElement('canvas'),
			ctx2 = canvas2.getContext('2d');
		canvas2.width = 100;
		canvas2.height = 100;
		var half = canvas2.width / 2,
			gradient2 = ctx2.createRadialGradient(half, half, 0, half, half, half);
		gradient2.addColorStop(0.025, '#fff');
		gradient2.addColorStop(0.1, 'hsl(' + hue + ', 61%, 33%)');
		gradient2.addColorStop(0.25, 'hsl(' + hue + ', 64%, 6%)');
		gradient2.addColorStop(1, 'transparent');

		ctx2.fillStyle = gradient2;
		ctx2.beginPath();
		ctx2.arc(half, half, half, 0, Math.PI * 2);
		ctx2.fill();

// End cache

		function random(min, max) {
			if (arguments.length < 2) {
				max = min;
				min = 0;
			}

			if (min > max) {
				var hold = max;
				max = min;
				min = hold;
			}

			return Math.floor(Math.random() * (max - min + 1)) + min;
		}

		function maxOrbit(x, y) {
			var max = Math.max(x, y),
				diameter = Math.round(Math.sqrt(max * max + max * max));
			return diameter / 2;
		}

		var Star = function() {

			this.orbitRadius = random(maxOrbit(w, h));
			this.radius = random(60, this.orbitRadius) / 12;
			this.orbitX = w / 2;
			this.orbitY = h / 2;
			this.timePassed = random(0, maxStars);
			this.speed = random(this.orbitRadius) / 900000;
			this.alpha = random(2, 10) / 10;

			count++;
			stars[count] = this;
		}

		Star.prototype.draw = function() {
			var x = Math.sin(this.timePassed) * this.orbitRadius + this.orbitX,
				y = Math.cos(this.timePassed) * this.orbitRadius + this.orbitY,
				twinkle = random(10);

			if (twinkle === 1 && this.alpha > 0) {
				this.alpha -= 0.05;
			} else if (twinkle === 2 && this.alpha < 1) {
				this.alpha += 0.05;
			}

			ctx.globalAlpha = this.alpha;
			ctx.drawImage(canvas2, x - this.radius / 2, y - this.radius / 2, this.radius, this.radius);
			this.timePassed += this.speed;
		}

		for (var i = 0; i < maxStars; i++) {
			new Star();
		}

		function animation() {
			ctx.globalCompositeOperation = 'source-over';
			ctx.globalAlpha = 0.8;
			ctx.fillStyle = 'hsla(' + hue + ', 64%, 6%, 1)';
			ctx.fillRect(0, 0, w, h)

			ctx.globalCompositeOperation = 'lighter';
			for (var i = 1, l = stars.length; i < l; i++) {
				stars[i].draw();
			};

			window.requestAnimationFrame(animation);
		}

		animation();

	}
	
	var isBindGoogleAuth = function(username){
		var url = './google-auth/isbind';
		$.ajax({
			type : 'post',
			url : url,
			data : {username:username},
			dataType : 'json',
			success : function(data) {
				if(data == 'true' || data == true){
					$('#googleAuth').css('display','block');
				}else{
					$('#googleAuth').css('display','none');
				}
			}
		});
	}

	/**
	 * 设置Google动态密码
	 */
	var AdminModGooglePwdModal = function() {
		var modal = $('#modal-admin-mod-google-pwd');
		var form = modal.find('form');
		var initForm = function() {
			form.validate({
				rules: {
					password: {
						required: true,
						number:true,
						minlength: 5
					},
					loginPassword: {
						required: true,
						minlength: 5
					},
				},
				messages: {
					password: {
						required: '密码不能为空！',
						number:'请输入有效的数字',
						minlength: '至少输入{0}个数字'
					},
					loginPassword: {
						required: '新密码不能为空！',
						minlength: '至少输入{0}个字符'
					}
				},
				invalidHandler: function (event, validator) {},
				errorPlacement: function (error, element) {
					$(element).closest('.form-group').find('.help-inline').html('<i class="fa fa-warning"></i> ' + error.text());
				},
				highlight: function (element) {
					$(element).closest('.form-group').removeClass('has-success').addClass('has-error');
				},
				unhighlight: function (element) {
					$(element).closest('.form-group').removeClass('has-error').addClass('has-success');
					$(element).closest('.form-group').find('.help-inline').html('<i class="fa fa-check"></i> 填写正确。');
				}
			});
			modal.find('[data-command="submit"]').unbind('click').click(function() {
				if(form.validate().form()) {
					doSubmit();
				}
			});
		}

		var isSending = false;
		var doSubmit = function() {
			if(isSending) return;
			var password = modal.find('input[name="password"]').val();
			var loginPwd = modal.find('input[name="loginPassword"]').val();
			isSending = true;

			$.ajax({
				type : 'post',
				url : './DisposableToken',
				data : {},
				dataType : 'json',
				success : function(tokenData) {
					if(tokenData.error == 0) {
						loginPwd = encryptPasswordWithToken(loginPwd, tokenData.token);
						var params = { vCode: password, loginPwd:loginPwd};
						var url = './google-auth/authorize';

						$.ajax({
							type : 'post',
							url : url,
							data : params,
							dataType : 'json',
							success : function(data) {
								isSending = false;

								if(data.error == 0) {
									modal.modal('hide');
									alert("Google动态密码绑定成功,请登录");
									$('#googleAuth').css('display','block');
								} else {
									alert(data.message);
								}
							}
						});

					} else {
						isSending = false;
						alert("请求失败，请重试！" + tokenData.message);
					}
				}
			});
		}

		var show = function() {
			var url = './google-auth/bind';
			$.ajax({
				type : 'post',
				url : url,
				data : {},
				dataType : 'json',
				success : function(data) {
					isSending = false;
					if(data.error == 0) {
						form[0].reset();
						form.find('.help-inline').empty();
						form.find('.has-error').removeClass('has-error');
						form.find('.has-success').removeClass('has-success');
						modal.modal('show');

						var url = data.qr;
						var secret = data.secret;
						$('#googleQR').attr('src', url);
						$('#googleSecret').val(secret);
					}
					if(data.error == 1 || data.error == 2) {
						alert(data.message);
					}
				}
			});

		}

		var initBrowserTools = function() {
			var tools = header.find('.top-toolbar .button-group');
			tools.find('a[data-command="back"]').unbind('click').click(function() {
				window.frames['main-frame'].history.go(-1);
			});
			tools.find('a[data-command="forward"]').unbind('click').click(function() {
				window.frames['main-frame'].history.go(1);
			});
		}

		var init = function() {
			initForm();
		}

		return {
			init: init,
			show: show
		}

	}();

	var encryptPasswordWithToken = function(plainStr, token) {
		var password = hex_md5(plainStr).toUpperCase();
		password = hex_md5(password).toUpperCase();
		password = hex_md5(password).toUpperCase();
		password = hex_md5(password + token).toUpperCase();
		return password;
	}

	var generatePassword = function(plainStr) {
		var password = hex_md5(plainStr).toUpperCase();
		password = hex_md5(password).toUpperCase();
		return password;
	}
	
	return {
		init: function() {
			init();
		}
	}
}();