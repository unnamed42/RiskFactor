export const baseUrl = process.env.NODE_ENV === "development" ? "http://localhost:8090/" : "http://120.27.221.101/api/";
// moment.js date pattern
export const datePattern = "YYYY-MM-DD";
// unicode不可见分隔符
export const sep = "\u2063";
// token的剩余时间占总时间的百分比，如果低于该阈值则刷新
export const refreshThres = 0.15;
// 匹配所有号码（手机卡 + 数据卡 + 上网卡） https://github.com/VincentSit/ChinaMobilePhoneNumberRegex
export const phoneRegex = /^(?:\+?86)?1(?:3\d{3}|5[^4\D]\d{2}|8\d{3}|7(?:[01356789]\d{2}|4(?:0\d|1[0-2]|9\d))|9[189]\d{2}|6[567]\d{2}|4(?:[14]0\d{3}|[68]\d{4}|[579]\d{2}))\d{6}$/;
// 空，或者数字
export const numberRegex = /^(-?(0|[1-9]\d*)(\.\d*)?)?$/;
// 简单翻译字符串，考虑用i18n库替换
export const text = {
  other: "其他",
  required: "此项必填",
  numberRequired: "请输入数字",
  reLogin: "登录信息失效，请重新登录"
};
