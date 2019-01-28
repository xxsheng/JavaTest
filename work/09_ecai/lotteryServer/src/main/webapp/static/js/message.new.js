$(function(){
    var $doc = $(document);
    var $content = $("#content", $doc);
    var $contentBox = $("#contentBox", $content);
    var $toUsersGroup;
    var $toUsers;
    var $sendSubject;
    var $sendContent;

    function init() {
        // 初始化内容
        initContentBox();
    }

    function initContentBox() {
        $contentBox.html(tpl('#message_new_content_tpl', {}));

        $toUsersGroup = $contentBox.find('[data-type=toUsersGroup]');
        $toUsers = $contentBox.find('input[name=toUsers]');
        $sendSubject = $contentBox.find('input[name=sendSubject]');
        $sendContent = $contentBox.find('textarea[name=sendContent]');
    }

    // 收件人类型选择
    $contentBox.on('click', '[data-command=changeType]', function(e) {
        var $this = $(this);
        if ($this.hasClass('cur')) {
            return;
        }

        $this.addClass('cur').siblings().removeClass('cur');

        var type = $this.attr('data-type');

        if (type === 'lower') {
            $toUsersGroup.show();
        } else {
            $toUsersGroup.hide()
        }
        $toUsers.val("")
    })

    // 选择用户
    $contentBox.on('click', '[data-command=chooseUser]', function(e) {
        new ChooseMember(function(data) {
            $toUsers.val(data);
        }, "/ListUserDirectLower");
    })

    // 清空选择
    $contentBox.on('click', '[data-command=clearUser]', function(e) {
        $toUsers.val('');
    })

    // 发送
    $contentBox.on('click', '[data-command=submit]', function(e) {
        layer.closeAll('tips');

        var target = $contentBox.find('.player .cur').attr('data-type');
        if ($.YF.isEmpty(target)) {
            $.YF.alert_warning('请选择收件人');
            return;
        }

        var toUsers = $toUsers.val();
        var subject = $sendSubject.val();
        var content = $sendContent.val();
        if (target === 'lower' && $.YF.isEmpty(toUsers)) {
            $toUsers.focus();
            $.YF.alert_tooltip('请选择用户', $toUsers);
            return;
        }
        if ($.YF.isEmpty(subject)) {
            $sendSubject.focus();
            $.YF.alert_tooltip('请输入主题', $sendSubject);
            return;
        }
        if ($.YF.isEmpty(content)) {
            $sendContent.focus();
            $.YF.alert_tooltip('请输入内容', $sendContent);
            return;
        }

        var data = {
            target: target,
            toUsers: toUsers,
            subject: subject,
            content: content
        };

        $.YF.alert_question('确认发送信息？', function(resolve, reject){
            $.ajax({
                url: '/SendUserMessage',
                data: data,
                success: function(res) {
                    if(res.error === 0) {
                        resolve();
                    }
                    else {
                        reject();
                        $.YF.alert_warning(res.message);
                    }
                }
            })
        }, function(){
            $.YF.alert_success('信息发送成功！', function(){
                init();
            });
        })
    })

    // 初始化
    init();
})