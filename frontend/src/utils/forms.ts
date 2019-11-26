import { GetFieldDecoratorOptions, WrappedFormUtils } from "antd/lib/form/Form";

import { Question as QSchema } from "@/types/task";
import { numberRegex, text } from "@/config";

// 生成 getFieldDecorator 的options参数
export const validationRules = ({ required, type }: QSchema): GetFieldDecoratorOptions => {
  const rules: GetFieldDecoratorOptions["rules"] = [];

  if(required)
    rules.push({ required: true, message: text.required });
  if(type === "number")
    rules.push({ pattern: numberRegex, message: text.numberRequired });

  return { rules };
};

// 将form.validateXXX的回调风格转换成Promise风格
// 没什么卵用，先放在这里
export const validateAndScrollAsync = <T = any>(form: WrappedFormUtils<T>) => new Promise((resolve, reject) => {
  form.validateFieldsAndScroll((errors, values) => {
    if(errors) reject(errors);
    else resolve(values);
  });
});

export const enablerFId = (parentId: string | number) => `#enabler.$${parentId}`;

// 用来给依赖其他项的值进行计算的动态表单项目求值
export const evalExpr = (expr: string, { getFieldValue }: WrappedFormUtils<any>) => {
  const parseOperand = (operand: string) =>
    Number(operand.startsWith("$") ? getFieldValue(operand) : operand);

  const elements = expr.split(" ");
  if(elements.length === 0)
    return undefined;
  let value = parseOperand(elements[0]);
  if(isNaN(value))
    return undefined;
  for(let i=1; i<elements.length; i+=2) {
    const rhs = parseOperand(elements[i]), op = elements[i+1];
    if(isNaN(rhs))
      return undefined;
    switch(op) {
      case "+": value += rhs; break;
      case "-": value -= rhs; break;
      case "*": value *= rhs; break;
      case "/": value /= rhs; break;
      default: return undefined;
    }
  }
  return value;
};
