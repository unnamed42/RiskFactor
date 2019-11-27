import { GetFieldDecoratorOptions, WrappedFormUtils } from "antd/lib/form/Form";

import { Question as QSchema } from "@/types/task";
import { numberRegex, text } from "@/config";

// 生成 getFieldDecorator 的options参数
export const validationRules = ({ required, type }: QSchema): GetFieldDecoratorOptions => {
  const rules: GetFieldDecoratorOptions["rules"] = [];

  if (required)
    rules.push({ required: true, message: text.required });
  if (type === "number")
    rules.push({ pattern: numberRegex, message: text.numberRequired });

  return { rules };
};

// 将form.validateXXX的回调风格转换成Promise风格
// 没什么卵用，先放在这里
export const validateAndScrollAsync = <T = any>(form: WrappedFormUtils<T>) => new Promise((resolve, reject) => {
  form.validateFieldsAndScroll((errors, values) => {
    if (errors) reject(errors);
    else resolve(values);
  });
});

export const enablerFId = (parentId: string | number) => `#enabler.$${parentId}`;

// 用来给依赖其他项的值进行计算的动态表单项目求值
export const evalExpr = (expr: string, { getFieldValue }: WrappedFormUtils<any>) => {
  const parseOperand = (operand: string) =>
    Number(operand.startsWith("$") ? getFieldValue(operand) : operand);
  const isDigit = (ch: number) =>
    "0".charCodeAt(0) <= ch && ch <= "9".charCodeAt(0);
  const op = (ch: string): ((lhs: number, rhs: number) => number) | undefined => {
    switch (ch) {
      case "+": return (a, b) => a + b;
      case "-": return (a, b) => a - b;
      case "*": return (a, b) => a * b;
      case "/": return (a, b) => a / b;
      default:  return undefined;
    }
  };

  const stack: number[] = [];
  for (const s of expr.split(" ")) {
    if(s.length === 1 && !isDigit(s.charCodeAt(0))) {
      const rhs = stack.pop(); const lhs = stack.pop();
      const operator = op(s);
      if(operator === undefined || lhs === undefined || rhs === undefined)
        return undefined;
      stack.push(operator(lhs, rhs));
    } else
      stack.push(parseOperand(s));
  }
  return stack.length === 1 && !isNaN(stack[0]) ? stack[0].toString(): undefined;
};
