import { GetFieldDecoratorOptions, WrappedFormUtils } from "antd/lib/form/Form";

import { Question as QSchema } from "@/types/task";
import { numberRegex, text } from "@/config";
import { useContext } from "react";
import { FormContext } from "@/views/Task/AnswerForm/Question";

export const useForm = () => useContext(FormContext)!;

// 生成 getFieldDecorator 的options参数
export const validationRules = ({ required, type }: QSchema): GetFieldDecoratorOptions => {
  const rules: GetFieldDecoratorOptions["rules"] = [];

  if (required)
    rules.push({ required: true, message: text.required });
  if (type === "number")
    rules.push({ pattern: numberRegex, message: text.numberRequired });

  return { rules };
};

export const enablerFId = (parentId: string | number) => `#enabler.$${parentId}`;

/**
 * 将表达式求值
 * @param expr 后缀表达式
 * @param form ant design form实例
 * @return {number | undefined} 表达式求值无错误发生，且结果不为`NaN`时返回该数值，其他情况返回`undefined`
 */
export const evalExpr = (expr: string, form: WrappedFormUtils) => {
  const parseOperand = (operand: string) =>
    Number(operand.startsWith("$") ? form.getFieldValue(operand) : operand);
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
  return stack.length === 1 && !isNaN(stack[0]) ? stack[0].toPrecision(3): undefined;
};
