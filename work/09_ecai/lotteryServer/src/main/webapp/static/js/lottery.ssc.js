$(function(){
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
    var uMaxCode = UserData.code > 1960 ? 1960 : UserData.code;
    var uMinLocPoint = UserData.code > 1960 ? ((Number((UserData.code - 1960))> 0) ? (Number((UserData.code - 1960)) / 20) : 0).toFixed(2) : 0;
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
        var _inputCheck_Num = function(datasel, l, fun, sort) {
            fun = $.isFunction(fun) ? fun : function(n, l) {
                return true;
            }
            var newsel = []; // 新的号码
            if (sort) { // 如果需要号码排序
                var sortsel = [];
                $.each(datasel, function(i, n) {
                    sortsel.push(n.split('').sort().toString().replace(/\,/g, ''));
                });
                datasel = sortsel;
            }
            datasel = ArrayUtil.unique(datasel); // 去除重复

            var regex = new RegExp('^[0-9]{' + l + '}$');
            $.each(datasel, function(i, n) {
                if (regex.test(n) && fun(n, l)) {
                    newsel.push(n);
                }
            });

            return newsel;
        }

        /**
         * 和值检测
         */
        var _HHZXCheck_Num = function(n, l) {
            var a = new Array();
            if (l == 2) { //两位
                a = ['00', '11', '22', '33', '44', '55', '66', '77', '88', '99'];
            } else { //三位[默认]
                a = ['000', '111', '222', '333', '444', '555', '666', '777', '888', '999'];
            }
            return $.inArray(n, a) == -1 ? true : false;
        }

        /**
         * 多少注计算
         */
        var _inputNumbers = function(type, datasel) {
            var nums = 0,
                tmp_nums = 1;
            switch (type) {
                case 'rx3z3':
                    var maxplace = 1;
                    if (datasel.length > 1) {
                        var place = 0;
                        for (var i = 0; i < datasel[0].length; i++) {
                            if (datasel[0][i] == '√') place++;
                        }
                        var newsel = datasel[1];
                        var m = 3;
                        // 任选3必须大于选了3位以上才能组成组合
                        if (place >= m) {
                            var h = ArrayUtil.ComNum(place, m);
                            if (h > 0) { // 组合数必须大于0
                                for (var i = 0; i < maxplace; i++) {
                                    var s = newsel.length;
                                    // 组三必须选两位或者以上
                                    if (s > 1) {
                                        nums += s * (s - 1);
                                    }
                                }
                                nums *= h;
                            }
                        }
                    }
                    break;
                case 'rx3z6':
                    var maxplace = 1;
                    if (datasel.length > 1) {
                        var place = 0;
                        for (var i = 0; i < datasel[0].length; i++) {
                            if (datasel[0][i] == '√') place++;
                        }
                        var newsel = datasel[1];
                        var m = 3;
                        // 任选3必须大于选了3位以上才能组成组合
                        if (place >= m) {
                            var h = ArrayUtil.ComNum(place, m);
                            if (h > 0) { // 组合数必须大于0
                                for (var i = 0; i < maxplace; i++) {
                                    var s = newsel.length;
                                    // 组六必须选三位或者以上
                                    if (s > 2) {
                                        nums += s * (s - 1) * (s - 2) / 6;
                                    }
                                }
                                nums *= h;
                            }
                        }
                    }
                    break;
                case 'rx2zx':
                    var maxplace = 1;
                    if (datasel.length > 1) {
                        var place = 0;
                        for (var i = 0; i < datasel[0].length; i++) {
                            if (datasel[0][i] == '√') place++;
                        }
                        var newsel = datasel[1];
                        var m = 2;
                        // 任选2必须大于选了2位以上才能组成组合
                        if (place >= m) {
                            var h = ArrayUtil.ComNum(place, m);
                            if (h > 0) { // 组合数必须大于0
                                for (var i = 0; i < maxplace; i++) {
                                    var s = newsel.length;
                                    // 二码不定位必须选两位或者以上
                                    if (s > 1) {
                                        nums += s * (s - 1) / 2;
                                    }
                                }
                                nums *= h;
                            }
                        }
                    }
                    break;
                case 'rx2ds':
                case 'rx3ds':
                case 'rx4ds':
                    if (datasel.length > 1) {
                        var place = 0;
                        for (var i = 0; i < datasel[0].length; i++) {
                            if (datasel[0][i] == '√') place++;
                        }
                        var newsel = [];
                        for (var i = 1; i < datasel.length; i++) {
                            newsel.push(datasel[i]);
                        }
                        var m = 0;
                        if (type == 'rx2ds') {
                            m = 2;
                        }
                        if (type == 'rx3ds') {
                            m = 3;
                        }
                        if (type == 'rx4ds') {
                            m = 4;
                        }
                        // 任选2必须大于选了2位以上才能组成组合
                        if (place >= m) {
                            var h = ArrayUtil.ComNum(place, m);
                            if (h > 0) { // 组合数必须大于0
                                nums += _inputCheck_Num(newsel, m).length;
                                nums *= h;
                            }
                        }
                    }
                    break;
                case 'rx3hhzx':
                    if (datasel.length > 1) {
                        var place = 0;
                        for (var i = 0; i < datasel[0].length; i++) {
                            if (datasel[0][i] == '√') place++;
                        }
                        var newsel = [];
                        for (var i = 1; i < datasel.length; i++) {
                            newsel.push(datasel[i]);
                        }
                        var m = 3; // 必须选择3个以上位置才可以
                        if (place >= m) {
                            var h = ArrayUtil.ComNum(place, m);
                            if (h > 0) { // 组合数必须大于0
                                nums += _inputCheck_Num(newsel, 3, _HHZXCheck_Num, true).length;
                                nums *= h;
                            }
                        }
                    }
                    break;
                case 'wxzhixds':
                    nums = _inputCheck_Num(datasel, 5).length;
                    break;
                case 'sixzhixdsh':
                case 'sixzhixdsq':
                    nums = _inputCheck_Num(datasel, 4).length;
                    break;
                case 'sxzhixdsh':
                case 'sxzhixdsz':
                case 'sxzhixdsq':
                    nums = _inputCheck_Num(datasel, 3).length;
                    break;
                case 'sxhhzxh':
                case 'sxhhzxz':
                case 'sxhhzxq':
                    nums = _inputCheck_Num(datasel, 3, _HHZXCheck_Num, true).length;
                    break;
                case 'exzhixdsh':
                case 'exzhixdsq':
                    nums = _inputCheck_Num(datasel, 2).length;
                    break;
                case 'exzuxdsh':
                case 'exzuxdsq':
                    nums = _inputCheck_Num(datasel, 2, _HHZXCheck_Num, true).length;
                    break;
                case 'wxzux120':
                    var s = datasel[0].length;
                    if (s > 4) {
                        nums += ArrayUtil.ComNum(s, 5);
                    }
                    break;
                case 'wxzux60':
                case 'wxzux30':
                case 'wxzux20':
                case 'wxzux10':
                case 'wxzux5':
                    var minchosen = new Array();
                    if (type == 'wxzux60') {
                        minchosen = [1, 3];
                    }
                    if (type == 'wxzux30') {
                        minchosen = [2, 1];
                    }
                    if (type == 'wxzux20') {
                        minchosen = [1, 2];
                    }
                    if (type == 'wxzux10' || type == 'wxzux5') {
                        minchosen = [1, 1];
                    }
                    if (datasel[0].length >= minchosen[0] && datasel[1].length >= minchosen[1]) {
                        var h = ArrayUtil.intersect(datasel[0], datasel[1]).length;
                        tmp_nums = ArrayUtil.ComNum(datasel[0].length, minchosen[0]) * ArrayUtil.ComNum(datasel[1].length, minchosen[1]);
                        if (h > 0) {
                            if (type == 'wxzux60') {
                                tmp_nums -= ArrayUtil.ComNum(h, 1) * ArrayUtil.ComNum(datasel[1].length - 1, 2);
                            }
                            if (type == 'wxzux30') {
                                tmp_nums -= ArrayUtil.ComNum(h, 2) * ArrayUtil.ComNum(2, 1);
                                if (datasel[0].length - h > 0) {
                                    tmp_nums -= ArrayUtil.ComNum(h, 1) * ArrayUtil.ComNum(datasel[0].length - h, 1);
                                }
                            }
                            if (type == 'wxzux20') {
                                tmp_nums -= ArrayUtil.ComNum(h, 1) * ArrayUtil.ComNum(datasel[1].length - 1, 1);
                            }
                            if (type == 'wxzux10' || type == 'wxzux5') {
                                tmp_nums -= ArrayUtil.ComNum(h, 1);
                            }
                        }
                        nums += tmp_nums;
                    }
                    break;
                case 'sixzux24h':
                case 'sixzux24q':
                    var s = datasel[0].length;
                    if (s > 3) {
                        nums += ArrayUtil.ComNum(s, 4);
                    }
                    break;
                case 'sixzux6h':
                case 'sixzux6q':
                    var minchosen = [2];
                    if (datasel[0].length >= minchosen[0]) {
                        nums += ArrayUtil.ComNum(datasel[0].length, minchosen[0]);
                    }
                    break;
                case 'sixzux12h':
                case 'sixzux12q':
                case 'sixzux4h':
                case 'sixzux4q':
                    var minchosen = new Array();
                    if (type == 'sixzux12h' || type == 'sixzux12q') {
                        minchosen = [1, 2];
                    }
                    if (type == 'sixzux4h' || type == 'sixzux4q') {
                        minchosen = [1, 1];
                    }
                    if (datasel[0].length >= minchosen[0] && datasel[1].length >= minchosen[1]) {
                        var h = ArrayUtil.intersect(datasel[0], datasel[1]).length;
                        tmp_nums = ArrayUtil.ComNum(datasel[0].length, minchosen[0]) * ArrayUtil.ComNum(datasel[1].length, minchosen[1]);
                        if (h > 0) {
                            if (type == 'sixzux12h' || type == 'sixzux12q') {
                                tmp_nums -= ArrayUtil.ComNum(h, 1) * ArrayUtil.ComNum(datasel[1].length - 1, 1);
                            }
                            if (type == 'sixzux4h' || type == 'sixzux4q') {
                                tmp_nums -= ArrayUtil.ComNum(h, 1);
                            }
                        }
                        nums += tmp_nums;
                    }
                    break;
                case 'sxzuxzsh':
                case 'sxzuxzsz':
                case 'sxzuxzsq':
                    var maxplace = 1;
                    for (var i = 0; i < maxplace; i++) {
                        var s = datasel[i].length;
                        // 组三必须选两位或者以上
                        if (s > 1) {
                            nums += s * (s - 1);
                        }
                    }
                    break;
                case 'sxzuxzlh':
                case 'sxzuxzlz':
                case 'sxzuxzlq':
                case 'bdwwx3m':
                    var maxplace = 1;
                    for (var i = 0; i < maxplace; i++) {
                        var s = datasel[i].length;
                        // 组六或不定位五星三码必须选三位或者以上
                        if (s > 2) {
                            nums += s * (s - 1) * (s - 2) / 6;
                        }
                    }
                    break;
                case 'wxzhixzh':
                case 'sixzhixzhh':
                case 'sixzhixzhq':
                    var maxplace = 0;
                    if ('wxzhixzh' == type) {
                        maxplace = 5;
                    }
                    if ('sixzhixzhh' == type || 'sixzhixzhq' == type) {
                        maxplace = 4;
                    }
                    for (var i = 0; i < maxplace; i++) {
                        // 有位置上没有选择
                        if (datasel[i].length == 0) {
                            tmp_nums = 0;
                            break;
                        }
                        tmp_nums *= datasel[i].length;
                    }
                    nums += tmp_nums * maxplace;
                    break;
                case 'sxzhixhzh':
                case 'sxzhixhzz':
                case 'sxzhixhzq':
                case 'exzhixhzh':
                case 'exzhixhzq':
                    var cc = { 0: 1, 1: 3, 2: 6, 3: 10, 4: 15, 5: 21, 6: 28, 7: 36, 8: 45, 9: 55, 10: 63, 11: 69, 12: 73, 13: 75, 14: 75, 15: 73, 16: 69, 17: 63, 18: 55, 19: 45, 20: 36, 21: 28, 22: 21, 23: 15, 24: 10, 25: 6, 26: 3, 27: 1 };
                    if (type == 'exzhixhzh' || type == 'exzhixhzq') {
                        cc = { 0: 1, 1: 2, 2: 3, 3: 4, 4: 5, 5: 6, 6: 7, 7: 8, 8: 9, 9: 10, 10: 9, 11: 8, 12: 7, 13: 6, 14: 5, 15: 4, 16: 3, 17: 2, 18: 1 };
                    }
                    for (var i = 0; i < datasel[0].length; i++) {
                        nums += cc[parseInt(datasel[0][i], 10)];
                    }
                    break;
                case 'sxzuxhzh':
                case 'sxzuxhzz':
                case 'sxzuxhzq':
                case 'exzuxhzh':
                case 'exzuxhzq':
                    var cc = { 1: 1, 2: 2, 3: 2, 4: 4, 5: 5, 6: 6, 7: 8, 8: 10, 9: 11, 10: 13, 11: 14, 12: 14, 13: 15, 14: 15, 15: 14, 16: 14, 17: 13, 18: 11, 19: 10, 20: 8, 21: 6, 22: 5, 23: 4, 24: 2, 25: 2, 26: 1};
                    if (type == 'exzuxhzq' || type == 'exzuxhzh') {
                        cc = { 1: 1, 2: 1, 3: 2, 4: 2, 5: 3, 6: 3, 7: 4, 8: 4, 9: 5, 10: 4, 11: 4, 12: 3, 13: 3, 14: 2, 15: 2, 16: 1, 17: 1};
                    }
                    for (var i = 0; i < datasel[0].length; i++) {
                        nums += cc[parseInt(datasel[0][i], 10)];
                    }
                    break;
                case 'rx2fs':
                case 'rx3fs':
                case 'rx4fs':
                    var minplace = 0;
                    if (type == 'rx2fs') {
                        minplace = 2;
                    }
                    if (type == 'rx3fs') {
                        minplace = 3;
                    }
                    if (type == 'rx4fs') {
                        minplace = 4;
                    }
                    var newsel = [];
                    for (var i = 0; i < datasel.length; i++) {
                        if (datasel[i].length != 0) {
                            newsel.push(datasel[i]);
                        }
                    }
                    // 最少位数
                    if (newsel.length >= minplace) {
                        var l = ArrayUtil.ComNum(newsel.length, minplace);
                        for (var i = 0; i < l; i++) {
                            tmp_nums = 1;
                            var data = ArrayUtil.ComVal(newsel, minplace, i);
                            for (var j = 0; j < data.length; j++) {
                                tmp_nums *= data[j].length;
                            }
                            nums += tmp_nums;
                        }
                    }
                    break;
                case 'dw': //定位胆所有在一起特殊处理
                    var maxplace = 5;
                    for (var i = 0; i < maxplace; i++) {
                        nums += datasel[i].length;
                    }
                    break;
                case 'bdw2mh':
                case 'bdw2mz':
                case 'bdw2mq':
                case 'bdwsix2mq':
                case 'bdwsix2mh':
                case 'bdwwx2m':
                case 'exzuxfsh':
                case 'exzuxfsq':
                    var maxplace = 1;
                    for (var i = 0; i < maxplace; i++) {
                        var s = datasel[i].length;
                        // 二码不定位必须选两位或者以上
                        if (s > 1) {
                            nums += s * (s - 1) / 2;
                        }
                    }
                    break;
                default:
                    var maxplace = 0;
                    switch (type) {
                        case 'wxzhixfs':
                            maxplace = 5;
                            break;
                        case 'sixzhixfsh':
                        case 'sixzhixfsq':
                            maxplace = 4;
                            break;
                        case 'sxzhixfsh':
                        case 'sxzhixfsz':
                        case 'sxzhixfsq':
                            maxplace = 3;
                            break;
                        case 'exzhixfsh':
                        case 'exzhixfsq':
                        case 'dxdsh':
                        case 'dxdsq':
                            maxplace = 2;
                            break;
                        case 'bdw1mh':
                        case 'bdw1mz':
                        case 'bdw1mq':
                        case 'bdwsix1mq':
                        case 'bdwsix1mh':
                        case 'qwyffs':
                        case 'qwhscs':
                        case 'qwsxbx':
                        case 'qwsjfc':
                        case 'longhuhewq':
                        case 'longhuhewb':
                        case 'longhuhews':
                        case 'longhuhewg':
                        case 'longhuheqb':
                        case 'longhuheqs':
                        case 'longhuheqg':
                        case 'longhuhebs':
                        case 'longhuhebg':
                        case 'longhuhesg':
                        case 'wxdxds':
                        case 'sscniuniu':
                            maxplace = 1;
                            break;
                    }
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
                // 五星
                case "wxzhixfs":
                case "wxzhixds":
                case "wxzux120":
                case "wxzux60":
                case "wxzux30":
                case "wxzux20":
                case "wxzux10":
                case "wxzux5":
                // 五星组合
                case "wxzhixzh":
                // 四星
                case "sixzhixfsh":
                case "sixzhixdsh":
                case "sixzux24h":
                case "sixzux12h":
                case "sixzux6h":
                case "sixzux4h":
                case "sixzhixfsq":
                case "sixzhixdsq":
                case "sixzux24q":
                case "sixzux12q":
                case "sixzux6q":
                case "sixzux4q":
                // 四星组合
                case "sixzhixzhh":
                case "sixzhixzhq":
                // 三星
                case "sxzhixfsh":
                case "sxzhixdsh":
                case "sxzhixhzh":
                case "sxhhzxh":
                case "sxzhixfsz":
                case "sxzhixdsz":
                case "sxzhixhzz":
                case "sxhhzxz":
                case "sxzhixfsq":
                case "sxzhixdsq":
                case "sxzhixhzq":
                case "sxhhzxq":
                // 二星
                case "exzhixfsh":
                case "exzhixdsh":
                case "exzhixhzh":
                case "dxdsh":
                case "exzuxfsh":
                case "exzuxdsh":
                case "exzhixfsq":
                case "exzhixdsq":
                case "exzhixhzq":
                case "dxdsq":
                case "exzuxfsq":
                case "exzuxdsq":
                case 'sxzuxhzh':
                case 'sxzuxhzz':
                case 'sxzuxhzq':
                case 'exzuxhzh':
                case 'exzuxhzq':
                // 一帆风顺
                case "qwyffs":
                    var minNum = parseInt(minStr);
                    var maxNum = parseInt(maxStr);
                    if (num < minNum) {
                        return [1, minNum, num, "注"];
                    }
                    else if (num > maxNum) {
                        return [2, maxNum, num, "注"];
                    }
                    return null;
                // 定位胆
                case "dw":
                    var dwMinNum = parseInt(minStr);
                    var dwMaxNum = parseInt(maxStr);
                    var maxplace = 5;
                    for (var i = 0; i < maxplace; i++) {
                        if (datasel[i].length < dwMinNum) {
                            return [1, dwMinNum, datasel[i].length, "码"];
                        }
                        else if (datasel[i].length > dwMaxNum) {
                            return [2, dwMaxNum, datasel[i].length, "码"];
                        }
                    }
                    return null;
                // 不定位
                case "bdw1mh":
                case "bdw1mz":
                case "bdw1mq":
                case "bdw2mh":
                case "bdw2mz":
                case "bdw2mq":
                case "bdwsix1mq":
                case "bdwsix2mq":
                case "bdwsix1mh":
                case "bdwsix2mh":
                case "bdwwx2m":
                case "bdwwx3m":
                    var bdwMinNum = parseInt(minStr);
                    var bdwMaxNum = parseInt(maxStr);
                    var maxplace = 1;
                    for (var i = 0; i < maxplace; i++) {
                        if (datasel[i].length < bdwMinNum) {
                            return [1, bdwMinNum, datasel[i].length, "码"];
                        }
                        else if (datasel[i].length > bdwMaxNum) {
                            return [2, bdwMaxNum, datasel[i].length, "码"];
                        }
                    }
                    return null;
                // 任选
                case "rx2fs":
                case "rx3fs":
                case "rx4fs":
                case "rx2ds":
                case "rx3ds":
                case "rx4ds":
                    var m = 0; // 组合基数
                    var rxMinNum = parseInt(minStr);
                    var rxMaxNum = parseInt(maxStr);
                    if (type == "rx2fs" || type == "rx2ds") {
                        m = 2;
                    }
                    else if (type == "rx3fs" || type == "rx3ds") {
                        m = 3;
                    }
                    else if (type == "rx4fs" || type == "rx4ds") {
                        m = 4;
                    }

                    // 选择位置
                    var place = 0;
                    if (type == "rx2fs" || type == "rx3fs" || type == "rx4fs") {
                        for (var i = 0; i < 5; i++) {
                            if (datasel[i].length > 0) place++;
                        }
                    }
                    else {
                        for (var i = 0; i < datasel[0].length; i++) {
                            if (datasel[0][i] == "√") place++;
                        }
                    }
                    if (place >= m) {
                        var h = ArrayUtil.ComNum(place, m);
                        if (h > 0) {
                            var minNums = h * rxMinNum;
                            var maxNums = h * rxMaxNum;
                            if (num < minNums) {
                                return [false, minNums, num, "注"];
                            }
                            else if (num > maxNum) {
                                return [false, maxNums, num, "注"];
                            }
                        }
                    }
                    return null;
                default :
                    return null;
            }
        }

        var _formatSelect_Num = function(datasel, m, n) {
            var newsel = new Array();
            if (!m) m = 0;
            if (!n) n = 0;
            for (var i = 0; i < m; i++) {
                newsel.push('-');
            }
            for (var i = 0; i < datasel.length; i++) {
                var f = datasel[i].toString().replace(/\,/g, '');
                if (f == '') {
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
                case 'wxzhixds':
                    datasel = _inputCheck_Num(datasel, 5);
                    break;
                case 'sixzhixdsh':
                case 'sixzhixdsq':
                    datasel = _inputCheck_Num(datasel, 4);
                    break;
                case 'sxzhixdsh':
                case 'sxzhixdsz':
                case 'sxzhixdsq':
                    datasel = _inputCheck_Num(datasel, 3);
                    break;
                case 'sxhhzxh':
                case 'sxhhzxz':
                case 'sxhhzxq':
                    datasel = _inputCheck_Num(datasel, 3, _HHZXCheck_Num, true);
                    break;
                case 'exzhixdsh':
                case 'exzhixdsq':
                    datasel = _inputCheck_Num(datasel, 2);
                    break;
                case 'exzuxdsh':
                case 'exzuxdsq':
                    datasel = _inputCheck_Num(datasel, 2, _HHZXCheck_Num, true);
                    break;
                case 'rx2ds':
                case 'rx3ds':
                case 'rx4ds':
                    if (datasel.length > 1) {
                        var place = 0;
                        for (var i = 0; i < datasel[0].length; i++) {
                            if (datasel[0][i] == '√') place++;
                        }
                        var newsel = [];
                        for (var i = 1; i < datasel.length; i++) {
                            newsel.push(datasel[i]);
                        }
                        var m = 0;
                        if (type == 'rx2ds') {
                            m = 2;
                        }
                        if (type == 'rx3ds') {
                            m = 3;
                        }
                        if (type == 'rx4ds') {
                            m = 4;
                        }
                        // 任选2必须大于选了2位以上才能组成组合
                        if (place >= m) {
                            var h = ArrayUtil.ComNum(place, m);
                            if (h > 0) { // 组合数必须大于0
                                return '[' + datasel[0] + ']' + _inputCheck_Num(newsel, m);
                            }
                        }
                    }
                    break;
                case 'rx3hhzx':
                    if (datasel.length > 1) {
                        var place = 0;
                        for (var i = 0; i < datasel[0].length; i++) {
                            if (datasel[0][i] == '√') place++;
                        }
                        var newsel = [];
                        for (var i = 1; i < datasel.length; i++) {
                            newsel.push(datasel[i]);
                        }
                        var m = 3;
                        if (place >= m) {
                            var h = ArrayUtil.ComNum(place, m);
                            if (h > 0) { // 组合数必须大于0
                                return '[' + datasel[0] + ']' + _inputCheck_Num(newsel, 3, _HHZXCheck_Num, true);
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
            return datasel.toString().replace(/\,/g, ' ');
        }

        var _inputFormat = function(type, datasel) {
            switch (type) {
                case 'wxzhixds':
                case 'sixzhixdsh':
                case 'sixzhixdsq':
                case 'sxzhixdsh':
                case 'sxzhixdsz':
                case 'sxzhixdsq':
                case 'sxhhzxh':
                case 'sxhhzxz':
                case 'sxhhzxq':
                case 'exzhixdsh':
                case 'exzhixdsq':
                case 'exzuxdsh':
                case 'exzuxdsq':
                case 'rx2ds':
                case 'rx3ds':
                case 'rx4ds':
                case 'rx3hhzx':
                    return _formatTextarea_Num(type, datasel);
                case 'rx3z3':
                case 'rx3z6':
                case 'rx2zx':
                    var space = datasel[0];
                    return '[' + space + ']' + ArrayUtil.remove(datasel, 0).toString();
                case 'wxzux120':
                case 'sixzux24h':
                case 'sixzux24q':
                case 'sixzux6h':
                case 'sixzux6q':
                case 'sxzuxzsh':
                case 'sxzuxzsz':
                case 'sxzuxzsq':
                case 'sxzuxzlh':
                case 'sxzuxzlz':
                case 'sxzuxzlq':
                case 'exzuxfsh':
                case 'exzuxfsq':
                case 'bdw1mh':
                case 'bdw1mz':
                case 'bdw1mq':
                case 'bdw2mh':
                case 'bdw2mz':
                case 'bdw2mq':
                case "bdwsix1mq":
                case "bdwsix2mq":
                case "bdwsix1mh":
                case "bdwsix2mh":
                case "bdwwx2m":
                case "bdwwx3m":
                case 'qwyffs':
                case 'qwhscs':
                case 'qwsxbx':
                case 'qwsjfc':
                case 'sxzhixhzh':
                case 'sxzhixhzz':
                case 'sxzhixhzq':
                case 'exzhixhzh':
                case 'exzhixhzq':
                case 'longhuhewq':
                case 'longhuhewb':
                case 'longhuhews':
                case 'longhuhewg':
                case 'longhuheqb':
                case 'longhuheqs':
                case 'longhuheqg':
                case 'longhuhebs':
                case 'longhuhebg':
                case 'longhuhesg':
                case 'sxzuxhzh':
                case 'sxzuxhzz':
                case 'sxzuxhzq':
                case 'exzuxhzh':
                case 'exzuxhzq':
                case 'wxdxds':
                case 'sscniuniu':
                    return datasel.toString();
                case 'sixzhixfsh':
                case 'sixzhixzhh':
                    return _formatSelect_Num(datasel, 1);
                case 'sixzhixfsq':
                case 'sixzhixzhq':
                    return _formatSelect_Num(datasel, 0, 1);
                case 'sxzhixfsh':
                    return _formatSelect_Num(datasel, 2);
                case 'sxzhixfsz':
                    return _formatSelect_Num(datasel, 1, 1);
                case 'sxzhixfsq':
                    return _formatSelect_Num(datasel, 0, 2);
                case 'exzhixfsh':
                    return _formatSelect_Num(datasel, 3);
                case 'exzhixfsq':
                    return _formatSelect_Num(datasel, 0, 3);
                default:
                    return _formatSelect_Num(datasel);
            }
        }

        var _typeFormat = function(code) {
            var a = [code[0], code[1], code[2]];
            var b = [code[2], code[3], code[4]];
            var _a = a.uniquelize();
            var _b = b.uniquelize();
            var arr = [];
            if (_a.length == 1) arr[0] = '豹子';
            if (_a.length == 2) arr[0] = '组三';
            if (_a.length == 3) arr[0] = '组六';
            if (_b.length == 1) arr[1] = '豹子';
            if (_b.length == 2) arr[1] = '组三';
            if (_b.length == 3) arr[1] = '组六';
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
        title: '五星',
        code: 'sscwuxing',
        rows: [
            [{
                title: '五星直选',
                columns: [{
                    name: '复式',
                    code: 'wxzhixfs',
                    realname: '[五星_复式]',
                    tips: '从个、十、百、千、万位各选一个号码组成一注。',
                    example: '投注方案：23456；开奖号码：23456，即中五星直选一等奖。',
                    help: '从万位、千位、百位、十位、个位中选择一个5位数号码组成一注，所选号码与开奖号码全部相同，且顺序一致，即为中奖。',
                    select: {
                        layout: [{
                            title: '万位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '千位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '百位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '十位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '个位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '单式',
                    code: 'wxzhixds',
                    realname: '[五星_单式]',
                    tips: '手动输入号码，至少输入1个五位数号码组成一注。',
                    example: '投注方案：23456； 开奖号码：23456，即中五星直选一等奖',
                    help: '手动输入一个5位数号码组成一注，所选号码的万位、千位、百位、十位、个位与开奖号码相同，且顺序一致，即为中奖。',
                    textarea: {},
                    compressed: true
                }, {
                    name: '组合',
                    code: 'wxzhixzh',
                    realname: '[五星_组合]',
                    tips: '从个、十、百、千、万位各选一个号码组成五注。',
                    example: '五星组合示例，如购买：4+5+6+7+8，该票共10元，由以下5注：45678(五星)、5678(四星)、678(三星)、78(二星)、8(一星)构成。开奖号码：45678，即可中五星、四星、三星、二星、一星的一等奖各1注。',
                    help: '从万位、千位、百位、十位、个位中至少各选一个号码组成1-5星的组合，共五注，所选号码的个位与开奖号码相同，则中1个5等奖；所选号码的个位、十位与开奖号码相同，则中1个5等奖以及1个4等奖，依此类推，最高可中5个奖。',
                    select: {
                        layout: [{
                            title: '万位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '千位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '百位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '十位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '个位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }]
            }],
            [{
                title: '五星组选',
                columns: [{
                    name: '组选120',
                    code: 'wxzux120',
                    realname: '[五星_组选120]',
                    tips: '从0-9中选择5个号码组成一注。',
                    example: '投注方案：02568，开奖号码的五个数字只要包含0、2、5、6、8，即可中五星组选120一等奖。',
                    help: '从0-9中任意选择5个号码组成一注，所选号码与开奖号码的万位、千位、百位、十位、个位相同，顺序不限，即为中奖。',
                    select: {
                        layout: [{
                            title: '选号',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '组选60',
                    code: 'wxzux60',
                    realname: '[五星_组选60]',
                    tips: '从“二重号”选择一个号码，“单号”中选择三个号码组成一注。',
                    example: '投注方案：二重号：8，单号：0、2、5，只要开奖的5个数字包括 0、2、5、8、8，即可中五星组选60一等奖。',
                    help: '选择1个二重号码和3个单号号码组成一注，所选的单号号码与开奖号码相同，且所选二重号码在开奖号码中出现了2次，即为中奖。',
                    select: {
                        layout: [{
                            title: '二重号',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '单　号',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '组选30',
                    code: 'wxzux30',
                    realname: '[五星_组选30]',
                    tips: '从“二重号”选择两个号码，“单号”中选择一个号码组成一注。',
                    example: '投注方案：二重号：2、8，单号：0，只要开奖的5个数字包括 0、2、2、8、8，即可中五星组选30一等奖。',
                    help: '选择2个二重号和1个单号号码组成一注，所选的单号号码与开奖号码相同，且所选的2个二重号码分别在开奖号码中出现了2次，即为中奖。',
                    select: {
                        layout: [{
                            title: '二重号',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '单　号',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '组选20',
                    code: 'wxzux20',
                    realname: '[五星_组选20]',
                    tips: '从“三重号”选择一个号码，“单号”中选择两个号码组成一注。',
                    example: '投注方案：三重号：8，单号：0、2，只要开奖的5个数字包括 0、2、8、8、8，即可中五星组选20一等奖。',
                    help: '选择1个三重号码和2个单号号码组成一注，所选的单号号码与开奖号码相同，且所选三重号码在开奖号码中出现了3次，即为中奖。',
                    select: {
                        layout: [{
                            title: '三重号',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '单　号',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '组选10',
                    code: 'wxzux10',
                    realname: '[五星_组选10]',
                    tips: '从“三重号”选择一个号码，“二重号”中选择一个号码组成一注。',
                    example: '投注方案：三重号：8，二重号：2，只要开奖的5个数字包括 2、2、8、8、8，即可中五星组选10一等奖。',
                    help: '选择1个三重号码和1个二重号码，所选三重号码在开奖号码中出现3次，并且所选二重号码在开奖号码中出现了2次，即为中奖。',
                    select: {
                        layout: [{
                            title: '三重号',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '二重号',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '组选5',
                    code: 'wxzux5',
                    realname: '[五星_组选5]',
                    tips: '从“四重号”选择一个号码，“单号”中选择一个号码组成一注。',
                    example: '投注方案：四重号：8，单号：2，只要开奖的5个数字包括 2、8、8、8、8，即可中五星组选5一等奖。',
                    help: '选择1个四重号码和1个单号号码组成一注，所选的单号号码与开奖号码相同，且所选四重号码在开奖号码中出现了4次，即为中奖。',
                    select: {
                        layout: [{
                            title: '四重号',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '单　号',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }]
            }],
            [{
                title: '五星特殊',
                columns: [{
                    name: '一帆风顺',
                    code: 'qwyffs',
                    realname: '[特殊_一帆风顺]',
                    tips: '从0-9中任意选择1个以上号码。',
                    example: '投注方案：8；开奖号码：至少出现1个8，即中一帆风顺。',
                    help: '从0-9中任意选择1个号码组成一注，只要开奖号码的万位、千位、百位、十位、个位中包含所选号码，即为中奖。',
                    select: {
                        layout: [{
                            title: '选号',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '好事成双',
                    code: 'qwhscs',
                    realname: '[特殊_好事成双]',
                    tips: '从0-9中任意选择1个以上的二重号码。',
                    example: '投注方案：8；开奖号码：至少出现2个8，即中好事成双。',
                    help: '从0-9中任意选择1个号码组成一注，只要所选号码在开奖号码的万位、千位、百位、十位、个位中出现2次，即为中奖。',
                    select: {
                        layout: [{
                            title: '选号',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '三星报喜',
                    code: 'qwsxbx',
                    realname: '[特殊_三星报喜]',
                    tips: '从0-9中任意选择1个以上的三重号码。',
                    example: '投注方案：8；开奖号码：至少出现3个8，即中三星报喜。',
                    help: '从0-9中任意选择1个号码组成一注，只要所选号码在开奖号码的万位、千位、百位、十位、个位中出现3次，即为中奖。',
                    select: {
                        layout: [{
                            title: '选号',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '四季发财',
                    code: 'qwsjfc',
                    realname: '[特殊_四季发财]',
                    tips: '从0-9中任意选择1个以上的四重号码。',
                    example: '投注方案：8；开奖号码：至少出现4个8，即中四季发财。',
                    help: '从0-9中任意选择1个号码组成一注，只要所选号码在开奖号码的万位、千位、百位、十位、个位中出现4次，即为中奖。',
                    select: {
                        layout: [{
                            title: '选号',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }]
            }],
            [{
                title: '五星其它',
                columns: [{
                    name: '总和大小单双',
                    code: 'wxdxds',
                    realname: '[五星_总和大小单双]',
                    tips: '从总和大、小、单、双中任意选择1个号码形态组成一注。',
                    example: '投注方案：总和大。开奖号码：16568，开奖号码的总和26，即中总和大。',
                    help: '从总和大、小、单、双中任意选择1个号码形态组成一注，只要所选形态与开奖号码的5位数号码总和(大于等于23:总和大、小于等于22：总和小、单数：总和单、双数：总和双)形态相同，即为中奖。',
                    select: {
                        layout: [{
                            title: '总和大小单双',
                            balls: ['总和大','总和小', '总和单', '总和双']
                        }]
                    }
                }]
            }]
        ]
    }, {
        title: '后四',
        code: 'sschousi',
        rows: [
            [{
                title: '后四直选',
                columns: [{
                    name: '复式',
                    code: 'sixzhixfsh',
                    realname: '[后四星_复式]',
                    tips: '从千位、百位、十位、个位中选择一个4位数号码组成一注',
                    example: '投注方案：3456；开奖号码：*3456，即中四星直选。',
                    help: '从千位、百位、十位、个位中选择一个4位数号码组成一注，所选号码与开奖号码相同，且顺序一致，即为中奖。',
                    select: {
                        layout: [{
                            title: '千位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '百位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '十位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '个位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '单式',
                    code: 'sixzhixdsh',
                    realname: '[后四星_单式]',
                    tips: '手动输入号码，至少输入1个四位数号码组成一注。',
                    example: '投注方案：3456； 开奖号码：3456，即中四星直选一等奖。',
                    help: '手动输入一个4位数号码组成一注，所选号码的千位、百位、十位、个位与开奖号码相同，且顺序一致，即为中奖。',
                    textarea: {},
                    compressed: true
                }, {
                    name: '组合',
                    code: 'sixzhixzhh',
                    realname: '[后四星_组合]',
                    tips: '在千位，百位，十位，个位任意位置上任意选择1个或1个以上号码。',
                    example: '5+6+7+8，开奖号码：*5678，即可中四星、三星、二星、一星各1注。',
                    help: '从千位、百位、十位、个位中至少各选一个号码组成1-4星的组合，共四注，所选号码的个位与开奖号码相同，则中1个4等奖；所选号码的个位、十位与开奖号码相同，则中1个4等奖以及1个3等奖，依此类推，最高可中4个奖。',
                    select: {
                        layout: [{
                            title: '千位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '百位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '十位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '个位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }]
            }],
            [{
                title: '后四组选',
                columns: [{
                    name: '组选24',
                    code: 'sixzux24h',
                    realname: '[后四星_组选24]',
                    tips: '从0-9中选择4个号码组成一注。',
                    example: '投注方案：0568，开奖号码的四个数字只要包含0、5、6、8，即可中四星组选24一等奖。',
                    help: '从0-9中任意选择4个号码组成一注，所选号码与开奖号码的千位、百位、十位、个位相同，且顺序不限，即为中奖。',
                    select: {
                        layout: [{
                            title: '选号',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '组选12',
                    code: 'sixzux12h',
                    realname: '[后四星_组选12]',
                    tips: '从“二重号”选择一个号码，“单号”中选择两个号码组成一注。',
                    example: '投注方案：二重号：8，单号：0、6，只要开奖的四个数字包括 0、6、8、8，即可中四星组选12一等奖。',
                    help: '选择1个二重号码和2个单号号码组成一注，所选单号号码与开奖号码相同，且所选二重号码在开奖号码中出现了2次，即为中奖。选择1个二重号码和2个单号号码组成一注，所选单号号码与开奖号码相同，且所选二重号码在开奖号码中出现了2次，即为中奖。',
                    select: {
                        layout: [{
                            title: '二重号',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '单　号',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '组选6',
                    code: 'sixzux6h',
                    realname: '[后四星_组选6]',
                    tips: '从“二重号”选择两个号码组成一注。',
                    example: '投注方案：二重号：6、8，只要开奖的四个数字从小到大排列为 6、6、8、8，即可中四星组选6。',
                    help: '选择2个二重号码组成一注，所选的2个二重号码在开奖号码中分别出现了2次，即为中奖。',
                    select: {
                        layout: [{
                            title: '二重号',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '组选4',
                    code: 'sixzux4h',
                    realname: '[后四星_组选4]',
                    tips: '从“三重号”选择一个号码，“单号”中选择两个号码组成一注。',
                    example: '投注方案：三重号：8，单号：2，只要开奖的四个数字从小到大排列为 2、8、8、8，即可中四星组选4。',
                    help: '选择1个三重号码和1个单号号码组成一注，所选单号号码与开奖号码相同，且所选三重号码在开奖号码中出现了3次，即为中奖。',
                    select: {
                        layout: [{
                            title: '三重号',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '单　号',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }]
            }]
        ]
    }, {
        title: '前四',
        code: 'sscqiansi',
        rows: [
            [{
                title: '前四直选',
                columns: [{
                    name: '复式',
                    code: 'sixzhixfsq',
                    realname: '[前四星_复式]',
                    tips: '从万位、千位、百位、十位中选择一个4位数号码组成一注',
                    example: '投注方案：3456；开奖号码：3456*，即中四星直选。',
                    help: '从万位、千位、百位、十位中选择一个4位数号码组成一注，所选号码与开奖号码相同，且顺序一致，即为中奖。',
                    select: {
                        layout: [{
                            title: '万位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '千位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '百位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '十位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '单式',
                    code: 'sixzhixdsq',
                    realname: '[前四星_单式]',
                    tips: '手动输入号码，至少输入1个四位数号码组成一注。',
                    example: '投注方案：3456； 开奖号码：3456，即中四星直选一等奖',
                    help: '手动输入一个4位数号码组成一注，所选号码的千位、百位、十位、个位与开奖号码相同，且顺序一致，即为中奖。',
                    textarea: {},
                    compressed: true
                }, {
                    name: '组合',
                    code: 'sixzhixzhq',
                    realname: '[前四星_组合]',
                    tips: '在万位、千位、百位、十位任意位置上任意选择1个或1个以上号码。',
                    example: '投注方案：5+6+7+8，开奖号码： 5678*，即可中四星、三星、二星、一星各1注。',
                    help: '从万位、千位、百位、十位中至少各选一个号码组成1-4星的组合，共四注，所选号码的个位与开奖号码相同，则中1个4等奖；所选号码的个位、十位与开奖号码相同，则中1个4等奖以及1个3等奖，依此类推，最高可中4个奖。',
                    select: {
                        layout: [{
                            title: '万位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '千位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '百位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '十位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }]
            }],
            [{
                title: '前四组选',
                columns: [{
                    name: '组选24',
                    code: 'sixzux24q',
                    realname: '[前四星_组选24]',
                    tips: '从0-9中选择4个号码组成一注。',
                    example: '投注方案：0568，开奖号码的四个数字只要包含0、5、6、8，即可中四星组选24一等奖。',
                    help: '从0-9中任意选择4个号码组成一注，所选号码与开奖号码的万位、千位、百位、十位相同，且顺序不限，即为中奖。',
                    select: {
                        layout: [{
                            title: '选号',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '组选12',
                    code: 'sixzux12q',
                    realname: '[前四星_组选12]',
                    tips: '从“二重号”选择一个号码，“单号”中选择两个号码组成一注。',
                    example: '投注方案：二重号：8，单号：0、6，只要开奖的四个数字包括 0、6、8、8，即可中四星组选12一等奖。',
                    help: '选择1个二重号码和2个单号号码组成一注，所选单号号码与开奖号码相同，且所选二重号码在开奖号码中出现了2次，即为中奖。',
                    select: {
                        layout: [{
                            title: '二重号',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '单　号',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '组选6',
                    code: 'sixzux6q',
                    realname: '[前四星_组选6]',
                    tips: '从“二重号”选择两个号码组成一注。',
                    example: '投注方案：二重号：6、8，只要开奖的四个数字从小到大排列为 6、6、8、8，即可中四星组选6。',
                    help: '选择2个二重号码组成一注，所选的2个二重号码在开奖号码中分别出现了2次，即为中奖。',
                    select: {
                        layout: [{
                            title: '二重号',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '组选4',
                    code: 'sixzux4q',
                    realname: '[前四星_组选4]',
                    tips: '从“三重号”选择一个号码，“单号”中选择两个号码组成一注。',
                    example: '投注方案：三重号：8，单号：2，只要开奖的四个数字从小到大排列为 2、8、8、8，即可中四星组选4。',
                    help: '选择1个三重号码和1个单号号码组成一注，所选单号号码与开奖号码相同，且所选三重号码在开奖号码中出现了3次，即为中奖。',
                    select: {
                        layout: [{
                            title: '三重号',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '单　号',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }]
            }]
        ]
    }, {
        title: '后三',
        code: 'sschousan',
        rows: [
            [{
                title: '后三直选',
                columns: [{
                    name: '复式',
                    code: 'sxzhixfsh',
                    realname: '[后三码_复式]',
                    tips: '从个、十、百位各选一个号码组成一注。',
                    example: '投注方案：345；开奖号码：345；即中后三直选一等奖',
                    help: '从百位、十位、个位中选择一个3位数号码组成一注，所选号码与开奖号码后3位相同，且顺序一致，即为中奖。',
                    select: {
                        layout: [{
                            title: '百位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '十位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '个位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '单式',
                    code: 'sxzhixdsh',
                    realname: '[后三码_单式]',
                    tips: '手动输入号码，至少输入1个三位数号码组成一注。',
                    example: '投注方案：345； 开奖号码：345，即中后三直选一等奖。',
                    help: '手动输入一个3位数号码组成一注，所选号码的百位、十位、个位与开奖号码相同，且顺序一致，即为中奖。',
                    textarea: {}
                }, {
                    name: '直选和值',
                    code: 'sxzhixhzh',
                    realname: '[后三码_和值]',
                    tips: '从0-27中任意选择1个或1个以上号码',
                    example: '投注方案：和值1；开奖号码后三位：001,010,100,即中后三直选一等奖。',
                    help: '所选数值等于开奖号码的百位、十位、个位三个数字相加之和，即为中奖。',
                    select: {
                        layout: [{
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27],
                            tools: false,
                            cls: 'hz'
                        }]
                    }
                }]
            }],
            [{
                title: '后三组选',
                columns: [{
                    name: '组三',
                    code: 'sxzuxzsh',
                    realname: '[后三码_组三]',
                    tips: '从0-9中任意选择2个或2个以上号码。',
                    example: '投注方案：5,8,8；开奖号码后三位：1个5，2个8 (顺序不限)，即中后三组选三一等奖。',
                    help: '从0-9中选择2个数字组成两注，所选号码与开奖号码的百位、十位、个位相同，且顺序不限，即为中奖。',
                    select: {
                        layout: [{
                            title: '组三',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '组六',
                    code: 'sxzuxzlh',
                    realname: '[后三码_组六]',
                    tips: '从0-9中任意选择3个或3个以上号码。',
                    example: '投注方案：2,5,8；开奖号码后三位：1个2、1个5、1个8 (顺序不限)，即中后三组选六一等奖。',
                    help: '从0-9中任意选择3个号码组成一注，所选号码与开奖号码的百位、十位、个位相同，顺序不限，即为中奖。',
                    select: {
                        layout: [{
                            title: '组六',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '混合组选',
                    code: 'sxhhzxh',
                    realname: '[后三码_混合组选]',
                    tips: '手动输入号码，至少输入1个三位数号码。',
                    example: '投注方案：分別投注(0,0,1),以及(1,2,3)，开奖号码后三位包括：(1)0,0,1，顺序不限，即中得组三一等奖；或者(2)1,2,3，顺序不限，即中得组六一等奖。',
                    help: '手动输入购买号码，3个数字为一注，开奖号码的百位、十位、个位符合后三组三或组六均为中奖。',
                    textarea: {}
                },{
                    name: '组选和值',
                    code: 'sxzuxhzh',
                    realname: '[后三码_组选和值]',
                    tips: '从1-26中选择1个号码。',
                    example: '所选数值等于开奖号码百位、十位、个位三个数字相加之和（豹子除外），即为中奖。',
                    help: '投注方案：和值3；开奖号码后三位：(1)开出003号码，顺序不限，即中后三组三；(2)开出012号码，顺序不限，即中后三组六。',
                    select: {
                    	layout: [{
                            balls: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26],
                            tools: false,
                            cls: 'hz'
                        }]
                    }
                }]
            }]
        ]
    }, {
        title: '中三',
        code: 'ssczhongsan',
        rows: [
            [{
                title: '中三直选',
                columns: [{
                    name: '复式',
                    code: 'sxzhixfsz',
                    realname: '[中三码_复式]',
                    tips: '从千、百、十位各选一个号码组成一注。',
                    example: '投注方案：456； 开奖号码：3456，即中中三直选一等奖。',
                    help: '从千位、百位、十位中选择一个3位数号码组成一注，所选号码与开奖号码的中间3位相同，且顺序一致，即为中奖。',
                    select: {
                        layout: [{
                            title: '千位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '百位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '十位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '单式',
                    code: 'sxzhixdsz',
                    realname: '[中三码_单式]',
                    tips: '手动输入号码，至少输入1个三位数号码组成一注。',
                    example: '投注方案：345； 开奖号码：2345，即中中三直选一等奖。',
                    help: '手动输入一个3位数号码组成一注，所选号码的千位、百位、十位与开奖号码相同，且顺序一致，即为中奖。',
                    textarea: {}
                }, {
                    name: '直选和值',
                    code: 'sxzhixhzz',
                    realname: '[中三码_和值]',
                    tips: '从0-27中任意选择1个或1个以上号码',
                    example: '投注方案：和值1；开奖号码中间三位：01001,00010,00100,即中中三直选一等奖',
                    help: '所选数值等于开奖号码的千位、百位、十位三个数字相加之和，即为中奖。',
                    select: {
                        layout: [{
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27],
                            tools: false,
                            cls: 'hz'
                        }]
                    }
                }]
            }],
            [{
                title: '中三组选',
                columns: [{
                    name: '组三',
                    code: 'sxzuxzsz',
                    realname: '[中三码_组三]',
                    tips: '从0-9中任意选择2个或2个以上号码。',
                    example: '投注方案：5,8,8；开奖号码中间三位：1个5，2个8 (顺序不限)，即中中三组选三一等奖。',
                    help: '从0-9中选择2个数字组成两注，所选号码与开奖号码的千位、百位、十位相同，且顺序不限，即为中奖。',
                    select: {
                        layout: [{
                            title: '组三',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '组六',
                    code: 'sxzuxzlz',
                    realname: '[中三码_组六]',
                    tips: '从0-9中任意选择3个或3个以上号码。',
                    example: '投注方案：2,5,8；开奖号码中间三位：1个2、1个5、1个8 (顺序不限)，即中中三组选六一等奖。',
                    help: '从0-9中任意选择3个号码组成一注，所选号码与开奖号码的千位、百位、十位相同，顺序不限，即为中奖。',
                    select: {
                        layout: [{
                            title: '组六',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '混合组选',
                    code: 'sxhhzxz',
                    realname: '[中三码_混合组选]',
                    tips: '手动输入号码，至少输入1个三位数号码。',
                    example: '投注方案：分別投注(0,0,1),以及(1,2,3)，开奖号码中间三位包括：(1)0,0,1，顺序不限，即中得组三一等奖；或者(2)1,2,3，顺序不限，即中得组六一等奖。',
                    help: '手动输入购买号码，3个数字为一注，开奖号码的千位、百位、十位符合中三组三或组六均为中奖。',
                    textarea: {}
                },{
                    name: '组选和值',
                    code: 'sxzuxhzz',
                    realname: '[中三码_组选和值]',
                    tips: '从1-26中选择1个号码。',
                    example: '所选数值等于开奖号码千位、百位、十位三个数字相加之和（豹子除外），即为中奖。',
                    help: '投注方案：和值3；开奖号码中三位：(1)开出003号码，顺序不限，即中中三组三；(2)开出012号码，顺序不限，即中中三组六。',
                    select: {
                    	layout: [{
                            balls: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26],
                            tools: false,
                            cls: 'hz'
                        }]
                    }
                }]
            }]
        ]
    }, {
        title: '前三',
        code: 'sscqiansan',
        rows: [
            [{
                title: '前三直选',
                columns: [{
                    name: '复式',
                    code: 'sxzhixfsq',
                    realname: '[前三码_复式]',
                    tips: '从万、千、百位各选一个号码组成一注。',
                    example: '投注方案：345； 开奖号码：345，即中前三直选一等奖',
                    help: '从万位、千位、百位中选择一个3位数号码组成一注，所选号码与开奖号码的前3位相同，且顺序一致，即为中奖。',
                    select: {
                        layout: [{
                            title: '万位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '千位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '百位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '单式',
                    code: 'sxzhixdsq',
                    realname: '[前三码_单式]',
                    tips: '手动输入号码，至少输入1个三位数号码组成一注。',
                    example: '投注方案：345； 开奖号码：345，即中前三直选一等奖',
                    help: '手动输入一个3位数号码组成一注，所选号码的万位、千位、百位与开奖号码相同，且顺序一致，即为中奖。',
                    textarea: {}
                }, {
                    name: '直选和值',
                    code: 'sxzhixhzq',
                    realname: '[前三码_和值]',
                    tips: '从0-27中任意选择1个或1个以上号码',
                    example: '投注方案：和值1；开奖号码前三位：001,010,100,即中前三直选一等奖',
                    help: '所选数值等于开奖号码的万位、千位、百位三个数字相加之和，即为中奖。',
                    select: {
                        layout: [{
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27],
                            tools: false,
                            cls: 'hz'
                        }]
                    }
                }]
            }],
            [{
                title: '前三组选',
                columns: [{
                    name: '组三',
                    code: 'sxzuxzsq',
                    realname: '[前三码_组三]',
                    tips: '从0-9中任意选择2个或2个以上号码。',
                    example: '投注方案：5,8,8；开奖号码前三位：1个5，2个8 (顺序不限)，即中前三组选三一等奖。',
                    help: '从0-9中选择2个数字组成两注，所选号码与开奖号码的万位、千位、百位相同，且顺序不限，即为中奖。',
                    select: {
                        layout: [{
                            title: '组三',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '组六',
                    code: 'sxzuxzlq',
                    realname: '[前三码_组六]',
                    tips: '从0-9中任意选择3个或3个以上号码。',
                    example: '投注方案：2,5,8；开奖号码前三位：1个2、1个5、1个8 (顺序不限)，即中前三组选六一等奖。',
                    help: '从0-9中任意选择3个号码组成一注，所选号码与开奖号码的万位、千位、百位相同，顺序不限，即为中奖。',
                    select: {
                        layout: [{
                            title: '组六',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '混合组选',
                    code: 'sxhhzxq',
                    realname: '[前三码_混合组选]',
                    tips: '手动输入号码，至少输入1个三位数号码。',
                    example: '投注方案：分別投注(0,0,1),以及(1,2,3)，开奖号码前三位包括：(1)0,0,1，顺序不限，即中得组三一等奖；或者(2)1,2,3，顺序不限，即中得组六一等奖。',
                    help: '键盘手动输入购买号码，3个数字为一注，开奖号码的万位、千位、百位符合后三组三或组六均为中奖。',
                    textarea: {}
                },{
                    name: '组选和值',
                    code: 'sxzuxhzq',
                    realname: '[前三码_组选和值]',
                    tips: '从1-26中选择1个号码。',
                    example: '所选数值等于开奖号码万位、千位、百位三个数字相加之和（豹子除外），即为中奖。',
                    help: '投注方案：和值3；开奖号码前三位：(1)开出003号码，顺序不限，即中前三组三；(2)开出012号码，顺序不限，即中前三组六。',
                    select: {
                    	layout: [{
                            balls: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26],
                            tools: false,
                            cls: 'hz'
                        }]
                    }
                }]
            }]
        ]
    }, {
        title: '前二',
        code: 'sscqianer',
        rows: [
            [{
                title: '前二星直选',
                columns: [{
                    name: '复式',
                    code: 'exzhixfsq',
                    realname: '[直选复式]',
                    tips: '从万位、千位各选一个号码组成一注。',
                    example: '投注方案：5,8；开奖号码前二位：5,8；即中前二直选一等奖。',
                    help: '从万位、千位中选择一个2位数号码组成一注，所选号码与开奖号码的前2位相同，且顺序一致，即为中奖。',
                    select: {
                        layout: [{
                            title: '万位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '千位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '单式',
                    code: 'exzhixdsq',
                    realname: '[直选单式]',
                    tips: '手动输入号码，至少输入1个两位数号码。',
                    example: '投注方案：58；开奖号码前二位：58，即中前二直选一等奖。',
                    help: '手动输入一个2位数号码组成一注，所选号码的万位、千位与开奖号码相同，且顺序一致，即为中奖。',
                    textarea: {}
                }, {
                    name: '直选和值',
                    code: 'exzhixhzq',
                    realname: '[直选和值]',
                    tips: '从0-18中任意选择1个或1个以上的和值号码。',
                    example: '投注方案：和值1；开奖号码前二位：01,10，即中前二直选和值。',
                    help: '所选数值等于开奖号码的万位、千位二个数字相加之和，即为中奖。',
                    select: {
                        layout: [{
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18],
                            tools: false,
                            cls: 'hz'
                        }]
                    }
                }, {
                    name: '大小单双',
                    code: 'dxdsq',
                    realname: '[大小单双]',
                    tips: '从万位、千位中的“大、小、单、双”中至少各选一个组成一注。',
                    example: '投注方案：小双；开奖号码万位与千位：小双，即中前二大小单双一等奖。',
                    help: '对万位和千位的“大（56789）小（01234）、单（13579）双（02468）”形态进行购买，所选号码的位置、形态与开奖号码的位置、形态相同，即为中奖。',
                    select: {
                        layout: [{
                            title: '万位',
                            balls: ['大', '小', '单', '双'],
                            tools: false
                        }, {
                            title: '千位',
                            balls: ['大', '小', '单', '双'],
                            tools: false
                        }]
                    }
                }]
            }],
            [{
                title: '前二星组选',
                columns: [{
                    name: '复式',
                    code: 'exzuxfsq',
                    realname: '[组选复式]',
                    tips: '从0-9中任意选择2个或2个以上号码。',
                    example: '投注方案：5,8；开奖号码前二位：1个5，1个8 (顺序不限)，即中前二组选一等奖。',
                    help: '从0-9中选2个号码组成一注，所选号码与开奖号码的万位、千位相同，顺序不限，即中奖。',
                    select: {
                        layout: [{
                            title: '组选',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '单式',
                    code: 'exzuxdsq',
                    realname: '[组选单式]',
                    tips: '手动输入号码，至少输入1个两位数号码。',
                    example: '投注方案：5,8；开奖号码后前位：1个5，1个8 (顺序不限)，即中前二组选一等奖。',
                    help: '手动输入一个2位数号码组成一注，所选号码的万位、千位与开奖号码相同，顺序不限，即为中奖。',
                    textarea: {}
                },{
                    name: '组选和值',
                    code: 'exzuxhzq',
                    realname: '[组选和值]',
                    tips: '从1-17中任意选择1个或1个以上的和值号码。',
                    example: '投注方案：和值3；开奖号码前二位：12,30，即中前二直选和值。',
                    help: '所选数值等于开奖号码的万位、千位二个数字相加之和（对子除外），即为中奖。',
                    select: {
                        layout: [{
                            balls: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17],
                            tools: false,
                            cls: 'hz'
                        }]
                    }
                }]
            }]
        ]
    }, {
        title: '后二',
        code: 'sschouer',
        rows: [
            [{
                title: '后二星直选',
                columns: [{
                    name: '复式',
                    code: 'exzhixfsh',
                    realname: '[直选复式]',
                    tips: '从十、个位各选一个号码组成一注。',
                    example: '投注方案：58；开奖号码后二位：58，即中后二直选一等奖。',
                    help: '从十位、个位中选择一个2位数号码组成一注，所选号码与开奖号码的十位、个位相同，且顺序一致，即为中奖。',
                    select: {
                        layout: [{
                            title: '十位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '个位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '单式',
                    code: 'exzhixdsh',
                    realname: '[直选单式]',
                    tips: '手动输入号码，至少输入1个两位数号码。',
                    example: '投注方案：58；开奖号码后二位：58，即中后二直选一等奖。',
                    help: '手动输入一个2位数号码组成一注，所选号码的十位、个位与开奖号码相同，且顺序一致，即为中奖。',
                    textarea: {}
                }, {
                    name: '直选和值',
                    code: 'exzhixhzh',
                    realname: '[直选和值]',
                    tips: '从0-18中任意选择1个或1个以上的和值号码。',
                    example: '投注方案：和值1；开奖号码后二位：01,10，即中后二直选。',
                    help: '所选数值等于开奖号码的十位、个位二个数字相加之和，即为中奖。',
                    select: {
                        layout: [{
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18],
                            tools: false,
                            cls: 'hz'
                        }]
                    }
                }, {
                    name: '大小单双',
                    code: 'dxdsh',
                    realname: '[大小单双]',
                    tips: '从十位、个位中的“大、小、单、双”中至少各选一个组成一注。',
                    example: '投注方案：大单；开奖号码十位与个位：大单，即中后二大小单双一等奖。',
                    help: '对十位和个位的“大（56789）小（01234）、单（13579）双（02468）”形态进行购买，所选号码的位置、形态与开奖号码的位置、形态相同，即为中奖。',
                    select: {
                        layout: [{
                            title: '十位',
                            balls: ['大', '小', '单', '双'],
                            tools: false
                        }, {
                            title: '个位',
                            balls: ['大', '小', '单', '双'],
                            tools: false
                        }]
                    }
                }]
            }],
            [{
                title: '后二星组选',
                columns: [{
                    name: '复式',
                    code: 'exzuxfsh',
                    realname: '[组选复式]',
                    tips: '从0-9中任意选择2个或2个以上号码。',
                    example: '投注方案：5,8；开奖号码后二位：1个5，1个8 (顺序不限)，即中后二组选一等奖。',
                    help: '从0-9中选2个号码组成一注，所选号码与开奖号码的十位、个位相同，顺序不限，即中奖。',
                    select: {
                        layout: [{
                            title: '组选',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '单式',
                    code: 'exzuxdsh',
                    realname: '[组选单式]',
                    tips: '手动输入号码，至少输入1个两位数号码。',
                    example: '投注方案：5,8；开奖号码后二位：1个5，1个8 (顺序不限)，即中后二组选一等奖。',
                    help: '手动输入一个2位数号码组成一注，所选号码的十位、个位与开奖号码相同，顺序不限，即为中奖。',
                    textarea: {}
                },{
                    name: '组选和值',
                    code: 'exzuxhzh',
                    realname: '[组选和值]',
                    tips: '从1-17中任意选择1个或1个以上的和值号码。',
                    example: '投注方案：和值2；开奖号码前二位：02,20，即中后二直选和值。',
                    help: '所选数值等于开奖号码的十位、个位二个数字相加之和（对子除外），即为中奖。',
                    select: {
                        layout: [{
                            balls: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17],
                            tools: false,
                            cls: 'hz'
                        }]
                    }
                }]
            }]
        ]
    }, {
        title: '定位胆',
        code: 'sscdwd',
        rows: [
            [{
                title: '定位胆',
                columns: [{
                    name: '定位胆',
                    code: 'dw',
                    realname: '定位胆',
                    tips: '在万千百十个位任意位置上任意选择1个或1个以上号码。',
                    example: '投注方案：1；开奖号码万位：1，即中定位胆万位一等奖。',
                    help: '从万位、千位、百位、十位、个位任意位置上至少选择1个以上号码，所选号码与相同位置上的开奖号码一致，即为中奖。',
                    select: {
                        layout: [{
                            title: '万位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '千位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '百位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '十位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '个位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }]
            }]
        ]
    }, {
        title: '不定位',
        code: 'sscbdw',
        rows: [
            [{
                title: '三星',
                columns: [{
                    name: '前三一码',
                    code: 'bdw1mq',
                    realname: '[不定位_前三一码]',
                    tips: '从0-9中任意选择1个以上号码。',
                    example: '投注方案：1；开奖号码前三位：至少出现1个1，即中前三一码不定位一等奖。',
                    help: '从0-9中选择1个号码，每注由1个号码组成，只要开奖号码的万位、千位、百位中包含所选号码，即为中奖。',
                    select: {
                        layout: [{
                            title: '不定位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '前三二码',
                    code: 'bdw2mq',
                    realname: '[不定位_前三二码]',
                    tips: '从0-9中任意选择2个以上号码。',
                    example: '投注方案：1,2；开奖号码前三位：至少出现1和2各1个，即中前三二码不定位一等奖。',
                    help: '从0-9中选择2个号码，每注由2个不同的号码组成，开奖号码的万位、千位、百位中同时包含所选的2个号码，即为中奖。',
                    select: {
                        layout: [{
                            title: '不定位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '中三一码',
                    code: 'bdw1mz',
                    realname: '[不定位_中三一码]',
                    tips: '从0-9中任意选择1个以上号码。',
                    example: '从0-9中选择1个号码，每注由1个号码组成，只要开奖号码的千位、百位、十位中包含所选号码，即为中奖。',
                    help: '帮助',
                    select: {
                        layout: [{
                            title: '不定位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '中三二码',
                    code: 'bdw2mz',
                    realname: '[不定位_中三二码]',
                    tips: '从0-9中任意选择2个以上号码。',
                    example: '投注方案：1,2；开奖号码中间三位：至少出现1和2各1个，即中中三二码不定位一等奖。',
                    help: '从0-9中选择2个号码，每注由2个不同的号码组成，开奖号码的千位、百位、十位中同时包含所选的2个号码，即为中奖。',
                    select: {
                        layout: [{
                            title: '不定位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '后三一码',
                    code: 'bdw1mh',
                    realname: '[不定位_后三一码]',
                    tips: '从0-9中任意选择1个以上号码。',
                    example: '投注方案：1；开奖号码后三位：至少出现1个1，即中后三一码不定位一等奖。',
                    help: '从0-9中选择1个号码，每注由1个号码组成，只要开奖号码的百位、十位、个位中包含所选号码，即为中奖。',
                    select: {
                        layout: [{
                            title: '不定位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                },{
                    name: '后三二码',
                    code: 'bdw2mh',
                    realname: '[不定位_后三二码]',
                    tips: '从0-9中任意选择2个以上号码。',
                    example: '投注方案：1,2；开奖号码后三位：至少出现1和2各1个，即中后三二码不定位一等奖。',
                    help: '从0-9中选择2个号码，每注由2个不同的号码组成，开奖号码的百位、十位、个位中同时包含所选的2个号码，即为中奖。',
                    select: {
                        layout: [{
                            title: '不定位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }]
            }],
            [{
                title: '四星',
                columns: [{
                    name: '前四一码',
                    code: 'bdwsix1mq',
                    realname: '[不定位_前四一码]',
                    tips: '从0-9中任意选择1个以上号码。',
                    example: '投注方案：1；开奖号码前四位：至少出现1个1，即中前四一码不定位一等奖。',
                    help: '从0-9中选择1个号码，每注由1个号码组成，只要开奖号码的万位、千位、百位、十位中包含所选号码，即为中奖。',
                    select: {
                        layout: [{
                            title: '不定位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '前四二码',
                    code: 'bdwsix2mq',
                    realname: '[不定位_前四二码]',
                    tips: '从0-9中任意选择2个以上号码。',
                    example: '投注方案：1,2；开奖号码前四位：至少出现1和2各1个，即中前四二码不定位一等奖。',
                    help: '从0-9中选择2个号码，每注由2个不同的号码组成，开奖号码的万位、千位、百位、十位中同时包含所选的2个号码，即为中奖。',
                    select: {
                        layout: [{
                            title: '不定位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '后四一码',
                    code: 'bdwsix1mh',
                    realname: '[不定位_后四一码]',
                    tips: '从0-9中任意选择1个以上号码。',
                    example: '从0-9中选择1个号码，每注由1个号码组成，只要开奖号码的千位、百位、十位、个位中包含所选号码，即为中奖。',
                    help: '帮助',
                    select: {
                        layout: [{
                            title: '不定位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '后四二码',
                    code: 'bdwsix2mh',
                    realname: '[不定位_后四二码]',
                    tips: '从0-9中任意选择2个以上号码。',
                    example: '投注方案：1,2；开奖号码后四位：至少出现1和2各1个，即中后四二码不定位一等奖。',
                    help: '从0-9中选择2个号码，每注由2个不同的号码组成，开奖号码的千位、百位、十位、个位中同时包含所选的2个号码，即为中奖。',
                    select: {
                        layout: [{
                            title: '不定位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }]
            }],
            [{
                title: '五星',
                columns: [{
                    name: '五星二码',
                    code: 'bdwwx2m',
                    realname: '[不定位_五星二码]',
                    tips: '从0-9中任意选择2个以上号码。',
                    example: '投注方案：1,2；开奖号码中至少出现1和2各1个，即中五星二码不定位一等奖。',
                    help: '从0-9中选择2个号码，每注由2个不同的号码组成，开奖号码的万位、千位、百位、十位、个位中同时包含所选的2个号码，即为中奖。',
                    select: {
                        layout: [{
                            title: '不定位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '五星三码',
                    code: 'bdwwx3m',
                    realname: '[不定位_五星三码]',
                    tips: '从0-9中任意选择3个以上号码。',
                    example: '投注方案：1,2,3；开奖号码中至少出现1和2和3各1个，即中五星三码不定位一等奖。',
                    help: '从0-9中选择2个号码，每注由3个不同的号码组成，开奖号码的万位、千位、百位、十位、个位中同时包含所选的3个号码，即为中奖。',
                    select: {
                        layout: [{
                            title: '不定位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }]
            }]
        ]
    }, {
        title: '任选',
        code: 'sscrx',
        rows: [
            [{
                title: '直选　复试',
                columns: [{
                    name: '任二',
                    code: 'rx2fs',
                    realname: '[任选二_复试]',
                    tips: '万、千、百、十、个任意2位，开奖号分别对应且顺序一致即中奖',
                    example: '投注方案：万位1，百位2；开奖号码：13245， 即中任选二直选一等奖。',
                    help: '从万、千、百、十、个中至少2个位置各选一个或多个号码，将各个位置的号码进行组合，所选号码的各位与开奖号码相同则中奖。',
                    select: {
                        layout: [{
                            title: '万位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '千位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '百位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '十位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '个位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '任三',
                    code: 'rx3fs',
                    realname: '[任选三_复试]',
                    tips: '万、千、百、十、个任意3位，开奖号分别对应且顺序一致即中奖',
                    example: '万位买0，千位买1，百万买2，十位买3，开奖01234，则中奖。',
                    help: '从万位、千位、百位、十位、个位中至少三位上各选1个号码组成一注，所选号码与开奖号码的指定位置上的号码相同，且顺序一致，即为中奖。',
                    select: {
                        layout: [{
                            title: '万位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '千位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '百位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '十位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '个位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '任四',
                    code: 'rx4fs',
                    realname: '[任选四_复试]',
                    tips: '万、千、百、十、个任意4位，开奖号分别对应且顺序一致即中奖',
                    example: '投注方案：万位买0，千位买1，百万买2，十位买3，个位买4，开奖01234，则中奖。',
                    help: '从万、千、百、十、个中至少4个位置各选一个或多个号码，将各个位置的号码进行组合，所选号码的各位与开奖号码相同则中奖。',
                    select: {
                        layout: [{
                            title: '万位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '千位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '百位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '十位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }, {
                            title: '个位',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }]
            }],
            [{
                title: '直选　单式',
                columns: [{
                    name: '任二',
                    code: 'rx2ds',
                    realname: '[任选二_单式]',
                    tips: '手动输入号码，至少输入1个两位数号码和至少选择两个位置',
                    example: '投注方案：输入号码01并选择万、千位置位，如开奖号码位01***； 则中奖。',
                    help: '手动输入一注或者多注的两个号码和至少两个位置，如果选中的号码与位置和开奖号码对应则中奖。',
                    checkbox: {
                        layout: [{
                            title: '位置',
                            value: ['万位', '千位', '百位', '十位', '个位']
                        }]
                    },
                    textarea: {}
                }, {
                    name: '任三',
                    code: 'rx3ds',
                    realname: '[任选三_单式]',
                    tips: '手动输入号码，至少输入1个三位数号码和至少选择三个位置',
                    example: '投注方案：输入号码012选择万、千、百位置，如开奖号码位012**； 则中奖。"',
                    help: '手动输入一注或者多注的三个号码和至少三个位置，如果选中的号码与位置和开奖号码对应则中奖。',
                    checkbox: {
                        layout: [{
                            title: '位置',
                            value: ['万位', '千位', '百位', '十位', '个位']
                        }]
                    },
                    textarea: {}
                }, {
                    name: '任四',
                    code: 'rx4ds',
                    realname: '[任选四_单式]',
                    tips: '手动输入号码，至少输入1个四位数号码和至少选择四个位置',
                    example: '投注方案：输入号码0123选择万、千、百、十位置，如开奖号码位0123*； 则中奖。',
                    help: '手动输入一注或者多注的四个号码和至少四个位置，如果选中的号码与位置和开奖号码对应则中奖。',
                    checkbox: {
                        layout: [{
                            title: '位置',
                            value: ['万位', '千位', '百位', '十位', '个位']
                        }]
                    },
                    textarea: {}
                }]
            }],
            [{
                title: '任选　组选',
                columns: [{
                    name: '任三组三',
                    code: 'rx3z3',
                    realname: '[任选三_组三]',
                    tips: '从0-9中任意选择2个或2个以上号码和任意三个位置',
                    example: '投注方案：位置选择万、千、百，号码选择01；开奖号码为110**、则中奖。',
                    help: '从0-9中任意选择2个或2个以上号码和万、千、百、十、个任意的三个位置，如何组合的号码与开奖号码对应则中奖。',
                    checkbox: {
                        layout: [{
                            title: '位置',
                            value: ['万位', '千位', '百位', '十位', '个位']
                        }]
                    },
                    select: {
                        layout: [{
                            title: '号码',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '任三组六',
                    code: 'rx3z6',
                    realname: '[任选三_组六]',
                    tips: '从0-9中任意选择3个或3个以上号码和任意三个位置',
                    example: '投注方案：位置选择万、千、百，号码选择012；开奖号码为012**、则中奖。',
                    help: '从0-9中任意选择3个或3个以上号码和万、千、百、十、个任意的三个位置，如何组合的号码与开奖号码对应则中奖。',
                    checkbox: {
                        layout: [{
                            title: '位置',
                            value: ['万位', '千位', '百位', '十位', '个位']
                        }]
                    },
                    select: {
                        layout: [{
                            title: '号码',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }, {
                    name: '任三混合组选',
                    code: 'rx3hhzx',
                    realname: '[任选三_混合组选]',
                    tips: '手动输入号码，至少输入1个三位数号码和至少选择三个位置',
                    example: '投注方案：位置选择万、千、百，号码输入001以及123，开奖号码万千百位包括：(1)0,0,1，顺序不限，即中得组三一等奖；或者(2)1,2,3，顺序不限，即中得组六一等奖。',
                    help: '任意选择3个以上位置，并动输入购买号码，3个数字为一注，所选位置与开奖号码相同并符合组三或组六均为中奖。',
                    checkbox: {
                        layout: [{
                            title: '位置',
                            value: ['万位', '千位', '百位', '十位', '个位']
                        }]
                    },
                    textarea: {}
                }, {
                    name: '任二组选',
                    code: 'rx2zx',
                    realname: '[任选二_组选]',
                    tips: '从0-9中任意选择2个或2个以上号码和任意两个位置',
                    example: '投注方案：位置选择万、千，号码选择01；开奖号码为01***、则中奖。',
                    help: '从0-9中任意选择2个或2个以上号码和万、千、百、十、个任意的两个位置，如何组合的号码与开奖号码对应则中奖',
                    checkbox: {
                        layout: [{
                            title: '位置',
                            value: ['万位', '千位', '百位', '十位', '个位']
                        }]
                    },
                    select: {
                        layout: [{
                            title: '号码',
                            balls: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                            tools: true
                        }]
                    }
                }]
            }]
        ]
    }, {
        title: '龙虎',
        code: 'ssclh',
        rows: [[{
            title: '龙虎',
            columns: [{
                name: '万千',
                code: 'longhuhewq',
                realname: '[龙虎和_万千]',
                tips: '选择龙或虎或和。',
                example: '在龙、虎、和三种形态中选择一种。用开奖号码的万位、千位数字比较大小，万位大于千位为龙；万位小于千位为虎；万位等于千位为和。开奖结果与投注项相符，即为中奖。',
                help: '选择龙或虎或和，只要开奖号码符合龙虎和规则、即为中奖。',
                select: {
                    layout: [{
                        title: '万千',
                        balls: ['龙','虎', '和']
                    }]
                },
                prizeIndexes:[0,0,1]
            }, {
                name: '万百',
                code: 'longhuhewb',
                realname: '[龙虎和_万百]',
                tips: '选择龙或虎或和。',
                example: '在龙、虎、和三种形态中选择一种。用开奖号码的万位、百位数字比较大小，万位大于百位为龙；万位小于百位为虎；万位等于百位为和。开奖结果与投注项相符，即为中奖。',
                help: '选择龙或虎或和，只要开奖号码符合龙虎规则、即为中奖。',
                select: {
                    layout: [{
                        title: '万百',
                        balls: ['龙','虎', '和']
                    }]
                },
                prizeIndexes:[0,0,1]
            }, {
                name: '万十',
                code: 'longhuhews',
                realname: '[龙虎和_万十]',
                tips: '选择龙或虎或和。',
                example: '在龙、虎、和三种形态中选择一种。用开奖号码的万位、十位数字比较大小，万位大于十位为龙；万位小于十位为虎；万位等于十位为和。开奖结果与投注项相符，即为中奖。',
                help: '选择龙或虎或和，只要开奖号码符合龙虎规则、即为中奖。',
                select: {
                    layout: [{
                        title: '万十',
                        balls: ['龙','虎', '和']
                    }]
                },
                prizeIndexes:[0,0,1]
            }, {
                name: '万个',
                code: 'longhuhewg',
                realname: '[龙虎和_万个]',
                tips: '选择龙或虎或和。',
                example: '在龙、虎、和三种形态中选择一种。用开奖号码的万位、个位数字比较大小，万位大于个位为龙；万位小于个位为虎；万位等于个位为和。开奖结果与投注项相符，即为中奖。',
                help: '选择龙或虎或和，只要开奖号码符合龙虎规则、即为中奖。',
                select: {
                    layout: [{
                        title: '万个',
                        balls: ['龙','虎', '和']
                    }]
                },
                prizeIndexes:[0,0,1]
            }, {
                name: '千百',
                code: 'longhuheqb',
                realname: '[龙虎和_千百]',
                tips: '选择龙或虎或和。',
                example: '在龙、虎、和三种形态中选择一种。用开奖号码的千位、百位数字比较大小，千位大于百位为龙；千位小于百位为虎；千位等于百位为和。开奖结果与投注项相符，即为中奖。',
                help: '选择龙或虎或和，只要开奖号码符合龙虎规则、即为中奖。',
                select: {
                    layout: [{
                        title: '千百',
                        balls: ['龙','虎', '和']
                    }]
                },
                prizeIndexes:[0,0,1]
            }, {
                name: '千十',
                code: 'longhuheqs',
                realname: '[龙虎和_千十]',
                tips: '选择龙或虎或和。',
                example: '在龙、虎、和三种形态中选择一种。用开奖号码的千位、十位数字比较大小，千位大于十位为龙；千位小于十位为虎；千位等于十位为和。开奖结果与投注项相符，即为中奖。',
                help: '选择龙或虎或和，只要开奖号码符合龙虎规则、即为中奖。',
                select: {
                    layout: [{
                        title: '千十',
                        balls: ['龙','虎', '和']
                    }]
                },
                prizeIndexes:[0,0,1]
            }, {
                name: '千个',
                code: 'longhuheqg',
                realname: '[龙虎和_千个]',
                tips: '选择龙或虎或和。',
                example: '在龙、虎、和三种形态中选择一种。用开奖号码的千位、个位数字比较大小，千位大于个位为龙；千位小于个位为虎；千位等于个位为和。开奖结果与投注项相符，即为中奖。',
                help: '选择龙或虎或和，只要开奖号码符合龙虎规则、即为中奖。',
                select: {
                    layout: [{
                        title: '千个',
                        balls: ['龙','虎', '和']
                    }]
                },
                prizeIndexes:[0,0,1]
            }, {
                name: '百十',
                code: 'longhuhebs',
                realname: '[龙虎和_百十]',
                tips: '选择龙或虎或和。',
                example: '在龙、虎、和三种形态中选择一种。用开奖号码的百位、十位数字比较大小，百位大于十位为龙；百位小于十位为虎；百位等于十位为和。开奖结果与投注项相符，即为中奖。',
                help: '选择龙或虎或和，只要开奖号码符合龙虎规则、即为中奖。',
                select: {
                    layout: [{
                        title: '百十',
                        balls: ['龙','虎', '和']
                    }]
                },
                prizeIndexes:[0,0,1]
            }, {
                name: '百个',
                code: 'longhuhebg',
                realname: '[龙虎和_百个]',
                tips: '选择龙或虎或和。',
                example: '在龙、虎、和三种形态中选择一种。用开奖号码的百位、个位数字比较大小，百位大于个位为龙；百位小于个位为虎；百位等于个位为和。开奖结果与投注项相符，即为中奖。',
                help: '选择龙或虎或和，只要开奖号码符合龙虎规则、即为中奖。',
                select: {
                    layout: [{
                        title: '百个',
                        balls: ['龙','虎', '和']
                    }]
                },
                prizeIndexes:[0,0,1]
            }, {
                name: '十个',
                code: 'longhuhesg',
                realname: '[龙虎和_十个]',
                tips: '选择龙或虎或和。',
                example: '在龙、虎、和三种形态中选择一种。用开奖号码的十位、个位数字比较大小，十位大于个位为龙；十位小于个位为虎；十位等于个位为和。开奖结果与投注项相符，即为中奖。',
                help: '选择龙或虎或和，只要开奖号码符合龙虎规则、即为中奖。',
                select: {
                    layout: [{
                        title: '十个',
                        balls: ['龙','虎', '和']
                    }]
                },
                prizeIndexes:[0,0,1]
            }]
        }]]
    }, {
        title: '牛牛',
        code: 'sscniuniu',
        rows: [[{
            title: '牛牛',
            columns: [{
                name: '牛牛',
                code: 'sscniuniu',
                realname: '[牛牛]',
                tips: '任意选择一个号码形态组成一注。',
                example:
                '牛1-牛9：开奖号码中任意三位数相加可以为0或10的倍数，另外两个数字相加的个位数为1-9，如：00026为牛8、02818为牛9； <br>' +
                '牛牛：开奖号码中任意三位数相加可以为0或10的倍数，另外两个数字相加的个位数为0，如：68628、23500；<br>' +
                '无牛：开奖号码中任意三位数相加都无法是0或10的倍数，如：37378、19197；  <br>' +
                '牛大：牛6、牛7、牛8、牛9、牛牛；<br>'+
                '牛小：牛1、牛2、牛3、牛4、牛5；<br>'+
                '牛单：牛1、牛3、牛5、牛7、牛9；<br>'+
                '牛双：牛2、牛4、牛6、牛8、牛牛；<br>'+
                '五条：开出五个号码中五个相同。如：00000/99999；<br>'+
                '炸弹：开出五个号码中四个相同。如：10000/22221；<br>'+
                '葫芦：开出五个号码中三个相同(三条)及两个号码相同(一对)。如：11222/33444；<br>'+
                '顺子：开出五个号码从小到大排列为01234/ 12345/ 23456/ 34567/ 45678/ 56789/ 06789/ 01789/ 01289/01239；<br>'+
                '三条：开出五个号码中三个相同(三条)及两个号码不相同。如：12333/66678；<br>'+
                '两对：开出五个号码中有两组相同。如：00112/36677；<br>'+
                '单对：开出五个号码中只有一组相同。如：00123/13556；<br>'+
                '散号：开出五个号码没有任何相同或相关联。如：12356/01345/34678/12579；<br>',
                help: '所选号码符合以上规则、即为中奖。',
                select: {
                    layout: [{
                        title: '',
                        balls: ['牛大','牛小', '牛单', '牛双', '无牛', '牛牛', '牛1', '牛2', '牛3', '牛4', '牛5', '牛6', '牛7', '牛8', '牛9', '五条', '炸弹', '葫芦', '顺子', '三条', '两对', '单对', '散号'],
                        tools: false,
                        cls: 'text width70'
                    }]
                },
                prizeIndexes: 'selectedIndex'
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
        var checkIndex = groupLength >= 8 ? 8 : 0;
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
            els().find('div.slider').noUiSlider({ range: { 'max': uMaxCode, 'min': 1800 } }, true);
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
                if (start < 1800) {
                    start = 1800;
                }
            }
        }
        slider.noUiSlider({ connect: 'lower', behaviour: 'snap', step: 2, start: start, range: { 'max': uMaxCode, 'min': 1800 } });
        if (uMaxCode <= 1800) {
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
        if (uMaxCode > 1800) {
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
        	uMaxCode = UserData.code > 1960 ? 1960 : UserData.code;
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
            var format = $textarea.val().replace(/\,|\;|\n|\t/g, ' ');
            var as = format.split(' ');
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
        $target.val($target.val().replace(/\,|\;|\n/g, ' '))
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
        if (validateMinMaxNumbers && validateMinMaxNumbers[0].length > 0) {
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
                $.YF.hideLoadingMask();

                if (res.error == 0) {
                    balance = Number(res.data.balance);
                }
                else {
                    $.YF.alert_warning(res.message);
                    return;
                }
                
//                if ("li" === model || '1li' === model) {
//                    if (Number(balance) < 0.2) {
//                        $.YF.hideLoadingMask();
//                        $.YF.alert_warning("每单至少投注0.2元！")
//                        return;
//                    }
//                }
                
                if (Number(balance) < total) {
                    $.YF.alert_warning('您的账户余额不足,不能梭哈！');
                    return;
                }
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