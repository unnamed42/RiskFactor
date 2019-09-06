import React, { ReactElement, forwardRef } from "react";

import { Select } from "antd";
import { SelectProps } from "antd/lib/select";

const ethnics = [
  { name: "汉族", pinyin: "hanzu" },
  { name: "满族", pinyin: "manzu" },
  { name: "蒙古族", pinyin: "mengguzu" },
  { name: "回族", pinyin: "huizu" },
  { name: "藏族", pinyin: "zangzu" },
  { name: "维吾尔族", pinyin: "weiwuerzu" },
  { name: "苗族", pinyin: "miaozu" },
  { name: "彝族", pinyin: "yizu" },
  { name: "壮族", pinyin: "zhuangzu" },
  { name: "布依族", pinyin: "buyizu" },
  { name: "侗族", pinyin: "dongzu" },
  { name: "瑶族", pinyin: "yaozu" },
  { name: "白族", pinyin: "baizu" },
  { name: "土家族", pinyin: "tujiazu" },
  { name: "哈尼族", pinyin: "hanizu" },
  { name: "哈萨克族", pinyin: "hasakezu" },
  { name: "傣族", pinyin: "daizu" },
  { name: "黎族", pinyin: "lizu" },
  { name: "傈僳族", pinyin: "lisuzu" },
  { name: "佤族", pinyin: "wazu" },
  { name: "畲族", pinyin: "shezu" },
  { name: "高山族", pinyin: "gaoshanzu" },
  { name: "拉祜族", pinyin: "lahuzu" },
  { name: "水族", pinyin: "shuizu" },
  { name: "东乡族", pinyin: "dongxiangzu" },
  { name: "纳西族", pinyin: "naxizu" },
  { name: "景颇族", pinyin: "jingpozu" },
  { name: "柯尔克孜族", pinyin: "keerkezizu" },
  { name: "土族", pinyin: "tuzu" },
  { name: "达斡尔族", pinyin: "dawoerzu" },
  { name: "仫佬族", pinyin: "mulaozu" },
  { name: "羌族", pinyin: "qiangzu" },
  { name: "布朗族", pinyin: "bulangzu" },
  { name: "撒拉族", pinyin: "salazu" },
  { name: "毛南族", pinyin: "maonanzu" },
  { name: "仡佬族", pinyin: "gelaozu" },
  { name: "锡伯族", pinyin: "xibozu" },
  { name: "阿昌族", pinyin: "achangzu" },
  { name: "普米族", pinyin: "pumizu" },
  { name: "朝鲜族", pinyin: "chaoxianzu" },
  { name: "塔吉克族", pinyin: "tajikezu" },
  { name: "怒族", pinyin: "nuzu" },
  { name: "乌孜别克族", pinyin: "wuzibiekezu" },
  { name: "俄罗斯族", pinyin: "eluosizu" },
  { name: "鄂温克族", pinyin: "ewenkezu" },
  { name: "德昂族", pinyin: "deangzu" },
  { name: "保安族", pinyin: "baoanzu" },
  { name: "裕固族", pinyin: "yuguzu" },
  { name: "京族", pinyin: "jingzu" },
  { name: "塔塔尔族", pinyin: "tataerzu" },
  { name: "独龙族", pinyin: "dulongzu" },
  { name: "鄂伦春族", pinyin: "elunchunzu" },
  { name: "赫哲族", pinyin: "hezhezu" },
  { name: "门巴族", pinyin: "menbazu" },
  { name: "珞巴族", pinyin: "luobazu" },
  { name: "基诺族", pinyin: "jinuozu" }
];

export default forwardRef<any, SelectProps>((props, ref) => {

  const pinyinFilter = (input: string, option: ReactElement<any>) => {
    const key = option.props["data-pinyin"];
    return key !== undefined && key.toString().startsWith(input);
  };

  return (
    <Select ref={ref} showSearch placeholder="请选择民族，可输入拼音筛选"
            filterOption={pinyinFilter} {...props}>
      {
        ethnics.map(({ name, pinyin }) => (
          <Select.Option data-pinyin={pinyin} key={pinyin} value={name}>{name}</Select.Option>
        ))
      }
    </Select>
  );
});
