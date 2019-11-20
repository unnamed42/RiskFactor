export const baseUrl = "/api";
// moment.js date pattern
export const datePattern = "YYYY-MM-DD";
// unicode不可见分隔符
export const sep = "\u2063";
// 匹配所有号码（手机卡 + 数据卡 + 上网卡） https://github.com/VincentSit/ChinaMobilePhoneNumberRegex
export const phoneRegex = /^(?:\+?86)?1(?:3\d{3}|5[^4\D]\d{2}|8\d{3}|7(?:[01356789]\d{2}|4(?:0\d|1[0-2]|9\d))|9[189]\d{2}|6[567]\d{2}|4(?:[14]0\d{3}|[68]\d{4}|[579]\d{2}))\d{6}$/;
