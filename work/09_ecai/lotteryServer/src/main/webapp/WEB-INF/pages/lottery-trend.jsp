<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="zh-CN" >
<head>
    <%@include file="file/cdn.jsp"%>
	<title>${Lottery.showName}走势图</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <script type="text/javascript" src="<%=cdnDomain%>static/plugins/jquery/jquery.min.js"></script>
    <script src="<%=cdnDomain%>static/plugins/core.js"></script>
    <script type="text/javascript">
        var Lottery = ${Lottery}
    </script>
	<script type="text/javascript" src="<%=cdnDomain%>static/js/lottery.trend.js?v=${cdnVersion}"></script>
    <link rel="stylesheet" type="text/css" href="<%=cdnDomain%>static/css/lottery.trend.css?v=${cdnVersion}">
</head>
<body>
    <div class="search">
        <div class="search-title">
            <span class="name">彩种：${Lottery.showName}</span>
            <span class="collapse link-btn" data-command="collapse">展开功能区</span>
            <input type="hidden" name="lotteryId" value="${Lottery.id}">
        </div>
        <div class="search-condition">
            <div class="title"><span id="defaultRuleName"></span></div>
            <div class="function">
                <label class="label"><input data-command="help-line" type="checkbox" class="checkbox" checked/>辅助线</label>
                <label class="label"><input data-command="lost" type="checkbox" class="checkbox" checked/>遗漏</label>
                <label class="label"><input data-command="lost-line" type="checkbox" class="checkbox"/>遗漏条</label>
                <label class="label"><input data-command="trend" type="checkbox" class="checkbox" checked/>走势</label>
                <%--<label class="label"><input data-command="temperature" type="checkbox" class="checkbox"/>号温</label>--%>
            </div>
            <div class="expect">
                <span class="loading">Loading...</span>
                <span class="expect-btn link-btn" data-command="showTrend" data-val="latest-30-desc">近30期</span>
                <span class="expect-btn link-btn" data-command="showTrend" data-val="latest-50-desc">近50期</span>
            </div>
        </div>
    </div>
    <div class="table-responsive">
        <table class="trend-table">
            <thead></thead>
            <tbody></tbody>
            <tfoot></tfoot>
        </table>
    </div>
    <div style="height: 100px;"></div>
    <canvas id="canvas"></canvas>
    <script type="text/template" id="thead_tpl">
        <tr>
            <th rowspan="2" class="border-bottom border-right">期号</td>
            <th rowspan="2" class="border-bottom border-right">开奖号码</th>
            <# for (var i = 0; i < title.length; i++) { #>
                <th colspan="<#=codes.length#>" class="border-right"><#=title[i]#></th>
            <# } #>
            <# if (showDistribution && showDistribution == true){ #>
                <th colspan="<#=codes.length#>" class="border-right">号码分布</th>
            <# } #>
        </tr>
        <tr>
            <# for (var i = 0; i < title.length; i++) { #>
                <# for (var k = 0; k < codes.length; k++) { #>
                    <# if (k == codes.length-1) { #>
                        <td class="border-bottom border-right"><i class="ball ball-normal"><#=codes[k]#></i></td>
                    <# } else { #>
                        <td class="border-bottom"><i class="ball ball-normal"><#=codes[k]#></i></td>
                    <# } #>
                <# } #>
            <# } #>
            <# if (showDistribution && showDistribution == true){ #>
                <# for (var k = 0; k < codes.length; k++) { #>
                    <# if (k == codes.length-1) { #>
                        <td class="border-bottom border-right"><i class="ball ball-normal"><#=codes[k]#></i></td>
                    <# } else { #>
                        <td class="border-bottom"><i class="ball ball-normal"><#=codes[k]#></i></td>
                    <# } #>
                <# } #>
            <# } #>
        </tr>
    </script>
    <script type="text/template" id="tbody_tpl">
        <# for (var i = 0; i < list.length; i++) { #>
        <# var opencodes = list[i].code.split(',')#>--%>
        <tr>
            <# if ((i+1) % 5 == 0) { #>
                <td class="border-bottom border-right help-line"><#=list[i].expect#></td>
                <td class="border-bottom border-right help-line">
                    <# if (separator && separator == true) { #>
                        <#=list[i].code#>
                    <# } else { #>
                        <#=list[i].code.replace(/,/g, '')#>
                    <# } #>
                </td>
                <# for (var j = 0; j < title.length; j++) { #>
                    <# for (var k = 0; k < codes.length; k++) { #>

                        <# var lostLineTdClass = codeLostLine[j][k][i] ? 'lost-line-td' : '';#>

                        <# if (k == codes.length-1) { #>
                            <td class="border-bottom border-right code-td help-line <#=lostLineTdClass#>">
                                <# if (opencodes[j] == codes[k]) { #>
                                    <cite class="ball ball-cite"><#=codes[k]#></cite>
                                <# } else { #>
                                    <i class="ball ball-gray lost-code"><#=codeLost[i][j][k]#></i>
                                <# } #>
                            </td>
                        <# } else { #>
                            <td class="border-bottom code-td help-line <#=lostLineTdClass#>">
                                <# if (opencodes[j] == codes[k]) { #>
                                    <cite class="ball ball-cite"><#=codes[k]#></cite>
                                <# } else { #>
                                    <i class="ball ball-gray lost-code"><#=codeLost[i][j][k]#></i>
                                <# } #>
                            </td>
                        <# } #>
                    <# } #>
                <# } #>
                <# if (showDistribution && showDistribution == true){ #>
                    <# for (var k = 0; k < codes.length; k++) { #>
                        <# if (k == codes.length-1) { #>

                            <# var codeDistribution = null; #>
                            <# for (var m = 0; m < distribution[i].length; m++) { #>
                                <# if (distribution[i][m].code == codes[k]) { #>
                                    <# codeDistribution = distribution[i][m]; #>
                                <# } #>
                            <# }#>

                            <# if (codeDistribution) { #>
                                <# if (codeDistribution.special == true) { #>
                                    <td class="border-bottom border-right help-line"><i class="ball ball-purple"><#=codeDistribution.code#></i></td>
                                <# } else { #>
                                    <td class="border-bottom border-right help-line"><i class="ball ball-green"><#=codeDistribution.code#></i></td>
                                <# } #>
                            <# } else { #>
                                <td class="border-bottom border-right help-line"><i class="ball ball-gray lost-code"><#=distributionLost[i][k]#></i></td>
                            <# } #>
                        <# } else { #>
                            <# var codeDistribution = null; #>
                            <# for (var m = 0; m < distribution[i].length; m++) { #>
                                <# if (distribution[i][m].code == codes[k]) { #>
                                    <# codeDistribution = distribution[i][m]; #>
                                <# } #>
                            <# }#>

                            <# if (codeDistribution != null) { #>
                                <# if (codeDistribution.special == true) { #>
                                    <td class="border-bottom help-line"><i class="ball ball-purple"><#=codeDistribution.code#></i></td>
                                <# } else { #>
                                    <td class="border-bottom help-line"><i class="ball ball-green"><#=codeDistribution.code#></i></td>
                                <# } #>
                            <# } else { #>
                                <td class="border-bottom help-line"><i class="ball ball-gray lost-code"><#=distributionLost[i][k]#></i></td>
                            <# } #>
                        <# } #>
                    <# } #>
                <# } #>
            <# } else { #>
                <td class="border-right"><#=list[i].expect#></td>
                <td class="border-right">
                    <# if (separator && separator == true) { #>
                        <#=list[i].code#>
                    <# } else { #>
                        <#=list[i].code.replace(/,/g, '')#>
                    <# } #>
                </td>
                <# for (var j = 0; j < title.length; j++) { #>
                    <# for (var k = 0; k < codes.length; k++) { #>

                        <# var lostLineTdClass = codeLostLine[j][k][i] ? 'lost-line-td' : '';#>

                        <# if (k == codes.length-1) { #>
                            <td class="border-right code-td <#=lostLineTdClass#>">
                                <# if (opencodes[j] == codes[k]) { #>
                                    <cite class="ball ball-cite"><#=codes[k]#></cite>
                                <# } else { #>
                                    <i class="ball ball-gray lost-code"><#=codeLost[i][j][k]#></i>
                                <# } #>
                            </td>
                        <# } else { #>
                            <td class="code-td <#=lostLineTdClass#>">
                                <# if (opencodes[j] == codes[k]) { #>
                                    <cite class="ball ball-cite"><#=codes[k]#></cite>
                                <# } else { #>
                                    <i class="ball ball-gray lost-code"><#=codeLost[i][j][k]#></i>
                                <# } #>
                            </td>
                        <# } #>
                    <# } #>
                <# } #>
                <# if (showDistribution && showDistribution == true){ #>
                    <# for (var k = 0; k < codes.length; k++) { #>
                        <# if (k == codes.length-1) { #>

                            <# var codeDistribution = null; #>
                            <# for (var m = 0; m < distribution[i].length; m++) { #>
                                <# if (distribution[i][m].code == codes[k]) { #>
                                    <# codeDistribution = distribution[i][m]; #>
                                <# } #>
                            <# }#>

                            <# if (codeDistribution) { #>
                                <# if (codeDistribution.special == true) { #>
                                    <td class="border-right"><i class="ball ball-purple"><#=codeDistribution.code#></i></td>
                                <# } else { #>
                                    <td class="border-right"><i class="ball ball-green"><#=codeDistribution.code#></i></td>
                                <# } #>
                            <# } else { #>
                                <td class="border-right"><i class="ball ball-gray lost-code"><#=distributionLost[i][k]#></i></td>
                            <# } #>
                        <# } else { #>
                            <# var codeDistribution = null; #>
                            <# for (var m = 0; m < distribution[i].length; m++) { #>
                                <# if (distribution[i][m].code == codes[k]) { #>
                                    <# codeDistribution = distribution[i][m]; #>
                                <# } #>
                            <# }#>

                            <# if (codeDistribution != null) { #>
                                <# if (codeDistribution.special == true) { #>
                                    <td><i class="ball ball-purple"><#=codeDistribution.code#></i></td>
                                <# } else { #>
                                    <td><i class="ball ball-green"><#=codeDistribution.code#></i></td>
                                <# } #>
                            <# } else { #>
                                <td><i class="ball ball-gray lost-code"><#=distributionLost[i][k]#></i></td>
                            <# } #>
                        <# } #>
                    <# } #>
                <# } #>
            <# } #>
        </tr>
        <# } #>
    </script>
    <script type="text/template" id="tfoot_tpl">
        <tr>
            <td class="border-top border-right">当前出现次数</td>
            <td class="border-top border-right"></td>
            <# for (var i = 0; i < title.length; i++) { #>
                <# for (var j = 0; j < codeTimes[i].length; j++) { #>
                    <# if (j == codeTimes[i].length-1) { #>
                        <td class="border-top border-right"><#=codeTimes[i][j]#></td>
                    <# } else { #>
                        <td class="border-top"><#=codeTimes[i][j]#></td>
                    <# } #>
                <# } #>
            <# } #>
            <# if (showDistribution && showDistribution == true){ #>
                <# for (var i = 0; i < distributionCodeTimes.length; i++) { #>
                    <# if (i == distributionCodeTimes.length-1) { #>
                        <td class="border-top border-right"><#=distributionCodeTimes[i]#></td>
                    <# } else { #>
                        <td class="border-top"><#=distributionCodeTimes[i]#></td>
                    <# } #>
                <# } #>
            <# } #>
        </tr>
        <tr>
            <td class="border-top border-right">当前最大遗漏</td>
            <td class="border-top border-right"></td>
            <# for (var i = 0; i < title.length; i++) { #>
                <# for (var j = 0; j < maxLost[i].length; j++) { #>
                    <# if (j == maxLost[i].length-1) { #>
                        <td class="border-top border-right"><#=maxLost[i][j]#></td>
                    <# } else { #>
                        <td class="border-top"><#=maxLost[i][j]#></td>
                    <# } #>
                <# } #>
            <# } #>
            <# if (showDistribution && showDistribution == true){ #>
                <# for (var i = 0; i < distributionMaxLost.length; i++) { #>
                    <# if (i == distributionMaxLost.length-1) { #>
                        <td class="border-top border-right"><#=distributionMaxLost[i]#></td>
                    <# } else { #>
                        <td class="border-top"><#=distributionMaxLost[i]#></td>
                    <# } #>
                <# } #>
            <# } #>
        </tr>
        <tr>
            <td class="border-top border-right">当前最大连出</td>
            <td class="border-top border-right"></td>
            <# for (var i = 0; i < title.length; i++) { #>
                <# for (var j = 0; j < maxHit[i].length; j++) { #>
                    <# if (j == maxHit[i].length-1) { #>
                        <td class="border-bottom border-top border-right"><#=maxHit[i][j]#></td>
                    <# } else { #>
                        <td class="border-bottom border-top"><#=maxHit[i][j]#></td>
                    <# } #>
                <# } #>
            <# } #>
            <# if (showDistribution && showDistribution == true){ #>
                <# for (var i = 0; i < distributionMaxHit.length; i++) { #>
                    <# if (i == distributionMaxHit.length-1) { #>
                        <td class="border-bottom border-top border-right"><#=distributionMaxHit[i]#></td>
                    <# } else { #>
                        <td class="border-bottom border-top"><#=distributionMaxHit[i]#></td>
                    <# } #>
                <# } #>
            <# } #>
        </tr>
        <tr>
            <td rowspan="2" class="border-top border-right">期号</td>
            <td rowspan="2" class="border-top border-right">开奖号码</td>
            <# for (var i = 0; i < title.length; i++) { #>
                <# for (var k = 0; k < codes.length; k++) { #>
                    <# if (k == codes.length-1) { #>
                        <td class="border-bottom border-right"><#=codes[k]#></td>
                    <# } else { #>
                        <td class="border-bottom"><#=codes[k]#></td>
                    <# } #>
                <# } #>
            <# } #>
            <# if (showDistribution && showDistribution == true){ #>
                <# for (var k = 0; k < codes.length; k++) { #>
                    <# if (k == codes.length-1) { #>
                        <td class="border-bottom border-right"><#=codes[k]#></td>
                    <# } else { #>
                        <td class="border-bottom"><#=codes[k]#></td>
                    <# } #>
                <# } #>
            <# } #>
        </tr>
        <tr>
            <# for (var i = 0; i < title.length; i++) { #>
                <td colspan="<#=codes.length#>" class="border-top border-right"><#=title[i]#></td>
            <# } #>
            <# if (showDistribution && showDistribution == true){ #>
                <td colspan="<#=codes.length#>" class="border-top border-right">号码分布</td>
            <# } #>
        </tr>
    </script>
</body>
</html>