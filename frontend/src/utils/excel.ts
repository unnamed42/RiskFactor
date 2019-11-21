import { CellRichTextValue, ValueType, Workbook } from "exceljs";
import { assign } from "lodash";

import { cachedLayout } from "@/utils";
import { Dict, Question } from "@/types";

const header = (h1: string, h2: string | undefined) =>
  h2 === undefined ? h1 : `${h1}/${h2}`;

const trim = (str: string | undefined) => {
  if(str === undefined) return "";
  const idx = str.indexOf("（");
  return str.substring(0, idx === -1 ? undefined: idx).trim();
};

const toLookup = (layout: Dict<Question[]>): Dict<Dict<Question>> =>
  Object.entries(layout).reduce((obj, [header, questions]) => {
    if(questions === undefined) return obj;
    const dict = questions.reduce((o: Dict<Question>, q) =>
      assign(o, { [trim(q.label)]: q })
    , {});
    return assign(obj, { [header]: dict });
  }, {});

const valueOf = (value: CellRichTextValue) =>
  value.richText.map(({ text }) => text).join("");

// NOTE: exceljs index从1开始
export const parsedExcel = async (taskId: number | string, buffer: ArrayBuffer): Promise<void> => {
  const [excel, layout] = await Promise.all([
    new Workbook().xlsx.load(buffer),
    cachedLayout(taskId)
  ]);

  const lookup = toLookup(layout);
  const sheet = excel.getWorksheet(1);
  const { rowCount, columnCount } = sheet;

  const result = [];

  for (let r = 4; r <= rowCount; ++r) {
    for (let c = 1; c <= columnCount; ++c) {
      const [h1, h2, label] = [1, 2, 3].map(row =>
        sheet.getCell(row, c).value?.toString());
      if(h1 === undefined)
        throw new Error(`Excel格式错误：(1, ${c}) 应为一级标题，但为空`);
      if(label === undefined)
        throw new Error(`Excel格式错误：(${r}, ${c}) 应为问题标签，但为空`);

      const { value, type } = sheet.getCell(r, c);
      if(value === null)
        continue;
      const questions = lookup[header(h1, h2)];
      if(!questions)
        throw new Error(`问卷样式错误：不存在内容 ${header(h1, h2)}`);
      const entry = questions[trim(label)];
      if(entry) {
        console.log(value);
        let strValue = value.toString();
        if(type === ValueType.RichText)
          strValue = valueOf(value as CellRichTextValue);
        console.log({
          question_id: entry.id, task_id: taskId, value: strValue
        });
      } else
        console.log(`找不到标签：${label}`, c);
    }
    break;
  }
  console.log('done');
};
