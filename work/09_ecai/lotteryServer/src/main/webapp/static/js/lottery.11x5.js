$(function(){
    var textAreaReplaceRegex = /\,|\n/g;
    var textAreaReplaceWith = ';';
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
    var uLocPoint = UserData.lp;
    var uNotLocPoint = UserData.nlp;
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
        var _inputCheck_Num = function(datasel, l, fun) {
            fun = $.isFunction(fun) ? fun : function(n, l) {
                return true;
            }
            var newsel = []; // 新的号码
            datasel = ArrayUtil.unique(datasel); // 去除重复
            var regex = new RegExp('^([0-9]{2}\\s{1}){' + (l - 1) + '}[0-9]{2}$');
            $.each(datasel, function(i, n) {
                if(regex.test(n) && fun(n, l)) {
                    newsel.push(n);
                }
            });
            return newsel;
        }

        /**
         * 输入框号码检测
         */
        var _numberCheck_Num = function(n) {
            var t = n.split(' ');
            var l = t.length;
            for (var i = 0; i < l; i++) {
                if (Number(t[i]) > 11 || Number(t[i]) < 1) {
                    return false;
                }
                for (var j = i + 1; j < l; j++) {
                    if (Number(t[i]) == Number(t[j])) {
                        return false;
                    }
                }
            }
            return true;
        }

        /**
         * 多少注计算
         */
        var _inputNumbers = function(type, datasel) {
            var nums = 0;
            switch (type) {
                // 这里验证输入框类型
                case 'sanmzhixdsq':
                case 'sanmzuxdsq':
                    return _inputCheck_Num(datasel, 3, _numberCheck_Num).length;
                case 'ermzhixdsq':
                case 'ermzuxdsq':
                    return _inputCheck_Num(datasel, 2, _numberCheck_Num).length;
                case 'rx1ds':
                    return _inputCheck_Num(datasel, 1, _numberCheck_Num).length;
                case 'rx2ds':
                    return _inputCheck_Num(datasel, 2, _numberCheck_Num).length;
                case 'rx3ds':
                    return _inputCheck_Num(datasel, 3, _numberCheck_Num).length;
                case 'rx4ds':
                    return _inputCheck_Num(datasel, 4, _numberCheck_Num).length;
                case 'rx5ds':
                    return _inputCheck_Num(datasel, 5, _numberCheck_Num).length;
                case 'rx6ds':
                    return _inputCheck_Num(datasel, 6, _numberCheck_Num).length;
                case 'rx7ds':
                    return _inputCheck_Num(datasel, 7, _numberCheck_Num).length;
                case 'rx8ds':
                    return _inputCheck_Num(datasel, 8, _numberCheck_Num).length;
                // 这里验证选号类型
                case 'sanmzhixfsq':
                    if (datasel[0].length > 0 && datasel[1].length > 0 && datasel[2].length > 0) {
                        for (var i = 0; i < datasel[0].length; i++) {
                            for (var j = 0; j < datasel[1].length; j++) {
                                for (var k = 0; k < datasel[2].length; k++) {
                                    if (datasel[0][i] != datasel[1][j] && datasel[0][i] != datasel[2][k] && datasel[1][j] != datasel[2][k]) {
                                        nums++;
                                    }
                                }
                            }
                        }
                    }
                    break;
                case 'sanmzuxfsq':
                    var maxplace = 1;
                    for (var i = 0; i < maxplace; i++) {
                        var s = datasel[i].length;
                        if (s > 2) {
                            nums += s * (s - 1) * (s - 2) / 6;
                        }
                    }
                    break;
                case 'ermzhixfsq':
                    if (datasel[0].length > 0 && datasel[1].length > 0) {
                        for (var i = 0; i < datasel[0].length; i++) {
                            for (var j = 0; j < datasel[1].length; j++) {
                                if (datasel[0][i] != datasel[1][j]) {
                                    nums++;
                                }
                            }
                        }
                    }
                    break;
                case 'ermzuxfsq':
                    var maxplace = 1;
                    for (var i = 0; i < maxplace; i++) {
                        var s = datasel[i].length;
                        if (s > 1) {
                            nums += s * (s - 1) / 2;
                        }
                    }
                    break;
                case 'bdw':
                case 'dwd':
                case 'dds':
                case 'czw':
                case 'rx1fs': // 任选1中1
                    var maxplace = 0;
                    if('bdw' == type || 'dds' == type || 'czw' == type || 'rx1fs' ==type) {
                        maxplace = 1;
                    }
                    if('dwd' == type) {
                        maxplace = 3;
                    }
                    for (var i = 0; i < maxplace; i++) {
                        nums += datasel[i].length;
                    }
                    break;
                case 'rx2fs': // 任选2中2
                    var maxplace = 1;
                    for (var i = 0; i < maxplace; i++) {
                        var s = datasel[i].length;
                        if (s > 1) {
                            nums += s * (s - 1) / 2;
                        }
                    }
                    break;
                case 'rx3fs': // 任选3中3
                    var maxplace = 1;
                    for (var i = 0; i < maxplace; i++) {
                        var s = datasel[i].length;
                        if (s > 2) {
                            nums += s * (s - 1) * (s - 2) / 6;
                        }
                    }
                    break;
                case 'rx4fs': // 任选4中4
                    var maxplace = 1;
                    for (var i = 0; i < maxplace; i++) {
                        var s = datasel[i].length;
                        if (s > 3) {
                            nums += s * (s - 1) * (s - 2) * (s - 3) / 24;
                        }
                    }
                    break;
                case 'rx5fs': // 任选5中5
                    var maxplace = 1;
                    for (var i = 0; i < maxplace; i++) {
                        var s = datasel[i].length;
                        if (s > 4) {
                            nums += s * (s - 1) * (s - 2) * (s - 3) * (s - 4) / 120;
                        }
                    }
                    break;
                case 'rx6fs': // 任选6中6
                    var maxplace = 1;
                    for (var i = 0; i < maxplace; i++) {
                        var s = datasel[i].length;
                        if (s > 5) {
                            nums += s * (s - 1) * (s - 2) * (s - 3) * (s - 4) * (s - 5) / 720;
                        }
                    }
                    break;
                case 'rx7fs': // 任选7中7
                    var maxplace = 1;
                    for (var i = 0; i < maxplace; i++) {
                        var s = datasel[i].length;
                        if (s > 6) {
                            nums += s * (s - 1) * (s - 2) * (s - 3) * (s - 4) * (s - 5) * (s - 6) / 5040;
                        }
                    }
                    break;
                case 'rx8fs': // 任选8中8
                    var maxplace = 1;
                    for (var i = 0; i < maxplace; i++) {
                        var s = datasel[i].length;
                        if (s > 7) {
                            nums += s * (s - 1) * (s - 2) * (s - 3) * (s - 4) * (s - 5) * (s - 6) * (s - 7) / 40320;
                        }
                    }
                    break;
                case 'rxtd2': //任选拖胆
                case 'rxtd3':
                case 'rxtd4':
                case 'rxtd5':
                case 'rxtd6':
                case 'rxtd7':
                case 'rxtd8':
                	 var maxplace = 1;
                	 var isNul = false;
                    for (i = 0; i <= maxplace; i++) {
                    	if (datasel[i].length == 0) { //有位置上没有选择
                    		isNul = true;
                            break;
                        }
                    }
                    if(isNul){
                    	break;
                    }
                    var betArr = ArrayUtil.uniquelize(datasel[0].concat(datasel[1]));
                    var gcode = Number(type.substring(4));
                    if (betArr.length >= gcode) {
                    	nums = ArrayUtil.ComNum(datasel[1].length, gcode - datasel[0].length);
                    } else {
                    	nums = 0;
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
                // 三码
                case "sanmzhixfsq":
                case "sanmzhixdsq":
                case "sanmzuxfsq":
                case "sanmzuxdsq":
                // 二码
                case "ermzhixfsq":
                case "ermzhixdsq":
                case "ermzuxfsq":
                case "ermzuxdsq":
                // 任选一中一
                case "rx1fs":
                case "rx1ds":
                // 任选二中二
                case "rx2fs":
                case "rx2ds":
                case "rxtd2":
                // 任选三中三
                case "rx3fs":
                case "rx3ds":
                case "rxtd3":
                // 任选四中四
                case "rx4fs":
                case "rx4ds":
                case "rxtd4":
                // 任选五中五
                case "rx5fs":
                case "rx5ds":
                case "rxtd5":
                // 任选六中五
                case "rx6fs":
                case "rx6ds":
                case "rxtd6":
                // 任选七中五
                case "rx7fs":
                case "rx7ds":
                case "rxtd7":
                // 任选八中五
                case "rx8fs":
                case "rx8ds":
                case "rxtd8":
                    var minNum = parseInt(minStr);
                    var maxNum = parseInt(maxStr);
                    if (num < minNum) {
                        return [1, minNum, num, "注"];
                    }
                    else if (num > maxNum) {
                        return [2, maxNum, num, "注"];
                    }
                    return null;
                // 不定位
                case "bdw":
                    var bdwMinNum = parseInt(minStr);
                    var bdwMaxNum = parseInt(maxStr);
                    if (num < bdwMinNum) {
                        return [1, bdwMinNum, num, "码"];
                    }
                    else if (num > bdwMaxNum) {
                        return [2, bdwMaxNum, num, "码"];
                    }
                    return null;
                // 定位胆
                case "dwd":
                    var maxplace = 3;
                    var dwdMinNum = parseInt(minStr);
                    var dwdMaxNum = parseInt(maxStr);
                    for (var i = 0; i < maxplace; i++) {
                        if (datasel[i].length < dwdMinNum) {
                            return [1, dwdMinNum, datasel[i].length, "码"];
                        }
                        else if (datasel[i].length > dwdMaxNum) {
                            return [2, dwdMaxNum, datasel[i].length, "码"];
                        }
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
                var f = datasel[i].toString().replace(/\,/g, ' ');
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
                case 'sanmzhixdsq':
                case 'sanmzuxdsq':
                    datasel = _inputCheck_Num(datasel, 3, _numberCheck_Num);
                    break;
                case 'ermzhixdsq':
                case 'ermzuxdsq':
                    datasel = _inputCheck_Num(datasel, 2, _numberCheck_Num);
                    break;
                case 'rx1ds':
                    datasel = _inputCheck_Num(datasel, 1, _numberCheck_Num);
                    break;
                case 'rx2ds':
                    datasel = _inputCheck_Num(datasel, 2, _numberCheck_Num);
                    break;
                case 'rx3ds':
                    datasel = _inputCheck_Num(datasel, 3, _numberCheck_Num);
                    break;
                case 'rx4ds':
                    datasel = _inputCheck_Num(datasel, 4, _numberCheck_Num);
                    break;
                case 'rx5ds':
                    datasel = _inputCheck_Num(datasel, 5, _numberCheck_Num);
                    break;
                case 'rx6ds':
                    datasel = _inputCheck_Num(datasel, 6, _numberCheck_Num);
                    break;
                case 'rx7ds':
                    datasel = _inputCheck_Num(datasel, 7, _numberCheck_Num);
                    break;
                case 'rx8ds':
                    datasel = _inputCheck_Num(datasel, 8, _numberCheck_Num);
                    break;
                default:
                    break;
            }
            return datasel.toString().replace(/\,/g, ';');
        }

        var _inputFormat = function(type, datasel) {
            switch (type) {
                case 'sanmzhixfsq':
                case 'dwd':
                    return _formatSelect_Num(datasel, 0, 2);
                case 'ermzhixfsq':
                    return _formatSelect_Num(datasel, 0, 3);
                case 'sanmzuxfsq':
                case 'ermzuxfsq':
                case 'bdw':
                case 'rx1fs':
                case 'rx2fs':
                case 'rx3fs':
                case 'rx4fs':
                case 'rx5fs':
                case 'rx6fs':
                case 'rx7fs':
                case 'rx8fs':
                	return datasel[0].toString();
                case 'rxtd1':
                case 'rxtd2':
                case 'rxtd3':
                case 'rxtd4':
                case 'rxtd5':
                case 'rxtd6':
                case 'rxtd7':
                case 'rxtd8':
                	return _formatSelect_Num(datasel);
                case 'sanmzhixdsq':
                case 'sanmzuxdsq':
                case 'ermzhixdsq':
                case 'ermzuxdsq':
                case 'rx1ds':
                case 'rx2ds':
                case 'rx3ds':
                case 'rx4ds':
                case 'rx5ds':
                case 'rx6ds':
                case 'rx7ds':
                case 'rx8ds':
                    return _formatTextarea_Num(type, datasel);
                case 'dds':
                    return datasel[0].toString().replace(/\,/g , '|');
                case 'czw':
                    return datasel[0].toString();
                default:
                    break;
            }
        }

        var _typeFormat = function(code) {
            code.sort();
            var arr = [];
            var j = 0, o = 0, z = 0;
            $.each(code, function(idx, val) {
                var num = parseInt(val);
                if(num%2 == 0) {
                    o++;
                } else {
                    j++;
                }
                if(idx == 2) {
                    z = num;
                }
            });
            arr[0] = j + '单' + o + '双';
            arr[1] = z;
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
        title: '三码',
        code: 'x5sanma',
        rows: [[{
            title: '前三直选',
            columns: [{
                name: '复式',
                code: 'sanmzhixfsq',
                realname: '[前三直选_复式]',
                tips: '从第一位、第二位、第三位中至少各选择1个号码。',
                example: '',
                help: '从01-11共11个号码中选择3个不重复的号码组成一注，所选号码与当期顺序摇出的5个号码中的前3个号码相同，且顺序一致，即为中奖。',
                select: {
                    layout: [{
                        title: '第一位',
                        balls: ['01','02','03','04','05','06','07','08','09','10','11'],
                        tools: true
                    }, {
                        title: '第二位',
                        balls: ['01','02','03','04','05','06','07','08','09','10','11'],
                        tools: true
                    }, {
                        title: '第三位',
                        balls: ['01','02','03','04','05','06','07','08','09','10','11'],
                        tools: true
                    }]
                }
            }, {
                name: '单式',
                code: 'sanmzhixdsq',
                realname: '[前三直选_单式]',
                tips: '手动输入号码，至少输入1个三位数号码组成一注。',
                example: '',
                help: '手动输入3个号码组成一注，所输入的号码与当期顺序摇出的5个号码中的前3个号码相同，且顺序一致，即为中奖。',
                textarea: {}
            }]
        }], [{
            title: '前三组选',
            columns: [{
                name: '复式',
                code: 'sanmzuxfsq',
                realname: '[前三组选_复式]',
                tips: '从0-9中任意选择3个或3个以上号码。',
                example: '',
                help: '从01-11中共11个号码中选择3个号码，所选号码与当期顺序摇出的5个号码中的前3个号码相同，顺序不限，即为中奖。',
                select: {
                    layout: [{
                        title: '选号',
                        balls: ['01','02','03','04','05','06','07','08','09','10','11'],
                        tools: true
                    }]
                }
            }, {
                name: '单式',
                code: 'sanmzuxdsq',
                realname: '[前三组选_单式]',
                tips: '手动输入号码，至少输入1个三位数号码组成一注。',
                example: '',
                help: '手动输入3个号码组成一注，所输入的号码与当期顺序摇出的5个号码中的前3个号码相同，顺序不限，即为中奖。',
                textarea: {}
            }]
        }]]
    }, {
        title: '二码',
        code: 'x5erma',
        rows: [[{
            title: '前二直选',
            columns: [{
                name: '复式',
                code: 'ermzhixfsq',
                realname: '[前二直选_复式]',
                tips: '从第一位、第二位中至少各选择1个号码。',
                example: '',
                help: '从01-11共11个号码中选择2个不重复的号码组成一注，所选号码与当期顺序摇出的5个号码中的前2个号码相同，且顺序一致，即为中奖。',
                select: {
                    layout: [{
                        title: '第一位',
                        balls: ['01','02','03','04','05','06','07','08','09','10','11'],
                        tools: true
                    }, {
                        title: '第二位',
                        balls: ['01','02','03','04','05','06','07','08','09','10','11'],
                        tools: true
                    }]
                }
            }, {
                name: '单式',
                code: 'ermzhixdsq',
                realname: '[前二直选_单式]',
                tips: '手动输入号码，至少输入1个两位数号码组成一注。',
                example: '',
                help: '手动输入2个号码组成一注，所输入的号码与当期顺序摇出的5个号码中的前2个号码相同，且顺序一致，即为中奖。',
                textarea: {}
            }]
        }], [{
            title: '前二组选',
            columns: [{
                name: '复式',
                code: 'ermzuxfsq',
                realname: '[前二组选_复式]',
                tips: '从0-9中任意选择2个或2个以上号码。',
                example: '',
                help: '从01-11中共11个号码中选择2个号码，所选号码与当期顺序摇出的5个号码中的前2个号码相同，顺序不限，即为中奖。',
                select: {
                    layout: [{
                        title: '选号',
                        balls: ['01','02','03','04','05','06','07','08','09','10','11'],
                        tools: true
                    }]
                }
            }, {
                name: '单式',
                code: 'ermzuxdsq',
                realname: '[前二组选_单式]',
                tips: '手动输入号码，至少输入1个两位数号码组成一注。',
                example: '',
                help: '手动输入2个号码组成一注，所输入的号码与当期顺序摇出的5个号码中的前2个号码相同，顺序不限，即为中奖。',
                textarea: {}
            }]
        }]]
    }, {
        title: '不定位',
        code: 'x5bdw',
        rows: [[{
            title: '不定位',
            columns: [{
                name: '前三位',
                code: 'bdw',
                realname: '[不定位_前三位]',
                tips: '从01-11中任意选择1个或1个以上号码。',
                example: '',
                help: '从01-11中共11个号码中选择1个号码，每注由1个号码组成，只要当期顺序摇出的第一位、第二位、第三位开奖号码中包含所选号码，即为中奖。',
                select: {
                    layout: [{
                        title: '选号',
                        balls: ['01','02','03','04','05','06','07','08','09','10','11'],
                        tools: true
                    }]
                }
            }]
        }]]
    }, {
        title: '定位胆',
        code: 'x5dwd',
        rows: [[{
            title: '定位胆',
            columns: [{
                name: '定位胆',
                code: 'dwd',
                realname: '[定位胆]',
                tips: '从第一位，第二位，第三位任意位置上任意选择1个或1个以上号码。',
                example: '',
                help: '从第一位，第二位，第三位任意1个位置或多个位置上选择1个号码，所选号码与相同位置上的开奖号码一致，即为中奖。',
                select: {
                    layout: [{
                        title: '第一位',
                        balls: ['01','02','03','04','05','06','07','08','09','10','11'],
                        tools: true
                    }, {
                        title: '第二位',
                        balls: ['01','02','03','04','05','06','07','08','09','10','11'],
                        tools: true
                    }, {
                        title: '第三位',
                        balls: ['01','02','03','04','05','06','07','08','09','10','11'],
                        tools: true
                    }]
                }
            }]
        }]]
    }, {
        title: '趣味型',
        code: 'x5qw',
        rows: [[{
            title: '趣味型',
            columns: [{
                name: '定单双',
                code: 'dds',
                realname: '[定单双]',
                tips: '从不同的单双组合中任意选择1个或1个以上的组合。',
                example: '',
                help: '从5种单双个数组合中选择1种组合，当期开奖号码的单双个数与所选单双组合一致，即为中奖。',
                select: {
                    layout: [{
                        title: '定单双',
                        balls: ['5单0双', '4单1双', '3单2双', '2单3双', '1单4双', '0单5双'],
                        tools: false
                    }]
                },
                prizeIndexes:'selectedIndex'
            }, {
                name: '猜中位',
                code: 'czw',
                realname: '[猜中位]',
                tips: '从3-9中任意选择1个或1个以上数字。',
                example: '',
                help: '从3-9中选择1个号码进行购买，所选号码与5个开奖号码按照大小顺序排列后的第3个号码相同，即为中奖。',
                select: {
                    layout: [{
                        title: '猜中位',
                        balls: ['03','04','05','06','07','08','09'],
                        tools: true
                    }]
                },
                prizeIndexes:[0,1,2,3,2,1,0]
            }]
        }]]
    }, {
        title: '任选',
        code: 'x5rx',
        rows: [[{
            title: '任选复式',
            columns: [{
                name: '一中一',
                code: 'rx1fs',
                realname: '[任选复式_一中一]',
                tips: '从01-11中任意选择1个或1个以上号码。',
                example: '',
                help: '从01-11共11个号码中选择1个号码进行购买，只要当期顺序摇出的5个开奖号码中包含所选号码，即为中奖。',
                select: {
                    layout: [{
                        title: '一中一',
                        balls: ['01','02','03','04','05','06','07','08','09','10','11'],
                        tools: true
                    }]
                }
            }, {
                name: '二中二',
                code: 'rx2fs',
                realname: '[任选复式_二中二]',
                tips: '从01-11中任意选择2个或2个以上号码。',
                example: '',
                help: '从01-11共11个号码中选择2个号码进行购买，只要当期顺序摇出的5个开奖号码中包含所选号码，即为中奖。',
                select: {
                    layout: [{
                        title: '二中二',
                        balls: ['01','02','03','04','05','06','07','08','09','10','11'],
                        tools: true
                    }]
                }
            }, {
                name: '三中三',
                code: 'rx3fs',
                realname: '[任选复式_三中三]',
                tips: '从01-11中任意选择3个或3个以上号码。',
                example: '',
                help: '从01-11共11个号码中选择3个号码进行购买，只要当期顺序摇出的5个开奖号码中包含所选号码，即为中奖。',
                select: {
                    layout: [{
                        title: '三中三',
                        balls: ['01','02','03','04','05','06','07','08','09','10','11'],
                        tools: true
                    }]
                }
            }, {
                name: '四中四',
                code: 'rx4fs',
                realname: '[任选复式_四中四]',
                tips: '从01-11中任意选择4个或4个以上号码。',
                example: '',
                help: '从01-11共11个号码中选择4个号码进行购买，只要当期顺序摇出的5个开奖号码中包含所选号码，即为中奖。',
                select: {
                    layout: [{
                        title: '四中四',
                        balls: ['01','02','03','04','05','06','07','08','09','10','11'],
                        tools: true
                    }]
                }
            }, {
                name: '五中五',
                code: 'rx5fs',
                realname: '[任选复式_五中五]',
                tips: '从01-11中任意选择5个或5个以上号码。',
                example: '',
                help: '从01-11共11个号码中选择5个号码进行购买，只要当期顺序摇出的5个开奖号码中包含所选号码，即为中奖。',
                select: {
                    layout: [{
                        title: '五中五',
                        balls: ['01','02','03','04','05','06','07','08','09','10','11'],
                        tools: true
                    }]
                }
            }, {
                name: '六中五',
                code: 'rx6fs',
                realname: '[任选复式_六中五]',
                tips: '从01-11中任意选择6个或6个以上号码。',
                example: '',
                help: '从01-11共11个号码中选择6个号码进行购买，只要当期顺序摇出的5个开奖号码中包含所选号码，即为中奖。',
                select: {
                    layout: [{
                        title: '六中五',
                        balls: ['01','02','03','04','05','06','07','08','09','10','11'],
                        tools: true
                    }]
                }
            }, {
                name: '七中五',
                code: 'rx7fs',
                realname: '[任选复式_七中五]',
                tips: '从01-11中任意选择7个或7个以上号码。',
                example: '',
                help: '从01-11共11个号码中选择7个号码进行购买，只要当期顺序摇出的5个开奖号码中包含所选号码，即为中奖。',
                select: {
                    layout: [{
                        title: '七中五',
                        balls: ['01','02','03','04','05','06','07','08','09','10','11'],
                        tools: true
                    }]
                }
            }, {
                name: '八中五',
                code: 'rx8fs',
                realname: '[任选复式_八中五]',
                tips: '从01-11中任意选择8个或8个以上号码。',
                example: '',
                help: '从01-11共11个号码中选择8个号码进行购买，只要当期顺序摇出的5个开奖号码中包含所选号码，即为中奖。',
                select: {
                    layout: [{
                        title: '八中五',
                        balls: ['01','02','03','04','05','06','07','08','09','10','11'],
                        tools: true
                    }]
                }
            }]
        }], [{
            title: '任选单式',
            columns: [{
                name: '一中一',
                code: 'rx1ds',
                realname: '[任选单式_一中一]',
                tips: '手动输入号码，从01-11中任意输入1个号码组成一注。',
                example: '投注提示：输入：01 02 即为2注；',
                help: '从01-11中手动输入1个号码进行购买，只要当期顺序摇出的5个开奖号码中包含所选号码，即为中奖。',
                textarea: {}
            }, {
                name: '二中二',
                code: 'rx2ds',
                realname: '[任选单式_二中二]',
                tips: '手动输入号码，从01-11中任意输入2个号码组成一注。',
                example: '投注提示：输入：01 02;03 04; 即为2注；',
                help: '从01-11共11中手动输入2个号码进行购买，只要当期顺序摇出的5个开奖号码中包含所选号码，即为中奖。',
                textarea: {}
            }, {
                name: '三中三',
                code: 'rx3ds',
                realname: '[任选单式_三中三]',
                tips: '手动输入号码，从01-11中任意输入3个号码组成一注。。',
                example: '投注提示：输入：01 02 03;03 04 05; 即为2注；',
                help: '从01-11共11中手动输入3个号码进行购买，只要当期顺序摇出的5个开奖号码中包含所选号码，即为中奖。',
                textarea: {}
            }, {
                name: '四中四',
                code: 'rx4ds',
                realname: '[任选单式_四中四]',
                tips: '手动输入号码，从01-11中任意输入4个号码组成一注。',
                example: '投注提示：输入：01 02 03 04;03 04 05 06; 即为2注；',
                help: '从01-11共11中手动输入4个号码进行购买，只要当期顺序摇出的5个开奖号码中包含所选号码，即为中奖。',
                textarea: {}
            }, {
                name: '五中五',
                code: 'rx5ds',
                realname: '[任选单式_五中五]',
                tips: '手动输入号码，从01-11中任意输入5个号码组成一注。',
                example: '投注提示：输入：01 02 03 04 05;03 04 05 06 07; 即为2注；',
                help: '从01-11共11中手动输入5个号码进行购买，只要当期顺序摇出的5个开奖号码中包含所选号码，即为中奖。',
                textarea: {}
            }, {
                name: '六中五',
                code: 'rx6ds',
                realname: '[任选单式_六中五]',
                tips: '手动输入号码，从01-11中任意输入6个号码组成一注。',
                example: '投注提示：输入：01 02 03 04 05 06;03 04 05 06 07 08; 即为2注；',
                help: '从01-11共11中手动输入6个号码进行购买，只要当期顺序摇出的5个开奖号码中包含所选号码，即为中奖。',
                textarea: {}
            }, {
                name: '七中五',
                code: 'rx7ds',
                realname: '[任选单式_七中五]',
                tips: '手动输入号码，从01-11中任意输入7个号码组成一注。',
                example: '投注提示：输入：01 02 03 04 05 06 07;03 04 05 06 07 08 09; 即为2注；',
                help: '从01-11共11中手动输入7个号码进行购买，只要当期顺序摇出的5个开奖号码中包含所选号码，即为中奖。',
                textarea: {}
            }, {
                name: '八中五',
                code: 'rx8ds',
                realname: '[任选单式_八中五]',
                tips: '手动输入号码，从01-11中任意输入8个号码组成一注。',
                example: '投注提示：输入：01 02 03 04 05 06 07 08;03 04 05 06 07 08 09 10; 即为2注；',
                help: '从01-11共11中手动输入8个号码进行购买，只要当期顺序摇出的5个开奖号码中包含所选号码，即为中奖。',
                textarea: {}
            }]
        }],[{
            title: '任选拖胆',
            columns: [{
                name: '二中二',
                code: 'rxtd2',
                realname: '[任选拖胆_二中二]',
                tips: '分别从胆码和拖码的01-11中，至少选择1个胆码和1个拖码组成一注。',
                example: '如：选择胆码 08，选择拖码 06，开奖号码为 06 08 11 09 02，即为中奖。',
                help: '分别从胆码和拖码的01-11中，至少选择1个胆码和1个拖码组成一注，只要当期顺序摇出的5个开奖号码中同时包含所选的1个胆码和1个拖码，所选胆码必须全中，即为中奖。',
                select: {
                    layout: [{
                        title: '胆码',
                        balls: ['01','02','03','04','05','06','07','08','09','10','11'],
                        maxcode:1,
                        tools: false
                    },{
                        title: '拖码',
                        balls: ['01','02','03','04','05','06','07','08','09','10','11'],
                        tools: true
                    }]
                }
            }, {
                name: '三中三',
                code: 'rxtd3',
                realname: '[任选拖胆_三中三]',
                tips: '分别从胆码和拖码的01-11中，至少选择1个胆码和2个拖码组成一注。',
                example: '如：选择胆码 08，选择拖码 06 11，开奖号码为 06 08 11 09 02，即为中奖。',
                help: '分别从胆码和拖码的01-11中，至少选择1个胆码和2个拖码组成一注，只要当期顺序摇出的5个开奖号码中同时包含所选的1个胆码和2个拖码，所选胆码必须全中，即为中奖。',
                select: {
                	layout: [{
                        title: '胆码',
                        balls: ['01','02','03','04','05','06','07','08','09','10','11'],
                        maxcode:2,
                        tools: false
                    },{
                        title: '拖码',
                        balls: ['01','02','03','04','05','06','07','08','09','10','11'],
                        tools: true
                    }]
                }
            }, {
                name: '四中四',
                code: 'rxtd4',
                realname: '[任选拖胆_四中四]',
                tips: '分别从胆码和拖码的01-11中，至少选择1个胆码和3个拖码组成一注。',
                example: '如：选择胆码 08，选择拖码 06 09 11，开奖号码为 06 08 11 09 02，即为中奖。',
                help: '分别从胆码和拖码的01-11中，至少选择1个胆码和3个拖码组成一注，只要当期顺序摇出的5个开奖号码中同时包含所选的1个胆码和3个拖码，所选胆码必须全中，即为中奖。',
                select: {
                	layout: [{
                        title: '胆码',
                        balls: ['01','02','03','04','05','06','07','08','09','10','11'],
                        maxcode:3,
                        tools: false
                    },{
                        title: '拖码',
                        balls: ['01','02','03','04','05','06','07','08','09','10','11'],
                        tools: true
                    }]
                }
            }, {
                name: '五中五',
                code: 'rxtd5',
                realname: '[任选拖胆_五中五]',
                tips: '分别从胆码和拖码的01-11中，至少选择1个胆码和4个拖码组成一注。',
                example: '如：选择胆码 08，选择拖码 02 06 09 11，开奖号码为  06 08 11 09 02，即为中奖。',
                help: '分别从胆码和拖码的01-11中，至少选择1个胆码和4个拖码组成一注，只要当期顺序摇出的5个开奖号码中同时包含所选的1个胆码和4个拖码，所选胆码必须全中，即为中奖。',
                select: {
                	layout: [{
                        title: '胆码',
                        balls: ['01','02','03','04','05','06','07','08','09','10','11'],
                        maxcode:4,
                        tools: false
                    },{
                        title: '拖码',
                        balls: ['01','02','03','04','05','06','07','08','09','10','11'],
                        tools: true
                    }]
                }
            }, {
                name: '六中五',
                code: 'rxtd6',
                realname: '[任选拖胆_六中五]',
                tips: '分别从胆码和拖码的01-11中，至少选择1个胆码和5个拖码组成一注。',
                example: '如：选择胆码 08，选择拖码 01 02 05 06 09 11，开奖号码为 06 08 11 09 02，即为中奖。',
                help: '分别从胆码和拖码的01-11中，至少选择1个胆码和5个拖码组成一注，只要当期顺序摇出的5个开奖号码同时存在于胆码和拖码的任意组合中，即为中奖。',
                select: {
                	layout: [{
                        title: '胆码',
                        balls: ['01','02','03','04','05','06','07','08','09','10','11'],
                        maxcode:5,
                        tools: false
                    },{
                        title: '拖码',
                        balls: ['01','02','03','04','05','06','07','08','09','10','11'],
                        tools: true
                    }]
                }
            }, {
                name: '七中五',
                code: 'rxtd7',
                realname: '[任选拖胆_七中五]',
                tips: '分别从胆码和拖码的01-11中，至少选择1个胆码和6个拖码组成一注。',
                example: '如：选择胆码 08，选择拖码 01 02 05 06 07 09 11，开奖号码为 06 08 11 09 02，即为中奖。',
                help: '分别从胆码和拖码的01-11中，至少选择1个胆码和6个拖码组成一注，只要当期顺序摇出的5个开奖号码同时存在于胆码和拖码的任意组合中，即为中奖。',
                select: {
                	layout: [{
                        title: '胆码',
                        balls: ['01','02','03','04','05','06','07','08','09','10','11'],
                        maxcode:6,
                        tools: false
                    },{
                        title: '拖码',
                        balls: ['01','02','03','04','05','06','07','08','09','10','11'],
                        tools: true
                    }]
                }
            }, {
                name: '八中五',
                code: 'rxtd8',
                realname: '[任选拖胆_八中五]',
                tips: '分别从胆码和拖码的01-11中，至少选择1个胆码和7个拖码组成一注。',
                example: '如：选择胆码 08，选择拖码 01 02 03 05 06 07 09 11，开奖号码为 06 08 11 09 02，即为中奖。',
                help: '分别从胆码和拖码的01-11中，至少选择1个胆码和7个拖码组成一注，只要当期顺序摇出的5个开奖号码中同时包含所选1个胆码和任意4个拖码，即为中奖。',
                select: {
                	layout: [{
                        title: '胆码',
                        balls: ['01','02','03','04','05','06','07','08','09','10','11'],
                        maxcode:7,
                        tools: false
                    },{
                        title: '拖码',
                        balls: ['01','02','03','04','05','06','07','08','09','10','11'],
                        tools: true
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
        var checkIndex = groupLength > 3 ? 3 : 0;
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
        $('.rule-btn:first', $bettingCondition).trigger('click');

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
//                $balls.addClass('active')
                $.each($balls, function(i, d) {
                	checkTDcode($(d));
                	$(d).addClass('active');
                });
                break
            case 'big':
                $.each($balls, function(i, d) {
                	if(i > max - 1 ){
                		checkTDcode($(d));
                	}
                    $(d)[i > max - 1 ? 'addClass' : 'removeClass']('active')
                })
                break
            case 'small':
                $.each($balls, function(i, d) {
                	if(i < max){
                		checkTDcode($(d));
                	}
                    $(d)[i < max ? 'addClass' : 'removeClass']('active')
                })
                break
            case 'single':
                $.each($balls, function(i, d) {
                	if(parseInt(d.innerHTML) % 2 > 0 ){
                		checkTDcode($(d));
                	}
                    $(d)[parseInt(d.innerHTML) % 2 > 0 ? 'addClass' : 'removeClass']('active')
                })
                break
            case 'double':
                $.each($balls, function(i, d) {
                	if(parseInt(d.innerHTML) % 2 < 1){
                		checkTDcode($(d));
                	}
                    $(d)[parseInt(d.innerHTML) % 2 < 1 ? 'addClass' : 'removeClass']('active')
                })
                break
            case 'clean':
                $balls.removeClass('active')
                break
        }
        PlayOptions.update();
    })
    
    function checkTDcode(obj){
    	if(activeData.code.indexOf('rxtd') != -1){
    		if(!obj.hasClass('active')){
    			var spanVal = obj.attr('data-val');
    			$bettingArea.find('span.active').each(function(i,o){
                	var val = $(o).attr('data-val');
                	if(spanVal == val){
                		$(o).removeClass('active');
                	}
                });
    		}
    	}
    }

    // 选择类球点击
    $bettingArea.on('click', 'dd.choose-pannel span', function(e) {
    	var $target = $(e.target);
    	checkTDcode($(this));
    	
        $target[$target.hasClass('active') ? 'removeClass' : 'addClass']('active');
        PlayOptions.update();
        
        //拖胆玩法验证
        if(activeData.code.indexOf('rxtd') != -1){
        	var datasel1 = getData()[0];
        	var datasel2 = getData()[1];
            var maccode = activeData.select.layout[0].maxcode;
            if(datasel1.length > maccode){
            	//取消第一个胆码选中
            	var $spans = $(this).siblings('.active');
            	$($spans[0]).removeClass('active');
                PlayOptions.update();
            }
        }
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
//                        $.YF.alert_warning("每单至少投注0.2元！")
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
                        bList.push({ lotteryId: Lottery.id, codes: codes, num: num, ruleId: ruleId, ruleCode: ruleCode,  multiple: multiple, model: model, code: code });

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