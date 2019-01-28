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
        };

        var _numberCheck_Num = function(n) {
            var t = n.split(' ');
            var l = t.length;
            for (var i = 0; i < l; i++) {
                if (Number(t[i]) > 10 || Number(t[i]) < 1) {
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

        var _inputNumbers = function(type, datasel) {
            var nums = 0, tmp_nums = 1;
            // 输入号
            switch (type) {
                case 'qianerzxds':
                    return _inputCheck_Num(datasel, 2, _numberCheck_Num).length;
                case 'qiansanzxds':
                    return _inputCheck_Num(datasel, 3, _numberCheck_Num).length;
                case 'qiansizxds':
                    return _inputCheck_Num(datasel, 4, _numberCheck_Num).length;
                case 'qianwuzxds':
                    return _inputCheck_Num(datasel, 5, _numberCheck_Num).length;
                // 选号
                case 'qiansanzxfs':
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
                case 'qianerzxfs':
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
                case 'qiansizxfs':
                    if (datasel[0].length > 0 && datasel[1].length > 0 && datasel[2].length > 0 && datasel[3].length > 0) {
                        for (var i = 0; i < datasel[0].length; i++) {
                            for (var j = 0; j < datasel[1].length; j++) {
                                for (var k = 0; k < datasel[2].length; k++) {
                                    for (var l = 0; l < datasel[3].length; l++) {
                                        var a = datasel[0][i];
                                        var b = datasel[1][j];
                                        var c = datasel[2][k];
                                        var d = datasel[3][l];
                                        if (a != b && a != c && a != d
                                            && b != c && b != d
                                            && c != d) {
                                            nums++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
                case 'qianwuzxfs':
                    if (datasel[0].length > 0 && datasel[1].length > 0 && datasel[2].length > 0 && datasel[3].length > 0 && datasel[4].length > 0) {
                        for (var i = 0; i < datasel[0].length; i++) {
                            for (var j = 0; j < datasel[1].length; j++) {
                                for (var k = 0; k < datasel[2].length; k++) {
                                    for (var l = 0; l < datasel[3].length; l++) {
                                        for (var m = 0; m < datasel[4].length; m++) {
                                            var a = datasel[0][i];
                                            var b = datasel[1][j];
                                            var c = datasel[2][k];
                                            var d = datasel[3][l];
                                            var e = datasel[4][m];
                                            if (a != b && a != c && a != d && a != e
                                                && b != c && b != d && b != e
                                                && c != d && c != e
                                                && d != e) {
                                                nums++;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
                case 'qwdingweidan':
                case 'hwdingweidan':
                case 'dw1dxds':
                case 'dw2dxds':
                case 'dw3dxds':
                case 'dw4dxds':
                case 'dw5dxds':
                case 'pk10_dxdsgyhz':
                case '01vs10':
                case '02vs09':
                case '03vs08':
                case '04vs07':
                case '05vs06':
                case 'pk10_hzgyhz':
                case 'pk10_hzqshz':
                    var maxplace = 1;
                    switch (type) {
                        case 'qwdingweidan': case 'hwdingweidan':
                        maxplace = 5;
                        break;
                        case 'dw1dxds':
                        case 'dw2dxds':
                        case 'dw3dxds':
                        case 'dw4dxds':
                        case 'dw5dxds':
                        case 'pk10_dxdsgyhz':
                        case '01vs10':
                        case '02vs09':
                        case '03vs08':
                        case '04vs07':
                        case '05vs06':
                        case 'pk10_hzgyhz':
                        case 'pk10_hzqshz':
                            maxplace = 1;
                            break;
                        default:
                            maxplace = 1;
                            break;
                    }
                    for (var i = 0; i < maxplace; i++) {
                        nums += datasel[i].length;
                    }
                    break;
                default:
                    var maxplace = 0;
                    if('qianyi' == type) maxplace = 1;
                    for (var i = 0; i < maxplace; i++) {
                        // 有位置上没有选择
                        if (datasel[i].length == 0) {
                            tmp_nums = 0; break;
                        }
                        tmp_nums *= datasel[i].length;
                    }
                    nums += tmp_nums;
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
                // 前一
                case "qianyi":
                // 前二
                case "qianerzxfs":
                case "qianerzxds":
                // 前三
                case "qiansanzxfs":
                case "qiansanzxds":
                // 前四
                case "qiansizxfs":
                case "qiansizxds":
                // 前五
                case "qianwuzxfs":
                case "qianwuzxds":
                // 和值冠亚和值
                case "pk10_hzgyhz":
                // 和值前三和值
                case "pk10_hzqshz":
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
                case "qwdingweidan":
                case "hwdingweidan":
                    var maxplace = 5;
                    var dingweidanMinNum = parseInt(minStr);
                    var dingweidanMaxNum = parseInt(maxStr);
                    for (var i = 0; i < maxplace; i++) {
                        if (datasel[i].length < dingweidanMinNum) {
                            return [1, dingweidanMinNum, datasel[i].length, "码"];
                        }
                        else if (datasel[i].length > dingweidanMaxNum) {
                            return [2, dingweidanMaxNum, datasel[i].length, "码"];
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
                case 'qianerzxds':
                    datasel = _inputCheck_Num(datasel, 2, _numberCheck_Num);
                    break;
                case 'qiansanzxds':
                    datasel = _inputCheck_Num(datasel, 3, _numberCheck_Num);
                    break;
                case 'qiansizxds':
                    datasel = _inputCheck_Num(datasel, 4, _numberCheck_Num);
                    break;
                case 'qianwuzxds':
                    datasel = _inputCheck_Num(datasel, 5, _numberCheck_Num);
                    break;
                default:
                    break;
            }
            return datasel.toString().replace(/\,/g, ';');
        }

        var _inputFormat = function(type, datasel) {
            switch (type) {
                case 'qianyi':
                    return datasel[0].toString();
                case 'qianerzxfs':
                case 'qiansanzxfs':
                case 'qiansizxfs':
                case 'qianwuzxfs':
                case 'qwdingweidan':
                case 'hwdingweidan':
                case 'dw1dxds':
                case 'dw2dxds':
                case 'dw3dxds':
                case 'dw4dxds':
                case 'dw5dxds':
                case 'pk10_dxdsgyhz':
                case '01vs10':
                case '02vs09':
                case '03vs08':
                case '04vs07':
                case '05vs06':
                    return _formatSelect_Num(datasel);
                case 'pk10_hzgyhz':
                case 'pk10_hzqshz':
                    return datasel.toString();
                case 'qianerzxds':
                case 'qiansanzxds':
                case 'qiansizxds':
                case 'qianwuzxds':
                    return _formatTextarea_Num(type, datasel);
                default:
                    break;
            }
        }

        return {
            inputNumbers: _inputNumbers,
            inputFormat: _inputFormat,
            validateMinMaxNumbers: _validateMinMaxNumbers
        }
    }();



    // 布局
    var layout = [{
        title: '前一',
        code: 'pk10qy',
        rows: [[{
            title: '前一',
            columns: [{
                name: '前一',
                code: 'qianyi',
                realname: '[前一]',
                tips: '从第一位中选择1个以上号码。',
                example: '选择的号码与开奖号码中第一位一致即中奖。',
                help: '从01-10选择一个号码组成一组，只要开奖号码的第一位与所选号码相同且顺序一致，即为中奖。',
                select: {
                    layout: [{
                        title: '第一位',
                        balls: ['01','02','03','04','05','06','07','08','09','10'],
                        tools: true
                    }]
                }
            }]
        }]]
    }, {
        title: '前二',
        code: 'pk10qe',
        rows: [[{
            title: '前二',
            columns: [{
                name: '前二复式',
                code: 'qianerzxfs',
                realname: '[前二_复式]',
                tips: '从第一位、第二位中至少各选择1个号码。',
                example: '投注方案：05 08；开奖号码第一位是05，第二位是08，即为中奖。',
                help: '从01-10选择两个号码组成一组，只要开奖号码的第一位和第二位与所选号码相同且顺序一致，即为中奖。',
                select: {
                    layout: [{
                        title: '第一位',
                        balls: ['01','02','03','04','05','06','07','08','09','10'],
                        tools: true
                    }, {
                        title: '第二位',
                        balls: ['01','02','03','04','05','06','07','08','09','10'],
                        tools: true
                    }]
                }
            }, {
                name: '前二单式',
                code: 'qianerzxds',
                realname: '[前二_单式]',
                tips: '手动输入号码，至少输入2个二位数号码组成一注。',
                example: '投注方案：01 02;03 04;05 06；开奖第一位和第二位的号码为01 02或03 04 或05 06；即为中奖。',
                help: '手动输入两个数字组成一组，只要开奖号码的第一位和第二位与所选号码相同且顺序一致，即为中奖。',
                textarea: {}
            }]
        }]]
    }, {
        title: '前三',
        code: 'pk10qsan',
        rows: [[{
            title: '前三',
            columns: [{
                name: '前三复式',
                code: 'qiansanzxfs',
                realname: '[前三_复式]',
                tips: '从第一位、第二位、第三位中至少各选择1个号码。',
                example: '投注方案：05 08 10；开奖号码第一位是05，第二位是08，第三位是 10即为中奖。',
                help: '从01-10选择三个号码组成一组，只要开奖号码的第一位、第二位、第三位与所选号码相同且顺序一致，即为中奖。',
                select: {
                    layout: [{
                        title: '第一位',
                        balls: ['01','02','03','04','05','06','07','08','09','10'],
                        tools: true
                    }, {
                        title: '第二位',
                        balls: ['01','02','03','04','05','06','07','08','09','10'],
                        tools: true
                    }, {
                        title: '第三位',
                        balls: ['01','02','03','04','05','06','07','08','09','10'],
                        tools: true
                    }]
                }
            }, {
                name: '前三单式',
                code: 'qiansanzxds',
                realname: '[前三_单式]',
                tips: '从01-10选择三个号码组成一注。',
                example: '投注方案：01 02 03; 04 05 06开奖第一位、第二位、第三位的号码为01 02 03或03 04 或04 05 06；即为中奖。',
                help: '手动输入三个数字组成一组，只要开奖号码的第一位、第二位、第三位与所选号码相同且顺序一致，即为中奖。',
                textarea: {}
            }]
        }]]
    }, {
        title: '前四',
        code: 'pk10qsi',
        rows: [[{
            title: '前四',
            columns: [{
                name: '前四复式',
                code: 'qiansizxfs',
                realname: '[前四_复式]',
                tips: '从第一位、第二位、第三位、第四位中至少各选择1个号码。',
                example: '投注方案：01 05 08 10；开奖号码第一位是01，第二位是05，第三位是08，第四位是10即为中奖。',
                help: '从01-10选择四个号码组成一组，只要开奖号码的第一位、第二位、第三位、第四位与所选号码相同且顺序一致，即为中奖。',
                select: {
                    layout: [{
                        title: '第一位',
                        balls: ['01','02','03','04','05','06','07','08','09','10'],
                        tools: true
                    }, {
                        title: '第二位',
                        balls: ['01','02','03','04','05','06','07','08','09','10'],
                        tools: true
                    }, {
                        title: '第三位',
                        balls: ['01','02','03','04','05','06','07','08','09','10'],
                        tools: true
                    }, {
                        title: '第四位',
                        balls: ['01','02','03','04','05','06','07','08','09','10'],
                        tools: true
                    }]
                }
            }, {
                name: '前四单式',
                code: 'qiansizxds',
                realname: '[前四_单式]',
                tips: '从01-10选择四个号码组成一注。',
                example: '投注方案：01 02 03 04; 05 06 07 08开奖第一位、第二位、第三位、第四位的号码为01 02 03 04或05 06 07 08；即为中奖。',
                help: '手动输入四个数字组成一组，只要开奖号码的第一位、第二位、第三位、第四位与所选号码相同且顺序一致，即为中奖。',
                textarea: {}
            }]
        }]]
    }, {
        title: '前五',
        code: 'pk10qw',
        rows: [[{
            title: '前五',
            columns: [{
                name: '前五复式',
                code: 'qianwuzxfs',
                realname: '[前五_复式]',
                tips: '从第一位、第二位、第三位、第四位、第五位中至少各选择1个号码。',
                example: '投注方案：01 03 05 08 10；开奖号码第一位是01，第二位是03，第三位是05，第四位是08，第五位是10即为中奖。',
                help: '从01-10选择五个号码组成一组，只要开奖号码的第一位、第二位、第三位、第四位、第五位与所选号码相同且顺序一致，即为中奖。',
                select: {
                    layout: [{
                        title: '第一位',
                        balls: ['01','02','03','04','05','06','07','08','09','10'],
                        tools: true
                    }, {
                        title: '第二位',
                        balls: ['01','02','03','04','05','06','07','08','09','10'],
                        tools: true
                    }, {
                        title: '第三位',
                        balls: ['01','02','03','04','05','06','07','08','09','10'],
                        tools: true
                    }, {
                        title: '第四位',
                        balls: ['01','02','03','04','05','06','07','08','09','10'],
                        tools: true
                    }, {
                        title: '第五位',
                        balls: ['01','02','03','04','05','06','07','08','09','10'],
                        tools: true
                    }]
                }
            }, {
                name: '前五单式',
                code: 'qianwuzxds',
                realname: '[前五_单式]',
                tips: '从01-10选择五个号码组成一注。',
                example: '投注方案：01 02 03 04 05; 06 07 08 09 10开奖第一位、第二位、第三位、第四位、第五位的号码为01 02 03 04 05或06 07 08 09 10；即为中奖。',
                help: '手动输入五个数字组成一组，只要开奖号码的第一位、第二位、第三位、第四位、第五位与所选号码相同且顺序一致，即为中奖。',
                textarea: {}
            }]
        }]]
    }, {
        title: '定位胆',
        code: 'pk10dwd',
        rows: [[{
            title: '定位胆',
            columns: [{
                name: '前五定位胆',
                code: 'qwdingweidan',
                realname: '[定位胆_前五定位胆]',
                tips: '在第一位、第二位、第三位、第四位、第五位任意位置上任意选择1个或1个以上号码。',
                example: '投注方案第一位01，开奖号码第一位为01，即为中奖',
                help: '在第一位、第二位、第三位、第四位、第五位任意位置上任意选择1个或1个以上号码。',
                select: {
                    layout: [{
                        title: '第一位',
                        balls: ['01','02','03','04','05','06','07','08','09','10'],
                        tools: true
                    }, {
                        title: '第二位',
                        balls: ['01','02','03','04','05','06','07','08','09','10'],
                        tools: true
                    }, {
                        title: '第三位',
                        balls: ['01','02','03','04','05','06','07','08','09','10'],
                        tools: true
                    }, {
                        title: '第四位',
                        balls: ['01','02','03','04','05','06','07','08','09','10'],
                        tools: true
                    }, {
                        title: '第五位',
                        balls: ['01','02','03','04','05','06','07','08','09','10'],
                        tools: true
                    }]
                }
            }, {
                name: '后五定位胆',
                code: 'hwdingweidan',
                realname: '[定位胆_后五定位胆]',
                tips: '在第六位、第七位、第八位、第九位、第十位任意位置上任意选择1个或1个以上号码。',
                example: '投注方案第一位01，开奖号码第一位为01，即为中奖',
                help: '在第六位、第七位、第八位、第九位、第十位任意位置上任意选择1个或1个以上号码。',
                select: {
                    layout: [{
                        title: '第六位',
                        balls: ['01','02','03','04','05','06','07','08','09','10'],
                        tools: true
                    }, {
                        title: '第七位',
                        balls: ['01','02','03','04','05','06','07','08','09','10'],
                        tools: true
                    }, {
                        title: '第八位',
                        balls: ['01','02','03','04','05','06','07','08','09','10'],
                        tools: true
                    }, {
                        title: '第九位',
                        balls: ['01','02','03','04','05','06','07','08','09','10'],
                        tools: true
                    }, {
                        title: '第十位',
                        balls: ['01','02','03','04','05','06','07','08','09','10'],
                        tools: true
                    }]
                }
            }]
        }]]
    }, {
        title: '大小单双',
        code: 'pk10dxds',
        rows: [[{
            title: '大小单双',
            columns: [{
                name: '第一位',
                code: 'dw1dxds',
                realname: '[大小单双_第一位]',
                tips: '选择大小单双。',
                example: '投注方案：大；开奖号码第一位是06、07、08、09、10即为中奖。',
                help: '大（06、07、08、09、10）；小（01、02、03、04、05）；单（01、03、05、07、09）；双（02、04、06、08、10）；',
                select: {
                    layout: [{
                        title: '第一位',
                        balls: ['大','小','单','双']
                    }]
                }
            }, {
                name: '第二位',
                code: 'dw2dxds',
                realname: '[大小单双_第二位]',
                tips: '选择大小单双。',
                example: '投注方案：大；开奖号码第二位是06、07、08、09、10即为中奖。',
                help: '大（06、07、08、09、10）；小（01、02、03、04、05）；单（01、03、05、07、09）；双（02、04、06、08、10）；',
                select: {
                    layout: [{
                        title: '第二位',
                        balls: ['大','小','单','双']
                    }]
                }
            }, {
                name: '第三位',
                code: 'dw3dxds',
                realname: '[大小单双_第三位]',
                tips: '选择大小单双。',
                example: '投注方案：大；开奖号码第三位是06、07、08、09、10即为中奖。',
                help: '大（06、07、08、09、10）；小（01、02、03、04、05）；单（01、03、05、07、09）；双（02、04、06、08、10）；',
                select: {
                    layout: [{
                        title: '第三位',
                        balls: ['大','小','单','双']
                    }]
                }
            }, {
                name: '第四位',
                code: 'dw4dxds',
                realname: '[大小单双_第四位]',
                tips: '选择大小单双。',
                example: '投注方案：大；开奖号码第四位是06、07、08、09、10即为中奖。',
                help: '大（06、07、08、09、10）；小（01、02、03、04、05）；单（01、03、05、07、09）；双（02、04、06、08、10）；',
                select: {
                    layout: [{
                        title: '第四位',
                        balls: ['大','小','单','双']
                    }]
                }
            }, {
                name: '第五位',
                code: 'dw5dxds',
                realname: '[大小单双_第五位]',
                tips: '选择大小单双。',
                example: '投注方案：大；开奖号码第五位是06、07、08、09、10即为中奖。',
                help: '大（06、07、08、09、10）；小（01、02、03、04、05）；单（01、03、05、07、09）；双（02、04、06、08、10）；',
                select: {
                    layout: [{
                        title: '第五位',
                        balls: ['大','小','单','双']
                    }]
                }
            }, {
                name: '冠亚和值',
                code: 'pk10_dxdsgyhz',
                realname: '[大小单双_冠亚和值]',
                tips: '选择大小单双。',
                example: '投注方案：大；开奖号码第一、二位和值是12、13、14、15、16、17、18、19即为中奖。',
                help: '大（12、13、14、15、16、17、18、19）；小（3、4、5、6、7、8、9、10、11）；单（3、5、7、9、11、13、15、17、19）；双（4、6、8、10、12、14、16、18）；',
                select: {
                    layout: [{
                        title: '冠亚和',
                        balls: ['大','小','单','双']
                    }]
                },
                prizeIndexes:'selectedIndex'
            }]
        }]]
    }, {
        title: '和值',
        code: 'pk10hz',
        rows: [[{
            title: '和值',
            columns: [{
                name: '冠亚和值',
                code: 'pk10_hzgyhz',
                realname: '[和值_冠亚和值]',
                tips: '从3-19中任意选择1个或1个以上号码。',
                example: '投注方案：和值3；开奖号码第一、二位和值是3,即为中奖。',
                help: '所选数值等于开奖号码的第一、二位数值相加之和，即为中奖。',
                select: {
                    layout: [{
                        balls: [3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19],
                        tools: false,
                        cls: 'hz'
                    }]
                },
                prizeIndexes:'selectedIndex'
            },{
                name: '前三和值',
                code: 'pk10_hzqshz',
                realname: '[和值_前三和值]',
                tips: '从6-27中任意选择1个或1个以上号码。',
                example: '投注方案：和值6；开奖号码第一、二、三位和值是6,即为中奖。',
                help: '所选数值等于开奖号码的第一、二位、三位数值相加之和，即为中奖。',
                select: {
                    layout: [{
                        balls: [6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27],
                        tools: false,
                        cls: 'hz'
                    }]
                },
                prizeIndexes:'selectedIndex'
            }]
        }]]
    }, {
        title: '龙虎',
        code: 'pk10lh',
        rows: [[{
            title: '龙虎',
            columns: [{
                name: '01 VS 10',
                code: '01vs10',
                realname: '[龙虎_01vs10]',
                tips: '选择龙或虎。',
                example: '投注方案：龙；开奖号码第一位比第十位大，开奖结果即为龙，反之则为虎。',
                help: '选择龙或虎，只要开奖号码符合龙虎规则、即为中奖。',
                select: {
                    layout: [{
                        title: '01VS10',
                        balls: ['龙','虎']
                    }]
                }
            }, {
                name: '02 VS 09',
                code: '02vs09',
                realname: '[龙虎_02vs09]',
                tips: '选择龙或虎。',
                example: '投注方案：龙；开奖号码第二位比第九位大，开奖结果即为龙，反之则为虎。',
                help: '选择龙或虎，只要开奖号码符合龙虎规则、即为中奖。',
                select: {
                    layout: [{
                        title: '02VS09',
                        balls: ['龙','虎']
                    }]
                }
            }, {
                name: '03 VS 08',
                code: '03vs08',
                realname: '[龙虎_03vs08]',
                tips: '选择龙或虎。',
                example: '投注方案：龙；开奖号码第三位比第八位大，开奖结果即为龙，反之则为虎。',
                help: '选择龙或虎，只要开奖号码符合龙虎规则、即为中奖。',
                select: {
                    layout: [{
                        title: '03VS08',
                        balls: ['龙','虎']
                    }]
                }
            }, {
                name: '04 VS 07',
                code: '04vs07',
                realname: '[龙虎_04vs07]',
                tips: '选择龙或虎。',
                example: '投注方案：龙；开奖号码第四位比第七位大，开奖结果即为龙，反之则为虎。',
                help: '选择龙或虎，只要开奖号码符合龙虎规则、即为中奖。',
                select: {
                    layout: [{
                        title: '04VS07',
                        balls: ['龙','虎']
                    }]
                }
            }, {
                name: '05 VS 06',
                code: '05vs06',
                realname: '[龙虎_05vs06]',
                tips: '选择龙或虎。',
                example: '投注方案：龙；开奖号码第五位比第六位大，开奖结果即为龙，反之则为虎。',
                help: '选择龙或虎，只要开奖号码符合龙虎规则、即为中奖。',
                select: {
                    layout: [{
                        title: '05VS06',
                        balls: ['龙','虎']
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
        var checkIndex = groupLength > 5 ? 5 : 0;
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