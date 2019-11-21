export type QType =
  "header" |
  "text" | "number" | "date" | "disabled" | "either" |
  "list" | "template" | "table" | "table-header" |
  "choice" | "choice-multi" | "select" | "select-multi";

export interface Question {
  id: number | string;
  type?: QType;
  label?: string;
  list?: Question[];

  customizable?: boolean;
  choices?: string[];
  required?: boolean;
  yesno?: string;
  addonPosition?: "prefix" | "postfix";
  labelPosition?: "prefix" | "postfix";
  selected?: string;
  description?: string;
  placeholder?: string;
}

export interface TaskStruct {
  name: string;
  list?: TaskStruct[];
}

export interface TaskView {
  id: number;
  center: string;
  name: string;
  mtime: string;
}

export interface Task extends TaskView {
  list: Question[];
}

export interface AnswerBrief {
  id: number;
  creator: string;
  mtime: string;
}
