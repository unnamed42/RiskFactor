import { ValueType, Workbook } from "exceljs";
import { cachedLayout } from "@/utils";

const header = (h1: string, h2: string | undefined) =>
  h2 === undefined ? h1 : `${h1}/${h2}`;

// NOTE: exceljs index从1开始
export const parsedExcel = async (taskId: number | string, buffer: ArrayBuffer) => {
  const [excel, layout] = await Promise.all([
    new Workbook().xlsx.load(buffer),
    cachedLayout(taskId)
  ]);

  const sheet = excel.getWorksheet(1);
  const { rowCount, columnCount } = sheet;
  for (let r = 4; r <= rowCount; ++r) {
    for (let c = 1; c <= columnCount; ++c) {
      const [h1, h2, label] = [1, 2, 3].map(row =>
        sheet.getCell(row, c).value?.toString());
      if(h1 === undefined)
        throw new Error(`Excel格式错误：(1, ${c}) 应为一级标题，但为空`);
      if(label === undefined)
        throw new Error(`Excel格式错误：(${r}, ${c}) 应为问题标签，但为空`);

      const { value } = sheet.getCell(r, c);
      if(value === null)
        continue;
      const questions = layout[header(h1, h2)];
      if(questions === undefined)
        throw new Error(`问卷样式错误：不存在内容 ${header(h1, h2)}`);
    }
    break;
  }

};
