import { CellRichTextValue, ValueType, Workbook, Worksheet } from "exceljs";

import { isNil, set, range } from "lodash";

import { cachedLayout, tuple } from "@/utils";
import { Dict, Question } from "@/types";

const header = (h1: string, h2: string | undefined) =>
  h2 === undefined ? h1 : `${h1}/${h2}`;

const slashJoin = (...args: Array<string | undefined | null>) =>
  args.filter(arg => !isNil(arg)).join("/");

interface QInfo extends Question {
  parent?: QInfo;
}

const titleMapping = (layout: Dict<Question[]>) => {
  const result: Dict<QInfo> = {};
  const collect = (title: string, root: Question, parent?: QInfo) => {
    if(root.type === undefined || root.label === undefined)
      return;
    const curr = parent ? { ...root, parent } : root;
    const currTitle = slashJoin(title, root.label);
    result[currTitle] = curr;
    if(root.type === "table") {
      const [, ...rows] = root.list!;
      rows.map(r => r.list!).forEach(row => {
        collect(slashJoin(currTitle, row[0].label), row[1], curr);
      });
    } else
      root.list?.forEach(q => collect(slashJoin(title, root.label), q, curr));
  };
  Object.entries(layout).forEach(([title, list]) => list?.forEach(q => collect(title, q)));
  return result;
};

const propsOf = (info: QInfo, value: string) => {
  const path = [];
  for(let p: QInfo | undefined = info; p !== undefined; ) {
    path.push(p.id);
    p = p.parent;
  }
  if(info?.parent?.enabler)
    path.push("#enabler");
  return [tuple(path.reverse().map(id => `$${id}`).join("."), value)];
};

const valueOf = (value: CellRichTextValue) =>
  value.richText.map(({ text }) => text).join("");

const parseCells = (sheet: Worksheet, layout: Dict<Question[]>) => {
  const mapping = titleMapping(layout);
  const { rowCount, columnCount } = sheet;

  console.log(mapping);

  const parseColumn = (r: number) => {
    const entry: any = {};
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
      let strValue = value.toString();
      if (type === ValueType.RichText)
        strValue = valueOf(value as CellRichTextValue);
      propsOf(qu, strValue).forEach(([k, v]) => set(entry, k, v));
    }
    return entry;
  };

  return range(4, rowCount + 1).reduce((acc: any[], r) => {
    acc.push(parseColumn(r)); return acc;
  }, []);
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
