package lottery.domains.pool.play.impl;



import lottery.domains.pool.play.ITicketPlayHandler;
import lottery.domains.pool.play.WinResult;
import lottery.domains.pool.util.TicketPlayUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 时时彩 任选 直选 单式 玩法处理类
 *
 */
public class SSCRXZHDSPlayHandler implements ITicketPlayHandler{
	private String playId;
	/** 任选号码数量 */
	private int rxNum;

	public SSCRXZHDSPlayHandler(String playId,int rxNum) {
		this.playId= playId;
		this.rxNum = rxNum;
		schedulerClearCache();
	}

	private static ConcurrentHashMap<Integer, Object[]> numCache = new ConcurrentHashMap<>();
	public static void schedulerClearCache() {
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				synchronized (numCache) {
					numCache.clear();
				}
			}
		};
		Timer timer = new Timer();
		timer.schedule(task, 1000, 1000 * 60 * 30); // 每30分钟清理一次缓存
	}

	@Override
	public String[] getBetNums(String bet) {
		return null;
	}

	private Object[] resolveBetCodes(String betCodes) {
		Object [] res = new Object [2];
		String []xz = betCodes.substring(1,10).split(",");
		StringBuffer placeIndex = new StringBuffer();
		for (int i = 0; i < xz.length; i++) {
			if(xz[i].equals("√")){
				placeIndex.append(String.valueOf(i)).append(",");
			}
		}
		String placeVal = placeIndex.toString().trim();
		if(!placeVal.equals("")){
			placeVal = placeVal.substring(0,placeVal.length()-1);
		}

		String betValue = betCodes.substring(11,betCodes.length()).trim();

		TreeSet<String> filter = new TreeSet<>();
		String[] split = betValue.split(" ");
		for (int i = 0; i < split.length; i++) {
			if (split[i] != null && split[i].trim().length() > 0) {
				filter.add(split[i]);
			}
		}
		String[] nums = filter.toArray(new String[] {});
		Arrays.sort(nums);

		res[0] = placeVal.trim().split(",");
		res[1] = nums;
		return res;
	}

	@Override
	public String[] getOpenNums(String openNums) {
		return TicketPlayUtils.getOpenNums(openNums, OFFSETS_WUXIN);
	}

	private Object[] resolveDataFromCache(int userBetsId, String betCodes) {
		Object[] data = null;
		boolean hit = false;
		try {
			if (numCache.containsKey(userBetsId)) {
				synchronized (numCache) {
					data = numCache.get(userBetsId);
					hit = true;
				}
			}
			else {
				data = resolveBetCodes(betCodes);
			}
		} catch (Exception e) {
			e.printStackTrace();
			numCache.remove(userBetsId);
		}

		if (data == null) {
			data = resolveBetCodes(betCodes);
			hit = false;
		}

		if (data == null || data.length != 2) {
			return null;
		}

		if (!hit) {
			String[] offsets = ((String[])data[0]);
			String[] betNums = ((String[])data[1]);

			data = new Object[]{betNums, offsets};
			synchronized (numCache) {
				numCache.put(userBetsId, data);
			}
		}

		return data;
	}

	@Override
	public WinResult calculateWinNum(int userBetsId, String betCodes, String openCodes) {
		WinResult result = new WinResult();

		Object[] data = resolveDataFromCache(userBetsId, betCodes);
		if (data == null) {
			return result;
		}

		String[] betNums = ((String[])data[0]);
		String[] offsets = ((String[])data[1]);

		switch (rxNum) {
			case 2: // 二码
				for (int i = 0; i < offsets.length - 1; i++) {
					for (int j = i + 1; j < offsets.length; j++) {
						String[] offsetOpenNums = TicketPlayUtils.getOpenNums(openCodes, new int[]{Integer.parseInt(offsets[i]), Integer.parseInt(offsets[j])});

						String num = new StringBuilder().append(offsetOpenNums[0]).append(offsetOpenNums[1]).toString();
						if (Arrays.binarySearch(betNums, num) >= 0) {
							result.setWinNum(result.getWinNum()+1);
						}
					}
				}
				break;
			case 3: // 三码
				for (int i = 0; i < offsets.length - 1; i++) {
					for (int j = i + 1; j < offsets.length; j++) {
						for (int k = j + 1; k < offsets.length; k++) {
							String[] offsetOpenNums = TicketPlayUtils.getOpenNums(openCodes, new int[]{Integer.parseInt(offsets[i]), Integer.parseInt(offsets[j]), Integer.parseInt(offsets[k])});

							String num = new StringBuilder().append(offsetOpenNums[0]).append(offsetOpenNums[1]).append(offsetOpenNums[2]).toString();
							if (Arrays.binarySearch(betNums, num) >= 0) {
								result.setWinNum(result.getWinNum()+1);
							}
						}
					}
				}
				break;
			case 4: // 四码
				for (int i = 0; i < offsets.length - 1; i++) {
					for (int j = i + 1; j < offsets.length; j++) {
						for (int k = j + 1; k < offsets.length; k++) {
							for (int m = k + 1; m < offsets.length; m++) {
								String[] offsetOpenNums = TicketPlayUtils.getOpenNums(openCodes, new int[]{Integer.parseInt(offsets[i]), Integer.parseInt(offsets[j]), Integer.parseInt(offsets[k]), Integer.parseInt(offsets[m])});

								String num = new StringBuilder().append(offsetOpenNums[0]).append(offsetOpenNums[1]).append(offsetOpenNums[2]).append(offsetOpenNums[3]).toString();
								if (Arrays.binarySearch(betNums, num) >= 0) {
									result.setWinNum(result.getWinNum()+1);
								}
							}
						}
					}
				}
				break;
			default:
				break;
		}

		result.setPlayId(playId);
		return result;
	}

	public static void main(String[] args) {
		// // 任二直选单式
		// String betCodes = "[-,-,√,√,√]08 09 12 30 35 37 48 51 53 57 61 62 63 65 69 71 73 75 82 96 99";
		// String openCodes = "7,5,6,9,9";
        //
		// long start = System.currentTimeMillis();
		// SSCRXZHDSPlayHandler handler = new SSCRXZHDSPlayHandler("az5fc_rx2ds", 2);
        //
		// // 测试性能
		// for (int i = 0; i < 100000; i++) {
		// 	WinResult winResult = handler.calculateWinNum(1, betCodes, openCodes);
		// }
        //
		// // 测试准确性
		// WinResult winResult = handler.calculateWinNum(1, betCodes, openCodes);
		// System.out.println(winResult.getWinNum());
        //
		// long spent = System.currentTimeMillis() - start;
		// System.out.println("耗时：" + spent);
        //
		// System.exit(0);

		// // 任三直选单式
		// String betCodes = "[-,√,√,√,√]013 014 017 018 023 024 027 028 031 032 034 035 041 042 043 045 046 053 054 057 064 067 068 071 072 075 076 078 079 081 082 086 087 089 097 098 103 104 107 108 113 123 124 127 128 130 131 132 133 134 135 136 137 138 139 140 142 143 145 146 153 154 157 163 164 167 168 170 172 173 175 176 178 179 180 182 183 186 187 189 193 197 198 203 204 207 208 213 214 217 218 223 224 230 231 232 233 234 235 236 237 238 239 240 241 242 243 244 245 246 247 248 249 253 254 257 263 264 267 268 270 271 273 274 275 276 278 279 280 281 283 284 286 287 289 293 294 297 298 301 302 304 305 310 311 312 313 314 315 316 317 318 319 320 321 322 323 324 325 326 327 328 329 331 332 335 340 341 342 345 350 351 352 353 354 355 356 357 358 359 361 362 365 371 372 375 381 382 385 391 392 395 401 402 403 405 406 410 412 413 415 416 420 421 422 423 424 425 426 427 428 429 430 431 432 435 442 445 450 451 452 453 454 455 456 457 458 459 460 461 462 465 472 475 482 485 492 495 503 504 507 513 514 517 523 524 527 530 531 532 533 534 535 536 537 538 539 540 541 542 543 544 545 546 547 548 549 553 554 557 563 564 567 568 570 571 572 573 574 575 576 577 578 579 583 584 586 587 589 593 594 597 598 604 607 608 613 614 617 618 623 624 627 628 631 632 635 640 641 642 645 653 654 657 658 670 671 672 675 680 681 682 685 701 702 705 706 708 709 710 712 713 715 716 718 719 720 721 723 724 725 726 728 729 731 732 735 742 745 750 751 752 753 754 755 756 757 758 759 760 761 762 765 775 780 781 782 785 790 791 792 795 801 802 806 807 809 810 812 813 816 817 819 820 821 823 824 826 827 829 831 832 835 842 845 853 854 856 857 859 860 861 862 865 870 871 872 875 890 891 892 895 907 908 913 917 918 923 924 927 928 931 932 935 942 945 953 954 957 958 970 971 972 975 980 981 982 985";
		// String openCodes = "0,6,1,7,4";
        //
		// long start = System.currentTimeMillis();
		// SSCRXZHDSPlayHandler handler = new SSCRXZHDSPlayHandler("az5fc_rx3ds", 3);
        //
		// // 测试性能
		// for (int i = 0; i < 100000; i++) {
		// 	WinResult winResult = handler.calculateWinNum(1, betCodes, openCodes);
		// }
        //
		// // 测试准确性
		// WinResult winResult = handler.calculateWinNum(1, betCodes, openCodes);
		// System.out.println(winResult.getWinNum());
        //
		// long spent = System.currentTimeMillis() - start;
		// System.out.println("耗时：" + spent);
        //
		// System.exit(0);

		// 任选四单式
		String betCodes = "[√,√,-,√,√]0041 0051 0060 0061 0062 0063 0064 0065 0071 0076 0081 0086 0091 0096 0104 0105 0106 0107 0108 0109 0140 0141 0142 0143 0150 0151 0152 0153 0154 0160 0161 0162 0163 0164 0165 0170 0171 0172 0173 0174 0175 0176 0180 0181 0182 0183 0184 0185 0186 0187 0190 0191 0193 0194 0195 0196 0197 0206 0214 0215 0216 0217 0218 0241 0251 0260 0261 0262 0263 0264 0265 0271 0276 0281 0286 0296 0306 0314 0315 0316 0317 0318 0319 0326 0341 0351 0360 0361 0362 0363 0364 0365 0371 0376 0381 0386 0391 0396 0401 0406 0410 0411 0412 0413 0414 0415 0416 0417 0418 0419 0421 0426 0431 0436 0441 0451 0460 0461 0462 0463 0464 0465 0471 0476 0481 0486 0491 0496 0501 0506 0510 0511 0512 0513 0514 0515 0516 0517 0518 0519 0521 0526 0531 0536 0541 0546 0551 0560 0561 0562 0563 0564 0565 0571 0576 0581 0586 0591 0596 0600 0601 0602 0603 0604 0605 0606 0607 0608 0609 0610 0611 0612 0613 0614 0615 0616 0617 0618 0619 0620 0621 0622 0623 0624 0625 0626 0627 0628 0629 0630 0631 0632 0633 0634 0635 0636 0637 0638 0639 0640 0641 0642 0643 0644 0645 0646 0647 0648 0649 0650 0651 0652 0653 0654 0655 0656 0657 0658 0659 0660 0661 0662 0663 0664 0665 0670 0671 0672 0673 0674 0675 0676 0680 0681 0682 0683 0684 0685 0686 0687 0690 0691 0692 0693 0694 0695 0696 0698 0701 0706 0710 0711 0712 0713 0714 0715 0716 0717 0718 0719 0721 0726 0731 0736 0741 0746 0751 0756 0760 0761 0762 0763 0764 0765 0766 0767 0768 0771 0776 0781 0786 0791 0801 0806 0810 0811 0812 0813 0814 0815 0816 0817 0818 0821 0826 0831 0836 0841 0846 0851 0856 0860 0861 0862 0863 0864 0865 0866 0867 0869 0871 0876 0881 0896 0901 0906 0910 0911 0913 0914 0915 0916 0917 0919 0926 0931 0936 0941 0946 0951 0956 0960 0961 0962 0963 0964 0965 0966 0968 0969 0971 0986 0991 0996 1004 1005 1006 1007 1008 1009 1014 1015 1016 1017 1018 1019 1024 1025 1026 1027 1028 1034 1035 1036 1037 1038 1039 1040 1041 1042 1043 1044 1045 1046 1047 1048 1049 1050 1051 1052 1053 1054 1055 1056 1057 1058 1059 1060 1061 1062 1063 1064 1065 1066 1067 1068 1069 1070 1071 1072 1073 1074 1075 1076 1077 1078 1079 1080 1081 1082 1083 1084 1085 1086 1087 1088 1090 1091 1093 1094 1095 1096 1097 1099 1104 1105 1106 1107 1108 1109 1140 1141 1142 1143 1150 1151 1152 1153 1154 1160 1161 1162 1163 1164 1165 1170 1171 1172 1173 1174 1175 1176 1180 1181 1182 1183 1184 1185 1186 1187 1190 1191 1192 1193 1194 1195 1196 1197 1198 1204 1205 1206 1207 1208 1214 1215 1216 1217 1218 1219 1240 1241 1242 1250 1251 1252 1253 1254 1260 1261 1262 1263 1264 1265 1270 1271 1272 1273 1274 1275 1276 1280 1281 1282 1283 1284 1285 1286 1287 1291 1292 1293 1294 1295 1296 1297 1298 1304 1305 1306 1307 1308 1309 1314 1315 1316 1317 1318 1319 1325 1326 1327 1328 1329 1340 1341 1343 1350 1351 1352 1353 1354 1360 1361 1362 1363 1364 1365 1370 1371 1372 1373 1374 1375 1376 1380 1381 1382 1383 1384 1385 1386 1387 1390 1391 1392 1393 1394 1395 1396 1397 1398 1400 1401 1402 1403 1404 1405 1406 1407 1408 1409 1410 1411 1412 1413 1414 1415 1416 1417 1418 1419 1420 1421 1422 1424 1425 1426 1427 1428 1429 1430 1431 1433 1434 1435 1436 1437 1438 1439 1440 1441 1442 1443 1450 1451 1452 1453 1454 1460 1461 1462 1463 1464 1465 1470 1471 1472 1473 1474 1475 1476 1480 1481 1482 1483 1484 1485 1486 1487 1490 1491 1492 1493 1494 1495 1496 1497 1500 1501 1502 1503 1504 1505 1506 1507 1508 1509 1510 1511 1512 1513 1514 1515 1516 1517 1518 1519 1520 1521 1522 1523 1524 1525 1526 1527 1528 1529 1530 1531 1532 1533 1534 1535 1536 1537 1538 1539 1540 1541 1542 1543 1544 1545 1546 1547 1548 1549 1550 1551 1552 1553 1554 1560 1561 1562 1563 1564 1565 1570 1571 1572 1573 1574 1575 1576 1580 1581 1582 1583 1584 1585 1586 1587 1590 1591 1592 1593 1594 1595 1596 1598 1600 1601 1602 1603 1604 1605 1606 1607 1608 1609 1610 1611 1612 1613 1614 1615 1616 1617 1618 1619 1620 1621 1622 1623 1624 1625 1626 1627 1628 1629 1630 1631 1632 1633 1634 1635 1636 1637 1638 1639 1640 1641 1642 1643 1644 1645 1646 1647 1648 1649 1650 1651 1652 1653 1654 1655 1656 1657 1658 1659 1660 1661 1662 1663 1664 1665 1670 1671 1672 1673 1674 1675 1676 1680 1681 1682 1683 1684 1685 1686 1690 1691 1692 1693 1694 1695 1697 1698 1700 1701 1702 1703 1704 1705 1706 1707 1708 1709 1710 1711 1712 1713 1714 1715 1716 1717 1718 1719 1720 1721 1722 1723 1724 1725 1726 1727 1728 1729 1730 1731 1732 1733 1734 1735 1736 1737 1738 1739 1740 1741 1742 1743 1744 1745 1746 1747 1748 1749 1750 1751 1752 1753 1754 1755 1756 1757 1758 1760 1761 1762 1763 1764 1765 1766 1767 1769 1770 1771 1772 1773 1774 1775 1776 1780 1781 1782 1783 1784 1785 1787 1790 1791 1792 1793 1794 1796 1797 1798 1800 1801 1802 1803 1804 1805 1806 1807 1808 1810 1811 1812 1813 1814 1815 1816 1817 1818 1819 1820 1821 1822 1823 1824 1825 1826 1827 1828 1829 1830 1831 1832 1833 1834 1835 1836 1837 1838 1839 1840 1841 1842 1843 1844 1845 1846 1847 1848 1850 1851 1852 1853 1854 1855 1856 1857 1859 1860 1861 1862 1863 1864 1865 1866 1868 1869 1870 1871 1872 1873 1874 1875 1877 1878 1879 1880 1881 1882 1883 1884 1886 1887 1891 1892 1893 1895 1896 1897 1898 1900 1901 1903 1904 1905 1906 1907 1909 1910 1911 1912 1913 1914 1915 1916 1917 1918 1919 1921 1922 1923 1924 1925 1926 1927 1928 1929 1930 1931 1932 1933 1934 1935 1936 1937 1938 1940 1941 1942 1943 1944 1945 1946 1947 1949 1950 1951 1952 1953 1954 1955 1956 1958 1959 1960 1961 1962 1963 1964 1965 1967 1968 1969 1970 1971 1972 1973 1974 1976 1977 1978 1979 1981 1982 1983 1985 1986 1987 1988 1989 1990 1991 1992 1994 1995 1996 1997 1998 2006 2014 2015 2016 2017 2018 2026 2036 2041 2046 2051 2056 2060 2061 2062 2063 2064 2065 2066 2067 2068 2069 2071 2076 2081 2086 2096 2104 2105 2106 2107 2108 2114 2115 2116 2117 2118 2119 2124 2125 2126 2127 2128 2129 2135 2136 2137 2138 2139 2140 2141 2142 2144 2145 2146 2147 2148 2149 2150 2151 2152 2153 2154 2155 2156 2157 2158 2159 2160 2161 2162 2163 2164 2165 2166 2167 2168 2169 2170 2171 2172 2173 2174 2175 2176 2177 2178 2179 2180 2181 2182 2183 2184 2185 2186 2187 2188 2189 2191 2192 2193 2194 2195 2196 2197 2198 2199 2206 2214 2215 2216 2217 2218 2219 2241 2251 2260 2261 2262 2263 2264 2265 2271 2276 2281 2286 2291 2296 2306 2315 2316 2317 2318 2319 2326 2351 2360 2361 2362 2363 2364 2365 2371 2376 2381 2386 2391 2396 2401 2406 2410 2411 2412 2414 2415 2416 2417 2418 2419 2421 2426 2436 2441 2451 2460 2461 2462 2463 2464 2465 2471 2476 2481 2486 2491 2496 2501 2506 2510 2511 2512 2513 2514 2515 2516 2517 2518 2519 2521 2526 2531 2536 2541 2546 2551 2560 2561 2562 2563 2564 2565 2571 2576 2581 2586 2591 2600 2601 2602 2603 2604 2605 2606 2607 2608 2609 2610 2611 2612 2613 2614 2615 2616 2617 2618 2619 2620 2621 2622 2623 2624 2625 2626 2627 2628 2629 2630 2631 2632 2633 2634 2635 2636 2637 2638 2639 2640 2641 2642 2643 2644 2645 2646 2647 2648 2649 2650 2651 2652 2653 2654 2655 2656 2657 2658 2660 2661 2662 2663 2664 2665 2670 2671 2672 2673 2674 2675 2676 2680 2681 2682 2683 2684 2685 2687 2690 2691 2692 2693 2694 2696 2697 2698 2701 2706 2710 2711 2712 2713 2714 2715 2716 2717 2718 2719 2721 2726 2731 2736 2741 2746 2751 2756 2760 2761 2762 2763 2764 2765 2766 2768 2769 2771 2781 2786 2791 2796 2801 2806 2810 2811 2812 2813 2814 2815 2816 2817 2818 2819 2821 2826 2831 2836 2841 2846 2851 2856 2860 2861 2862 2863 2864 2865 2867 2868 2869 2871 2876 2881 2886 2891 2896 2906 2911 2912 2913 2914 2915 2916 2917 2918 2919 2921 2926 2931 2936 2941 2946 2951 2960 2961 2962 2963 2964 2966 2967 2968 2969 2971 2976 2981 2986 2991 2996 3006 3014 3015 3016 3017 3018 3019 3026 3036 3041 3046 3051 3056 3060 3061 3062 3063 3064 3065 3066 3067 3068 3069 3071 3076 3081 3086 3091 3096 3104 3105 3106 3107 3108 3109 3114 3115 3116 3117 3118 3119 3125 3126 3127 3128 3129 3134 3135 3136 3137 3138 3139 3140 3141 3143 3144 3145 3146 3147 3148 3149 3150 3151 3152 3153 3154 3155 3156 3157 3158 3159 3160 3161 3162 3163 3164 3165 3166 3167 3168 3169 3170 3171 3172 3173 3174 3175 3176 3177 3178 3179 3180 3181 3182 3183 3184 3185 3186 3187 3188 3189 3190 3191 3192 3193 3194 3195 3196 3197 3198 3206 3215 3216 3217 3218 3219 3226 3236 3246 3251 3256 3260 3261 3262 3263 3264 3265 3266 3267 3268 3269 3271 3276 3281 3286 3291 3296 3306 3314 3315 3316 3317 3318 3319 3326 3341 3351 3360 3361 3362 3363 3364 3365 3371 3376 3381 3386 3391 3396 3401 3406 3410 3411 3413 3414 3415 3416 3417 3418 3419 3426 3431 3436 3441 3451 3460 3461 3462 3463 3464 3471 3476 3481 3486 3491 3501 3506 3510 3511 3512 3513 3514 3515 3516 3517 3518 3519 3521 3526 3531 3536 3541 3551 3560 3561 3562 3563 3565 3571 3576 3581 3591 3596 3600 3601 3602 3603 3604 3605 3606 3607 3608 3609 3610 3611 3612 3613 3614 3615 3616 3617 3618 3619 3620 3621 3622 3623 3624 3625 3626 3627 3628 3629 3630 3631 3632 3633 3634 3635 3636 3637 3638 3639 3640 3641 3642 3643 3644 3646 3647 3648 3650 3651 3652 3653 3655 3656 3657 3659 3660 3661 3662 3663 3664 3665 3670 3671 3672 3673 3674 3675 3680 3681 3682 3683 3684 3686 3687 3690 3691 3692 3693 3695 3696 3697 3698 3701 3706 3710 3711 3712 3713 3714 3715 3716 3717 3718 3719 3721 3726 3731 3736 3741 3746 3751 3756 3760 3761 3762 3763 3764 3765 3767 3768 3769 3771 3776 3781 3786 3791 3796 3801 3806 3810 3811 3812 3813 3814 3815 3816 3817 3818 3819 3821 3826 3831 3836 3841 3846 3851 3860 3861 3862 3863 3864 3866 3867 3868 3869 3871 3876 3881 3886 3891 3896 3901 3906 3910 3911 3912 3913 3914 3915 3916 3917 3918 3921 3926 3931 3936 3941 3951 3956 3960 3961 3962 3963 3965 3966 3967 3968 3969 3971 3976 3981 3986 3996 4001 4006 4010 4011 4012 4013 4014 4015 4016 4017 4018 4019 4021 4026 4031 4036 4041 4046 4051 4056 4060 4061 4062 4063 4064 4065 4066 4067 4068 4069 4071 4076 4081 4086 4091 4096 4101 4102 4103 4104 4105 4106 4107 4108 4109 4112 4113 4114 4115 4116 4117 4118 4119 4120 4121 4122 4124 4125 4126 4127 4128 4129 4130 4131 4133 4134 4135 4136 4137 4138 4139 4140 4141 4142 4143 4144 4145 4146 4147 4148 4149 4150 4151 4152 4153 4154 4155 4156 4157 4158 4159 4160 4161 4162 4163 4164 4165 4166 4167 4168 4169 4170 4171 4172 4173 4174 4175 4176 4177 4178 4179 4180 4181 4182 4183 4184 4185 4186 4187 4188 4190 4191 4192 4193 4194 4195 4196 4197 4199 4201 4206 4212 4214 4215 4216 4217 4218 4219 4226 4236 4241 4246 4251 4256 4260 4261 4262 4263 4264 4265 4266 4267 4268 4269 4271 4276 4281 4286 4291 4296 4301 4306 4313 4314 4315 4316 4317 4318 4319 4326 4336 4341 4346 4351 4360 4361 4362 4363 4364 4366 4367 4368 4371 4376 4381 4386 4391 4401 4406 4412 4413 4414 4415 4416 4417 4418 4419 4426 4436 4451 4460 4461 4462 4463 4464 4465 4471 4476 4481 4491 4496 4501 4506 4510 4511 4512 4513 4514 4515 4516 4517 4518 4519 4521 4526 4531 4541 4546 4551 4560 4561 4562 4564 4565 4571 4581 4586 4591 4596 4600 4601 4602 4603 4604 4605 4606 4607 4608 4609 4610 4611 4612 4613 4614 4615 4616 4617 4618 4619 4620 4621 4622 4623 4624 4625 4626 4627 4628 4629 4630 4631 4632 4633 4634 4636 4637 4638 4640 4641 4642 4643 4644 4645 4646 4647 4649 4650 4651 4652 4654 4655 4656 4658 4659 4660 4661 4662 4663 4664 4665 4670 4671 4672 4673 4674 4676 4680 4681 4682 4683 4685 4686 4687 4690 4691 4692 4694 4695 4696 4697 4698 4701 4706 4710 4711 4712 4713 4714 4715 4716 4717 4718 4719 4721 4726 4731 4736 4741 4746 4751 4760 4761 4762 4763 4764 4766 4767 4768 4769 4771 4776 4781 4786 4791 4796 4801 4806 4810 4811 4812 4813 4814 4815 4816 4817 4818 4821 4826 4831 4836 4841 4851 4856 4860 4861 4862 4863 4865 4866 4867 4868 4869 4871 4876 4881 4886 4896 4901 4906 4910 4911 4912 4913 4914 4915 4916 4917 4919 4921 4926 4931 4941 4946 4951 4956 4960 4961 4962 4964 4965 4966 4967 4968 4969 4971 4976 4986 4991 4996 5001 5006 5010 5011 5012 5013 5014 5015 5016 5017 5018 5019 5021 5026 5031 5036 5041 5046 5051 5056 5060 5061 5062 5063 5064 5065 5066 5067 5068 5069 5071 5076 5081 5086 5091 5096 5101 5102 5103 5104 5105 5106 5107 5108 5109 5112 5113 5114 5115 5116 5117 5118 5119 5120 5121 5122 5123 5124 5125 5126 5127 5128 5129 5130 5131 5132 5133 5134 5135 5136 5137 5138 5139 5140 5141 5142 5143 5144 5145 5146 5147 5148 5149 5150 5151 5152 5153 5154 5155 5156 5157 5158 5159 5160 5161 5162 5163 5164 5165 5166 5167 5168 5169 5170 5171 5172 5173 5174 5175 5176 5177 5178 5180 5181 5182 5183 5184 5185 5186 5187 5189 5190 5191 5192 5193 5194 5195 5196 5198 5199 5201 5206 5212 5213 5214 5215 5216 5217 5218 5219 5226 5231 5236 5241 5246 5251 5256 5260 5261 5262 5263 5264 5265 5266 5267 5268 5271 5276 5281 5286 5291 5301 5306 5312 5313 5314 5315 5316 5317 5318 5319 5326 5336 5341 5351 5356 5360 5361 5362 5363 5365 5366 5367 5369 5371 5376 5381 5391 5396 5401 5406 5412 5413 5414 5415 5416 5417 5418 5419 5426 5446 5451 5456 5460 5461 5462 5464 5465 5466 5468 5469 5471 5481 5486 5491 5496 5501 5506 5512 5513 5514 5515 5516 5517 5518 5519 5526 5536 5546 5560 5561 5562 5563 5564 5571 5576 5581 5586 5591 5596 5600 5601 5602 5603 5604 5605 5606 5607 5608 5609 5610 5611 5612 5613 5614 5615 5616 5617 5618 5619 5620 5621 5622 5623 5624 5625 5626 5627 5628 5630 5631 5632 5633 5635 5636 5637 5639 5640 5641 5642 5644 5645 5646 5648 5649 5650 5651 5652 5653 5654 5657 5658 5659 5660 5661 5662 5663 5664 5670 5671 5672 5673 5675 5676 5680 5681 5682 5684 5685 5686 5690 5691 5693 5694 5695 5696 5697 5698 5701 5706 5710 5711 5712 5713 5714 5715 5716 5717 5718 5721 5726 5731 5736 5741 5751 5756 5760 5761 5762 5763 5765 5766 5767 5769 5771 5776 5781 5796 5801 5806 5810 5811 5812 5813 5814 5815 5816 5817 5819 5821 5826 5831 5841 5846 5851 5856 5860 5861 5862 5864 5865 5866 5868 5869 5871 5886 5891 5896 5901 5906 5910 5911 5912 5913 5914 5915 5916 5918 5919 5921 5931 5936 5941 5946 5951 5956 5960 5961 5963 5964 5965 5966 5967 5968 5969 5976 5981 5986 5991 5996 6001 6002 6003 6004 6005 6006 6007 6008 6009 6010 6011 6012 6013 6014 6015 6016 6017 6018 6019 6020 6021 6022 6023 6024 6025 6026 6027 6028 6029 6030 6031 6032 6033 6034 6035 6036 6037 6038 6039 6040 6041 6042 6043 6044 6045 6046 6047 6048 6049 6050 6051 6052 6053 6054 6055 6056 6057 6058 6059 6060 6061 6062 6063 6064 6065 6066 6067 6068 6069 6070 6071 6072 6073 6074 6075 6076 6077 6078 6080 6081 6082 6083 6084 6085 6086 6087 6089 6090 6091 6092 6093 6094 6095 6096 6098 6099 6101 6102 6103 6104 6105 6106 6107 6108 6109 6112 6113 6114 6115 6116 6117 6118 6119 6120 6121 6122 6123 6124 6125 6126 6127 6128 6129 6130 6131 6132 6133 6134 6135 6136 6137 6138 6139 6140 6141 6142 6143 6144 6145 6146 6147 6148 6149 6150 6151 6152 6153 6154 6155 6156 6157 6158 6159 6160 6161 6162 6163 6164 6165 6166 6167 6168 6170 6171 6172 6173 6174 6175 6176 6177 6179 6180 6181 6182 6183 6184 6185 6186 6188 6189 6190 6191 6192 6193 6194 6195 6197 6198 6199 6201 6202 6203 6204 6205 6206 6207 6208 6209 6212 6213 6214 6215 6216 6217 6218 6219 6223 6224 6225 6226 6227 6228 6229 6230 6231 6232 6233 6234 6235 6236 6237 6238 6239 6240 6241 6242 6243 6244 6245 6246 6247 6248 6249 6250 6251 6252 6253 6254 6255 6256 6257 6258 6260 6261 6262 6263 6264 6265 6266 6267 6269 6270 6271 6272 6273 6274 6275 6276 6278 6279 6280 6281 6282 6283 6284 6285 6287 6288 6289 6290 6291 6292 6293 6294 6296 6297 6298 6299 6301 6302 6303 6304 6305 6306 6307 6308 6309 6312 6313 6314 6315 6316 6317 6318 6319 6323 6324 6325 6326 6327 6328 6329 6334 6335 6336 6337 6338 6339 6340 6341 6342 6343 6344 6346 6347 6348 6350 6351 6352 6353 6355 6356 6357 6359 6360 6361 6362 6363 6364 6365 6366 6368 6369 6370 6371 6372 6373 6374 6375 6377 6378 6379 6380 6381 6382 6383 6384 6386 6387 6388 6389 6390 6391 6392 6393 6395 6396 6397 6398 6399 6401 6402 6403 6404 6405 6406 6407 6408 6409 6412 6413 6414 6415 6416 6417 6418 6419 6423 6424 6425 6426 6427 6428 6429 6434 6436 6437 6438 6445 6446 6447 6449 6450 6451 6452 6454 6455 6456 6458 6459 6460 6461 6462 6463 6464 6465 6467 6468 6469 6470 6471 6472 6473 6474 6476 6477 6478 6479 6480 6481 6482 6483 6485 6486 6487 6488 6489 6490 6491 6492 6494 6495 6496 6497 6498 6499 6501 6502 6503 6504 6505 6506 6507 6508 6509 6512 6513 6514 6515 6516 6517 6518 6519 6523 6524 6525 6526 6527 6528 6535 6536 6537 6539 6545 6546 6548 6549 6557 6558 6559 6560 6561 6562 6563 6564 6567 6568 6569 6570 6571 6572 6573 6575 6576 6577 6579 6580 6581 6582 6584 6585 6586 6588 6589 6590 6591 6593 6594 6595 6596 6597 6598 6599 6601 6602 6603 6604 6605 6606 6607 6608 6609 6612 6613 6614 6615 6616 6617 6618 6623 6624 6625 6626 6627 6629 6634 6635 6636 6638 6639 6645 6647 6648 6649 6657 6658 6659 6670 6671 6672 6674 6675 6680 6681 6683 6684 6685 6690 6692 6693 6694 6695 6700 6701 6702 6703 6704 6705 6706 6707 6708 6710 6711 6712 6713 6714 6715 6716 6717 6719 6720 6721 6722 6723 6724 6725 6726 6728 6729 6730 6731 6732 6733 6734 6735 6737 6738 6739 6740 6741 6742 6743 6744 6746 6747 6748 6749 6750 6751 6752 6753 6755 6756 6757 6759 6760 6761 6762 6764 6765 6770 6771 6773 6774 6775 6780 6782 6783 6784 6791 6792 6793 6794 6795 6800 6801 6802 6803 6804 6805 6806 6807 6809 6810 6811 6812 6813 6814 6815 6816 6818 6819 6820 6821 6822 6823 6824 6825 6827 6828 6829 6830 6831 6832 6833 6834 6836 6837 6838 6839 6840 6841 6842 6843 6845 6846 6847 6848 6849 6850 6851 6852 6854 6855 6856 6858 6859 6860 6861 6863 6864 6865 6870 6872 6873 6874 6881 6882 6883 6884 6885 6890 6891 6892 6893 6894 6895 6900 6901 6902 6903 6904 6905 6906 6908 6909 6910 6911 6912 6913 6914 6915 6917 6918 6919 6920 6921 6922 6923 6924 6926 6927 6928 6929 6930 6931 6932 6933 6935 6936 6937 6938 6939 6940 6941 6942 6944 6945 6946 6947 6948 6949 6950 6951 6953 6954 6955 6956 6957 6958 6959 6960 6962 6963 6964 6965 6971 6972 6973 6974 6975 6980 6981 6982 6983 6984 6985 6990 6991 6992 6993 6994 6995 7001 7006 7010 7011 7012 7013 7014 7015 7016 7017 7018 7019 7021 7026 7031 7036 7041 7046 7051 7056 7060 7061 7062 7063 7064 7065 7066 7067 7068 7071 7076 7081 7086 7091 7101 7102 7103 7104 7105 7106 7107 7108 7109 7112 7113 7114 7115 7116 7117 7118 7119 7120 7121 7122 7123 7124 7125 7126 7127 7128 7129 7130 7131 7132 7133 7134 7135 7136 7137 7138 7139 7140 7141 7142 7143 7144 7145 7146 7147 7148 7149 7150 7151 7152 7153 7154 7155 7156 7157 7158 7160 7161 7162 7163 7164 7165 7166 7167 7169 7170 7171 7172 7173 7174 7175 7176 7178 7179 7180 7181 7182 7183 7184 7185 7187 7188 7189 7190 7191 7192 7193 7194 7196 7197 7198 7199 7201 7206 7212 7213 7214 7215 7216 7217 7218 7219 7226 7231 7236 7241 7246 7251 7256 7260 7261 7262 7263 7264 7265 7266 7268 7269 7271 7281 7286 7291 7296 7301 7306 7312 7313 7314 7315 7316 7317 7318 7319 7326 7336 7341 7346 7351 7356 7360 7361 7362 7363 7364 7365 7367 7368 7369 7371 7376 7381 7386 7391 7396 7401 7406 7412 7413 7414 7415 7416 7417 7418 7419 7426 7436 7446 7451 7460 7461 7462 7463 7464 7466 7467 7468 7469 7471 7476 7481 7486 7491 7496 7501 7506 7512 7513 7514 7515 7516 7517 7518 7526 7536 7556 7560 7561 7562 7563 7565 7566 7567 7569 7571 7576 7581 7596 7601 7602 7603 7604 7605 7606 7607 7608 7612 7613 7614 7615 7616 7617 7619 7623 7624 7625 7626 7628 7629 7634 7635 7637 7638 7639 7646 7647 7648 7649 7656 7657 7659 7670 7671 7673 7674 7675 7680 7682 7683 7684 7691 7692 7693 7694 7695 7701 7706 7712 7713 7714 7715 7716 7718 7719 7736 7746 7756 7781 7791 7801 7806 7810 7811 7812 7813 7814 7815 7817 7818 7819 7821 7826 7831 7836 7841 7846 7851 7860 7862 7863 7864 7871 7881 7891 7901 7910 7911 7912 7913 7914 7916 7917 7918 7919 7921 7926 7931 7936 7941 7946 7956 7961 7962 7963 7964 7965 7971 7981 7991 8001 8006 8010 8011 8012 8013 8014 8015 8016 8017 8018 8021 8026 8031 8036 8041 8046 8051 8056 8060 8061 8062 8063 8064 8065 8066 8067 8069 8071 8076 8081 8096 8101 8102 8103 8104 8105 8106 8107 8108 8112 8113 8114 8115 8116 8117 8118 8119 8120 8121 8122 8123 8124 8125 8126 8127 8128 8129 8130 8131 8132 8133 8134 8135 8136 8137 8138 8139 8140 8141 8142 8143 8144 8145 8146 8147 8148 8150 8151 8152 8153 8154 8155 8156 8157 8159 8160 8161 8162 8163 8164 8165 8166 8168 8169 8170 8171 8172 8173 8174 8175 8177 8178 8179 8180 8181 8182 8183 8184 8186 8187 8188 8189 8191 8192 8193 8195 8196 8197 8198 8199 8201 8206 8212 8213 8214 8215 8216 8217 8218 8219 8226 8231 8236 8241 8246 8251 8256 8260 8261 8262 8263 8264 8265 8267 8268 8269 8271 8276 8281 8286 8291 8296 8301 8306 8312 8313 8314 8315 8316 8317 8318 8319 8326 8336 8341 8346 8351 8360 8361 8362 8363 8364 8366 8367 8368 8369 8371 8376 8381 8386 8391 8396 8401 8406 8412 8413 8414 8415 8416 8417 8418 8426 8436 8451 8456 8460 8461 8462 8463 8465 8466 8467 8468 8469 8471 8476 8481 8486 8496 8501 8506 8512 8513 8514 8515 8516 8517 8519 8526 8546 8556 8560 8561 8562 8564 8565 8566 8568 8569 8571 8586 8591 8596 8601 8602 8603 8604 8605 8606 8607 8609 8612 8613 8614 8615 8616 8618 8619 8623 8624 8625 8627 8628 8629 8634 8636 8637 8638 8639 8645 8646 8647 8648 8649 8656 8658 8659 8670 8672 8673 8674 8681 8682 8683 8684 8685 8690 8691 8692 8693 8694 8695 8701 8706 8712 8713 8714 8715 8717 8718 8719 8726 8736 8746 8781 8791 8801 8812 8813 8814 8816 8817 8818 8819 8826 8836 8846 8856 8891 8906 8911 8912 8913 8915 8916 8917 8918 8919 8921 8926 8931 8936 8946 8951 8956 8960 8961 8962 8963 8964 8965 8971 8981 8991 9001 9006 9010 9011 9013 9014 9015 9016 9017 9019 9026 9031 9036 9041 9046 9051 9056 9060 9061 9062 9063 9064 9065 9066 9068 9069 9071 9086 9091 9096 9101 9103 9104 9105 9106 9107 9109 9112 9113 9114 9115 9116 9117 9118 9119 9121 9122 9123 9124 9125 9126 9127 9128 9129 9130 9131 9132 9133 9134 9135 9136 9137 9138 9140 9141 9142 9143 9144 9145 9146 9147 9149 9150 9151 9152 9153 9154 9155 9156 9158 9159 9160 9161 9162 9163 9164 9165 9167 9168 9169 9170 9171 9172 9173 9174 9176 9177 9178 9179 9181 9182 9183 9185 9186 9187 9188 9189 9190 9191 9192 9194 9195 9196 9197 9198 9199 9206 9212 9213 9214 9215 9216 9217 9218 9219 9226 9231 9236 9241 9246 9251 9260 9261 9262 9263 9264 9266 9267 9268 9269 9271 9276 9281 9286 9291 9296 9301 9306 9312 9313 9314 9315 9316 9317 9318 9326 9336 9341 9351 9356 9360 9361 9362 9363 9365 9366 9367 9368 9369 9371 9376 9381 9386 9396 9401 9406 9412 9413 9414 9415 9416 9417 9419 9426 9446 9451 9456 9460 9461 9462 9464 9465 9466 9467 9468 9469 9471 9476 9486 9491 9496 9501 9506 9512 9513 9514 9515 9516 9518 9519 9536 9546 9556 9560 9561 9563 9564 9565 9566 9567 9568 9569 9576 9581 9586 9591 9596 9601 9602 9603 9604 9605 9606 9608 9609 9612 9613 9614 9615 9617 9618 9619 9623 9624 9626 9627 9628 9629 9635 9636 9637 9638 9639 9645 9646 9647 9648 9649 9656 9657 9658 9659 9671 9672 9673 9674 9675 9680 9681 9682 9683 9684 9685 9690 9691 9692 9693 9694 9695 9701 9712 9713 9714 9716 9717 9718 9719 9726 9736 9746 9756 9781 9791 9806 9812 9813 9815 9816 9817 9818 9819 9826 9836 9846 9856 9891 9901 9906 9912 9914 9915 9916 9917 9918 9919 9926 9936 9946 9956";
		String openCodes = "5,3,2,4,9";

		long start = System.currentTimeMillis();
		SSCRXZHDSPlayHandler handler = new SSCRXZHDSPlayHandler("az5fc_rx4ds", 4);

		// 测试性能
		for (int i = 0; i < 100000; i++) {
			WinResult winResult = handler.calculateWinNum(1, betCodes, openCodes);
		}

		// 测试准确性
		WinResult winResult = handler.calculateWinNum(1, betCodes, openCodes);
		System.out.println(winResult.getWinNum());

		long spent = System.currentTimeMillis() - start;
		System.out.println("耗时：" + spent);

		System.exit(0);
	}
}