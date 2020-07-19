import { request, IdType, ApiIdType } from "./base";
import type { UserInfo } from "./account";

export interface SchemaInfo {
  id: IdType;
  name: string;
  creator: UserInfo;
  modifiedAt: number;
  createdAt: number;
}

export interface SchemaDetail extends SchemaInfo {
  rules?: RuleInfo[];
}

export type RuleInfo =
  RuleList | RuleTemplate |
  RuleInput | RuleDate | RuleExpression |
  RuleChoices | RuleSelect | RuleEither | RuleTable |
  RuleLabel | RuleHeader;

export const getSchemas = (): Promise<SchemaInfo[]> =>
  request({ url: "/schema" });

export const schemaModifiedTime = (schemaId: ApiIdType): Promise<number> =>
  request({ url: `/schema/${schemaId}/modifiedAt` });

export const getSchemaInfo = (schemaId: ApiIdType): Promise<SchemaInfo> => 
  request({ url: `/schema/${schemaId}` });

export const getSchemaDetail = (schemaId: ApiIdType): Promise<SchemaDetail> => 
  request({ url: `/schema/${schemaId}/detail` });


/*
 * 渲染规则 -- 类型
 */
export interface RuleBase {
  id: IdType;
  label?: string;
}

export interface RuleHeader extends RuleBase {
  type: "header";
  list: RuleInfo[];
  label: string;
}

export interface RuleLabel extends RuleBase {
  type: undefined;
  label: string;
}

export interface RuleList extends RuleBase {
  type: "list";
  list: Array<RuleInfo>;
}

export interface RuleTemplate extends RuleBase {
  type: "template";
  list: Array<RuleInfo>;
}

type Position = "prefix" | "postfix" | null;

export type RuleInput = RuleBase & {
  type: "text" | "number";
  placeholder?: string;
} & ({
  list?: undefined;
} | {
  list: [RuleInfo & { addonPosition: Position }];
});

export interface RuleDate extends RuleBase {
  type: "date";
  placeholder?: string;
}

export interface RuleSelect extends RuleBase {
  type: "select" | "select-multi";
  choices: string[];
  placeholder?: string;
}

export interface RuleChoices extends RuleBase {
  type: "choice" | "choice-multi";
  choices: string[];
  customizable?: boolean;
}

interface RuleTableHeader extends RuleBase {
  type: "table-header";
  choices: string[];
}

export interface RuleTable extends RuleBase {
  type: "table";
  list: [RuleTableHeader, ...Array<RuleList>];
}

export interface RuleExpression extends RuleBase {
  type: "expression";
  placeholder: string;
}

export interface RuleEither extends RuleBase {
  type: "either";
  yesno?: string;
  list?: Array<RuleInfo>;
}
