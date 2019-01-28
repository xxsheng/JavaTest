$(function(){
    var textAreaReplaceRegex = /\,|\;|\n|\t/g;
    var textAreaReplaceWith = ' ';
    var $doc = $(document);
    var $rightBanner = $('#rightBanner', $doc);

    var $content = $("#content", $doc);
    var $layoutBox = $("#layoutBox", $content);
    var $bettingTab = $("#bettingTab", $layoutBox);
    var $bettingCondition = $("#bettingCondition", $layoutBox);
    var $bettingPrize = $("#bettingPrize", $layoutBox);
    var $bettingArea = $("#betting-area", $layoutBox);
    var playOptions = $("#play-options", $layoutBox);
    var $playRulesTipBtn = $("#playRulesTipBtn", $bettingPrize);
    var $playRulesTipBox = $("#playRulesTipBox", $bettingPrize);
    var $cartNonList;
    var $cartlist;
    var $statistics;
    var $modelBtn;
    var $adjustPrizeBox;
    var $sliderBox;
    var submited = false
    var adjustPrize = null
    var $multiple = null
    var activeData = null
    var refreshOrderId = null;
    // 返点变量
    var uLocPoint = UserData.lp
    var uNotLocPoint = UserData.nlp
    var uMaxCode = UserData.code > 1932 ? 1932 : UserData.code;
    var uMinLocPoint = UserData.code > 1932 ? ((Number((UserData.code - 1932))> 0) ? (Number((UserData.code - 1932)) / 20) : 0).toFixed(2) : 0;
    var uMinCode = uMaxCode - uLocPoint * 20
    // 默认多少钱一注
    var bUnitMoney = Config.bUnitMoney
    var USER_BDATA_CODE_COOKIE_KEY = 'YF_USER_BDATA_CODE_' + UserData.username;
    var USER_BDATA_MODEL_COOKIE_KEY = 'YF_USER_BDATA_MODEL_' + UserData.username;
    var USER_BDATA_MULTIPLE_COOKIE_KEY = 'YF_USER_BDATA_MULTIPLE_' + UserData.username;
    // 用户临时储存
    var bData = {}
    // 投注列表
    var bList = []

    /**
     * 彩票投注辅助
     */
    var LotteryUtils = function() {
        /**
         * 输入框类型检测
         */
        var _inputCheck_Num = function(datasel, l, fun) {
            fun = $.isFunction(fun) ? fun : function(n, l) {
                return true;
            }
            var newsel = []; // 新的号码
            datasel = ArrayUtil.unique(datasel); // 去除重复
            var regex = new RegExp('^[1-6]{' + l + '}$');
            $.each(datasel, function(i, n) {
                if(regex.test(n) && fun(n, l)) {
                    newsel.push(n);
                }
            });
            return newsel;
        }

        /**
         * 2排不重复检测
         */
        var _uniqueCheck = function(a, b) {
            return ArrayUtil.intersect(a, b).length == 0 ? true : false;
        }

        /**
         * 二同号单式
         */
        var _ethdsCheck = function(n, l) {
            if (l != 3) return false;
            var first = n.substring(0, 1);
            var second = n.substring(1, 2);
            var third = n.substring(2, 3);
            if (first == 0 || first == '0') {
                return false;
            }
            if (second == 0 || second == '0') {
                return false;
            }
            if (third == 0 || third == '0') {
                return false;
            }
            if (first == second && second == third) return false;
            if (first == second || second == third || third == first) return true;
            return false;
        }

        /**
         * 二不同号单式
         */
        var _ebthdsCheck = function(n, l) {
            if (l != 2) return false;
            var first = n.substring(0, 1);
            var second = n.substring(1, 2);
            if (first == second) return false;
            if (first == 0 || first == '0') {
                return false;
            }
            if (second == 0 || second == '0') {
                return false;
            }
            return true;
        }

        /**
         * 三不同号单式
         */
        var _sbthdsCheck = function(n, l) {
            if (l != 3) return false;
            var first = n.substring(0, 1);
            var second = n.substring(1, 2);
            var third = n.substring(2, 3);
            if (first == 0 || first == '0') {
                return false;
            }
            if (second == 0 || second == '0') {
                return false;
            }
            if (third == 0 || third == '0') {
                return false;
            }
            if (first != second && second != third && third != first) return true;
            return false;
        }

        /**
         * 快三计算号码
         */
        var _inputNumbers = function(type, datasel) {
            var nums = 0;
            var tmp_nums = 1;
            // 输入号
            switch (type) {
                case 'ebthds':
                    return _inputCheck_Num(datasel, 2, _ebthdsCheck).length;
                case 'ethds':
                    return _inputCheck_Num(datasel, 3, _ethdsCheck).length;
                case 'sbthds':
                    return _inputCheck_Num(datasel, 3, _sbthdsCheck).length;
                // 选号
                case 'ebthdx': // 二不同号，标准选号
                    if (datasel[0].length >= 2) {
                        nums += ArrayUtil.ComNum(datasel[0].length, 2);
                    }
                    break;
                case 'ebthdt':
                    var maxplace = 2;
                    if(datasel.length == maxplace) {
                        if(_uniqueCheck(datasel[0], datasel[1])) {
                            for (var i = 0; i < maxplace; i++) {
                                var s = datasel[i].length;
                                if (s > 0) {
                                    if(i > 0) {
                                        nums = datasel[i].length;
                                    }
                                } else {
                                    nums = 0;
                                    break;
                                }
                            }
                        }
                    }
                    break;
                case 'ethdx':
                    var s = datasel.length;
                    if(s > 1) {
                        var a = datasel[0].length;
                        var b = datasel[1].length;
                        if(a > 0 && b > 0) {
                            if(_uniqueCheck(datasel[0], datasel[1])) {
                                nums = a * b;
                            }
                        }
                    }
                    break;
                case 'ethfx':
                    nums = datasel[0].length;
                    break;
                case 'sbthdx': // 三不同号单选
                    if (datasel[0].length >= 3) {
                        nums += ArrayUtil.ComNum(datasel[0].length, 3);
                    }
                    break;
                case 'sthdx': // 三同号单选
                case 'hezhi': // 和值
                    nums = datasel[0].length;
                    break;
                case 'sthtx': // 三同号通选
                case 'slhtx': // 三连号通选
                    nums = datasel[0].length > 0 ? 1 : 0;
                    break;
                case 'hzdxds':
                	maxplace = 1;
                	if (datasel.length == maxplace) {
                        for (var i = 0; i < maxplace; i++) {
                            // 有位置上没有选择
                            if (datasel[i].length == 0) {
                                tmp_nums = 0;
                                break;
                            }
                            tmp_nums *= datasel[i].length;
                        }
                        nums += tmp_nums;
                    }
                	break;
                default:
                    break;
            }
            return nums;
        }

        /**
         * 验证是否超过最大投注注数(80%)
         */
        var _validateMinMaxNumbers = function(type, num, datasel) {
            var rule = PlayRules[type];
            if (!rule) {
                return null;
            }
            var minStr = rule.minNum;
            var maxStr = rule.maxNum;
            if (!minStr) {
                minStr = '0';
            }
            if (!maxStr) {
                maxStr = '0';
            }
            if ('0' == minStr && '0' == maxStr) {
                return null;
            }

            switch (type) {
                // 二不同号
                case "ebthdx":
                case "ebthds":
                // 三不同号
                case "sbthdx":
                case "sbthds":
                // 和值
                case "hezhi":
                    var minNum = parseInt(minStr);
                    var maxNum = parseInt(maxStr);
                    if (num < minNum) {
                        return [1, minNum, num, "注"];
                    }
                    else if (num > maxNum) {
                        return [2, maxNum, num, "注"];
                    }
                    return null;
                default :
                    return null;
            }
        }

        var _formatSelect_Num = function(datasel, m, n) {
            var newsel = new Array();
            if(!m) m = 0;
            if(!n) n = 0;
            for (var i = 0; i < m; i++) {
                newsel.push('-');
            }
            for (var i = 0; i < datasel.length; i++) {
                var f = datasel[i].toString().replace(/\,/g, '');
                if(f == '') {
                    newsel.push('-');
                } else {
                    newsel.push(f);
                }
            }
            for (var i = 0; i < n; i++) {
                newsel.push('-');
            }
            return newsel.toString();
        }

        var _formatTextarea_Num = function(type, datasel) {
            switch (type) {
                case 'ebthds':
                    datasel = _inputCheck_Num(datasel, 2, _ebthdsCheck);
                    break;
                case 'ethds':
                    datasel = _inputCheck_Num(datasel, 3, _ethdsCheck);
                    break;
                case 'sbthds':
                    datasel = _inputCheck_Num(datasel, 3, _sbthdsCheck);
                    break;
                default:
                    break;
            }
            return datasel.toString().replace(/\,/g, ' ');
        }

        var _inputFormat = function(type, datasel) {
            switch (type) {
                case 'ebthds':
                case 'ethds':
                case 'sbthds':
                    return _formatTextarea_Num(type, datasel);
                case 'ebthdx':
                case 'ethfx':
                case 'sbthdx':
                case 'sthdx':
                case 'sthtx':
                case 'slhtx':
                case 'hezhi':
                case 'hzdxds':
                    return datasel[0].toString();
                case 'ebthdt':
                case 'ethdx':
                    return _formatSelect_Num(datasel);
                default:
                    break;
            }
        }

        var _typeFormat = function(code) {
            var arr = [];
            code.sort();
            var a = code[0];
            var b = code[1];
            var c = code[2];
            var sanlian = ['123', '234', '345', '456'];
            if($.inArray(code.toString().replace(/\,/g , ''), sanlian) != -1) {
                arr[0] = '三连号';
            } else if(a == b && b == c) {
                arr[0] = '三同号';
            } else if(a == b || b == c) {
                arr[0] = '二同号';
            }else {
                arr[0] = '三不同';
            }
            arr[1] = parseInt(a) + parseInt(b) + parseInt(c);
            return arr;
        }

        return {
            inputNumbers: _inputNumbers,
            inputFormat: _inputFormat,
            typeFormat: _typeFormat,
            validateMinMaxNumbers: _validateMinMaxNumbers
        }
    }();


    // 布局
    var layout = [{
        title: '二不同号',
        code: 'k3ebt',
        rows: [[{
            title: '二不同号',
            columns: [{
                name: '标准选号',
                code: 'ebthdx',
                realname: '[二不同号_标准选号]',
                tips: '从1-6中任意选择2个或2个以上号码。',
                example: '投注方案：2,5；开奖号码中出现：1个2、1个5 (顺序不限)，即中奖。',
                help: '从1-6中任意选择2个号码组成一注，顺序不限。开奖号码中出现所选的两个号码即为中奖。',
                select: {
                    layout: [{
                        title: '号码',
                        balls: [1,2,3,4,5,6]
                    }]
                }
            }, {
                name: '手动选号',
                code: 'ebthds',
                realname: '[二不同号_手动选号]',
                tips: '手动输入号码，至少输入1-6中两个不同的数字组成一注号码。',
                example: '投注方案：56；开奖号码：536，即中奖。',
                help: '开奖号码中至少包含所输入的两个数字即为中奖。',
                textarea: {}
            }, {
                name: '胆拖选号',
                code: 'ebthdt',
                realname: '[二不同号_胆拖选号]',
                tips: '从1-6中任意选择1个胆码以及1个以上的号码作为拖码。',
                example: '',
                help: '从1-6中选择一个胆码和至少一个拖码，如果开奖号码中不重复数字至少包含胆码所选号码即为中奖',
                select: {
                    layout: [{
                        title: '胆码',
                        balls: [1,2,3,4,5,6],
                        trigger: 'only'
                    }, {
                        title: '拖码',
                        balls: [1,2,3,4,5,6]
                    }]
                }
            }]
        }]]
    }, {
        title: '二同号',
        code: 'k3et',
        rows: [[{
            title: '二同号单选',
            columns: [{
                name: '标准选号',
                code: 'ethdx',
                realname: '[二同号_标准选号]',
                tips: '选择1个对子（11,22,33,44,55,66）和1个不同号码(1,2,3,4,5,6)投注。',
                example: '投注方案：112；开奖号码为112,121,211中任意一个，即中奖。',
                help: '选择1个对子（11,22,33,44,55,66）和1个不同号码(1,2,3,4,5,6)投注，选号与开奖号码一致即中奖。',
                select: {
                    layout: [{
                        title: '二同号',
                        balls: ['11','22','33','44','55','66'],
                        values: [1,2,3,4,5,6],
                        trigger: 'only'
                    }, {
                        title: '不同号',
                        balls: [1,2,3,4,5,6]
                    }]
                }
            }, {
                name: '手动选号',
                code: 'ethds',
                realname: '[二同号_手动选号]',
                tips: '手动输入号码，至少输入1个三位数号码组成一注。',
                example: '投注方案：112；开奖号码为112,121,211中任意一个，即中奖。',
                help: '手动输入号码，至少输入1个三位数号码，选号与开奖号码一致即中奖。',
                textarea: {}
            }]
        }], [{
            title: '二同号复选',
            columns: [{
                name: '二同号复选',
                code: 'ethfx',
                realname: '[二同号_复选]',
                tips: '选择对子(11*,22*,33*,44*,55*,66*)进行投注。',
                example: '投注方案：11*；开奖号码为：112,211,121,即中奖。',
                help: '选择对子(11*,22*,33*,44*,55*,66*)投注，开奖号码中包含选择的对子即中奖。',
                select: {
                    layout: [{
                        title: '二同号',
                        balls: ['11*','22*','33*','44*','55*','66*'],
                        values: [1,2,3,4,5,6]
                    }]
                }
            }]
        }]]
    }, {
        title: '三不同号',
        code: 'k3sbt',
        rows: [[{
            title: '三不同号',
            columns: [{
                name: '标准选号',
                code: 'sbthdx',
                realname: '[三不同号_标准选号]',
                tips: '选择任意三个或以上的号码进行投注。',
                example: '投注方案：2,5,6；开奖号码中出现：1个2、1个5、1个6(顺序不限)即中奖。',
                help: '从1-6中任意选择3个或3个以上不相同号码组成一注，顺序不限，若其中三位与开奖号码相同即为中奖。',
                select: {
                    layout: [{
                        title: '号码',
                        balls: [1,2,3,4,5,6]
                    }]
                }
            }, {
                name: '手动选号',
                code: 'sbthds',
                realname: '[三不同号_手动选号]',
                tips: '对三个各不相同的号码进行投注。',
                example: '投注方案：258；开奖号码中出现：1个2、1个5、1个8 (顺序不限)，即中奖。。',
                help: '手动输入1-6中任意选择3个不相同号码组成一注，顺序不限，若开奖号码与所选号码相同，即为中奖。',
                textarea: {}
            }]
        }]]
    }, {
        title: '三同号',
        code: 'k3st',
        rows: [[{
            title: '三同号单选',
            columns: [{
                name: '三同号单选',
                code: 'sthdx',
                realname: '[三同号单选]',
                tips: '选择任意一组以上三位相同的号码。',
                example: '投注方案：222；开奖号码为：3个2，即中奖。',
                help: '从111,222,333,444,555，666中选择任意一组或一组以上的号码，若和开奖号相同即为中奖。',
                select: {
                    layout: [{
                        title: '号码',
                        balls: ['111','222','333','444','555','666'],
                        values: [1,2,3,4,5,6]
                    }]
                }
            }]
        }], [{
            title: '三同号通选',
            columns: [{
                name: '三同号通选',
                code: 'sthtx',
                realname: '[三同号通选]',
                tips: '对所有三同号（111,222,333,444,555,666）进行投注。',
                example: '投注方案：通选；开奖号码中出现：222或3个其他数字，即中奖。',
                help: '投注后，开奖号码为任意数字的三重号，即为中奖。',
                select: {
                    layout: [{
                        title: '胆码',
                        balls: ['111','222','333','444','555','666'],
                        trigger: 'all'
                    }]
                }
            }]
        }]]
    }, {
        title: '三连号',
        code: 'k3sl',
        rows: [[{
            title: '三连号通选',
            columns: [{
                name: '三连号通选',
                code: 'slhtx',
                realname: '[三同号连选]',
                tips: '对所有三个相连的号码进行投注。',
                example: '投注方案：三连号通选 开奖号码：123或234或345或456   即为中奖',
                help: '开奖号码为3连号（123,234,345,456）即为中奖',
                select: {
                    layout: [{
                        title: '号码',
                        balls: ['123','234','345','456'],
                        trigger: 'all'
                    }]
                }
            }]
        }]]
    }, {
        title: '和值',
        code: 'k3hz',
        rows: [[{
            title: '和值',
            columns: [{
                name: '和值',
                code: 'hezhi',
                realname: '[和值]',
                tips: '从3-18中任意选择1个或1个以上号码。',
                example: '投注方案：和值4；开奖号码：112,即中奖。',
                help: '所选数值等于开奖号码三个数字相加之和，即为中奖。注意:和值为3或18一等奖；4或17二等奖；5或16三等奖；6或15四等奖；7或14五等奖；8或13六等奖；9或12七等奖；10或11八等奖。',
                select: {
                    layout: [{
                        balls: [3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18],
                        cls: 'hz'
                    }]
                },
                prizeIndexes:[0,1,2,3,4,5,6,7,7,6,5,4,3,2,1,0]
            },{
                name: '总和大小单双',
                code: 'hzdxds',
                realname: '[_总和大小单双]',
                tips: '从总和大、小、单、双中任意选择1个号码形态组成一注。',
                example: '投注方案：总和大。开奖号码：166，开奖号码的总和13，即中总和大。',
                help: '从总和大、小、单、双中任意选择1个号码形态组成一注，只要所选形态与开奖号码3位数号码总和(大（11-18）、小（3-10）、单（奇数）、双（偶数）形态相同，即为中奖。',
                select: {
                    layout: [{
                        title: '总和大小单双',
                        balls: ['总和大','总和小', '总和单', '总和双']
                    }]
                }
            }]
        }]]
    }];

    // 渲染玩法组
    function renderBettingCondition() {
        $bettingTab.html(tpl('#betting_tab_tpl', {
            items: layout,
            groups: PlayRulesGroup
        }))
    }

    // 渲染玩法
    function renderSearch(index) {
        $bettingCondition.html(tpl('#betting_condition_tpl', {
            items: layout[index].rows,
            rules: PlayRules
        }))
    }

    // 渲染投注区
    function renderBettingArea(data) {
        $bettingArea.html(tpl('#betting_area_tpl', {
            data: data
        }))
    }

    function init() {
        renderBettingCondition();
        var groupLength  = Object.keys(PlayRulesGroup).length ;
        if (groupLength <= 0) {
            $.YF.alert_error('该彩种暂没有可用玩法', function(){
                window.location.href = '/center';
            })
            return;
        }
        var checkIndex = groupLength > 1 ? 1 : 0;
        $('.tab-btn:eq('+checkIndex+')', $bettingTab).trigger('click'); // 默认选中玩法组
    }

    function initOptions(model){
        var _model = model;
        if (!_model) {
            _model = $.cookie(USER_BDATA_MODEL_COOKIE_KEY);
        }

        // 初始化选项栏
        playOptions.html(tpl('#play_options_tpl',{
            model:_model||"yuan"
        }))

        adjustPrize = $('.adjust-prize', playOptions);
        $multiple = $('input[name="multiple"]', playOptions)

        $cartNonList = $('#shoppingNonCar', $rightBanner);
        $cartlist = $('#shoppingCar', $rightBanner);
        $statistics = $('#statistics', $rightBanner);
        $modelBtn = $('#model', playOptions);
        $adjustPrizeBox = $('#adjustPrizeBox', playOptions);
        $sliderBox = $('#sliderBox', playOptions);

        buildPlayOptions(playOptions)
        buildAdjustPrize(adjustPrize, activeData)
    }

    var PlayOptions = function() {
        var els = function() {
            return playOptions
        }
        var multiple = function() {
            return Number(els().find('input[name="multiple"]').val());
        }
        var model = function() {
            var data
            var val = $('span.cur', $modelBtn).attr("data-val")
            switch (val) {
                case 'yuan':
                    data = { val: val, money: 1 }
                    break
                case 'jiao':
                    data = { val: val, money: 0.1 }
                    break
                case 'fen':
                    data = { val: val, money: 0.01 }
                    break
                case 'li':
                    data = { val: val, money: 0.001 }
                    break
                case '1yuan':
                    data = { val: val, money: 0.5 }
                    break
                case '1jiao':
                    data = { val: val, money: 0.05 }
                    break
                case '1fen':
                    data = { val: val, money: 0.005 }
                    break
                case '1li':
                    data = { val: val, money: 0.0005 }
                    break
            }
            return data
        }
        var update = function() {
            var num = LotteryUtils.inputNumbers(bData.code, getData());
            var inputMultiple = multiple();
            var total = inputMultiple * num * bUnitMoney * model().money;
            els().find('[data-property="num"]').html(num);
            els().find('[data-property="total"]').html($.YF.setMaxScale(total, 3));
            els().find('[data-property="multiple"]').html(inputMultiple);
            AdjustPrize.update();
        }
        return { els: els, multiple: multiple, model: model, update: update };
    }();

    // 奖金调节获取值
    var AdjustPrize = function() {
        var els = function() {
            return adjustPrize;
        }
        var code = function() {
            return Number(els().find('[data-property="code"]').html());
        }
        var point = function() {
            return Number(els().find('[data-property="point"]').attr('data-val'));
        }
        var slider = function() {
            els().find('div.slider').noUiSlider({ range: { 'max': uMaxCode, 'min': uMinCode } }, true);
        }
        var update = function() {
            var rule = PlayRules[bData.code];
            if (rule) {
                var ps = rule.prize.split(',');
                var odds = ps;
                if (ps.length > 1 && activeData.prizeIndexes) {
                    var indexes = getFieldsetsBallIndexes();
                    if (!indexes || indexes.length <= 0) {
                        indexes = getSelectBallIndexes();
                    }
                    // 有选择
                    if (indexes && indexes.length > 0) {
                        odds = new Array();
                        for (var i = 0; i < indexes.length; i++) {
                            var selectedIndex = indexes[i];
                            var prizeIndex;
                            if (activeData.prizeIndexes === 'selectedIndex') {
                                prizeIndex = selectedIndex;
                            }
                            else {
                                prizeIndex = activeData.prizeIndexes[selectedIndex];
                            }
                            odds.push(ps[prizeIndex]);
                        }
                    }
                }

                if (odds && odds.length > 0){
                    var min = AdjustPrize.prize(ArrayUtil.min(odds));
                    var max = AdjustPrize.prize(ArrayUtil.max(odds));


                    var num = PlayOptions.els().find('[data-property="num"]').html();
                    if (num) {
                        num = Number(num);
                        if (num && num >= 1) {
                            var multiple = PlayOptions.multiple();
                            var predictPrizeMin = $.YF.setMaxScale(multiple * min, 3);
                            var predictPrizeMax = $.YF.setMaxScale(multiple * max, 3);
                            if (predictPrizeMin == predictPrizeMax) {
                                $('[data-property="prize"]', $bettingPrize).html(predictPrizeMin);
                            }
                            else {
                                $('[data-property="prize"]', $bettingPrize).html(predictPrizeMax + '~' + predictPrizeMin);
                            }
                        }
                        else {
                            if (min == max) {
                                var minStr = $.YF.setMaxScale(min, 3);
                                $('[data-property="prize"]', $bettingPrize).html(minStr);
                            }
                            else {
                                var minStr = $.YF.setMaxScale(min, 3);
                                var maxStr = $.YF.setMaxScale(max, 3);
                                $('[data-property="prize"]', $bettingPrize).html(maxStr + '~' + minStr);
                            }
                        }
                    }
                    else {
                        if (min == max) {
                            var minStr = $.YF.setMaxScale(min, 3);
                            $('[data-property="prize"]', $bettingPrize).html(minStr);
                        }
                        else {
                            var minStr = $.YF.setMaxScale(min, 3);
                            var maxStr = $.YF.setMaxScale(max, 3);
                            $('[data-property="prize"]', $bettingPrize).html(maxStr + '~' + minStr);
                        }
                    }
                }
            }
        }
        var prize = function(odd){
            var rule = PlayRules[bData.code];
            if (rule) {
                var mMoney = PlayOptions.model().money;
                if (rule.fixed == false) {
                    var pm = (code() / Number(odd)) * (bUnitMoney / 2) * mMoney;
                    return pm;
                }
                if (rule.fixed == true) {
                    var pm = Number(odd) * (bUnitMoney / 2) * mMoney;
                    return pm;
                }
            }
            return 0;
        }
        return { els: els, code: code, point: point, update: update, slider: slider, prize: prize};
    }();
    // 构造奖金
    var buildAdjustPrize = function(adjustPrize, column) {
        var prizeSlider = adjustPrize.find('.progress');
        prizeSlider.empty();
        prizeSlider.append('<div class="slider"></div>');
        var slider = prizeSlider.find('.slider');
        var start = uMaxCode;
        if ($.cookie(USER_BDATA_CODE_COOKIE_KEY)) {
            var bCode = $.cookie(USER_BDATA_CODE_COOKIE_KEY);
            if (bCode) {
                bCode = Number(bCode);
            }
            if (bCode > uMaxCode) {
                start = uMaxCode;
            }
            else {
                start = bCode;
                if (start < uMinCode) {
                    start = uMinCode;
                }
            }
        }
        slider.noUiSlider({ connect: 'lower', behaviour: 'snap', step: 2, start: start, range: { 'max': uMaxCode, 'min': 1800 } });
        if (uMaxCode <= uMinCode) {
            slider.attr('disabled', 'disabled');
        }
        var update = function(code) {
            var point = uLocPoint - ((code - uMinCode) / 20.0).toFixed(2) + Number(uMinLocPoint);
            adjustPrize.find('[data-property="code"]').html(code);
            adjustPrize.find('[data-property="point"]').html(point.toFixed(2) + '%').attr('data-val', point.toFixed(2));
            AdjustPrize.update();
        }
        var onSet = function() {
            var code = Number(slider.val());
            update(code);
            // SET Cookie Code Value
            var expires = new Date(moment().startOf('year').add(1, 'years'));
            $.cookie(USER_BDATA_CODE_COOKIE_KEY, code, { expires: expires, path: '/' });
        }
        var onSlide = function() {
            var code = Number(slider.val());
            update(code);
        }
        if (uMaxCode > uMinCode) {
            slider.on({ set: onSet, slide: onSlide });
        }
        update(start);
    }

    // 构造选项栏
    var buildPlayOptions = function(playOptions) {
        // 模式
        var bDataModel = 'yuan';
        var cookieModel = $.cookie(USER_BDATA_MODEL_COOKIE_KEY);
        if (cookieModel) {
            var modelName = GlobalFun.formatUserBetsModel(cookieModel);
            if (modelName) {
                bDataModel = cookieModel;
            }
        }
        $('span[data-val='+bDataModel+']', $modelBtn).addClass('cur').siblings().removeClass('cur');

        // 倍数
        var multiple = '1';
        if ($.cookie(USER_BDATA_MULTIPLE_COOKIE_KEY)) {
            multiple = $.cookie(USER_BDATA_MULTIPLE_COOKIE_KEY);
        }
        playOptions.find('input[name=multiple]').val(multiple);
        PlayOptions.update();

        // 分模式
        var fenModel = function() {
            if (Config.fenModelDownCode > 0) {
                var thisCode = Config.sysCode - Config.fenModelDownCode;
                if (UserData.code > thisCode) {
                    uMaxCode = thisCode;
                    $.YF.alert_warning('分模式最高为' + uMaxCode + '。')
                }
            }
        }
        // 厘模式
        var liModel = function() {
            if (Config.liModelDownCode > 0) {
                var thisCode = Config.sysCode - Config.liModelDownCode;
                if (UserData.code > thisCode) {
                    uMaxCode = thisCode;
                    $.YF.alert_warning('厘模式最高为' + uMaxCode + '。')
                }
            }
        }
        // 默认模式
        var defaultModel = function() {
        	uMaxCode = UserData.code > 1920 ? 1920 : UserData.code;
            uMinCode = uMaxCode - uLocPoint * 20;
        }
        // 更新默认
        var update = function(model) {
            if (model == 'fen') {
                fenModel();
            } else if (model == 'li') {
                liModel();
            } else {
                defaultModel();
            }
        }

        // 模式选择
        $('[data-command=changeModel]', $modelBtn).off('click').on('click', function(e) {
            var $this = $(e.target);
            if ($this.hasClass('cur')) {
                return;
            }

            $this.addClass('cur').siblings().removeClass('cur');

            var val = $this.attr("data-val");
            update(val)
            AdjustPrize.slider()
            AdjustPrize.update()
            PlayOptions.update()
            // SET Cookie Model Value
            var expires = new Date(moment().startOf('year').add(1, 'years'))
            $.cookie(USER_BDATA_MODEL_COOKIE_KEY, val, { expires: expires, path: '/' })
        })

        update(bDataModel)
    }

    // 得到用户选择的位置，格式化后的数据
    var getSelectPlace = function(playArea) {
        var places = [];
        var sp = $('dl.places', playArea);
        if (sp && sp.length > 0) {
            $.each(sp, function() {
                var place = [];
                var as = $(this).find('input[type="checkbox"]');
                $.each(as, function() {
                    if ($(this).is(':checked')) {
                        place.push('√');
                    } else {
                        place.push('-');
                    }
                });
                places.push(place);
            });
        }
        return places;
    }

    // 得到用户选择的号码，格式化后的数据
    var getSelectBall = function(playArea) {
        var datasel = [];
        var sb = $('dd.choose-pannel', playArea);
        if (sb && sb.length > 0) {
            $.each(sb, function() {
                var ball = [];
                var as = $(this).find('span.active');
                $.each(as, function() {
                    var val = $(this).attr('data-val');
                    ball.push(val);
                });
                datasel.push(ball);
            });
        }
        return datasel;
    }

    // 得到用户选择的号码的下标
    var getSelectBallIndexes = function(playArea) {
        var indexes = [];
        var sb = $('dd.choose-pannel', playArea);
        if (sb && sb.length > 0) {
            $.each(sb, function() {
                var as = $(this).find('span.active');
                $.each(as, function() {
                    var val = $(this).index();
                    indexes.push(val);
                });
            });
        }
        return indexes;
    }

    // 得到用户选择的号码，格式化后的数据 [和值]
    var getFieldsetsBall = function(playArea) {
        var datasel = [];
        var sb = $('div.betting-fieldset > fieldset', playArea);
        if (sb && sb.length > 0) {
            $.each(sb, function() {
                var ball = [];
                var as = $(this).find('span.active');
                $.each(as, function() {
                    var val = $(this).attr('data-val');
                    ball.push(val);
                });
                datasel.push(ball);
            });
        }
        return datasel;
    }

    // 得到用户选择的号码的下标 [和值]
    var getFieldsetsBallIndexes = function(playArea) {
        var indexes = [];
        var sb = $('div.betting-fieldset > fieldset', playArea);
        if (sb && sb.length > 0) {
            $.each(sb, function() {
                var as = $(this).find('span.active');
                $.each(as, function() {
                    var val = $(this).index()-1;
                    indexes.push(val);
                });
            });
        }
        return indexes;
    }

    // 得到用户输入的号码，格式化后的数据
    var getTextareaBall = function(playArea) {
        var datasel = [];
        var $textarea = $('#textarea', playArea);
        if ($textarea.length > 0) {
            var format = $textarea.val().replace(textAreaReplaceRegex, textAreaReplaceWith);
            var as = format.split(textAreaReplaceWith);
            $.each(as, function(idx, val) {
                datasel.push(val);
            });
        }
        return datasel;
    }

    function getData() {
        var datasel = []
        var places = getSelectPlace($bettingArea)
        var balls = getSelectBall($bettingArea)
        var textarea = getTextareaBall($bettingArea)
        var fballs = getFieldsetsBall($bettingArea)
        return datasel.concat(places).concat(balls).concat(textarea).concat(fballs)
    }

    // 玩法组点击
    $bettingTab.on('click', '.tab-btn', function(e) {
        var index
        var $target = $(e.target)
        if ($target.hasClass('cur'))
            return
        $target = $(e.target)
        index = $target.attr('data-index');
        $('.tab-btn.cur', $bettingTab).removeClass('cur')
        $target.addClass('cur')

        renderSearch(index)
        $('.rule-btn:first', $bettingCondition).trigger('click'); // 默认选中第1个

        initOptions();
    })

    // 玩法点击
    $bettingCondition.on('click', '.rule-btn', function(e) {

        var id
        var col
        var pid
        var data
        var index
        var $target = $(e.target)
        if ($target.hasClass('cur'))
            return
        $target = $(e.target)
        id = $target.attr('data-id')
        col = $target.attr('data-col')
        pid = $target.attr('data-pid')
        index = $('.tab-btn.cur', $bettingTab).attr('data-index'); // 选中的玩法组

        activeData = layout[index].rows[pid][col].columns[id]

        $('.rule-btn.cur', $bettingCondition).removeClass('cur')
        $target.addClass('cur')
        renderBettingArea(activeData)
        // 用户选中的方法
        bData.code = activeData.code
        bData.ruleId = PlayRules[bData.code].ruleId
        // 是否加密传输
        bData.compressed = activeData.compressed
        initOptions();

        // 设置tip
        $playRulesTipBox.find('[data-property=tips]').html(activeData.tips);
        $playRulesTipBox.find('[data-property=example]').html(activeData.example);
        $playRulesTipBox.find('[data-property=help]').html(activeData.help);

        // 触发玩法选中事件，给右边的历史号码调整高度
        $(document).trigger('rule.changed');
    })

    // 工具按钮
    $bettingArea.on('click', 'dd.tools .tool-btn', function(e) {
        var $target = $(e.target)
        var $balls = $target.closest('dl').find('dd.choose-pannel > span')
        var max = Math.round($balls.length / 2)
        switch ($target.attr('data-command')) {
            case 'all':
                $balls.addClass('active')
                break
            case 'big':
                $.each($balls, function(i, d) {
                    $(d)[i > max - 1 ? 'addClass' : 'removeClass']('active')
                })
                break
            case 'small':
                $.each($balls, function(i, d) {
                    $(d)[i < max ? 'addClass' : 'removeClass']('active')
                })
                break
            case 'single':
                $.each($balls, function(i, d) {
                    $(d)[parseInt(d.innerHTML) % 2 > 0 ? 'addClass' : 'removeClass']('active')
                })
                break
            case 'double':
                $.each($balls, function(i, d) {
                    $(d)[parseInt(d.innerHTML) % 2 < 1 ? 'addClass' : 'removeClass']('active')
                })
                break
            case 'clean':
                $balls.removeClass('active')
                break
        }
        PlayOptions.update();
    })

    // 选择类球点击
    $bettingArea.on('click', 'dd.choose-pannel span', function(e) {
        var $target = $(e.target)
        $target[$target.hasClass('active') ? 'removeClass' : 'addClass']('active')
        switch ($target.closest('dd.choose-pannel').attr('choose-type')) {
            case 'only':
                $target.siblings('span.active').removeClass('active')
                break
            case 'all':
                $target.siblings('span')[$target.hasClass('active') ? 'addClass' : 'removeClass']('active')
                break
        }
        PlayOptions.update()
    })

    // 和值球点击
    $bettingArea.on('click', 'div.betting-fieldset fieldset span', function(e) {
        var $target = $(e.target)
        $target[$target.hasClass('active') ? 'removeClass' : 'addClass']('active')
        PlayOptions.update()
    })

    // 任选类选择框
    $bettingArea.on('change', 'div.betting-checkbox dl.places input[type=checkbox]', function(e) {
        PlayOptions.update()
    })

    // 输入值
    $bettingArea.on('keyup', '#textarea', function(e) {
        var $target = $(e.target)
        $target.val($target.val().replace(textAreaReplaceRegex, textAreaReplaceWith))
        PlayOptions.update()
    })

    // 倍数加减
    playOptions.on('keyup', 'input[name="multiple"]', function(e) {
        var val
        var $target = $(e.target)
        val = $target.val()
        if (val === '')
            return
        if (val.indexOf('0') === 0) {
            $target.val(Number(val) === 0 ? '1' : Number(val));
            return;
        }
        if (/^[0-9]*$/.test(val)) {
            val = Number(val);
            if (val > 10000) $(this).val(10000);
            if (val < 1) $(this).val(1);
            PlayOptions.update();
        } else {
            $(this).val(1);
            PlayOptions.update();
        }

        var multiple = $target.val();
        var expires = new Date(moment().startOf('year').add(1, 'years'));
        $.cookie(USER_BDATA_MULTIPLE_COOKIE_KEY, multiple, { expires: expires, path: '/' });
    })

    // 倍数加减
    playOptions.on('keydown', 'input[name="multiple"]', function(e) {
        var val
        var $target = $(e.target)
        if (e.keyCode == 38 || e.keyCode == 40) {
            val = $target.val()
            if (val === '')
                return
            val = Number(val);
            if (!isNaN(val)) {
                if (e.keyCode === 38)
                    val++
                if (e.keyCode === 40)
                    val--
                if (val > 10000)
                    val = 10000
                if (val < 1)
                    val = 1
                $target.val(val)
            }
        }
    })

    // 倍数加减
    playOptions.on('blur', 'input[name="multiple"]', function(e) {
        var $target = $(e.target)
        if ($target.val() === '') {
            $target.val(1);
            PlayOptions.update();
        }
    })

    // 倍数加减
    playOptions.on('click', 'em.btn-plus, em.btn-minus', function(e) {
        var val = parseInt($multiple.val())
        var $target = $(e.target)
        if ($target.hasClass('btn-minus')) {
            val > 0 && $multiple.val(val - 1)
        } else {
            $multiple.val(val + 1)
        }
        $multiple.trigger('keyup')
    })

    // 倍数加减
    var continuousIntervalMultipleId;
    var continuousTimeoutMultipleId;
    var continuousInterval = 50;
    playOptions.on('mousedown', 'em.btn-plus', function(e) {
        clearTimeout(continuousTimeoutMultipleId);
        clearInterval(continuousIntervalMultipleId);
        var currentTotalTime = 0;

        continuousTimeoutMultipleId = setTimeout(function(){
            var currentMultiple = parseInt($multiple.val())
            continuousIntervalMultipleId = setInterval(function () {
                currentTotalTime += continuousInterval;
                var add = 1;
                // 根据连续点时间，递进值也不同
                if (currentTotalTime < 1000) {
                    add = 1;
                }
                else if (currentTotalTime < 1500) {
                    add = 10;
                }
                else if (currentTotalTime < 2500) {
                    add = 100;
                }
                else {
                    add = 200;
                }

                currentMultiple += add;

                if (currentMultiple < 10000) {
                    $multiple.val(currentMultiple)
                }
                else {
                    clearInterval(continuousIntervalMultipleId);
                    currentMultiple = 10000;
                    $multiple.val(currentMultiple)
                }
            }, continuousInterval);
        }, 280);
    })
    playOptions.on('mousedown', 'em.btn-minus', function(e) {
        clearTimeout(continuousTimeoutMultipleId);
        clearInterval(continuousIntervalMultipleId);
        var currentTotalTime = 0;

        continuousTimeoutMultipleId = setTimeout(function(){
            var currentMultiple = parseInt($multiple.val())
            continuousIntervalMultipleId = setInterval(function () {
                currentTotalTime += continuousInterval;
                var add = 1;
                // 根据连续点时间，递进值也不同
                if (currentTotalTime < 1000) {
                    add = 1;
                }
                else if (currentTotalTime < 1500) {
                    add = 10;
                }
                else if (currentTotalTime < 2500) {
                    add = 100;
                }
                else {
                    add = 200;
                }

                currentMultiple -= add;

                if (currentMultiple > 1) {
                    $multiple.val(currentMultiple)
                }
                else {
                    clearInterval(continuousIntervalMultipleId);
                    currentMultiple = 1;
                    $multiple.val(currentMultiple)
                }
            }, continuousInterval);
        }, 280);
    })
    playOptions.on('mouseup', 'em.btn-plus,em.btn-minus', function(e) {
        if (continuousTimeoutMultipleId) {
            clearTimeout(continuousTimeoutMultipleId);
        }
        if (continuousIntervalMultipleId) {
            clearInterval(continuousIntervalMultipleId);
            $multiple.trigger('keyup');
        }
    })

    // 清除输入框
    $bettingArea.on('click', '[data-command="clean"]', function(e) {
        var $target = $(e.target)
        $('#textarea', $bettingArea).val('').trigger('keyup')
    })

    // 获取玩法名称
    function getName(code) {
        var rule = PlayRules[code];
        return '[' + rule.groupName + '_' + rule.name + ']';
    }

    // 加入到购物车
    function addToCart(render, callback) {
        if (Lottery.status != 0) {
            $.YF.alert_warning("该彩种暂停销售，维护中！")
            return;
        }

        $.YF.showLoadingMask();

        var ruleCode = bData.code
        var ruleId = bData.ruleId
        var compressed = bData.compressed
        var datasel = getData()
        var num = LotteryUtils.inputNumbers(ruleCode, datasel)
        var validateMinMaxNumbers = LotteryUtils.validateMinMaxNumbers(ruleCode, num, datasel);
        if (validateMinMaxNumbers && validateMinMaxNumbers.length > 0) {
            var name = getName(ruleCode);
            var exceedType = validateMinMaxNumbers[0] == 1 ? '低于最小' : '超过最大';
            $.YF.hideLoadingMask();
            $.YF.alert_warning("订单"+name+exceedType +"[" + validateMinMaxNumbers[1] + "]" +validateMinMaxNumbers[3] + "限制,当前选择["+ validateMinMaxNumbers[2] + "]" + validateMinMaxNumbers[3] +"！")
            return;
        }
        var codes = LotteryUtils.inputFormat(ruleCode, datasel)
        var code = AdjustPrize.code()
        var point = AdjustPrize.point()
        var multiple = PlayOptions.multiple()
        var model = PlayOptions.model().val
        var total = multiple * num * bUnitMoney * PlayOptions.model().money

        if (num < 1) {
            $.YF.hideLoadingMask();
            $.YF.alert_warning("您还没有选择号码或所选号码不全！")
        } else {
//            if ("li" === model || '1li' === model) {
//                if (total < 0.2) {
//                    $.YF.hideLoadingMask();
//                    $.YF.alert_warning("每单至少投注0.2元！")
//                    return;
//                }
//            }

            if (render) {
                var sum = 0
                // 添加至购物车
                var predict = $('[data-property="prize"]', $bettingPrize).html();
                $("#shoppingCarList .mCSB_container", $cartlist).append(tpl('#cart_list_tpl', {
                    lottery: Lottery.showName,
                    num: num,
                    total: $.YF.setMaxScale(total, 3),
                    name: getName(ruleCode),
                    codes: codes,
                    multiple: multiple,
                    point: point,
                    predict: predict
                })).find('li').each(function(i) {
                    var $this = $(this)
                    sum += Number($this.find('[data-property=total]').html())
                })
                $statistics.find('[data-property=total]').html('<em>￥</em>' + $.YF.setMaxScale(sum, 3));
                $cartNonList.hide();
                $cartlist.show();
            }

            // 加密处理开始
            if (compressed === true && num >= 1000) {
                my_lzma.compress(codes, 1, function(result, error){
                    if (error) {
                        $.YF.hideLoadingMask();
                        $.YF.alert_warning("号码添加失败，请重试！")
                        return;
                    }
                    codes = LZMAUtil.toHex(result);
                    bList.push({ lotteryId: Lottery.id, codes: codes, num: num, ruleId: ruleId, ruleCode: ruleCode, multiple: multiple, model: model, code: code, compressed: compressed });
                    $.YF.hideLoadingMask();
                    callback && callback();
                });
            } else {
                $.YF.hideLoadingMask();
                bList.push({ lotteryId: Lottery.id, codes: codes, num: num, ruleId: ruleId, ruleCode: ruleCode, multiple: multiple, model: model, code: code });
                callback && callback()
            }
            initOptions(model)
        }
    }

    // 清除所有
    function clearAll() {
        $('span[data-val]', $bettingArea).removeClass('active')
        $('[data-command="clean"]', $bettingArea).trigger('click')
        $('[data-command="clean_all"]', $cartlist).trigger('click')
    }

    // 添加订单
    playOptions.on('click', '[data-command="add_to_cart"]', function(e, callback) {
        addToCart(true, function () {
            $('span[data-val]', $bettingArea).removeClass('active')
            $('[data-command="clean"]', $bettingArea).trigger('click')
        });
    })

    // 快速投注
    playOptions.on('click', '[data-command="quick_betting"]', function(e) {
        addToCart(false, function(){
            $('[data-command="submit"]', $cartlist).trigger('click')
            clearAll();
        });
    })

    //梭哈投注
    playOptions.on('click', '[data-command="all_betting"]', function(e) {
        if (Lottery.status != 0) {
            $.YF.alert_warning("该彩种暂停销售，维护中！")
            return;
        }

        $.YF.showLoadingMask();

        var balance = 0;
        var ruleCode = bData.code
        var ruleId = bData.ruleId
        var compressed = bData.compressed
        var datasel = getData();
        var num = LotteryUtils.inputNumbers(ruleCode, datasel);
        var validateMinMaxNumbers = LotteryUtils.validateMinMaxNumbers(ruleCode, num, datasel);
        if (validateMinMaxNumbers && validateMinMaxNumbers.length > 0) {
            var name = getName(ruleCode);
            var exceedType = validateMinMaxNumbers[0] == 1 ? '低于最小' : '超过最大';
            $.YF.hideLoadingMask();
            $.YF.alert_warning("订单"+name+exceedType +"[" + validateMinMaxNumbers[1] + "]" +validateMinMaxNumbers[3] + "限制,当前选择["+ validateMinMaxNumbers[2] + "]" + validateMinMaxNumbers[3] +"！")
            return;
        }
        var codes = LotteryUtils.inputFormat(ruleCode, datasel);
        var code = AdjustPrize.code();
        var point = AdjustPrize.point();
        // var multiple = PlayOptions.multiple();
        var multiple = 1; // 默认1倍
        var model = '1li';
        var total = multiple * num * 0.001 * 1;
        if (num < 1) {
            $.YF.hideLoadingMask();
            $.YF.alert_warning('您还没有选择号码或所选号码不全！');
            return;
        }
        $.ajax({
            url: '/AccountBalance',
            data: {
                platformId: 2
            },
            success: function(res) {
                if (res.error == 0) {
                    balance = Number(res.data.balance);
                }
                else {
                    $.YF.alert_warning(res.message);
                    return;
                }
                if (Number(balance) < total) {
                    $.YF.hideLoadingMask();
                    $.YF.alert_warning('您的账户余额不足,不能梭哈！');
                    return;
                }
//                if ("li" === model || '1li' === model) {
//                    if (Number(balance) < 0.2) {
//                        $.YF.hideLoadingMask();
//                        $.YF.alert_warning("每单至少投注1元！")
//                        return;
//                    }
//                }
                //开始计算倍数(彩票总余额/当前合计)
                var modename = {};
                modename['yuan'] = '2元';
                modename['jiao'] = '2角';
                modename['fen'] = '2分';
                modename['li'] = '2厘';
                modename['1yuan'] = '1元';
                modename['1jiao'] = '1角';
                modename['1fen'] = '1分';
                modename['1li'] = '1厘';
                var modevalarr = {};
                modevalarr['yuan'] = 1;
                modevalarr['jiao'] = 0.1;
                modevalarr['fen'] = 0.01;
                modevalarr['li'] = 0.001;
                modevalarr['1yuan'] = 0.5;
                modevalarr['1jiao'] = 0.05;
                modevalarr['1fen'] = 0.005;
                modevalarr['1li'] = 0.0005;
                
                multiple = Math.floor(balance / total);
                var tempmul = 1;
                for (var key in modevalarr) { 
                	var temptotal =  1 * num * (bUnitMoney*modevalarr[key]) * 1;
                	if(balance > temptotal){
                		tempmul = Math.floor(balance / temptotal);
                		if( tempmul > 0 && tempmul <= 10000){
                			multiple = tempmul;
                			model = key;
                			total = 1 * num * (bUnitMoney*modevalarr[key]) * 1;
                		}
                		
                		if('yuan' == key && tempmul > 10000 ){
                			multiple = 10000;
                			model = key;
                			total = 1 * num * (bUnitMoney*modevalarr[key]) * 1;
                		}
                	}
                }
                if (multiple > 10000) {
                    multiple = 10000;
                }
                if (multiple < 1) {
                    $.YF.hideLoadingMask();
                    $.YF.alert_warning('您的账户余额不足,不能梭哈！');
                    return;
                }

                total *= multiple;

                var questMsg = '<h3 class="height30">当前余额最大可投</h3><h4 class="height25">模式：'+modename[model]+'</h4><h4 class="height25">倍数：'+multiple+'</h4><h4 class="height25">金额：'+total+'</h4><h3 class="height30">确认投注吗？</h3>';
                $.YF.alert_question(questMsg, function(resolve, reject){
                    // 加密处理开始
                    if (compressed === true && num >= 1000) {
                        my_lzma.compress(codes, 1, function(result, error){
                            if (error) {
                            	$.YF.hideLoadingMask();
                                reject();
                                // $.YF.alert_warning('号码添加失败，请重试！');
                                reject('号码添加失败，请重试！');
                                return;
                            }
                            codes = LZMAUtil.toHex(result);

                            bList.length=0;
                            bList.push({ lotteryId: Lottery.id, codes: codes, num: num, ruleId: ruleId, ruleCode: ruleCode, multiple: multiple, model: model, code: code, compressed: true});

                            $.ajax({
                                url: '/UserBetsGeneral',
                                data: {
                                    blist: $.toJSON(bList)
                                },
                                success: function(res) {
                                    if (res.error === 0) {
                                        resolve();
                                    }
                                    else {
                                        // $.YF.alert_warning(res.message);
                                        reject(res.message);
                                    }
                                },
                                complete: function() {
                                    submited = false;
                                    $.YF.hideLoadingMask();
                                }
                            })
                        });
                    } else {
                        bList.length=0;
                        bList.push({ lotteryId: Lottery.id, codes: codes, num: num, ruleId: ruleId, ruleCode: ruleCode, multiple: multiple, model: model, code: code });

                        $.ajax({
                            url: '/UserBetsGeneral',
                            data: {
                                blist: $.toJSON(bList)
                            },
                            success: function(res) {
                                if (res.error === 0) {
                                    resolve();
                                }
                                else {
                                    // $.YF.alert_warning(res.message);
                                    reject(res.message);
                                }
                            },
                            complete: function() {
                                submited = false;
                                $.YF.hideLoadingMask();
                            }
                        })
                    }
                }, function(){
                    $.YF.alert_success('投注成功', function(){
                    	$.YF.hideLoadingMask();
                        clearAll();
                        clearTimeout(refreshOrderId);
                        refreshOrderId = setTimeout(function(){
                            refresh();
                        }, 2000);
                    });
                }, function(){
                    $.YF.hideLoadingMask();
                })
            },
            complete: function(xhr, status) {

            }
        });
    })

    // 删除订单
    $rightBanner.on('click', '[data-command="delete"]', function(e) {
        var sum = 0
        var $li = $(e.target).closest('li')
        ArrayUtil.remove(bList, $li.index())
        $li.remove()
        $("#shoppingCarList .mCSB_container li", $cartlist).each(function(i) {
            var $this = $(this)
            sum += Number($this.find('[data-property=total]').html())
        })
        $statistics.find('[data-property=total]').html($.YF.setMaxScale(sum, 3));

        if (bList.length <= 0) {
            $cartlist.hide();
            $cartNonList.show();
        }
    })

    // 删除所有订单
    $rightBanner.on('click', '[data-command="clean_all"]', function(e,model) {
        bList = []
        $("#shoppingCarList .mCSB_container", $cartlist).html("");
        $statistics.find('[data-property=total]').html('<em>￥</em>0');
        $cartlist.hide();
        $cartNonList.show();
    })

    // 提交订单
    $rightBanner.on('click', '[data-command="submit"]', function(e) {
        if (Lottery.status != 0) {
            $.YF.alert_warning("该彩种暂停销售，维护中！")
            return;
        }

        var $target = $(e.target);
        if (submited || ( typeof($target.attr('disabled')) != "undefined"))
            return
        if (bList.length < 1) {
            $.YF.alert_warning("投注列表没有订单！")
            return;
        }
        submited = true
        $target.attr('disabled', 'disabled')
        $.YF.showLoadingMask();

        $.ajax({
            url: '/UserBetsGeneral',
            data: {
                blist: $.toJSON(bList)
            },
            success: function(res) {
                if (res.error === 0) {
                    $.YF.alert_success('投注成功', function(){
                        clearAll();
                        clearTimeout(refreshOrderId);
                        refreshOrderId = setTimeout(function(){
                            refresh();
                        }, 2000);
                    });
                    return;
                }
                else {
                    $.YF.alert_warning(res.message);
                    return;
                }
            },
            complete: function() {
                submited = false
                $.YF.hideLoadingMask();
                $target.removeAttr('disabled')
            }
        })
    })

    // 追号
    $rightBanner.on('click', '[data-command="chase"]', function(e) {
        if (Lottery.status != 0) {
            $.YF.alert_warning("该彩种暂停销售，维护中！")
            return;
        }

        LotteryChase.init()
    })

    // 玩法规则提示
    $playRulesTipBtn.hover(function(){
        if (activeData.tips || activeData.example || activeData.help) {
            $playRulesTipBox.stop(true, true).fadeIn(300);
        }
    }, function(){
        $playRulesTipBox.stop(true, true).fadeOut(500);
    });

    // 玩法规则提示
    $playRulesTipBox.hover(function(){
        $playRulesTipBox.stop(true, true).show();
    }, function(){
        $playRulesTipBox.stop(true, true).hide();
    });

    init();

    window.LotteryMain = {
        bList: function() {
            return bList
        },
        clear: function() {
            clearAll();
        }
    }
})