export type QType =
  "text" | "number" | "date" | "disabled" | "either" |
  "list" | "template" | "table" |
  "choice" | "choice-multi" | "select" | "select-multi";

export interface Question {
  id: number | string;
  type?: QType;
  label?: string;
  list?: Question[];

  required?: boolean;
  yesno?: string;
  addonPosition?: "prefix" | "postfix";
  labelPosition?: "prefix" | "postfix";
  selected?: string;
  description?: string;
  placeholder?: string;
  filterKey?: string;
}

export interface SectionBrief {
  id: number;
  title: string;
}

export interface Section extends SectionBrief {
  questions?: Question[];
}

export interface TaskBrief {
  id: number;
  center: string;
  name: string;
  mtime: string;
}

export interface Task extends TaskBrief {
  sections?: Section[];
  questions?: Question[];
}

export interface AnswerBrief {
  id: number;
  creator: string;
  mtime: string;
}
