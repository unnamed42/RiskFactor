import { CellRichTextValue, ValueType, Workbook, Worksheet } from "exceljs";

import { assign, isNil } from "lodash";

import { cachedLayout } from "@/utils";
import { Dict, Question } from "@/types";

const header = (h1: string, h2: string | undefined) =>
  h2 === undefined ? h1 : `${h1}/${h2}`;

const slashJoin = (...args: Array<string | undefined | null>) =>
  args.filter(arg => !isNil(arg)).join("/");

const titleMapping = (layout: Dict<Question[]>) => {
  const result: Dict<Question> = {};
  const addList = (title: string, list: Question[]) => {
    list.forEach(q => {
      result[slashJoin(title, q.label)] = q;
    });
  };
  Object.entries(layout).forEach(([title, list]) => {
    list?.forEach(q => {
      switch (q.type) {
        case "template":
        case "table":
          addList(title, q.list!);
          break;
        case undefined:
          return;
        default:
          if (!q.label) {
            console.error(`level 1 question ${q.id} has no label`);
            return;
          }
          result[slashJoin(title, q.label)] = q;
      }
      if (q.enabler)
        addList(title, q.list!);
    });
  });
  return result;
};

const valueOf = (value: CellRichTextValue) =>
  value.richText.map(({ text }) => text).join("");

const parseCells = (sheet: Worksheet, layout: Dict<Question[]>) => {
  const mapping = titleMapping(layout);
  const { rowCount, columnCount } = sheet;
  const result = [];

  for (let r = 4; r <= rowCount; ++r) {
    const entry = {};
    for (let c = 1; c <= columnCount; ++c) {
      const [h1, h2, label] = [1, 2, 3].map(row =>
        sheet.getCell(row, c).value?.toString());
      if (h1 === undefined)
        throw new Error(`Excel格式错误：(1, ${c}) 应为一级标题，但为空`);
      if (label === undefined)
        throw new Error(`Excel格式错误：(${r}, ${c}) 应为问题标签，但为空`);

      const { value, type } = sheet.getCell(r, c);
      if (value === null)
        continue;
      const qu = mapping[slashJoin(h1, h2, label)];
      if (!qu) {
        console.error(`问卷样式错误：不存在内容 ${header(h1, h2)}`);
        continue;
      }
      console.log(value);
      let strValue = value.toString();
      if (type === ValueType.RichText)
        strValue = valueOf(value as CellRichTextValue);
      assign(entry, {
        [`$${qu.id}`]: strValue
      });
    }
    result.push(entry);
  }

  return result;
};

// NOTE: exceljs index从1开始
export const parsedExcel = async (taskId: number | string, buffer: ArrayBuffer) =>
  Promise.all([new Workbook().xlsx.load(buffer), cachedLayout(taskId)]).then(([excel, layout]) => {
    const sheet = excel.getWorksheet(1);
    return parseCells(sheet, layout);
  }).then(result => {
    console.log("done");
    return result;
  });
