import React, { FC } from "react";

import { Input, Form } from "antd";
import { FormInstance } from "rc-field-form/es";

import { QProps as P } from ".";

/**
 * 将表达式求值
 * @param expr 后缀表达式
 * @param form ant design form实例
 * @return 表达式求值无错误发生，且结果不为`NaN`时返回该数值，其他情况返回`undefined`
 */
const evalExpr = (expr: string, form: FormInstance) => {
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

/**
 * TODO: 由公式计算出来的值是否需要放入到最终上传给服务器的结果中？
 */
export const QImmutable: FC<P> = ({ rule: { placeholder } }) => <Form.Item>
  {
    form => {
      const content = (() => {
        if (!placeholder) return undefined;
        const [type, expr] = placeholder.split(":");
        if (type === "var")
          return form.getFieldValue(["#vars", expr]);
        if (type === "expr")
          return evalExpr(expr, form);
      })();
      return <Input placeholder={content} disabled />;
    }
  }
</Form.Item>;
